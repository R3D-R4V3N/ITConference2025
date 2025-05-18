package com.hogent.ewdj.itconference.config; // Pas dit package aan naar jouw structuur

import domain.Event;
import domain.Lokaal;
import domain.Spreker;
import repository.LokaalRepository;
import repository.SprekerRepository;
import service.EventService; // Belangrijk: Injecteer de EventService

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // Kan hier ook, maar Service is al Transactional

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component // Zorgt ervoor dat Spring deze klasse detecteert en uitvoert bij opstart
public class InitDataConfig implements CommandLineRunner {

    @Autowired
    private LokaalRepository lokaalRepository; // Injecteer LokaalRepository

    @Autowired
    private SprekerRepository sprekerRepository; // Injecteer SprekerRepository

    @Autowired
    private EventService eventService; // Injecteer EventService voor het opslaan van Events

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Database initialiseren met voorbeeld data...");

        // Voeg hier code toe om Lokalen, Sprekers en Events aan te maken en op te slaan

        // Voorbeeld Lokalen aanmaken en opslaan (via Repository)
        Lokaal lokaal1 = new Lokaal("A101", 50);
        Lokaal lokaal2 = new Lokaal("B202", 30);
        lokaalRepository.save(lokaal1);
        lokaalRepository.save(lokaal2);

        // Voorbeeld Sprekers aanmaken en opslaan (via Repository)
        Spreker spreker1 = new Spreker("Jan Janssen");
        Spreker spreker2 = new Spreker("Piet Peeters");
        Spreker spreker3 = new Spreker("Joris Joosten");
        sprekerRepository.save(spreker1);
        sprekerRepository.save(spreker2);
        sprekerRepository.save(spreker3);

        // Voorbeeld Events aanmaken en opslaan (via EventService)
        // Zorg ervoor dat de datum/tijd binnen je ingestelde conferentieperiode vallen
        // en dat de beamercheck correct is
        LocalDateTime eventTime1 = LocalDateTime.of(2025, 3, 15, 10, 0); // Voorbeelddatum binnen maart 2025
        int beamerCode1 = 1234;
        int beamerCheck1 = beamerCode1 % 97; // Correcte checksum

        Event event1 = new Event();
        event1.setNaam("Inleiding tot Spring Boot");
        event1.setBeschrijving("Een introductie tot het Spring Boot framework en de basisprincipes.");
        event1.setDatumTijd(eventTime1);
        event1.setLokaal(lokaalRepository.findByNaam("A101")); // Haal lokaal op om te koppelen (belangrijk voor managed entity)
        event1.setPrijs(new BigDecimal("49.99"));
        event1.setBeamercode(beamerCode1);
        event1.setBeamercheck(beamerCheck1); // Stel de berekende checksum in

        // Koppel sprekers aan event1 (haal sprekers op om zeker te zijn dat ze managed zijn)
        List<Spreker> sprekersEvent1 = Arrays.asList(sprekerRepository.findByNaam("Jan Janssen"), sprekerRepository.findByNaam("Piet Peeters"));
        event1.setSprekers(sprekersEvent1);

        // *** Gebruik de EventService om het event op te slaan ***
        // De EventService.saveEvent methode is @Transactional en beheert de relaties.
        eventService.saveEvent(event1);


        LocalDateTime eventTime2 = LocalDateTime.of(2025, 3, 16, 14, 30);
        int beamerCode2 = 5678;
        int beamerCheck2 = beamerCode2 % 97;

        Event event2 = new Event();
        event2.setNaam("Advanced JPA Techniques");
        event2.setBeschrijving("Diepgaande kijk op JPA en Hibernate voor geavanceerd databeheer.");
        event2.setDatumTijd(eventTime2);
        event2.setLokaal(lokaalRepository.findByNaam("B202")); // Haal lokaal op om te koppelen
        event2.setPrijs(new BigDecimal("75.00"));
        event2.setBeamercode(beamerCode2);
        event2.setBeamercheck(beamerCheck2);

        // Koppel sprekers aan event2 (haal spreker op)
        List<Spreker> sprekersEvent2 = Arrays.asList(sprekerRepository.findByNaam("Joris Joosten"));
        event2.setSprekers(sprekersEvent2);

        // *** Gebruik de EventService om het event op te slaan ***
        eventService.saveEvent(event2);


        System.out.println("Database initialisatie voltooid.");
    }
}