package dk.ek.gruppe2.chooseyourfate.service.migration.mongodb;

import dk.ek.gruppe2.chooseyourfate.model.mongodb.ItemDocumentMongo;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Item;
import dk.ek.gruppe2.chooseyourfate.repository.mongodb.ItemRepositoryMongo;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.ItemRepository;
import dk.ek.gruppe2.chooseyourfate.service.migration.IdMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemMigrationServiceMongo {
    private final ItemRepositoryMongo mongoRepo;
    private final ItemRepository mysqlRepo;
    private final IdMappingService idMappingService;

    public void migrate() {
        log.info("Starting Item migration...");

        List<Item> entities = mysqlRepo.findAll();

        for (Item entity : entities) {

            // 1. build the MongoDB document
            ItemDocumentMongo doc = ItemDocumentMongo.builder()
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .type(entity.getType())
                    .build();

            // 2. save to MongoDB
            mongoRepo.save(doc);

            // 3. store ID mapping
            idMappingService.put(CollectionNames.ITEMS, entity.getId(), doc.getId());
        }

        log.info("Migrated {} items", entities.size());
    }

}
