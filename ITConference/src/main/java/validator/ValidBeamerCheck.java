package validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BeamerCheckValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.TYPE}) // <-- WIJZIG DEZE REGEL
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBeamerCheck {

    String message() default "{event.beamercheck.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}