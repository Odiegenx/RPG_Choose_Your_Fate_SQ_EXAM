package dk.ek.gruppe2.chooseyourfate.interfaces;

import dk.ek.gruppe2.chooseyourfate.dto.InventoryResponseDTO;

public interface InventoryDataAccess {

    InventoryResponseDTO getInventoryByCharacterId(Integer inventoryId);

}
