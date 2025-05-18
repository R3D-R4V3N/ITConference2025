package validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented // Geeft aan dat deze annotatie gedocumenteerd moet worden door Javadoc
@Constraint(validatedBy = BeamerCheckValidator.class) // Koppel deze annotatie aan de validator klasse
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE}) // Waar de annotatie geplaatst mag worden
@Retention(RetentionPolicy.RUNTIME) // De annotatie moet beschikbaar zijn tijdens runtime
public @interface ValidBeamerCheck {

    String message() default "{event.beamercheck.invalid}"; // Standaard foutmelding (uit resource bundle)

    Class<?>[] groups() default {}; // Standaard Bean Validation groepen

    Class<? extends Payload>[] payload() default {}; // Standaard Bean Validation payload

    // Eventuele extra parameters voor de annotatie kunnen hier toegevoegd worden indien nodig
}