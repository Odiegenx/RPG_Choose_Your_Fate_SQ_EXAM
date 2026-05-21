package dk.ek.gruppe2.chooseyourfate.availability.replication;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcAccountSecondaryReplicationGatewayTest {

    @Test
    void createAccountJobIsUpsertedIntoSecondary() {
        // Arrange
        CapturingJdbcTemplate jdbcTemplate = new CapturingJdbcTemplate();
        JdbcAccountSecondaryReplicationGateway gateway = new JdbcAccountSecondaryReplicationGateway(jdbcTemplate);

        // Act
        gateway.apply(new ReplicationJob(
                ReplicationOperationType.CREATE,
                "account",
                Map.of(
                        "id", 1,
                        "username", "player",
                        "characterLimit", 3,
                        "email", "player@test.dk",
                        "password", "hashed",
                        "role", "ROLE_USER"
                )
        ));

        // Assert
        assertTrue(jdbcTemplate.sql.getFirst().contains("INSERT INTO account"));
        assertTrue(jdbcTemplate.sql.getFirst().contains("ON DUPLICATE KEY UPDATE"));
        assertEquals(List.of(1, "player", 3, "player@test.dk", "hashed", "ROLE_USER"), jdbcTemplate.args.getFirst());
    }

    @Test
    void updateAccountJobUpdatesProvidedFieldsOnly() {
        // Arrange
        CapturingJdbcTemplate jdbcTemplate = new CapturingJdbcTemplate();
        JdbcAccountSecondaryReplicationGateway gateway = new JdbcAccountSecondaryReplicationGateway(jdbcTemplate);

        // Act
        gateway.apply(new ReplicationJob(
                ReplicationOperationType.UPDATE,
                "account",
                Map.of(
                        "id", 1,
                        "username", "updated",
                        "email", "updated@test.dk"
                )
        ));

        // Assert
        assertEquals("UPDATE account SET username = ?, email = ? WHERE id = ?", jdbcTemplate.sql.getFirst());
        assertEquals(List.of("updated", "updated@test.dk", 1), jdbcTemplate.args.getFirst());
    }

    @Test
    void deleteAccountJobDeletesAccountFromSecondary() {
        // Arrange
        CapturingJdbcTemplate jdbcTemplate = new CapturingJdbcTemplate();
        JdbcAccountSecondaryReplicationGateway gateway = new JdbcAccountSecondaryReplicationGateway(jdbcTemplate);

        // Act
        gateway.apply(new ReplicationJob(
                ReplicationOperationType.DELETE,
                "account",
                Map.of("id", 1)
        ));

        // Assert
        assertEquals("DELETE FROM account WHERE id = ?", jdbcTemplate.sql.getFirst());
        assertEquals(List.of(1), jdbcTemplate.args.getFirst());
    }

    private static class CapturingJdbcTemplate extends JdbcTemplate {
        private final List<String> sql = new ArrayList<>();
        private final List<List<Object>> args = new ArrayList<>();

        @Override
        public int update(String sql, Object... args) {
            this.sql.add(sql.strip());
            this.args.add(List.of(args));
            return 1;
        }
    }
}
