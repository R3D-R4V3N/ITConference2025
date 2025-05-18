package com.hogent.ewdj.itconference.controller;

import domain.Event; // Importeer de Event klasse
import service.EventService; // Importeer de EventService

import org.springframework.beans.factory.annotation.Autowired; // Nodig voor injectie
import org.springframework.stereotype.Controller; // Markeer deze klasse als een Spring Controller
import org.springframework.ui.Model; // Nodig om data door te geven aan de template
import org.springframework.web.bind.annotation.GetMapping; // Annotation voor HTTP GET verzoeken
import org.springframework.web.bind.annotation.RequestMapping; // Basis URL mapping voor de controller

import java.util.List;

@Controller // Spring detecteert deze klasse als een webcontroller
@RequestMapping("/events") // De basis URL voor deze controller is /events
public class EventController {

    @Autowired // Injecteer de EventService
    private EventService eventService;

    /**
     * Handelt GET verzoeken voor /events af en toont een overzicht van alle events.
     * @param model Het Model object om data aan de view door te geven.
     * @return De naam van de Thymeleaf template die gerenderd moet worden.
     */
    @GetMapping // Handelt GET verzoeken af op de basis URL (/events)
    public String showEventOverview(Model model) {
        // Haal alle events op via de EventService
        List<Event> events = eventService.findAllEvents();

        // Voeg de lijst met events toe aan het Model, zodat deze beschikbaar is in de Thymeleaf template
        model.addAttribute("events", events);

        // Retourneer de naam van de Thymeleaf template (zonder .html extensie en zonder de 'templates/' prefix)
        return "event-overview";
    }

    // Later voegen we hier methoden toe voor het tonen van individuele events, toevoegen, bewerken, etc.
}