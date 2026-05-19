package dk.ek.gruppe2.chooseyourfate.availability;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WriteOperationCoordinatorTest {

    @Test
    void successfulPrimaryWriteQueuesReplicationJob() {
        DatabaseRoutingService routingService = new DatabaseRoutingService();
        InMemoryReplicationQueue queue = new InMemoryReplicationQueue();
        WriteOperationCoordinator coordinator = new WriteOperationCoordinator(
                routingService,
                new ReplicationService(queue)
        );

        String result = coordinator.execute(
                ReplicationOperationType.CREATE,
                "account",
                () -> Map.of("id", 1),
                () -> "created"
        );

        assertEquals("created", result);
        assertEquals(1, queue.pendingJobs().size());
        assertEquals("account", queue.pendingJobs().getFirst().getEntityName());
    }

    @Test
    void secondaryWriteAfterFailoverDoesNotQueueReplicationBackToSecondary() {
        DatabaseRoutingService routingService = new DatabaseRoutingService();
        routingService.beginFailover();
        routingService.completeFailover();
        InMemoryReplicationQueue queue = new InMemoryReplicationQueue();
        WriteOperationCoordinator coordinator = new WriteOperationCoordinator(
                routingService,
                new ReplicationService(queue)
        );

        coordinator.execute(
                ReplicationOperationType.CREATE,
                "account",
                () -> Map.of("id", 1),
                () -> "created"
        );

        assertEquals(0, queue.pendingJobs().size());
    }
}
