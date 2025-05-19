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

@Controller
@RequestMapping("/lokalen")
public class LokaalController {

    @Autowired
    private LokaalService lokaalService;

    @GetMapping("/add")
    public String showAddLokaalForm(Model model) {
        model.addAttribute("lokaal", new Lokaal()); // Empty Lokaal object for the form
        return "lokaal-add";
    }

    @PostMapping("/add")
    public String processAddLokaalForm(@Valid Lokaal lokaal, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "lokaal-add";
        }

        lokaalService.saveLokaal(lokaal);
        redirectAttributes.addFlashAttribute("successMessage", "Lokaal met " + lokaal.getCapaciteit() + " plaatsen werd toegevoegd."); // TODO: Use resource bundle
        return "redirect:/lokalen"; // Redirect to a Lokaal overview page (or wherever you want)
    }

    // You can add more methods here for viewing/editing Lokalen later
}