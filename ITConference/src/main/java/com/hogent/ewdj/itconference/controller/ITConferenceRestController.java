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
import exceptions.LokaalNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;


@RestController
@RequestMapping("/api")
public class ITConferenceRestController {

    @Autowired
    private MessageSource messageSource; // Injected for use in exceptions

    @Autowired
    private EventService eventService;

    @Autowired
    private LokaalService lokaalService;

    @GetMapping("/eventsByDate")
    public ResponseEntity<List<Event>> getEventsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Locale locale) { // Voeg Locale toe om gelokaliseerde berichten te gebruiken
        List<Event> allEvents = eventService.findAllEvents();
        List<Event> eventsOnDate = allEvents.stream()
                .filter(event -> event.getDatumTijd() != null && event.getDatumTijd().toLocalDate().equals(date))
                .toList();

        if (eventsOnDate.isEmpty()) {
            // Voor een REST endpoint is het returnen van NOT_FOUND prima, maar je kunt een body toevoegen.
            // De ITConferenceRestErrorAdvice zou dit afhandelen als je een specifieke exception gooit.
            // Laten we hier een expliciete message teruggeven.
            String message = messageSource.getMessage("event.notfound", new Object[]{date.toString()}, locale);
            return new ResponseEntity(message, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(eventsOnDate, HttpStatus.OK);
    }

    @GetMapping("/lokalen/{naam}/capaciteit")
    public ResponseEntity<Integer> getLokaalCapaciteit(@PathVariable("naam") String naam, Locale locale) {
        Lokaal lokaal = lokaalService.findLokaalByNaam(naam);
        if (lokaal == null) {
            // Gooi een exception die door ITConferenceRestErrorAdvice wordt opgevangen
            String message = messageSource.getMessage("lokaal.notfound", new Object[]{naam}, locale);
            throw new LokaalNotFoundException(message);
        }
        return new ResponseEntity<>(lokaal.getCapaciteit(), HttpStatus.OK);
    }
}