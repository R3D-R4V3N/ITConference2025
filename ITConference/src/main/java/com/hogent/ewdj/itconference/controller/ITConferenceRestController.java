// Begin creatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/com/hogent/ewdj/itconference/controller/ITConferenceRestController.java
package com.hogent.ewdj.itconference.controller;

import domain.Event;
import domain.Lokaal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.EventService;
import service.LokaalService;
import exceptions.EventNotFoundException; // Import EventNotFoundException
import exceptions.LokaalNotFoundException; // We gaan deze later aanmaken

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController // Geeft aan dat dit een REST controller is
@RequestMapping("/api") // Basis URL voor de API endpoints
public class ITConferenceRestController {

    @Autowired
    private EventService eventService;

    @Autowired
    private LokaalService lokaalService;

    // REST Service 1: Ophalen van eventgegevens van een gegeven datum
    // GET /api/eventsByDate?date=YYYY-MM-DD
    @GetMapping("/eventsByDate")
    public ResponseEntity<List<Event>> getEventsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // De findByNaamAndDatum methode in EventService gebruikt LocalDate voor de datum, maar de naam is ook een parameter.
        // Omdat de opdracht 'eventgegevens van een gegeven datum' vraagt, en niet 'eventgegevens van een gegeven naam op een gegeven datum',
        // zullen we hier een nieuwe methode moeten toevoegen in EventService/Repository.
        // Voor nu simuleren we door alle events op te halen en te filteren, of we maken een nieuwe query.

        // Optie 1: Nieuwe query in EventRepository en service
        // List<Event> events = eventService.findEventsByDate(date);

        // Voorlopige implementatie (kan efficiënter met een dedicated repository methode):
        List<Event> allEvents = eventService.findAllEvents(); // Haal alle events op
        List<Event> eventsOnDate = allEvents.stream()
                .filter(event -> event.getDatumTijd() != null && event.getDatumTijd().toLocalDate().equals(date))
                .toList();

        if (eventsOnDate.isEmpty()) {
            // We kunnen hier een custom exception gooien, of een 404 Not Found retourneren zonder exception.
            // Volgens de opdracht wordt een exception doorgestuurd naar een errorpage, wat voor een REST API vaak een @ExceptionHandler betekent.
            // Laten we voor nu een lege lijst met 200 OK retourneren, of 404 indien geen events gevonden (betere RESTful practice).
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Of HttpStatus.OK met lege lijst
        }
        return new ResponseEntity<>(eventsOnDate, HttpStatus.OK);
    }

    // REST Service 2: Ophalen van een capaciteit van een gegeven lokaal
    // GET /api/lokalen/{naam}/capaciteit
    @GetMapping("/lokalen/{naam}/capaciteit")
    public ResponseEntity<Integer> getLokaalCapaciteit(@PathVariable("naam") String naam) {
        Lokaal lokaal = lokaalService.findLokaalByNaam(naam);
        if (lokaal == null) {
            // Gooi een LokaalNotFoundException, die dan door een @RestControllerAdvice wordt afgehandeld.
            throw new LokaalNotFoundException("Lokaal met naam " + naam + " niet gevonden.");
        }
        return new ResponseEntity<>(lokaal.getCapaciteit(), HttpStatus.OK);
    }
}
// Einde creatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/com/hogent/ewdj/itconference/controller/ITConferenceRestController.java