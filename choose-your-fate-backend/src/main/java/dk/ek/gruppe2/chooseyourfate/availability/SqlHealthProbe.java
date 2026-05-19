package dk.ek.gruppe2.chooseyourfate.availability;

public interface SqlHealthProbe {

    boolean isHealthy(DatabaseRole role);
}
