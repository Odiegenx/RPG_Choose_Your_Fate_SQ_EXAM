package dk.ek.gruppe2.chooseyourfate.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import dk.ek.gruppe2.chooseyourfate.enums.DataSourceType;
import dk.ek.gruppe2.chooseyourfate.model.mongodb.AccountDocumentMongo;
import dk.ek.gruppe2.chooseyourfate.model.mysql.Account;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Account accountSql;
    private final AccountDocumentMongo accountMongo;
    // private final Optional<AccountNode> accountNeo4J;

    

    public CustomUserDetails(Account accountSql, AccountDocumentMongo accountMongo/*, Optional<AccountNode> accountNeo4J*/) {
        this.accountSql = accountSql;
        this.accountMongo = accountMongo;
        // this.accountNeo4J = accountNeo4J;
    }

    public boolean mongoIsNull(){
        if (accountMongo == null) {
            return true;
        }

        return false;
    }

    // public boolean neoIsNull(){
    //     if (accountNeo4J == null) {
    //         return true;
    //     }

    //     return false;
    // }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (accountSql.getRole() == null) {
            throw new IllegalStateException("Account role is null for user: " + accountSql.getUsername());
        }
        return List.of(new SimpleGrantedAuthority(accountSql.getRole().name()));
    }

    public Collection<? extends GrantedAuthority> getAuthorities(DataSourceType dataSource) {
        if (accountSql.getRole() == null) {
            throw new IllegalStateException("Account role is null for user: " + accountSql.getUsername());
        }
        
        switch (dataSource) {
            case SQL:
                return getAuthoritiesSQL();

            case MONGODB:
                return getAuthoritiesMONGO();
            
            // case DataSourceType.NEO4J:
            //     return getAuthoritiesNEO4J();
        
            default:
                return getAuthoritiesSQL();
        }
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesSQL() {
        if (accountSql.getRole() == null) {
            throw new IllegalStateException("Account role is null for user: " + accountSql.getUsername());
        }

        return List.of(new SimpleGrantedAuthority(accountSql.getRole().name()));
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesMONGO() {

        if (accountMongo.getRole() == null) {
            throw new IllegalStateException("Account role is null for user: " + accountMongo.getUsername());
        }

        return List.of(new SimpleGrantedAuthority(accountMongo.getRole().name()));
    }

    // private Collection<? extends GrantedAuthority> getAuthoritiesNEO4J() {
    //     if (accountNeo4J.getRole() == null) {
    //         throw new IllegalStateException("Account role is null for user: " + accountNeo4J.getUsername());
    //     }

    //     return List.of(new SimpleGrantedAuthority(accountNeo4J.getRole().name()));
    // }

    public String getPassword(DataSourceType dataSource) { 
        switch (dataSource) {
            case SQL:
                return accountSql.getPassword();

            case MONGODB:
                return accountMongo.getPassword();
            
            // case DataSourceType.NEO4J:
            //     return accountNeo4J.getPassword();
        
            default:
                return accountSql.getPassword();
        }
    }

    public String getUsername(DataSourceType dataSource) {
         switch (dataSource) {
            case SQL:
                return accountSql.getUsername();

            case MONGODB:
                return accountMongo.getUsername();
            
            // case DataSourceType.NEO4J:
            //     return accountNeo4J.getUsername();
        
            default:
                return accountSql.getUsername();
        } 
    }

    public String getId(DataSourceType dataSource) { 
        switch (dataSource) {
            case SQL:
                return accountSql.getId().toString();

            case MONGODB:
                return accountMongo.getId();
            
            // case DataSourceType.NEO4J:
            //     return accountNeo4J.getId().toString();
        
            default:
                return accountSql.getId().toString();
        } 
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    @Override
    public @Nullable String getPassword() {
        return accountSql.getPassword();
    }

    @Override
    public String getUsername() {
        return accountSql.getUsername();
    }
}
