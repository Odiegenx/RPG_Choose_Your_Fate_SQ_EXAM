package dk.ek.gruppe2.chooseyourfate.service;

import java.util.List;

import dk.ek.gruppe2.chooseyourfate.dto.scene.SceneLookaheadResponseDTO;
import org.springframework.stereotype.Service;

import dk.ek.gruppe2.chooseyourfate.dto.scene.CreateSceneRequestDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.SceneResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.UpdateSceneRequestDTO;
import dk.ek.gruppe2.chooseyourfate.enums.DataSourceType;
import dk.ek.gruppe2.chooseyourfate.interfaces.SceneDataAccess;
import dk.ek.gruppe2.chooseyourfate.service.mysql.SqlSceneService;

@Service
public class SceneService {
    private final SqlSceneService sqlSceneService;
    //private final Neo4jSceneService neo4jSceneService;
    //private final MongoSceneService mongoSceneService;

    public SceneService(
            SqlSceneService sqlSceneService
            //Neo4jSceneervice neo4jSceneService,
            //MongoSceneService mongoSceneService
    ) {
        this.sqlSceneService = sqlSceneService;
        //this.neo4jSceneService = neo4jSceneService;
        //this.mongoSceneService = mongoSceneService;
    }

    public List<SceneResponseDTO> getAllScenes(DataSourceType source) {
        return resolveDataService(source).getAllScenes();
    }

    public SceneLookaheadResponseDTO getSceneById(DataSourceType source, Integer id) {
        return resolveDataService(source).getSceneById(id);
    }

    // Calls the SQL-only lookahead implementation directly for easy endpoint testing.
    public SceneLookaheadResponseDTO getSqlSceneLookAheadById(DataSourceType source,Integer id) {
        return resolveDataService(source).getSceneById(id);
    }

    public SceneResponseDTO createScene(DataSourceType source, CreateSceneRequestDTO request) {
        return resolveDataService(source).createScene(request);
    }

    public SceneResponseDTO updateScene(DataSourceType source, Integer id, UpdateSceneRequestDTO request) {
        return resolveDataService(source).updateScene(id, request);
    }

    public void deleteScene(DataSourceType source, Integer id) {
        resolveDataService(source).deleteScene(id);
    }

    public SceneResponseDTO registerScene(CreateSceneRequestDTO request) {
        return sqlSceneService.createScene(request);
    }

    private SceneDataAccess resolveDataService(DataSourceType source) {
        return switch (source) {
            case SQL -> sqlSceneService;
            //case NEO4J -> neo4jSceneService;
            //case MONGODB -> mongoSceneservice;
            default -> throw new IllegalArgumentException("Unexpected value: " + source);
        };
    }
}
