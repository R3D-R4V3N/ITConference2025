package service;

import domain.Lokaal;
import repository.LokaalRepository;
import repository.EventRepository; // Importeer EventRepository
import exceptions.LokaalNotFoundException; // Importeer LokaalNotFoundException

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LokaalServiceImpl implements LokaalService {

    @Autowired
    private LokaalRepository lokaalRepository;

    @Autowired
    private EventRepository eventRepository; // Injecteer EventRepository

    @Override
    public List<Lokaal> findAllLokalen() {
        return lokaalRepository.findAllByOrderByNaamAsc();
    }

    @Override
    @Transactional // Voeg @Transactional toe voor save operaties
    public Lokaal saveLokaal(Lokaal lokaal) {
        return lokaalRepository.save(lokaal);
    }

    @Override
    public Optional<Lokaal> findLokaalById(Long id) {
        return lokaalRepository.findById(id);
    }

    @Override
    public Lokaal findLokaalByNaam(String naam) {
        return lokaalRepository.findByNaam(naam);
    }

    @Override
    public boolean existsLokaalByNaam(String naam) {
        return lokaalRepository.existsByNaam(naam);
    }

    @Override
    @Transactional // Zorg ervoor dat de hele operatie transactioneel is
    public void deleteLokaalById(Long id) {
        Lokaal lokaal = lokaalRepository.findById(id)
                .orElseThrow(() -> new LokaalNotFoundException("Lokaal met ID " + id + " niet gevonden."));

        // Controleer of er events gekoppeld zijn aan dit lokaal
        if (eventRepository.countByLokaal(lokaal) > 0) { // Nieuwe methode in EventRepository nodig
            throw new IllegalStateException("Kan lokaal niet verwijderen. Er zijn nog evenementen gekoppeld aan dit lokaal.");
        }

        lokaalRepository.deleteById(id);
    }
}