package dk.ek.gruppe2.chooseyourfate.repository.mongodb;

import dk.ek.gruppe2.chooseyourfate.model.mongodb.NpcDocumentMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NpcRepositoryMongo extends MongoRepository<NpcDocumentMongo, String> {
}
