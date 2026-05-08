package com.tunisales.inventory.service;

import com.tunisales.inventory.client.GpfReworkClient;
import com.tunisales.inventory.domain.ReworkRequest;
import com.tunisales.inventory.domain.StockItem;
import com.tunisales.inventory.domain.enumeration.ReworkStatus;
import com.tunisales.inventory.repository.ReworkRequestRepository;
import com.tunisales.inventory.repository.StockItemRepository;
import com.tunisales.inventory.service.dto.ReworkRequestDTO;
import com.tunisales.inventory.service.mapper.ReworkRequestMapper;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ReworkRequest}.
 */
@Service
@Transactional
public class ReworkRequestService {

    private final Logger log = LoggerFactory.getLogger(ReworkRequestService.class);

    private final ReworkRequestRepository reworkRequestRepository;
    private final ReworkRequestMapper reworkRequestMapper;
    private final StockItemRepository stockItemRepository;
    private final GpfReworkClient gpfReworkClient;
    private final StockItemService stockItemService;

    public ReworkRequestService(
        ReworkRequestRepository reworkRequestRepository,
        ReworkRequestMapper reworkRequestMapper,
        StockItemRepository stockItemRepository,
        GpfReworkClient gpfReworkClient,
        StockItemService stockItemService
    ) {
        this.reworkRequestRepository = reworkRequestRepository;
        this.reworkRequestMapper = reworkRequestMapper;
        this.stockItemRepository = stockItemRepository;
        this.gpfReworkClient = gpfReworkClient;
        this.stockItemService = stockItemService;
    }

    @Transactional(readOnly = true)
    public Optional<ReworkRequestDTO> findOne(Long id) {
        return reworkRequestRepository.findById(id).map(reworkRequestMapper::toDto);
    }

    /**
     * Create a {@link ReworkRequest} for a defective stock item and dispatch
     * it to GPF.
     *
     * <p>The local record is persisted first (so we always have a trace, even
     * if the outbound call fails) and updated with the GPF external id once
     * the call returns. Network failures are turned into {@link ReworkStatus#FAILED}
     * but never propagate as exceptions.</p>
     */
    public ReworkRequestDTO sendToRework(Long stockItemId, String reason) {
        log.debug("Request to send StockItem {} to rework (reason: {})", stockItemId, reason);
        StockItem item = stockItemRepository
            .findById(stockItemId)
            .orElseThrow(() -> new NoSuchElementException("StockItem " + stockItemId + " not found"));

        ReworkRequest request = new ReworkRequest()
            .tenantId(item.getTenantId())
            .status(ReworkStatus.PENDING)
            .sentAt(ZonedDateTime.now())
            .stockItem(item)
            .payloadJson(buildPayloadJson(item.getImei(), reason));
        request = reworkRequestRepository.save(request);

        Optional<String> externalId = gpfReworkClient.sendRework(item.getImei(), reason);
        if (externalId.isPresent()) {
            request.setExternalId(externalId.get());
            request.setStatus(ReworkStatus.ACK);
        } else {
            request.setStatus(ReworkStatus.FAILED);
        }
        request = reworkRequestRepository.save(request);

        return reworkRequestMapper.toDto(request);
    }

    /**
     * Apply a GPF callback to update the local rework record.
     *
     * <p>If {@code newStatus} is {@link ReworkStatus#COMPLETED}, the related
     * {@link StockItem} is automatically marked as repaired.</p>
     *
     * @return the updated DTO, or {@link Optional#empty()} if the callback
     *         could not be matched to any local record.
     */
    public Optional<ReworkRequestDTO> applyCallback(String externalId, ReworkStatus newStatus, String payloadJson) {
        log.debug("Applying GPF rework callback for externalId={}, status={}", externalId, newStatus);
        Optional<ReworkRequest> opt = reworkRequestRepository.findFirstByExternalId(externalId);
        if (opt.isEmpty()) {
            log.warn("No ReworkRequest found for externalId {}", externalId);
            return Optional.empty();
        }
        ReworkRequest req = opt.get();
        req.setStatus(newStatus);
        if (payloadJson != null) {
            req.setPayloadJson(payloadJson);
        }
        if (newStatus == ReworkStatus.COMPLETED) {
            req.setCompletedAt(ZonedDateTime.now());
        }
        req = reworkRequestRepository.save(req);

        if (newStatus == ReworkStatus.COMPLETED && req.getStockItem() != null) {
            try {
                stockItemService.markRepaired(req.getStockItem().getId());
            } catch (Exception ex) {
                // Don't fail the callback transaction over a mark-repaired error;
                // the rework record is already updated and ops can resolve it.
                log.warn("markRepaired failed after GPF callback for item {}: {}", req.getStockItem().getId(), ex.getMessage());
            }
        }

        return Optional.of(reworkRequestMapper.toDto(req));
    }

    private static String buildPayloadJson(String imei, String reason) {
        String r = reason == null ? "" : reason.replace("\"", "\\\"");
        return "{\"imei\":\"" + (imei == null ? "" : imei) + "\",\"defectReason\":\"" + r + "\"}";
    }
}
