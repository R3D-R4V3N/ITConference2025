package com.hogent.ewdj.itconference;

import domain.Event;
import domain.Lokaal;
import domain.Spreker;
import exceptions.EventNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import service.EventService;
import service.FavoriteService;
import service.LokaalService;
import service.SprekerService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private LokaalService lokaalService;

    @MockitoBean
    private SprekerService sprekerService;

    @MockitoBean
    private FavoriteService favoriteService;

    private Event testEvent;
    private Lokaal testLokaal;
    private Spreker testSpreker1;
    private Spreker testSpreker2;

    @BeforeEach
    void setUp() {
        testLokaal = new Lokaal(1L, "A101", 50);
        testSpreker1 = new Spreker(1L, "Jan Janssen", new HashSet<>());
        testSpreker2 = new Spreker(2L, "Piet Peeters", new HashSet<>());

        testEvent = new Event(
                1L,
                "Test Event",
                "Beschrijving van Test Event",
                Arrays.asList(testSpreker1, testSpreker2),
                testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                1234,
                0, // beamercheck is transient en wordt niet door form ingevuld
                new BigDecimal("50.00")
        );
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowEventOverview() throws Exception {
        List<Event> events = Collections.singletonList(testEvent);
        when(eventService.findAllEvents()).thenReturn(events);

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("event-overview"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attribute("events", events));
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void testShowAddEventForm_Admin() throws Exception {
        when(lokaalService.findAllLokalen()).thenReturn(Collections.singletonList(testLokaal));
        when(sprekerService.findAllSprekers()).thenReturn(Arrays.asList(testSpreker1, testSpreker2));

        mockMvc.perform(get("/events/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("event-add"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("lokalen"))
                .andExpect(model().attributeExists("sprekers"));
    }

    @Test
    @WithMockUser(username = "USER", roles = {"USER"})
    void testShowAddEventForm_UserDenied() throws Exception {
        mockMvc.perform(get("/events/add"))
                .andExpect(status().isForbidden()); // Verwacht 403 Forbidden
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void testProcessAddEventForm_ValidData() throws Exception {
        when(eventService.saveEvent(any(Event.class))).thenReturn(testEvent);
        when(lokaalService.findLokaalById(any(Long.class))).thenReturn(Optional.of(testLokaal));
        when(sprekerService.findSprekerById(any(Long.class))).thenReturn(Optional.of(testSpreker1));
        when(sprekerService.findSprekerById(any(Long.class))).thenReturn(Optional.of(testSpreker2));


        mockMvc.perform(post("/events/add")
                        .param("naam", testEvent.getNaam())
                        .param("beschrijving", testEvent.getBeschrijving())
                        .param("sprekers[0].id", testSpreker1.getId().toString())
                        .param("sprekers[1].id", testSpreker2.getId().toString())
                        .param("lokaal.id", testLokaal.getId().toString())
                        .param("datumTijd", testEvent.getDatumTijd().toString())
                        .param("beamercode", String.valueOf(testEvent.getBeamercode()))
                        .param("beamercheck", String.valueOf(testEvent.getBeamercheck())) // Transient field
                        .param("prijs", testEvent.getPrijs().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void testProcessAddEventForm_InvalidData() throws Exception {
        when(lokaalService.findLokaalById(any(Long.class))).thenReturn(Optional.of(testLokaal));
        when(sprekerService.findSprekerById(any(Long.class))).thenReturn(Optional.of(testSpreker1));
        when(sprekerService.findSprekerById(any(Long.class))).thenReturn(Optional.of(testSpreker2));
        mockMvc.perform(post("/events/add")
                        .param("naam", "ValidNaam")
                        .param("beschrijving", testEvent.getBeschrijving())
                        .param("sprekers[0].id", testSpreker1.getId().toString())
                        .param("sprekers[1].id", testSpreker2.getId().toString())
                        .param("lokaal.id", testLokaal.getId().toString())
                        .param("datumTijd", testEvent.getDatumTijd().toString())
                        .param("beamercode", String.valueOf(testEvent.getBeamercode()))
                        .param("beamercheck", String.valueOf(testEvent.getBeamercheck()))
                        .param("prijs", "0.00")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("event-add"))
                .andExpect(model().attributeHasFieldErrors("event", "prijs"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowEventDetail_User() throws Exception {
        when(eventService.findEventById(1L)).thenReturn(Optional.of(testEvent));
        when(favoriteService.isEventFavoriteForUser("user", 1L)).thenReturn(false);
        when(favoriteService.getNumberOfFavoriteEventsForUser("user")).thenReturn(0L);

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("event-detail"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attribute("event", testEvent))
                .andExpect(model().attribute("isFavorite", false))
                .andExpect(model().attribute("canAddFavorite", true))
                .andExpect(model().attribute("maxFavoritesReached", false));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowEventDetail_UserIsFavorite() throws Exception {
        when(eventService.findEventById(1L)).thenReturn(Optional.of(testEvent));
        when(favoriteService.isEventFavoriteForUser("user", 1L)).thenReturn(true);
        when(favoriteService.getNumberOfFavoriteEventsForUser("user")).thenReturn(3L); // Onder max van 5

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("event-detail"))
                .andExpect(model().attribute("isFavorite", true))
                .andExpect(model().attribute("canAddFavorite", false))
                .andExpect(model().attribute("maxFavoritesReached", false));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowEventDetail_UserMaxFavoritesReached() throws Exception {
        when(eventService.findEventById(1L)).thenReturn(Optional.of(testEvent));
        when(favoriteService.isEventFavoriteForUser("user", 1L)).thenReturn(false);
        when(favoriteService.getNumberOfFavoriteEventsForUser("user")).thenReturn(5L); // Max bereikt

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("event-detail"))
                .andExpect(model().attribute("isFavorite", false))
                .andExpect(model().attribute("canAddFavorite", false))
                .andExpect(model().attribute("maxFavoritesReached", true));
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void testShowEventDetail_Admin() throws Exception {
        when(eventService.findEventById(1L)).thenReturn(Optional.of(testEvent));

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("event-detail"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attribute("event", testEvent))
                .andExpect(model().attributeDoesNotExist("isFavorite"))
                .andExpect(model().attributeDoesNotExist("canAddFavorite"))
                .andExpect(model().attributeDoesNotExist("maxFavoritesReached"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowEventDetail_NotFound() throws Exception {
        when(eventService.findEventById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/events/99"))
                .andExpect(status().isOk()) // Controller stuurt naar "error" view, niet 404
                .andExpect(view().name("error"));
    }


}