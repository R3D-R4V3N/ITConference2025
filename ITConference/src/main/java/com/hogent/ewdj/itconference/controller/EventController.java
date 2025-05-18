package com.hogent.ewdj.itconference.controller;

import domain.Event; // Importeer de Event klasse
import domain.Lokaal;
import domain.Spreker;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import service.EventService; // Importeer de EventService

import org.springframework.beans.factory.annotation.Autowired; // Nodig voor injectie
import org.springframework.stereotype.Controller; // Markeer deze klasse als een Spring Controller
import org.springframework.ui.Model; // Nodig om data door te geven aan de template
import org.springframework.web.bind.annotation.GetMapping; // Annotation voor HTTP GET verzoeken
import org.springframework.web.bind.annotation.RequestMapping; // Basis URL mapping voor de controller
import service.LokaalService;
import service.SprekerService;

import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private LokaalService lokaalService; // Injecteer LokaalService

    @Autowired
    private SprekerService sprekerService; // Injecteer SprekerService

    // ... showEventOverview method (bestaat al)

    /**
     * Handelt GET verzoeken voor /events/add af en toont het formulier om een event toe te voegen.
     * Alleen toegankelijk voor gebruikers met de rol ADMIN (geconfigureerd in SecurityConfig).
     * @param model Het Model object.
     * @return De naam van de Thymeleaf template voor het formulier.
     */
    @GetMapping("/add")
    public String showAddEventForm(Model model) {
        // Maak een nieuw Event object aan voor het formulier
        Event event = new Event();

        // Haal beschikbare lokalen op om in een dropdown te tonen
        List<Lokaal> beschikbareLokalen = lokaalService.findAllLokalen();

        // Haal beschikbare sprekers op om te selecteren (voor nu alle sprekers)
        List<Spreker> beschikbareSprekers = sprekerService.findAllSprekers();


        // Voeg de objecten toe aan het model
        model.addAttribute("event", event);
        model.addAttribute("lokalen", beschikbareLokalen);
        model.addAttribute("sprekers", beschikbareSprekers);

        return "event-add"; // Naam van de Thymeleaf template voor het formulier
    }

    /**
     * Handelt POST verzoeken voor /events/add af en verwerkt het ingediende formulier.
     * Inclusief Bean Validation.
     * @param event Het Event object dat gebonden is aan de formulier data.
     * @param result Het BindingResult object met validatie fouten.
     * @param model Het Model object.
     * @return Redirect naar het overzicht bij succes, anders toon het formulier opnieuw met fouten.
     */
    @PostMapping("/add")
    // Gebruik @Valid om Bean Validation te activeren
    // Als je validatiegroepen gebruikt (wat we voor de opstart hebben omzeild),
    // zou je hier @Validated(ValidationGroups.class) gebruiken.
    public String processAddEventForm(@Validated @ModelAttribute("event") Event event, BindingResult result, Model model) {

        // Je custom class-level validator (EventConstraintsValidator) wordt automatisch
        // getriggerd door @Validated als deze correct geconfigureerd is.
        // Als die validator dependencies heeft, moeten die hier wel werkend zijn.

        // Controleer of er validatiefouten zijn
        if (result.hasErrors()) {
            // Als er fouten zijn, toon het formulier opnieuw met de ingevulde data en foutmeldingen

            // Haal opnieuw de beschikbare lokalen en sprekers op om in het formulier te tonen
            List<Lokaal> beschikbareLokalen = lokaalService.findAllLokalen();
            List<Spreker> beschikbareSprekers = sprekerService.findAllSprekers();

            model.addAttribute("lokalen", beschikbareLokalen);
            model.addAttribute("sprekers", beschikbareSprekers);

            return "event-add"; // Toon het formulier opnieuw
        }

        // Geen validatiefouten, sla het event op via de service
        // De saveEvent methode in de service handelt de complexere logica af,
        // zoals het beheren van de relaties met Sprekers en Lokaal.
        eventService.saveEvent(event);

        // Redirect naar de event overzichtspagina na succesvol opslaan
        // Gebruik een redirect om te voorkomen dat bij een refresh het formulier opnieuw wordt ingediend.
        return "redirect:/events";
    }
}