package dk.ek.gruppe2.chooseyourfate.service.mysql;

import dk.ek.gruppe2.chooseyourfate.dto.EquipmentResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateEquipmentRequestDTO;
import dk.ek.gruppe2.chooseyourfate.enums.ItemType;
import dk.ek.gruppe2.chooseyourfate.exception.ResourceNotFoundException;
import dk.ek.gruppe2.chooseyourfate.interfaces.EquipmentDataAccess;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Equipment;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Inventory;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Item;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.EquipmentRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.InventoryHasItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SqlEquipmentService implements EquipmentDataAccess {

    private final EquipmentRepository equipmentRepository;
    private final SqlItemService itemService;
    private final SqlInventoryService inventoryService;
    private final InventoryHasItemRepository inventoryHasItemRepository;

    public SqlEquipmentService(EquipmentRepository equipmentRepository, SqlItemService itemService, SqlInventoryService inventoryService, InventoryHasItemRepository inventoryHasItemRepository) {
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


    public EquipmentResponseDTO updateEquipment(Equipment equipment) {
        Equipment updatedEquipment = equipmentRepository.save(equipment);
        return toDto(updatedEquipment);
    }

    public Equipment getEquipmentEntity(Integer characterId) {
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
                equipment.getHead() == null ? null : itemService.toDto(equipment.getHead()),
                equipment.getChest() == null ? null : itemService.toDto(equipment.getChest()),
                equipment.getLegs() == null ? null : itemService.toDto(equipment.getLegs())
        );
    }

    private ItemType resolveItemType(Equipment equipment, Integer itemId) {
        if(equipment.getHead().getId().equals(itemId)) {
            return equipment.getHead().getType();
        }
        if(equipment.getChest().getId().equals(itemId)) {
            return equipment.getChest().getType();
        }
        if(equipment.getLegs().getId().equals(itemId)) {
            return equipment.getLegs().getType();
        }
        else {
            return null;
        }
    }
}
