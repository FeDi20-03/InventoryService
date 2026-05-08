package com.tunisales.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tunisales.inventory.domain.enumeration.StockItemStatus;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link StockItemStatusMachine}.
 *
 * <p>Each transition referenced by Étape 1.2 is exercised explicitly, plus a
 * handful of rejection cases (terminal states, no-op transitions, unrelated
 * jumps) to guard against accidental loosening of the rules.</p>
 */
class StockItemStatusMachineTest {

    @Test
    void missingToLostIsAllowed() {
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.MISSING, StockItemStatus.LOST)).isTrue();
    }

    @Test
    void defectiveToAvailableIsAllowed() {
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.DEFECTIVE, StockItemStatus.AVAILABLE)).isTrue();
    }

    @Test
    void availableToMissingIsAllowed() {
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.AVAILABLE, StockItemStatus.MISSING)).isTrue();
    }

    @Test
    void availableToLostIsForbidden_mustGoThroughMissing() {
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.AVAILABLE, StockItemStatus.LOST)).isFalse();
        assertThatThrownBy(() -> StockItemStatusMachine.assertCanTransition(StockItemStatus.AVAILABLE, StockItemStatus.LOST))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("AVAILABLE")
            .hasMessageContaining("LOST");
    }

    @Test
    void deployedToLostIsForbidden() {
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.DEPLOYED, StockItemStatus.LOST)).isFalse();
    }

    @Test
    void lostIsTerminal() {
        assertThat(StockItemStatusMachine.allowedNextStates(StockItemStatus.LOST)).isEmpty();
        for (StockItemStatus to : StockItemStatus.values()) {
            assertThat(StockItemStatusMachine.canTransition(StockItemStatus.LOST, to)).as("LOST -> %s should be forbidden", to).isFalse();
        }
    }

    @Test
    void soldIsTerminal() {
        assertThat(StockItemStatusMachine.allowedNextStates(StockItemStatus.SOLD)).isEmpty();
    }

    @Test
    void retiredIsTerminal() {
        assertThat(StockItemStatusMachine.allowedNextStates(StockItemStatus.RETIRED)).isEmpty();
    }

    @Test
    void selfTransitionIsForbidden() {
        for (StockItemStatus s : StockItemStatus.values()) {
            assertThat(StockItemStatusMachine.canTransition(s, s)).as("%s -> %s should be forbidden", s, s).isFalse();
        }
    }

    @Test
    void nullsAreRejected() {
        assertThat(StockItemStatusMachine.canTransition(null, StockItemStatus.AVAILABLE)).isFalse();
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.AVAILABLE, null)).isFalse();
    }

    @Test
    void availableCanGoToMostOperationalStates() {
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.AVAILABLE, StockItemStatus.RESERVED)).isTrue();
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.AVAILABLE, StockItemStatus.ALLOCATED)).isTrue();
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.AVAILABLE, StockItemStatus.IN_TRANSIT)).isTrue();
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.AVAILABLE, StockItemStatus.DEFECTIVE)).isTrue();
    }

    @Test
    void deployedCanBeSoldOrFlaggedDefective() {
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.DEPLOYED, StockItemStatus.SOLD)).isTrue();
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.DEPLOYED, StockItemStatus.DEFECTIVE)).isTrue();
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.DEPLOYED, StockItemStatus.MISSING)).isTrue();
    }

    @Test
    void inTransitCanLand() {
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.IN_TRANSIT, StockItemStatus.DEPLOYED)).isTrue();
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.IN_TRANSIT, StockItemStatus.AVAILABLE)).isTrue();
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.IN_TRANSIT, StockItemStatus.MISSING)).isTrue();
    }

    @Test
    void missingCanBeRecoveredToAvailable() {
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.MISSING, StockItemStatus.AVAILABLE)).isTrue();
    }

    @Test
    void defectiveCanBeRetired() {
        assertThat(StockItemStatusMachine.canTransition(StockItemStatus.DEFECTIVE, StockItemStatus.RETIRED)).isTrue();
    }
}
