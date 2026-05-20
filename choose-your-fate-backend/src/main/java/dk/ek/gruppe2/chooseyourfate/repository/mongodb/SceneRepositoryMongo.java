package dk.ek.gruppe2.chooseyourfate.repository.mongodb;

import dk.ek.gruppe2.chooseyourfate.dto.scene.CreateSceneRequestDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.SceneResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.scene.UpdateSceneRequestDTO;
import dk.ek.gruppe2.chooseyourfate.model.mongodb.SceneDocumentMongo;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SceneRepositoryMongo extends MongoRepository<SceneDocumentMongo, String> {

    @Override
    Optional<SceneDocumentMongo> findById(String id);

    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            "{ $unwind: { path: '$choices', preserveNullAndEmptyArrays: true } }",
            "{ $addFields: { 'choices.destinationIdObj': { $toObjectId: '$choices.destinationId' } } }",
            "{ $lookup: { from: 'scenes', localField: 'choices.destinationIdObj', foreignField: '_id', as: 'choices.destinationScene' } }",
            "{ $addFields: { 'choices.destinationScene': { $arrayElemAt: ['$choices.destinationScene', 0] } } }",
            "{ $group: { _id: '$_id', chapterId: { $first: '$chapterId' }, name: { $first: '$name' }, questIds: { $first: '$questIds' }, npcIds: { $first: '$npcIds' }, choices: { $push: '$choices' } } }"
    })
    Optional<SceneDocumentMongo> findByIdWithNextScenes(String id);

    List<SceneDocumentMongo> findAll();

    SceneDocumentMongo getSceneById(String id);

}
