package io.github.sushnag22.pdfgenerator.validation;

import io.github.sushnag22.pdfgenerator.model.ItemDetailsModel;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ItemDetailsValidatorTest {

    // Inject the mocks
    @InjectMocks
    private ItemDetailsValidator itemDetailsValidator;

    // Mock the constraint validator context
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    // Mock the constraint violation builder
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;

    // Mock the node builder customizable context
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilderCustomizableContext;

    // Setup method to initialize the mocks
    @BeforeEach
    public void setUp() {

        // Initialize the mocks
        MockitoAnnotations.openMocks(this);

        // Mock behavior for context and violation builder
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode(anyString()))
                .thenReturn(nodeBuilderCustomizableContext);
    }

    // Test case for empty item details
    @Test
    public void testIsValid_EmptyItemDetails() {

        // Create a list of empty items
        List<ItemDetailsModel> items = new ArrayList<>();
        boolean result = itemDetailsValidator.isValid(items, constraintValidatorContext);

        // Assert the validation result
        assertFalse(result);

        // Verify that the correct violation message is added
        verify(constraintValidatorContext, times(1))
                .buildConstraintViolationWithTemplate("Item details cannot be empty");
    }

    // Test case for valid item details
    @Test
    public void testIsValid_ValidItemDetails() {

        // Create a list of valid items
        List<ItemDetailsModel> items = new ArrayList<>();
        items.add(new ItemDetailsModel("Item 1", 2, new BigDecimal("100.00"), new BigDecimal("200.00")));  // quantity * rate = amount

        // Validate the items
        boolean result = itemDetailsValidator.isValid(items, constraintValidatorContext);

        // Assert the validation result
        assertTrue(result);

        // Verify that no violation messages are added
        verify(constraintValidatorContext, never())
                .buildConstraintViolationWithTemplate(anyString()); // No violations should be added
    }

    // Test case for invalid quantity
    @Test
    public void testIsValid_InvalidQuantity() {

        // Create a list of items with invalid quantity
        List<ItemDetailsModel> items = new ArrayList<>();
        items.add(new ItemDetailsModel("Item 2", 0, new BigDecimal("100.00"), new BigDecimal("200.00")));

        // Validate the items
        boolean result = itemDetailsValidator.isValid(items, constraintValidatorContext);

        // Assert the validation result
        assertFalse(result);

        // Verify that the correct violation message is added
        verify(constraintValidatorContext, times(1))
                .buildConstraintViolationWithTemplate("Item quantity is mandatory and must be greater than 0");
    }

    // Test case for invalid rate
    @Test
    public void testIsValid_InvalidRate() {

        // Create a list of items with invalid rate
        List<ItemDetailsModel> items = new ArrayList<>();
        items.add(new ItemDetailsModel("Item 3", 2, new BigDecimal("0.00"), new BigDecimal("200.00")));

        // Validate the items
        boolean result = itemDetailsValidator.isValid(items, constraintValidatorContext);

        // Assert the validation result
        assertFalse(result);

        // Verify that the correct violation message is added
        verify(constraintValidatorContext, times(1))
                .buildConstraintViolationWithTemplate("Item rate is mandatory and must be greater than 0");
    }

    // Test case for mismatched amount (amount does not match quantity * rate)
    @Test
    public void testIsValid_MismatchedAmount() {

        // Create a list of items with mismatched amount
        List<ItemDetailsModel> items = new ArrayList<>();
        items.add(new ItemDetailsModel("Item 4", 2, new BigDecimal("100.00"), new BigDecimal("150.00")));  // Mismatched amount

        // Validate the items
        boolean result = itemDetailsValidator.isValid(items, constraintValidatorContext);

        // Assert the validation result
        assertFalse(result);

        // Verify that the correct violation message is added
        verify(constraintValidatorContext, times(1))
                .buildConstraintViolationWithTemplate("Item amount must be equal to quantity multiplied by rate");
    }

    // Test case for null amount
    @Test
    public void testIsValid_NullAmount() {

        // Create a list of items with null amount
        List<ItemDetailsModel> items = new ArrayList<>();
        items.add(new ItemDetailsModel("Item 5", 2, new BigDecimal("100.00"), null));

        // Validate the items
        boolean result = itemDetailsValidator.isValid(items, constraintValidatorContext);

        // Assert the validation result
        assertFalse(result);

        // Verify that the correct violation message is added
        verify(constraintValidatorContext, times(1))
                .buildConstraintViolationWithTemplate("Item amount is mandatory and must be greater than 0");
    }

}
