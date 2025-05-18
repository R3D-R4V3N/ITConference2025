package service;

import domain.Event;
import domain.Lokaal; // Nodig voor methoden die Lokaal objecten gebruiken
import domain.Spreker; // Nodig voor methoden die Spreker objecten gebruiken
import repository.EventRepository; // Importeer EventRepository
import repository.LokaalRepository; // Importeer LokaalRepository (of gebruik LokaalService)
import repository.SprekerRepository; // Importeer SprekerRepository (of gebruik SprekerService)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Nodig voor transacties

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LokaalService lokaalService; // Gebruik de LokaalService voor lokaal-gerelateerde operaties

    @Autowired
    private SprekerService sprekerService; // We maken deze service later aan

    /**
     * Haalt alle events op, gesorteerd op datum en tijd.
     * @return Een lijst van Event objecten.
     */
    public List<Event> findAllEvents() {
        return eventRepository.findAllByOrderByDatumTijdAsc();
    }

    /**
     * Slaat een nieuw event op of werkt een bestaand event bij.
     * De Bean Validation (inclusief custom validators) wordt typisch al uitgevoerd
     * voordat deze methode wordt aangeroepen via @Valid in de controller.
     * @param event Het op te slaan of bij te werken Event object.
     * @return Het opgeslagen of bijgewerkte Event object.
     */
    @Transactional // Zorgt ervoor dat deze bewerking transactioneel is
    public Event saveEvent(Event event) {
        // Hier kunnen eventuele business regels komen die uitgevoerd moeten worden
        // voor het opslaan, of interacties met andere services.

        // Bijvoorbeeld, zorgen dat de gekoppelde Spreker objecten managed zijn
        // als de sprekers nog niet uit de database komen.
        // Dit is belangrijk bij ManyToMany relaties.
        if (event.getSprekers() != null) {
            for (int i = 0; i < event.getSprekers().size(); i++) {
                Spreker spreker = event.getSprekers().get(i);
                if (spreker.getId() == null) {
                    // Als de spreker nieuw is, sla hem eerst op of vind een bestaande
                    Spreker bestaandeSpreker = sprekerService.findSprekerByNaam(spreker.getNaam());
                    if (bestaandeSpreker != null) {
                        event.getSprekers().set(i, bestaandeSpreker); // Gebruik de bestaande spreker
                    } else {
                        spreker = sprekerService.saveSpreker(spreker); // Sla de nieuwe spreker op
                        event.getSprekers().set(i, spreker); // Gebruik de managed spreker
                    }
                } else {
                    // Als de spreker al een ID heeft, zorg ervoor dat het een managed entity is
                    Optional<Spreker> managedSpreker = sprekerService.findSprekerById(spreker.getId());
                    if (managedSpreker.isPresent()) {
                        event.getSprekers().set(i, managedSpreker.get());
                    } else {
                        // Dit zou niet mogen gebeuren als de spreker uit de DB komt, maar voor safety
                        throw new IllegalArgumentException("Ongeldige spreker met ID: " + spreker.getId());
                    }
                }
            }
        }

        // Zorg ervoor dat het gekoppelde Lokaal object managed is
        if (event.getLokaal() != null && event.getLokaal().getId() != null) {
            lokaalService.findLokaalById(event.getLokaal().getId()).ifPresent(event::setLokaal);
        }


        // Sla het event op in de database via de repository
        return eventRepository.save(event);
    }

    /**
     * Zoekt een event op basis van het ID.
     * @param id Het ID van het te zoeken event.
     * @return Een Optional met het gevonden Event object, of een lege Optional als het niet gevonden is.
     */
    public Optional<Event> findEventById(Long id) {
        return eventRepository.findById(id);
    }

    // Eventuele methoden voor het verwijderen of bewerken van events kunnen hier komen
    // public void deleteEvent(Long id) { ... }
    // public Event updateEvent(Event event) { ... }


    // Methodes gebruikt door de custom validators of controllers voor specifieke checks
    public List<Event> findEventsByDatumTijdAndLokaal(LocalDateTime datumTijd, Lokaal lokaal) {
        return eventRepository.findByDatumTijdAndLokaal(datumTijd, lokaal);
    }

    public List<Event> findEventsByNaamAndDatum(String naam, LocalDate datum) {
        return eventRepository.findByNaamAndDatum(naam, datum);
    }

    // Methode om alle beschikbare lokalen op te halen (delegeert naar LokaalService)
    public List<Lokaal> findAllLokalen() {
        return lokaalService.findAllLokalen();
    }

    // Methode om alle sprekers op te halen (delegeert naar SprekerService)
    public List<Spreker> findAllSprekers() {
        return sprekerService.findAllSprekers();
    }
}