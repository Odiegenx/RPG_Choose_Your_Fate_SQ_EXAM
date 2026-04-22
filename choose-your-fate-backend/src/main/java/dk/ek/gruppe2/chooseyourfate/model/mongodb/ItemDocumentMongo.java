package dk.ek.gruppe2.chooseyourfate.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDocumentMongo {

    @Id
    private String id;
    private String name;
    private String description;
    private String type;
}
