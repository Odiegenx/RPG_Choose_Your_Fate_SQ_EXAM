package dk.ek.gruppe2.chooseyourfate.service.mysql;

import dk.ek.gruppe2.chooseyourfate.dto.InventoryResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.ItemResponseDTO;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Inventory;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Item;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.CharacterAvatarRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.InventoryHasItemRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.InventoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InventoryService {

    InventoryRepository inventoryRepository;
    InventoryHasItemRepository inventoryHasItemRepository;

    public InventoryService(InventoryRepository inventoryRepository, InventoryHasItemRepository inventoryHasItemRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryHasItemRepository = inventoryHasItemRepository;
    }

    public InventoryResponseDTO getInventoryData(Integer id) {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<Item> items = inventoryHasItemRepository.findByInventory_Id(id).stream().map((inventoryHasItem -> inventoryHasItem.getItem())).toList();
        List<ItemResponseDTO> itemResponseDTOS = items.stream().map((item -> new ItemResponseDTO(item))).toList();
        InventoryResponseDTO response = new InventoryResponseDTO(inventory.getCharacter().getName(), itemResponseDTOS);
        return response;
    }
}
