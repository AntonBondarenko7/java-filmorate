package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {YearCheckValidation.class})
public @interface YearValidator {
    String message() default "{Validation error: Год должен быть до 1895 включительно}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
