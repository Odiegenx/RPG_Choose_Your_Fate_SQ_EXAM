package dk.ek.gruppe2.chooseyourfate.service.mysql;

import dk.ek.gruppe2.chooseyourfate.dto.CharacterResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.CharacterStatsDTO;
import dk.ek.gruppe2.chooseyourfate.dto.CharacterViewResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.CreateCharacterRequestDTO;
import dk.ek.gruppe2.chooseyourfate.exception.ResourceNotFoundException;
import dk.ek.gruppe2.chooseyourfate.interfaces.CharacterDataAccess;
import dk.ek.gruppe2.chooseyourfate.model.mysql.CharacterAvatar;
import dk.ek.gruppe2.chooseyourfate.model.mysql.CharacterDetails;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.AccountRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.ChapterRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.CharacterAvatarRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.CharacterDetailsRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.RaceDetailsRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.SceneRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SqlCharacterService implements CharacterDataAccess {

    @PersistenceContext
    private EntityManager entityManager;

    private final CharacterAvatarRepository characterAvatarRepository;
    private final CharacterDetailsRepository characterDetailsRepository;
    private final AccountRepository accountRepository;
    private final ChapterRepository chapterRepository;
    private final SceneRepository sceneRepository;
    private final RaceDetailsRepository raceDetailsRepository;

    public SqlCharacterService(
            CharacterAvatarRepository characterAvatarRepository,
            CharacterDetailsRepository characterDetailsRepository,
            AccountRepository accountRepository,
            ChapterRepository chapterRepository,
            SceneRepository sceneRepository,
            RaceDetailsRepository raceDetailsRepository
    ) {
        this.characterAvatarRepository = characterAvatarRepository;
        this.characterDetailsRepository = characterDetailsRepository;
        this.accountRepository = accountRepository;
        this.chapterRepository = chapterRepository;
        this.sceneRepository = sceneRepository;
        this.raceDetailsRepository = raceDetailsRepository;
    }

    @Override
    public List<CharacterResponseDTO> getAllCharacters() {
        return characterAvatarRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public CharacterResponseDTO getCharacterById(Integer id) {
        return toDto(getCharacterEntity(id));
    }

    // Builds one SQL-backed character view so the frontend does not need separate character and detail requests.
    public CharacterViewResponseDTO getCharacterViewById(Integer id) {
        CharacterDetails details = getCharacterDetailsEntity(id);
        CharacterAvatar character = details.getCharacter();
        long characterCount = characterAvatarRepository.countByAccount_Id(character.getAccount().getId());
        Boolean canCreateMoreCharacters = characterCount < character.getAccount().getCharacterLimit();

        return toViewDto(character, details, canCreateMoreCharacters);
    }

    @Override
    public CharacterResponseDTO createCharacter(CreateCharacterRequestDTO request) {
        validateCreateRequest(request);

        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("sp_create_character");
        storedProcedure.registerStoredProcedureParameter("p_account_id", Integer.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("p_chapter_id", Integer.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("p_scene_id", Integer.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("p_race_detail_id", Integer.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("p_name", String.class, ParameterMode.IN);
        storedProcedure.registerStoredProcedureParameter("p_character_id", Integer.class, ParameterMode.OUT);

        storedProcedure.setParameter("p_account_id", request.getAccountId());
        storedProcedure.setParameter("p_chapter_id", request.getChapterId());
        storedProcedure.setParameter("p_scene_id", request.getSceneId());
        storedProcedure.setParameter("p_race_detail_id", request.getRaceDetailsId());
        storedProcedure.setParameter("p_name", request.getName());

        storedProcedure.execute();

        Integer createdCharacterId = (Integer) storedProcedure.getOutputParameterValue("p_character_id");
        return toDto(getCharacterEntity(createdCharacterId));
    }

    @Override
    public void deleteCharacter(Integer id) {
        if (!characterAvatarRepository.existsById(id)) {
            throw new ResourceNotFoundException("Character not found with id: " + id);
        }

        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("sp_delete_character");
        storedProcedure.registerStoredProcedureParameter("p_character_id", Integer.class, ParameterMode.IN);
        storedProcedure.setParameter("p_character_id", id);
        storedProcedure.execute();
    }

    private CharacterAvatar getCharacterEntity(Integer id) {
        return characterAvatarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Character not found with id: " + id));
    }

    // Loads the auto-created stats row for a character.
    private CharacterDetails getCharacterDetailsEntity(Integer characterId) {
        return characterDetailsRepository.findByIdWithCharacterView(characterId)
                .orElseThrow(() -> new ResourceNotFoundException("Character details not found for character id: " + characterId));
    }

    // Validates referenced entities before insert and checks that the selected scene belongs to the selected chapter.
    private void validateCreateRequest(CreateCharacterRequestDTO request) {
        if (!accountRepository.existsById(request.getAccountId())) {
            throw new ResourceNotFoundException("Account not found with id: " + request.getAccountId());
        }

        if (!chapterRepository.existsById(request.getChapterId())) {
            throw new ResourceNotFoundException("Chapter not found with id: " + request.getChapterId());
        }

        if (!raceDetailsRepository.existsById(request.getRaceDetailsId())) {
            throw new ResourceNotFoundException("Race details not found with id: " + request.getRaceDetailsId());
        }

        var scene = sceneRepository.findById(request.getSceneId())
                .orElseThrow(() -> new ResourceNotFoundException("Scene not found with id: " + request.getSceneId()));

        if (!scene.getChapter().getId().equals(request.getChapterId())) {
            throw new IllegalArgumentException("Selected scene does not belong to the selected chapter.");
        }
    }

    // Maps the SQL character entity to the normal character response.
    private CharacterResponseDTO toDto(CharacterAvatar character) {
        return new CharacterResponseDTO(
                character.getId(),
                character.getAccount().getId(),
                character.getChapter().getId(),
                character.getScene().getId(),
                character.getRaceDetails().getId(),
                character.getName(),
                character.getFlag()
        );
    }

    // Maps character and detail data to the combined character view response.
    private CharacterViewResponseDTO toViewDto(
            CharacterAvatar character,
            CharacterDetails details,
            Boolean canCreateMoreCharacters
    ) {
        return new CharacterViewResponseDTO(
                character.getId(),
                character.getName(),
                character.getChapter().getId(),
                character.getChapter().getName(),
                character.getRaceDetails().getId(),
                getRaceDisplayName(character),
                new CharacterStatsDTO(
                        details.getIntelligence(),
                        details.getCharisma(),
                        details.getFashion()
                ),
                canCreateMoreCharacters
        );
    }

    // Gives race_details a display label until the SQL schema stores real race names.
    private String getRaceDisplayName(CharacterAvatar character) {
        return "Race " + character.getRaceDetails().getId();
    }
}
