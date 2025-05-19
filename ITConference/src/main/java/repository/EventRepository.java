// Begin modificatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/repository/EventRepository.java
package repository;

import domain.Event;
import domain.Lokaal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Standaard find methoden geërfd van JpaRepository

    // Methode voor validatie: zoek events op tijdstip en lokaal
    List<Event> findByDatumTijdAndLokaal(LocalDateTime datumTijd, Lokaal lokaal);

    // Methode voor validatie: zoek events op naam en dag
    // JPQL query nodig omdat we op de datum (zonder tijd) willen filteren
    @Query("SELECT e FROM Event e WHERE e.naam = :naam AND DATE(e.datumTijd) = :datum")
    List<Event> findByNaamAndDatum(@Param("naam") String naam, @Param("datum") LocalDate datum);

    // NIEUWE METHODE: Zoek events op een specifieke datum (alle events op die dag)
    @Query("SELECT e FROM Event e WHERE DATE(e.datumTijd) = :date ORDER BY e.datumTijd ASC")
    List<Event> findByDatum(@Param("date") LocalDate date);

    // Andere custom find methoden indien nodig (bv. sorteren op datum en tijd)
    List<Event> findAllByOrderByDatumTijdAsc();
}
// Einde modificatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/repository/EventRepository.java