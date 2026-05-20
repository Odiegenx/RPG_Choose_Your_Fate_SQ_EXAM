package dk.ek.gruppe2.chooseyourfate.dto.scene;

import dk.ek.gruppe2.chooseyourfate.dto.choice.ChoiceResponseDTO;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Scene;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Choice;
import java.util.Comparator;
import java.util.List;

public class SceneResponseDTO {
    private String id;
    private String chapterId;
    private String name;

    public SceneResponseDTO() {
    }

    public SceneResponseDTO(String id, String name, String chapterId) {
        this.id = id;
        this.name = name;
        this.chapterId = chapterId;
    }

    public SceneResponseDTO(Scene scene) {
        this.id = scene.getId().toString();
        this.name = scene.getName();
        this.chapterId = scene.getChapter().getId().toString();
    }

    public SceneResponseDTO toDTO(Scene scene) {
        return new SceneResponseDTO(
                scene.getId().toString(),
                scene.getName(),
                scene.getChapter().getId().toString()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

}