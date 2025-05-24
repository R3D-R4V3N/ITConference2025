package validator;

import domain.Spreker;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SpeakerListValidatorTest {

    private final SpeakerListValidator validator = new SpeakerListValidator();

    @Test
    void returnsTrueWhenListIsNull() {
        assertThat(validator.isValid(null, mock(ConstraintValidatorContext.class))).isTrue();
    }

    @Test
    void returnsTrueForUniqueSpeakers() {
        List<Spreker> list = List.of(new Spreker("A"), new Spreker("B"));
        assertThat(validator.isValid(list, mock(ConstraintValidatorContext.class))).isTrue();
    }

    @Test
    void returnsFalseForDuplicateSpeakers() {
        Spreker s = new Spreker("A");
        List<Spreker> list = List.of(s, s);
        assertThat(validator.isValid(list, mock(ConstraintValidatorContext.class))).isFalse();
    }

    @Test
    void returnsFalseForNullElement() {
        List<Spreker> list = new java.util.ArrayList<>();
        list.add(new Spreker("A"));
        list.add(null);
        assertThat(validator.isValid(list, mock(ConstraintValidatorContext.class))).isFalse();
    }
}
