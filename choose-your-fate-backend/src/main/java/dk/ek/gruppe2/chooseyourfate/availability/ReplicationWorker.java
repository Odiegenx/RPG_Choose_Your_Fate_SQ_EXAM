package dk.ek.gruppe2.chooseyourfate.availability;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReplicationWorker {

    private final ReplicationQueue replicationQueue;
    private final SecondaryReplicationGateway secondaryReplicationGateway;
    private final int maxRetries;

    public ReplicationWorker(
            ReplicationQueue replicationQueue,
            SecondaryReplicationGateway secondaryReplicationGateway,
            @Value("${app.availability.replication.max-retries:3}") int maxRetries
    ) {
        this.replicationQueue = replicationQueue;
        this.secondaryReplicationGateway = secondaryReplicationGateway;
        this.maxRetries = Math.max(1, maxRetries);
    }

    public boolean processNext() {
        return replicationQueue.poll()
                .map(this::process)
                .orElse(false);
    }

    public int processBatch(int maxJobs) {
        int processed = 0;
        while (processed < maxJobs && processNext()) {
            processed++;
        }
        return processed;
    }

    private boolean process(ReplicationJob job) {
        job.markInProgress();
        try {
            secondaryReplicationGateway.apply(job);
            job.markCompleted();
            replicationQueue.markCompleted(job);
        } catch (RuntimeException ex) {
            job.markFailed(ex, maxRetries);
            if (job.getStatus() == ReplicationStatus.DEAD_LETTER) {
                replicationQueue.markDeadLetter(job);
            } else {
                replicationQueue.requeue(job);
            }
        }
        return true;
    }
}
