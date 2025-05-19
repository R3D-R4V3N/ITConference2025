package validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import java.util.List;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Documented
@Constraint(validatedBy = SpeakerListValidator.class)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ValidSpeakerList {

    String message() default "{event.sprekers.duplicate}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target({FIELD, METHOD, TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        ValidSpeakerList[] value();
    }
}