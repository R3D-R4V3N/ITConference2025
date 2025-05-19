// Begin modificatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/com/hogent/ewdj/itconference/controller/EventController.java
package com.hogent.ewdj.itconference.controller;

import domain.Event;
import domain.Lokaal;
import domain.Spreker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // Import Authentication
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.EventService;
import service.LokaalService;
import service.SprekerService;
import service.FavoriteService; // Import FavoriteService

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
    private FavoriteService favoriteService; // Inject FavoriteService

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

        if (result.hasErrors()) {
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
            return "error"; // Or "redirect:/events"
        }
        model.addAttribute("event", event);

        // Voeg favorieten logica toe voor de view
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            String username = authentication.getName();
            boolean isFavorite = favoriteService.isEventFavoriteForUser(username, event.getId());
            long numberOfFavorites = favoriteService.getNumberOfFavoriteEventsForUser(username);

            // Haal de MAX_FAVORITES_PER_USER op een veilige manier op
            // Omdat MAX_FAVORITES_PER_USER een private static final is in FavoriteServiceImpl,
            // kunnen we deze niet direct injecteren of via de interface ophalen.
            // Voor dit voorbeeld hardcoden we het hier, maar in een echte applicatie
            // zou dit via een configuratie property of een getter in de service moeten.
            int maxFavorites = 5;

            model.addAttribute("isFavorite", isFavorite);
            model.addAttribute("canAddFavorite", !isFavorite && numberOfFavorites < maxFavorites);
            model.addAttribute("maxFavoritesReached", numberOfFavorites >= maxFavorites);
        }

        return "event-detail";
    }
}
// Einde modificatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/com/hogent/ewdj/itconference/controller/EventController.java