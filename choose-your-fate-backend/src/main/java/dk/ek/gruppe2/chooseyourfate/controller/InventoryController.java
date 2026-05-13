package dk.ek.gruppe2.chooseyourfate.controller;

import dk.ek.gruppe2.chooseyourfate.dto.EquipmentResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.InventoryResponseDTO;
import dk.ek.gruppe2.chooseyourfate.service.mysql.SqlInventoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/choose-your-fate/inventories")
public class InventoryController {

    SqlInventoryService inventoryService;

    public InventoryController(SqlInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{characterId}")
    public InventoryResponseDTO getInventoryByCharacterId(@PathVariable Integer characterId) {
        return inventoryService.getInventoryByCharacterId(characterId);
    }

    @PostMapping("/{inventoryId}/items/{itemId}")
    public void addItemToInventory(@PathVariable Integer inventoryId, @PathVariable Integer itemId) {
        inventoryService.addItemToInventory(inventoryId, itemId);
    }

    @PostMapping("/{inventoryId}/items/{itemId}/use")
    public void useItem(@PathVariable Integer inventoryId, @PathVariable Integer itemId) {
        inventoryService.useItem(inventoryId, itemId);
    }

}
