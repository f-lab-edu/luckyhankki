package com.java.luckyhankki.config.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WonUnitValidator implements ConstraintValidator<WonUnit, Integer> {

    private int unit;

    @Override
    public void initialize(WonUnit constraintAnnotation) {
        this.unit = constraintAnnotation.unit();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value % unit == 0;
    }
}
