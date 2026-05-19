package dk.ek.gruppe2.chooseyourfate.availability;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseRoutingServiceTest {

    @Test
    void startsWithPrimaryActiveForReadsAndWrites() {
        DatabaseRoutingService routingService = new DatabaseRoutingService();

        assertEquals(DatabaseSystemState.PRIMARY_ACTIVE, routingService.state());
        assertEquals(DatabaseRole.PRIMARY, routingService.routeRead());
        assertEquals(DatabaseRole.PRIMARY, routingService.routeWrite());
        assertFalse(routingService.isMaintenanceMode());
    }

    @Test
    void failoverSwitchesActiveRoleToSecondary() {
        DatabaseRoutingService routingService = new DatabaseRoutingService();

        routingService.beginFailover();
        assertEquals(DatabaseSystemState.FAILOVER_IN_PROGRESS, routingService.state());

        routingService.completeFailover();

        assertEquals(DatabaseSystemState.SECONDARY_ACTIVE, routingService.state());
        assertEquals(DatabaseRole.SECONDARY, routingService.routeRead());
        assertEquals(DatabaseRole.SECONDARY, routingService.routeWrite());
    }

    @Test
    void completeFailoverWithoutStartingItIsRejected() {
        DatabaseRoutingService routingService = new DatabaseRoutingService();

        assertThrows(InvalidDatabaseStateTransitionException.class, routingService::completeFailover);
        assertEquals(DatabaseSystemState.PRIMARY_ACTIVE, routingService.state());
    }

    @Test
    void failbackUsesMaintenanceModeAndReturnsToPrimary() {
        DatabaseRoutingService routingService = new DatabaseRoutingService();
        routingService.beginFailover();
        routingService.completeFailover();

        routingService.beginFailback();
        assertEquals(DatabaseSystemState.FAILBACK_IN_PROGRESS, routingService.state());
        assertTrue(routingService.isMaintenanceMode());

        routingService.completeFailback();

        assertEquals(DatabaseSystemState.PRIMARY_ACTIVE, routingService.state());
        assertEquals(DatabaseRole.PRIMARY, routingService.activeRole());
        assertFalse(routingService.isMaintenanceMode());
    }
}
