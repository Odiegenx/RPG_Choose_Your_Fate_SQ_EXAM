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

    @GetMapping("/{id}")
    public InventoryResponseDTO getInventoryData(@PathVariable Integer id) {
        return inventoryService.getInventoryData(id);
    }

    @PostMapping("/{id}/items/{itemId}")
    public void addItemToInventory(@PathVariable Integer id, @PathVariable Integer itemId) {
        inventoryService.addItemToInventory(id, itemId);
    }

    @PostMapping("/{id}/items/{itemId}/use")
    public void useItem(@PathVariable Integer id, @PathVariable Integer itemId) {
        inventoryService.useItem(id, itemId);
    }
}
