package dk.ek.gruppe2.chooseyourfate.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "race_details")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaceDetailsDocumentMongo {

    @Id
    private String id;
}
