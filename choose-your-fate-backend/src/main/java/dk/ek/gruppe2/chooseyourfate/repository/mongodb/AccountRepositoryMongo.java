package dk.ek.gruppe2.chooseyourfate.repository.mongodb;

import dk.ek.gruppe2.chooseyourfate.model.mongodb.AccountDocumentMongo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepositoryMongo extends MongoRepository<AccountDocumentMongo, String> {
    Optional<AccountDocumentMongo> findByUsername(String username);

}
