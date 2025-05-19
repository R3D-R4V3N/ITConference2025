package repository;

import domain.Spreker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprekerRepository extends JpaRepository<Spreker, Long> {

    Spreker findByNaam(String naam);

    boolean existsByNaam(String naam);

    List<Spreker> findAllByOrderByNaamAsc();
}