package dk.ek.gruppe2.chooseyourfate.service.migration.mongodb;

import dk.ek.gruppe2.chooseyourfate.model.mongodb.QuestDocumentMongo;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Quest;
import dk.ek.gruppe2.chooseyourfate.repository.mongodb.QuestRepositoryMongo;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.QuestHasItemRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.QuestRepository;
import dk.ek.gruppe2.chooseyourfate.service.migration.IdMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestMigrationServiceMongo {

    private final QuestRepository mysqlRepo;
    private final QuestRepositoryMongo mongoRepo;
    private final QuestHasItemRepository questHasItemRepository;
    private final IdMappingService idMappingService;

    public void migrate() {
        log.info("Starting Quest migration...");

        List<Quest> entities = mysqlRepo.findAll();

        for (Quest entity : entities) {

            // resolve scene ID
            String mongoSceneId = idMappingService.get(
                    CollectionNames.SCENES,
                    entity.getScene().getId()
            );

            // resolve item IDs via join table
            List<String> itemIds = questHasItemRepository.findByQuest_Id(entity.getId())
                    .stream()
                    .map(questHasItem -> idMappingService.get(
                            CollectionNames.ITEMS,
                            questHasItem.getItem().getId()
                    ))
                    .toList();

            // build the MongoDB document
            QuestDocumentMongo doc = QuestDocumentMongo.builder()
                    .sceneId(mongoSceneId)
                    .description(entity.getDescription())
                    .itemIds(itemIds)
                    .build();

            // save to MongoDB
            mongoRepo.save(doc);

            // store ID mapping
            idMappingService.put(CollectionNames.QUESTS, entity.getId(), doc.getId());
        }

        log.info("Migrated {} quests", entities.size());
    }
}
