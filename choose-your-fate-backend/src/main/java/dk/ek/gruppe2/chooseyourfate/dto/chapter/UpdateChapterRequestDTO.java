package dk.ek.gruppe2.chooseyourfate.dto.chapter;

import java.util.List;

import dk.ek.gruppe2.chooseyourfate.model.mysql.CharacterAvatar;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Scene;

public class UpdateChapterRequestDTO {
    
    private String name;
    private List<Scene> scenes;
    private List<CharacterAvatar> characters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Scene> getScenes() { return scenes; }

    public void setScenes(List<Scene> scenes) {
        this.scenes = scenes;
    }

    public List<CharacterAvatar> getCharacters() { return characters; }

    public void setCharacters(List<CharacterAvatar> characters) {
        this.characters = characters;
    }
}
