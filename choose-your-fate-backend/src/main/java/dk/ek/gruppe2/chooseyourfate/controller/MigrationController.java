package dk.ek.gruppe2.chooseyourfate.controller;

import dk.ek.gruppe2.chooseyourfate.service.migration.MigrationRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/migration")
@RequiredArgsConstructor
@Slf4j
public class MigrationController {

    private final MigrationRunner migrationRunner;

    private boolean migrationAlreadyRun = false;

    @PostMapping("/run")
    public ResponseEntity<String> runMigration() {
        if (migrationAlreadyRun) {
            return ResponseEntity.badRequest().body("Migration has already been run!");
        }
        migrationRunner.runAll();
        migrationAlreadyRun = true;
        return ResponseEntity.ok("Migration complete!");
    }
}
