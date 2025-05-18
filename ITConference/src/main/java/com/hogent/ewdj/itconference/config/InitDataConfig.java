package com.hogent.ewdj.itconference.config;

import domain.Event;
import domain.Lokaal;
import domain.Spreker;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import service.EventService;
import service.LokaalService;
import service.SprekerService;
import validator.EventConstraintsValidator;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
@Transactional
public class InitDataConfig implements CommandLineRunner {

    @Autowired
    private LokaalService lokaalService;

    @Autowired
    private SprekerService sprekerService;

    @Autowired
    private EventService eventService;

    @Autowired
    private Validator validator;

    @Autowired
    private EventConstraintsValidator eventConstraintsValidator;


    @Override
    public void run(String... args) throws Exception {
        System.out.println("Database initialiseren met voorbeeld data...");

        // Voorbeeld Lokalen aanmaken en persisteren via de service
        Lokaal lokaal1 = new Lokaal("A101", 50);
        Lokaal lokaal2 = new Lokaal("B202", 30);
        lokaalService.saveLokaal(lokaal1);
        lokaalService.saveLokaal(lokaal2);

        // Voorbeeld Sprekers aanmaken en persisteren via de service
        Spreker spreker1 = new Spreker("Jan Janssen");
        Spreker spreker2 = new Spreker("Piet Peeters");
        Spreker spreker3 = new Spreker("Joris Joosten");
        spreker1 = sprekerService.saveSpreker(spreker1);
        spreker2 = sprekerService.saveSpreker(spreker2);
        spreker3 = sprekerService.saveSpreker(spreker3);


        // Voorbeeld Events aanmaken
        // Dates are already set to future dates using LocalDateTime.now().plusDays()
        LocalDateTime eventTime1 = LocalDateTime.now().plusDays(7).withHour(10).withMinute(0).withSecond(0).withNano(0);
        int beamerCode1 = 1234;
        int beamerCheck1 = beamerCode1 % 97;

        Event event1 = new Event();
        event1.setNaam("Inleiding tot Spring Boot");
        event1.setBeschrijving("Een introductie tot het Spring Boot framework en de basisprincipes.");
        event1.setDatumTijd(eventTime1);
        event1.setLokaal(lokaal1);
        event1.setPrijs(new BigDecimal("49.99"));
        event1.setBeamercode(beamerCode1);
        event1.setBeamercheck(beamerCheck1);

        List<Spreker> sprekersEvent1 = Arrays.asList(spreker1, spreker2);
        event1.setSprekers(sprekersEvent1);

        // Manual validation using the injected validator
        System.out.println("Manually validating Event 1...");
        Set<ConstraintViolation<Event>> violations1 = validator.validate(event1);
        if (!violations1.isEmpty()) {
            System.err.println("Validation errors for Event 1:");
            for (ConstraintViolation<Event> violation : violations1) {
                System.err.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
        } else {
            System.out.println("Event 1 validation successful.");
            eventService.saveEvent(event1);
        }


        // Dates are already set to future dates using LocalDateTime.now().plusDays()
        LocalDateTime eventTime2 = LocalDateTime.now().plusDays(8).withHour(14).withMinute(30).withSecond(0).withNano(0);
        int beamerCode2 = 5678;
        int beamerCheck2 = beamerCode2 % 97;

        Event event2 = new Event();
        event2.setNaam("Advanced JPA Techniques");
        event2.setBeschrijving("Diepgaande kijk op JPA en Hibernate voor geavanceerd databeheer.");
        event2.setDatumTijd(eventTime2);
        event2.setLokaal(lokaal2);
        event2.setPrijs(new BigDecimal("75.00"));
        event2.setBeamercode(beamerCode2);
        event2.setBeamercheck(beamerCheck2);

        List<Spreker> sprekersEvent2 = Arrays.asList(spreker3);
        event2.setSprekers(sprekersEvent2);

        // Manual validation using the injected validator
        System.out.println("Manually validating Event 2...");
        Set<ConstraintViolation<Event>> violations2 = validator.validate(event2);
        if (!violations2.isEmpty()) {
            System.err.println("Validation errors for Event 2:");
            for (ConstraintViolation<Event> violation : violations2) {
                System.err.println(violation.getPropertyPath() + ": " + violation.getMessage());
            }
        } else {
            System.out.println("Event 2 validation successful.");
            eventService.saveEvent(event2);
        }


        System.out.println("Database initialisatie voltooid.");
    }
}