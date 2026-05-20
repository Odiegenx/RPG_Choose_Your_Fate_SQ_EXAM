package dk.ek.gruppe2.chooseyourfate.repository.neo4j;

import dk.ek.gruppe2.chooseyourfate.dto.choice.ChoiceResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.CreateSceneRequestDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.SceneResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.UpdateSceneRequestDTO;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Repository
public class Neo4jSceneRepositoryImpl implements Neo4jSceneRepository {

    private final Neo4jClient neo4jClient;

    public Neo4jSceneRepositoryImpl(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public List<SceneResponseDTO> getAllScenes() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public SceneResponseDTO getSceneById(Integer id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public SceneResponseDTO createScene(CreateSceneRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public SceneResponseDTO updateScene(Integer id, UpdateSceneRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteScene(Integer id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }


    @Override
    public SceneResponseDTO findSceneForLookahead(Integer sceneId) {
        SceneData sceneData = neo4jClient.query("""
                    MATCH (chapter:Chapter)-[:HAS_SCENE]->(scene:Scene {id: $sceneId})
                    RETURN scene.id AS id,
                           scene.name AS name,
                           chapter.id AS chapterId
                    """)
                .bind(sceneId).to("sceneId")
                .fetchAs(SceneData.class)
                .mappedBy((typeSystem, sceneRecord) -> new SceneData(
                        sceneRecord.get("id").asInt(),
                        sceneRecord.get("name").asString(),
                        sceneRecord.get("chapterId").asInt()
                ))
                .one()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Scene not found with id: " + sceneId
                ));

        return toSceneResponseDTO(sceneData);
    }

    @Override
    public List<ChoiceResponseDTO> findChoicesForScene(Integer sceneId) {
        return neo4jClient.query("""
                    MATCH (scene:Scene {id: $sceneId})-[:HAS_CHOICE]->(choice:Choice)
                    OPTIONAL MATCH (choice)-[:LEADS_TO]->(destination:Scene)
                    RETURN choice.id AS id,
                           destination.id AS destinationSceneId,
                           scene.id AS sceneId,
                           choice.description AS description,
                           choice.consequence AS consequence,
                           choice.targetId AS targetId,
                           choice.valueInt AS valueInt,
                           choice.requirements AS requirements
                    ORDER BY choice.id
                    """)
                .bind(sceneId).to("sceneId")
                .fetchAs(ChoiceData.class)
                .mappedBy((typeSystem, choiceRecord) -> new ChoiceData(
                        choiceRecord.get("id").asInt(),
                        choiceRecord.get("destinationSceneId").isNull() ? null : choiceRecord.get("destinationSceneId").asInt(),
                        choiceRecord.get("sceneId").asInt(),
                        choiceRecord.get("description").isNull() ? null : choiceRecord.get("description").asString(),
                        choiceRecord.get("consequence").isNull() ? null : choiceRecord.get("consequence").asString(),
                        choiceRecord.get("targetId").isNull() ? null : choiceRecord.get("targetId").asInt(),
                        choiceRecord.get("valueInt").isNull() ? null : choiceRecord.get("valueInt").asInt(),
                        choiceRecord.get("requirements").isNull() ? null : choiceRecord.get("requirements").asString()
                ))
                .all()
                .stream()
                .map(this::toChoiceResponseDTO)
                .toList();
    }

    @Override
    public List<SceneResponseDTO> findDestinationScenesForScene(Integer sceneId) {
        return neo4jClient.query("""
                    MATCH (:Scene {id: $sceneId})-[:HAS_CHOICE]->(:Choice)-[:LEADS_TO]->(destination:Scene)
                    MATCH (chapter:Chapter)-[:HAS_SCENE]->(destination)
                    RETURN DISTINCT destination.id AS id,
                                    destination.name AS name,
                                    chapter.id AS chapterId
                    ORDER BY id
                    """)
                .bind(sceneId).to("sceneId")
                .fetchAs(SceneData.class)
                .mappedBy((typeSystem, sceneRecord) -> new SceneData(
                        sceneRecord.get("id").asInt(),
                        sceneRecord.get("name").asString(),
                        sceneRecord.get("chapterId").asInt()
                ))
                .all()
                .stream()
                .map(this::toSceneResponseDTO)
                .toList();
    }

    private SceneResponseDTO toSceneResponseDTO(SceneData sceneData) {
        return new SceneResponseDTO(
                sceneData.id().toString(),
                sceneData.name(),
                sceneData.chapterId().toString()
        );
    }


    private ChoiceResponseDTO toChoiceResponseDTO(ChoiceData choiceData) {
        return new ChoiceResponseDTO(
                choiceData.id().toString(),
                choiceData.destinationSceneId().toString(),
                choiceData.sceneId().toString(),
                choiceData.description(),
                choiceData.consequence(),
                choiceData.targetId(),
                choiceData.valueInt(),
                choiceData.requirements()
        );
    }

    private record SceneData(
            Integer id,
            String name,
            Integer chapterId
    ) {}

    private record ChoiceData(
            Integer id,
            Integer destinationSceneId,
            Integer sceneId,
            String description,
            String consequence,
            Integer targetId,
            Integer valueInt,
            String requirements
    ) {}
}
