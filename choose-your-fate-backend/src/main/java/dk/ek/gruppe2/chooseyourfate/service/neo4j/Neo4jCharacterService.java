package dk.ek.gruppe2.chooseyourfate.service.neo4j;

import dk.ek.gruppe2.chooseyourfate.dto.CharacterResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.CreateCharacterRequestDTO;
import dk.ek.gruppe2.chooseyourfate.interfaces.CharacterDataAccess;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Neo4jCharacterService implements CharacterDataAccess<Integer> {

    private static final String MESSAGE =
            "Neo4j character functionality is planned to create a minimal subgraph and is not implemented yet";

    @Override
    public List<CharacterResponseDTO> getAllCharacters() {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public CharacterResponseDTO getCharacterById(Integer id) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public CharacterResponseDTO createCharacter(CreateCharacterRequestDTO request) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public void deleteCharacter(Integer id) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public List<CharacterResponseDTO> getCharactersByAccountId(Integer id) {
        throw new UnsupportedOperationException(MESSAGE);
    }
}
