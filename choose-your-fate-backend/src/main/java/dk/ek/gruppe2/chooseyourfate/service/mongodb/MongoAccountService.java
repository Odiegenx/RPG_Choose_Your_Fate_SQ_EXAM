package dk.ek.gruppe2.chooseyourfate.service.mongodb;

import dk.ek.gruppe2.chooseyourfate.dto.AccountResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.CreateAccountRequestDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateAccountRequestDTO;
import dk.ek.gruppe2.chooseyourfate.interfaces.AccountDataAccess;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoAccountService implements AccountDataAccess {

    private static final String MESSAGE = "MongoDB account functionality is not implemented yet";

    @Override
    public List<AccountResponseDTO> getAllAccounts() {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public AccountResponseDTO getAccountById(Integer id) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public AccountResponseDTO createAccount(CreateAccountRequestDTO request) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public AccountResponseDTO updateAccount(Integer id, UpdateAccountRequestDTO request) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public void deleteAccount(Integer id) {
        throw new UnsupportedOperationException(MESSAGE);
    }
}
