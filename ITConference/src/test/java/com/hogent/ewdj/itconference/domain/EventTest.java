package com.hogent.ewdj.itconference.domain;

import domain.Event;
import domain.Lokaal;
import domain.Spreker;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import repository.EventRepository;
import validator.EventConstraintsValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventTest {

    private Validator validator;

    private static class InjectingConstraintValidatorFactory implements ConstraintValidatorFactory {
        private final EventRepository repository;

        InjectingConstraintValidatorFactory(EventRepository repository) {
            this.repository = repository;
        }

        @Override
        public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
            try {
                T instance = key.getDeclaredConstructor().newInstance();
                if (instance instanceof EventConstraintsValidator ecv) {
                    ecv.setEventRepository(repository);
                }
                return instance;
            } catch (Exception e) {
                throw new ValidationException("Unable to instantiate validator", e);
            }
        }

        @Override
        public void releaseInstance(ConstraintValidator<?, ?> instance) {
            // No-op
        }
    }

    @BeforeEach
    void beforeEach() {
        EventRepository repo = mock(EventRepository.class);
        when(repo.findByDatumTijdAndLokaal(any(LocalDateTime.class), any(Lokaal.class))).thenReturn(Collections.emptyList());
        when(repo.findByNaamAndDatum(anyString(), any(LocalDate.class))).thenReturn(Collections.emptyList());

        Configuration<?> config = Validation.byDefaultProvider().configure();
        config.constraintValidatorFactory(new InjectingConstraintValidatorFactory(repo));
        ValidatorFactory factory = config.buildValidatorFactory();
        validator = factory.getValidator();
    }

    private Event createEvent(String naam, Lokaal lokaal, List<Spreker> sprekers,
                              LocalDateTime datumTijd, int beamercode, BigDecimal prijs) {
        Event event = new Event(naam, "Beschrijving", sprekers, lokaal, datumTijd, beamercode, prijs);
        event.setBeamercheck(event.calculateCorrectBeamerCheck());
        return event;
    }

    @Test
    void testValidEvent() {
        Lokaal lokaal = new Lokaal("A101", 30);
        Spreker spreker = new Spreker("Jan Janssen");
        Event validEvent = createEvent("TestEvent", lokaal, List.of(spreker),
                LocalDateTime.of(2025, 6, 1, 10, 0), 1234, new BigDecimal("10.00"));

        Set<ConstraintViolation<Event>> violations = validator.validate(validEvent);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testInvalidEventNaamNotBlank(String naam) {
        Lokaal lokaal = new Lokaal("A101", 30);
        Spreker spreker = new Spreker("Jan Janssen");
        Event invalidEvent = createEvent(naam, lokaal, List.of(spreker),
                LocalDateTime.of(2025, 6, 1, 10, 0), 1234, new BigDecimal("10.00"));

        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("naam") &&
                v.getMessageTemplate().equals("{event.naam.notBlank}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1Event", " event", "_Event"})
    void testInvalidEventNaamPattern(String naam) {
        Lokaal lokaal = new Lokaal("A101", 30);
        Spreker spreker = new Spreker("Jan Janssen");
        Event invalidEvent = createEvent(naam, lokaal, List.of(spreker),
                LocalDateTime.of(2025, 6, 1, 10, 0), 1234, new BigDecimal("10.00"));

        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("naam") &&
                v.getMessageTemplate().equals("{event.naam.pattern}"));
    }

    @Test
    void testInvalidEventSprekersSize() {
        Lokaal lokaal = new Lokaal("A101", 30);
        Spreker s1 = new Spreker("Jan Janssen");
        Spreker s2 = new Spreker("Piet Peeters");
        Spreker s3 = new Spreker("Joris Joosten");
        Spreker s4 = new Spreker("Klaas Klaassen");
        Event invalidEvent = createEvent("Test", lokaal, Arrays.asList(s1, s2, s3, s4),
                LocalDateTime.of(2025, 6, 1, 10, 0), 1234, new BigDecimal("10.00"));

        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("sprekers") &&
                v.getMessageTemplate().equals("{event.sprekers.size}"));
    }

    @Test
    void testInvalidEventDuplicateSprekers() {
        Lokaal lokaal = new Lokaal("A101", 30);
        Spreker spreker = new Spreker("Jan Janssen");
        Event invalidEvent = createEvent("Test", lokaal, Arrays.asList(spreker, spreker),
                LocalDateTime.of(2025, 6, 1, 10, 0), 1234, new BigDecimal("10.00"));

        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("sprekers") &&
                v.getMessageTemplate().equals("{event.sprekers.duplicate}"));
    }

    @Test
    void testInvalidEventLokaalNotNull() {
        Spreker spreker = new Spreker("Jan Janssen");
        Event invalidEvent = createEvent("Test", null, List.of(spreker),
                LocalDateTime.of(2025, 6, 1, 10, 0), 1234, new BigDecimal("10.00"));

        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("lokaal") &&
                v.getMessageTemplate().equals("{event.lokaal.notNull}"));
    }

    @Test
    void testInvalidEventDatumTijdPast() {
        Lokaal lokaal = new Lokaal("A101", 30);
        Spreker spreker = new Spreker("Jan Janssen");
        Event invalidEvent = createEvent("Test", lokaal, List.of(spreker),
                LocalDateTime.of(2024, 6, 1, 10, 0), 1234, new BigDecimal("10.00"));

        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("datumTijd") &&
                v.getMessageTemplate().equals("{event.datumTijd.futureOrPresent}"));
    }

    @Test
    void testInvalidEventDatumTijdConferencePeriod() {
        Lokaal lokaal = new Lokaal("A101", 30);
        Spreker spreker = new Spreker("Jan Janssen");
        Event invalidEvent = createEvent("Test", lokaal, List.of(spreker),
                LocalDateTime.of(2026, 1, 1, 10, 0), 1234, new BigDecimal("10.00"));

        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);

        assertThat(violations).isNotEmpty();
        String expectedMessage = "De datum moet tussen 2025-05-18 en 2025-12-31 liggen.";
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("datumTijd") &&
                v.getMessageTemplate().equals(expectedMessage));
    }

    @ParameterizedTest
    @CsvSource({"999", "10000"})
    void testInvalidEventBeamercodeRange(int code) {
        Lokaal lokaal = new Lokaal("A101", 30);
        Spreker spreker = new Spreker("Jan Janssen");
        Event invalidEvent = createEvent("Test", lokaal, List.of(spreker),
                LocalDateTime.of(2025, 6, 1, 10, 0), code, new BigDecimal("10.00"));

        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("beamercode") &&
                v.getMessageTemplate().equals("{event.beamercode.size}"));
    }

    @Test
    void testInvalidEventBeamercheck() {
        Lokaal lokaal = new Lokaal("A101", 30);
        Spreker spreker = new Spreker("Jan Janssen");
        Event invalidEvent = createEvent("Test", lokaal, List.of(spreker),
                LocalDateTime.of(2025, 6, 1, 10, 0), 1234, new BigDecimal("10.00"));
        invalidEvent.setBeamercheck(1); // wrong value

        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("beamercheck") &&
                v.getMessageTemplate().equals("{event.beamercheck.invalid}"));
    }

    @ParameterizedTest
    @CsvSource({"9.98", "100.00"})
    void testInvalidEventPrijsRange(String prijs) {
        Lokaal lokaal = new Lokaal("A101", 30);
        Spreker spreker = new Spreker("Jan Janssen");
        Event invalidEvent = createEvent("Test", lokaal, List.of(spreker),
                LocalDateTime.of(2025, 6, 1, 10, 0), 1234, new BigDecimal(prijs));

        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("prijs"));
    }
}
