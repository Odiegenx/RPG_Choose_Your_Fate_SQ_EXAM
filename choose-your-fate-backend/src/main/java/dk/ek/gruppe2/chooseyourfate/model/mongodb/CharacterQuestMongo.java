package dk.ek.gruppe2.chooseyourfate.model.mongodb;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterQuestMongo {
    private String questId;
    private boolean status;
}