package com.hogent.ewdj.itconference.controller;

import domain.Lokaal;
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
import service.LokaalService;
import exceptions.LokaalNotFoundException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/lokalen")
public class LokaalController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LokaalService lokaalService;

    @GetMapping
    public String showLokaalOverview(Model model) {
        List<Lokaal> lokalen = lokaalService.findAllLokalen();
        model.addAttribute("lokalen", lokalen);
        return "lokaal-overview";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddLokaalForm(Model model) {
        model.addAttribute("lokaal", new Lokaal());
        model.addAttribute("isEdit", false);
        return "lokaal-add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String processAddLokaalForm(@Valid Lokaal lokaal, BindingResult result, Model model, RedirectAttributes redirectAttributes, Locale locale) {

        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "lokaal-add";
        }

        lokaalService.saveLokaal(lokaal);
        String successMessage = messageSource.getMessage(
                "lokaal.add.success",
                new Object[]{lokaal.getCapaciteit()},
                locale
        );
        redirectAttributes.addFlashAttribute("message", successMessage);
        return "redirect:/lokalen";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditLokaalForm(@PathVariable("id") Long id, Model model) {
        Lokaal lokaal = lokaalService.findLokaalById(id)
                .orElseThrow(() -> {
                    String msg = messageSource.getMessage(
                            "lokaal.notfound.id",
                            new Object[]{id},
                            LocaleContextHolder.getLocale());
                    return new LokaalNotFoundException(msg);
                });

        model.addAttribute("lokaal", lokaal);
        model.addAttribute("isEdit", true);
        return "lokaal-add";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String processEditLokaalForm(@PathVariable("id") Long id,
                                        @Valid @ModelAttribute("lokaal") Lokaal lokaal,
                                        BindingResult result,
                                        Model model,
                                        RedirectAttributes redirectAttributes,
                                        Locale locale) {

        lokaal.setId(id);

        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "lokaal-add";
        }

        lokaalService.saveLokaal(lokaal);
        String message = messageSource.getMessage("lokaal.edit.success", null, "Lokaal succesvol bijgewerkt!", locale);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/lokalen";
    }

    @GetMapping("/remove/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showRemoveLokaalForm(@PathVariable("id") Long id, Model model) {
        Optional<Lokaal> lokaalOptional = lokaalService.findLokaalById(id);
        if (lokaalOptional.isEmpty()) {
            String msg = messageSource.getMessage(
                    "lokaal.notfound.id",
                    new Object[]{id},
                    LocaleContextHolder.getLocale());
            throw new LokaalNotFoundException(msg);
        }
        model.addAttribute("lokaal", lokaalOptional.get());
        return "lokaal-remove";
    }

    @PostMapping("/remove/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String processRemoveLokaal(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            lokaalService.deleteLokaalById(id);
            String message = messageSource.getMessage("lokaal.delete.success", null, "Lokaal succesvol verwijderd!", locale);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (IllegalStateException e) {
            String errorMessage = messageSource.getMessage("lokaal.delete.error.hasEvents", new Object[]{e.getMessage()}, "Kan lokaal niet verwijderen: " + e.getMessage(), locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/lokalen";
    }
}