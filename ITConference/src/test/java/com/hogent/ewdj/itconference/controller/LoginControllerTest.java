package com.hogent.ewdj.itconference.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import service.MyUserService;
import service.EventService;
import service.LokaalService;
import service.SprekerService;
import domain.MyUser;
import domain.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.messages.basename=i18n/messages,i18n/messages_nl"})
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private MyUserService myUserService;
    @MockitoBean private UserDetailsService userDetailsService;
    @MockitoBean private PasswordEncoder passwordEncoder;

    @MockitoBean private EventService eventService;
    @MockitoBean private LokaalService lokaalService;
    @MockitoBean private SprekerService sprekerService;


    @BeforeEach
    void setUp() {

        given(myUserService.saveUser(any(MyUser.class))).willAnswer(invocation -> {
            MyUser user = invocation.getArgument(0);
            if (user.getId() == null) user.setId(1L);
            return user;
        });
        given(myUserService.findByUsername("user")).willReturn(MyUser.builder().username("user").password("encodedPassword").role(Role.USER).build());
        given(myUserService.findByUsername("admin")).willReturn(MyUser.builder().username("admin").password("encodedPassword").role(Role.ADMIN).build());
        given(myUserService.findByUsername(anyString())).willReturn(MyUser.builder().username("unknown").password("encodedPassword").role(Role.USER).build());


        given(userDetailsService.loadUserByUsername(anyString())).willAnswer(invocation -> {
            String username = invocation.getArgument(0);
            if ("user".equals(username)) {
                return org.springframework.security.core.userdetails.User.withUsername("user")
                        .password("encodedPassword")
                        .roles("USER")
                        .build();
            } else if ("admin".equals(username)) {
                return org.springframework.security.core.userdetails.User.withUsername("admin")
                        .password("encodedPassword")
                        .roles("ADMIN")
                        .build();
            } else {
                return null;
            }
        });


        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

        given(passwordEncoder.matches(anyString(), anyString())).willAnswer(invocation -> {
            String rawPassword = invocation.getArgument(0);
            String encodedPasswordStoredInDB = invocation.getArgument(1);

            if ("anyPassword".equals(rawPassword) && "encodedPassword".equals(encodedPasswordStoredInDB)) {
                return true;
            }
            return false;
        });


        given(eventService.saveEvent(any())).willAnswer(invocation -> invocation.getArgument(0));
        given(lokaalService.saveLokaal(any())).willAnswer(invocation -> invocation.getArgument(0));
        given(sprekerService.saveSpreker(any())).willAnswer(invocation -> invocation.getArgument(0));
        given(lokaalService.findLokaalById(anyLong())).willReturn(Optional.empty());
        given(sprekerService.findSprekerById(anyLong())).willReturn(Optional.empty());
        given(sprekerService.findSprekerByNaam(anyString())).willReturn(null);
        given(lokaalService.findLokaalByNaam(anyString())).willReturn(null);
    }

    @Test
    @WithAnonymousUser
    void testShowLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeDoesNotExist("msg"));
    }

    @Test
    @WithAnonymousUser
    void testShowLoginFormWithError() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Ongeldige gebruikersnaam of wachtwoord!"));
    }

    @Test
    @WithAnonymousUser
    void testShowLoginFormWithLogoutMessage() throws Exception {
        mockMvc.perform(get("/login").param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("msg"))
                .andExpect(model().attribute("msg", "Je bent succesvol afgemeld."));
    }

    @Test
    void testSuccessfulLogin() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("username", "user")
                        .password("password", "anyPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));
    }

    @Test
    void testFailedLogin() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("username", "wronguser")
                        .password("password", "wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @WithMockUser
    void testLogoutSuccess() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}