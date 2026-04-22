package dk.ek.gruppe2.chooseyourfate.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterPathMongo {
    private String summary;
    private List<ChoiceMadeMongo> choicesMade = new ArrayList<>();
}
