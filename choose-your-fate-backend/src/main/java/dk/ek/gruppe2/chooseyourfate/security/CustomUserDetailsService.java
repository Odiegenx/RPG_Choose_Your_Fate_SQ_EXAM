package dk.ek.gruppe2.chooseyourfate.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import dk.ek.gruppe2.chooseyourfate.enums.DataSourceType;
import dk.ek.gruppe2.chooseyourfate.model.mongodb.AccountDocumentMongo;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Account;
import dk.ek.gruppe2.chooseyourfate.model.neo4j.AccountNode;
import dk.ek.gruppe2.chooseyourfate.repository.mongodb.AccountRepositoryMongo;
import dk.ek.gruppe2.chooseyourfate.repository.mysql.AccountRepository;
import dk.ek.gruppe2.chooseyourfate.repository.neo4j.AccountNodeRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository repo;
    private final AccountRepositoryMongo repoMongo;
    private final AccountNodeRepository repoNeo4J;

    public CustomUserDetailsService(AccountRepository repo, AccountNodeRepository repoNeo4J, AccountRepositoryMongo repoMongo) {
        this.repo = repo;
        this.repoMongo = repoMongo;
        this.repoNeo4J = repoNeo4J;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        Account acc = repo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("SQL Not found"));

        AccountDocumentMongo accMongo = repoMongo.findByUsername(username).orElse(null);

        // Optional<AccountNode> accNeo = repoNeo4J.findByUsername(username);
        //     // .orElseThrow(() ->{ 

        //     //     return new UsernameNotFoundException("Neo4J Not found");});
        
        // System.out.println("Neo: " + accNeo.toString());

        return new CustomUserDetails(acc, accMongo);
    }
}