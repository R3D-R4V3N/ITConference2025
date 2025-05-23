package validator;

import domain.Event;
import domain.Lokaal;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventConstraintsValidator implements ConstraintValidator<ValidEventConstraints, Event> {

    private EventRepository eventRepository;

    @Autowired
    public void setEventRepository(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void initialize(ValidEventConstraints constraintAnnotation) {
    }

    @Override
    public boolean isValid(Event event, ConstraintValidatorContext context) {
        if (event == null) {
            return true;
        }

        boolean isValid = true;

        LocalDateTime datumTijd = event.getDatumTijd();
        Lokaal lokaal = event.getLokaal();

        if (eventRepository == null) {
            return false;
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