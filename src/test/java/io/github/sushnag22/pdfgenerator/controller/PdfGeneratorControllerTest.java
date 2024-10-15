package io.github.sushnag22.pdfgenerator.controller;

import io.github.sushnag22.pdfgenerator.model.PdfDataModel;
import io.github.sushnag22.pdfgenerator.service.PdfGeneratorService;
import io.github.sushnag22.pdfgenerator.util.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PdfGeneratorControllerTest {

    // Inject the mocks
    @InjectMocks
    private PdfGeneratorController pdfGeneratorController;

    // Mock the PDF generator service
    @Mock
    private PdfGeneratorService pdfGeneratorService;

    // Mock the string utility
    @Mock
    private StringUtil stringUtil;

    // Mock the binding result
    @Mock
    private BindingResult bindingResult;

    // Mock the PDF data model
    @Mock
    private PdfDataModel pdfDataModel;

    // Setup method to initialize the mocks and set the PDF directory
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(pdfGeneratorController, "PDF_DIRECTORY", "/tmp/pdf");
    }

    // Test the `generateAndStorePdf` method with validation errors
    @Test
    public void testGenerateAndStorePdf_ValidationError() {

        // Mock validation errors
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("pdfDataModel", "sellerName", "Seller name is required")));
        when(stringUtil.getFormattedErrorMessage(anyList())).thenReturn("Seller name is required");

        ResponseEntity<Map<String, Object>> response = pdfGeneratorController.generateAndStorePdf(pdfDataModel, bindingResult);

        // Assert response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Failure", Objects.requireNonNull(response.getBody()).get("status"));
        assertEquals(400, response.getBody().get("statusCode"));
        assertEquals("Seller name is required", response.getBody().get("message"));
    }

    // Test the `downloadPdf` method for file not found error
    @Test
    public void testDownloadPdf_FileNotFound() {
        ResponseEntity<?> response = pdfGeneratorController.downloadPdf("nonExistingFile.pdf");

        // Assert response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
