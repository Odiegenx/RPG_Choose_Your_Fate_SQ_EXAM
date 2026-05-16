package dk.ek.gruppe2.chooseyourfate.service;

import dk.ek.gruppe2.chooseyourfate.dto.InventoryResponseDTO;
import dk.ek.gruppe2.chooseyourfate.enums.DataSourceType;
import dk.ek.gruppe2.chooseyourfate.interfaces.InventoryDataAccess;
import dk.ek.gruppe2.chooseyourfate.service.mysql.SqlInventoryService;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final SqlInventoryService sqlInventoryService;

    public InventoryService(SqlInventoryService sqlInventoryService) {
        this.sqlInventoryService = sqlInventoryService;
    }

    public InventoryResponseDTO getInventoryByCharacterId(DataSourceType source, Integer characterId) {
        return resolveDataService(source).getInventoryByCharacterId(characterId);
    }

    private InventoryDataAccess resolveDataService(DataSourceType source) {
        return switch (source) {
            case SQL -> sqlInventoryService;
            //case NEO4J -> neo4jInventoryService;
            //case MONGODB -> mongoInventoryservice;
            default -> throw new IllegalArgumentException("Unexpected value: " + source);
        };
    }
}
