package dk.ek.gruppe2.chooseyourfate.availability;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ReplicationJob {

    private final UUID id;
    private final ReplicationOperationType operationType;
    private final String entityName;
    private final Map<String, Object> payload;
    private final Instant createdAt;
    private Instant lastAttemptAt;
    private ReplicationStatus status;
    private int retryCount;
    private String lastError;

    public ReplicationJob(
            ReplicationOperationType operationType,
            String entityName,
            Map<String, Object> payload
    ) {
        this.id = UUID.randomUUID();
        this.operationType = operationType;
        this.entityName = entityName;
        this.payload = new LinkedHashMap<>(payload);
        this.createdAt = Instant.now();
        this.status = ReplicationStatus.PENDING;
    }

    public UUID getId() {
        return id;
    }

    public ReplicationOperationType getOperationType() {
        return operationType;
    }

    public String getEntityName() {
        return entityName;
    }

    public Map<String, Object> getPayload() {
        return Map.copyOf(payload);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastAttemptAt() {
        return lastAttemptAt;
    }

    public ReplicationStatus getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getLastError() {
        return lastError;
    }

    void markInProgress() {
        status = ReplicationStatus.IN_PROGRESS;
        lastAttemptAt = Instant.now();
    }

    void markCompleted() {
        status = ReplicationStatus.COMPLETED;
        lastError = null;
    }

    void markFailed(RuntimeException ex, int maxRetries) {
        retryCount++;
        lastError = ex.getMessage();
        status = retryCount >= maxRetries ? ReplicationStatus.DEAD_LETTER : ReplicationStatus.FAILED;
    }

    void markPendingForRetry() {
        status = ReplicationStatus.PENDING;
    }
}
