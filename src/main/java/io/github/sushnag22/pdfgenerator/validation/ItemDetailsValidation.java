package io.github.sushnag22.pdfgenerator.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ItemDetailsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)

// Custom annotation to validate the item details in the PDF data
public @interface ItemDetailsValidation {

    // Message to be displayed when the validation fails
    String message() default "Item details are incorrect. Each item must have 'name', 'quantity', 'rate', and 'amount'";

    // Groups to which this constraint belongs (default is an empty array)
    Class<?>[] groups() default {};

    // Payload to be included in the constraint violation report (default is an empty array)
    Class<? extends Payload>[] payload() default {};
}
