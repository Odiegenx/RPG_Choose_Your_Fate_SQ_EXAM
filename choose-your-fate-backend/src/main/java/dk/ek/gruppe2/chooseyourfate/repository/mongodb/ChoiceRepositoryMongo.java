package dk.ek.gruppe2.chooseyourfate.repository.mongodb;

/**
 * {@code ChoiceMongo} is modeled as an embedded document within
 * {@code SceneDocumentMongo}, so it should not be exposed as a dedicated
 * Spring Data repository backed by its own MongoDB collection.
 */
public final class ChoiceRepositoryMongo {

    private ChoiceRepositoryMongo() {
    }
}
