package validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Documented
@Constraint(validatedBy = ConferenceDateValidator.class) // Koppel aan de validator
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE}) // Waar te plaatsen
@Retention(RUNTIME) // Beschikbaar tijdens runtime
public @interface ValidConferenceDate {

    String message() default "{event.datumTijd.outOfConferencePeriod}"; // Foutmelding

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // Parameters voor de start- en einddatum van de conferentieperiode
    String startDate(); // Formaat yyyy-MM-dd
    String endDate(); // Formaat yyyy-MM-dd

    @Target({FIELD, METHOD, TYPE, ANNOTATION_TYPE}) // Waar deze Nested Annotation geplaatst mag worden
    @Retention(RUNTIME)
    @Documented
    @interface List {
        ValidConferenceDate[] value();
    }
}