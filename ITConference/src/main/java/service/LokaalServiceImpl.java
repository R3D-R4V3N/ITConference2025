package service.impl;

import domain.Lokaal;
import repository.LokaalRepository;
import service.LokaalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LokaalServiceImpl implements LokaalService {

    @Autowired
    private LokaalRepository lokaalRepository;

    @Override
    public List<Lokaal> findAllLokalen() {
        return lokaalRepository.findAllByOrderByNaamAsc();
    }

    @Override
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
}
