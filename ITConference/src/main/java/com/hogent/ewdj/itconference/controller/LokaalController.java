// com/hogent/ewdj/itconference/controller/LokaalController.java
package com.hogent.ewdj.itconference.controller;

import domain.Lokaal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.LokaalService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;


import java.util.List;

@Controller
@RequestMapping("/lokalen")
public class LokaalController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LokaalService lokaalService;

    @GetMapping("/add")
    public String showAddLokaalForm(Model model) {
        model.addAttribute("lokaal", new Lokaal());
        return "lokaal-add";
    }

    @PostMapping("/add")
    public String processAddLokaalForm(@Valid Lokaal lokaal, BindingResult result, RedirectAttributes redirectAttributes) {

        // Deze if-check blijft behouden voor formuliervalidatie, conform de cursus.
        if (result.hasErrors()) {
            return "lokaal-add";
        }

        lokaalService.saveLokaal(lokaal);
        String successMessage = messageSource.getMessage(
                "lokaal.add.success",
                new Object[]{lokaal.getCapaciteit()},
                LocaleContextHolder.getLocale()
        );
        redirectAttributes.addFlashAttribute("successMessage", successMessage);

        return "redirect:/lokalen";
    }

    @GetMapping
    public String showLokaalOverview(Model model) {
        List<Lokaal> lokalen = lokaalService.findAllLokalen();
        model.addAttribute("lokalen", lokalen);
        return "lokaal-overview";
    }
}