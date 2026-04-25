package dk.ek.gruppe2.chooseyourfate.interfaces;

import dk.ek.gruppe2.chooseyourfate.dto.CharacterDetailsResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateCharacterDetailsRequestDTO;

import java.util.List;

public interface CharacterDetailsDataAccess {

    List<CharacterDetailsResponseDTO> getAllCharacterDetails();

    CharacterDetailsResponseDTO getCharacterDetailsByCharacterId(Integer characterId);

    CharacterDetailsResponseDTO updateCharacterDetails(Integer characterId, UpdateCharacterDetailsRequestDTO request);
}
