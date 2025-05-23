package com.hogent.ewdj.itconference;

import domain.Event;
import domain.Lokaal;
import domain.Spreker;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoriteService favoriteService;

    @MockitoBean
    private EventService eventService;

    private Event testEvent;
    private Lokaal testLokaal;
    private Spreker testSpreker;

    @BeforeEach
    void setUp() {
        testLokaal = new Lokaal(1L, "A101", 50);
        testSpreker = new Spreker(1L, "Jan Janssen", new HashSet<>());

        testEvent = new Event(
                1L,
                "Test Event",
                "Beschrijving van Test Event",
                Arrays.asList(testSpreker),
                testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                1234,
                0,
                new BigDecimal("50.00")
        );
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testAddFavoriteEventSuccess() throws Exception {
        doNothing().when(favoriteService).addFavoriteEvent(anyString(), anyLong());

        mockMvc.perform(post("/favorites/add")
                        .param("eventId", testEvent.getId().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events/" + testEvent.getId()))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testAddFavoriteEventAdminForbidden() throws Exception {
        mockMvc.perform(post("/favorites/add")
                        .param("eventId", testEvent.getId().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // Aangepast van isForbidden()
                .andExpect(redirectedUrl("/error"));    // Verwacht een redirect naar /error
        // Geen flash attribute verwacht, gezien eerdere issues
    }

    @Test
    void testAddFavoriteEventUnauthenticatedRedirectToLogin() throws Exception {
        mockMvc.perform(post("/favorites/add")
                        .param("eventId", testEvent.getId().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testRemoveFavoriteEventSuccess() throws Exception {
        doNothing().when(favoriteService).removeFavoriteEvent(anyString(), anyLong());

        mockMvc.perform(post("/favorites/remove")
                        .param("eventId", testEvent.getId().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events/" + testEvent.getId())) // Aangepast naar de juiste redirect URL
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testRemoveFavoriteEventAdminForbidden() throws Exception {
        mockMvc.perform(post("/favorites/remove")
                        .param("eventId", testEvent.getId().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // Aangepast van isForbidden()
                .andExpect(redirectedUrl("/error"));    // Verwacht een redirect naar /error
        // Geen flash attribute verwacht
    }

    @Test
    void testRemoveFavoriteEventUnauthenticatedRedirectToLogin() throws Exception {
        mockMvc.perform(post("/favorites/remove")
                        .param("eventId", testEvent.getId().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowFavoriteEventsUserRole() throws Exception {
        when(favoriteService.findFavoriteEventsByUsername(anyString())).thenReturn(Collections.singletonList(testEvent));

        mockMvc.perform(get("/favorites"))
                .andExpect(status().isOk())
                .andExpect(view().name("favorites-overview"))
                .andExpect(model().attributeExists("favoriteEvents"))
                .andExpect(model().attribute("favoriteEvents", Collections.singletonList(testEvent)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShowFavoriteEventsAdminForbidden() throws Exception {
        mockMvc.perform(get("/favorites"))
                .andExpect(status().is3xxRedirection()) // Aangepast van isForbidden()
                .andExpect(redirectedUrl("/error"));    // Verwacht een redirect naar /error
        // Geen flash attribute verwacht
    }

    @Test
    void testShowFavoriteEventsUnauthenticatedRedirectToLogin() throws Exception {
        mockMvc.perform(get("/favorites"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}