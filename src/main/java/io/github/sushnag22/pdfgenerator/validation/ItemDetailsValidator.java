package io.github.sushnag22.pdfgenerator.validation;

import io.github.sushnag22.pdfgenerator.model.ItemDetailsModel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.util.List;

public class ItemDetailsValidator implements ConstraintValidator<ItemDetailsValidation, List<ItemDetailsModel>> {

    @Override
    public boolean isValid(List<ItemDetailsModel> items, ConstraintValidatorContext context) {
        // Clear existing violations
        context.disableDefaultConstraintViolation();

        if (items == null || items.isEmpty()) {

            // Add validation for empty item details
            context.buildConstraintViolationWithTemplate("Item details cannot be empty")
                    .addConstraintViolation();
            return false;
        }

        boolean isValid = true;

        for (ItemDetailsModel item : items) {

            // Validate item quantity to be greater than 0
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                context.buildConstraintViolationWithTemplate("Item quantity is mandatory and must be greater than 0")
                        .addPropertyNode("quantity")
                        .addConstraintViolation();
                isValid = false;
            }

            // Validate item rate to be greater than 0
            if (item.getRate() == null || item.getRate().compareTo(BigDecimal.ZERO) <= 0) {
                context.buildConstraintViolationWithTemplate("Item rate is mandatory and must be greater than 0")
                        .addPropertyNode("rate")
                        .addConstraintViolation();
                isValid = false;
            }

            // Validate item amount to be greater than 0
            if (item.getAmount() == null) {
                context.buildConstraintViolationWithTemplate("Item amount is mandatory and must be greater than 0")
                        .addPropertyNode("amount")
                        .addConstraintViolation();
                isValid = false;
            }

            // Check if amount matches quantity * rate
            if (item.getQuantity() != null && item.getRate() != null && item.getAmount() != null) {
                BigDecimal expectedAmount = item.getRate().multiply(BigDecimal.valueOf(item.getQuantity().longValue()));
                if (item.getAmount().compareTo(expectedAmount) != 0) {
                    context.buildConstraintViolationWithTemplate("Item amount must be equal to quantity multiplied by rate")
                            .addPropertyNode("amount")
                            .addConstraintViolation();
                    isValid = false;
                }
            }
        }

        // Return the overall validation result
        return isValid;
    }
}
