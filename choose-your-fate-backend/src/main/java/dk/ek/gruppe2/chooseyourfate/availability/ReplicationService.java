package dk.ek.gruppe2.chooseyourfate.availability;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReplicationService {

    private final ReplicationQueue replicationQueue;

    public ReplicationService(ReplicationQueue replicationQueue) {
        this.replicationQueue = replicationQueue;
    }

    public ReplicationJob enqueue(
            ReplicationOperationType operationType,
            String entityName,
            Map<String, Object> payload
    ) {
        ReplicationJob job = new ReplicationJob(operationType, entityName, payload);
        replicationQueue.enqueue(job);
        return job;
    }
}
