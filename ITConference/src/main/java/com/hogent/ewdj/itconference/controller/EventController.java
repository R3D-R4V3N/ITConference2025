package com.hogent.ewdj.itconference.controller;

import domain.Event;
import domain.Lokaal;
import domain.Spreker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Behoud deze import
import org.springframework.validation.annotation.Validated; // Behoud deze import
import org.springframework.web.bind.annotation.*;
import service.EventService;
import service.LokaalService;
import service.SprekerService;
import service.FavoriteService;

import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private LokaalService lokaalService;

    @Autowired
    private SprekerService sprekerService;

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping
    public String showEventOverview(Model model) {
        List<Event> events = eventService.findAllEvents();
        model.addAttribute("events", events);
        return "event-overview";
    }

    @GetMapping("/add")
    public String showAddEventForm(Model model) {
        Event event = new Event();
        List<Lokaal> beschikbareLokalen = lokaalService.findAllLokalen();
        List<Spreker> beschikbareSprekers = sprekerService.findAllSprekers();

        model.addAttribute("event", event);
        model.addAttribute("lokalen", beschikbareLokalen);
        model.addAttribute("sprekers", beschikbareSprekers);

        return "event-add";
    }

    @PostMapping("/add")
    public String processAddEventForm(@Validated @ModelAttribute("event") Event event, BindingResult result, Model model) {

        // Deze if-check wordt nu redundant omdat de @ControllerAdvice de MethodArgumentNotValidException zal opvangen.
        // We laten de BindingResult als parameter, want die is nodig voor de validatie.
        // Als er fouten zijn, zal de @ControllerAdvice de controle overnemen.
        if (result.hasErrors()) {
            // Deze logica moet nu ergens anders worden afgehandeld, bijv. in de @ControllerAdvice
            // of je moet de user terugsturen naar het formulier met de errors die in het model zitten.
            // Voor nu houden we de fallback om de view op te vullen, maar de ideale aanpak is dat de
            // @ControllerAdvice beslist waar de gebruiker naartoe gaat na een validatiefout.
            // Een optie is om een exception te gooien die de ControllerAdvice oppakt en de juiste view teruggeeft
            // met de BindingResult object in het model.
            // Voor een simpele aanpassing zoals gevraagd, verwijderen we de expliciete error handling hier,
            // en laten we de ControllerAdvice de MethodArgumentNotValidException afhandelen.
            // Echter, als de ControllerAdvice een generieke foutpagina teruggeeft, verlies je de veldfouten op het formulier.
            // De beste aanpak hiervoor is een @InitBinder in een @ControllerAdvice om de validators te registreren.
            // En vervolgens in de controller de result.hasErrors() te gebruiken om de view terug te geven met de fouten.
            // Dit is de meest gangbare en flexibele manier in Spring MVC.
            // Dus, laten we de `if (result.hasErrors())` check hier *behouden*,
            // maar de `ITConferenceErrorAdvice` alleen de *runtime* exceptions laten afhandelen.
            // De `examenopdracht_Java_Spring_24-25 (1).pdf` vraagt om Jakarta Validation, wat betekent dat
            // BindingResult en de if-check op `hasErrors()` hier horen.
            List<Lokaal> beschikbareLokalen = lokaalService.findAllLokalen();
            List<Spreker> beschikbareSprekers = sprekerService.findAllSprekers();

            model.addAttribute("lokalen", beschikbareLokalen);
            model.addAttribute("sprekers", beschikbareSprekers);

            return "event-add";
        }

        eventService.saveEvent(event);
        return "redirect:/events";
    }

    @GetMapping("/{id}")
    public String showEventDetail(@PathVariable("id") Long id, Model model, Authentication authentication) {
        Event event = eventService.findEventById(id).orElse(null);
        if (event == null) {
            // TODO: Handle event not found (e.g., show an error page or redirect)
            // Deze kan nu ook via een @ExceptionHandler in ITConferenceErrorAdvice.
            // Maar voor nu, laten we dit even buiten de scope van deze specifieke aanpassing.
            return "error";
        }
        model.addAttribute("event", event);

        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            String username = authentication.getName();
            boolean isFavorite = favoriteService.isEventFavoriteForUser(username, event.getId());
            long numberOfFavorites = favoriteService.getNumberOfFavoriteEventsForUser(username);

            int maxFavorites = 5;

            model.addAttribute("isFavorite", isFavorite);
            model.addAttribute("canAddFavorite", !isFavorite && numberOfFavorites < maxFavorites);
            model.addAttribute("maxFavoritesReached", numberOfFavorites >= maxFavorites);
        }

        return "event-detail";
    }
}