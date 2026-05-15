package dk.ek.gruppe2.chooseyourfate.repository.neo4j;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AccountNodeRepositoryImpl implements AccountNodeRepository {

    private final Neo4jClient neo4jClient;

    public AccountNodeRepositoryImpl(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public List<AccountData> findAllAccountData() {
        return neo4jClient.query("""
                        MATCH (a:Account)
                        RETURN a.id AS id, a.username AS username, a.characterLimit AS characterLimit, a.email AS email
                        ORDER BY a.id
                        """)
                .fetchAs(AccountData.class)
                .mappedBy((typeSystem, accountRecord) -> new AccountData(
                        accountRecord.get("id").asInt(),
                        accountRecord.get("username").asString(),
                        accountRecord.get("characterLimit").asInt(),
                        accountRecord.get("email").asString()
                ))
                .all()
                .stream()
                .toList();
    }

    @Override
    public Optional<AccountData> findAccountDataById(Integer id) {
        return neo4jClient.query("""
                        MATCH (a:Account {id: $id})
                        RETURN a.id AS id, a.username AS username, a.characterLimit AS characterLimit, a.email AS email
                        """)
                .bind(id).to("id")
                .fetchAs(AccountData.class)
                .mappedBy((typeSystem, accountRecord) -> new AccountData(
                        accountRecord.get("id").asInt(),
                        accountRecord.get("username").asString(),
                        accountRecord.get("characterLimit").asInt(),
                        accountRecord.get("email").asString()
                ))
                .one();
    }

    @Override
    public Optional<AccountSnapshot> findAccountSnapshotById(Integer id) {
        return neo4jClient.query("""
                        MATCH (a:Account {id: $id})
                        RETURN a.id AS id,
                               a.username AS username,
                               a.characterLimit AS characterLimit,
                               a.email AS email,
                               a.password AS password,
                               a.role AS role
                        """)
                .bind(id).to("id")
                .fetchAs(AccountSnapshot.class)
                .mappedBy((typeSystem, accountRecord) -> new AccountSnapshot(
                        accountRecord.get("id").asInt(),
                        accountRecord.get("username").asString(),
                        accountRecord.get("characterLimit").asInt(),
                        accountRecord.get("email").asString(),
                        accountRecord.get("password").asString(),
                        accountRecord.get("role").asString()
                ))
                .one();
    }

    @Override
    public Optional<Integer> findAccountIdByUsername(String username) {
        return neo4jClient.query("""
                        MATCH (a:Account {username: $username})
                        RETURN a.id AS id
                        LIMIT 1
                        """)
                .bind(username).to("username")
                .fetchAs(Integer.class)
                .mappedBy((typeSystem, accountRecord) -> accountRecord.get("id").asInt())
                .one();
    }

    @Override
    public Optional<Integer> findAccountIdByEmail(String email) {
        return neo4jClient.query("""
                        MATCH (a:Account {email: $email})
                        RETURN a.id AS id
                        LIMIT 1
                        """)
                .bind(email).to("email")
                .fetchAs(Integer.class)
                .mappedBy((typeSystem, accountRecord) -> accountRecord.get("id").asInt())
                .one();
    }

    @Override
    public Optional<AccountData> createAccount(CreateAccountData toCreate) {
        return neo4jClient.query("""
                        MERGE (counter:Counter {name: 'account'})
                        ON CREATE SET counter.value = 0
                        SET counter.value = counter.value + 1
                        CREATE (a:Account {
                            id: counter.value,
                            username: $username,
                            email: $email,
                            characterLimit: $characterLimit,
                            password: $password,
                            role: $role
                        })
                        RETURN a.id AS id, a.username AS username, a.characterLimit AS characterLimit, a.email AS email
                        """)
                .bind(toCreate.username()).to("username")
                .bind(toCreate.email()).to("email")
                .bind(toCreate.characterLimit()).to("characterLimit")
                .bind(toCreate.password()).to("password")
                .bind(toCreate.role()).to("role")
                .fetchAs(AccountData.class)
                .mappedBy((typeSystem, accountRecord) -> new AccountData(
                        accountRecord.get("id").asInt(),
                        accountRecord.get("username").asString(),
                        accountRecord.get("characterLimit").asInt(),
                        accountRecord.get("email").asString()
                ))
                .one();
    }

    @Override
    public Optional<AccountData> updateAccount(UpdateAccountData toUpdate) {
        return neo4jClient.query("""
                        MATCH (a:Account {id: $id})
                        SET a.username = $username,
                            a.email = $email,
                            a.characterLimit = $characterLimit,
                            a.password = $password
                        RETURN a.id AS id, a.username AS username, a.characterLimit AS characterLimit, a.email AS email
                        """)
                .bind(toUpdate.id()).to("id")
                .bind(toUpdate.username()).to("username")
                .bind(toUpdate.email()).to("email")
                .bind(toUpdate.characterLimit()).to("characterLimit")
                .bind(toUpdate.password()).to("password")
                .fetchAs(AccountData.class)
                .mappedBy((typeSystem, accountRecord) -> new AccountData(
                        accountRecord.get("id").asInt(),
                        accountRecord.get("username").asString(),
                        accountRecord.get("characterLimit").asInt(),
                        accountRecord.get("email").asString()
                ))
                .one();
    }

    @Override
    public int deleteAccountById(Integer id) {
        return neo4jClient.query("""
                        MATCH (a:Account {id: $id})
                        WITH count(a) AS matches, collect(a) AS accounts
                        FOREACH (accountToDelete IN accounts | DETACH DELETE accountToDelete)
                        RETURN matches AS deletedCount
                        """)
                .bind(id).to("id")
                .fetchAs(Integer.class)
                .mappedBy((typeSystem, deleteRecord) -> deleteRecord.get("deletedCount").asInt())
                .one()
                .orElse(0);
    }
}
