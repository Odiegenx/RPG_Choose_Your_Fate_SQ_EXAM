package dk.ek.gruppe2.chooseyourfate.repository.mongodb;

import dk.ek.gruppe2.chooseyourfate.model.mongodb.CharacterAvatarDocumentMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterAvatarRepositoryMongo extends MongoRepository<CharacterAvatarDocumentMongo, String> {
    public List<CharacterAvatarDocumentMongo> findByAccountId(String accountId);
}
