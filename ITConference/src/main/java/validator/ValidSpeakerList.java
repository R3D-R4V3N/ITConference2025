package validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import java.util.List;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Documented // Geeft aan dat deze annotatie gedocumenteerd moet worden door Javadoc
@Constraint(validatedBy = SpeakerListValidator.class) // Koppel deze annotatie aan de validator klasse (die we net hebben gemaakt in Stap 11 Deel 2)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE}) // Geeft aan waar deze annotatie geplaatst mag worden (op een veld, methode, parameter, of andere annotatie)
@Retention(RUNTIME) // Geeft aan dat deze annotatie beschikbaar moet zijn tijdens runtime
public @interface ValidSpeakerList {

    // Het standaard foutbericht wanneer de validatie mislukt.
    // De waarde tussen accolades {} verwijst naar een sleutel in een resource bundle (messages.properties).
    String message() default "{event.sprekers.duplicate}";

    // Standaard Bean Validation elementen - laat deze zo
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    // Nested annotatie nodig als je meerdere @ValidSpeakerList annotaties zou willen gebruiken op hetzelfde element
    @Target({FIELD, METHOD, TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        ValidSpeakerList[] value();
    }
}