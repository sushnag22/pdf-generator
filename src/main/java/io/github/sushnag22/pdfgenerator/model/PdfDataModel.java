package io.github.sushnag22.pdfgenerator.model;

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
    private String sellerName;
    private String sellerAddress;
    private String sellerGstin;
    private String buyerName;
    private String buyerAddress;
    private String buyerGstin;
    private List<Map<String, Object>> items;
}
