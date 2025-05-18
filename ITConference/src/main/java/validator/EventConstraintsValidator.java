package validator;

import domain.Event;
import domain.Lokaal;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import repository.EventRepository; // Importeer de EventRepository
import org.springframework.beans.factory.annotation.Autowired; // Nodig voor injectie
import org.springframework.stereotype.Component; // Maak dit een Spring Component zodat injectie werkt

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component // Maak deze klasse een Spring Component
public class EventConstraintsValidator implements ConstraintValidator<ValidEventConstraints, Event> {

    @Autowired // Injecteer de EventRepository
    private EventRepository eventRepository;

    @Override
    public void initialize(ValidEventConstraints constraintAnnotation) {
        // Initialisatie code indien nodig
    }

    @Override
    public boolean isValid(Event event, ConstraintValidatorContext context) {
        // Null event object is geldig als eerdere validaties faalden
        if (event == null) {
            return true;
        }

        boolean isValid = true;

        // Controle 1: Geen dubbel event op hetzelfde tijdstip in hetzelfde lokaal.
        LocalDateTime datumTijd = event.getDatumTijd();
        Lokaal lokaal = event.getLokaal();

        // Zoek naar bestaande events met hetzelfde tijdstip en lokaal, exclusief het huidige event (als het al bestaat)
        List<Event> bestaandeEventsTijdLokaal = eventRepository.findByDatumTijdAndLokaal(datumTijd, lokaal);

        // Als er bestaande events zijn en ze zijn niet het event dat we nu valideren (bij een update)
        if (bestaandeEventsTijdLokaal != null && !bestaandeEventsTijdLokaal.isEmpty()) {
            boolean isDuplicate = false;
            for (Event bestaandEvent : bestaandeEventsTijdLokaal) {
                // Als het bestaande event niet hetzelfde is als het event dat we nu valideren (op basis van ID)
                if (event.getId() == null || !event.getId().equals(bestaandEvent.getId())) {
                    isDuplicate = true;
                    break;
                }
            }
            if (isDuplicate) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{event.constraints.duplicateTimeLocation}")
                        .addPropertyNode("datumTijd") // Koppel fout aan datumTijd veld
                        .addConstraintViolation();
                isValid = false;
            }
        }


        // Controle 2: Naam van het event op dezelfde dag mag nog niet voorkomen.
        String naam = event.getNaam();
        LocalDate datum = null;
        if (datumTijd != null) {
            datum = datumTijd.toLocalDate();
        }


        if (naam != null && datum != null) {
            // Zoek naar bestaande events met dezelfde naam op dezelfde dag
            List<Event> bestaandeEventsNaamDag = eventRepository.findByNaamAndDatum(naam, datum); // We moeten deze methode toevoegen aan EventRepository

            if (bestaandeEventsNaamDag != null && !bestaandeEventsNaamDag.isEmpty()) {
                boolean isDuplicate = false;
                for (Event bestaandEvent : bestaandeEventsNaamDag) {
                    if (event.getId() == null || !event.getId().equals(bestaandEvent.getId())) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (isDuplicate) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("{event.constraints.duplicateNameDay}")
                            .addPropertyNode("naam") // Koppel fout aan naam veld
                            .addConstraintViolation();
                    isValid = false;
                }
            }
        }


        return isValid;
    }
}