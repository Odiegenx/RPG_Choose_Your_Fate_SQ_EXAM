package dk.ek.gruppe2.chooseyourfate.service.neo4j;

import dk.ek.gruppe2.chooseyourfate.dto.LoadoutResponseDTO;
import dk.ek.gruppe2.chooseyourfate.interfaces.LoadoutDataAccess;
import dk.ek.gruppe2.chooseyourfate.repository.neo4j.Neo4jLoadoutRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class Neo4jLoadoutService implements LoadoutDataAccess {

    private final Neo4jLoadoutRepository loadoutRepository;

    public Neo4jLoadoutService(Neo4jLoadoutRepository loadoutRepository) {
        this.loadoutRepository = loadoutRepository;
    }

    @Override
    public LoadoutResponseDTO getLoadoutByCharacterId(Integer characterId) {
        return loadoutRepository.getLoadoutByCharacterId(characterId);
    }

    @Override
    @Transactional
    public LoadoutResponseDTO equipItem(Integer characterId, Integer itemId) {
        // Finder inventory og itemet der skal equipes.
        // Tjekker at itemet faktisk findes i inventory, og finder eventuelt det item der allerede sidder i slotten.
        // Det nye item fjernes fra inventory, det gamle lægges tilbage hvis nødvendigt, og equipped-relationen opdateres.
        // Til sidst returneres den opdaterede loadout.
        Integer inventoryId = loadoutRepository.findInventoryId(characterId);
        Neo4jLoadoutRepository.ItemData itemToEquip = loadoutRepository.findItemById(itemId);
        Integer amountInInventory = loadoutRepository.findInventoryItemAmount(inventoryId, itemToEquip.itemId());
        if (amountInInventory < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Item is not present in inventory"
            );
        }
        loadoutRepository.findEquippedItem(characterId, itemToEquip.type())
                .ifPresent(currentlyEquippedItem ->
                        loadoutRepository.incrementInventoryItem(inventoryId, currentlyEquippedItem.itemId())
                );
        loadoutRepository.decrementInventoryItem(inventoryId, itemToEquip.itemId());
        loadoutRepository.setEquippedItem(characterId, itemToEquip.itemId(), itemToEquip.type());
        return loadoutRepository.getLoadoutByCharacterId(characterId);
    }
    @Override
    @Transactional
    public LoadoutResponseDTO unequipItem(Integer characterId, Integer itemId) {
        // Finder characterens inventory og det item der skal unequippes.
        // Bruger itemets type til at finde det item der aktuelt sidder i den forventede slot.
        // Tjekker derefter at det konkrete item faktisk er equipped.
        // Hvis det passer, lægges itemet tilbage i inventory og equipped-relationen fjernes.
        // Til sidst returneres den opdaterede loadout.
        Integer inventoryId = loadoutRepository.findInventoryId(characterId);
        Neo4jLoadoutRepository.ItemData itemToUnequip = loadoutRepository.findItemById(itemId);
        Neo4jLoadoutRepository.ItemData currentlyEquippedItem = loadoutRepository.findEquippedItem(characterId, itemToUnequip.type())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Item is not currently equipped"
                ));
        if (!currentlyEquippedItem.itemId().equals(itemToUnequip.itemId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Item is not currently equipped"
            );
        }
        loadoutRepository.incrementInventoryItem(inventoryId, itemToUnequip.itemId());
        loadoutRepository.removeEquippedItem(characterId, itemToUnequip.type());
        return loadoutRepository.getLoadoutByCharacterId(characterId);
    }
}
