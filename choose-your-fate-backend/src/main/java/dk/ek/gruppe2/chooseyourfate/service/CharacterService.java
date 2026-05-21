package dk.ek.gruppe2.chooseyourfate.service;

import dk.ek.gruppe2.chooseyourfate.availability.replication.ReplicationOperationType;
import dk.ek.gruppe2.chooseyourfate.availability.replication.WriteOperationCoordinator;
import dk.ek.gruppe2.chooseyourfate.dto.CharacterResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.CreateCharacterRequestDTO;
import dk.ek.gruppe2.chooseyourfate.enums.DataSourceType;
import dk.ek.gruppe2.chooseyourfate.interfaces.CharacterDataAccess;
import dk.ek.gruppe2.chooseyourfate.service.mysql.SqlCharacterService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CharacterService {

    private final SqlCharacterService sqlCharacterService;
    private final WriteOperationCoordinator writeOperationCoordinator;

    public CharacterService(
            SqlCharacterService sqlCharacterService,
            WriteOperationCoordinator writeOperationCoordinator
    ) {
        this.sqlCharacterService = sqlCharacterService;
        this.writeOperationCoordinator = writeOperationCoordinator;
    }

    public List<CharacterResponseDTO> getAllCharacters(DataSourceType sourceHeader) {
        return resolveDataAccess(sourceHeader).getAllCharacters();
    }

    public CharacterResponseDTO getCharacterById(DataSourceType sourceHeader, String id) {
        return resolveDataAccess(sourceHeader).getCharacterById(Integer.parseInt(id));
    }

    public CharacterResponseDTO createCharacter(DataSourceType sourceHeader, CreateCharacterRequestDTO request) {
        return writeOperationCoordinator.execute(
                ReplicationOperationType.CREATE,
                "character_avatar",
                () -> characterPayload(request),
                () -> resolveDataAccess(sourceHeader).createCharacter(request)
        );
    }

    public void deleteCharacter(DataSourceType sourceHeader, String id) {
        writeOperationCoordinator.execute(
                ReplicationOperationType.DELETE,
                "character_avatar",
                () -> Map.of("id", id),
                () -> resolveDataAccess(sourceHeader).deleteCharacter(Integer.parseInt(id))
        );
    }

    public List<CharacterResponseDTO> getCharactersByAccountId(DataSourceType sourceHeader, String id) {
        return resolveDataAccess(sourceHeader).getCharactersByAccountId(Integer.parseInt(id));
    }

    private CharacterDataAccess<Integer> resolveDataAccess(DataSourceType sourceHeader) {
        DataSourceType dataSourceType = sourceHeader == null ? DataSourceType.SQL : sourceHeader;
        return switch (dataSourceType) {
            case SQL -> sqlCharacterService;
        };
    }

    private Map<String, Object> characterPayload(CreateCharacterRequestDTO request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("accountId", request.getAccountId());
        payload.put("raceDetailsId", request.getRaceDetailsId());
        payload.put("chapterId", request.getChapterId());
        payload.put("sceneId", request.getSceneId());
        payload.put("name", request.getName());
        return payload;
    }
}
