package dk.ek.gruppe2.chooseyourfate.availability;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class InMemoryReplicationQueue implements ReplicationQueue {

    private final Queue<ReplicationJob> pending = new ConcurrentLinkedQueue<>();
    private final List<ReplicationJob> completed = new ArrayList<>();
    private final List<ReplicationJob> deadLetter = new ArrayList<>();

    @Override
    public void enqueue(ReplicationJob job) {
        pending.add(job);
    }

    @Override
    public Optional<ReplicationJob> poll() {
        return Optional.ofNullable(pending.poll());
    }

    @Override
    public synchronized void markCompleted(ReplicationJob job) {
        completed.add(job);
    }

    @Override
    public void requeue(ReplicationJob job) {
        job.markPendingForRetry();
        pending.add(job);
    }

    @Override
    public synchronized void markDeadLetter(ReplicationJob job) {
        deadLetter.add(job);
    }

    @Override
    public List<ReplicationJob> pendingJobs() {
        return List.copyOf(pending);
    }

    @Override
    public synchronized List<ReplicationJob> completedJobs() {
        return List.copyOf(completed);
    }

    @Override
    public synchronized List<ReplicationJob> deadLetterJobs() {
        return List.copyOf(deadLetter);
    }
}
