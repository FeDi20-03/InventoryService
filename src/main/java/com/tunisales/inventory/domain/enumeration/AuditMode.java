package com.tunisales.inventory.domain.enumeration;

/**
 * Mode in which a {@link com.tunisales.inventory.domain.StockAudit} is being conducted.
 *
 * <ul>
 *   <li>{@link #COUNT}: the auditor only enters the head-count of items in the
 *       perimeter. Used for fast periodic audits.</li>
 *   <li>{@link #SCAN_ONE_BY_ONE}: each item must be scanned individually
 *       (IMEI by IMEI). Triggered automatically when a {@link #COUNT} audit
 *       reveals a discrepancy with the theoretical count, so the auditor can
 *       pinpoint exactly which items are missing or unexpected.</li>
 * </ul>
 */
public enum AuditMode {
    COUNT,
    SCAN_ONE_BY_ONE,
}
