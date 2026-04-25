package dk.ek.gruppe2.chooseyourfate.repository.mongodb;

import dk.ek.gruppe2.chooseyourfate.model.mongodb.RaceDetailsDocumentMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RaceDetailsRepositoryMongo extends MongoRepository<RaceDetailsDocumentMongo, String> {
}
