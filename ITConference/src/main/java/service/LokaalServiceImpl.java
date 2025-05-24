package service;

import domain.Lokaal;
import repository.LokaalRepository;
import repository.EventRepository;
import exceptions.LokaalNotFoundException;

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
    private EventRepository eventRepository;

    @Override
    public List<Lokaal> findAllLokalen() {
        return lokaalRepository.findAllByOrderByNaamAsc();
    }

    @Override
    @Transactional
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
    @Transactional
    public void deleteLokaalById(Long id) {
        Lokaal lokaal = lokaalRepository.findById(id)
                .orElseThrow(() -> new LokaalNotFoundException("Lokaal met ID " + id + " niet gevonden."));

        if (eventRepository.countByLokaal(lokaal) > 0) {
            throw new IllegalStateException("Kan lokaal niet verwijderen. Er zijn nog evenementen gekoppeld aan dit lokaal.");
        }

        lokaalRepository.deleteById(id);
    }
}