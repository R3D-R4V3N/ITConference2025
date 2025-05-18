package validator;

import domain.Event;
import domain.Lokaal;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import repository.EventRepository;
import org.springframework.beans.BeansException; // Import BeansException
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext; // Import ApplicationContext
import org.springframework.context.ApplicationContextAware; // Import ApplicationContextAware
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
// Implement ApplicationContextAware
public class EventConstraintsValidator implements ConstraintValidator<ValidEventConstraints, Event>, ApplicationContextAware {

    // Keep @Autowired, but add a fallback
    private EventRepository eventRepository;

    // Store the ApplicationContext
    private ApplicationContext applicationContext;

    @Autowired
    public void setEventRepository(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // Implement setApplicationContext method
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(ValidEventConstraints constraintAnnotation) {
        // Initialisatie code indien nodig
    }

    @Override
    public boolean isValid(Event event, ConstraintValidatorContext context) {
        // If @Autowired injection failed, get the repository from the ApplicationContext
        if (eventRepository == null) {
            System.out.println("EventRepository was null, retrieving from ApplicationContext...");
            eventRepository = applicationContext.getBean(EventRepository.class);
        }

        // Null event object is geldig als eerdere validaties faalden
        if (event == null) {
            return true;
        }

        boolean isValid = true;

        // Controle 1: Geen dubbel event op hetzelfde tijdstip in hetzelfde lokaal.
        LocalDateTime datumTijd = event.getDatumTijd();
        Lokaal lokaal = event.getLokaal();

        // Use the eventRepository (now guaranteed to be not null)
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

        // Controle 2: Naam van het event op dezelfde dag mag nog niet voorkomen.
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