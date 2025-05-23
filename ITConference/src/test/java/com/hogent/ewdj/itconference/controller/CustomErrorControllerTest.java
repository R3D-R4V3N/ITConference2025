package com.hogent.ewdj.itconference.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpSession;

import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.messages.basename=i18n/messages,i18n/messages_nl"})
class CustomErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {

        lenient().doReturn("Fallback message for null code (4-arg)")
                .when(messageSource).getMessage(isNull(), nullable(Object[].class), anyString(), any(Locale.class));

        lenient().doReturn("Fallback message for null code (3-arg)")
                .when(messageSource).getMessage(isNull(), nullable(Object[].class), any(Locale.class));

        lenient().doReturn("Fallback message for null code (2-arg)")
                .when(messageSource).getMessage(isNull(), any(Locale.class));


        doReturn("Pagina niet gevonden")
                .when(messageSource).getMessage(eq("error.404"), nullable(Object[].class), eq("Page not found"), any(Locale.class));
        doReturn("Toegang geweigerd")
                .when(messageSource).getMessage(eq("error.403"), nullable(Object[].class), eq("Access is denied"), any(Locale.class));
        doReturn("Er is een onverwachte fout opgetreden")
                .when(messageSource).getMessage(eq("error.unexpected"), nullable(Object[].class), eq("An unexpected error occurred"), any(Locale.class));
    }

    @Test
    void testHandleErrorWithRequestParams() throws Exception {
        mockMvc.perform(get("/error")
                        .param("errorCode", "500")
                        .param("errorMessage", "Custom Error Message"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorCode", "500"))
                .andExpect(model().attribute("errorMessage", "Custom Error Message"));
    }

    @Test
    void testHandleErrorWithSessionAttributes() throws Exception {
        mockMvc.perform(get("/error")
                        .sessionAttr("errorCode", "401")
                        .sessionAttr("errorMessage", "Unauthorized Access"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorCode", "401"))
                .andExpect(model().attribute("errorMessage", "Unauthorized Access"));
    }

    @Test
    void testHandleErrorWith404Status() throws Exception {
        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorCode", "404"))
                .andExpect(model().attribute("errorMessage", "Pagina niet gevonden"));
    }

    @Test
    void testHandleErrorWith403Status() throws Exception {
        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorCode", "403"))
                .andExpect(model().attribute("errorMessage", "Toegang geweigerd"));
    }

    @Test
    void testHandleErrorWith500Status() throws Exception {
        mockMvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorCode", "500"))
                .andExpect(model().attribute("errorMessage", "Er is een onverwachte fout opgetreden"));
    }

    @Test
    void testHandleErrorWithNoSpecificAttributes() throws Exception {
        mockMvc.perform(get("/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorCode", (String) null))
                .andExpect(model().attribute("errorMessage", (String) null));
    }

    @Test
    void testHandleErrorPrioritizesSessionOverRequestParams() throws Exception {
        mockMvc.perform(get("/error")
                        .param("errorCode", "ParamCode")
                        .param("errorMessage", "ParamMessage")
                        .sessionAttr("errorCode", "SessionCode")
                        .sessionAttr("errorMessage", "SessionMessage"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorCode", "SessionCode"))
                .andExpect(model().attribute("errorMessage", "SessionMessage"));
    }

    @Test
    void testSessionAttributesAreClearedAfterUse() throws Exception {
        MockHttpSession session = (MockHttpSession) mockMvc.perform(get("/error")
                        .sessionAttr("errorCode", "TEST_CODE")
                        .sessionAttr("errorMessage", "TEST_MESSAGE"))
                .andReturn().getRequest().getSession();

        mockMvc.perform(get("/error").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeDoesNotExist("errorCode"))
                .andExpect(model().attributeDoesNotExist("errorMessage"));
    }
}