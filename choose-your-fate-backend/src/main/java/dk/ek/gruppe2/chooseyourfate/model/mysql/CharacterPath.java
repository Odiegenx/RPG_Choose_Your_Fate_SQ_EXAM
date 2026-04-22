package dk.ek.gruppe2.chooseyourfate.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "character_path")
public class CharacterPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "character_id", nullable = false)
    private CharacterAvatar character;

    @Column(columnDefinition = "LONGTEXT")
    private String summary;

    public CharacterPath() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public CharacterAvatar getCharacter() { return character; }
    public void setCharacter(CharacterAvatar character) { this.character = character; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}