package io.github.sushnag22.pdfgenerator.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomValidationException {

    // Logger to log the events
    Logger logger = LoggerFactory.getLogger(CustomValidationException.class);

    @ExceptionHandler(ConstraintViolationException.class)

    // Method to handle the constraint violation exception
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {

        // Extract validation error messages
        String errorMessages = constraintViolationException.getConstraintViolations().stream()
                .sorted(Comparator.comparing(violation -> violation.getPropertyPath().toString()))
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(". "));

        // Log the error message
        logger.error("Validation errors: {}", errorMessages);

        // Return the error response as a bad request with a detailed message
        return ResponseEntity.badRequest().body(Map.of(
                "status", "Failure",
                "statusCode", 400,
                "message", errorMessages
        ));
    }
}
