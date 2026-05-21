package dk.ek.gruppe2.chooseyourfate.availability.replication;

import dk.ek.gruppe2.chooseyourfate.availability.routing.DatabaseRole;
import dk.ek.gruppe2.chooseyourfate.availability.routing.DatabaseRoutingService;
import dk.ek.gruppe2.chooseyourfate.availability.routing.DatabaseSystemState;
import dk.ek.gruppe2.chooseyourfate.availability.routing.InvalidDatabaseStateTransitionException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Coordinates write operations with the availability and replication flow (the glue between them).
 * This class acts as a write middleware layer:
 * 1. It checks the current database system state before executing the write operation.
 * 2. It blocks writes while failover or failback is in progress.
 * 3. It executes the provided write function if writes are currently allowed.
 * 4. It creates a replication job when the write was accepted on the primary database.
 * This keeps write-blocking and replication rules centralized instead of duplicating them across services.
 */
@Service
public class WriteOperationCoordinator {

    private final DatabaseRoutingService databaseRoutingService;
    private final ReplicationService replicationService;

    public WriteOperationCoordinator(DatabaseRoutingService databaseRoutingService, ReplicationService replicationService) {
        this.databaseRoutingService = databaseRoutingService;
        this.replicationService = replicationService;
    }
    // This coordinator acts like write middleware: it checks availability state before the actual write is executed.
    public <T> T execute(ReplicationOperationType operationType, String entityName, Function<T, Map<String, Object>> replicationPayload, Supplier<T> funktionToUse) {
        DatabaseSystemState state = databaseRoutingService.state();
        if (state == DatabaseSystemState.FAILOVER_IN_PROGRESS || state == DatabaseSystemState.FAILBACK_IN_PROGRESS) {
            throw new InvalidDatabaseStateTransitionException("Writes are blocked while state is " + state);
        }
        // Capture the target role before running the write, so replication behavior matches the state at write time.
        DatabaseRole databaseToUse = databaseRoutingService.routeWrite();
        T result = funktionToUse.get();
        // Writes accepted on primary must be replicated to secondary. Writes on secondary are not replicated back to secondary.
        if (databaseToUse == DatabaseRole.PRIMARY) {
            replicationService.createJobAndAddToQueue(operationType, entityName, replicationPayload.apply(result));
        }
        return result;
    }
    // Use this overload when the replication payload does not need data returned from the write operation.
    public <T> T execute(ReplicationOperationType operationType, String entityName, Supplier<Map<String, Object>> replicationPayload, Supplier<T> funktionToUse) {
        // Use this overload when the replication payload does not need data returned from the write operation.
        return execute(operationType, entityName, ignored -> replicationPayload.get(), funktionToUse);
    }
    // Void writes, such as delete, are wrapped so they can use the same availability and replication flow.
    public void execute(ReplicationOperationType operationType, String entityName, Supplier<Map<String, Object>> replicationPayload, Runnable primaryWrite) {
        execute(operationType, entityName, replicationPayload, () -> {
            primaryWrite.run();
            return null;
        });
    }
}
