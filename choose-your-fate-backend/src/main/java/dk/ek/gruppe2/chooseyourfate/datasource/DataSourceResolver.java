package dk.ek.gruppe2.chooseyourfate.datasource;

import dk.ek.gruppe2.chooseyourfate.enums.DataSourceType;
import org.springframework.stereotype.Component;

@Component
public class DataSourceResolver {

    public DataSourceType resolve(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return DataSourceType.SQL;
        }

        return switch (headerValue.trim().toLowerCase()) {
            case "sql", "mysql" -> DataSourceType.SQL;
            case "neo4j", "graph" -> DataSourceType.NEO4J;
            case "mongodb", "mongo", "document" -> DataSourceType.MONGODB;
            default -> throw new IllegalArgumentException("Unsupported data source: " + headerValue);
        };
    }
}
