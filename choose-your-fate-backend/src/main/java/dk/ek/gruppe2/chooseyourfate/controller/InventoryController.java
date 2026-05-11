package dk.ek.gruppe2.chooseyourfate.controller;

import dk.ek.gruppe2.chooseyourfate.dto.InventoryResponseDTO;
import dk.ek.gruppe2.chooseyourfate.service.mysql.InventoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/choose-your-fate/inventories")
public class InventoryController {

    InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{inventoryId}")
    public InventoryResponseDTO getInventoryData(@PathVariable Integer inventoryId) {
        return inventoryService.getInventoryData(inventoryId);
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
