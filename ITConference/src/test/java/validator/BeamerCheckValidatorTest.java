package validator;

import domain.Event;
import domain.Lokaal;
import domain.Spreker;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BeamerCheckValidatorTest {

    private ConstraintValidatorContext mockContext() {
        ConstraintValidatorContext ctx = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext node = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
        when(ctx.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(node);
        when(node.addConstraintViolation()).thenReturn(ctx);
        return ctx;
    }

    private Event createEvent(int beamercode, int beamercheck) {
        Event event = new Event("Test", "desc",
                List.of(new Spreker("Jan")),
                new Lokaal("A101", 30),
                LocalDateTime.of(2025, 6, 1, 10, 0),
                beamercode,
                new BigDecimal("10.00"));
        event.setBeamercheck(beamercheck);
        return event;
    }

    @Test
    void returnsTrueWhenBeamercheckMatches() {
        Event event = createEvent(1234, 1234 % 97);
        BeamerCheckValidator validator = new BeamerCheckValidator();
        boolean result = validator.isValid(event, mockContext());
        assertThat(result).isTrue();
    }

    @Test
    void returnsFalseWhenBeamercheckDoesNotMatch() {
        Event event = createEvent(1234, 1);
        BeamerCheckValidator validator = new BeamerCheckValidator();
        boolean result = validator.isValid(event, mockContext());
        assertThat(result).isFalse();
    }
}
