package dk.ek.gruppe2.chooseyourfate.service.neo4j;

import dk.ek.gruppe2.chooseyourfate.dto.AccountResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.CreateAccountRequestDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateAccountRequestDTO;
import dk.ek.gruppe2.chooseyourfate.exception.DuplicateResourceException;
import dk.ek.gruppe2.chooseyourfate.exception.ResourceNotFoundException;
import dk.ek.gruppe2.chooseyourfate.interfaces.AccountDataAccess;
import dk.ek.gruppe2.chooseyourfate.model.neo4j.AccountNode;
import dk.ek.gruppe2.chooseyourfate.repository.neo4j.AccountNodeRepository;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Neo4jAccountService implements AccountDataAccess {

    private final AccountNodeRepository accountNodeRepository;
    private final PasswordEncoder encoder;

    private final Driver neo4jDriver;

    public Neo4jAccountService(AccountNodeRepository accountNodeRepository, PasswordEncoder encoder, Driver neo4jDriver) {
        this.accountNodeRepository = accountNodeRepository;
        this.encoder = encoder;
        this.neo4jDriver = neo4jDriver;
    }

//    @Override
//    public List<AccountResponseDTO> getAllAccounts() {
//        return accountNodeRepository.findAll()
//                .stream()
//                .map(this::toDto)
//                .toList();
//    }

    @Override
    public List<AccountResponseDTO> getAllAccounts() {
        try (Session session = neo4jDriver.session()) {
            return session.executeRead(tx ->
                    tx.run("""
                MATCH (a:Account)
                RETURN a.id AS id, a.username AS username, a.characterLimit AS characterLimit, a.email AS email
                ORDER BY a.id
            """).list(accountFound -> new AccountResponseDTO(
                            accountFound.get("id").asInt(),
                            accountFound.get("username").asString(),
                            accountFound.get("characterLimit").asInt(),
                            accountFound.get("email").asString()
                    ))
            );
        }

    }

    @Override
    public AccountResponseDTO getAccountById(Integer id) {
        return toDto(getAccountNode(id));
    }

    @Override
    public AccountResponseDTO createAccount(CreateAccountRequestDTO request) {
        ensureUniqueUsername(request.getUsername(), null);
        ensureUniqueEmail(request.getEmail(), null);

        AccountNode accountNode = new AccountNode();
        accountNode.setId(accountNodeRepository.findNextId());
        accountNode.setUsername(request.getUsername());
        accountNode.setEmail(request.getEmail());
        accountNode.setCharacterLimit(3);
        accountNode.setPassword(encoder.encode(request.getPassword()));
        accountNode.setRole(request.toEntity().getRole());

        return toDto(accountNodeRepository.save(accountNode));
    }

    @Override
    public AccountResponseDTO updateAccount(Integer id, UpdateAccountRequestDTO request) {
        AccountNode accountNode = getAccountNode(id);

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            ensureUniqueUsername(request.getUsername(), id);
            accountNode.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            ensureUniqueEmail(request.getEmail(), id);
            accountNode.setEmail(request.getEmail());
        }

        if (request.getCharacterLimit() != null) {
            accountNode.setCharacterLimit(request.getCharacterLimit());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            accountNode.setPassword(encoder.encode(request.getPassword()));
        }

        return toDto(accountNodeRepository.save(accountNode));
    }

    @Override
    public void deleteAccount(Integer id) {
        if (!accountNodeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }
        accountNodeRepository.deleteById(id);
    }

    private AccountNode getAccountNode(Integer id) {
        return accountNodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }

    private void ensureUniqueUsername(String username, Integer currentId) {
        accountNodeRepository.findByUsername(username)
                .filter(account -> !account.getId().equals(currentId))
                .ifPresent(account -> {
                    throw new DuplicateResourceException("Username already exists");
                });
    }

    private void ensureUniqueEmail(String email, Integer currentId) {
        accountNodeRepository.findByEmail(email)
                .filter(account -> !account.getId().equals(currentId))
                .ifPresent(account -> {
                    throw new DuplicateResourceException("Email already exists");
                });
    }

    private AccountResponseDTO toDto(AccountNode accountNode) {
        return new AccountResponseDTO(
                accountNode.getId(),
                accountNode.getUsername(),
                accountNode.getCharacterLimit(),
                accountNode.getEmail()
        );
    }
}
