package dk.ek.gruppe2.chooseyourfate.service;

import dk.ek.gruppe2.chooseyourfate.datasource.DataSourceResolver;
import dk.ek.gruppe2.chooseyourfate.availability.ReplicationOperationType;
import dk.ek.gruppe2.chooseyourfate.availability.WriteOperationCoordinator;
import dk.ek.gruppe2.chooseyourfate.dto.AccountResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.CreateAccountRequestDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateAccountRequestDTO;
import dk.ek.gruppe2.chooseyourfate.enums.DataSourceType;
import dk.ek.gruppe2.chooseyourfate.interfaces.AccountDataAccess;
import dk.ek.gruppe2.chooseyourfate.service.mysql.SqlAccountService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountService {

    private final DataSourceResolver dataSourceResolver;
    private final SqlAccountService sqlAccountService;
    private final WriteOperationCoordinator writeOperationCoordinator;

    public AccountService(
            DataSourceResolver dataSourceResolver,
            SqlAccountService sqlAccountService,
            WriteOperationCoordinator writeOperationCoordinator
    ) {
        this.dataSourceResolver = dataSourceResolver;
        this.sqlAccountService = sqlAccountService;
        this.writeOperationCoordinator = writeOperationCoordinator;
    }

    public List<AccountResponseDTO> getAllAccounts(String sourceHeader) {
        return resolveDataService(sourceHeader).getAllAccounts();
    }

    public AccountResponseDTO getAccountById(String sourceHeader, Integer id) {
        return resolveDataService(sourceHeader).getAccountById(id);
    }

    public AccountResponseDTO createAccount(String sourceHeader, CreateAccountRequestDTO request) {
        return writeOperationCoordinator.execute(
                ReplicationOperationType.CREATE,
                "account",
                () -> accountPayload(request),
                () -> resolveDataService(sourceHeader).createAccount(request)
        );
    }

    public AccountResponseDTO updateAccount(String sourceHeader, Integer id, UpdateAccountRequestDTO request) {
        return writeOperationCoordinator.execute(
                ReplicationOperationType.UPDATE,
                "account",
                () -> Map.of("id", id),
                () -> resolveDataService(sourceHeader).updateAccount(id, request)
        );
    }

    public void deleteAccount(String sourceHeader, Integer id) {
        writeOperationCoordinator.execute(
                ReplicationOperationType.DELETE,
                "account",
                () -> Map.of("id", id),
                () -> resolveDataService(sourceHeader).deleteAccount(id)
        );
    }

    public AccountResponseDTO registerAccount(CreateAccountRequestDTO request) {
        return writeOperationCoordinator.execute(
                ReplicationOperationType.CREATE,
                "account",
                () -> accountPayload(request),
                () -> sqlAccountService.createAccount(request)
        );
    }

    private AccountDataAccess resolveDataService(String sourceHeader) {
        DataSourceType dataSourceType = dataSourceResolver.resolve(sourceHeader);
        return switch (dataSourceType) {
            case SQL -> sqlAccountService;
        };
    }

    private Map<String, Object> accountPayload(CreateAccountRequestDTO request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", request.getUsername());
        payload.put("email", request.getEmail());
        return payload;
    }
}
