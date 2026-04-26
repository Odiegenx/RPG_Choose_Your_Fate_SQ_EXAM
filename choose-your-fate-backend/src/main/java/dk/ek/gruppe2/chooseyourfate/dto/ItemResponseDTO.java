package dk.ek.gruppe2.chooseyourfate.dto;

import dk.ek.gruppe2.chooseyourfate.model.mysql.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDTO {

    private String name;
    private String description;
    private String type;

    public ItemResponseDTO(Item item) {
        this.name = item.getName();
        this.description = item.getDescription();
        this.type = item.getType();
    }

}
