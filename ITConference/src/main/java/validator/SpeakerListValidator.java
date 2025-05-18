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
        // Initialisatie code indien nodig. Hier niet nodig.
    }

    @Override
    public boolean isValid(List<Spreker> sprekers, ConstraintValidatorContext context) {
        // Als de lijst null is of leeg, beschouwen we dit als geldig voor deze specifieke validatie.
        // Het controleren op een minimum aantal sprekers (min = 1) wordt afgehandeld door @Size.
        if (sprekers == null || sprekers.isEmpty()) {
            return true;
        }

        Set<Spreker> uniekeSprekers = new HashSet<>();

        // Loop door de lijst met sprekers
        for (Spreker spreker : sprekers) {
            // Controleer op null sprekers in de lijst (hoewel dit door @NotNull op Spreker afgehandeld zou moeten worden indien Spreker nullable was)
            if (spreker == null) {
                // Voeg een fout toe als een spreker in de lijst null is
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Spreker in lijst mag niet null zijn") // Of gebruik een resource bundle key
                        .addPropertyNode("sprekers")
                        .addConstraintViolation();
                return false;
            }

            // Probeer de spreker aan de set toe te voegen.
            // Als add() 'false' teruggeeft, betekent dit dat de spreker (of een gelijk object) al in de set zat.
            if (!uniekeSprekers.add(spreker)) {
                // Een duplicaat gevonden, dus de validatie mislukt.
                // Voeg een fout toe aan het 'sprekers' veld
                context.disableDefaultConstraintViolation(); // Schakel de standaardfout uit (indien van toepassing)
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()) // Gebruik de standaardfoutmelding van de annotatie
                        .addPropertyNode("sprekers") // Koppel de foutmelding aan het 'sprekers' veld
                        .addConstraintViolation(); // Voeg de aangepaste fout toe
                return false; // De validatie is mislukt
            }
        }

        // Geen duplicaten of null sprekers gevonden, de validatie is geslaagd.
        return true;
    }
}