package dk.ek.gruppe2.chooseyourfate.availability;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrimaryHealthServiceTest {

    @Test
    void primaryIsUnavailableOnlyAfterThresholdFailures() {
        PrimaryHealthService healthService = new PrimaryHealthService(
                new SequenceProbe(false, false),
                2
        );

        assertFalse(healthService.checkPrimaryHealth());
        assertFalse(healthService.isPrimaryUnavailable());

        assertFalse(healthService.checkPrimaryHealth());
        assertTrue(healthService.isPrimaryUnavailable());
        assertEquals(2, healthService.consecutiveFailures());
    }

    @Test
    void healthyCheckResetsConsecutiveFailures() {
        PrimaryHealthService healthService = new PrimaryHealthService(
                new SequenceProbe(false, true),
                2
        );

        assertFalse(healthService.checkPrimaryHealth());
        assertEquals(1, healthService.consecutiveFailures());

        assertTrue(healthService.checkPrimaryHealth());

        assertEquals(0, healthService.consecutiveFailures());
        assertFalse(healthService.isPrimaryUnavailable());
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
}
