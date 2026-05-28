package dk.ek.gruppe2.chooseyourfate.controller;

import dk.ek.gruppe2.chooseyourfate.dto.CharacterResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.CreateCharacterRequestDTO;
import dk.ek.gruppe2.chooseyourfate.service.CharacterService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/choose-your-fate/characters")
public class CharacterController {
    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CharacterResponseDTO> getAllCharacters(
    ) {
        return characterService.getAllCharacters();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @characterAuthorizationService.canAccessCharacter(#id, authentication)")
    public CharacterResponseDTO getCharacterById(
            @PathVariable Integer id
    ) {
        
        return characterService.getCharacterById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @accountAuthorizationService.canModifyAccount(#request.accountId, authentication)")
    public CharacterResponseDTO createCharacter(
            @RequestBody CreateCharacterRequestDTO request
    ) {
        return characterService.createCharacter(request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @characterAuthorizationService.canAccessCharacter(#id, authentication)")
    public void deleteCharacter(
            @PathVariable Integer id
    ) {
        characterService.deleteCharacter(id);
    }
    
    @GetMapping("/all")
    public List<CharacterResponseDTO> getCharactersByAccountId(
            Authentication auth
    ) {
        Map<String, Object> extraInfo =  (Map<String, Object>) auth.getDetails(); 

        Object accountId = extraInfo.get("sqlId");

        return characterService.getCharactersByAccountId(Integer.parseInt(accountId.toString()));
    }
}