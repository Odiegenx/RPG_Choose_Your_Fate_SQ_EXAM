package dk.ek.gruppe2.chooseyourfate.service.migration.mongodb;

import dk.ek.gruppe2.chooseyourfate.model.mongodb.ChoiceMongo;
import dk.ek.gruppe2.chooseyourfate.model.mongodb.SceneDocumentMongo;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Scene;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Choice;
import dk.ek.gruppe2.chooseyourfate.repository.mongodb.SceneRepositoryMongo;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.ChoiceHasItemRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.SceneHasNpcRepository;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.SceneRepository;
import dk.ek.gruppe2.chooseyourfate.service.migration.IdMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SceneMigrationServiceMongo {

    private final SceneRepository mysqlRepo;
    private final SceneRepositoryMongo mongoRepo;
    private final SceneHasNpcRepository sceneHasNpcRepository;
    private final ChoiceHasItemRepository choiceHasItemRepository;
    private final IdMappingService idMappingService;

    // FIRST PASS — migrate scenes with embedded choices and npcIds
    // questIds left empty because quests haven't been migrated yet
    public void migrateFirstPass() {
        log.info("Starting Scene migration (first pass)...");

        List<Scene> entities = mysqlRepo.findAll();

        for (Scene entity : entities) {

            // resolve chapter ID
            String mongoChapterId = idMappingService.get(
                    CollectionNames.CHAPTERS,
                    entity.getChapter().getId()
            );

            // resolve npc IDs via join table
            List<String> npcIds = sceneHasNpcRepository.findBySceneId(entity.getId())
                    .stream()
                    .map(sceneHasNpc -> idMappingService.get(
                            CollectionNames.NPCS,
                            sceneHasNpc.getNpc().getId()
                    ))
                    .toList();

            // transform embedded choices — destinationSceneId left null for second pass
            List<ChoiceMongo> choices = entity.getChoices()
                    .stream()
                    .map(this::transformChoice)
                    .toList();

            SceneDocumentMongo doc = SceneDocumentMongo.builder()
                    .chapterId(mongoChapterId)
                    .name(entity.getName())
                    .choices(choices)
                    .npcIds(npcIds)
                    .questIds(new ArrayList<>())     // empty, resolved in second pass
                    .build();

            mongoRepo.save(doc);

            idMappingService.put(CollectionNames.SCENES, entity.getId(), doc.getId());
        }

        log.info("Migrated {} scenes (first pass)", entities.size());
    }

    // SECOND PASS — resolve questIds and destinationSceneId on choices
    // Both quests and all scenes now exist in MongoDB so we can resolve all references
    public void migrateSecondPass() {
        log.info("Starting Scene migration (second pass)...");

        List<Scene> entities = mysqlRepo.findAll();

        for (Scene entity : entities) {

            // fetch the scene document we saved in first pass
            String mongoSceneId = idMappingService.get(CollectionNames.SCENES, entity.getId());
            SceneDocumentMongo doc = mongoRepo.findById(mongoSceneId).orElseThrow();

            // resolve quest IDs
            List<String> questIds = entity.getQuests()
                    .stream()
                    .map(quest -> idMappingService.get(CollectionNames.QUESTS, quest.getId()))
                    .toList();

            // resolve destinationSceneId on each embedded choice
            List<ChoiceMongo> updatedChoices = entity.getChoices()
                    .stream()
                    .map(choiceEntity -> {
                        // find the matching choice we saved in first pass by its original MySQL id
                        ChoiceMongo choice = doc.getChoices().stream()
                                .filter(c -> c.getId().equals(choiceEntity.getId().toString()))
                                .findFirst()
                                .orElseThrow();

                        // resolve destination scene if it has one
                        if (choiceEntity.getDestinationScene() != null) {
                            choice.setDestinationId(idMappingService.get(
                                    CollectionNames.SCENES,
                                    choiceEntity.getDestinationScene().getId()
                            ));
                        }

                        return choice;
                    })
                    .toList();

            doc.setQuestIds(questIds);
            doc.setChoices(updatedChoices);

            mongoRepo.save(doc);
        }

        log.info("Updated {} scenes (second pass)", entities.size());
    }

    private ChoiceMongo transformChoice(Choice entity) {

        // resolve item IDs via join table
        List<String> itemIds = choiceHasItemRepository.findByChoice_Id(entity.getId())
                .stream()
                .map(choiceHasItem -> idMappingService.get(
                        CollectionNames.ITEMS,
                        choiceHasItem.getItem().getId()
                ))
                .toList();

        return ChoiceMongo.builder()
                .id(entity.getId().toString())          // keep original ID for CharacterPath snapshots
                .description(entity.getDescription())
                .consequence(entity.getConsequence())
                .destinationId(null)               // resolved in second pass
                .targetId(entity.getTargetId())
                .value(entity.getValueInt())
                .storyWeight(entity.getStoryWeight())
                .requirements(entity.getRequirements())
                .itemIds(itemIds)
                .build();
    }

    public void dropCollection() {
        log.info("dropping collection scenes");
        mongoRepo.deleteAll();
    }
}
