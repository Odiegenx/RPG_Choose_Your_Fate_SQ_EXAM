package dk.ek.gruppe2.chooseyourfate.controller;

import dk.ek.gruppe2.chooseyourfate.dto.CharacterPathResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateCharacterPathRequestDTO;
import dk.ek.gruppe2.chooseyourfate.service.CharacterPathService;
import dk.ek.gruppe2.chooseyourfate.service.TTSService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/character-paths")
public class CharacterPathController {

    private static final String DATA_SOURCE_HEADER = "X-Data-Source";

    private final CharacterPathService characterPathService;
    private final TTSService ttsService;

    public CharacterPathController(CharacterPathService characterPathService, TTSService ttsService) {
        this.characterPathService = characterPathService;
        this.ttsService = ttsService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CharacterPathResponseDTO> getAllCharacterPaths(
            @RequestHeader(value = DATA_SOURCE_HEADER, required = false) String dataSource
    ) {
        return characterPathService.getAllCharacterPaths(dataSource);
    }

    @GetMapping("/{characterId}")
    @PreAuthorize("hasRole('ADMIN') or @characterAuthorizationService.canAccessCharacter(#characterId, authentication)")
    public CharacterPathResponseDTO getCharacterPathByCharacterId(
            @RequestHeader(value = DATA_SOURCE_HEADER, required = false) String dataSource,
            @PathVariable Integer characterId
    ) {
        return characterPathService.getCharacterPathByCharacterId(dataSource, characterId);
    }

    @GetMapping("/{characterId}/audio")
    public ResponseEntity<byte[]> textToSpeech(@PathVariable Integer characterId) {
        byte[] bytes = ttsService.textToSpeech(characterId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"speech.mp3\"")
                .body(bytes);
    }

    @PutMapping("/{characterId}")
    @PreAuthorize("hasRole('ADMIN') or @characterAuthorizationService.canAccessCharacter(#characterId, authentication)")
    public CharacterPathResponseDTO updateCharacterPath(
            @RequestHeader(value = DATA_SOURCE_HEADER, required = false) String dataSource,
            @PathVariable Integer characterId,
            @RequestBody UpdateCharacterPathRequestDTO request
    ) {
        return characterPathService.updateCharacterPath(dataSource, characterId, request);
    }
}
