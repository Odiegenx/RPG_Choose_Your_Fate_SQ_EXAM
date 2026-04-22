package dk.ek.gruppe2.chooseyourfate.model.mongodb;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceMadeMongo {

    private String description;
    private String consequence;
    private short storyWeight;
    private String destinationSceneId;
}
