package io.github.sushnag22.pdfgenerator.service;

import io.github.sushnag22.pdfgenerator.model.PdfDataModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class PdfGeneratorServiceTest {

    // Inject the PDF generator service to be tested
    @InjectMocks
    private PdfGeneratorService pdfGeneratorService;

    // Mock the PDF data model to be used in the tests
    @Mock
    private PdfDataModel pdfDataModel;

    // Setup method to initialize the mocks
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(pdfGeneratorService, "PDF_DIRECTORY", "/tmp/pdf");
    }

    // Test the generateFileName method
    @Test
    public void testGenerateFileName() {

        // Mock the seller and buyer names
        when(pdfDataModel.getSellerName()).thenReturn("Seller Company");
        when(pdfDataModel.getBuyerName()).thenReturn("Buyer Company");

        // Generate the file name
        String fileName = pdfGeneratorService.generateFileName(pdfDataModel);

        // Assert that the file name is not null and has the expected format
        assertNotNull(fileName);
        assertTrue(fileName.startsWith("Seller_Company_Buyer_Company_"));
        assertTrue(fileName.endsWith(".pdf"));
    }

}
