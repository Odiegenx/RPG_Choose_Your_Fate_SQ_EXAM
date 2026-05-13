package dk.ek.gruppe2.chooseyourfate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryHasItemResponseDTO {

    private Integer inventoryId;
    private Integer amount;
    private ItemResponseDTO item;
}
