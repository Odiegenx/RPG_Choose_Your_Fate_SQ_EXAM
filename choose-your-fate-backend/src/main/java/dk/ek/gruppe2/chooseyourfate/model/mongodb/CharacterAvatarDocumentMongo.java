package dk.ek.gruppe2.chooseyourfate.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document(collection = "characterAvatars")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CharacterAvatarDocumentMongo {

    @Id
    private String id;
    private String name;
    private String accountId;
    private String raceDetailId;
    private String chapterId;
    private String sceneId;
    private String flag;   // was JSON column
    private CharacterDetailsMongo details;   // was character_details table
    private EquipmentMongo equipment;        // was equipment table
    private InventoryMongo inventory;        // was inventory + inventory_has_item
    private CharacterPathMongo path;
    private List<CharacterQuestMongo> characterQuests = new ArrayList<>();

}
