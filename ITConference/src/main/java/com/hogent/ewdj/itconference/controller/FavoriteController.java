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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import java.util.List;

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
        try {
            favoriteService.addFavoriteEvent(username, eventId);
            String successMessage = messageSource.getMessage("favorite.add.success", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("message", successMessage);
        } catch (Exception ex) { // Deze vangt ook de IllegalStateException op
            String errorMessage = messageSource.getMessage("favorite.add.error", new Object[]{ex.getMessage()}, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage); // Gebruik errorMessage voor fouten
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
            String successMessage = messageSource.getMessage("favorite.remove.success", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("message", successMessage);
        } catch (Exception ex) {
            String errorMessage = messageSource.getMessage("favorite.remove.error", new Object[]{ex.getMessage()}, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage); // Gebruik errorMessage voor fouten
        }
        return "redirect:/events/" + eventId; // Redirect naar event detail pagina na verwijderen
    }
}