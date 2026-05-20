package dk.ek.gruppe2.chooseyourfate.service.neo4j;

import dk.ek.gruppe2.chooseyourfate.dto.scene.CreateSceneRequestDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.SceneResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.SceneLookaheadResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.UpdateSceneRequestDTO;
import dk.ek.gruppe2.chooseyourfate.interfaces.SceneDataAccess;
import dk.ek.gruppe2.chooseyourfate.repository.neo4j.Neo4jSceneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Neo4jSceneService implements SceneDataAccess {

    private final Neo4jSceneRepository neo4jSceneRepository;

    public Neo4jSceneService(Neo4jSceneRepository neo4jSceneRepository) {
        this.neo4jSceneRepository = neo4jSceneRepository;
    }

    @Override
    public List<SceneResponseDTO> getAllScenes() {
        return neo4jSceneRepository.getAllScenes();
    }

    @Override
    public SceneResponseDTO getSceneById(String id) {
        Integer parsedId = Integer.parseInt(id);
        return neo4jSceneRepository.getSceneById(parsedId);
    }

    @Override
    public SceneLookaheadResponseDTO getSceneLookahead(String id) {
        Integer parsedId = Integer.parseInt(id);
        return new SceneLookaheadResponseDTO(
                neo4jSceneRepository.findSceneForLookahead(parsedId),
                neo4jSceneRepository.findChoicesForScene(parsedId),
                neo4jSceneRepository.findDestinationScenesForScene(parsedId)
        );
    }

    @Override
    public SceneResponseDTO createScene(CreateSceneRequestDTO request) {
        return neo4jSceneRepository.createScene(request);
    }

    @Override
    public SceneResponseDTO updateScene(String id, UpdateSceneRequestDTO request) {
        Integer parsedId = Integer.parseInt(id);
        return neo4jSceneRepository.updateScene(parsedId, request);
    }

    @Override
    public void deleteScene(String id) {
        Integer parsedId = Integer.parseInt(id);
        neo4jSceneRepository.deleteScene(parsedId);
    }
}
