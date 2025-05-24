package com.hogent.ewdj.itconference.controller;

import domain.Room;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.RoomService;
import exceptions.RoomNotFoundException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/lokalen")
public class RoomController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RoomService roomService;

    @GetMapping
    public String showRoomOverview(Model model) {
        List<Room> rooms = roomService.findAllRooms();
        model.addAttribute("lokalen", rooms);
        return "room-overview";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddRoomForm(Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("isEdit", false);
        return "room-add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String processAddRoomForm(@Valid Room room, BindingResult result, Model model, RedirectAttributes redirectAttributes, Locale locale) {

        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "room-add";
        }

        roomService.saveRoom(room);
        String successMessage = messageSource.getMessage(
                "lokaal.add.success",
                new Object[]{room.getCapacity()},
                locale
        );
        redirectAttributes.addFlashAttribute("message", successMessage);
        return "redirect:/lokalen";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditRoomForm(@PathVariable("id") Long id, Model model) {
        Room room = roomService.findRoomById(id)
                .orElseThrow(() -> {
                    String msg = messageSource.getMessage(
                            "lokaal.notfound.id",
                            new Object[]{id},
                            LocaleContextHolder.getLocale());
                    return new RoomNotFoundException(msg);
                });

        model.addAttribute("room", room);
        model.addAttribute("isEdit", true);
        return "room-add";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String processEditRoomForm(@PathVariable("id") Long id,
                                        @Valid @ModelAttribute("room") Room room,
                                        BindingResult result,
                                        Model model,
                                        RedirectAttributes redirectAttributes,
                                        Locale locale) {

        room.setId(id);

        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "room-add";
        }

        roomService.saveRoom(room);
        String message = messageSource.getMessage("lokaal.edit.success", null, "Lokaal succesvol bijgewerkt!", locale);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/lokalen";
    }

    @GetMapping("/remove/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showRemoveRoomForm(@PathVariable("id") Long id, Model model) {
        Optional<Room> roomOptional = roomService.findRoomById(id);
        if (roomOptional.isEmpty()) {
            String msg = messageSource.getMessage(
                    "lokaal.notfound.id",
                    new Object[]{id},
                    LocaleContextHolder.getLocale());
            throw new RoomNotFoundException(msg);
        }
        model.addAttribute("room", roomOptional.get());
        return "room-remove";
    }

    @PostMapping("/remove/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String processRemoveRoom(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            roomService.deleteRoomById(id);
            String message = messageSource.getMessage("lokaal.delete.success", null, "Lokaal succesvol verwijderd!", locale);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (IllegalStateException e) {
            String errorMessage = messageSource.getMessage("lokaal.delete.error.hasEvents", new Object[]{e.getMessage()}, "Kan lokaal niet verwijderen: " + e.getMessage(), locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/lokalen";
    }
}