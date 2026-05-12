package dk.ek.gruppe2.chooseyourfate.service.migration.mongodb;

import dk.ek.gruppe2.chooseyourfate.model.mongodb.NpcDocumentMongo;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Npc;
import dk.ek.gruppe2.chooseyourfate.repository.mongodb.NpcRepositoryMongo;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.NpcRepository;
import dk.ek.gruppe2.chooseyourfate.service.migration.IdMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NpcMigrationServiceMongo {

    private final NpcRepository mysqlRepo;
    private final NpcRepositoryMongo mongoRepo;
    private final IdMappingService idMappingService;

    public void migrate() {
        log.info("Starting NPC migration...");

        List<Npc> entities = mysqlRepo.findAll();

        for (Npc entity : entities) {

            // resolve race_details MySQL ID → MongoDB string ID
            String mongoRaceDetailId = idMappingService.get(
                    CollectionNames.RACE_DETAILS,
                    entity.getRaceDetails().getId()
            );

            // 1. build the MongoDB document
            NpcDocumentMongo doc = NpcDocumentMongo.builder()
                    .name(entity.getName())
                    .raceDetailId(mongoRaceDetailId)    // resolved reference
                    .build();

            // 2. save to MongoDB
            mongoRepo.save(doc);

            // 3. store ID mapping
            idMappingService.put(CollectionNames.NPCS, entity.getId(), doc.getId());
        }

        log.info("Migrated {} npcs", entities.size());
    }

    public void dropCollection() {
        log.info("dropping collection Npcs");
        mongoRepo.deleteAll();
    }
}
