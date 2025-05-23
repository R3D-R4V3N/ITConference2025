package com.hogent.ewdj.itconference.controller;

import domain.Event;
import domain.Lokaal;
import domain.Spreker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.Optional;

@Controller // verwerkt webverzoeken rond events
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
            String msg = messageSource.getMessage(
                    "event.notfound",
                    new Object[]{id},
                    LocaleContextHolder.getLocale());
            throw new EventNotFoundException(msg);
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
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditEventForm(@PathVariable("id") Long id, Model model) {
        Event event = eventService.findEventById(id)
                .orElseThrow(() -> {
                    String msg = messageSource.getMessage(
                            "event.notfound",
                            new Object[]{id},
                            LocaleContextHolder.getLocale());
                    return new EventNotFoundException(msg);
                });

        List<Lokaal> beschikbareLokalen = lokaalService.findAllLokalen();
        List<Spreker> beschikbareSprekers = sprekerService.findAllSprekers();

        model.addAttribute("event", event);
        model.addAttribute("lokalen", beschikbareLokalen);
        model.addAttribute("sprekers", beschikbareSprekers);
        model.addAttribute("isEdit", true);

        return "event-add";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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

    @GetMapping("/remove/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showRemoveEventForm(@PathVariable("id") Long id, Model model) {
        Optional<Event> eventOptional = eventService.findEventById(id);
        if (eventOptional.isEmpty()) {
            String msg = messageSource.getMessage(
                    "event.notfound",
                    new Object[]{id},
                    LocaleContextHolder.getLocale());
            throw new EventNotFoundException(msg);
        }
        model.addAttribute("event", eventOptional.get());
        return "event-remove";
    }

    @PostMapping("/remove/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String processRemoveEvent(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Locale locale) {
        eventService.deleteEventById(id);
        String message = messageSource.getMessage("event.delete.success", null, "Evenement succesvol verwijderd!", locale);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/events";
    }
}