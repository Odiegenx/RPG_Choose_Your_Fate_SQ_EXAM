package dk.ek.gruppe2.chooseyourfate.model.mongodb;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import dk.ek.gruppe2.chooseyourfate.enums.Role;

@Document(collection = "accounts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountDocumentMongo {

    @Id
    private String id;
    private String username;
    private String password;
    private int characterLimit;
    private String email;
    private Role role;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCharacterLimit() {
        return characterLimit;
    }

    public void setCharacterLimit(int characterLimit) {
        this.characterLimit = characterLimit;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
