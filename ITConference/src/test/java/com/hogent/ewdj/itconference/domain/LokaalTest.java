package com.hogent.ewdj.itconference.domain;

import domain.Lokaal;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        Lokaal validLokaal = createLokaal(naam, capaciteit);

        Set<ConstraintViolation<Lokaal>> violations = validator.validate(validLokaal);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testInvalidLokaalNaamNotBlank(String naam) {
        Lokaal invalidLokaal = createLokaal(naam, 30);

        Set<ConstraintViolation<Lokaal>> violations = validator.validate(invalidLokaal);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("naam") &&
                v.getMessageTemplate().equals("{lokaal.naam.notBlank}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234", "a12", "AB123", "A101A", "1A10"})
    void testInvalidLokaalNaamPattern(String naam) {
        Lokaal invalidLokaal = createLokaal(naam, 30);

        Set<ConstraintViolation<Lokaal>> violations = validator.validate(invalidLokaal);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("naam") &&
                v.getMessageTemplate().equals("{lokaal.naam.pattern}"));
    }

    @Test
    void testInvalidLokaalCapaciteitNotNull() {
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void testInvalidLokaalCapaciteitMin(int capaciteit) {
        Lokaal invalidLokaal = createLokaal("A101", capaciteit);

        Set<ConstraintViolation<Lokaal>> violations = validator.validate(invalidLokaal);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("capaciteit") &&
                v.getMessageTemplate().equals("{lokaal.capaciteit.min}"));
    }

    @ParameterizedTest
    @ValueSource(ints = {51, 100, 1000})
    void testInvalidLokaalCapaciteitMax(int capaciteit) {
        Lokaal invalidLokaal = createLokaal("A101", capaciteit);

        Set<ConstraintViolation<Lokaal>> violations = validator.validate(invalidLokaal);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("capaciteit") &&
                v.getMessageTemplate().equals("{lokaal.capaciteit.max}"));
    }
}