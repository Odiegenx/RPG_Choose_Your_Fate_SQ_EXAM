package dk.ek.gruppe2.chooseyourfate.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "scenes")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SceneDocumentMongo {

    @Id
    private String id;
    private String chapterId;
    private String name;
    private List<String> questIds = new ArrayList<>();
    private List<ChoiceMongo> choices = new ArrayList<>();
    private List<String> npcIds = new ArrayList<>();

}
