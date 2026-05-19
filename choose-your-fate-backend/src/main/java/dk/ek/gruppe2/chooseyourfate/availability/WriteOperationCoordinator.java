package dk.ek.gruppe2.chooseyourfate.availability;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Supplier;

@Service
public class WriteOperationCoordinator {

    private final DatabaseRoutingService databaseRoutingService;
    private final ReplicationService replicationService;

    public WriteOperationCoordinator(
            DatabaseRoutingService databaseRoutingService,
            ReplicationService replicationService
    ) {
        this.databaseRoutingService = databaseRoutingService;
        this.replicationService = replicationService;
    }

    public <T> T execute(
            ReplicationOperationType operationType,
            String entityName,
            Supplier<Map<String, Object>> replicationPayload,
            Supplier<T> primaryWrite
    ) {
        DatabaseRole writeRole = databaseRoutingService.routeWrite();
        T result = primaryWrite.get();

        if (writeRole == DatabaseRole.PRIMARY) {
            replicationService.enqueue(operationType, entityName, replicationPayload.get());
        }

        return result;
    }

    public void execute(
            ReplicationOperationType operationType,
            String entityName,
            Supplier<Map<String, Object>> replicationPayload,
            Runnable primaryWrite
    ) {
        execute(operationType, entityName, replicationPayload, () -> {
            primaryWrite.run();
            return null;
        });
    }
}
