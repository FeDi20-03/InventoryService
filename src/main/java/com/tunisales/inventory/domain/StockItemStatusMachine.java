package com.tunisales.inventory.domain;

import com.tunisales.inventory.domain.enumeration.StockItemStatus;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Centralised state machine for {@link StockItemStatus} transitions.
 *
 * <p>Each entry in {@link #ALLOWED_TRANSITIONS} maps a source status to the set
 * of statuses it is allowed to transition into. {@link #assertCanTransition(StockItemStatus, StockItemStatus)}
 * is the canonical guard used by service-layer transition methods.</p>
 *
 * <p>Keeping the rules in one place makes invariants explicit, simplifies unit
 * testing and avoids the historic problem of transition logic creeping into
 * multiple service methods.</p>
 */
public final class StockItemStatusMachine {

    private static final Map<StockItemStatus, Set<StockItemStatus>> ALLOWED_TRANSITIONS;

    static {
        EnumMap<StockItemStatus, Set<StockItemStatus>> map = new EnumMap<>(StockItemStatus.class);

        map.put(
            StockItemStatus.AVAILABLE,
            EnumSet.of(
                StockItemStatus.RESERVED,
                StockItemStatus.ALLOCATED,
                StockItemStatus.IN_TRANSIT,
                StockItemStatus.DEFECTIVE,
                StockItemStatus.MISSING,
                StockItemStatus.RETIRED
            )
        );
        map.put(StockItemStatus.RESERVED, EnumSet.of(StockItemStatus.AVAILABLE, StockItemStatus.ALLOCATED, StockItemStatus.IN_TRANSIT));
        map.put(StockItemStatus.ALLOCATED, EnumSet.of(StockItemStatus.IN_TRANSIT, StockItemStatus.AVAILABLE, StockItemStatus.DEPLOYED));
        map.put(StockItemStatus.IN_TRANSIT, EnumSet.of(StockItemStatus.DEPLOYED, StockItemStatus.AVAILABLE, StockItemStatus.MISSING));
        map.put(
            StockItemStatus.DEPLOYED,
            EnumSet.of(StockItemStatus.SOLD, StockItemStatus.DEFECTIVE, StockItemStatus.AVAILABLE, StockItemStatus.MISSING)
        );
        // DEFECTIVE -> AVAILABLE is the markRepaired transition (1.2)
        map.put(StockItemStatus.DEFECTIVE, EnumSet.of(StockItemStatus.AVAILABLE, StockItemStatus.RETIRED));
        // MISSING -> LOST is the declareLost transition (1.2). MISSING can also be recovered to AVAILABLE.
        map.put(StockItemStatus.MISSING, EnumSet.of(StockItemStatus.AVAILABLE, StockItemStatus.LOST));
        // LOST is terminal.
        map.put(StockItemStatus.LOST, EnumSet.noneOf(StockItemStatus.class));
        // SOLD/RETIRED are terminal.
        map.put(StockItemStatus.SOLD, EnumSet.noneOf(StockItemStatus.class));
        map.put(StockItemStatus.RETIRED, EnumSet.noneOf(StockItemStatus.class));

        ALLOWED_TRANSITIONS = Collections.unmodifiableMap(map);
    }

    private StockItemStatusMachine() {}

    /**
     * Returns the set of statuses that {@code from} is allowed to transition to.
     */
    public static Set<StockItemStatus> allowedNextStates(StockItemStatus from) {
        return ALLOWED_TRANSITIONS.getOrDefault(from, EnumSet.noneOf(StockItemStatus.class));
    }

    /**
     * Returns {@code true} iff the transition {@code from -> to} is allowed.
     */
    public static boolean canTransition(StockItemStatus from, StockItemStatus to) {
        if (from == null || to == null) {
            return false;
        }
        if (from == to) {
            return false;
        }
        return allowedNextStates(from).contains(to);
    }

    /**
     * Throws {@link IllegalStateException} if the transition is not allowed.
     */
    public static void assertCanTransition(StockItemStatus from, StockItemStatus to) {
        if (!canTransition(from, to)) {
            throw new IllegalStateException("Illegal StockItem status transition: " + from + " -> " + to);
        }
    }
}
