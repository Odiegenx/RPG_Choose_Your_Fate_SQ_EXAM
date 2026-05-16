package dk.ek.gruppe2.chooseyourfate.service.migration.mongodb;

import dk.ek.gruppe2.chooseyourfate.model.mongodb.ChapterDocumentMongo;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Chapter;
import dk.ek.gruppe2.chooseyourfate.repository.mongodb.ChapterRepositoryMongo;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.ChapterRepository;
import dk.ek.gruppe2.chooseyourfate.service.migration.IdMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChapterMigrationServiceMongo {

    private final ChapterRepository mysqlRepo;
    private final ChapterRepositoryMongo mongoRepo;
    private final IdMappingService idMappingService;

    public void migrate() {
        log.info("Starting Chapter migration...");

        List<Chapter> entities = mysqlRepo.findAll();

        for (Chapter entity : entities) {

            // 1. build the MongoDB document
            ChapterDocumentMongo doc = ChapterDocumentMongo.builder()
                    .name(entity.getName())
                    .build();

            // 2. save to MongoDB
            mongoRepo.save(doc);

            // 3. store ID mapping
            idMappingService.put(CollectionNames.CHAPTERS, entity.getId(), doc.getId());
        }

        log.info("Migrated {} chapters", entities.size());
    }

    // SECOND PASS — resolve StartingSceneId
    // All scenes now exist in MongoDB so we can resolve all references
    public void migrateSecondPass() {
        log.info("Starting Scene migration (second pass)...");

        List<Chapter> entities = mysqlRepo.findAll();

        for (Chapter entity : entities) {

            // fetch the chapter document we saved in first pass
            String mongoChapterId = idMappingService.get(CollectionNames.CHAPTERS, entity.getId());
            ChapterDocumentMongo doc = mongoRepo.findById(mongoChapterId).orElseThrow();

            if (entity.getStartingScene() != null) {
                doc.setStartingSceneId(idMappingService.get(
                        CollectionNames.SCENES,
                        entity.getStartingScene().getId()
                ));
            } else {
                doc.setStartingSceneId(null);
            }

            mongoRepo.save(doc);
        }

        log.info("Updated {} scenes (second pass)", entities.size());
    }
}