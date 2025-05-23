package repository;

import domain.Event;
import domain.Lokaal; // Importeer Lokaal
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByDatumTijdAndLokaal(LocalDateTime datumTijd, Lokaal lokaal);

    @Query("SELECT e FROM Event e WHERE e.naam = :naam AND DATE(e.datumTijd) = :datum")
    List<Event> findByNaamAndDatum(@Param("naam") String naam, @Param("datum") LocalDate datum);

    @Query("SELECT e FROM Event e WHERE DATE(e.datumTijd) = :date ORDER BY e.datumTijd ASC")
    List<Event> findByDatum(@Param("date") LocalDate date);

    List<Event> findAllByOrderByDatumTijdAsc();

    // Nieuwe methode om het aantal events te tellen gekoppeld aan een specifiek lokaal
    long countByLokaal(Lokaal lokaal);
}