package validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

class ConferenceDateValidatorTest {

    private ConferenceDateValidator validator;

    private ConstraintValidatorContext mockContext() {
        ConstraintValidatorContext ctx = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext node = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
        when(ctx.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(ctx);
        when(builder.addPropertyNode(anyString())).thenReturn(node);
        when(node.addConstraintViolation()).thenReturn(ctx);
        return ctx;
    }

    static class Dummy {
        @ValidConferenceDate(startDate = "2025-05-18", endDate = "2025-12-31")
        LocalDateTime value;
    }

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        validator = new ConferenceDateValidator();
        ValidConferenceDate annotation = Dummy.class.getDeclaredField("value")
                .getAnnotation(ValidConferenceDate.class);
        validator.initialize(annotation);
    }

    @Test
    void allowsNullValues() {
        assertThat(validator.isValid(null, mockContext())).isTrue();
    }

    @Test
    void returnsTrueForDateWithinPeriod() {
        LocalDateTime date = LocalDateTime.of(2025, 6, 1, 10, 0);
        assertThat(validator.isValid(date, mockContext())).isTrue();
    }

    @Test
    void returnsFalseForDateBeforePeriod() {
        LocalDateTime date = LocalDateTime.of(2025, 5, 1, 10, 0);
        assertThat(validator.isValid(date, mockContext())).isFalse();
    }

    @Test
    void returnsFalseForDateAfterPeriod() {
        LocalDateTime date = LocalDateTime.of(2026, 1, 1, 10, 0);
        assertThat(validator.isValid(date, mockContext())).isFalse();
    }
}
