package com.hogent.ewdj.itconference.controller;

import domain.Event;
import exceptions.EventNotFoundException; // Keep for clarity if needed, but not directly used in catch
import exceptions.UserNotFoundException; // Keep for clarity if needed, but not directly used in catch
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.FavoriteService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private MessageSource messageSource;

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
        // De exceptions worden nu opgevangen door GlobalExceptionHandler / ITConferenceErrorAdvice.
        // De specifieke catch blokken hier zijn overbodig tenzij je een andere specifieke redirect wilt.
        // Voor consistentie met de opdracht laten we de adviezen de errors afhandelen.
        favoriteService.addFavoriteEvent(username, eventId);
        String successMessage = messageSource.getMessage("favorite.add.success", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("message", successMessage);

        return "redirect:/events/" + eventId;
    }

    @PostMapping("/remove")
    public String removeFavoriteEvent(@RequestParam("eventId") Long eventId,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        // De exceptions worden nu opgevangen door GlobalExceptionHandler / ITConferenceErrorAdvice.
        // De specifieke catch blokken hier zijn overbodig tenzij je een andere specifieke redirect wilt.
        // Voor consistentie met de opdracht laten we de adviezen de errors afhandelen.
        favoriteService.removeFavoriteEvent(username, eventId);
        String successMessage = messageSource.getMessage("favorite.remove.success", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("message", successMessage);

        return "redirect:/favorites";
    }
}