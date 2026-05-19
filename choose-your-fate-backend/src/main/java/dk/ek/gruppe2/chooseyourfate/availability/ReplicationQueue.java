package dk.ek.gruppe2.chooseyourfate.availability;

import java.util.List;
import java.util.Optional;

public interface ReplicationQueue {

    void enqueue(ReplicationJob job);

    Optional<ReplicationJob> poll();

    void markCompleted(ReplicationJob job);

    void requeue(ReplicationJob job);

    void markDeadLetter(ReplicationJob job);

    List<ReplicationJob> pendingJobs();

    List<ReplicationJob> completedJobs();

    List<ReplicationJob> deadLetterJobs();
}
