package dk.ek.gruppe2.chooseyourfate.service.neo4j;

import dk.ek.gruppe2.chooseyourfate.dto.CharacterPathResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateCharacterPathRequestDTO;
import dk.ek.gruppe2.chooseyourfate.interfaces.CharacterPathDataAccess;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Neo4jCharacterPathService implements CharacterPathDataAccess {

    private static final String MESSAGE = "Neo4j character path functionality is not implemented yet";

    @Override
    public List<CharacterPathResponseDTO> getAllCharacterPaths() {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public CharacterPathResponseDTO getCharacterPathByCharacterId(Integer characterId) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public CharacterPathResponseDTO updateCharacterPath(Integer characterId, UpdateCharacterPathRequestDTO request) {
        throw new UnsupportedOperationException(MESSAGE);
    }
}
