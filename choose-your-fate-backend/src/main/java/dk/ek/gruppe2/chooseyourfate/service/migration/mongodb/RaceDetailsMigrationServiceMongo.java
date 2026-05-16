package dk.ek.gruppe2.chooseyourfate.service.migration.mongodb;

import dk.ek.gruppe2.chooseyourfate.model.mongodb.RaceDetailsDocumentMongo;
import dk.ek.gruppe2.chooseyourfate.model.mysql.RaceDetails;
import dk.ek.gruppe2.chooseyourfate.repository.mongodb.RaceDetailsRepositoryMongo;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.RaceDetailsRepository;
import dk.ek.gruppe2.chooseyourfate.service.migration.IdMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RaceDetailsMigrationServiceMongo {

    private final RaceDetailsRepositoryMongo mongoRepo;
    private final RaceDetailsRepository mysqlRepo;
    private final IdMappingService idMappingService;

    public void migrate() {
        log.info("migrating race details");

        List<RaceDetails> entities = mysqlRepo.findAll();

        for (RaceDetails entity : entities) {

            // resolve scene ID
            String mongoStartingChapterId = idMappingService.get(
                    CollectionNames.CHAPTERS,
                    entity.getStartingChapter().getId()
            );

            RaceDetailsDocumentMongo doc = RaceDetailsDocumentMongo.builder()
            .name(entity.getName())
            .startingChapterId(mongoStartingChapterId)
            .build();

            mongoRepo.save(doc);

            idMappingService.put(CollectionNames.RACE_DETAILS, entity.getId(), doc.getId());
        }

        log.info("Migrated {} race detail entities", entities.size());
    }
}
