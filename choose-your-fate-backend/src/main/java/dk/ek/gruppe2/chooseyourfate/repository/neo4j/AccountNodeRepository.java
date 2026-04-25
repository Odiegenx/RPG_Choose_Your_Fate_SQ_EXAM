package dk.ek.gruppe2.chooseyourfate.repository.neo4j;

import dk.ek.gruppe2.chooseyourfate.model.neo4j.AccountNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface AccountNodeRepository extends Neo4jRepository<AccountNode, Integer> {

    Optional<AccountNode> findByUsername(String username);

    Optional<AccountNode> findByEmail(String email);

    @Query("MATCH (a:Account) RETURN coalesce(max(a.id), 0) + 1")
    Integer findNextId();
}
