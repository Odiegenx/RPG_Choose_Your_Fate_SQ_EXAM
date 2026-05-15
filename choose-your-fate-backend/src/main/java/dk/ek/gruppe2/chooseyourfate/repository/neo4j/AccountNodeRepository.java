package dk.ek.gruppe2.chooseyourfate.repository.neo4j;

import java.util.List;
import java.util.Optional;

public interface AccountNodeRepository {

    List<AccountData> findAllAccountData();

    Optional<AccountData> findAccountDataById(Integer id);

    Optional<AccountSnapshot> findAccountSnapshotById(Integer id);

    Optional<Integer> findAccountIdByUsername(String username);

    Optional<Integer> findAccountIdByEmail(String email);

    Optional<AccountData> createAccount(CreateAccountData toCreate);

    Optional<AccountData> updateAccount(UpdateAccountData toUpdate);

    int deleteAccountById(Integer id);

    record AccountData(
            Integer id,
            String username,
            Integer characterLimit,
            String email
    ) {
    }

    record CreateAccountData(
            String username,
            String email,
            Integer characterLimit,
            String password,
            String role
    ) {
    }

    record UpdateAccountData(
            Integer id,
            String username,
            String email,
            Integer characterLimit,
            String password
    ) {
    }

    record AccountSnapshot(
            Integer id,
            String username,
            Integer characterLimit,
            String email,
            String password,
            String role
    ) {
    }
}
