package dk.ek.gruppe2.chooseyourfate.dto.scene;

import dk.ek.gruppe2.chooseyourfate.model.mysql.Scene;

public class SceneResponseDTO {
    private Integer id;
    private Integer chapterId;
    private String name;

    public SceneResponseDTO() {
    }

    public SceneResponseDTO(Integer id, String name, Integer chapterId) {
        this.id = id;
        this.name = name;
        this.chapterId = chapterId;
    }

    public SceneResponseDTO(Scene scene) {
        this.id = scene.getId();
        this.name = scene.getName();
        this.chapterId = scene.getChapter().getId();
    }

    public SceneResponseDTO toDTO(Scene scene) {
        return new SceneResponseDTO(
                scene.getId(),
                scene.getName(),
                scene.getChapter().getId()
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getChapterId() {
        return chapterId;
    }

    public void setChapterId(Integer chapterId) {
        this.chapterId = chapterId;
    }
}
