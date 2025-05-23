package com.hogent.ewdj.itconference.domain;

import domain.Lokaal;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test; // Importeer Test voor testInvalidLokaalCapaciteitNotNull
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class LokaalTest {

    private Validator validator;

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Lokaal createLokaal(String naam, int capaciteit) {
        return new Lokaal(naam, capaciteit);
    }

    @ParameterizedTest
    @CsvSource({
            "A101, 50",
            "B001, 1",
            "C999, 49"
    })
    void testValidLokaal(String naam, int capaciteit) {
        // Arrange: Maak een Lokaal object aan met geldige data
        Lokaal validLokaal = createLokaal(naam, capaciteit);

        // Act: Valideer het Lokaal object
        Set<ConstraintViolation<Lokaal>> violations = validator.validate(validLokaal);

        // Assert: Controleer of er geen validatiefouten zijn
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testInvalidLokaalNaamNotBlank(String naam) {
        // Arrange: Maak een Lokaal object aan met ongeldige naam (leeg/spaties)
        Lokaal invalidLokaal = createLokaal(naam, 30);

        // Act: Valideer het Lokaal object
        Set<ConstraintViolation<Lokaal>> violations = validator.validate(invalidLokaal);

        // Assert: Controleer op validatiefouten voor 'naam'
        // Controleer of de foutmeldingen de template keys bevatten, zoals in de console output.
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("naam") &&
                v.getMessageTemplate().equals("{lokaal.naam.notBlank}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234", "a12", "AB123", "A101A", "1A10"})
    void testInvalidLokaalNaamPattern(String naam) {
        // Arrange: Maak een Lokaal object aan met ongeldig naamformaat
        Lokaal invalidLokaal = createLokaal(naam, 30);

        // Act: Valideer het Lokaal object
        Set<ConstraintViolation<Lokaal>> violations = validator.validate(invalidLokaal);

        // Assert: Controleer op validatiefouten voor 'naam'
        // Controleer of de foutmeldingen de template keys bevatten.
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("naam") &&
                v.getMessageTemplate().equals("{lokaal.naam.pattern}"));
    }

    // De test testInvalidLokaalCapaciteitNotNull is leeggelaten omdat @NotNull
    // niet van toepassing is op primitieve types zoals 'int'.
    // Als 'capaciteit' van type 'Integer' zou zijn, dan zou een test voor null wel zinvol zijn.
    @Test
    void testInvalidLokaalCapaciteitNotNull() {
        // Deze test is niet relevant voor een 'int' veld, aangezien 'int' niet null kan zijn.
        // Als het veld 'Integer' was, zou je hier iets kunnen testen als:
        // Lokaal invalidLokaal = new Lokaal("A101", null);
        // Set<ConstraintViolation<Lokaal>> violations = validator.validate(invalidLokaal);
        // assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("capaciteit") &&
        //         v.getMessageTemplate().equals("{lokaal.capaciteit.notNull}"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void testInvalidLokaalCapaciteitMin(int capaciteit) {
        // Arrange: Maak een Lokaal object aan met capaciteit < 1
        Lokaal invalidLokaal = createLokaal("A101", capaciteit);

        // Act: Valideer het Lokaal object
        Set<ConstraintViolation<Lokaal>> violations = validator.validate(invalidLokaal);

        // Assert: Controleer op validatiefouten voor 'capaciteit'
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("capaciteit") &&
                v.getMessageTemplate().equals("{lokaal.capaciteit.min}"));
    }

    @ParameterizedTest
    @ValueSource(ints = {51, 100, 1000})
    void testInvalidLokaalCapaciteitMax(int capaciteit) {
        // Arrange: Maak een Lokaal object aan met capaciteit > 50
        Lokaal invalidLokaal = createLokaal("A101", capaciteit);

        // Act: Valideer het Lokaal object
        Set<ConstraintViolation<Lokaal>> violations = validator.validate(invalidLokaal);

        // Assert: Controleer op validatiefouten voor 'capaciteit'
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("capaciteit") &&
                v.getMessageTemplate().equals("{lokaal.capaciteit.max}"));
    }
}