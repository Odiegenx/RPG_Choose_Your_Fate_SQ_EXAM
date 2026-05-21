package dk.ek.gruppe2.chooseyourfate.availability.routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseRoutingServiceTest {

    @Test
    void startsWithPrimaryActiveForReadsAndWrites() {
        // Arrange
        DatabaseRoutingService routingService = new DatabaseRoutingService();

        // Act / Assert
        assertEquals(DatabaseSystemState.PRIMARY_ACTIVE, routingService.state());
        assertEquals(DatabaseRole.PRIMARY, routingService.routeRead());
        assertEquals(DatabaseRole.PRIMARY, routingService.routeWrite());
        assertFalse(routingService.isMaintenanceMode());
    }

    @Test
    void failoverSwitchesActiveRoleToSecondary() {
        // Arrange
        DatabaseRoutingService routingService = new DatabaseRoutingService();

        // Act
        routingService.beginFailover();

        // Assert
        assertEquals(DatabaseSystemState.FAILOVER_IN_PROGRESS, routingService.state());

        // Act
        routingService.completeFailover();

        // Assert
        assertEquals(DatabaseSystemState.SECONDARY_ACTIVE, routingService.state());
        assertEquals(DatabaseRole.SECONDARY, routingService.routeRead());
        assertEquals(DatabaseRole.SECONDARY, routingService.routeWrite());
    }

    @Test
    void completeFailoverWithoutStartingItIsRejected() {
        // Arrange
        DatabaseRoutingService routingService = new DatabaseRoutingService();

        // Act / Assert
        assertThrows(InvalidDatabaseStateTransitionException.class, routingService::completeFailover);
        assertEquals(DatabaseSystemState.PRIMARY_ACTIVE, routingService.state());
    }

    @Test
    void failoverCanBeAbortedWhileInProgress() {
        // Arrange
        DatabaseRoutingService routingService = new DatabaseRoutingService();

        // Act
        routingService.beginFailover();
        routingService.abortFailover();

        // Assert
        assertEquals(DatabaseSystemState.PRIMARY_ACTIVE, routingService.state());
        assertEquals(DatabaseRole.PRIMARY, routingService.activeRole());
    }

    @Test
    void failbackUsesMaintenanceModeAndReturnsToPrimary() {
        // Arrange
        DatabaseRoutingService routingService = new DatabaseRoutingService();
        routingService.beginFailover();
        routingService.completeFailover();

        // Act
        routingService.beginFailback();

        // Assert
        assertEquals(DatabaseSystemState.FAILBACK_IN_PROGRESS, routingService.state());
        assertTrue(routingService.isMaintenanceMode());

        // Act
        routingService.completeFailback();

        // Assert
        assertEquals(DatabaseSystemState.PRIMARY_ACTIVE, routingService.state());
        assertEquals(DatabaseRole.PRIMARY, routingService.activeRole());
        assertFalse(routingService.isMaintenanceMode());
    }
}
