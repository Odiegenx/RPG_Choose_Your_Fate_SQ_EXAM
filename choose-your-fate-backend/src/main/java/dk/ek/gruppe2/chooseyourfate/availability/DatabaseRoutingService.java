package dk.ek.gruppe2.chooseyourfate.availability;

import org.springframework.stereotype.Service;

@Service
public class DatabaseRoutingService {

    private DatabaseSystemState state = DatabaseSystemState.PRIMARY_ACTIVE;
    private DatabaseRole activeRole = DatabaseRole.PRIMARY;
    private boolean maintenanceMode;

    public synchronized DatabaseRole activeRole() {
        return activeRole;
    }

    public synchronized DatabaseRole routeRead() {
        return activeRole;
    }

    public synchronized DatabaseRole routeWrite() {
        return activeRole;
    }

    public synchronized DatabaseSystemState state() {
        return state;
    }

    public synchronized boolean isMaintenanceMode() {
        return maintenanceMode;
    }

    public synchronized void beginFailover() {
        requireState(DatabaseSystemState.PRIMARY_ACTIVE, "Failover can only start while primary is active");
        state = DatabaseSystemState.FAILOVER_IN_PROGRESS;
    }

    public synchronized void completeFailover() {
        requireState(DatabaseSystemState.FAILOVER_IN_PROGRESS, "Failover must be in progress before it can complete");
        activeRole = DatabaseRole.SECONDARY;
        state = DatabaseSystemState.SECONDARY_ACTIVE;
    }

    public synchronized void beginFailback() {
        requireState(DatabaseSystemState.SECONDARY_ACTIVE, "Failback can only start while secondary is active");
        maintenanceMode = true;
        state = DatabaseSystemState.FAILBACK_IN_PROGRESS;
    }

    public synchronized void completeFailback() {
        requireState(DatabaseSystemState.FAILBACK_IN_PROGRESS, "Failback must be in progress before it can complete");
        activeRole = DatabaseRole.PRIMARY;
        maintenanceMode = false;
        state = DatabaseSystemState.PRIMARY_ACTIVE;
    }

    public synchronized void abortFailback() {
        requireState(DatabaseSystemState.FAILBACK_IN_PROGRESS, "Only failback in progress can be aborted");
        maintenanceMode = false;
        activeRole = DatabaseRole.SECONDARY;
        state = DatabaseSystemState.SECONDARY_ACTIVE;
    }

    private void requireState(DatabaseSystemState expected, String message) {
        if (state != expected) {
            throw new InvalidDatabaseStateTransitionException(message + ". Current state: " + state);
        }
    }
}
