package dk.ek.gruppe2.chooseyourfate.availability;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ObservableSecondaryReplicationGateway implements SecondaryReplicationGateway {

    private final List<ReplicationJob> appliedJobs = new ArrayList<>();

    @Override
    public synchronized void apply(ReplicationJob job) {
        appliedJobs.add(job);
    }

    public synchronized List<ReplicationJob> appliedJobs() {
        return List.copyOf(appliedJobs);
    }
}
