package service;

import domain.Event;
import domain.Lokaal;
import domain.Spreker;
import repository.EventRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LokaalService lokaalService;

    @Autowired
    private SprekerService sprekerService;

    @Override
    public List<Event> findAllEvents() {
        return eventRepository.findAllByOrderByDatumTijdAsc();
    }

    @Override
    @Transactional
    public Event saveEvent(@Valid Event event) {
        if (event.getSprekers() != null) {
            for (int i = 0; i < event.getSprekers().size(); i++) {
                Spreker spreker = event.getSprekers().get(i);
                if (spreker.getId() == null) {
                    Spreker bestaandeSpreker = sprekerService.findSprekerByNaam(spreker.getNaam());
                    if (bestaandeSpreker != null) {
                        event.getSprekers().set(i, bestaandeSpreker);
                    } else {
                        spreker = sprekerService.saveSpreker(spreker);
                        event.getSprekers().set(i, spreker);
                    }
                } else {
                    Optional<Spreker> managedSpreker = sprekerService.findSprekerById(spreker.getId());
                    if (managedSpreker.isPresent()) {
                        event.getSprekers().set(i, managedSpreker.get());
                    } else {
                        throw new IllegalArgumentException("Ongeldige spreker met ID: " + spreker.getId());
                    }
                }
            }
        }

        if (event.getLokaal() != null && event.getLokaal().getId() != null) {
            lokaalService.findLokaalById(event.getLokaal().getId()).ifPresent(event::setLokaal);
        }

        return eventRepository.save(event);
    }

    @Override
    public Optional<Event> findEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public List<Event> findEventsByDatumTijdAndLokaal(LocalDateTime datumTijd, Lokaal lokaal) {
        return eventRepository.findByDatumTijdAndLokaal(datumTijd, lokaal);
    }

    @Override
    public List<Event> findEventsByNaamAndDatum(String naam, LocalDate datum) {
        return eventRepository.findByNaamAndDatum(naam, datum);
    }

    @Override
    public List<Event> findEventsByDate(LocalDate date) {
        return eventRepository.findByDatum(date);
    }

    @Override
    public List<Lokaal> findAllLokalen() {
        return lokaalService.findAllLokalen();
    }

    @Override
    public List<Spreker> findAllSprekers() {
        return sprekerService.findAllSprekers();
    }
}