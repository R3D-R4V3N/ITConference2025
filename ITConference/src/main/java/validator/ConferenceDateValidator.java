package validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class ConferenceDateValidator implements ConstraintValidator<ValidConferenceDate, LocalDateTime> {

    private LocalDate conferenceStartDate;
    private LocalDate conferenceEndDate;
    @Setter
    private MessageSource messageSource;

    @Override
    public void initialize(ValidConferenceDate constraintAnnotation) {
        try {
            this.conferenceStartDate = LocalDate.parse(constraintAnnotation.startDate());
            this.conferenceEndDate = LocalDate.parse(constraintAnnotation.endDate());
        } catch (DateTimeParseException e) {
            String msg;
            if (messageSource != null) {
                msg = messageSource.getMessage(
                        "validator.conferenceDate.invalid_format",
                        null,
                        LocaleContextHolder.getLocale());
            } else {
                msg = "Invalid date format in ValidConferenceDate annotation";
            }
            throw new RuntimeException(msg, e);
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
            String msg;
            if (messageSource != null) {
                msg = messageSource.getMessage(
                        "event.datumTijd.outOfConferencePeriod",
                        new Object[]{conferenceStartDate, conferenceEndDate},
                        LocaleContextHolder.getLocale());
            } else {
                msg = String.format("De datum moet tussen %s en %s liggen.",
                        conferenceStartDate, conferenceEndDate);
            }
            context.buildConstraintViolationWithTemplate(msg)
                    .addConstraintViolation();
        }

        return isValid;
    }
}