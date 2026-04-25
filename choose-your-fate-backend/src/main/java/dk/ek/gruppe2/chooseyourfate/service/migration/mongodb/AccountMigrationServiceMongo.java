package dk.ek.gruppe2.chooseyourfate.service.migration.mongodb;

import dk.ek.gruppe2.chooseyourfate.model.mongodb.AccountDocumentMongo;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Account;
import dk.ek.gruppe2.chooseyourfate.repository.mongodb.AccountRepositoryMongo;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.AccountRepository;
import dk.ek.gruppe2.chooseyourfate.service.migration.IdMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountMigrationServiceMongo {

    private final AccountRepository mysqlRepo;
    private final AccountRepositoryMongo mongoRepo;
    private final IdMappingService idMappingService;

    public void migrate() {
        log.info("Starting Account migration...");

        List<Account> entities = mysqlRepo.findAll();

        for (Account entity : entities) {

            // 1. build the MongoDB document
            AccountDocumentMongo doc = AccountDocumentMongo.builder()
                    .username(entity.getUsername())
                    .password(entity.getPassword())
                    .email(entity.getEmail())
                    .characterLimit(entity.getCharacterLimit())
                    .build();

            // 2. save to MongoDB
            mongoRepo.save(doc);

            // 3. store ID mapping
            idMappingService.put(CollectionNames.ACCOUNTS, entity.getId(), doc.getId());
        }

        log.info("Migrated {} accounts", entities.size());
    }

}
