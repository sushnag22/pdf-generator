package io.github.sushnag22.pdfgenerator.service;

import io.github.sushnag22.pdfgenerator.model.PdfDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class PdfGeneratorService {

    // Logger to log the events
    Logger logger = LoggerFactory.getLogger(PdfGeneratorService.class);

    // Directory where all the PDF files will be stored
    private static final String PDF_DIRECTORY = "pdfs/";

    // Method to create the PDF directory
    public void createPdfDirectory() {
        try {
            // Path to the PDF directory
            Path directory = Paths.get(PDF_DIRECTORY);

            // Create the PDF directory if it does not exist
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
                logger.info("PDF directory created successfully");
            }
        } catch (Exception exception) {
            // Log the error if the PDF directory creation fails
            logger.error("Failed to create PDF directory", exception);
        }
    }

    // Method to hash the PDF data
    public String hashPdfData(PdfDataModel pdfDataModel) {
        try {
            // Convert the PDF data to a string
            String dataString = pdfDataModel.toString();

            // Hash the PDF data using SHA-256 algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Generate the hash for the PDF data
            byte[] hash = digest.digest(dataString.getBytes());

            // Return the Base64 encoded hash
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception exception) {
            // Log the error if hashing fails
            logger.error("Error while hashing PDF data", exception);
            return "";
        }
    }

    // Method to generate the PDF from the HTML template
    public ByteArrayOutputStream generatePdfFromHtml(PdfDataModel pdfDataModel) {
        try {
            // Use Thymeleaf template engine to process the HTML template
            ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
            classLoaderTemplateResolver.setSuffix(".html");
            classLoaderTemplateResolver.setTemplateMode(TemplateMode.HTML);
            TemplateEngine templateEngine = new SpringTemplateEngine();
            templateEngine.setTemplateResolver(classLoaderTemplateResolver);

            // Set the variables in the HTML template using the PDF data model
            Context context = new Context();
            context.setVariable("sellerName", pdfDataModel.getSellerName());
            context.setVariable("sellerAddress", pdfDataModel.getSellerAddress());
            context.setVariable("sellerGstin", pdfDataModel.getSellerGstin());
            context.setVariable("buyerName", pdfDataModel.getBuyerName());
            context.setVariable("buyerAddress", pdfDataModel.getBuyerAddress());
            context.setVariable("buyerGstin", pdfDataModel.getBuyerGstin());
            context.setVariable("items", pdfDataModel.getItems());

            // Generate the PDF from the HTML template
            ITextRenderer iTextRenderer = new ITextRenderer();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            iTextRenderer.setDocumentFromString(templateEngine.process("pdf_template", context));
            iTextRenderer.layout();
            iTextRenderer.createPDF(byteArrayOutputStream, false);
            iTextRenderer.finishPDF();

            // Return the PDF as a byte array
            return byteArrayOutputStream;
        } catch (Exception exception) {
            // Log the error if PDF generation fails
            logger.error("Error while generating PDF from HTML", exception);
            return new ByteArrayOutputStream();
        }
    }
}
