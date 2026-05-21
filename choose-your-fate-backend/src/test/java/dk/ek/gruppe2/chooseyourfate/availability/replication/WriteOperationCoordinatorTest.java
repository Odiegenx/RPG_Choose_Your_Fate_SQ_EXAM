package dk.ek.gruppe2.chooseyourfate.availability.replication;

import dk.ek.gruppe2.chooseyourfate.availability.routing.DatabaseRole;
import dk.ek.gruppe2.chooseyourfate.availability.routing.DatabaseRoutingService;
import dk.ek.gruppe2.chooseyourfate.availability.routing.DatabaseSystemState;
import dk.ek.gruppe2.chooseyourfate.availability.routing.InvalidDatabaseStateTransitionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class WriteOperationCoordinatorTest {

    @Test
    void successfulPrimaryWriteQueuesReplicationJob() {
        // Arrange
        DatabaseRoutingService routingService = new DatabaseRoutingService();
        ReplicationService replicationService = mock(ReplicationService.class);
        WriteOperationCoordinator coordinator = new WriteOperationCoordinator(
                routingService,
                replicationService
        );

        // Act
        String result = coordinator.execute(
                ReplicationOperationType.CREATE,
                "account",
                () -> Map.of("id", 1),
                () -> "created"
        );

        // Assert
        assertEquals("created", result);
        verify(replicationService).createJobAndAddToQueue(
                eq(ReplicationOperationType.CREATE),
                eq("account"),
                eq(Map.of("id", 1))
        );
    }

    @Test
    void secondaryWriteAfterFailoverDoesNotQueueReplicationBackToSecondary() {
        // Arrange
        DatabaseRoutingService routingService = new DatabaseRoutingService();
        routingService.beginFailover();
        routingService.completeFailover();
        ReplicationService replicationService = mock(ReplicationService.class);
        WriteOperationCoordinator coordinator = new WriteOperationCoordinator(
                routingService,
                replicationService
        );

        // Act
        coordinator.execute(
                ReplicationOperationType.CREATE,
                "account",
                () -> Map.of("id", 1),
                () -> "created"
        );

        // Assert
        verify(replicationService, never()).createJobAndAddToQueue(any(), any(), any());
    }

    @Test
    void failedPrimaryWriteDoesNotQueueReplicationJob() {
        // Arrange
        DatabaseRoutingService routingService = new DatabaseRoutingService();
        ReplicationService replicationService = mock(ReplicationService.class);
        WriteOperationCoordinator coordinator = new WriteOperationCoordinator(
                routingService,
                replicationService
        );

        // Act / Assert
        assertThrows(
                IllegalStateException.class,
                () -> coordinator.execute(
                        ReplicationOperationType.CREATE,
                        "account",
                        () -> Map.of("id", 1),
                        () -> {
                            throw new IllegalStateException("duplicate key");
                        }
                )
        );

        // Assert
        verify(replicationService, never()).createJobAndAddToQueue(any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource("blockedTransitionStates")
    void writeIsBlockedDuringTransitionStates(
            DatabaseSystemState expectedState,
            Consumer<DatabaseRoutingService> stateSetup
    ) {
        // Arrange
        DatabaseRoutingService routingService = new DatabaseRoutingService();
        stateSetup.accept(routingService);
        ReplicationService replicationService = mock(ReplicationService.class);
        WriteOperationCoordinator coordinator = new WriteOperationCoordinator(
                routingService,
                replicationService
        );

        // Act / Assert
        InvalidDatabaseStateTransitionException exception = assertThrows(
                InvalidDatabaseStateTransitionException.class,
                () -> coordinator.execute(
                        ReplicationOperationType.UPDATE,
                        "account",
                        () -> Map.of("id", 1),
                        () -> "updated"
                )
        );

        // Assert
        assertEquals("Writes are blocked while state is " + expectedState, exception.getMessage());
        verify(replicationService, never()).createJobAndAddToQueue(any(), any(), any());
    }

    private static Stream<org.junit.jupiter.params.provider.Arguments> blockedTransitionStates() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        DatabaseSystemState.FAILOVER_IN_PROGRESS,
                        (Consumer<DatabaseRoutingService>) DatabaseRoutingService::beginFailover
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        DatabaseSystemState.FAILBACK_IN_PROGRESS,
                        (Consumer<DatabaseRoutingService>) routingService -> {
                            routingService.beginFailover();
                            routingService.completeFailover();
                            routingService.beginFailback();
                        }
                )
        );
    }
}
