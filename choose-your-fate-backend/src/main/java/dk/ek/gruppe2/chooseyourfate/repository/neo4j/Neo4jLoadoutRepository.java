package dk.ek.gruppe2.chooseyourfate.repository.neo4j;

import dk.ek.gruppe2.chooseyourfate.dto.LoadoutResponseDTO;
import dk.ek.gruppe2.chooseyourfate.enums.ItemType;

import java.util.Optional;

public interface Neo4jLoadoutRepository {

    LoadoutResponseDTO getLoadoutByCharacterId(Integer characterId);

    Integer findInventoryId(Integer characterId);

    ItemData findItemById(Integer itemId);

    Optional<ItemData> findEquippedItem(Integer characterId, ItemType itemType);

    Integer findInventoryItemAmount(Integer inventoryId, Integer itemId);

    void decrementInventoryItem(Integer inventoryId, Integer itemId);

    void incrementInventoryItem(Integer inventoryId, Integer itemId);

    void setEquippedItem(Integer characterId, Integer itemId, ItemType itemType);

    void removeEquippedItem(Integer characterId, ItemType itemType);

    record ItemData(
            Integer itemId,
            String name,
            String description,
            ItemType type
    ) {
    }
}
