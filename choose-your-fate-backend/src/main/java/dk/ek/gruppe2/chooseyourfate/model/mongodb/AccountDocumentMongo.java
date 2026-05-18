package dk.ek.gruppe2.chooseyourfate.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import dk.ek.gruppe2.chooseyourfate.enums.Role;

@Document(collection = "accounts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDocumentMongo {

    @Id
    private String id;
    private String username;
    private String password;
    private int characterLimit;
    private String email;
    private Role role;
}
