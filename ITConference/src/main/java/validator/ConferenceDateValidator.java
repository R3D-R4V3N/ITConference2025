package validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class ConferenceDateValidator implements ConstraintValidator<ValidConferenceDate, LocalDateTime> {

    private LocalDate conferenceStartDate;
    private LocalDate conferenceEndDate;

    @Override
    public void initialize(ValidConferenceDate constraintAnnotation) {
        try {
            // Parse de start- en einddatum uit de annotatie parameters
            this.conferenceStartDate = LocalDate.parse(constraintAnnotation.startDate());
            this.conferenceEndDate = LocalDate.parse(constraintAnnotation.endDate());
        } catch (DateTimeParseException e) {
            // Log een fout of gooi een RuntimeException als de datums ongeldig zijn in de annotatie
            System.err.println("Fout bij het parsen van conferentie datums in ValidConferenceDate annotatie: " + e.getMessage());
            throw new RuntimeException("Ongeldige datum formaat in ValidConferenceDate annotatie", e);
        }
    }

    @Override
    public boolean isValid(LocalDateTime datumTijd, ConstraintValidatorContext context) {
        // Null waardes worden afgehandeld door @NotNull op het veld zelf
        if (datumTijd == null) {
            return true;
        }

        LocalDate eventDate = datumTijd.toLocalDate();

        // Controleer of de datum van het event binnen de conferentieperiode valt
        // eventDate >= conferenceStartDate EN eventDate <= conferenceEndDate
        boolean isValid = !eventDate.isBefore(conferenceStartDate) && !eventDate.isAfter(conferenceEndDate);

        if (!isValid) {
            // Optioneel: pas het foutbericht aan of voeg context toe indien nodig
            // context.disableDefaultConstraintViolation();
            // context.buildConstraintViolationWithTemplate(...).addConstraintViolation();
        }

        return isValid;
    }
}