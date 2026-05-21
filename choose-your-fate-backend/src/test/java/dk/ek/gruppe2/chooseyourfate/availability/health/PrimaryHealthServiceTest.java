package dk.ek.gruppe2.chooseyourfate.availability.health;

import dk.ek.gruppe2.chooseyourfate.availability.routing.DatabaseRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PrimaryHealthServiceTest {

    @Test
    void primaryIsUnavailableOnlyAfterThresholdFailures() {
        // Arrange
        SqlHealthProbe sqlHealthProbe = mock(SqlHealthProbe.class);
        when(sqlHealthProbe.isHealthy(DatabaseRole.PRIMARY)).thenReturn(false, false);
        PrimaryHealthService healthService = new PrimaryHealthService(sqlHealthProbe, 2);

        // Act / Assert
        assertFalse(healthService.checkPrimaryHealth());
        assertFalse(healthService.isPrimaryUnavailable());

        // Act / Assert
        assertFalse(healthService.checkPrimaryHealth());
        assertTrue(healthService.isPrimaryUnavailable());
        assertEquals(2, healthService.consecutiveFailures());
    }

    @Test
    void healthyCheckResetsConsecutiveFailures() {
        // Arrange
        SqlHealthProbe sqlHealthProbe = mock(SqlHealthProbe.class);
        when(sqlHealthProbe.isHealthy(DatabaseRole.PRIMARY)).thenReturn(false, true);
        PrimaryHealthService healthService = new PrimaryHealthService(sqlHealthProbe, 2);

        // Act / Assert
        assertFalse(healthService.checkPrimaryHealth());
        assertEquals(1, healthService.consecutiveFailures());

        // Act
        assertTrue(healthService.checkPrimaryHealth());

        // Assert
        assertEquals(0, healthService.consecutiveFailures());
        assertFalse(healthService.isPrimaryUnavailable());
    }
}
