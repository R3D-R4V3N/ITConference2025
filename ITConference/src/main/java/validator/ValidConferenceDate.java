package validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Documented
@Constraint(validatedBy = ConferenceDateValidator.class)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ValidConferenceDate {

    String message() default "{event.datumTijd.outOfConferencePeriod}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    String startDate();
    String endDate();

    @Target({FIELD, METHOD, TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        ValidConferenceDate[] value();
    }
}