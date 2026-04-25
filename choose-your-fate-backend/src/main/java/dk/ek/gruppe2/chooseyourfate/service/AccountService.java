package dk.ek.gruppe2.chooseyourfate.service;

import dk.ek.gruppe2.chooseyourfate.datasource.DataSourceResolver;
import dk.ek.gruppe2.chooseyourfate.dto.AccountResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.CreateAccountRequestDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateAccountRequestDTO;
import dk.ek.gruppe2.chooseyourfate.enums.DataSourceType;
import dk.ek.gruppe2.chooseyourfate.interfaces.AccountDataAccess;
import dk.ek.gruppe2.chooseyourfate.service.mongodb.MongoAccountService;
import dk.ek.gruppe2.chooseyourfate.service.mysql.SqlAccountService;
import dk.ek.gruppe2.chooseyourfate.service.neo4j.Neo4jAccountService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final DataSourceResolver dataSourceResolver;
    private final SqlAccountService sqlAccountService;
    private final Neo4jAccountService neo4jAccountService;
    private final MongoAccountService mongoAccountService;

    public AccountService(
            DataSourceResolver dataSourceResolver,
            SqlAccountService sqlAccountService,
            Neo4jAccountService neo4jAccountService,
            MongoAccountService mongoAccountService
    ) {
        this.dataSourceResolver = dataSourceResolver;
        this.sqlAccountService = sqlAccountService;
        this.neo4jAccountService = neo4jAccountService;
        this.mongoAccountService = mongoAccountService;
    }

    public List<AccountResponseDTO> getAllAccounts(String sourceHeader) {
        return resolveDataService(sourceHeader).getAllAccounts();
    }

    public AccountResponseDTO getAccountById(String sourceHeader, Integer id) {
        return resolveDataService(sourceHeader).getAccountById(id);
    }

    public AccountResponseDTO createAccount(String sourceHeader, CreateAccountRequestDTO request) {
        return resolveDataService(sourceHeader).createAccount(request);
    }

    public AccountResponseDTO updateAccount(String sourceHeader, Integer id, UpdateAccountRequestDTO request) {
        return resolveDataService(sourceHeader).updateAccount(id, request);
    }

    public void deleteAccount(String sourceHeader, Integer id) {
        resolveDataService(sourceHeader).deleteAccount(id);
    }

    public AccountResponseDTO registerAccount(CreateAccountRequestDTO request) {
        return sqlAccountService.createAccount(request);
    }

    private AccountDataAccess resolveDataService(String sourceHeader) {
        DataSourceType dataSourceType = dataSourceResolver.resolve(sourceHeader);
        return switch (dataSourceType) {
            case SQL -> sqlAccountService;
            case NEO4J -> neo4jAccountService;
            case MONGODB -> mongoAccountService;
        };
    }
}
