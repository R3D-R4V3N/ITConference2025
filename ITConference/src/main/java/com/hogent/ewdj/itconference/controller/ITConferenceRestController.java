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


@RestController
@RequestMapping("/api")
public class ITConferenceRestController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private EventService eventService;

    @Autowired
    private LokaalService lokaalService;

    @GetMapping("/eventsByDate")
    public ResponseEntity<List<Event>> getEventsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Event> allEvents = eventService.findAllEvents();
        List<Event> eventsOnDate = allEvents.stream()
                .filter(event -> event.getDatumTijd() != null && event.getDatumTijd().toLocalDate().equals(date))
                .toList();

        if (eventsOnDate.isEmpty()) {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(eventsOnDate, HttpStatus.OK);
    }

    @GetMapping("/lokalen/{naam}/capaciteit")
    public ResponseEntity<Integer> getLokaalCapaciteit(@PathVariable("naam") String naam) {
        Lokaal lokaal = lokaalService.findLokaalByNaam(naam);
        if (lokaal == null) {
            String msg = messageSource.getMessage(
                    "lokaal.notfound",
                    new Object[]{naam},
                    LocaleContextHolder.getLocale());
            throw new LokaalNotFoundException(msg);
        }
        return new ResponseEntity<>(lokaal.getCapaciteit(), HttpStatus.OK);
    }
}
