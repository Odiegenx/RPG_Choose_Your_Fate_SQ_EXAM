package dk.ek.gruppe2.chooseyourfate.service.mysql;

import dk.ek.gruppe2.chooseyourfate.dto.EquipmentResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.ItemResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateEquipmentRequestDTO;
import dk.ek.gruppe2.chooseyourfate.exception.ResourceNotFoundException;
import dk.ek.gruppe2.chooseyourfate.interfaces.EquipmentDataAccess;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Equipment;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Inventory;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Item;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.EquipmentRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.InventoryHasItemRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SqlEquipmentService implements EquipmentDataAccess {

    private final EquipmentRepository equipmentRepository;
    private final ItemService itemService;
    private final InventoryService inventoryService;
    private final InventoryHasItemRepository inventoryHasItemRepository;

    public SqlEquipmentService(EquipmentRepository equipmentRepository, ItemService itemService, InventoryService inventoryService, InventoryHasItemRepository inventoryHasItemRepository) {
        this.equipmentRepository = equipmentRepository;
        this.itemService = itemService;
        this.inventoryService = inventoryService;
        this.inventoryHasItemRepository = inventoryHasItemRepository;
    }

    @Override
    public List<EquipmentResponseDTO> getAllEquipment() {
        return equipmentRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public EquipmentResponseDTO getEquipmentByCharacterId(Integer characterId) {
        return toDto(getEquipmentEntity(characterId));
    }

    public EquipmentResponseDTO updateEquipment(Integer characterId, UpdateEquipmentRequestDTO request) {
        if (inventoryHasItemRepository.existsByInventoryIdAndItemId(characterId, request.getItemId())) {
            if (request.getItemId() != null) {
                return equipItem(characterId, request);
            } else {
                return unequipItem(characterId, request);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found in inventory");
    }

    private EquipmentResponseDTO equipItem(Integer characterId, UpdateEquipmentRequestDTO request) {
        Equipment equipment = getEquipmentEntity(characterId);
        Inventory inventory = inventoryService.getInventoryEntityByCharacterId(characterId);
        switch (request.getType()) {
            case "armor_head" -> {
                equipment.setHead(resolveItem(request.getItemId()));
            }
            case "armor_chest" -> {
                equipment.setChest(resolveItem(request.getItemId()));
            }
            case "armor_legs" -> {
                equipment.setLegs(resolveItem(request.getItemId()));
            }
        }
        inventoryService.removeItem(inventory.getId(), request.getItemId());
        return toDto(equipmentRepository.save(equipment));
    }

    private EquipmentResponseDTO unequipItem(Integer characterId, UpdateEquipmentRequestDTO request) {
        Equipment equipment = getEquipmentEntity(characterId);
        Inventory inventory = inventoryService.getInventoryEntityByCharacterId(characterId);
        Integer itemId = null;
        switch (request.getType()) {
            case "armor_head" -> {
                itemId = equipment.getHead().getId();
                equipment.setHead(null);
            }
            case "armor_chest" -> {
                itemId = equipment.getChest().getId();
                equipment.setChest(null);
            }
            case "armor_legs" -> {
                itemId = equipment.getLegs().getId();
                equipment.setLegs(null);
            }
        }
        inventoryService.addItemToInventory(inventory.getId(), itemId);
        return toDto(equipmentRepository.save(equipment));
    }

    private Equipment getEquipmentEntity(Integer characterId) {
        return equipmentRepository.findById(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found for character id: " + characterId));
    }

    private Item resolveItem(Integer itemId) {
        if (itemId == null) {
            return null;
        }

        return itemService.getItemEntity(itemId);
    }

    private EquipmentResponseDTO toDto(Equipment equipment) {
        return new EquipmentResponseDTO(
                equipment.getCharacterId(),
                equipment.getHead() == null ? null : equipment.getHead().getId(),
                equipment.getChest() == null ? null : equipment.getChest().getId(),
                equipment.getLegs() == null ? null : equipment.getLegs().getId()
        );
    }
}
