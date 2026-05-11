package dk.ek.gruppe2.chooseyourfate.service.mysql;

import dk.ek.gruppe2.chooseyourfate.dto.CharacterPathResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateCharacterPathRequestDTO;
import dk.ek.gruppe2.chooseyourfate.exception.ResourceNotFoundException;
import dk.ek.gruppe2.chooseyourfate.interfaces.CharacterPathDataAccess;
import dk.ek.gruppe2.chooseyourfate.model.mysql.CharacterPath;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.CharacterPathRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SqlCharacterPathService implements CharacterPathDataAccess {

    private final CharacterPathRepository characterPathRepository;

    public SqlCharacterPathService(CharacterPathRepository characterPathRepository) {
        this.characterPathRepository = characterPathRepository;
    }

    @Override
    public List<CharacterPathResponseDTO> getAllCharacterPaths() {
        return characterPathRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public CharacterPathResponseDTO getCharacterPathByCharacterId(Integer characterId) {
        return toDto(getCharacterPathEntity(characterId));
    }

    @Override
    public CharacterPathResponseDTO updateCharacterPath(Integer characterId, UpdateCharacterPathRequestDTO request) {
        CharacterPath characterPath = getCharacterPathEntity(characterId);
        characterPath.setSummary(request.getSummary());
        return toDto(characterPathRepository.save(characterPath));
    }

    private CharacterPath getCharacterPathEntity(Integer characterId) {
        CharacterPath characterPath = characterPathRepository.findByCharacter_Id(characterId);
        if (characterPath == null) {
            throw new ResourceNotFoundException("Character path not found for character id: " + characterId);
        }
        return characterPath;
    }

    private CharacterPathResponseDTO toDto(CharacterPath characterPath) {
        return new CharacterPathResponseDTO(
                characterPath.getId(),
                characterPath.getCharacter().getId(),
                characterPath.getSummary()
        );
    }
}
