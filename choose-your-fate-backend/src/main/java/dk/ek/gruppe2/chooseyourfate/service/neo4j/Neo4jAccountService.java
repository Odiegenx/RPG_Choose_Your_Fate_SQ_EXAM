package dk.ek.gruppe2.chooseyourfate.service.neo4j;

import dk.ek.gruppe2.chooseyourfate.dto.AccountResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.CreateAccountRequestDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateAccountRequestDTO;
import dk.ek.gruppe2.chooseyourfate.exception.DuplicateResourceException;
import dk.ek.gruppe2.chooseyourfate.exception.ResourceNotFoundException;
import dk.ek.gruppe2.chooseyourfate.interfaces.AccountDataAccess;
import dk.ek.gruppe2.chooseyourfate.repository.neo4j.AccountNodeRepository.AccountData;
import dk.ek.gruppe2.chooseyourfate.repository.neo4j.AccountNodeRepository;
import dk.ek.gruppe2.chooseyourfate.repository.neo4j.AccountNodeRepository.AccountSnapshot;
import dk.ek.gruppe2.chooseyourfate.repository.neo4j.AccountNodeRepository.CreateAccountData;
import dk.ek.gruppe2.chooseyourfate.repository.neo4j.AccountNodeRepository.UpdateAccountData;
import org.neo4j.driver.exceptions.ClientException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Neo4jAccountService implements AccountDataAccess {

    private final PasswordEncoder encoder;
    private final AccountNodeRepository accountRepository;

    public Neo4jAccountService(PasswordEncoder encoder, AccountNodeRepository accountRepository) {
        this.encoder = encoder;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<AccountResponseDTO> getAllAccounts() {
        return accountRepository.findAllAccountData()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public AccountResponseDTO getAccountById(Integer id) {
        return accountRepository.findAccountDataById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }

    @Override
    public AccountResponseDTO createAccount(CreateAccountRequestDTO request) {
        ensureUniqueUsername(request.getUsername(), null);
        ensureUniqueEmail(request.getEmail(), null);

        String encodedPassword = encoder.encode(request.getPassword());
        String role = request.toEntity().getRole().name();

        try {
            return accountRepository.createAccount(new CreateAccountData(
                            request.getUsername(),
                            request.getEmail(),
                            3,
                            encodedPassword,
                            role
                    ))
                    .map(this::toDto)
                    .orElseThrow(() -> new IllegalStateException("Failed to create account"));
        } catch (RuntimeException ex) {
            throw translateDuplicateConstraint(ex);
        }
    }

    @Override
    public AccountResponseDTO updateAccount(Integer id, UpdateAccountRequestDTO request) {
        AccountSnapshot currentAccount = accountRepository.findAccountSnapshotById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        String username = currentAccount.username();
        String email = currentAccount.email();
        Integer characterLimit = currentAccount.characterLimit();
        String password = currentAccount.password();

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            ensureUniqueUsername(request.getUsername(), id);
            username = request.getUsername();
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            ensureUniqueEmail(request.getEmail(), id);
            email = request.getEmail();
        }

        if (request.getCharacterLimit() != null) {
            characterLimit = request.getCharacterLimit();
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            password = encoder.encode(request.getPassword());
        }

        try {
            return accountRepository.updateAccount(new UpdateAccountData(id, username, email, characterLimit, password))
                    .map(this::toDto)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        } catch (RuntimeException ex) {
            throw translateDuplicateConstraint(ex);
        }
    }

    @Override
    public void deleteAccount(Integer id) {
        Integer deletedCount = accountRepository.deleteAccountById(id);

        if (deletedCount == 0) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }
    }

    private void ensureUniqueUsername(String username, Integer currentId) {
        Integer existingId = accountRepository.findAccountIdByUsername(username).orElse(null);

        if (existingId != null && !existingId.equals(currentId)) {
            throw new DuplicateResourceException("Username already exists");
        }
    }

    private void ensureUniqueEmail(String email, Integer currentId) {
        Integer existingId = accountRepository.findAccountIdByEmail(email).orElse(null);

        if (existingId != null && !existingId.equals(currentId)) {
            throw new DuplicateResourceException("Email already exists");
        }
    }

    private AccountResponseDTO toDto(AccountData accountData) {
        return new AccountResponseDTO(
                accountData.id(),
                accountData.username(),
                accountData.characterLimit(),
                accountData.email()
        );
    }

    private RuntimeException translateDuplicateConstraint(RuntimeException ex) {
        if (isUniqueConstraintViolation(ex, "username")) {
            return new DuplicateResourceException("Username already exists", ex);
        }

        if (isUniqueConstraintViolation(ex, "email")) {
            return new DuplicateResourceException("Email already exists", ex);
        }

        return ex;
    }

    private boolean isUniqueConstraintViolation(Throwable throwable, String propertyName) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof ClientException clientException) {
                String code = clientException.code();
                String message = clientException.getMessage();
                if (code != null
                        && code.contains("ConstraintValidationFailed")
                        && message != null
                        && message.toLowerCase().contains(propertyName.toLowerCase())) {
                    return true;
                }
            }

            String message = current.getMessage();
            if (message != null) {
                String lowerMessage = message.toLowerCase();
                if (lowerMessage.contains("constraint")
                        && lowerMessage.contains(propertyName.toLowerCase())
                        && (lowerMessage.contains("unique") || lowerMessage.contains("already exists"))) {
                    return true;
                }
            }

            current = current.getCause();
        }
        return false;
    }
}
