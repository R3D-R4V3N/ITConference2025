// validator/EventConstraintsValidator.java
package validator;

import domain.Event;
import domain.Lokaal;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import repository.EventRepository;
// Verwijder deze imports, ze zijn niet langer nodig
// import org.springframework.beans.BeansException;
// import org.springframework.context.ApplicationContext;
// import org.springframework.context.ApplicationContextAware;
import org.springframework.beans.factory.annotation.Autowired; // Zorg ervoor dat deze import er is
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
// Verwijder de implementatie van ApplicationContextAware
public class EventConstraintsValidator implements ConstraintValidator<ValidEventConstraints, Event> {

    private EventRepository eventRepository;

    // Verwijder deze velden
    // private ApplicationContext applicationContext;

    @Autowired
    // Spring zal EventRepository direct injecteren.
    // De 'setEventRepository' methode is nog steeds een valide manier voor injectie,
    // maar vaak wordt direct op het veld geinjecteerd of via een constructor.
    // Voor consistentie met hoe het nu is, behouden we de setter, maar de fallback logica verdwijnt.
    public void setEventRepository(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // Verwijder deze methode
    // @Override
    // public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    //     this.applicationContext = applicationContext;
    // }

    @Override
    public void initialize(ValidEventConstraints constraintAnnotation) {
    }

    @Override
    public boolean isValid(Event event, ConstraintValidatorContext context) {
        // De 'if (eventRepository == null)' check en de fallback via ApplicationContext is niet langer nodig.
        // Spring zorgt ervoor dat de repository is geïnjecteerd wanneer deze validator wordt gebruikt.
        // if (eventRepository == null) {
        //     System.out.println("EventRepository was null, retrieving from ApplicationContext...");
        //     eventRepository = applicationContext.getBean(EventRepository.class);
        // }
        if (event == null) {
            return true;
        }

        boolean isValid = true;

        LocalDateTime datumTijd = event.getDatumTijd();
        Lokaal lokaal = event.getLokaal();

        // Zorg ervoor dat eventRepository niet null is. Als Spring het niet injecteert, is er een groter configuratieprobleem.
        // In een correcte Spring Boot app zal dit altijd geïnjecteerd zijn.
        if (eventRepository == null) {
            // Dit zou in een correct geconfigureerde applicatie niet moeten gebeuren.
            // Voor robuustheid in geval van onverwachte configuratiefouten, kun je een RuntimeException gooien.
            // Of, beter nog, laat Spring de fout afhandelen als de bean niet kan worden geïnjecteerd.
            return false; // Kan hier ook een foutlog maken
        }


        List<Event> bestaandeEventsTijdLokaal = eventRepository.findByDatumTijdAndLokaal(datumTijd, lokaal);

        if (bestaandeEventsTijdLokaal != null && !bestaandeEventsTijdLokaal.isEmpty()) {
            boolean isDuplicate = false;
            for (Event bestaandEvent : bestaandeEventsTijdLokaal) {
                if (event.getId() == null || !event.getId().equals(bestaandEvent.getId())) {
                    isDuplicate = true;
                    break;
                }
            }
            if (isDuplicate) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{event.constraints.duplicateTimeLocation}")
                        .addPropertyNode("datumTijd")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        String naam = event.getNaam();
        LocalDate datum = null;
        if (datumTijd != null) {
            datum = datumTijd.toLocalDate();
        }

        if (naam != null && datum != null) {
            List<Event> bestaandeEventsNaamDag = eventRepository.findByNaamAndDatum(naam, datum);

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
                            .addPropertyNode("naam")
                            .addConstraintViolation();
                    isValid = false;
                }
            }
        }

        return isValid;
    }
}