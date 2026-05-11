package dk.ek.gruppe2.chooseyourfate.service.mysql;

import dk.ek.gruppe2.chooseyourfate.dto.InventoryResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.ItemResponseDTO;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Inventory;
import dk.ek.gruppe2.chooseyourfate.model.mysql.InventoryHasItem;
import dk.ek.gruppe2.chooseyourfate.model.mysql.InventoryHasItemId;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Item;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.CharacterAvatarRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.InventoryHasItemRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.InventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InventoryService {

    private final ItemService itemService;
    private final InventoryRepository inventoryRepository;
    private final InventoryHasItemRepository inventoryHasItemRepository;

    public InventoryService(InventoryRepository inventoryRepository, InventoryHasItemRepository inventoryHasItemRepository, ItemService itemService) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryHasItemRepository = inventoryHasItemRepository;
        this.itemService = itemService;
    }

    public InventoryResponseDTO getInventoryData(Integer inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<Item> items = inventoryHasItemRepository.findByInventory_Id(inventoryId).stream().map((inventoryHasItem -> inventoryHasItem.getItem())).toList();
        List<ItemResponseDTO> itemResponseDTOS = items.stream().map((item -> new ItemResponseDTO(item))).toList();
        InventoryResponseDTO response = new InventoryResponseDTO(inventory.getCharacter().getName(), itemResponseDTOS);
        return response;
    }

    public InventoryResponseDTO getInventoryByCharacterId(Integer characterId) {
        Inventory inventory = inventoryRepository.findByCharacter_Id(characterId);
        return toDTO(inventory);
    }

    public Inventory getInventoryEntityByCharacterId(Integer characterId) {
        Inventory inventory = inventoryRepository.findByCharacter_Id(characterId);
        return inventory;
    }

    @Transactional
    public void removeItem(Integer inventoryId, Integer itemId) {
        inventoryHasItemRepository.deleteByInventoryIdAndItemId(inventoryId, itemId);
    }

    public void addItemToInventory(Integer inventoryId, Integer itemId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Item item = itemService.getItemEntity(itemId);  // Changed this line
        inventoryHasItemRepository.save(new InventoryHasItem(item, inventory, 1));
    }

    private InventoryResponseDTO toDTO(Inventory inventory) {
        InventoryResponseDTO dto = new InventoryResponseDTO();
        dto.setInventoryId(inventory.getId());
        return dto;
    }

    public void useItem(Integer inventoryId, Integer itemId) {
        InventoryHasItem inventoryHasItem = inventoryHasItemRepository
                .findById(new InventoryHasItemId(itemId, inventoryId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (inventoryHasItem.getAmount() > 1) {
            inventoryHasItem.setAmount(inventoryHasItem.getAmount() - 1);
            inventoryHasItemRepository.save(inventoryHasItem);
        } else {
            inventoryHasItemRepository.delete(inventoryHasItem);
        }
    }
}
