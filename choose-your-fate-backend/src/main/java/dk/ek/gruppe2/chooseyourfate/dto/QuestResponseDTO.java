package dk.ek.gruppe2.chooseyourfate.dto;

import dk.ek.gruppe2.chooseyourfate.model.mysql.Quest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestResponseDTO {

    private Integer scene_id;
    private String description;

    public QuestResponseDTO(Quest quest) {
        this.scene_id = quest.getScene().getId();
        this.description = quest.getDescription();
    }

}
