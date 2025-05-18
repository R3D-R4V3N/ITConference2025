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

    List<Lokaal> findAllLokalen();

    List<Spreker> findAllSprekers();
}