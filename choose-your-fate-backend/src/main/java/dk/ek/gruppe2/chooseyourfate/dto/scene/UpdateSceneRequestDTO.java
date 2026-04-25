package dk.ek.gruppe2.chooseyourfate.dto.scene;

public class UpdateSceneRequestDTO {
    private String name;
    private Integer chapterId;

    public Integer getChapterId() {
        return chapterId;
    }

    public void setChapterId(Integer chapterId) {
        this.chapterId = chapterId;
    }

    public String getName() {
        return name;
    }
}
