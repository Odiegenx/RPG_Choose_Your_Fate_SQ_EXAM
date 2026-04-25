package dk.ek.gruppe2.chooseyourfate.model.mongodb;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceMongo {

    @Id
    private String id;
    private String description;
    private String sceneId;
    private String destinationId;
    private String consequence;
    private Integer targetId;
    private Integer value;
    private short storyWeight;
    private String requirements;
    private List<String> itemIds = new ArrayList<>();
}
