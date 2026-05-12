package dk.ek.gruppe2.chooseyourfate.service;

import dk.ek.gruppe2.chooseyourfate.datasource.DataSourceResolver;
import dk.ek.gruppe2.chooseyourfate.dto.CharacterResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.CharacterViewResponseDTO;
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

    private final DataSourceResolver dataSourceResolver;
    private final SqlCharacterService sqlCharacterService;
    private final Neo4jCharacterService neo4jCharacterService;
    private final MongoCharacterService mongoCharacterService;

    public CharacterService(
            DataSourceResolver dataSourceResolver,
            SqlCharacterService sqlCharacterService,
            Neo4jCharacterService neo4jCharacterService,
            MongoCharacterService mongoCharacterService
    ) {
        this.dataSourceResolver = dataSourceResolver;
        this.sqlCharacterService = sqlCharacterService;
        this.neo4jCharacterService = neo4jCharacterService;
        this.mongoCharacterService = mongoCharacterService;
    }

    public List<CharacterResponseDTO> getAllCharacters(String sourceHeader) {
        return resolveDataAccess(sourceHeader).getAllCharacters();
    }

    public CharacterResponseDTO getCharacterById(String sourceHeader, Integer id) {
        return resolveDataAccess(sourceHeader).getCharacterById(id);
    }

    // Returns the combined character view; currently implemented for SQL because MongoDB and Neo4j character services are not implemented yet.
    public CharacterViewResponseDTO getCharacterViewById(String sourceHeader, Integer id) {
        DataSourceType dataSourceType = dataSourceResolver.resolve(sourceHeader);
        return switch (dataSourceType) {
            case SQL -> sqlCharacterService.getCharacterViewById(id);
            case NEO4J -> throw new UnsupportedOperationException("Neo4j character view is not implemented yet");
            case MONGODB -> throw new UnsupportedOperationException("MongoDB character view is not implemented yet");
        };
    }

    public CharacterResponseDTO createCharacter(String sourceHeader, CreateCharacterRequestDTO request) {
        return resolveDataAccess(sourceHeader).createCharacter(request);
    }

    public void deleteCharacter(String sourceHeader, Integer id) {
        resolveDataAccess(sourceHeader).deleteCharacter(id);
    }

    private CharacterDataAccess resolveDataAccess(String sourceHeader) {
        DataSourceType dataSourceType = dataSourceResolver.resolve(sourceHeader);
        return switch (dataSourceType) {
            case SQL -> sqlCharacterService;
            case NEO4J -> neo4jCharacterService;
            case MONGODB -> mongoCharacterService;
        };
    }
}
