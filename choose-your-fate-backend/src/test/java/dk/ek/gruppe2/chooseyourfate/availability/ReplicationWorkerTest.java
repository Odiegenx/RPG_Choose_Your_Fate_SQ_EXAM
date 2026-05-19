package dk.ek.gruppe2.chooseyourfate.availability;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReplicationWorkerTest {

    @Test
    void successfulJobIsAppliedAndMarkedCompleted() {
        InMemoryReplicationQueue queue = new InMemoryReplicationQueue();
        RecordingGateway gateway = new RecordingGateway(0);
        ReplicationWorker worker = new ReplicationWorker(queue, gateway, 3);
        ReplicationJob job = new ReplicationJob(ReplicationOperationType.CREATE, "account", Map.of("id", 1));
        queue.enqueue(job);

        assertTrue(worker.processNext());

        assertEquals(1, gateway.appliedCount);
        assertEquals(ReplicationStatus.COMPLETED, job.getStatus());
        assertEquals(1, queue.completedJobs().size());
        assertEquals(0, queue.pendingJobs().size());
    }

    @Test
    void failedJobIsRequeuedUntilItCanSucceed() {
        InMemoryReplicationQueue queue = new InMemoryReplicationQueue();
        RecordingGateway gateway = new RecordingGateway(1);
        ReplicationWorker worker = new ReplicationWorker(queue, gateway, 3);
        ReplicationJob job = new ReplicationJob(ReplicationOperationType.UPDATE, "account", Map.of("id", 1));
        queue.enqueue(job);

        worker.processNext();
        assertEquals(1, job.getRetryCount());
        assertEquals(ReplicationStatus.PENDING, job.getStatus());
        assertEquals(1, queue.pendingJobs().size());

        worker.processNext();
        assertEquals(ReplicationStatus.COMPLETED, job.getStatus());
        assertEquals(1, queue.completedJobs().size());
    }

    @Test
    void failedJobMovesToDeadLetterAfterRetryLimit() {
        InMemoryReplicationQueue queue = new InMemoryReplicationQueue();
        RecordingGateway gateway = new RecordingGateway(Integer.MAX_VALUE);
        ReplicationWorker worker = new ReplicationWorker(queue, gateway, 2);
        ReplicationJob job = new ReplicationJob(ReplicationOperationType.DELETE, "account", Map.of("id", 1));
        queue.enqueue(job);

        worker.processNext();
        worker.processNext();

        assertEquals(ReplicationStatus.DEAD_LETTER, job.getStatus());
        assertEquals(2, job.getRetryCount());
        assertEquals(1, queue.deadLetterJobs().size());
        assertEquals(0, queue.pendingJobs().size());
    }

    private static class RecordingGateway implements SecondaryReplicationGateway {
        private int failuresRemaining;
        private int appliedCount;

        RecordingGateway(int failuresRemaining) {
            this.failuresRemaining = failuresRemaining;
        }

        @Override
        public void apply(ReplicationJob job) {
            if (failuresRemaining > 0) {
                failuresRemaining--;
                throw new IllegalStateException("secondary unavailable");
            }
            appliedCount++;
        }
    }
}
