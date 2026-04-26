package dk.ek.gruppe2.chooseyourfate.interfaces;

import dk.ek.gruppe2.chooseyourfate.dto.CharacterPathResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateCharacterPathRequestDTO;

import java.util.List;

public interface CharacterPathDataAccess {

    List<CharacterPathResponseDTO> getAllCharacterPaths();

    CharacterPathResponseDTO getCharacterPathByCharacterId(Integer characterId);

    CharacterPathResponseDTO updateCharacterPath(Integer characterId, UpdateCharacterPathRequestDTO request);
}
