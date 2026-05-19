package dk.ek.gruppe2.chooseyourfate.availability;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FailoverAndFailbackServiceTest {

    @Test
    void failoverServiceSwitchesToSecondaryWhenPrimaryIsUnavailable() {
        DatabaseRoutingService routingService = new DatabaseRoutingService();
        PrimaryHealthService healthService = new PrimaryHealthService(new SequenceProbe(false), 1);
        FailoverService failoverService = new FailoverService(routingService, healthService);

        assertTrue(failoverService.evaluateAndFailoverIfNeeded());

        assertEquals(DatabaseSystemState.SECONDARY_ACTIVE, routingService.state());
        assertEquals(DatabaseRole.SECONDARY, routingService.activeRole());
    }

    @Test
    void failbackRequiresHealthyPrimaryBeforeMaintenanceStarts() {
        DatabaseRoutingService routingService = new DatabaseRoutingService();
        routingService.beginFailover();
        routingService.completeFailover();

        PrimaryHealthService healthService = new PrimaryHealthService(new SequenceProbe(false), 1);
        FailbackService failbackService = new FailbackService(
                routingService,
                healthService,
                new RecordingSyncService()
        );

        assertThrows(InvalidDatabaseStateTransitionException.class, failbackService::beginManualFailback);
        assertEquals(DatabaseSystemState.SECONDARY_ACTIVE, routingService.state());
    }

    @Test
    void failbackSynchronizesDataAndReturnsToPrimary() {
        DatabaseRoutingService routingService = new DatabaseRoutingService();
        routingService.beginFailover();
        routingService.completeFailover();
        RecordingSyncService syncService = new RecordingSyncService();
        FailbackService failbackService = new FailbackService(
                routingService,
                new PrimaryHealthService(new SequenceProbe(true), 1),
                syncService
        );

        failbackService.beginManualFailback();
        failbackService.completeManualFailback();

        assertEquals(1, syncService.syncCount);
        assertEquals(DatabaseSystemState.PRIMARY_ACTIVE, routingService.state());
        assertEquals(DatabaseRole.PRIMARY, routingService.activeRole());
    }

    private static class SequenceProbe implements SqlHealthProbe {
        private final Queue<Boolean> results = new ArrayDeque<>();

        SequenceProbe(Boolean... values) {
            results.addAll(java.util.List.of(values));
        }

        @Override
        public boolean isHealthy(DatabaseRole role) {
            return results.isEmpty() || results.remove();
        }
    }

    private static class RecordingSyncService implements DataSynchronizationService {
        private int syncCount;

        @Override
        public void synchronizeSecondaryToPrimary() {
            syncCount++;
        }
    }
}
