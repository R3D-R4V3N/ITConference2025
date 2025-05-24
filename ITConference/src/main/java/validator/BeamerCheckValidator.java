package validator;

import domain.Event;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BeamerCheckValidator implements ConstraintValidator<ValidBeamerCheck, Event> {

    @Override
    public void initialize(ValidBeamerCheck constraintAnnotation) {
    }

    @Override
    public boolean isValid(Event event, ConstraintValidatorContext context) {
        if (event == null) {
            return true;
        }

        int beamercode = event.getBeamercode();
        int beamercheck = event.getBeamercheck();
        int calculatedBeamerCheck = event.calculateCorrectBeamerCheck();

        boolean isValid = beamercheck == calculatedBeamerCheck;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate());
            if (violationBuilder != null) { // Add null check here
                violationBuilder.addPropertyNode("beamercheck")
                        .addConstraintViolation();
            }
        }

        return isValid;
    }
}