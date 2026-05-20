package dk.ek.gruppe2.chooseyourfate.service.mongodb;

import dk.ek.gruppe2.chooseyourfate.dto.scene.CreateSceneRequestDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.SceneLookaheadResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.SceneResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.UpdateSceneRequestDTO;
import dk.ek.gruppe2.chooseyourfate.exception.ResourceNotFoundException;
import dk.ek.gruppe2.chooseyourfate.interfaces.SceneDataAccess;
import dk.ek.gruppe2.chooseyourfate.model.mongodb.SceneDocumentMongo;
import dk.ek.gruppe2.chooseyourfate.repository.mongodb.SceneRepositoryMongo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoSceneService implements SceneDataAccess {

    private final SceneRepositoryMongo sceneRepository;


    public MongoSceneService(SceneRepositoryMongo SceneRepositoryMongo) {
        this.sceneRepository = SceneRepositoryMongo;
    }

    @Override
    public List<SceneResponseDTO> getAllScenes() {
        List<SceneDocumentMongo> scenes = sceneRepository.findAll();
        List<SceneResponseDTO> responseDTOS = scenes.stream().map(scene -> new SceneResponseDTO(scene.getId(), scene.getName(), scene.getChapterId())).toList();
        return responseDTOS;
    }

    @Override
    public SceneResponseDTO getSceneById(String id) {
        return toDto(sceneRepository.getSceneById(id));
    }

    // In your service
    @Override
    public SceneResponseDTO createScene(CreateSceneRequestDTO request) {
        SceneDocumentMongo scene = new SceneDocumentMongo();
        scene.setName(request.getName());
        scene.setChapterId(request.getMongoChapterId());
        SceneDocumentMongo saved = sceneRepository.save(scene);
        return new SceneResponseDTO(saved.getId(), saved.getName(), saved.getChapterId());
    }

    @Override
    public SceneResponseDTO updateScene(String id, UpdateSceneRequestDTO request) {
        SceneDocumentMongo scene = sceneRepository.getSceneById(id);
        scene.setName(request.getName());
        scene.setChapterId(request.getMongoChapterId());
        SceneDocumentMongo saved = sceneRepository.save(scene);
        return new SceneResponseDTO(saved.getId(), saved.getName(), saved.getChapterId());
    }


    @Override
    public void deleteScene(String id) {
        sceneRepository.deleteById(id);
    }


    @Override
    public SceneLookaheadResponseDTO getSceneLookahead(String id) {
        SceneDocumentMongo scene = sceneRepository.findByIdWithNextScenes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scene not found for id: " + id));
        return new SceneLookaheadResponseDTO(scene);
    }

    public SceneResponseDTO toDto(SceneDocumentMongo scene) {
        SceneResponseDTO dto = new SceneResponseDTO(scene.getId(), scene.getName(), scene.getChapterId());
        return dto;
    }
}


