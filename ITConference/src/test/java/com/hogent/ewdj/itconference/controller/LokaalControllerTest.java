package com.hogent.ewdj.itconference.controller;

import domain.Event; // Import Event
import domain.Lokaal;
import domain.MyUser; // Import MyUser
import domain.Spreker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import service.EventService; // Import EventService
import service.LokaalService;
import service.MyUserService; // Import MyUserService
import service.SprekerService;

import java.util.Collections;
import java.util.HashSet; // Import HashSet
import java.util.List;
import java.util.Optional; // Import Optional

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong; // Import anyLong
import static org.mockito.ArgumentMatchers.anyString; // Import anyString
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.messages.basename=i18n/messages,i18n/messages_nl"})
class LokaalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LokaalService lokaalService;

    @MockitoBean
    private SprekerService sprekerService;

    @MockitoBean
    private EventService eventService; // Nodig voor InitDataConfig

    @MockitoBean
    private MyUserService myUserService; // Nodig voor InitDataConfig

    private Lokaal testLokaal;
    private Spreker testSpreker;

    @BeforeEach
    void setUp() {
        testLokaal = new Lokaal(1L, "A101", 50);
        testSpreker = new Spreker(1L, "Jan Janssen", new HashSet<>());

        // --- Mocking voor InitDataConfig om succesvol te laten laden ---

        // Mock LokaalService.saveLokaal
        when(lokaalService.saveLokaal(any(Lokaal.class))).thenAnswer(invocation -> {
            Lokaal l = invocation.getArgument(0);
            if (l.getId() == null) { // Zorg voor een ID als het een nieuw object is
                l.setId(100L); // Dummy ID
            }
            return l;
        });
        // Mock LokaalService.findLokaalById, gebruikt in EventService
        when(lokaalService.findLokaalById(anyLong())).thenReturn(Optional.of(testLokaal));
        // Mock LokaalService.findLokaalByNaam, gebruikt in ITConferenceRestController of andere plaatsen
        when(lokaalService.findLokaalByNaam(anyString())).thenReturn(testLokaal);

        // Mock SprekerService.saveSpreker
        when(sprekerService.saveSpreker(any(Spreker.class))).thenAnswer(invocation -> {
            Spreker s = invocation.getArgument(0);
            if (s.getId() == null) { // Zorg voor een ID als het een nieuw object is
                s.setId(200L); // Dummy ID
            }
            return s;
        });
        // Mock SprekerService.findSprekerById, gebruikt in EventService
        when(sprekerService.findSprekerById(anyLong())).thenReturn(Optional.of(testSpreker));
        // Mock SprekerService.findSprekerByNaam, gebruikt in EventService
        when(sprekerService.findSprekerByNaam(anyString())).thenReturn(testSpreker);


        // Mock MyUserService.saveUser
        when(myUserService.saveUser(any(MyUser.class))).thenAnswer(invocation -> {
            MyUser user = invocation.getArgument(0);
            if (user.getId() == null) { // Zorg voor een ID als het een nieuw object is
                user.setId(300L); // Dummy ID
            }
            return user;
        });
        // Mock MyUserService.findByUsername
        when(myUserService.findByUsername(anyString())).thenAnswer(invocation -> {
            String username = invocation.getArgument(0);
            return MyUser.builder().id(300L).username(username).build();
        });


        // Mock EventService.saveEvent, zodat InitDataConfig succesvol Events kan opslaan
        when(eventService.saveEvent(any(Event.class))).thenAnswer(invocation -> {
            Event e = invocation.getArgument(0);
            if (e.getId() == null) { // Zorg voor een ID als het een nieuw object is
                e.setId(400L); // Dummy ID
            }
            return e;
        });

        // Mock EventService.findAllEvents, gebruikt in EventController, ITConferenceRestController
        when(eventService.findAllEvents()).thenReturn(Collections.emptyList()); // Standaard leeg, tenzij overschreven voor specifieke tests
        // Mock EventService.findEventById, gebruikt in EventController
        when(eventService.findEventById(anyLong())).thenReturn(Optional.empty()); // Standaard leeg, tenzij overschreven
        // Mock EventService.findEventsByDatumTijdAndLokaal, gebruikt in EventConstraintsValidator
        when(eventService.findEventsByDatumTijdAndLokaal(any(), any(Lokaal.class))).thenReturn(Collections.emptyList());
        // Mock EventService.findEventsByNaamAndDatum, gebruikt in EventConstraintsValidator
        when(eventService.findEventsByNaamAndDatum(anyString(), any())).thenReturn(Collections.emptyList());
    }

    // --- showAddLokaalForm tests (GET /lokalen/add) ---

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShowAddLokaalFormAdmin() throws Exception {
        mockMvc.perform(get("/lokalen/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("lokaal-add"))
                .andExpect(model().attributeExists("lokaal"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testShowAddLokaalFormUserForbidden() throws Exception {
        mockMvc.perform(get("/lokalen/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error"));
    }

    @Test
    void testShowAddLokaalFormUnauthenticatedRedirectToLogin() throws Exception {
        mockMvc.perform(get("/lokalen/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    // --- processAddLokaalForm tests (POST /lokalen/add) ---

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testProcessAddLokaalFormValidData() throws Exception {
        // Zorg ervoor dat existsLokaalByNaam false is voor een succesvolle toevoeging
        when(lokaalService.existsLokaalByNaam(anyString())).thenReturn(false);

        mockMvc.perform(post("/lokalen/add")
                        .param("naam", "A101")
                        .param("capaciteit", "50")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lokalen"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message", "Zaal met 50 zitplaatsen werd toegevoegd."));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testProcessAddLokaalFormInvalidData() throws Exception {
        // Hier hoeven we existsLokaalByNaam niet te mocken, omdat de validatie
        // al faalt voordat die service wordt aangeroepen.
        mockMvc.perform(post("/lokalen/add")
                        .param("naam", "invalid") // Ongeldig patroon
                        .param("capaciteit", "0") // Ongeldige minimum capaciteit
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("lokaal-add"))
                .andExpect(model().attributeHasFieldErrors("lokaal", "naam", "capaciteit"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testProcessAddLokaalFormUserForbidden() throws Exception {
        mockMvc.perform(post("/lokalen/add")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error"));
    }

    @Test
    void testProcessAddLokaalFormUnauthenticatedRedirectToLogin() throws Exception {
        mockMvc.perform(post("/lokalen/add")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    // --- showLokaalOverview tests (GET /lokalen) ---

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testShowLokaalOverviewAdmin() throws Exception {
        List<Lokaal> lokalen = Collections.singletonList(testLokaal);
        when(lokaalService.findAllLokalen()).thenReturn(lokalen);

        mockMvc.perform(get("/lokalen"))
                .andExpect(status().isOk())
                .andExpect(view().name("lokaal-overview"))
                .andExpect(model().attributeExists("lokalen"))
                .andExpect(model().attribute("lokalen", lokalen));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testShowLokaalOverviewUserForbidden() throws Exception {
        mockMvc.perform(get("/lokalen"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error"));
    }

    @Test
    void testShowLokaalOverviewUnauthenticatedRedirectToLogin() throws Exception {
        mockMvc.perform(get("/lokalen"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}