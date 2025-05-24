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

        Lokaal lokaal1 = lokaalService.saveLokaal(new Lokaal("A101", 50));
        Lokaal lokaal2 = lokaalService.saveLokaal(new Lokaal("B202", 30));

        Spreker spreker1 = sprekerService.saveSpreker(new Spreker("Jan Janssen"));
        Spreker spreker2 = sprekerService.saveSpreker(new Spreker("Piet Peeters"));
        Spreker spreker3 = sprekerService.saveSpreker(new Spreker("Joris Joosten"));

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

        System.out.println("✅ Database initialisatie voltooid.");
    }
}