package dk.ek.gruppe2.chooseyourfate.availability;

public record AvailabilityStatusResponse(
        DatabaseSystemState state,
        DatabaseRole activeRole,
        boolean maintenanceMode,
        int primaryConsecutiveFailures,
        int pendingReplicationJobs,
        int completedReplicationJobs,
        int deadLetterReplicationJobs
) {
}
