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
public class ItemRequestDTO {

    private String name;
    private String description;
    private String type;

    public Item getItemEntity() {
        Item item = new Item();
        item.setName(getName());
        item.setDescription(getDescription());
        item.setType(getType());
        return item;
    }

}
