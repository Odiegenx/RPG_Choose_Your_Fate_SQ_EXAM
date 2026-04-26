package dk.ek.gruppe2.chooseyourfate.service;

import dk.ek.gruppe2.chooseyourfate.datasource.DataSourceResolver;
import dk.ek.gruppe2.chooseyourfate.dto.EquipmentResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateEquipmentRequestDTO;
import dk.ek.gruppe2.chooseyourfate.enums.DataSourceType;
import dk.ek.gruppe2.chooseyourfate.interfaces.EquipmentDataAccess;
import dk.ek.gruppe2.chooseyourfate.service.mongodb.MongoEquipmentService;
import dk.ek.gruppe2.chooseyourfate.service.mysql.SqlEquipmentService;
import dk.ek.gruppe2.chooseyourfate.service.neo4j.Neo4jEquipmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentService {

    private final DataSourceResolver dataSourceResolver;
    private final SqlEquipmentService sqlEquipmentService;
    private final Neo4jEquipmentService neo4jEquipmentService;
    private final MongoEquipmentService mongoEquipmentService;

    public EquipmentService(
            DataSourceResolver dataSourceResolver,
            SqlEquipmentService sqlEquipmentService,
            Neo4jEquipmentService neo4jEquipmentService,
            MongoEquipmentService mongoEquipmentService
    ) {
        this.dataSourceResolver = dataSourceResolver;
        this.sqlEquipmentService = sqlEquipmentService;
        this.neo4jEquipmentService = neo4jEquipmentService;
        this.mongoEquipmentService = mongoEquipmentService;
    }

    public List<EquipmentResponseDTO> getAllEquipment(String sourceHeader) {
        return resolveDataAccess(sourceHeader).getAllEquipment();
    }

    public EquipmentResponseDTO getEquipmentByCharacterId(String sourceHeader, Integer characterId) {
        return resolveDataAccess(sourceHeader).getEquipmentByCharacterId(characterId);
    }

    public EquipmentResponseDTO updateEquipment(
            String sourceHeader,
            Integer characterId,
            UpdateEquipmentRequestDTO request
    ) {
        return resolveDataAccess(sourceHeader).updateEquipment(characterId, request);
    }

    private EquipmentDataAccess resolveDataAccess(String sourceHeader) {
        DataSourceType dataSourceType = dataSourceResolver.resolve(sourceHeader);
        return switch (dataSourceType) {
            case SQL -> sqlEquipmentService;
            case NEO4J -> neo4jEquipmentService;
            case MONGODB -> mongoEquipmentService;
        };
    }
}
