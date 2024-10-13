package io.github.sushnag22.pdfgenerator.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode

// Data model for the item details in the PDF
public class ItemDetailsModel {

    // Attributes of the item details model
    @NotBlank(message = "Item name is mandatory")
    @Size(min = 3, max = 50, message = "Item name must be between 3 and 50 characters")
    private String name;

    private Integer quantity;

    private BigDecimal rate;

    private BigDecimal amount;
}
