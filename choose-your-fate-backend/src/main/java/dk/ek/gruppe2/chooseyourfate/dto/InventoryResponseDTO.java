package dk.ek.gruppe2.chooseyourfate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDTO {

    private String characterName;
    private List<ItemResponseDTO> items;



}
