package com.hogent.ewdj.itconference.controller;

import domain.Event;
import exceptions.EventNotFoundException;
import exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.FavoriteService;

import java.util.List;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping
    public String showFavoriteEvents(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Event> favoriteEvents = favoriteService.findFavoriteEventsByUsername(username);
        model.addAttribute("favoriteEvents", favoriteEvents);
        return "favorites-overview";
    }

    @PostMapping("/add")
    public String addFavoriteEvent(@RequestParam("eventId") Long eventId,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        try {
            favoriteService.addFavoriteEvent(username, eventId);
            redirectAttributes.addFlashAttribute("message", "Evenement succesvol toegevoegd aan favorieten.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (UserNotFoundException | EventNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/events/" + eventId;
    }

    @PostMapping("/remove")
    public String removeFavoriteEvent(@RequestParam("eventId") Long eventId,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        try {
            favoriteService.removeFavoriteEvent(username, eventId);
            redirectAttributes.addFlashAttribute("message", "Evenement succesvol verwijderd uit favorieten."); // TODO: Resource bundle
        } catch (UserNotFoundException | EventNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/favorites";
    }
}