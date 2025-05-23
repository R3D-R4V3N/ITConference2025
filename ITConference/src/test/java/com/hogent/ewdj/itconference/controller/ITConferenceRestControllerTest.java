package com.hogent.ewdj.itconference.controller;

import domain.Event;
import domain.Lokaal;
import domain.MyUser; // Importeer MyUser
import domain.Spreker;
import exceptions.LokaalNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import service.EventService;
import service.LokaalService;
import service.MyUserService; // Importeer MyUserService
import service.SprekerService; // Importeer SprekerService

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ITConferenceRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private LokaalService lokaalService;

    @MockitoBean // TOEVOEGD: SprekerService als mock bean
    private SprekerService sprekerService;

    @MockitoBean // TOEVOEGD: MyUserService als mock bean
    private MyUserService myUserService;

    private Lokaal testLokaal;
    private Spreker testSpreker;
    private Event testEvent;

    @BeforeEach
    void setUp() {
        testLokaal = new Lokaal(1L, "A101", 50);
        testSpreker = new Spreker(1L, "Test Spreker", new HashSet<>());

        testEvent = new Event(
                1L,
                "Test Event Naam",
                "Test Beschrijving",
                Arrays.asList(testSpreker),
                testLokaal,
                LocalDateTime.of(2025, 10, 26, 14, 0),
                1234,
                0, // beamercheck zal correct berekend worden in de constructor of via een setter
                new BigDecimal("50.00")
        );

        // Algemene mocks voor de test context initialisatie, zodat InitDataConfig slaagt
        when(lokaalService.saveLokaal(any(Lokaal.class))).thenAnswer(invocation -> {
            Lokaal l = invocation.getArgument(0);
            if (l.getId() == null) {
                l.setId(100L); // Dummy ID
            }
            return l;
        });
        when(lokaalService.findLokaalById(anyLong())).thenReturn(Optional.of(testLokaal));
        when(lokaalService.findLokaalByNaam(anyString())).thenReturn(testLokaal);


        when(sprekerService.saveSpreker(any(Spreker.class))).thenAnswer(invocation -> {
            Spreker s = invocation.getArgument(0);
            if (s.getId() == null) {
                s.setId(200L); // Dummy ID
            }
            return s;
        });
        when(sprekerService.findSprekerById(anyLong())).thenReturn(Optional.of(testSpreker));
        when(sprekerService.findSprekerByNaam(anyString())).thenReturn(testSpreker);


        when(eventService.saveEvent(any(Event.class))).thenAnswer(invocation -> {
            Event e = invocation.getArgument(0);
            if (e.getId() == null) {
                e.setId(400L); // Dummy ID
            }
            return e;
        });
        // Zorg ervoor dat EventService.findAllEvents altijd een lijst retourneert, ook al is deze leeg
        when(eventService.findAllEvents()).thenReturn(Collections.emptyList());


        // Mock MyUserService.saveUser, gebruikt in InitDataConfig
        when(myUserService.saveUser(any(MyUser.class))).thenAnswer(invocation -> {
            MyUser user = invocation.getArgument(0);
            user.setId(500L); // Geef een dummy ID om het beheerd te maken
            return user;
        });
        // Mock MyUserService.findByUsername, gebruikt in InitDataConfig, SecurityConfig, etc.
        when(myUserService.findByUsername(anyString())).thenAnswer(invocation -> {
            String username = invocation.getArgument(0);
            // Een eenvoudige mock MyUser, voldoende voor InitDataConfig
            return MyUser.builder().id(500L).username(username).build();
        });
    }

    // --- Tests voor getEventsByDate ---

    @Test
    void testGetEventsByDateSuccess() throws Exception {
        LocalDate date = LocalDate.of(2025, 10, 26);
        List<Event> eventsOnDate = Collections.singletonList(testEvent);

        // Overschrijf de algemene mock voor findAllEvents voor deze specifieke test
        when(eventService.findAllEvents()).thenReturn(eventsOnDate);

        mockMvc.perform(get("/api/eventsByDate")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].naam").value(testEvent.getNaam()))
                .andExpect(jsonPath("$[0].lokaal.naam").value(testEvent.getLokaal().getNaam()));
    }

    @Test
    void testGetEventsByDateNoEventsFound() throws Exception {
        LocalDate date = LocalDate.of(2025, 1, 1);
        // Voor deze test is de algemene mock (returns emptyList) voldoende,
        // maar expliciet herhalen voor duidelijkheid is geen kwaad.
        when(eventService.findAllEvents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/eventsByDate")
                        .param("date", date.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetEventsByDateInvalidFormat() throws Exception {
        mockMvc.perform(get("/api/eventsByDate")
                        .param("date", "invalid-date"))
                .andExpect(status().isBadRequest()); // Verwacht HTTP 400 Bad Request voor ongeldig datumformaat
    }

    // --- Tests voor getLokaalCapaciteit ---

    @Test
    void testGetLokaalCapaciteitSuccess() throws Exception {
        String lokaalNaam = "A101";
        when(lokaalService.findLokaalByNaam(lokaalNaam)).thenReturn(testLokaal);

        mockMvc.perform(get("/api/lokalen/{naam}/capaciteit", lokaalNaam))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(testLokaal.getCapaciteit()));
    }

    @Test
    void testGetLokaalCapaciteitNotFound() throws Exception {
        String nonExistentLokaal = "Z999";
        when(lokaalService.findLokaalByNaam(nonExistentLokaal)).thenReturn(null);

        mockMvc.perform(get("/api/lokalen/{naam}/capaciteit", nonExistentLokaal))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Lokaal met naam " + nonExistentLokaal + " niet gevonden."));
    }
}