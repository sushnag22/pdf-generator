package io.github.sushnag22.pdfgenerator.controller;

import io.github.sushnag22.pdfgenerator.model.PdfDataModel;
import io.github.sushnag22.pdfgenerator.service.PdfGeneratorService;

import io.github.sushnag22.pdfgenerator.util.StringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/v1/pdf")
public class PdfGeneratorController {

    // Logger to log the events
    private static final Logger logger = LoggerFactory.getLogger(PdfGeneratorController.class);

    // Directory where all the PDF files will be stored
    @Value("${pdf.storage.path}")
    private String PDF_DIRECTORY;

    // Service to generate and store the PDF
    private final PdfGeneratorService pdfGeneratorService;

    // Service to format the field names
    private final StringUtil stringUtil;

    // Constructor based dependency injection
    @Autowired
    public PdfGeneratorController(PdfGeneratorService pdfGeneratorService, StringUtil stringUtil) {

        // Initialize the services
        this.pdfGeneratorService = pdfGeneratorService;
        this.stringUtil = stringUtil;

        // Create the PDF directory (if it does not exist) when the controller is initialized
        pdfGeneratorService.createPdfDirectory();
    }

    // API to generate and store the PDF
    @Operation(summary = "Generate and store a PDF file",
            description = "Generates a PDF file based on the provided data and stores it on the server.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF generated and stored successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/generate-and-store")
    public ResponseEntity<Map<String, Object>> generateAndStorePdf(@Valid @RequestBody PdfDataModel pdfDataModel, BindingResult bindingResult) {
        try {

            // Check if there are validation errors in the PDF data
            if (bindingResult.hasErrors()) {

                // Extract the field names with validation errors
                String errorFields = stringUtil.getFormattedErrorMessage(bindingResult.getFieldErrors());

                // Log the validation errors in the PDF data
                logger.error("Validation errors in the PDF data: {}", errorFields);

                // Return a bad request response with the formatted error messages
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "Failure",
                        "statusCode", 400,
                        "message", errorFields
                ));
            }

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

                    // Log if the PDF is generated and stored successfully
                    logger.info("PDF generated and saved: {}", filePath.toAbsolutePath());
                } else {

                    // Log if the PDF file already exists
                    logger.info("PDF already exists: {}", filePath.toAbsolutePath());
                }

                // Return the file name
                return ResponseEntity.ok(Map.of(
                        "status", "Success",
                        "statusCode", 200,
                        "message", "PDF generated and stored successfully",
                        "fileName", fileName
                ));
            } else {

                // Log if the hash generation fails
                logger.error("Error generating hash for PDF data");

                // Return an internal server error response
                return ResponseEntity.internalServerError().body(Map.of(
                        "status", "Error",
                        "statusCode", 500,
                        "message", "Error generating hash for PDF data"
                ));
            }
        } catch (Exception exception) {

            // Log if an error occurs while generating and storing the PDF
            logger.error("Error generating and storing PDF", exception);

            // Return an internal server error response
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "Error",
                    "statusCode", 500,
                    "message", "Error generating and storing PDF"
            ));
        }
    }

    // API to download the PDF
    @Operation(summary = "Download a PDF file",
            description = "Downloads a specified PDF file from the server.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF file downloaded successfully",
                    content = @Content(mediaType = "application/pdf",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "PDF file not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

            // Return an internal server error response
            return ResponseEntity.status(500).body(null);
        }
    }
}
