package dk.ek.gruppe2.chooseyourfate.service;

import dk.ek.gruppe2.chooseyourfate.dto.CharacterResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.CreateCharacterRequestDTO;
import dk.ek.gruppe2.chooseyourfate.enums.DataSourceType;
import dk.ek.gruppe2.chooseyourfate.interfaces.CharacterDataAccess;
import dk.ek.gruppe2.chooseyourfate.service.mongodb.MongoCharacterService;
import dk.ek.gruppe2.chooseyourfate.service.mysql.SqlCharacterService;
import dk.ek.gruppe2.chooseyourfate.service.neo4j.Neo4jCharacterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharacterService {

    private final SqlCharacterService sqlCharacterService;
    private final Neo4jCharacterService neo4jCharacterService;
    private final MongoCharacterService mongoCharacterService;

    public CharacterService(
            SqlCharacterService sqlCharacterService,
            Neo4jCharacterService neo4jCharacterService,
            MongoCharacterService mongoCharacterService
    ) {
        this.sqlCharacterService = sqlCharacterService;
        this.neo4jCharacterService = neo4jCharacterService;
        this.mongoCharacterService = mongoCharacterService;
    }

    public List<CharacterResponseDTO> getAllCharacters(DataSourceType sourceHeader) {
        return resolveDataAccess(sourceHeader).getAllCharacters();
    }

    public CharacterResponseDTO getCharacterById(DataSourceType sourceHeader, String id) {
        return resolveDataAccess(sourceHeader).getCharacterById(sourceHeader == DataSourceType.MONGODB ? id : Integer.parseInt(id));
    }

    public CharacterResponseDTO createCharacter(DataSourceType sourceHeader, CreateCharacterRequestDTO request) {
        return resolveDataAccess(sourceHeader).createCharacter(request);
    }

    public void deleteCharacter(DataSourceType sourceHeader, String id) {
        resolveDataAccess(sourceHeader).deleteCharacter(sourceHeader == DataSourceType.MONGODB ? id : Integer.parseInt(id));
    }

    public List<CharacterResponseDTO> getCharactersByAccountId(DataSourceType sourceHeader, String id) {
        return resolveDataAccess(sourceHeader).getCharactersByAccountId(sourceHeader == DataSourceType.MONGODB ? id : Integer.parseInt(id));
    }

    private CharacterDataAccess resolveDataAccess(DataSourceType sourceHeader) {
        return switch (sourceHeader) {
            case SQL -> sqlCharacterService;
            case NEO4J -> neo4jCharacterService;
            case MONGODB -> mongoCharacterService;
        };
    }
}