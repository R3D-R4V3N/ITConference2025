package validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Documented
@Constraint(validatedBy = EventConstraintsValidator.class) // Koppel aan de validator
@Target({TYPE, ANNOTATION_TYPE}) // Deze validatie werkt op klasniveau
@Retention(RUNTIME) // Beschikbaar tijdens runtime
public @interface ValidEventConstraints {

    String message() default "{event.constraints.invalid}"; // Standaard foutmelding

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        ValidEventConstraints[] value();
    }
}