package com.knighteye097.order_processing_system.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumValidatorConstraint.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValidator {
    Class<? extends Enum<?>> enumClass();
    String message() default "Value is not valid. Allowed values are: {enumValues}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}