package dk.ek.gruppe2.chooseyourfate.service.migration.mongodb;

import dk.ek.gruppe2.chooseyourfate.exception.ResourceNotFoundException;
import dk.ek.gruppe2.chooseyourfate.model.mongodb.*;
import dk.ek.gruppe2.chooseyourfate.model.mysql.*;
import dk.ek.gruppe2.chooseyourfate.repository.mongodb.CharacterAvatarRepositoryMongo;
import dk.ek.gruppe2.chooseyourfate.repository.mongodb.SceneRepositoryMongo;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.*;
import dk.ek.gruppe2.chooseyourfate.service.migration.IdMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CharacterAvatarMigrationServiceMongo {

    private final CharacterAvatarRepository mysqlRepo;
    private final CharacterAvatarRepositoryMongo mongoRepo;
    private final CharacterDetailsRepository characterDetailsRepository;
    private final EquipmentRepository equipmentRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryHasItemRepository inventoryHasItemRepository;
    private final CharacterPathRepository characterPathRepository;
    private final CharacterPathChoiceRepository characterPathChoiceRepository;
    private final CharacterHasQuestRepository characterHasQuestRepository;
    private final SceneRepositoryMongo sceneMongoRepo;
    private final IdMappingService idMappingService;

    public void migrate() {
        log.info("Starting Character migration...");

        List<CharacterAvatar> entities = mysqlRepo.findAll();

        for (CharacterAvatar entity : entities) {

            // resolve references
            String mongoAccountId = idMappingService.get(
                    CollectionNames.ACCOUNTS,
                    entity.getAccount().getId()
            );
            String mongoRaceDetailId = idMappingService.get(
                    CollectionNames.RACE_DETAILS,
                    entity.getRaceDetails().getId()
            );
            String mongoChapterId = idMappingService.get(
                    CollectionNames.CHAPTERS,
                    entity.getChapter().getId()
            );
            String mongoSceneId = idMappingService.get(
                    CollectionNames.SCENES,
                    entity.getScene().getId()
            );

            // transform embedded objects
            CharacterDetailsMongo details = transformDetails(entity.getId());
            EquipmentMongo equipment = transformEquipment(entity.getId());
            InventoryMongo inventory = transformInventory(entity.getId());
            CharacterPathMongo path = transformPath(entity.getId());
            List<CharacterQuestMongo> quests = transformQuests(entity.getId());

            CharacterAvatarDocumentMongo doc = CharacterAvatarDocumentMongo.builder()
                    .accountId(mongoAccountId)
                    .raceDetailId(mongoRaceDetailId)
                    .chapterId(mongoChapterId)
                    .sceneId(mongoSceneId)
                    .name(entity.getName())
                    .flag(entity.getFlag())
                    .details(details)
                    .equipment(equipment)
                    .inventory(inventory)
                    .path(path)
                    .characterQuests(quests)
                    .build();

            mongoRepo.save(doc);

            idMappingService.put(CollectionNames.CHARACTERS, entity.getId(), doc.getId());
        }

        log.info("Migrated {} characters", entities.size());
    }

    private CharacterDetailsMongo transformDetails(Integer characterId) {
        return characterDetailsRepository.findById(characterId)
                .map(d -> CharacterDetailsMongo.builder()
                        .intelligence(d.getIntelligence())
                        .charisma(d.getCharisma())
                        .fashion(d.getFashion())
                        .build())
                .orElse(null);
    }

    private EquipmentMongo transformEquipment(Integer characterId) {
        return equipmentRepository.findById(characterId)
                .map(e -> EquipmentMongo.builder()
                        .headItemId(e.getHead() != null
                                ? idMappingService.get(CollectionNames.ITEMS, e.getHead().getId())
                                : null)
                        .legsItemId(e.getLegs() != null
                                ? idMappingService.get(CollectionNames.ITEMS, e.getLegs().getId())
                                : null)
                        .chestItemId(e.getChest() != null
                                ? idMappingService.get(CollectionNames.ITEMS, e.getChest().getId())
                                : null)
                        .build())
                .orElse(null);
    }

    private InventoryMongo transformInventory(Integer characterId) {
        Inventory inv = inventoryRepository.findByCharacter_Id(characterId);

        List<InventoryEntryMongo> entries = inventoryHasItemRepository
                .findByInventory_Id(inv.getId())
                .stream()
                .map(ihi -> InventoryEntryMongo.builder()
                        .itemId(idMappingService.get(CollectionNames.ITEMS, ihi.getItem().getId()))
                        .amount(ihi.getAmount())
                        .build())
                .toList();

        return InventoryMongo.builder()
                .inventoryEntries(entries)
                .build();
    }

    private CharacterPathMongo transformPath(Integer characterId) {
        CharacterPath path = characterPathRepository.findByCharacter_Id(characterId);
        List<ChoiceMadeMongo> choicesMade = characterPathChoiceRepository
                .findByCharacterPath_Id(path.getId())
                .stream()
                .map(characterPathChoice -> {
                    String mongoSceneId = idMappingService.get(
                            CollectionNames.SCENES,
                            characterPathChoice.getChoice().getScene().getId()
                    );
                    SceneDocumentMongo scene = sceneMongoRepo.findById(mongoSceneId).orElseThrow();

                    ChoiceMongo choice = scene.getChoices().stream().filter(
                            characterPath -> characterPath.getId().equals(
                                    characterPathChoice.getChoice().getId().toString())
                                    ).findFirst().orElseThrow();
                    return ChoiceMadeMongo.builder().description(choice.getDescription()).consequence(choice.getConsequence()).storyWeight(choice.getStoryWeight()).destinationSceneId(choice.getDestinationId()).build();

                }).toList();
        return CharacterPathMongo.builder().summary(path.getSummary()).choicesMade(choicesMade).build();
    }

    private List<CharacterQuestMongo> transformQuests(Integer characterId) {
        return characterHasQuestRepository.findByCharacter_Id(characterId)
                .stream()
                .map(chq -> CharacterQuestMongo.builder()
                        .questId(idMappingService.get(CollectionNames.QUESTS, chq.getQuest().getId()))
                        .status(chq.getStatus() == (byte) 1)   // Byte → boolean   // TINYINT → boolean
                        .build())
                .toList();
    }

    public void dropCollection() {
        log.info("dropping collection characters");
        mongoRepo.deleteAll();
    }
}