package com.java.luckyhankki.config.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PickupDateValidator implements ConstraintValidator<PickupDate, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        LocalDate pickupDate = value.toLocalDate();
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        return pickupDate.isEqual(today) || pickupDate.isEqual(tomorrow);
    }
}
