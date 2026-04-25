package dk.ek.gruppe2.chooseyourfate.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDetailsMongo {
    private Integer intelligence;
    private Integer charisma;
    private Integer fashion;

}