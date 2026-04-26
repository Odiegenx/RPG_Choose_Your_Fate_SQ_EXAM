package dk.ek.gruppe2.chooseyourfate.service.mongodb;

import dk.ek.gruppe2.chooseyourfate.dto.RaceDetailsResponseDTO;
import dk.ek.gruppe2.chooseyourfate.interfaces.RaceDetailsDataAccess;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoRaceDetailsService implements RaceDetailsDataAccess {

    private static final String MESSAGE = "MongoDB race details functionality is not implemented yet";

    @Override
    public List<RaceDetailsResponseDTO> getAllRaceDetails() {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public RaceDetailsResponseDTO getRaceDetailsById(Integer id) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public RaceDetailsResponseDTO createRaceDetails() {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public void deleteRaceDetails(Integer id) {
        throw new UnsupportedOperationException(MESSAGE);
    }
}
