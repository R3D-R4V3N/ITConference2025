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
        if (sprekers == null || sprekers.isEmpty()) {
            return true;
        }

        Set<Spreker> uniekeSprekers = new HashSet<>();

        for (Spreker spreker : sprekers) {
            if (!uniekeSprekers.add(spreker)) {

                return false;
            }
        }

        return true;
    }
}