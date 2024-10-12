package io.github.sushnag22.pdfgenerator.controller;

import io.github.sushnag22.pdfgenerator.model.PdfDataModel;
import io.github.sushnag22.pdfgenerator.service.PdfGeneratorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/pdf")
public class PdfGeneratorController {

    // Logger to log the events
    private static final Logger logger = LoggerFactory.getLogger(PdfGeneratorController.class);

    // Directory where all the PDF files will be stored
    private static final String PDF_DIRECTORY = "pdfs/";

    // Service to generate and store the PDF
    private final PdfGeneratorService pdfGeneratorService;

    // Constructor based dependency injection
    @Autowired
    public PdfGeneratorController(PdfGeneratorService pdfGeneratorService) {
        // Service initialization
        this.pdfGeneratorService = pdfGeneratorService;

        // Create the PDF directory (if it does not exist) when the controller is initialized
        pdfGeneratorService.createPdfDirectory();
    }

    // API to generate and store the PDF
    @PostMapping("/generate-and-store")
    public ResponseEntity<String> generateAndStorePdf(@RequestBody PdfDataModel pdfDataModel) {
        try {
            // Generate the unique name for the PDF file based on the data
            String fileName = pdfGeneratorService.generateFileName(pdfDataModel);

            if (!fileName.isEmpty()) {
                // Path to store the PDF file
                Path filePath = Paths.get(PDF_DIRECTORY, fileName);

                // Check if the PDF file already exists
                if (!Files.exists(filePath)) {
                    // Generate the PDF from the HTML template
                    byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml(pdfDataModel).toByteArray();

                    // Write the PDF to the file
                    Files.write(filePath, pdfBytes);
                    logger.info("PDF generated and saved: {}", filePath.toAbsolutePath());
                } else {
                    // Log if the PDF file already exists
                    logger.info("PDF already exists: {}", filePath.toAbsolutePath());
                }
                // Return the file name
                return ResponseEntity.ok(fileName);
            } else {
                // Log if the hash generation fails
                return ResponseEntity.internalServerError().body("Error generating hash for PDF data");
            }
        } catch (Exception exception) {
            // Log if an error occurs while generating and storing the PDF
            logger.error("Error generating and storing PDF", exception);
            return ResponseEntity.internalServerError().body("Error generating and storing PDF");
        }
    }

    // API to download the PDF
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable String fileName) {
        try {
            // Get the path to the PDF file
            Path file = Paths.get(PDF_DIRECTORY, fileName);

            // Get the resource for the PDF file
            Resource resource = new UrlResource(file.toUri());

            // Check if the resource exists and is readable
            if (resource.exists() || resource.isReadable()) {
                // Return the resource as a response
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                // Return HTTP 404 (Not Found) if the resource does not exist
                return ResponseEntity.notFound().build();
            }
        } catch (Exception exception) {
            // Log if an error occurs while downloading the PDF
            logger.error("Error occurred while downloading the PDF file: {}", fileName, exception);
            return ResponseEntity.status(500).body(null);
        }
    }
}
