package dk.ek.gruppe2.chooseyourfate.service;

import dk.ek.gruppe2.chooseyourfate.dto.LoadoutResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateEquipmentRequestDTO;
import dk.ek.gruppe2.chooseyourfate.enums.DataSourceType;
import dk.ek.gruppe2.chooseyourfate.interfaces.LoadoutDataAccess;
import dk.ek.gruppe2.chooseyourfate.service.mysql.SqlLoadoutService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoadoutService {

    private final SqlLoadoutService sqlLoadoutService;
    //private final Neo4jLoadoutService neo4jLoadoutService;
    //private final MongoLoadoutService mongoLoadoutService;

    public LoadoutService(
            SqlLoadoutService sqlLoadoutService
            //Neo4jLoadoutService neo4jLoadoutService,
            //MongoLoadoutService mongoLoadoutService
    ) {
        this.sqlLoadoutService = sqlLoadoutService;
        //this.neo4jLoadoutService = neo4jLoadoutService;
        //this.mongoLoadoutService = mongoLoadoutService;
    }

    public LoadoutResponseDTO getLoadoutByCharacterId(DataSourceType source, Integer characterId) {
        return resolveDataService(source).getLoadoutByCharacterId(characterId);
    }

    public LoadoutResponseDTO unequipItem(DataSourceType source, Integer characterId, Integer itemId) {
        return resolveDataService(source).unequipItem(characterId, itemId);
    }

    public LoadoutResponseDTO equipItem(DataSourceType source, Integer characterId, Integer itemId) {
        return resolveDataService(source).equipItem(characterId, itemId);
    }

    private LoadoutDataAccess resolveDataService(DataSourceType source) {
        return switch (source) {
            case SQL -> sqlLoadoutService;
            //case NEO4J -> neo4jLoadoutService;
            //case MONGODB -> mongoLoadoutservice;
            default -> throw new IllegalArgumentException("Unexpected value: " + source);
        };
    }

}
