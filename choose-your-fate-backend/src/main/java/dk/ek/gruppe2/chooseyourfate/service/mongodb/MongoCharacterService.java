package dk.ek.gruppe2.chooseyourfate.service.mongodb;

import dk.ek.gruppe2.chooseyourfate.dto.CharacterResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.CreateCharacterRequestDTO;
import dk.ek.gruppe2.chooseyourfate.interfaces.CharacterDataAccess;
import dk.ek.gruppe2.chooseyourfate.model.mongodb.CharacterAvatarDocumentMongo;
import dk.ek.gruppe2.chooseyourfate.repository.mongodb.CharacterAvatarRepositoryMongo;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoCharacterService implements CharacterDataAccess<String> {
    private final CharacterAvatarRepositoryMongo characterAvatarRepository;

    public MongoCharacterService(CharacterAvatarRepositoryMongo characterAvatarRepository) {
        this.characterAvatarRepository = characterAvatarRepository;
    }

    private static final String MESSAGE = "MongoDB character functionality is not implemented yet";

    @Override
    public List<CharacterResponseDTO> getAllCharacters() {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public CharacterResponseDTO getCharacterById(String id) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public CharacterResponseDTO createCharacter(CreateCharacterRequestDTO request) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public void deleteCharacter(String id) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    //Made to retreive all characters connected to the account that is logged in.
    @Override
    public List<CharacterResponseDTO> getCharactersByAccountId(String id) {
        return getCharacterentitesByAccountId(id).stream()
                .map(this::toDto)
                .toList();
    }

    private List<CharacterAvatarDocumentMongo> getCharacterentitesByAccountId(String id) {
        return characterAvatarRepository.findByAccountId(id);
    }

    private CharacterResponseDTO toDto(CharacterAvatarDocumentMongo character) {
        return new CharacterResponseDTO(
                character.getId(),
                character.getAccountId(),
                character.getChapterId(),
                character.getSceneId(),
                character.getRaceDetailId(),
                character.getName(),
                character.getFlag()
        );
    }
}
