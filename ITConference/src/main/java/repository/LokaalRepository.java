package repository;

import domain.Lokaal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LokaalRepository extends JpaRepository<Lokaal, Long> {

    Lokaal findByNaam(String naam);

    List<Lokaal> findAllByOrderByNaamAsc();

    boolean existsByNaam(String naam);
}