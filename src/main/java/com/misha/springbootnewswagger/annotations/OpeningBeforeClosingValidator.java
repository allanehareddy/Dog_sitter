package com.misha.springbootnewswagger.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.sql.Time;

public class OpeningBeforeClosingValidator implements ConstraintValidator<OpeningBeforeClosing, Object> {

    private String openingTimeField;
    private String closingTimeField;

    @Override
    public void initialize(OpeningBeforeClosing constraintAnnotation) {
        this.openingTimeField = "timeOfOpening";
        this.closingTimeField = "timeOfClosing";
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field openingField = value.getClass().getDeclaredField(openingTimeField);
            Field closingField = value.getClass().getDeclaredField(closingTimeField);

            openingField.setAccessible(true);
            closingField.setAccessible(true);

            Time openingTime = (Time) openingField.get(value);
            Time closingTime = (Time) closingField.get(value);

            return openingTime != null && closingTime != null && openingTime.before(closingTime);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}