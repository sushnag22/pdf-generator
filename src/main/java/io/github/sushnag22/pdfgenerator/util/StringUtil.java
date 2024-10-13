package io.github.sushnag22.pdfgenerator.util;

import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class StringUtil {

    // Helper method to format the field name (capitalize first letter, handle camel case, and detect abbreviations)
    public String formatFieldName(String field) {

        // List of abbreviations that need to be capitalized
        List<String> abbreviations = List.of("gstin");

        // Use a regex to split camel case into words (e.g., sellerName -> Seller Name)
        String formattedField = splitCamelCase(field);

        // Check if the field contains an abbreviation (e.g., GSTIN) and capitalize it
        for (String abbreviation : abbreviations) {
            if (formattedField.toLowerCase().contains(abbreviation.toLowerCase())) {
                formattedField = formattedField.replaceAll("(?i)" + abbreviation, abbreviation.toUpperCase());
            }
        }

        // Capitalize the first letter of each word except for abbreviations
        return capitalizeWords(formattedField);
    }

    // Helper method to split camel case into words
    private String splitCamelCase(String field) {

        // Add a space before each uppercase letter that's followed by a lowercase letter or another uppercase
        Pattern pattern = Pattern.compile("([a-z])([A-Z])");
        Matcher matcher = pattern.matcher(field);
        return matcher.replaceAll("$1 $2");
    }

    // Helper method to capitalize the first letter of each word in the string
    private String capitalizeWords(String field) {
        StringBuilder result = new StringBuilder();

        // Split the string into words based on spaces
        String[] words = field.split(" ");

        for (String word : words) {

            // If the word is an abbreviation, keep it as is (already in uppercase)
            if (word.equals(word.toUpperCase())) {
                result.append(word);
            } else {

                // Capitalize the first letter of the word and append to the result
                result.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase());
            }
            result.append(" ");
        }

        // Remove the trailing space
        return result.toString().trim();
    }

    // Helper method to format the error message with correct grammar
    public String getFormattedErrorMessage(List<FieldError> fieldErrors) {

        // Extract the field names with validation errors
        List<String> fieldNames = fieldErrors.stream()
                .map(FieldError::getField)
                .distinct()
                .map(this::formatFieldName)
                .toList();

        // Handle the formatting for the error message
        if (fieldNames.isEmpty()) {

            // Handle the case where no fields are mandatory
            return "No fields are mandatory";
        } else if (fieldNames.size() == 1) {

            // Handle the singular case with a quoted field name
            return "'" + fieldNames.getFirst() + "' is mandatory";
        } else {

            // Handle the plural case with quoted field names separated by commas and an 'and'
            String allButLast = fieldNames.subList(0, fieldNames.size() - 1).stream()
                    .map(field -> "'" + field + "'")
                    .collect(Collectors.joining(", "));
            String lastField = "'" + fieldNames.getLast() + "'";
            return allButLast + ", and " + lastField + " are mandatory";
        }
    }
}
