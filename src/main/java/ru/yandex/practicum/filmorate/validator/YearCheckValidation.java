package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class YearCheckValidation implements ConstraintValidator<YearValidator, Integer> {
    @Override
    public boolean isValid(Integer date, ConstraintValidatorContext constraintValidatorContext) {
        return (date > 1895 || date == 0);
    }
}
