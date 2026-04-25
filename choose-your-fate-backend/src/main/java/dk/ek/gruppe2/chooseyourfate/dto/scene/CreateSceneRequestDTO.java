package dk.ek.gruppe2.chooseyourfate.dto.scene;

import dk.ek.gruppe2.chooseyourfate.model.mysql.Chapter;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Scene;

public class CreateSceneRequestDTO {
    private String name;
    private Integer chapterId;

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

    public Scene toEntity(Chapter chapter) {
        Scene scene = new Scene();
        scene.setName(this.name);
        scene.setChapter(chapter);
        return scene;
    }
}
