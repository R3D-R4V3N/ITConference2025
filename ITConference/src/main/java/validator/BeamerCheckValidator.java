package validator;

import domain.Event;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BeamerCheckValidator implements ConstraintValidator<ValidBeamerCheck, Event> {

    @Override
    public void initialize(ValidBeamerCheck constraintAnnotation) {
        // Initialisatie code indien nodig
    }

    @Override
    public boolean isValid(Event event, ConstraintValidatorContext context) {
        if (event == null) {
            return true; // Kan null zijn als andere validaties al gefaald zijn
        }

        // Controleer of beamercode en beamercheck gezet zijn
        // Let op: primitive int velden hebben een standaard waarde van 0.
        // Je zou hier kunnen controleren of ze binnen een geldig bereik vallen of
        // gebruik maken van @NotNull op de Integer wrapper klasse indien de velden nullable zouden zijn.
        // Aangezien ze int zijn, gaan we er vanuit dat ze altijd een waarde hebben.

        int beamercode = event.getBeamercode();
        int beamercheck = event.getBeamercheck();
        int calculatedBeamerCheck = event.calculateCorrectBeamerCheck();

        boolean isValid = beamercheck == calculatedBeamerCheck;

        if (!isValid) {
            // Pas het foutbericht aan om naar het juiste veld te wijzen
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("beamercheck") // Koppel de foutmelding aan het 'beamercheck' veld
                    .addConstraintViolation();
        }

        return isValid;
    }
}