package repository;

import domain.Spreker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Geeft aan dat dit een Spring Repository component is
public interface SprekerRepository extends JpaRepository<Spreker, Long> {

    // Custom find methoden zoals findByNaam, die door Spring Data JPA automatisch worden geïmplementeerd.
    Spreker findByNaam(String naam);

    boolean existsByNaam(String naam);

    List<Spreker> findAllByOrderByNaamAsc();
}