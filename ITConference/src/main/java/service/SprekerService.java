package service;

import domain.Spreker;

import java.util.List;
import java.util.Optional;

public interface SprekerService {

    List<Spreker> findAllSprekers();

    Spreker saveSpreker(Spreker spreker);

    Optional<Spreker> findSprekerById(Long id);

    Spreker findSprekerByNaam(String naam);

    boolean existsSprekerByNaam(String naam);
}
