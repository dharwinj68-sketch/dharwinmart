package com.dharwinmart.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        logger.warn("Access denied: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Access Denied (403)");
        model.addAttribute("errorMessage", ex.getMessage());
        return "access-denied";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        logger.warn("Resource not found: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Item Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(InsufficientStockException.class)
    public String handleInsufficientStock(InsufficientStockException ex, Model model) {
        logger.warn("Insufficient stock: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Inventory Notice");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        logger.warn("Invalid input argument: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Invalid Request");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound(NoResourceFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Page Not Found (404)");
        model.addAttribute("errorMessage", "The page or resource you requested could not be found.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        logger.error("Unexpected error occurred: ", ex);
        model.addAttribute("errorTitle", "Something went wrong");
        model.addAttribute("errorMessage", "An unexpected error occurred: " + (ex.getMessage() != null ? ex.getMessage() : "Please try again later."));
        return "error";
    }
}
