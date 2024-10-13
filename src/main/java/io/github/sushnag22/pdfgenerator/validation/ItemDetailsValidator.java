package io.github.sushnag22.pdfgenerator.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.Map;

public class ItemDetailsValidator implements ConstraintValidator<ItemDetailsValidation, List<Map<String, Object>>> {

    @Override
    public boolean isValid(List<Map<String, Object>> items, ConstraintValidatorContext context) {
        if (items == null || items.isEmpty()) {

            // The validation fails if the list is null or empty
            return false;
        }

        for (Map<String, Object> item : items) {
            if (!item.containsKey("name") ||
                    !item.containsKey("quantity") ||
                    !item.containsKey("rate") ||
                    !item.containsKey("amount")) {

                // The validation fails if any key is missing
                return false;
            }

            if (!(item.get("name") instanceof String) ||
                    !(item.get("quantity") instanceof String) ||
                    !(item.get("rate") instanceof Number) ||
                    !(item.get("amount") instanceof Number)) {

                // The validation fails if the data types are incorrect
                return false;
            }
        }

        // The validation passes if all maps (items) contain the required keys and valid data types
        return true;
    }
}
