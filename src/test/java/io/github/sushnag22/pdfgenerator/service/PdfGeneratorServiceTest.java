package io.github.sushnag22.pdfgenerator.service;

import io.github.sushnag22.pdfgenerator.model.PdfDataModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PdfGeneratorServiceTest {

    // Inject the mocks
    @InjectMocks
    private PdfGeneratorService pdfGeneratorService;

    // Mock the PDF data model
    @Mock
    private PdfDataModel pdfDataModel;

    // Setup method to initialize the mocks and set the PDF directory, item quantity unit, and currency format
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(pdfGeneratorService, "PDF_DIRECTORY", "/tmp/pdf");
        ReflectionTestUtils.setField(pdfGeneratorService, "ITEM_QUANTITY_UNIT", "Nos");
        ReflectionTestUtils.setField(pdfGeneratorService, "CURRENCY_FORMAT", "INR");
    }

    // Test the createPdfDirectory method
    @Test
    public void testCreatePdfDirectory_Success() {

        // Path to the PDF directory
        Path directory = Paths.get("/tmp/pdf");

        // Ensure directory does not exist before the test
        if (Files.exists(directory)) {
            directory.toFile().delete();
        }

        // Create the PDF directory
        pdfGeneratorService.createPdfDirectory();

        // Assert the directory exists
        assertTrue(Files.exists(directory));

        // Cleanup
        directory.toFile().delete();
    }

    // Test the generateFileName method
    @Test
    public void testGenerateFileName() {

        // Mock seller and buyer names
        when(pdfDataModel.getSellerName()).thenReturn("Seller Company");
        when(pdfDataModel.getBuyerName()).thenReturn("Buyer Company");

        // Generate the file name
        String fileName = pdfGeneratorService.generateFileName(pdfDataModel);

        // Assert the file name is not null and has the expected format
        assertNotNull(fileName);
        assertTrue(fileName.startsWith("Seller_Company_Buyer_Company_"));
        assertTrue(fileName.endsWith(".pdf"));
    }

    // Test the hashPdfData method
    @Test
    public void testHashPdfData() {

        // Mock the toString method of the PdfDataModel
        when(pdfDataModel.toString()).thenReturn("sample data");

        // Generate the hash
        String hash = pdfGeneratorService.hashPdfData(pdfDataModel);

        // Assert the hash is not null or empty
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }
}
