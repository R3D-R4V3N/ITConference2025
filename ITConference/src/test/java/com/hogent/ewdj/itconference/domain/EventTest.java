package com.hogent.ewdj.itconference.domain;

import domain.Event;
import domain.Lokaal;
import domain.Spreker;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.core.ResolvableType;
import repository.EventRepository;
import validator.EventConstraintsValidator;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet; // Added for HashSet
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class EventTest {

    private Validator validator;
    private EventRepository eventRepository; // Mock deze repository voor database validaties

    private Lokaal testLokaal;
    private Spreker testSpreker1;
    private Spreker testSpreker2;
    private Spreker testSpreker3;
    private Spreker testSpreker4;


    @BeforeEach
    public void setupCommonData() {
        testLokaal = new Lokaal(1L, "A101", 50);
        // Corrected Spreker instantiation to use the available constructor
        testSpreker1 = new Spreker(1L, "Jan Janssen", new HashSet<>());
        testSpreker2 = new Spreker(2L, "Piet Peeters", new HashSet<>());
        testSpreker3 = new Spreker(3L, "Joris Joosten", new HashSet<>());
        testSpreker4 = new Spreker(4L, "Anna Alberts", new HashSet<>());

        // Mock de EventRepository
        eventRepository = Mockito.mock(EventRepository.class);

        // Configureer de validator met een custom ConstraintValidatorFactory
        // Dit is nodig om Spring-geïnjecteerde validators (zoals EventConstraintsValidator)
        // te testen in een pure JUnit context, door de mock repository te injecteren.
        ValidatorFactory factory = Validation.byProvider(org.hibernate.validator.HibernateValidator.class)
                .configure()
                .constraintValidatorFactory(new SpringConstraintValidatorFactory(
                        new AutowireCapableBeanFactory() {
                            @Override
                            public Object getBean(String name) throws BeansException {
                                return null;
                            }

                            @Override
                            public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
                                return null;
                            }

                            @Override
                            public Object getBean(String name, Object... args) throws BeansException {
                                return null;
                            }

                            @Override
                            public <T> T getBean(Class<T> requiredType) throws BeansException {
                                return null;
                            }

                            @Override
                            public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
                                return null;
                            }

                            @Override
                            public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
                                return null;
                            }

                            @Override
                            public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
                                return null;
                            }

                            @Override
                            public boolean containsBean(String name) {
                                return false;
                            }

                            @Override
                            public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
                                return false;
                            }

                            @Override
                            public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
                                return false;
                            }

                            @Override
                            public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
                                return false;
                            }

                            @Override
                            public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
                                return false;
                            }

                            @Override
                            public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
                                return null;
                            }

                            @Override
                            public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
                                return null;
                            }

                            @Override
                            public String[] getAliases(String name) {
                                return new String[0];
                            }

                            @Override
                            public <T> T createBean(Class<T> beanClass) throws BeansException {
                                if (beanClass.equals(EventConstraintsValidator.class)) {
                                    EventConstraintsValidator ecv = new EventConstraintsValidator();
                                    ecv.setEventRepository(eventRepository); // Injecteer de mock
                                    return (T) ecv;
                                }
                                try {
                                    // Use getDeclaredConstructor().newInstance() to avoid deprecated newInstance()
                                    return beanClass.getDeclaredConstructor().newInstance();
                                } catch (Exception e) { // Catch broader Exception for reflection issues
                                    throw new BeansException("Failed to instantiate bean: " + beanClass.getName(), e) {};
                                }
                            }

                            @Override
                            public void autowireBean(Object existingBean) throws BeansException { /* no-op */ }
                            @Override
                            public Object configureBean(Object existingBean, String beanName) throws BeansException { return existingBean; }

                            @Override
                            public Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
                                return null;
                            }

                            @Override
                            public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
                                return null;
                            }

                            @Override
                            public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException {

                            }

                            @Override
                            public void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException {

                            }

                            @Override
                            public Object initializeBean(Object existingBean, String beanName) throws BeansException {
                                return null;
                            }

                            @Override
                            public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
                                return null;
                            }

                            @Override
                            public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
                                return null;
                            }

                            @Override
                            public void destroyBean(Object existingBean) {

                            }

                            @Override
                            public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
                                return null;
                            }

                            @Override
                            public Object resolveBeanByName(String name, DependencyDescriptor descriptor) throws BeansException {
                                return null;
                            }

                            @Override
                            public Object resolveDependency(DependencyDescriptor descriptor, String beanName) throws BeansException {
                                if (descriptor.getDependencyType().equals(EventRepository.class)) {
                                    return eventRepository;
                                }
                                return null;
                            }

                            @Override
                            public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName, Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException {
                                return null;
                            }
                            // Removed the problematic resolveDependency method as it's not needed
                            // and causes compilation issues with BeanContainer.
                        }
                ))
                .buildValidatorFactory();
        validator = factory.getValidator();
    }

    // Helper methode om een Event object te creëren
    private Event createEvent(Long id, String naam, String beschrijving, List<Spreker> sprekers, Lokaal lokaal,
                              LocalDateTime datumTijd, int beamercode, BigDecimal prijs) {
        Event event = new Event(naam, beschrijving, sprekers, lokaal, datumTijd, beamercode, prijs);
        event.setId(id);
        event.setBeamercheck(event.calculateCorrectBeamerCheck()); // Bereken de correcte beamercheck
        return event;
    }

    // Helper methode om een Event object te creëren met een specifieke beamercheck
    private Event createEventWithSpecificBeamercheck(Long id, String naam, String beschrijving, List<Spreker> sprekers, Lokaal lokaal,
                                                     LocalDateTime datumTijd, int beamercode, int beamercheck, BigDecimal prijs) {
        Event event = new Event(naam, beschrijving, sprekers, lokaal, datumTijd, beamercode, prijs);
        event.setId(id);
        event.setBeamercheck(beamercheck);
        return event;
    }


    // --- Validatie tests voor individuele velden (zonder @ValidEventConstraints) ---

    @Test
    void testValidEvent() {
        Event validEvent = createEvent(
                1L,
                "Geldig Evenement",
                "Beschrijving van een geldig evenement.",
                Arrays.asList(testSpreker1),
                testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                1234,
                new BigDecimal("50.00")
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(validEvent);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testInvalidEventNaamNotBlank(String naam) {
        Event invalidEvent = createEvent(
                1L,
                naam,
                "Beschrijving",
                Arrays.asList(testSpreker1),
                testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                1234,
                new BigDecimal("50.00")
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("naam") &&
                v.getMessageTemplate().equals("{event.naam.notBlank}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1Evenement", " Evenement", "!Evenement"})
    void testInvalidEventNaamPattern(String naam) {
        Event invalidEvent = createEvent(
                1L,
                naam,
                "Beschrijving",
                Arrays.asList(testSpreker1),
                testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                1234,
                new BigDecimal("50.00")
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("naam") &&
                v.getMessageTemplate().equals("{event.naam.pattern}"));
    }

    @Test
    void testInvalidEventSprekersNotNull() {
        Event invalidEvent = createEvent(
                1L,
                "Evenement",
                "Beschrijving",
                null, // null sprekers
                testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                1234,
                new BigDecimal("50.00")
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("sprekers") &&
                v.getMessageTemplate().equals("{event.sprekers.notNull}"));
    }

    @Test
    void testInvalidEventSprekersSizeMin() {
        Event invalidEvent = createEvent(
                1L,
                "Evenement",
                "Beschrijving",
                Collections.emptyList(), // lege sprekers lijst
                testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                1234,
                new BigDecimal("50.00")
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("sprekers") &&
                v.getMessageTemplate().equals("{event.sprekers.size}"));
    }

    @Test
    void testInvalidEventSprekersSizeMax() {
        Event invalidEvent = createEvent(
                1L,
                "Evenement",
                "Beschrijving",
                Arrays.asList(testSpreker1, testSpreker2, testSpreker3, testSpreker4), // 4 sprekers
                testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                1234,
                new BigDecimal("50.00")
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("sprekers") &&
                v.getMessageTemplate().equals("{event.sprekers.size}"));
    }

    @Test
    void testInvalidEventSprekersDuplicate() {
        Event invalidEvent = createEvent(
                1L,
                "Evenement",
                "Beschrijving",
                Arrays.asList(testSpreker1, testSpreker1), // Dubbele spreker
                testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                1234,
                new BigDecimal("50.00")
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("sprekers") &&
                v.getMessageTemplate().equals("{event.sprekers.duplicate}"));
    }

    @Test
    void testInvalidEventLokaalNotNull() {
        Event invalidEvent = createEvent(
                1L,
                "Evenement",
                "Beschrijving",
                Arrays.asList(testSpreker1),
                null, // null lokaal
                LocalDateTime.of(2025, 6, 1, 10, 0),
                1234,
                new BigDecimal("50.00")
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("lokaal") &&
                v.getMessageTemplate().equals("{event.lokaal.notNull}"));
    }

    @Test
    void testInvalidEventDatumTijdNotNull() {
        Event invalidEvent = createEvent(
                1L,
                "Evenement",
                "Beschrijving",
                Arrays.asList(testSpreker1),
                testLokaal,
                null, // null datumTijd
                1234,
                new BigDecimal("50.00")
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("datumTijd") &&
                v.getMessageTemplate().equals("{event.datumTijd.notNull}"));
    }

    @Test
    void testInvalidEventDatumTijdFutureOrPresent() {
        Event invalidEvent = createEvent(
                1L,
                "Evenement",
                "Beschrijving",
                Arrays.asList(testSpreker1),
                testLokaal,
                LocalDateTime.of(2020, 1, 1, 10, 0), // Datum in het verleden
                1234,
                new BigDecimal("50.00")
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("datumTijd") &&
                v.getMessageTemplate().equals("{event.datumTijd.futureOrPresent}"));
    }

    @Test
    void testInvalidEventDatumTijdOutOfConferencePeriod() {
        Event invalidEvent = createEvent(
                1L,
                "Evenement",
                "Beschrijving",
                Arrays.asList(testSpreker1),
                testLokaal,
                LocalDateTime.of(2024, 1, 1, 10, 0), // Buiten 2025 periode (volgens ValidConferenceDate in Event.java)
                1234,
                new BigDecimal("50.00")
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("datumTijd") &&
                // De boodschap wordt direct geinterpolleerd in de validator.
                v.getMessage().equals("De datum moet tussen 2025-05-18 en 2025-12-31 liggen."));
    }

    @ParameterizedTest
    @ValueSource(ints = {999, 10000, 0})
    void testInvalidEventBeamercodeSize(int beamercode) {
        Event invalidEvent = createEvent(
                1L,
                "Evenement",
                "Beschrijving",
                Arrays.asList(testSpreker1),
                testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                beamercode,
                new BigDecimal("50.00")
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("beamercode") &&
                v.getMessageTemplate().equals("{event.beamercode.size}"));
    }

    @Test
    void testInvalidEventBeamercheck() {
        Event invalidEvent = createEventWithSpecificBeamercheck(
                1L,
                "Evenement",
                "Beschrijving",
                Arrays.asList(testSpreker1),
                testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                1234,
                0, // Foutieve beamercheck (moet 1234 % 97 = 69 zijn)
                new BigDecimal("50.00")
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("beamercheck") &&
                v.getMessageTemplate().equals("{event.beamercheck.invalid}"));
    }

    @Test
    void testInvalidEventPrijsNotNull() {
        Event invalidEvent = createEvent(
                1L,
                "Evenement",
                "Beschrijving",
                Arrays.asList(testSpreker1),
                testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                1234,
                null // null prijs
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("prijs") &&
                v.getMessageTemplate().equals("{event.prijs.notNull}"));
    }

    @Test
    void testInvalidEventPrijsMin() {
        Event invalidEvent = createEvent(
                1L,
                "Evenement",
                "Beschrijving",
                Arrays.asList(testSpreker1),
                testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                1234,
                new BigDecimal("9.98") // Prijs kleiner dan 9.99
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("prijs") &&
                v.getMessageTemplate().equals("{event.prijs.min}"));
    }

    @Test
    void testInvalidEventPrijsMax() {
        Event invalidEvent = createEvent(
                1L,
                "Evenement",
                "Beschrijving",
                Arrays.asList(testSpreker1),
                testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                1234,
                new BigDecimal("100.00") // Prijs 100.00 (moet kleiner dan 100 zijn, dus exclusief)
        );
        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("prijs") &&
                v.getMessageTemplate().equals("{event.prijs.max}"));
    }

    // --- Validatie tests voor database constraints (@ValidEventConstraints) ---
    @Test
    void testDuplicateTimeLocation() {
        Event existingEvent = createEvent(
                100L, // Een ID voor een 'bestaand' evenement
                "Bestaand Evenement", "Omschrijving", Arrays.asList(testSpreker1), testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0), 1234, new BigDecimal("50.00")
        );
        // Stel in dat de repository een bestaand evenement vindt op dezelfde tijd en locatie
        Mockito.when(eventRepository.findByDatumTijdAndLokaal(
                        eq(LocalDateTime.of(2025, 6, 1, 10, 0)), eq(testLokaal)))
                .thenReturn(Collections.singletonList(existingEvent));

        Event newEvent = createEvent(
                200L, // Nieuw, verschillend ID
                "Nieuw Evenement", "Nieuwe Omschrijving", Arrays.asList(testSpreker2), testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0), 5678, new BigDecimal("60.00")
        );

        Set<ConstraintViolation<Event>> violations = validator.validate(newEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("datumTijd") &&
                v.getMessageTemplate().equals("{event.constraints.duplicateTimeLocation}"));
    }

    @Test
    void testNoDuplicateTimeLocationForSameEventEdit() {
        Event existingEvent = createEvent(
                100L, // Een ID voor een 'bestaand' evenement
                "Bestaand Evenement", "Omschrijving", Arrays.asList(testSpreker1), testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0), 1234, new BigDecimal("50.00")
        );
        // Wanneer we een bestaand evenement editen, mag het niet als duplicaat gezien worden.
        // De mock moet hetzelfde evenement teruggeven, wat betekent dat het evenement met dezelfde ID wordt gevonden.
        Mockito.when(eventRepository.findByDatumTijdAndLokaal(
                        eq(LocalDateTime.of(2025, 6, 1, 10, 0)), eq(testLokaal)))
                .thenReturn(Collections.singletonList(existingEvent));

        // Valideer hetzelfde evenement alsof het geüpdatet wordt (met hetzelfde ID)
        Set<ConstraintViolation<Event>> violations = validator.validate(existingEvent);

        // Filter op de specifieke constraint en controleer dat deze niet aanwezig is.
        assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("datumTijd") &&
                v.getMessageTemplate().equals("{event.constraints.duplicateTimeLocation}"));
    }

    @Test
    void testDuplicateNameDay() {
        Event existingEvent = createEvent(
                101L, // Een ID voor een 'bestaand' evenement
                "Dubbel Naam Evenement", "Omschrijving", Arrays.asList(testSpreker1), testLokaal,
                LocalDateTime.of(2025, 6, 1, 12, 0), 1234, new BigDecimal("50.00")
        );
        // Stel in dat de repository een bestaand evenement vindt met dezelfde naam op dezelfde dag
        Mockito.when(eventRepository.findByNaamAndDatum(
                        eq("Dubbel Naam Evenement"), eq(LocalDateTime.of(2025, 6, 1, 12, 0).toLocalDate())))
                .thenReturn(Collections.singletonList(existingEvent));

        Event newEvent = createEvent(
                201L, // Nieuw, verschillend ID
                "Dubbel Naam Evenement", "Nieuwe Omschrijving", Arrays.asList(testSpreker2), testLokaal,
                LocalDateTime.of(2025, 6, 1, 14, 0), 5678, new BigDecimal("60.00")
        );

        Set<ConstraintViolation<Event>> violations = validator.validate(newEvent);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("naam") &&
                v.getMessageTemplate().equals("{event.constraints.duplicateNameDay}"));
    }

    @Test
    void testNoDuplicateNameDayForSameEventEdit() {
        Event existingEvent = createEvent(
                101L, // Een ID voor een 'bestaand' evenement
                "Unieke Naam Evenement", "Omschrijving", Arrays.asList(testSpreker1), testLokaal,
                LocalDateTime.of(2025, 6, 1, 10, 0), 1234, new BigDecimal("50.00")
        );
        // Wanneer we een bestaand evenement editen, mag het niet als duplicaat gezien worden.
        Mockito.when(eventRepository.findByNaamAndDatum(
                        eq("Unieke Naam Evenement"), eq(LocalDateTime.of(2025, 6, 1, 10, 0).toLocalDate())))
                .thenReturn(Collections.singletonList(existingEvent));

        // Valideer hetzelfde evenement alsof het geüpdatet wordt (met hetzelfde ID)
        Set<ConstraintViolation<Event>> violations = validator.validate(existingEvent);

        // Filter op de specifieke constraint en controleer dat deze niet aanwezig is.
        assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("naam") &&
                v.getMessageTemplate().equals("{event.constraints.duplicateNameDay}"));
    }
}