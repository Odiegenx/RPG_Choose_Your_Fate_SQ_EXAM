package dk.ek.gruppe2.chooseyourfate.interfaces;

import java.util.List;

import dk.ek.gruppe2.chooseyourfate.dto.scene.SceneLookaheadResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.SceneResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.CreateSceneRequestDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.UpdateSceneRequestDTO;

public interface SceneDataAccess {
    List<SceneResponseDTO> getAllScenes();

    SceneLookaheadResponseDTO getSceneById(Integer id);

    SceneResponseDTO createScene(CreateSceneRequestDTO request);

    SceneResponseDTO updateScene(Integer id, UpdateSceneRequestDTO request);

    void deleteScene(Integer id);
}
