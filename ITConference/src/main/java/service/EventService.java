// Begin modificatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/service/EventService.java
package service;

import domain.Event;
import domain.Lokaal;
import domain.Spreker;

import jakarta.validation.Valid; // Import @Valid

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventService {

    List<Event> findAllEvents();

    // Add @Valid here
    Event saveEvent(@Valid Event event);

    Optional<Event> findEventById(Long id);

    List<Event> findEventsByDatumTijdAndLokaal(LocalDateTime datumTijd, Lokaal lokaal);

    List<Event> findEventsByNaamAndDatum(String naam, LocalDate datum);

    // NIEUWE METHODE: Events vinden op datum
    List<Event> findEventsByDate(LocalDate date);

    List<Lokaal> findAllLokalen();

    List<Spreker> findAllSprekers();
}
// Einde modificatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/service/EventService.java