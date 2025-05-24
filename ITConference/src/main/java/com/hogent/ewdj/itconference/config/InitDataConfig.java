package com.hogent.ewdj.itconference.config;

import domain.Event;
import domain.Lokaal;
import domain.Spreker;
import domain.MyUser;
import domain.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import service.EventService;
import service.LokaalService;
import service.SprekerService;
import service.MyUserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Order(1)
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
    private MyUserService myUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("⚙️ Database initialiseren met voorbeeld data...");

        MyUser adminUser = MyUser.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .role(Role.ADMIN)
                .build();

        MyUser standardUser = MyUser.builder()
                .username("user")
                .password(passwordEncoder.encode("user"))
                .role(Role.USER)
                .build();

        myUserService.saveUser(adminUser);
        myUserService.saveUser(standardUser);

        System.out.println("👥 Standaard gebruikers aangemaakt: admin/admin en user/user");

        // Bestaande lokalen
        Lokaal lokaal1 = lokaalService.saveLokaal(new Lokaal("A101", 50));
        Lokaal lokaal2 = lokaalService.saveLokaal(new Lokaal("B202", 30));
        System.out.println("🏢 Lokaal A101 (Capaciteit: 50) aangemaakt.");
        System.out.println("🏢 Lokaal B202 (Capaciteit: 30) aangemaakt.");

        // Nieuwe lokalen
        Lokaal lokaal3 = lokaalService.saveLokaal(new Lokaal("C303", 40));
        Lokaal lokaal4 = lokaalService.saveLokaal(new Lokaal("D404", 25));
        System.out.println("🏢 Lokaal C303 (Capaciteit: 40) aangemaakt.");
        System.out.println("🏢 Lokaal D404 (Capaciteit: 25) aangemaakt.");


        // Sprekers
        Spreker spreker1 = sprekerService.saveSpreker(new Spreker("Jan Janssen"));
        System.out.println("🎤 Spreker 'Jan Janssen' aangemaakt.");
        Spreker spreker2 = sprekerService.saveSpreker(new Spreker("Piet Peeters"));
        System.out.println("🎤 Spreker 'Piet Peeters' aangemaakt.");
        Spreker spreker3 = sprekerService.saveSpreker(new Spreker("Joris Joosten"));
        System.out.println("🎤 Spreker 'Joris Joosten' aangemaakt.");
        Spreker spreker4 = sprekerService.saveSpreker(new Spreker("Klaas Klaassen"));
        System.out.println("🎤 Spreker 'Klaas Klaassen' aangemaakt.");
        Spreker spreker5 = sprekerService.saveSpreker(new Spreker("Lisa Linders"));
        System.out.println("🎤 Spreker 'Lisa Linders' aangemaakt.");
        Spreker spreker6 = sprekerService.saveSpreker(new Spreker("Tom Theuns"));
        System.out.println("🎤 Spreker 'Tom Theuns' aangemaakt.");
        Spreker spreker7 = sprekerService.saveSpreker(new Spreker("Sofie Smits"));
        System.out.println("🎤 Spreker 'Sofie Smits' aangemaakt.");


        // Bestaand event 1
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

        eventService.saveEvent(event1);
        System.out.println("📅 Event 'Inleiding tot Spring Boot' aangemaakt.");

        // Bestaand event 2
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

        eventService.saveEvent(event2);
        System.out.println("📅 Event 'Advanced JPA Techniques' aangemaakt.");

        // Nieuw event 3
        LocalDateTime eventTime3 = LocalDateTime.now().plusDays(9).withHour(9).withMinute(0).withSecond(0).withNano(0);
        int beamerCode3 = 9876;
        int beamerCheck3 = beamerCode3 % 97;

        Event event3 = new Event();
        event3.setNaam("Microservices with Spring Cloud");
        event3.setBeschrijving("Leer hoe je schaalbare microservices bouwt met Spring Cloud.");
        event3.setDatumTijd(eventTime3);
        event3.setLokaal(lokaal3);
        event3.setPrijs(new BigDecimal("99.99"));
        event3.setBeamercode(beamerCode3);
        event3.setBeamercheck(beamerCheck3);

        List<Spreker> sprekersEvent3 = Arrays.asList(spreker1, spreker4);
        event3.setSprekers(sprekersEvent3);

        eventService.saveEvent(event3);
        System.out.println("📅 Event 'Microservices with Spring Cloud' aangemaakt.");

        // Nieuw event 4
        LocalDateTime eventTime4 = LocalDateTime.now().plusDays(10).withHour(11).withMinute(0).withSecond(0).withNano(0);
        int beamerCode4 = 2468;
        int beamerCheck4 = beamerCode4 % 97;

        Event event4 = new Event();
        event4.setNaam("Secure API Development");
        event4.setBeschrijving("Best practices voor het beveiligen van RESTful APIs.");
        event4.setDatumTijd(eventTime4);
        event4.setLokaal(lokaal4);
        event4.setPrijs(new BigDecimal("60.50"));
        event4.setBeamercode(beamerCode4);
        event4.setBeamercheck(beamerCheck4);

        List<Spreker> sprekersEvent4 = Arrays.asList(spreker2, spreker5);
        event4.setSprekers(sprekersEvent4);

        eventService.saveEvent(event4);
        System.out.println("📅 Event 'Secure API Development' aangemaakt.");


        System.out.println("✅ Database initialisatie voltooid.");
    }
}