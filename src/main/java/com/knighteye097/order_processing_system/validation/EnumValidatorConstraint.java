package com.knighteye097.order_processing_system.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumValidatorConstraint implements ConstraintValidator<EnumValidator, String> {

    private List<String> acceptedValues;

    @Override
    public void initialize(EnumValidator annotation) {
        acceptedValues = Arrays.stream(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            // Handle null if needed, or return false
            return false;
        }
        return acceptedValues.contains(value);
    }
}
