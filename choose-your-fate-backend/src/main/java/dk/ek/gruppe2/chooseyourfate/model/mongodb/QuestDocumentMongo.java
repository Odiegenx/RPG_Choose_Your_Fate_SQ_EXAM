package dk.ek.gruppe2.chooseyourfate.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "quests")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestDocumentMongo {

    @Id
    private String id;
    private String description;
    private String sceneId;
    private List<String> itemIds = new ArrayList<>();
}
