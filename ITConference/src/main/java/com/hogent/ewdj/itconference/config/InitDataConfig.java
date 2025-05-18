package com.hogent.ewdj.itconference.config; // Pas dit package aan

import domain.Event;
import domain.Lokaal;
import domain.Spreker;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@Transactional // Beheer transacties op dit niveau
public class InitDataConfig implements CommandLineRunner {

    @PersistenceContext // Injecteer de JPA EntityManager
    private EntityManager em;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Database initialiseren met voorbeeld data...");

        // Begin een transactie (als @Transactional op de klasse niet genoeg is)
        // em.getTransaction().begin();

        // Voorbeeld Lokalen aanmaken en persisteren
        Lokaal lokaal1 = new Lokaal("A101", 50);
        Lokaal lokaal2 = new Lokaal("B202", 30);
        em.persist(lokaal1); // Gebruik EntityManager.persist

        em.persist(lokaal2);

        // Zorg ervoor dat de objecten gesynced zijn met de database om IDs te krijgen
        em.flush();

        // Voorbeeld Sprekers aanmaken en persisteren
        Spreker spreker1 = new Spreker("Jan Janssen");
        Spreker spreker2 = new Spreker("Piet Peeters");
        Spreker spreker3 = new Spreker("Joris Joosten");
        em.persist(spreker1);
        em.persist(spreker2);
        em.persist(spreker3);

        em.flush(); // Sync opnieuw voor Spreker IDs

        // Voorbeeld Events aanmaken en persisteren
        LocalDateTime eventTime1 = LocalDateTime.of(2025, 3, 15, 10, 0); // Voorbeelddatum binnen maart 2025
        int beamerCode1 = 1234;
        int beamerCheck1 = beamerCode1 % 97; // Correcte checksum

        Event event1 = new Event();
        event1.setNaam("Inleiding tot Spring Boot");
        event1.setBeschrijving("Een introductie tot het Spring Boot framework en de basisprincipes.");
        event1.setDatumTijd(eventTime1);
        // Omdat we EntityManager gebruiken, moeten we de managed Lokaal/Spreker objecten gebruiken
        // die we net hebben gepersisteerd. We halen ze op met find.
        event1.setLokaal(em.find(Lokaal.class, lokaal1.getId()));
        event1.setPrijs(new BigDecimal("49.99"));
        event1.setBeamercode(beamerCode1);
        event1.setBeamercheck(beamerCheck1);

        // Koppel sprekers aan event1 (haal sprekers op met find)
        List<Spreker> sprekersEvent1 = Arrays.asList(em.find(Spreker.class, spreker1.getId()), em.find(Spreker.class, spreker2.getId()));
        event1.setSprekers(sprekersEvent1);

        // *** Persisteer het Event via EntityManager ***
        em.persist(event1);


        LocalDateTime eventTime2 = LocalDateTime.of(2025, 3, 16, 14, 30);
        int beamerCode2 = 5678;
        int beamerCheck2 = beamerCode2 % 97;

        Event event2 = new Event();
        event2.setNaam("Advanced JPA Techniques");
        event2.setBeschrijving("Diepgaande kijk op JPA en Hibernate voor geavanceerd databeheer.");
        event2.setDatumTijd(eventTime2);
        event2.setLokaal(em.find(Lokaal.class, lokaal2.getId())); // Haal lokaal op
        event2.setPrijs(new BigDecimal("75.00"));
        event2.setBeamercode(beamerCode2);
        event2.setBeamercheck(beamerCheck2);

        List<Spreker> sprekersEvent2 = Arrays.asList(em.find(Spreker.class, spreker3.getId())); // Haal spreker op
        event2.setSprekers(sprekersEvent2);

        em.persist(event2);


        // Commit de transactie (als @Transactional op de klasse niet genoeg is)
        // em.getTransaction().commit();

        System.out.println("Database initialisatie voltooid.");
    }
}