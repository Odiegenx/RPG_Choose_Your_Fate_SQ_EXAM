package dk.ek.gruppe2.chooseyourfate.service.migration;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class IdMappingService {

    private final Map<String, Map<Integer, String>> maps = new HashMap<>();

    public void put(String collection, Integer mysqlId, String mongoId) {
        maps.computeIfAbsent(collection, k -> new HashMap<>())
                .put(mysqlId, mongoId);
    }

    public String get(String collection, Integer mysqlId) {
        if (mysqlId == null) return null;
        return maps.getOrDefault(collection, new HashMap<>())
                .get(mysqlId);
    }
}