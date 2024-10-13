package io.github.sushnag22.pdfgenerator.model;

import io.github.sushnag22.pdfgenerator.validation.ItemDetailsValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode

// Data model for the PDF
public class PdfDataModel {

    // Attributes of the PDF data model with validation constraints
    @NotBlank(message = "Seller name is mandatory")
    @Size(min = 3, max = 50, message = "Seller name must be between 3 and 50 characters")
    private String sellerName;

    @NotBlank(message = "Seller address is mandatory")
    @Size(min = 3, max = 100, message = "Seller address must be between 3 and 100 characters")
    private String sellerAddress;

    @NotBlank(message = "Seller GSTIN is mandatory")
    @Size(min = 15, max = 15, message = "Seller GSTIN must be 15 characters")
    private String sellerGstin;

    @NotBlank(message = "Buyer name is mandatory")
    @Size(min = 3, max = 50, message = "Buyer name must be between 3 and 50 characters")
    private String buyerName;

    @NotBlank(message = "Buyer address is mandatory")
    @Size(min = 3, max = 100, message = "Buyer address must be between 3 and 100 characters")
    private String buyerAddress;

    @NotBlank(message = "Buyer GSTIN is mandatory")
    @Size(min = 15, max = 15, message = "Buyer GSTIN must be 15 characters")
    private String buyerGstin;

    @NotEmpty(message = "Items are mandatory")
    @ItemDetailsValidation
    private List<Map<String, Object>> items;
}
