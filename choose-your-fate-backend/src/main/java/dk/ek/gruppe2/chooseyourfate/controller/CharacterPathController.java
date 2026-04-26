package dk.ek.gruppe2.chooseyourfate.controller;

import dk.ek.gruppe2.chooseyourfate.dto.CharacterPathResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.UpdateCharacterPathRequestDTO;
import dk.ek.gruppe2.chooseyourfate.service.CharacterPathService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/character-paths")
public class CharacterPathController {

    private static final String DATA_SOURCE_HEADER = "X-Data-Source";

    private final CharacterPathService characterPathService;

    public CharacterPathController(CharacterPathService characterPathService) {
        this.characterPathService = characterPathService;
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
