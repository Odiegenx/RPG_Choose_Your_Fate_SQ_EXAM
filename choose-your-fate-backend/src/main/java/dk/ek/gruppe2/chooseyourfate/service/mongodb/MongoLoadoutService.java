package dk.ek.gruppe2.chooseyourfate.service.mongodb;

import dk.ek.gruppe2.chooseyourfate.dto.InventoryHasItemResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.ItemResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.LoadoutResponseDTO;
import dk.ek.gruppe2.chooseyourfate.enums.ItemType;
import dk.ek.gruppe2.chooseyourfate.interfaces.LoadoutDataAccess;
import dk.ek.gruppe2.chooseyourfate.model.mongodb.CharacterAvatarDocumentMongo;
import dk.ek.gruppe2.chooseyourfate.model.mongodb.EquipmentMongo;
import dk.ek.gruppe2.chooseyourfate.model.mongodb.InventoryEntryMongo;
import dk.ek.gruppe2.chooseyourfate.model.mongodb.InventoryMongo;
import dk.ek.gruppe2.chooseyourfate.model.mongodb.ItemDocumentMongo;
import dk.ek.gruppe2.chooseyourfate.repository.mongodb.CharacterAvatarRepositoryMongo;
import dk.ek.gruppe2.chooseyourfate.repository.mongodb.ItemRepositoryMongo;
import dk.ek.gruppe2.chooseyourfate.service.migration.IdMappingService;
import dk.ek.gruppe2.chooseyourfate.service.migration.mongodb.CollectionNames;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MongoLoadoutService implements LoadoutDataAccess {

    private final CharacterAvatarRepositoryMongo characterRepository;
    private final ItemRepositoryMongo itemRepository;
    private final IdMappingService idMappingService;

    public MongoLoadoutService(
            CharacterAvatarRepositoryMongo characterRepository,
            ItemRepositoryMongo itemRepository,
            IdMappingService idMappingService
    ) {
        this.characterRepository = characterRepository;
        this.itemRepository = itemRepository;
        this.idMappingService = idMappingService;
    }

    @Override
    public LoadoutResponseDTO getLoadoutByCharacterId(Integer characterId) {
        return toDto(getCharacter(characterId));
    }

    @Override
    public LoadoutResponseDTO unequipItem(Integer characterId, Integer itemId) {
        CharacterAvatarDocumentMongo character = getCharacter(characterId);
        ItemDocumentMongo item = getItem(itemId);
        ItemType itemType = getEquipmentType(item);
        EquipmentMongo equipment = getOrCreateEquipment(character);

        String equippedItemId = getEquippedItemId(equipment, itemType);
        if (!Objects.equals(equippedItemId, item.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item is not equipped");
        }

        setEquippedItemId(equipment, itemType, null);
        incrementInventoryItem(getOrCreateInventory(character), item.getId());
        characterRepository.save(character);

        return toDto(character);
    }

    @Override
    public LoadoutResponseDTO equipItem(Integer characterId, Integer itemId) {
        CharacterAvatarDocumentMongo character = getCharacter(characterId);
        ItemDocumentMongo item = getItem(itemId);
        ItemType itemType = getEquipmentType(item);
        EquipmentMongo equipment = getOrCreateEquipment(character);
        InventoryMongo inventory = getOrCreateInventory(character);

        InventoryEntryMongo inventoryEntry = findInventoryEntry(inventory, item.getId());
        if (inventoryEntry == null || inventoryEntry.getAmount() == null || inventoryEntry.getAmount() < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item is not in inventory");
        }

        String currentlyEquippedItemId = getEquippedItemId(equipment, itemType);
        decrementInventoryItem(inventory, inventoryEntry);
        if (currentlyEquippedItemId != null) {
            incrementInventoryItem(inventory, currentlyEquippedItemId);
        }
        setEquippedItemId(equipment, itemType, item.getId());
        characterRepository.save(character);

        return toDto(character);
    }

    private CharacterAvatarDocumentMongo getCharacter(Integer characterId) {
        String mongoCharacterId = idMappingService.get(CollectionNames.CHARACTERS, characterId);
        if (mongoCharacterId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mongo character mapping not found");
        }

        return characterRepository.findById(mongoCharacterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Character not found"));
    }

    private ItemDocumentMongo getItem(Integer itemId) {
        String mongoItemId = idMappingService.get(CollectionNames.ITEMS, itemId);
        if (mongoItemId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mongo item mapping not found");
        }

        return itemRepository.findById(mongoItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
    }

    private EquipmentMongo getOrCreateEquipment(CharacterAvatarDocumentMongo character) {
        if (character.getEquipment() == null) {
            character.setEquipment(EquipmentMongo.builder().build());
        }
        return character.getEquipment();
    }

    private InventoryMongo getOrCreateInventory(CharacterAvatarDocumentMongo character) {
        if (character.getInventory() == null) {
            character.setInventory(InventoryMongo.builder()
                    .inventoryEntries(new ArrayList<>())
                    .build());
        }
        if (character.getInventory().getInventoryEntries() == null) {
            character.getInventory().setInventoryEntries(new ArrayList<>());
        }
        return character.getInventory();
    }

    private ItemType getEquipmentType(ItemDocumentMongo item) {
        ItemType itemType = item.getType();
        return switch (itemType) {
            case ARMOR_HEAD, ARMOR_CHEST, ARMOR_LEGS -> itemType;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item type is not supported for equipment");
        };
    }

    private String getEquippedItemId(EquipmentMongo equipment, ItemType itemType) {
        return switch (itemType) {
            case ARMOR_HEAD -> equipment.getHeadItemId();
            case ARMOR_CHEST -> equipment.getChestItemId();
            case ARMOR_LEGS -> equipment.getLegsItemId();
            default -> null;
        };
    }

    private void setEquippedItemId(EquipmentMongo equipment, ItemType itemType, String itemId) {
        switch (itemType) {
            case ARMOR_HEAD -> equipment.setHeadItemId(itemId);
            case ARMOR_CHEST -> equipment.setChestItemId(itemId);
            case ARMOR_LEGS -> equipment.setLegsItemId(itemId);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item type is not supported for equipment");
        }
    }

    private InventoryEntryMongo findInventoryEntry(InventoryMongo inventory, String itemId) {
        return inventory.getInventoryEntries()
                .stream()
                .filter(entry -> Objects.equals(entry.getItemId(), itemId))
                .findFirst()
                .orElse(null);
    }

    private void incrementInventoryItem(InventoryMongo inventory, String itemId) {
        InventoryEntryMongo entry = findInventoryEntry(inventory, itemId);
        if (entry == null) {
            inventory.getInventoryEntries().add(InventoryEntryMongo.builder()
                    .itemId(itemId)
                    .amount(1)
                    .build());
            return;
        }

        entry.setAmount(entry.getAmount() == null ? 1 : entry.getAmount() + 1);
    }

    private void decrementInventoryItem(InventoryMongo inventory, InventoryEntryMongo entry) {
        if (entry.getAmount() == null || entry.getAmount() <= 1) {
            inventory.getInventoryEntries().remove(entry);
            return;
        }

        entry.setAmount(entry.getAmount() - 1);
    }

    private LoadoutResponseDTO toDto(CharacterAvatarDocumentMongo character) {
        EquipmentMongo equipment = getOrCreateEquipment(character);
        InventoryMongo inventory = getOrCreateInventory(character);

        List<ItemResponseDTO> equippedItems = new ArrayList<>();
        equippedItems.add(toItemDto(equipment.getHeadItemId()));
        equippedItems.add(toItemDto(equipment.getChestItemId()));
        equippedItems.add(toItemDto(equipment.getLegsItemId()));

        List<InventoryHasItemResponseDTO> inventoryItems = inventory.getInventoryEntries()
                .stream()
                .map(entry -> new InventoryHasItemResponseDTO(entry.getAmount(), toItemDto(entry.getItemId())))
                .toList();

        return new LoadoutResponseDTO(null, equippedItems, inventoryItems);
    }

    private ItemResponseDTO toItemDto(String mongoItemId) {
        if (mongoItemId == null) {
            return null;
        }

        ItemDocumentMongo item = itemRepository.findById(mongoItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        return new ItemResponseDTO(
                null,
                item.getName(),
                item.getDescription(),
                item.getType()
        );
    }
}
