package dk.ek.gruppe2.chooseyourfate.availability;

import org.springframework.stereotype.Service;

@Service
public class FailoverService {

    private final DatabaseRoutingService databaseRoutingService;
    private final PrimaryHealthService primaryHealthService;

    public FailoverService(
            DatabaseRoutingService databaseRoutingService,
            PrimaryHealthService primaryHealthService
    ) {
        this.databaseRoutingService = databaseRoutingService;
        this.primaryHealthService = primaryHealthService;
    }

    public boolean evaluateAndFailoverIfNeeded() {
        primaryHealthService.checkPrimaryHealth();
        if (!primaryHealthService.isPrimaryUnavailable()) {
            return false;
        }

        triggerManualFailover();
        return true;
    }

    public void triggerManualFailover() {
        databaseRoutingService.beginFailover();
        databaseRoutingService.completeFailover();
    }
}
