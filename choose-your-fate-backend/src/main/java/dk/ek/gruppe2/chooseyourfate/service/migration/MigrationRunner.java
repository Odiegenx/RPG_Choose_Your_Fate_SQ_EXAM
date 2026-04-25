package dk.ek.gruppe2.chooseyourfate.service.migration;

import dk.ek.gruppe2.chooseyourfate.service.migration.mongodb.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrationRunner {

    private final RaceDetailsMigrationServiceMongo raceDetailsMigrationService;
    private final ItemMigrationServiceMongo itemMigrationService;
    private final ChapterMigrationServiceMongo chapterMigrationService;
    private final AccountMigrationServiceMongo accountMigrationService;
    private final NpcMigrationServiceMongo npcMigrationService;
    private final SceneMigrationServiceMongo sceneMigrationService;
    private final QuestMigrationServiceMongo questMigrationService;
    private final CharacterAvatarMigrationServiceMongo characterMigrationService;

    public void runAll() {
        log.info("Starting full migration...");

        raceDetailsMigrationService.migrate();      // no dependencies
        itemMigrationService.migrate();             // no dependencies
        chapterMigrationService.migrate();          // no dependencies
        accountMigrationService.migrate();          // no dependencies
        npcMigrationService.migrate();              // depends on race_details
        sceneMigrationService.migrateFirstPass();   // depends on chapters, npcs
        questMigrationService.migrate();            // depends on scenes
        sceneMigrationService.migrateSecondPass();  // depends on quests
        characterMigrationService.migrate();        // depends on everything

        log.info("Full migration complete!");
    }
}
