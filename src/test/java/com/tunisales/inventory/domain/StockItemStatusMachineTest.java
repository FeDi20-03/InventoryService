package com.tunisales.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tunisales.inventory.domain.enumeration.StockItemStatus;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

    /**
     * Parametrised exhaustive coverage of the EXPECTED-ALLOWED transition table.
     *
     * <p>Each pair below corresponds to a row in {@link StockItemStatusMachine#allowedNextStates(StockItemStatus)}
     * — keeping it here gives us a tight regression guard against accidental
     * loosening of the rules.</p>
     */
    static Stream<Arguments> allowedTransitions() {
        return Stream.of(
            Arguments.of(StockItemStatus.AVAILABLE, StockItemStatus.RESERVED),
            Arguments.of(StockItemStatus.AVAILABLE, StockItemStatus.ALLOCATED),
            Arguments.of(StockItemStatus.AVAILABLE, StockItemStatus.IN_TRANSIT),
            Arguments.of(StockItemStatus.AVAILABLE, StockItemStatus.DEFECTIVE),
            Arguments.of(StockItemStatus.AVAILABLE, StockItemStatus.MISSING),
            Arguments.of(StockItemStatus.AVAILABLE, StockItemStatus.RETIRED),
            Arguments.of(StockItemStatus.RESERVED, StockItemStatus.AVAILABLE),
            Arguments.of(StockItemStatus.RESERVED, StockItemStatus.ALLOCATED),
            Arguments.of(StockItemStatus.RESERVED, StockItemStatus.IN_TRANSIT),
            Arguments.of(StockItemStatus.ALLOCATED, StockItemStatus.IN_TRANSIT),
            Arguments.of(StockItemStatus.ALLOCATED, StockItemStatus.AVAILABLE),
            Arguments.of(StockItemStatus.ALLOCATED, StockItemStatus.DEPLOYED),
            Arguments.of(StockItemStatus.IN_TRANSIT, StockItemStatus.DEPLOYED),
            Arguments.of(StockItemStatus.IN_TRANSIT, StockItemStatus.AVAILABLE),
            Arguments.of(StockItemStatus.IN_TRANSIT, StockItemStatus.MISSING),
            Arguments.of(StockItemStatus.DEPLOYED, StockItemStatus.SOLD),
            Arguments.of(StockItemStatus.DEPLOYED, StockItemStatus.DEFECTIVE),
            Arguments.of(StockItemStatus.DEPLOYED, StockItemStatus.AVAILABLE),
            Arguments.of(StockItemStatus.DEPLOYED, StockItemStatus.MISSING),
            Arguments.of(StockItemStatus.DEFECTIVE, StockItemStatus.AVAILABLE),
            Arguments.of(StockItemStatus.DEFECTIVE, StockItemStatus.RETIRED),
            Arguments.of(StockItemStatus.MISSING, StockItemStatus.AVAILABLE),
            Arguments.of(StockItemStatus.MISSING, StockItemStatus.LOST)
        );
    }

    @ParameterizedTest(name = "{0} -> {1} is allowed")
    @MethodSource("allowedTransitions")
    void allTabulatedTransitionsAreAllowed(StockItemStatus from, StockItemStatus to) {
        // Arrange — none (pure function)

        // Act
        boolean canTransition = StockItemStatusMachine.canTransition(from, to);

        // Assert
        assertThat(canTransition).isTrue();
        // assertCanTransition must not throw for the same input.
        StockItemStatusMachine.assertCanTransition(from, to);
        assertThat(StockItemStatusMachine.allowedNextStates(from)).contains(to);
    }

    /**
     * Cross-product of every status pair: any combination NOT enumerated by
     * {@link #allowedTransitions()} must be rejected — both {@code canTransition}
     * returning false AND {@code assertCanTransition} throwing.
     */
    @ParameterizedTest(name = "{0} -> {1} is forbidden")
    @MethodSource("forbiddenTransitions")
    void allOtherTransitionsAreForbiddenAndThrow(StockItemStatus from, StockItemStatus to) {
        // Act
        boolean canTransition = StockItemStatusMachine.canTransition(from, to);

        // Assert
        assertThat(canTransition).isFalse();
        assertThatThrownBy(() -> StockItemStatusMachine.assertCanTransition(from, to))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Illegal StockItem status transition");
    }

    static Stream<Arguments> forbiddenTransitions() {
        java.util.Set<java.util.AbstractMap.SimpleEntry<StockItemStatus, StockItemStatus>> allowed = allowedTransitions()
            .map(args -> new java.util.AbstractMap.SimpleEntry<>((StockItemStatus) args.get()[0], (StockItemStatus) args.get()[1]))
            .collect(java.util.stream.Collectors.toSet());
        Stream.Builder<Arguments> builder = Stream.builder();
        for (StockItemStatus from : StockItemStatus.values()) {
            for (StockItemStatus to : StockItemStatus.values()) {
                if (from == to) {
                    continue; // self-transition is exercised separately
                }
                if (allowed.contains(new java.util.AbstractMap.SimpleEntry<>(from, to))) {
                    continue;
                }
                builder.add(Arguments.of(from, to));
            }
        }
        return builder.build();
    }
}
