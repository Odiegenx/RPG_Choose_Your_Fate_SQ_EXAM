package dk.ek.gruppe2.chooseyourfate.dto.scene;

import dk.ek.gruppe2.chooseyourfate.model.mysql.Scene;

public class SceneSummaryDTO {
    private Integer id;
    private Integer chapterId;
    private String name;

    public SceneSummaryDTO() {
    }

    public SceneSummaryDTO(Scene scene) {
        this.id = scene.getId();
        this.name = scene.getName();
        this.chapterId = scene.getChapter().getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChapterId() {
        return chapterId;
    }

    public void setChapterId(Integer chapterId) {
        this.chapterId = chapterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
