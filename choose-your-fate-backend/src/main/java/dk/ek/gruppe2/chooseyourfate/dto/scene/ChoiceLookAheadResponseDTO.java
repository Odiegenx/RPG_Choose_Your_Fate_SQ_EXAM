package dk.ek.gruppe2.chooseyourfate.dto.scene;

import dk.ek.gruppe2.chooseyourfate.model.mysql.Choice;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Scene;

public class ChoiceLookAheadResponseDTO {
    private Integer id;
    private Integer sceneId;
    private Integer destinationSceneId;
    private SceneSummaryDTO destinationScene;
    private String description;

    public ChoiceLookAheadResponseDTO() {
    }

    public ChoiceLookAheadResponseDTO(Choice choice) {
        Scene destination = choice.getDestinationScene();

        this.id = choice.getId();
        this.sceneId = choice.getScene().getId();
        this.destinationSceneId = destination == null ? null : destination.getId();
        this.destinationScene = destination == null ? null : new SceneSummaryDTO(destination);
        this.description = choice.getDescription();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSceneId() {
        return sceneId;
    }

    public void setSceneId(Integer sceneId) {
        this.sceneId = sceneId;
    }

    public Integer getDestinationSceneId() {
        return destinationSceneId;
    }

    public void setDestinationSceneId(Integer destinationSceneId) {
        this.destinationSceneId = destinationSceneId;
    }

    public SceneSummaryDTO getDestinationScene() {
        return destinationScene;
    }

    public void setDestinationScene(SceneSummaryDTO destinationScene) {
        this.destinationScene = destinationScene;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
