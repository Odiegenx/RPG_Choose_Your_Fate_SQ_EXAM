package dk.ek.gruppe2.chooseyourfate.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

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

    @Column(columnDefinition = "MEDIUMBLOB")
    @Lob
    private byte[] audioBlob;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime summary_updated_at;


    @Column(columnDefinition = "DATETIME")
    private LocalDateTime audio_blob_updated_at;

    public CharacterPath() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public CharacterAvatar getCharacter() { return character; }
    public void setCharacter(CharacterAvatar character) { this.character = character; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public byte[] getAudioBlob() { return audioBlob; }
    public void setAudioBlob(byte[] audioBlob) { this.audioBlob = audioBlob; }

    public LocalDateTime getAudio_blob_updated_at() {
        return audio_blob_updated_at;
    }

    public LocalDateTime getSummary_updated_at() {
        return summary_updated_at;
    }

    public void setSummary_updated_at(LocalDateTime summary_updated_at) {
        this.summary_updated_at = summary_updated_at;
    }

    public void setAudio_blob_updated_at(LocalDateTime audio_blob_updated_at) {
        this.audio_blob_updated_at = audio_blob_updated_at;
    }

}