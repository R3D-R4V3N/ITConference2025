package service;

import domain.Lokaal;

import java.util.List;
import java.util.Optional;

public interface LokaalService {

    List<Lokaal> findAllLokalen();

    Lokaal saveLokaal(Lokaal lokaal);

    Optional<Lokaal> findLokaalById(Long id);

    Lokaal findLokaalByNaam(String naam);

    boolean existsLokaalByNaam(String naam);

    void deleteLokaalById(Long id);
}