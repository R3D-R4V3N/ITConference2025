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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
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
                0,
                new BigDecimal("50.00")
        );
    }

    @Test
    @WithMockUser
    void testShowEventOverview() throws Exception {
        when(eventService.findAllEvents()).thenReturn(Collections.singletonList(testEvent));

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("event-overview"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attribute("events", Collections.singletonList(testEvent)));
    }

    @Test
    void testShowEventOverviewUnauthenticated() throws Exception {
        when(eventService.findAllEvents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("event-overview"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attribute("events", Collections.emptyList()));
    }



    @Test
    @WithMockUser(roles = {"USER"})
    void testShowAddEventFormUserForbidden() throws Exception {
        mockMvc.perform(get("/events/add"))
                .andExpect(status().is3xxRedirection()) // Verwacht een redirect
                .andExpect(redirectedUrl("/error"))    // Verwacht een redirect naar /error
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Access is denied"));
    }

    @Test
    void testShowAddEventFormUnauthenticatedRedirectToLogin() throws Exception {
        mockMvc.perform(get("/events/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testProcessAddEventFormValidData() throws Exception {
        when(eventService.saveEvent(any(Event.class))).thenReturn(testEvent);
        when(lokaalService.findLokaalById(anyLong())).thenReturn(Optional.of(testLokaal));
        when(sprekerService.findSprekerById(anyLong())).thenReturn(Optional.of(testSpreker1)).thenReturn(Optional.of(testSpreker2));


        mockMvc.perform(post("/events/add")
                        .param("naam", testEvent.getNaam())
                        .param("beschrijving", testEvent.getBeschrijving())
                        .param("sprekers[0].id", testSpreker1.getId().toString())
                        .param("sprekers[1].id", testSpreker2.getId().toString())
                        .param("lokaal.id", testLokaal.getId().toString())
                        .param("datumTijd", "2025-06-01T10:00")
                        .param("beamercode", String.valueOf(testEvent.getBeamercode()))
                        .param("beamercheck", String.valueOf(testEvent.calculateCorrectBeamerCheck()))
                        .param("prijs", testEvent.getPrijs().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));
    }


    @Test
    @WithMockUser(roles = {"USER"})
    void testProcessAddEventFormUserForbidden() throws Exception {
        mockMvc.perform(post("/events/add")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // Verwacht een redirect
                .andExpect(redirectedUrl("/error"))    // Verwacht een redirect naar /error
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Access is denied"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testShowEventDetailUserRole() throws Exception {
        when(eventService.findEventById(testEvent.getId())).thenReturn(Optional.of(testEvent));
        when(favoriteService.isEventFavoriteForUser(any(String.class), eq(testEvent.getId()))).thenReturn(false);
        when(favoriteService.getNumberOfFavoriteEventsForUser(any(String.class))).thenReturn(0L);

        mockMvc.perform(get("/events/{id}", testEvent.getId()))
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
    void testShowEventDetailUserRoleMaxFavoritesReached() throws Exception {
        when(eventService.findEventById(testEvent.getId())).thenReturn(Optional.of(testEvent));
        when(favoriteService.isEventFavoriteForUser(anyString(), eq(testEvent.getId()))).thenReturn(false);
        when(favoriteService.getNumberOfFavoriteEventsForUser(anyString())).thenReturn(5L);

        mockMvc.perform(get("/events/{id}", testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("event-detail"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attribute("event", testEvent))
                .andExpect(model().attribute("isFavorite", false))
                .andExpect(model().attribute("canAddFavorite", false))
                .andExpect(model().attribute("maxFavoritesReached", true));
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShowEventDetailAdminRole() throws Exception {
        when(eventService.findEventById(testEvent.getId())).thenReturn(Optional.of(testEvent));

        mockMvc.perform(get("/events/{id}", testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("event-detail"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attribute("event", testEvent))
                .andExpect(model().attributeDoesNotExist("isFavorite"))
                .andExpect(model().attributeDoesNotExist("canAddFavorite"));
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShowEditEventFormAdmin() throws Exception {
        when(eventService.findEventById(testEvent.getId())).thenReturn(Optional.of(testEvent));
        when(lokaalService.findAllLokalen()).thenReturn(Collections.singletonList(testLokaal));
        when(sprekerService.findAllSprekers()).thenReturn(Arrays.asList(testSpreker1, testSpreker2));

        mockMvc.perform(get("/events/edit/{id}", testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("event-add"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attribute("event", testEvent))
                .andExpect(model().attributeExists("lokalen"))
                .andExpect(model().attributeExists("sprekers"))
                .andExpect(model().attribute("isEdit", true));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testShowEditEventFormUserForbidden() throws Exception {
        mockMvc.perform(get("/events/edit/{id}", testEvent.getId()))
                .andExpect(status().is3xxRedirection()) // Verwacht een redirect
                .andExpect(redirectedUrl("/error"))    // Verwacht een redirect naar /error
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Access is denied"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShowEditEventFormNotFound() throws Exception {
        when(eventService.findEventById(any(Long.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/events/edit/{id}", 999L))
                .andExpect(status().is3xxRedirection()) // Verwacht een redirect
                .andExpect(redirectedUrl("/error"))    // Verwacht een redirect naar /error
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Evenement met ID 999 niet gevonden om te bewerken."));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testProcessEditEventFormValidData() throws Exception {
        when(eventService.saveEvent(any(Event.class))).thenReturn(testEvent);
        when(lokaalService.findLokaalById(anyLong())).thenReturn(Optional.of(testLokaal));
        when(sprekerService.findSprekerById(anyLong())).thenReturn(Optional.of(testSpreker1));


        mockMvc.perform(post("/events/edit/{id}", testEvent.getId())
                        .param("id", testEvent.getId().toString())
                        .param("naam", testEvent.getNaam())
                        .param("beschrijving", testEvent.getBeschrijving())
                        .param("sprekers[0].id", testSpreker1.getId().toString())
                        .param("lokaal.id", testLokaal.getId().toString())
                        .param("datumTijd", "2025-06-01T10:00")
                        .param("beamercode", String.valueOf(testEvent.getBeamercode()))
                        .param("beamercheck", String.valueOf(testEvent.calculateCorrectBeamerCheck()))
                        .param("prijs", testEvent.getPrijs().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events/" + testEvent.getId()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message", "Evenement succesvol bijgewerkt!"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testProcessEditEventFormInvalidData() throws Exception {
        when(lokaalService.findAllLokalen()).thenReturn(Collections.singletonList(testLokaal));
        when(sprekerService.findAllSprekers()).thenReturn(Arrays.asList(testSpreker1, testSpreker2));
        when(eventService.findEventById(anyLong())).thenReturn(Optional.of(testEvent)); // Nodig voor edit formulier

        mockMvc.perform(post("/events/edit/{id}", testEvent.getId())
                        .param("id", testEvent.getId().toString())
                        .param("naam", "")
                        .param("beschrijving", "Some description")
                        .param("sprekers[0].id", testSpreker1.getId().toString())
                        .param("lokaal.id", testLokaal.getId().toString())
                        .param("datumTijd", "2025-06-01T10:00")
                        .param("beamercode", "1234")
                        .param("beamercheck", "0")
                        .param("prijs", "50.00")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("event-add"))
                .andExpect(model().attribute("isEdit", true))
                .andExpect(model().attributeHasFieldErrors("event", "naam"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testProcessEditEventFormUserForbidden() throws Exception {
        mockMvc.perform(post("/events/edit/{id}", testEvent.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // Verwacht een redirect
                .andExpect(redirectedUrl("/error"))    // Verwacht een redirect naar /error
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Access is denied"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testShowEventDetailNotFound() throws Exception {
        when(eventService.findEventById(any(Long.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/events/{id}", 999L))
                .andExpect(status().is3xxRedirection()) // Verwacht een redirect
                .andExpect(redirectedUrl("/error"))    // Verwacht een redirect naar /error
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Evenement met ID 999 niet gevonden."));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testProcessAddEventFormInvalidData() throws Exception {
        when(lokaalService.findAllLokalen()).thenReturn(Collections.singletonList(testLokaal));
        when(sprekerService.findAllSprekers()).thenReturn(Arrays.asList(testSpreker1, testSpreker2));

        mockMvc.perform(post("/events/add")
                        .param("naam", "")
                        .param("beschrijving", "Dit is een beschrijving.")
                        .param("sprekers[0].id", testSpreker1.getId().toString())
                        .param("lokaal.id", testLokaal.getId().toString())
                        .param("datumTijd", "2025-06-01T10:00")
                        .param("beamercode", "1234")
                        .param("beamercheck", "0")
                        .param("prijs", "50.00")
                        .requestAttr("isEdit", false)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("event-add"))
                .andExpect(model().attributeHasFieldErrors("event", "naam"))
                .andExpect(model().attributeHasFieldErrors("event", "beamercheck")); // Beamercheck kan ook een fout hebben als de validator checkt op correctheid.
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testAddFavoriteEventAdminForbidden() throws Exception {
        mockMvc.perform(post("/favorites/add")
                        .param("eventId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // Verwacht een redirect
                .andExpect(redirectedUrl("/error"))    // Verwacht een redirect naar /error
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Access is denied"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testAddFavoriteEventSuccess() throws Exception {
        doNothing().when(favoriteService).addFavoriteEvent(anyString(), anyLong());

        mockMvc.perform(post("/favorites/add")
                        .param("eventId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events/1"))
                .andExpect(flash().attributeExists("message"));
    }
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testRemoveFavoriteEventSuccess() throws Exception {
        doNothing().when(favoriteService).removeFavoriteEvent(anyString(), anyLong());

        mockMvc.perform(post("/favorites/remove")
                        .param("eventId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events/1")) // Aangepast naar de werkelijke redirect URL
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowFavoriteEventsUserRole() throws Exception {
        Lokaal lokaalFav = new Lokaal(10L, "C303", 25);
        Spreker sprekerFav = new Spreker(10L, "Alice Smith", new HashSet<>());

        Event favEvent1 = new Event(
                100L,
                "Favorite Event 1",
                "Beschrijving Fav Event 1",
                Arrays.asList(sprekerFav),
                lokaalFav,
                LocalDateTime.of(2025, 7, 10, 9, 0),
                9876,
                0,
                new BigDecimal("25.00")
        );

        Event favEvent2 = new Event(
                101L,
                "Favorite Event 2",
                "Beschrijving Fav Event 2",
                Arrays.asList(testSpreker1),
                testLokaal,
                LocalDateTime.of(2025, 7, 11, 13, 0),
                5432,
                0,
                new BigDecimal("30.00")
        );

        List<Event> favoriteEvents = Arrays.asList(favEvent1, favEvent2);

        when(favoriteService.findFavoriteEventsByUsername(anyString())).thenReturn(favoriteEvents);

        mockMvc.perform(get("/favorites"))
                .andExpect(status().isOk())
                .andExpect(view().name("favorites-overview"))
                .andExpect(model().attributeExists("favoriteEvents"))
                .andExpect(model().attribute("favoriteEvents", favoriteEvents));
    }

}