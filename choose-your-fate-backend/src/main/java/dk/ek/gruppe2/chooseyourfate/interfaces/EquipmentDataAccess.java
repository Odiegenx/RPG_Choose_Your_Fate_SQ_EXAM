package dk.ek.gruppe2.chooseyourfate.interfaces;

import dk.ek.gruppe2.chooseyourfate.dto.EquipmentResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateEquipmentRequestDTO;

import java.util.List;

public interface EquipmentDataAccess {

    List<EquipmentResponseDTO> getAllEquipment();

    EquipmentResponseDTO getEquipmentByCharacterId(Integer characterId);
}
