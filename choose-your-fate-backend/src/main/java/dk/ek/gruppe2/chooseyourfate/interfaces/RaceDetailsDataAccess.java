package dk.ek.gruppe2.chooseyourfate.interfaces;

import dk.ek.gruppe2.chooseyourfate.dto.RaceDetailsResponseDTO;

import java.util.List;

public interface RaceDetailsDataAccess {

    List<RaceDetailsResponseDTO> getAllRaceDetails();

    RaceDetailsResponseDTO getRaceDetailsById(Integer id);

    RaceDetailsResponseDTO createRaceDetails();

    void deleteRaceDetails(Integer id);
}
