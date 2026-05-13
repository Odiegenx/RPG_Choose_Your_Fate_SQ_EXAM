package dk.ek.gruppe2.chooseyourfate.service.neo4j;

import dk.ek.gruppe2.chooseyourfate.dto.EquipmentResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateEquipmentRequestDTO;
import dk.ek.gruppe2.chooseyourfate.interfaces.EquipmentDataAccess;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Neo4jEquipmentService implements EquipmentDataAccess {

    private static final String MESSAGE = "Neo4j equipment functionality is not implemented yet";

    @Override
    public List<EquipmentResponseDTO> getAllEquipment() {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public EquipmentResponseDTO getEquipmentByCharacterId(Integer characterId) {
        throw new UnsupportedOperationException(MESSAGE);
    }

}
