package dk.ek.gruppe2.chooseyourfate.service;

import dk.ek.gruppe2.chooseyourfate.enums.DataSourceType;
import dk.ek.gruppe2.chooseyourfate.interfaces.ItemDataAccess;
import dk.ek.gruppe2.chooseyourfate.service.mysql.SqlItemService;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final SqlItemService sqlItemService;

    public ItemService(SqlItemService sqlItemService) {
        this.sqlItemService = sqlItemService;
    }

    private ItemDataAccess resolveDataService(DataSourceType source) {
        return switch (source) {
            case SQL -> sqlItemService;
            //case NEO4J -> neo4jItemService;
            //case MONGODB -> mongoItemservice;
            default -> throw new IllegalArgumentException("Unexpected value: " + source);
        };
    }
}
