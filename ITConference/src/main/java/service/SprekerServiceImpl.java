package service.impl;

import domain.Spreker;
import repository.SprekerRepository;
import service.SprekerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SprekerServiceImpl implements SprekerService {

    @Autowired
    private SprekerRepository sprekerRepository;

    @Override
    public List<Spreker> findAllSprekers() {
        return sprekerRepository.findAllByOrderByNaamAsc();
    }

    @Override
    @Transactional
    public Spreker saveSpreker(Spreker spreker) {
        return sprekerRepository.save(spreker);
    }

    @Override
    public Optional<Spreker> findSprekerById(Long id) {
        return sprekerRepository.findById(id);
    }

    @Override
    public Spreker findSprekerByNaam(String naam) {
        return sprekerRepository.findByNaam(naam);
    }

    @Override
    public boolean existsSprekerByNaam(String naam) {
        return sprekerRepository.existsByNaam(naam);
    }
}
