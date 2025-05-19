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
            this.conferenceStartDate = LocalDate.parse(constraintAnnotation.startDate());
            this.conferenceEndDate = LocalDate.parse(constraintAnnotation.endDate());
        } catch (DateTimeParseException e) {
            System.err.println("Fout bij het parsen van conferentie datums in ValidConferenceDate annotatie: " + e.getMessage());
            throw new RuntimeException("Ongeldige datum formaat in ValidConferenceDate annotatie", e);
        }
    }

    @Override
    public boolean isValid(LocalDateTime datumTijd, ConstraintValidatorContext context) {
        if (datumTijd == null) {
            return true;
        }

        LocalDate eventDate = datumTijd.toLocalDate();

        boolean isValid = !eventDate.isBefore(conferenceStartDate) && !eventDate.isAfter(conferenceEndDate);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("De datum moet tussen " + conferenceStartDate + " en " + conferenceEndDate + " liggen.")
                    .addConstraintViolation();
        }

        return isValid;
    }
}