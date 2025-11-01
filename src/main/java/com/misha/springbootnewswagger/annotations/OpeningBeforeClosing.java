package com.misha.springbootnewswagger.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OpeningBeforeClosingValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OpeningBeforeClosing {
    String message() default "Opening time must be before closing time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
