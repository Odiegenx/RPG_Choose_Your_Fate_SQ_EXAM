package dk.ek.gruppe2.chooseyourfate.interfaces;

import java.util.List;

import dk.ek.gruppe2.chooseyourfate.dto.choice.ChoiceResponseDTO;
import dk.ek.gruppe2.chooseyourfate.dto.choice.CreateChoiceRequestDTO;
import dk.ek.gruppe2.chooseyourfate.dto.choice.UpdateChoiceRequestDTO;


public interface ChoiceDataAccess {
    List<ChoiceResponseDTO> getAllChoices();

    ChoiceResponseDTO getChoiceById(Integer id);

    ChoiceResponseDTO createChoice(CreateChoiceRequestDTO request);

    ChoiceResponseDTO updateChoice(Integer id, UpdateChoiceRequestDTO request);

    void deleteChoice(Integer id);
}
