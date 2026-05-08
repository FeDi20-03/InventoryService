package com.tunisales.inventory.domain.enumeration;

/**
 * Lifecycle of a {@link com.tunisales.inventory.domain.ReworkRequest}:
 *
 * <ul>
 *   <li>{@link #PENDING}: the request was just created and the GPF call is in flight.</li>
 *   <li>{@link #ACK}: GPF acknowledged receipt (HTTP 2xx on the outbound call).</li>
 *   <li>{@link #COMPLETED}: GPF posted a {@code COMPLETED} callback. Triggers
 *       {@link com.tunisales.inventory.service.StockItemService#markRepaired(Long)}.</li>
 *   <li>{@link #FAILED}: outbound call or GPF processing failed.</li>
 * </ul>
 */
public enum ReworkStatus {
    PENDING,
    ACK,
    COMPLETED,
    FAILED,
}
