package validator;

import domain.Event;
import domain.Lokaal;
import domain.Spreker;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.EventRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EventConstraintsValidatorTest {

    private EventRepository repository;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        repository = mock(EventRepository.class);
        context = mockContext();
    }

    private ConstraintValidatorContext mockContext() {
        ConstraintValidatorContext ctx = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext node = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
        when(ctx.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(node);
        when(node.addConstraintViolation()).thenReturn(ctx);
        return ctx;
    }

    private Event createEvent(Long id, String naam, LocalDateTime datumTijd, Lokaal lokaal) {
        Event event = new Event(naam, "desc", List.of(new Spreker("Jan")), lokaal, datumTijd, 1234, new BigDecimal("10.00"));
        event.setBeamercheck(event.calculateCorrectBeamerCheck());
        event.setId(id);
        return event;
    }

    @Test
    void returnsFalseWhenRepositoryNotInjected() {
        EventConstraintsValidator validator = new EventConstraintsValidator();
        Event event = createEvent(null, "Test", LocalDateTime.now(), new Lokaal("A101", 30));
        assertThat(validator.isValid(event, context)).isFalse();
    }

    @Test
    void returnsTrueWhenNoDuplicatesFound() {
        when(repository.findByDatumTijdAndLokaal(any(LocalDateTime.class), any(Lokaal.class)))
                .thenReturn(Collections.emptyList());
        when(repository.findByNaamAndDatum(anyString(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        EventConstraintsValidator validator = new EventConstraintsValidator();
        validator.setEventRepository(repository);

        Event event = createEvent(1L, "Test", LocalDateTime.of(2025,6,1,10,0), new Lokaal("A101", 30));
        assertThat(validator.isValid(event, context)).isTrue();
    }

    @Test
    void returnsFalseWhenDuplicateTimeAndLocationFound() {
        Event duplicate = createEvent(2L, "Dup", LocalDateTime.of(2025,6,1,10,0), new Lokaal("A101", 30));
        when(repository.findByDatumTijdAndLokaal(any(LocalDateTime.class), any(Lokaal.class)))
                .thenReturn(List.of(duplicate));
        when(repository.findByNaamAndDatum(anyString(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        EventConstraintsValidator validator = new EventConstraintsValidator();
        validator.setEventRepository(repository);

        Event event = createEvent(1L, "Test", LocalDateTime.of(2025,6,1,10,0), new Lokaal("A101", 30));
        assertThat(validator.isValid(event, context)).isFalse();
    }

    @Test
    void returnsFalseWhenDuplicateNameAndDateFound() {
        Event duplicate = createEvent(2L, "Test", LocalDateTime.of(2025,6,1,10,0), new Lokaal("B202", 30));
        when(repository.findByDatumTijdAndLokaal(any(LocalDateTime.class), any(Lokaal.class)))
                .thenReturn(Collections.emptyList());
        when(repository.findByNaamAndDatum(anyString(), any(LocalDate.class)))
                .thenReturn(List.of(duplicate));

        EventConstraintsValidator validator = new EventConstraintsValidator();
        validator.setEventRepository(repository);

        Event event = createEvent(1L, "Test", LocalDateTime.of(2025,6,1,10,0), new Lokaal("A101", 30));
        assertThat(validator.isValid(event, context)).isFalse();
    }

    @Test
    void ignoresDuplicatesWithSameId() {
        Event same = createEvent(1L, "Test", LocalDateTime.of(2025,6,1,10,0), new Lokaal("A101", 30));
        when(repository.findByDatumTijdAndLokaal(any(LocalDateTime.class), any(Lokaal.class)))
                .thenReturn(List.of(same));
        when(repository.findByNaamAndDatum(anyString(), any(LocalDate.class)))
                .thenReturn(List.of(same));

        EventConstraintsValidator validator = new EventConstraintsValidator();
        validator.setEventRepository(repository);

        Event event = createEvent(1L, "Test", LocalDateTime.of(2025,6,1,10,0), new Lokaal("A101", 30));
        assertThat(validator.isValid(event, context)).isTrue();
    }
}
