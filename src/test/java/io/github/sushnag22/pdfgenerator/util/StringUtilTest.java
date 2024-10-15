package io.github.sushnag22.pdfgenerator.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilTest {

    // Inject the string utility class
    @InjectMocks
    private StringUtil stringUtil;

    // Setup method to initialize the mocks
    @BeforeEach
    public void setUp() {

        // Initialize the mocks
        MockitoAnnotations.openMocks(this);
    }

    // Test the `formatFieldName` method
    @Test
    public void testFormatFieldName_WithCamelCase() {

        // Test formatting for a camel case field
        String field = "sellerName";

        // Format the field name
        String result = stringUtil.formatFieldName(field);

        // Assert the formatted field name
        assertEquals("Seller Name", result);
    }


    // Test the `formatFieldName` method with an abbreviation
    @Test
    public void testFormatFieldName_WithAbbreviation() {

        // Test formatting for a field containing an abbreviation
        String field = "gstin";

        // Format the field name
        String result = stringUtil.formatFieldName(field);

        // Assert the formatted field name
        assertEquals("GSTIN", result);
    }

    // Test the `formatFieldName` method with mixed case and an abbreviation
    @Test
    public void testFormatFieldName_WithMixedCaseAndAbbreviation() {

        // Test formatting for a camel case field containing an abbreviation
        String field = "sellerGstin";

        // Format the field name
        String result = stringUtil.formatFieldName(field);

        // Assert the formatted field name
        assertEquals("Seller GSTIN", result);
    }

    // Test the `splitCamelCase` method
    @Test
    public void testSplitCamelCase() {

        // Test splitting camel case field into words
        String field = "sellerName";

        // Split the camel case field
        String result = stringUtil.splitCamelCase(field);

        // Assert the split field
        assertEquals("seller Name", result);
    }

    // Test the `capitalizeWords` method
    @Test
    public void testCapitalizeWords_WithNormalWords() {

        // Test capitalizing normal words
        String field = "seller name";

        // Capitalize the words
        String result = stringUtil.capitalizeWords(field);

        // Assert the capitalized words
        assertEquals("Seller Name", result);
    }

    // Test the `getFormattedErrorMessage` method with one field error
    @Test
    public void testGetFormattedErrorMessage_WithOneField() {

        // Test error message generation with a single field error
        List<FieldError> fieldErrors = new ArrayList<>();

        // Add a single field error
        fieldErrors.add(new FieldError("objectName", "sellerName", "must not be empty"));

        // Generate the error message
        String result = stringUtil.getFormattedErrorMessage(fieldErrors);

        // Assert the error message
        assertEquals("'Seller Name' is mandatory", result);
    }

    // Test the `getFormattedErrorMessage` method with multiple field errors
    @Test
    public void testGetFormattedErrorMessage_WithMultipleFields() {

        // Test error message generation with multiple field errors
        List<FieldError> fieldErrors = new ArrayList<>();

        // Add multiple field errors
        fieldErrors.add(new FieldError("objectName", "sellerName", "must not be empty"));
        fieldErrors.add(new FieldError("objectName", "sellerGstin", "must not be empty"));

        // Generate the error message
        String result = stringUtil.getFormattedErrorMessage(fieldErrors);

        // Assert the error message
        assertEquals("'Seller Name', and 'Seller GSTIN' are mandatory", result);
    }

    // Test the `getFormattedErrorMessage` method with no errors
    @Test
    public void testGetFormattedErrorMessage_WithNoErrors() {

        // Test error message generation when there are no field errors
        List<FieldError> fieldErrors = new ArrayList<>();

        // Generate the error message
        String result = stringUtil.getFormattedErrorMessage(fieldErrors);

        // Assert the error message
        assertEquals("No fields are mandatory", result);
    }
}
