package dk.ek.gruppe2.chooseyourfate.service;

import dk.ek.gruppe2.chooseyourfate.datasource.DataSourceResolver;
import dk.ek.gruppe2.chooseyourfate.dto.CharacterDetailsResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateCharacterDetailsRequestDTO;
import dk.ek.gruppe2.chooseyourfate.enums.DataSourceType;
import dk.ek.gruppe2.chooseyourfate.interfaces.CharacterDetailsDataAccess;
import dk.ek.gruppe2.chooseyourfate.service.mongodb.MongoCharacterDetailsService;
import dk.ek.gruppe2.chooseyourfate.service.mysql.SqlCharacterDetailsService;
import dk.ek.gruppe2.chooseyourfate.service.neo4j.Neo4jCharacterDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharacterDetailsService {

    private final DataSourceResolver dataSourceResolver;
    private final SqlCharacterDetailsService sqlCharacterDetailsService;
    private final Neo4jCharacterDetailsService neo4jCharacterDetailsService;
    private final MongoCharacterDetailsService mongoCharacterDetailsService;

    public CharacterDetailsService(
            DataSourceResolver dataSourceResolver,
            SqlCharacterDetailsService sqlCharacterDetailsService,
            Neo4jCharacterDetailsService neo4jCharacterDetailsService,
            MongoCharacterDetailsService mongoCharacterDetailsService
    ) {
        this.dataSourceResolver = dataSourceResolver;
        this.sqlCharacterDetailsService = sqlCharacterDetailsService;
        this.neo4jCharacterDetailsService = neo4jCharacterDetailsService;
        this.mongoCharacterDetailsService = mongoCharacterDetailsService;
    }

    public List<CharacterDetailsResponseDTO> getAllCharacterDetails(String sourceHeader) {
        return resolveDataAccess(sourceHeader).getAllCharacterDetails();
    }

    public CharacterDetailsResponseDTO getCharacterDetailsByCharacterId(String sourceHeader, Integer characterId) {
        return resolveDataAccess(sourceHeader).getCharacterDetailsByCharacterId(characterId);
    }

    public CharacterDetailsResponseDTO updateCharacterDetails(
            String sourceHeader,
            Integer characterId,
            UpdateCharacterDetailsRequestDTO request
    ) {
        return resolveDataAccess(sourceHeader).updateCharacterDetails(characterId, request);
    }

    private CharacterDetailsDataAccess resolveDataAccess(String sourceHeader) {
        DataSourceType dataSourceType = dataSourceResolver.resolve(sourceHeader);
        return switch (dataSourceType) {
            case SQL -> sqlCharacterDetailsService;
            case NEO4J -> neo4jCharacterDetailsService;
            case MONGODB -> mongoCharacterDetailsService;
        };
    }
}
