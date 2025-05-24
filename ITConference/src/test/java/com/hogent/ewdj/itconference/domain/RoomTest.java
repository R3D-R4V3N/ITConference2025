package com.hogent.ewdj.itconference.domain;

import domain.Room;
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

public class RoomTest {

    private Validator validator;

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Room createRoom(String name, int capacity) {
        return new Room(name, capacity);
    }

    @ParameterizedTest
    @CsvSource({
            "A101, 50",
            "B001, 1",
            "C999, 49"
    })
    void testValidRoom(String name, int capacity) {
        Room validRoom = createRoom(name, capacity);

        Set<ConstraintViolation<Room>> violations = validator.validate(validRoom);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testInvalidRoomNameNotBlank(String name) {
        Room invalidRoom = createRoom(name, 30);

        Set<ConstraintViolation<Room>> violations = validator.validate(invalidRoom);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("naam") &&
                v.getMessageTemplate().equals("{lokaal.naam.notBlank}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234", "a12", "AB123", "A101A", "1A10"})
    void testInvalidRoomNamePattern(String name) {
        Room invalidRoom = createRoom(name, 30);

        Set<ConstraintViolation<Room>> violations = validator.validate(invalidRoom);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("naam") &&
                v.getMessageTemplate().equals("{lokaal.naam.pattern}"));
    }

    @Test
    void testInvalidRoomCapacityNotNull() {
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void testInvalidRoomCapacityMin(int capacity) {
        Room invalidRoom = createRoom("A101", capacity);

        Set<ConstraintViolation<Room>> violations = validator.validate(invalidRoom);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("capaciteit") &&
                v.getMessageTemplate().equals("{lokaal.capaciteit.min}"));
    }

    @ParameterizedTest
    @ValueSource(ints = {51, 100, 1000})
    void testInvalidRoomCapacityMax(int capacity) {
        Room invalidRoom = createRoom("A101", capacity);

        Set<ConstraintViolation<Room>> violations = validator.validate(invalidRoom);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("capaciteit") &&
                v.getMessageTemplate().equals("{lokaal.capaciteit.max}"));
    }
}