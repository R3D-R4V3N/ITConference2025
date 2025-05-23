package com.hogent.ewdj.itconference.controller;

import domain.Event;
import domain.Lokaal;
import domain.Spreker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.EventService;
import service.LokaalService;
import service.SprekerService;
import service.FavoriteService;
import exceptions.EventNotFoundException;

import java.util.List;
import java.util.Locale;

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

    @Autowired
    private MessageSource messageSource;

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
        model.addAttribute("isEdit", false);

        return "event-add";
    }

    @PostMapping("/add")
    public String processAddEventForm(@Validated @ModelAttribute("event") Event event, BindingResult result, Model model, RedirectAttributes redirectAttributes, Locale locale) {

        if (result.hasErrors()) {
            List<Lokaal> beschikbareLokalen = lokaalService.findAllLokalen();
            List<Spreker> beschikbareSprekers = sprekerService.findAllSprekers();

            model.addAttribute("lokalen", beschikbareLokalen);
            model.addAttribute("sprekers", beschikbareSprekers);
            model.addAttribute("isEdit", false);

            return "event-add";
        }

        eventService.saveEvent(event);
        String message = messageSource.getMessage("event.add.success", null, "Evenement succesvol toegevoegd!", locale);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/events";
    }

    @GetMapping("/{id}")
    public String showEventDetail(@PathVariable("id") Long id, Model model, Authentication authentication) {
        Event event = eventService.findEventById(id).orElse(null);
        if (event == null) {
            throw new EventNotFoundException("Evenement met ID " + id + " niet gevonden.");
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

    @GetMapping("/edit/{id}")
    public String showEditEventForm(@PathVariable("id") Long id, Model model) {
        Event event = eventService.findEventById(id)
                .orElseThrow(() -> new EventNotFoundException("Evenement met ID " + id + " niet gevonden om te bewerken."));

        List<Lokaal> beschikbareLokalen = lokaalService.findAllLokalen();
        List<Spreker> beschikbareSprekers = sprekerService.findAllSprekers();

        model.addAttribute("event", event);
        model.addAttribute("lokalen", beschikbareLokalen);
        model.addAttribute("sprekers", beschikbareSprekers);
        model.addAttribute("isEdit", true);

        return "event-add";
    }

    @PostMapping("/edit/{id}")
    public String processEditEventForm(@PathVariable("id") Long id,
                                       @Validated @ModelAttribute("event") Event event,
                                       BindingResult result,
                                       Model model,
                                       RedirectAttributes redirectAttributes,
                                       Locale locale) {

        event.setId(id);

        if (result.hasErrors()) {
            List<Lokaal> beschikbareLokalen = lokaalService.findAllLokalen();
            List<Spreker> beschikbareSprekers = sprekerService.findAllSprekers();

            model.addAttribute("lokalen", beschikbareLokalen);
            model.addAttribute("sprekers", beschikbareSprekers);
            model.addAttribute("isEdit", true);
            return "event-add";
        }

        eventService.saveEvent(event);
        String message = messageSource.getMessage("event.edit.success", null, "Evenement succesvol bijgewerkt!", locale);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/events/" + event.getId();
    }
}