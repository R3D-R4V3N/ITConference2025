package validator;

import domain.Spreker;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpeakerListValidator implements ConstraintValidator<ValidSpeakerList, List<Spreker>> {

    @Override
    public void initialize(ValidSpeakerList constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<Spreker> sprekers, ConstraintValidatorContext context) {
        // De @NotNull en @Size(min=1) annotaties op de Event.sprekers lijst
        // handelen nu de lege/null check af.
        // Deze validator richt zich nu enkel op dubbele sprekers.
        if (sprekers == null) { // Voorkom NullPointerException als er toch null binnenkomt
            return true;
        }

        Set<Spreker> uniekeSprekers = new HashSet<>();

        for (Spreker spreker : sprekers) {
            // Voeg een null-check toe voor individuele sprekers om problemen te voorkomen
            if (spreker == null || !uniekeSprekers.add(spreker)) {
                // Als een spreker null is, of als het toevoegen mislukt (dubbele spreker)
                return false;
            }
        }

        return true;
    }
}