package validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class ConferenceDateValidator implements ConstraintValidator<ValidConferenceDate, LocalDateTime> {

    private LocalDate conferenceStartDate;
    private LocalDate conferenceEndDate;
    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void initialize(ValidConferenceDate constraintAnnotation) {
        try {
            this.conferenceStartDate = LocalDate.parse(constraintAnnotation.startDate());
            this.conferenceEndDate = LocalDate.parse(constraintAnnotation.endDate());
        } catch (DateTimeParseException e) {
            String msg = messageSource.getMessage(
                    "validator.conferenceDate.invalid_format",
                    null,
                    LocaleContextHolder.getLocale());
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
            String msg = messageSource.getMessage(
                    "event.datumTijd.outOfConferencePeriod",
                    new Object[]{conferenceStartDate, conferenceEndDate},
                    LocaleContextHolder.getLocale());
            context.buildConstraintViolationWithTemplate(msg)
                    .addConstraintViolation();
        }

        return isValid;
    }
}