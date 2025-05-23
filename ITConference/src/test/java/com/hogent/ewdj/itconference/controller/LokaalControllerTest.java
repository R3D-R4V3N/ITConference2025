package com.hogent.ewdj.itconference.controller;

import domain.Event;
import domain.Lokaal;
import domain.MyUser;
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
import service.EventService;
import service.LokaalService;
import service.MyUserService;
import service.SprekerService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
    private EventService eventService;

    @MockitoBean
    private MyUserService myUserService;

    private Lokaal testLokaal;
    private Spreker testSpreker;

    @BeforeEach
    void setUp() {
        testLokaal = new Lokaal(1L, "A101", 50);
        testSpreker = new Spreker(1L, "Jan Janssen", new HashSet<>());

        when(lokaalService.saveLokaal(any(Lokaal.class))).thenAnswer(invocation -> {
            Lokaal l = invocation.getArgument(0);
            if (l.getId() == null) {
                l.setId(100L);
            }
            return l;
        });
        when(lokaalService.findLokaalById(anyLong())).thenReturn(Optional.of(testLokaal));
        when(lokaalService.findLokaalByNaam(anyString())).thenReturn(testLokaal);

        when(sprekerService.saveSpreker(any(Spreker.class))).thenAnswer(invocation -> {
            Spreker s = invocation.getArgument(0);
            if (s.getId() == null) {
                s.setId(200L);
            }
            return s;
        });
        when(sprekerService.findSprekerById(anyLong())).thenReturn(Optional.of(testSpreker));
        when(sprekerService.findSprekerByNaam(anyString())).thenReturn(testSpreker);


        when(myUserService.saveUser(any(MyUser.class))).thenAnswer(invocation -> {
            MyUser user = invocation.getArgument(0);
            if (user.getId() == null) {
                user.setId(300L);
            }
            return user;
        });
        when(myUserService.findByUsername(anyString())).thenAnswer(invocation -> {
            String username = invocation.getArgument(0);
            return MyUser.builder().id(300L).username(username).build();
        });


        when(eventService.saveEvent(any(Event.class))).thenAnswer(invocation -> {
            Event e = invocation.getArgument(0);
            if (e.getId() == null) {
                e.setId(400L);
            }
            return e;
        });

        when(eventService.findAllEvents()).thenReturn(Collections.emptyList());
        when(eventService.findEventById(anyLong())).thenReturn(Optional.empty());
        when(eventService.findEventsByDatumTijdAndLokaal(any(), any(Lokaal.class))).thenReturn(Collections.emptyList());
        when(eventService.findEventsByNaamAndDatum(anyString(), any())).thenReturn(Collections.emptyList());
    }

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

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testProcessAddLokaalFormValidData() throws Exception {
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
        mockMvc.perform(post("/lokalen/add")
                        .param("naam", "invalid")
                        .param("capaciteit", "0")
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