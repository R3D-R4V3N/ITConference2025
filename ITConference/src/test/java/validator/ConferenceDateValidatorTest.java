package validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ConferenceDateValidatorTest {

    private ConferenceDateValidator validator;

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
        assertThat(validator.isValid(null, mock(ConstraintValidatorContext.class))).isTrue();
    }

    @Test
    void returnsTrueForDateWithinPeriod() {
        LocalDateTime date = LocalDateTime.of(2025, 6, 1, 10, 0);
        assertThat(validator.isValid(date, mock(ConstraintValidatorContext.class))).isTrue();
    }

    @Test
    void returnsFalseForDateBeforePeriod() {
        LocalDateTime date = LocalDateTime.of(2025, 5, 1, 10, 0);
        assertThat(validator.isValid(date, mock(ConstraintValidatorContext.class))).isFalse();
    }

    @Test
    void returnsFalseForDateAfterPeriod() {
        LocalDateTime date = LocalDateTime.of(2026, 1, 1, 10, 0);
        assertThat(validator.isValid(date, mock(ConstraintValidatorContext.class))).isFalse();
    }
}
