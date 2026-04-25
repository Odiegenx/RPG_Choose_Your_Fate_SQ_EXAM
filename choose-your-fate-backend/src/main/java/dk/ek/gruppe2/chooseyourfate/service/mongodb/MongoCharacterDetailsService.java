package dk.ek.gruppe2.chooseyourfate.service.mongodb;

import dk.ek.gruppe2.chooseyourfate.dto.CharacterDetailsResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateCharacterDetailsRequestDTO;
import dk.ek.gruppe2.chooseyourfate.interfaces.CharacterDetailsDataAccess;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoCharacterDetailsService implements CharacterDetailsDataAccess {

    private static final String MESSAGE = "MongoDB character details functionality is not implemented yet";

    @Override
    public List<CharacterDetailsResponseDTO> getAllCharacterDetails() {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public CharacterDetailsResponseDTO getCharacterDetailsByCharacterId(Integer characterId) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public CharacterDetailsResponseDTO updateCharacterDetails(Integer characterId, UpdateCharacterDetailsRequestDTO request) {
        throw new UnsupportedOperationException(MESSAGE);
    }
}
