package dk.ek.gruppe2.chooseyourfate.controller;

import dk.ek.gruppe2.chooseyourfate.dto.EquipmentResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.InventoryResponseDTO;
import dk.ek.gruppe2.chooseyourfate.enums.DataSourceType;
import dk.ek.gruppe2.chooseyourfate.service.InventoryService;
import dk.ek.gruppe2.chooseyourfate.service.mysql.SqlInventoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/choose-your-fate/inventories")
public class InventoryController {

    InventoryService inventoryService;

    private static final String DATA_SOURCE_HEADER = "X-Data-Source";

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{characterId}")
    @PreAuthorize("hasRole('ADMIN') or @characterAuthorizationService.canAccessCharacter(#id, authentication)")
    public InventoryResponseDTO getInventoryByCharacterId(
            @RequestHeader(value = DATA_SOURCE_HEADER, required = true) DataSourceType dataSource,
            @PathVariable Integer characterId) {
        return inventoryService.getInventoryByCharacterId(dataSource, characterId);
    }

    @PostMapping("/{inventoryId}/items/{itemId}")
    @PreAuthorize("hasRole('ADMIN') or @characterAuthorizationService.canAccessCharacter(#id, authentication)")
    public InventoryResponseDTO addItemToInventory(
            @RequestHeader(value = DATA_SOURCE_HEADER, required = true) DataSourceType dataSource,
            @PathVariable Integer inventoryId, @PathVariable Integer itemId) {
        return inventoryService.addItemToInventory(dataSource, inventoryId, itemId);
    }

    @PostMapping("/{inventoryId}/items/{itemId}/use")
    @PreAuthorize("hasRole('ADMIN') or @characterAuthorizationService.canAccessCharacter(#id, authentication)")
    public void useItem(
            @RequestHeader(value = DATA_SOURCE_HEADER, required = true) DataSourceType dataSource,
            @PathVariable Integer inventoryId, @PathVariable Integer itemId) {
        inventoryService.useItem(dataSource, inventoryId, itemId);
    }

}
