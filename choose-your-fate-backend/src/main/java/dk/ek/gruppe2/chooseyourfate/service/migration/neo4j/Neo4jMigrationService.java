package dk.ek.gruppe2.chooseyourfate.service.migration.neo4j;

import dk.ek.gruppe2.chooseyourfate.dto.Neo4jMigrationResponseDTO;
import dk.ek.gruppe2.chooseyourfate.model.mysql.*;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.*;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionContext;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class Neo4jMigrationService {

    private final Driver neo4jDriver;
    private final AccountRepository accountRepository;
    private final ChapterRepository chapterRepository;
    private final SceneRepository sceneRepository;
    private final ChoiceRepository choiceRepository;
    private final QuestRepository questRepository;
    private final ItemRepository itemRepository;
    private final NpcRepository npcRepository;
    private final RaceDetailsRepository raceDetailsRepository;
    private final CharacterAvatarRepository characterAvatarRepository;
    private final CharacterDetailsRepository characterDetailsRepository;
    private final CharacterHasQuestRepository characterHasQuestRepository;
    private final CharacterPathRepository characterPathRepository;
    private final CharacterPathChoiceRepository characterPathChoiceRepository;
    private final ChoiceHasItemRepository choiceHasItemRepository;
    private final EquipmentRepository equipmentRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryHasItemRepository inventoryHasItemRepository;
    private final QuestHasItemRepository questHasItemRepository;
    private final SceneHasNpcRepository sceneHasNpcRepository;

    public Neo4jMigrationService(
            Driver neo4jDriver,
            AccountRepository accountRepository,
            ChapterRepository chapterRepository,
            SceneRepository sceneRepository,
            ChoiceRepository choiceRepository,
            QuestRepository questRepository,
            ItemRepository itemRepository,
            NpcRepository npcRepository,
            RaceDetailsRepository raceDetailsRepository,
            CharacterAvatarRepository characterAvatarRepository,
            CharacterDetailsRepository characterDetailsRepository,
            CharacterHasQuestRepository characterHasQuestRepository,
            CharacterPathRepository characterPathRepository,
            CharacterPathChoiceRepository characterPathChoiceRepository,
            ChoiceHasItemRepository choiceHasItemRepository,
            EquipmentRepository equipmentRepository,
            InventoryRepository inventoryRepository,
            InventoryHasItemRepository inventoryHasItemRepository,
            QuestHasItemRepository questHasItemRepository,
            SceneHasNpcRepository sceneHasNpcRepository
    ) {
        this.neo4jDriver = neo4jDriver;
        this.accountRepository = accountRepository;
        this.chapterRepository = chapterRepository;
        this.sceneRepository = sceneRepository;
        this.choiceRepository = choiceRepository;
        this.questRepository = questRepository;
        this.itemRepository = itemRepository;
        this.npcRepository = npcRepository;
        this.raceDetailsRepository = raceDetailsRepository;
        this.characterAvatarRepository = characterAvatarRepository;
        this.characterDetailsRepository = characterDetailsRepository;
        this.characterHasQuestRepository = characterHasQuestRepository;
        this.characterPathRepository = characterPathRepository;
        this.characterPathChoiceRepository = characterPathChoiceRepository;
        this.choiceHasItemRepository = choiceHasItemRepository;
        this.equipmentRepository = equipmentRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryHasItemRepository = inventoryHasItemRepository;
        this.questHasItemRepository = questHasItemRepository;
        this.sceneHasNpcRepository = sceneHasNpcRepository;
    }

    public Neo4jMigrationResponseDTO migrateFromMySql(boolean clearExisting) {
        List<Account> accounts = accountRepository.findAll();
        List<Chapter> chapters = chapterRepository.findAll();
        List<Scene> scenes = sceneRepository.findAll();
        List<Choice> choices = choiceRepository.findAll();
        List<Quest> quests = questRepository.findAll();
        List<Item> items = itemRepository.findAll();
        List<Npc> npcs = npcRepository.findAll();
        List<RaceDetails> raceDetails = raceDetailsRepository.findAll();
        List<CharacterAvatar> characters = characterAvatarRepository.findAll();
        List<CharacterDetails> characterDetails = characterDetailsRepository.findAll();
        List<CharacterHasQuest> characterQuests = characterHasQuestRepository.findAll();
        List<CharacterPath> characterPaths = characterPathRepository.findAll();
        List<CharacterPathChoice> characterPathChoices = characterPathChoiceRepository.findAll();
        List<ChoiceHasItem> choiceItems = choiceHasItemRepository.findAll();
        List<Equipment> equipment = equipmentRepository.findAll();
        List<Inventory> inventories = inventoryRepository.findAll();
        List<InventoryHasItem> inventoryItems = inventoryHasItemRepository.findAll();
        List<QuestHasItem> questItems = questHasItemRepository.findAll();
        List<SceneHasNpc> sceneNpcs = sceneHasNpcRepository.findAll();

        try (Session session = neo4jDriver.session()) {
            createConstraints(session);
        }

        try (Session session = neo4jDriver.session()) {
            session.executeWrite(tx -> {
                if (clearExisting) {
                    tx.run("MATCH (n) DETACH DELETE n").consume();
                }
                migrateNodes(tx, accounts, chapters, scenes, choices, quests, items, npcs, raceDetails, characters, characterPaths, inventories);
                syncAccountCounter(tx);
                migrateCharacterDetails(tx, characterDetails);
                migrateRelationships(tx, characterQuests, characterPathChoices, choiceItems, equipment, inventoryItems, questItems, sceneNpcs);
                return null;
            });
        }

        Map<String, Integer> migratedCounts = new LinkedHashMap<>();
        migratedCounts.put("accounts", accounts.size());
        migratedCounts.put("chapters", chapters.size());
        migratedCounts.put("scenes", scenes.size());
        migratedCounts.put("choices", choices.size());
        migratedCounts.put("quests", quests.size());
        migratedCounts.put("items", items.size());
        migratedCounts.put("npcs", npcs.size());
        migratedCounts.put("raceDetails", raceDetails.size());
        migratedCounts.put("characters", characters.size());
        migratedCounts.put("characterDetails", characterDetails.size());
        migratedCounts.put("characterPaths", characterPaths.size());
        migratedCounts.put("inventories", inventories.size());
        migratedCounts.put("characterQuestRelations", characterQuests.size());
        migratedCounts.put("characterPathChoiceRelations", characterPathChoices.size());
        migratedCounts.put("choiceItemRelations", choiceItems.size());
        migratedCounts.put("inventoryItemRelations", inventoryItems.size());
        migratedCounts.put("questItemRelations", questItems.size());
        migratedCounts.put("sceneNpcRelations", sceneNpcs.size());
        migratedCounts.put("equipmentRows", equipment.size());

        Map<String, Integer> integrityViolations = runIntegrityChecks();

        return new Neo4jMigrationResponseDTO(
                "neo4j",
                clearExisting,
                OffsetDateTime.now(ZoneOffset.UTC),
                migratedCounts,
                integrityViolations
        );
    }

    public Map<String, Integer> runIntegrityChecks() {
        String integrityQuery =
                "RETURN " +
                        "COUNT { MATCH (c:Character) WHERE NOT (:Account)-[:OWNS_CHARACTER]->(c) } AS charactersWithoutAccount, " +
                        "COUNT { MATCH (c:Character) WHERE NOT (c)-[:CURRENT_SCENE]->(:Scene) } AS charactersWithoutScene, " +
                        "COUNT { MATCH (c:Character) WHERE NOT (c)-[:HAS_RACE]->(:RaceDetails) } AS charactersWithoutRace, " +
                        "COUNT { MATCH (s:Scene) WHERE NOT (:Chapter)-[:HAS_SCENE]->(s) } AS scenesWithoutChapter, " +
                        "COUNT { MATCH (q:Quest) WHERE NOT (:Scene)-[:HAS_QUEST]->(q) } AS questsWithoutScene, " +
                        "COUNT { MATCH (i:Inventory) WHERE NOT (:Character)-[:HAS_INVENTORY]->(i) } AS inventoriesWithoutCharacter, " +
                        "COUNT { MATCH (p:CharacterPath) WHERE NOT (:Character)-[:HAS_PATH]->(p) } AS pathsWithoutCharacter";

        try (Session session = neo4jDriver.session()) {
            return session.executeRead(tx -> {
                Record record = tx.run(integrityQuery).single();
                Map<String, Integer> violations = new LinkedHashMap<>();
                violations.put("charactersWithoutAccount", record.get("charactersWithoutAccount").asInt());
                violations.put("charactersWithoutScene", record.get("charactersWithoutScene").asInt());
                violations.put("charactersWithoutRace", record.get("charactersWithoutRace").asInt());
                violations.put("scenesWithoutChapter", record.get("scenesWithoutChapter").asInt());
                violations.put("questsWithoutScene", record.get("questsWithoutScene").asInt());
                violations.put("inventoriesWithoutCharacter", record.get("inventoriesWithoutCharacter").asInt());
                violations.put("pathsWithoutCharacter", record.get("pathsWithoutCharacter").asInt());
                return violations;
            });
        }
    }

    private void createConstraints(Session session) {
        session.run("CREATE CONSTRAINT counter_name_unique IF NOT EXISTS FOR (c:Counter) REQUIRE c.name IS UNIQUE").consume();
        session.run("CREATE CONSTRAINT account_id IF NOT EXISTS FOR (n:Account) REQUIRE n.id IS UNIQUE").consume();
        session.run("CREATE CONSTRAINT account_username_unique IF NOT EXISTS FOR (n:Account) REQUIRE n.username IS UNIQUE").consume();
        session.run("CREATE CONSTRAINT account_email_unique IF NOT EXISTS FOR (n:Account) REQUIRE n.email IS UNIQUE").consume();
        session.run("CREATE CONSTRAINT chapter_id IF NOT EXISTS FOR (n:Chapter) REQUIRE n.id IS UNIQUE").consume();
        session.run("CREATE CONSTRAINT scene_id IF NOT EXISTS FOR (n:Scene) REQUIRE n.id IS UNIQUE").consume();
        session.run("CREATE CONSTRAINT choice_id IF NOT EXISTS FOR (n:Choice) REQUIRE n.id IS UNIQUE").consume();
        session.run("CREATE CONSTRAINT quest_id IF NOT EXISTS FOR (n:Quest) REQUIRE n.id IS UNIQUE").consume();
        session.run("CREATE CONSTRAINT item_id IF NOT EXISTS FOR (n:Item) REQUIRE n.id IS UNIQUE").consume();
        session.run("CREATE CONSTRAINT npc_id IF NOT EXISTS FOR (n:Npc) REQUIRE n.id IS UNIQUE").consume();
        session.run("CREATE CONSTRAINT race_details_id IF NOT EXISTS FOR (n:RaceDetails) REQUIRE n.id IS UNIQUE").consume();
        session.run("CREATE CONSTRAINT character_id IF NOT EXISTS FOR (n:Character) REQUIRE n.id IS UNIQUE").consume();
        session.run("CREATE CONSTRAINT character_path_id IF NOT EXISTS FOR (n:CharacterPath) REQUIRE n.id IS UNIQUE").consume();
        session.run("CREATE CONSTRAINT inventory_id IF NOT EXISTS FOR (n:Inventory) REQUIRE n.id IS UNIQUE").consume();
    }

    private void syncAccountCounter(TransactionContext tx) {
        tx.run("""
                MATCH (a:Account)
                WITH coalesce(max(a.id), 0) AS maxAccountId
                MERGE (counter:Counter {name: 'account'})
                SET counter.value = maxAccountId
                """).consume();
    }

    private void migrateNodes(
            TransactionContext tx,
            List<Account> accounts,
            List<Chapter> chapters,
            List<Scene> scenes,
            List<Choice> choices,
            List<Quest> quests,
            List<Item> items,
            List<Npc> npcs,
            List<RaceDetails> raceDetails,
            List<CharacterAvatar> characters,
            List<CharacterPath> characterPaths,
            List<Inventory> inventories
    ) {
        for (Account account : accounts) {
            tx.run(
                    "MERGE (a:Account {id: $id}) " +
                            "SET a.username = $username, a.password = $password, a.characterLimit = $characterLimit, " +
                            "a.email = $email, a.role = $role",
                    params(
                            "id", account.getId(),
                            "username", account.getUsername(),
                            "password", account.getPassword(),
                            "characterLimit", account.getCharacterLimit(),
                            "email", account.getEmail(),
                            "role", account.getRole() == null ? null : account.getRole().name()
                    )
            ).consume();
        }

        for (Chapter chapter : chapters) {
            tx.run(
                    "MERGE (c:Chapter {id: $id}) SET c.name = $name",
                    params("id", chapter.getId(), "name", chapter.getName())
            ).consume();
        }

        for (Scene scene : scenes) {
            tx.run(
                    "MERGE (s:Scene {id: $id}) SET s.name = $name",
                    params("id", scene.getId(), "name", scene.getName())
            ).consume();
            tx.run(
                    "MATCH (c:Chapter {id: $chapterId}), (s:Scene {id: $sceneId}) " +
                            "MERGE (c)-[:HAS_SCENE]->(s)",
                    params("chapterId", scene.getChapter().getId(), "sceneId", scene.getId())
            ).consume();
        }

        for (Choice choice : choices) {
            tx.run(
                    "MERGE (c:Choice {id: $id}) " +
                            "SET c.description = $description, c.consequence = $consequence, c.targetId = $targetId, " +
                            "c.valueInt = $valueInt, c.storyWeight = $storyWeight, c.requirements = $requirements",
                    params(
                            "id", choice.getId(),
                            "description", choice.getDescription(),
                            "consequence", choice.getConsequence(),
                            "targetId", choice.getTargetId(),
                            "valueInt", choice.getValueInt(),
                            "storyWeight", choice.getStoryWeight() == null ? null : choice.getStoryWeight().intValue(),
                            "requirements", choice.getRequirements()
                    )
            ).consume();
            tx.run(
                    "MATCH (s:Scene {id: $sceneId}), (c:Choice {id: $choiceId}) " +
                            "MERGE (s)-[:HAS_CHOICE]->(c)",
                    params("sceneId", choice.getScene().getId(), "choiceId", choice.getId())
            ).consume();

            if (choice.getDestinationScene() != null) {
                tx.run(
                        "MATCH (c:Choice {id: $choiceId}), (s:Scene {id: $destinationSceneId}) " +
                                "MERGE (c)-[:LEADS_TO]->(s)",
                        params("choiceId", choice.getId(), "destinationSceneId", choice.getDestinationScene().getId())
                ).consume();
            }
        }

        for (Quest quest : quests) {
            tx.run(
                    "MERGE (q:Quest {id: $id}) SET q.description = $description",
                    params("id", quest.getId(), "description", quest.getDescription())
            ).consume();
            tx.run(
                    "MATCH (s:Scene {id: $sceneId}), (q:Quest {id: $questId}) " +
                            "MERGE (s)-[:HAS_QUEST]->(q)",
                    params("sceneId", quest.getScene().getId(), "questId", quest.getId())
            ).consume();
        }

        for (Item item : items) {
            tx.run(
                    "MERGE (i:Item {id: $id}) SET i.name = $name, i.description = $description, i.type = $type",
                    params("id", item.getId(), "name", item.getName(), "description", item.getDescription(), "type", item.getType())
            ).consume();
        }

        for (RaceDetails detail : raceDetails) {
            tx.run("MERGE (r:RaceDetails {id: $id}) SET r.name = $name", params("id", detail.getId(), "name", detail.getName())).consume();
        }

        for (Npc npc : npcs) {
            tx.run(
                    "MERGE (n:Npc {id: $id}) SET n.name = $name",
                    params("id", npc.getId(), "name", npc.getName())
            ).consume();
            tx.run(
                    "MATCH (n:Npc {id: $npcId}), (r:RaceDetails {id: $raceDetailsId}) " +
                            "MERGE (n)-[:HAS_RACE]->(r)",
                    params("npcId", npc.getId(), "raceDetailsId", npc.getRaceDetails().getId())
            ).consume();
        }

        for (CharacterAvatar character : characters) {
            tx.run(
                    "MERGE (c:Character {id: $id}) SET c.name = $name, c.flag = $flag",
                    params("id", character.getId(), "name", character.getName(), "flag", character.getFlag())
            ).consume();
            tx.run(
                    "MATCH (a:Account {id: $accountId}), (c:Character {id: $characterId}) " +
                            "MERGE (a)-[:OWNS_CHARACTER]->(c)",
                    params("accountId", character.getAccount().getId(), "characterId", character.getId())
            ).consume();
            tx.run(
                    "MATCH (c:Character {id: $characterId}), (ch:Chapter {id: $chapterId}) " +
                            "MERGE (c)-[:IN_CHAPTER]->(ch)",
                    params("characterId", character.getId(), "chapterId", character.getChapter().getId())
            ).consume();
            tx.run(
                    "MATCH (c:Character {id: $characterId}), (s:Scene {id: $sceneId}) " +
                            "MERGE (c)-[:CURRENT_SCENE]->(s)",
                    params("characterId", character.getId(), "sceneId", character.getScene().getId())
            ).consume();
            tx.run(
                    "MATCH (c:Character {id: $characterId}), (r:RaceDetails {id: $raceDetailsId}) " +
                            "MERGE (c)-[:HAS_RACE]->(r)",
                    params("characterId", character.getId(), "raceDetailsId", character.getRaceDetails().getId())
            ).consume();
        }

        for (CharacterPath characterPath : characterPaths) {
            tx.run(
                    "MERGE (p:CharacterPath {id: $id}) SET p.summary = $summary",
                    params("id", characterPath.getId(), "summary", characterPath.getSummary())
            ).consume();
            tx.run(
                    "MATCH (c:Character {id: $characterId}), (p:CharacterPath {id: $pathId}) " +
                            "MERGE (c)-[:HAS_PATH]->(p)",
                    params("characterId", characterPath.getCharacter().getId(), "pathId", characterPath.getId())
            ).consume();
        }

        for (Inventory inventory : inventories) {
            tx.run("MERGE (i:Inventory {id: $id})", params("id", inventory.getId())).consume();
            tx.run(
                    "MATCH (c:Character {id: $characterId}), (i:Inventory {id: $inventoryId}) " +
                            "MERGE (c)-[:HAS_INVENTORY]->(i)",
                    params("characterId", inventory.getCharacter().getId(), "inventoryId", inventory.getId())
            ).consume();
        }
    }

    private void migrateCharacterDetails(TransactionContext tx, List<CharacterDetails> characterDetails) {
        for (CharacterDetails details : characterDetails) {
            tx.run(
                    "MATCH (c:Character {id: $characterId}) " +
                            "SET c.intelligence = $intelligence, c.charisma = $charisma, c.fashion = $fashion",
                    params(
                            "characterId", details.getCharacterId(),
                            "intelligence", details.getIntelligence(),
                            "charisma", details.getCharisma(),
                            "fashion", details.getFashion()
                    )
            ).consume();
        }
    }

    private void migrateRelationships(
            TransactionContext tx,
            List<CharacterHasQuest> characterQuests,
            List<CharacterPathChoice> characterPathChoices,
            List<ChoiceHasItem> choiceItems,
            List<Equipment> equipment,
            List<InventoryHasItem> inventoryItems,
            List<QuestHasItem> questItems,
            List<SceneHasNpc> sceneNpcs
    ) {
        for (CharacterHasQuest characterQuest : characterQuests) {
            tx.run(
                    "MATCH (c:Character {id: $characterId}), (q:Quest {id: $questId}) " +
                            "MERGE (c)-[r:HAS_QUEST]->(q) SET r.status = $status",
                    params(
                            "characterId", characterQuest.getCharacter().getId(),
                            "questId", characterQuest.getQuest().getId(),
                            "status", characterQuest.getStatus() == null ? null : characterQuest.getStatus().intValue()
                    )
            ).consume();
        }

        for (CharacterPathChoice characterPathChoice : characterPathChoices) {
            tx.run(
                    "MATCH (p:CharacterPath {id: $pathId}), (c:Choice {id: $choiceId}) " +
                            "MERGE (p)-[:INCLUDES_CHOICE]->(c)",
                    params(
                            "pathId", characterPathChoice.getCharacterPath().getId(),
                            "choiceId", characterPathChoice.getChoice().getId()
                    )
            ).consume();
        }

        for (ChoiceHasItem choiceItem : choiceItems) {
            tx.run(
                    "MATCH (c:Choice {id: $choiceId}), (i:Item {id: $itemId}) " +
                            "MERGE (c)-[:AFFECTS_ITEM]->(i)",
                    params("choiceId", choiceItem.getChoice().getId(), "itemId", choiceItem.getItem().getId())
            ).consume();
        }

        for (QuestHasItem questItem : questItems) {
            tx.run(
                    "MATCH (q:Quest {id: $questId}), (i:Item {id: $itemId}) " +
                            "MERGE (q)-[:INVOLVES_ITEM]->(i)",
                    params("questId", questItem.getQuest().getId(), "itemId", questItem.getItem().getId())
            ).consume();
        }

        for (SceneHasNpc sceneNpc : sceneNpcs) {
            tx.run(
                    "MATCH (s:Scene {id: $sceneId}), (n:Npc {id: $npcId}) " +
                            "MERGE (s)-[:HAS_NPC]->(n)",
                    params("sceneId", sceneNpc.getScene().getId(), "npcId", sceneNpc.getNpc().getId())
            ).consume();
        }

        for (InventoryHasItem inventoryItem : inventoryItems) {
            tx.run(
                    "MATCH (i:Inventory {id: $inventoryId}), (item:Item {id: $itemId}) " +
                            "MERGE (i)-[r:CONTAINS]->(item) SET r.amount = $amount",
                    params(
                            "inventoryId", inventoryItem.getInventory().getId(),
                            "itemId", inventoryItem.getItem().getId(),
                            "amount", inventoryItem.getAmount()
                    )
            ).consume();
        }

        for (Equipment row : equipment) {
            if (row.getHead() != null) {
                tx.run(
                        "MATCH (c:Character {id: $characterId}), (i:Item {id: $itemId}) " +
                                "MERGE (c)-[:EQUIPPED_HEAD]->(i)",
                        params("characterId", row.getCharacterId(), "itemId", row.getHead().getId())
                ).consume();
            }
            if (row.getChest() != null) {
                tx.run(
                        "MATCH (c:Character {id: $characterId}), (i:Item {id: $itemId}) " +
                                "MERGE (c)-[:EQUIPPED_CHEST]->(i)",
                        params("characterId", row.getCharacterId(), "itemId", row.getChest().getId())
                ).consume();
            }
            if (row.getLegs() != null) {
                tx.run(
                        "MATCH (c:Character {id: $characterId}), (i:Item {id: $itemId}) " +
                                "MERGE (c)-[:EQUIPPED_LEGS]->(i)",
                        params("characterId", row.getCharacterId(), "itemId", row.getLegs().getId())
                ).consume();
            }
        }
    }

    private Map<String, Object> params(Object... values) {
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            params.put((String) values[i], values[i + 1]);
        }
        return params;
    }
}
