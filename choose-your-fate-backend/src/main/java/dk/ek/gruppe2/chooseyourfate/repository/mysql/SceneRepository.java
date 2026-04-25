package dk.ek.gruppe2.chooseyourfate.repository.mysql;

import dk.ek.gruppe2.chooseyourfate.model.mysql.Scene;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SceneRepository extends JpaRepository<Scene, Integer> {
}