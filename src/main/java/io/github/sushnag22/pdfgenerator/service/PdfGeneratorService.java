package io.github.sushnag22.pdfgenerator.service;

import io.github.sushnag22.pdfgenerator.model.ItemDetailsModel;
import io.github.sushnag22.pdfgenerator.model.PdfDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Value("${pdf.storage.path}")
    private String PDF_DIRECTORY;

    @Value("${item.quantity.unit}")
    private String ITEM_QUANTITY_UNIT;

    @Value("${currency.format}")
    private String CURRENCY_FORMAT;

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

    // Method to generate a unique file name for the PDF
    public String generateFileName(PdfDataModel pdfDataModel) {
        try {
            // Create a descriptive part of the file name (e.g., based on seller and buyer)
            String sellerName = pdfDataModel.getSellerName().replaceAll("[^a-zA-Z0-9]", "_");
            String buyerName = pdfDataModel.getBuyerName().replaceAll("[^a-zA-Z0-9]", "_");

            // Limit the length of the seller and buyer name to avoid excessively long file names
            sellerName = sellerName.length() > 20 ? sellerName.substring(0, 20) : sellerName;
            buyerName = buyerName.length() > 20 ? buyerName.substring(0, 20) : buyerName;

            // Generate the hash for the PDF data
            String dataHash = hashPdfData(pdfDataModel);

            // Combine the descriptive part with the hash
            String fileName = sellerName + "_" + buyerName + "_" + dataHash + ".pdf";

            // Return the sanitized file name
            return fileName.replaceAll("[/\\\\:*?\"<>|]", "_");
        } catch (Exception exception) {
            logger.error("Error while generating file name", exception);
            return "";
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

            // Base64 encode the hash and sanitize it for file storage
            String base64Hash = Base64.getEncoder().encodeToString(hash);

            // Replace problematic characters for file paths
            return base64Hash.replace("/", "_").replace("\\", "_").replace("=", "");
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

            for (ItemDetailsModel itemDetailsModel : pdfDataModel.getItems()) {
                BigDecimal rate = new BigDecimal(itemDetailsModel.getRate().toString());
                BigDecimal amount = new BigDecimal(itemDetailsModel.getAmount().toString());

                // Format rate and amount to 2 decimal places
                itemDetailsModel.setRate(rate.setScale(2, RoundingMode.HALF_UP));
                itemDetailsModel.setAmount(amount.setScale(2, RoundingMode.HALF_UP));
            }

            context.setVariable("itemQuantityUnit", ITEM_QUANTITY_UNIT);
            context.setVariable("currencySymbol", CURRENCY_FORMAT);

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
