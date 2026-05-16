package dk.ek.gruppe2.chooseyourfate.repository.neo4j;

import dk.ek.gruppe2.chooseyourfate.dto.choice.ChoiceResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.CreateSceneRequestDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.SceneResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.UpdateSceneRequestDTO;

import java.util.List;

public interface Neo4jSceneRepository {

    List<SceneResponseDTO> getAllScenes();

    SceneResponseDTO getSceneById(Integer id);

    SceneResponseDTO findSceneForLookahead(Integer id);

    List<ChoiceResponseDTO> findChoicesForScene(Integer sceneId);

    List<SceneResponseDTO> findDestinationScenesForScene(Integer sceneId);

    SceneResponseDTO createScene(CreateSceneRequestDTO request);

    SceneResponseDTO updateScene(Integer id, UpdateSceneRequestDTO request);

    void deleteScene(Integer id);
}
