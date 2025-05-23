package com.hogent.ewdj.itconference.domain;

import domain.Spreker;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SprekerTest {

    private Validator validator;

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Spreker createSpreker(String naam) {
        return new Spreker(naam);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Jan Janssen", "Piet Peeters", "Joris Joosten"})
    void testValidSpreker(String naam) {
        // Arrange: Maak een Spreker object aan met geldige naam
        Spreker validSpreker = createSpreker(naam);

        // Act: Valideer het Spreker object
        Set<ConstraintViolation<Spreker>> violations = validator.validate(validSpreker);

        // Assert: Controleer of er geen validatiefouten zijn
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testInvalidSprekerNaamNotBlank(String naam) {
        // Arrange: Maak een Spreker object aan met ongeldige naam (leeg/spaties)
        Spreker invalidSpreker = createSpreker(naam);

        // Act: Valideer het Spreker object
        Set<ConstraintViolation<Spreker>> violations = validator.validate(invalidSpreker);

        // Assert: Controleer op validatiefouten voor 'naam'
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("naam") &&
                v.getMessageTemplate().equals("{spreker.naam.notBlank}"));
    }
}