package com.utown.utown_backend.exception;

import com.utown.utown_backend.dto.response.ErrorResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            CartEmptyException.class,
            CartAlreadyExistsException.class,
            InvalidRestaurantStatusException.class,
            RestaurantClosedException.class,
            DishNotAvailableException.class,
            DishRestaurantMismatchException.class,
            UserAddressMismatchException.class,
            InvalidOrderStatusException.class,
    })
    public ResponseEntity<ErrorResponseDTO> handleBadRequestExceptions(
            RuntimeException ex,
            HttpServletRequest request) {

        log.warn("Bad request: path={}, message={}",
                request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                buildError("BAD_REQUEST", ex.getMessage(), request)
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex,
            HttpServletRequest request) {

        log.warn("Email already exists: path={}, message={}",
                request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                buildError("EMAIL_ALREADY_EXISTS", ex.getMessage(), request)
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Entity not found: path={}, message={}",
                request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildError("NOT_FOUND", ex.getMessage(), request)
        );

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation error");

        log.warn("Validation failed: path={}, message={}",
                request.getRequestURI(), message);

        return ResponseEntity.badRequest().body(
                buildError("VALIDATION_ERROR", message, request)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleJsonError(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("Malformed JSON: path={}",
                request.getRequestURI(), ex);

        return ResponseEntity.badRequest().body(
                buildError("INVALID_JSON", "Malformed JSON request", request)
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.error("Database constraint violation: path={}",
                request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                buildError("DATA_INTEGRITY_ERROR", "Database constraint violation", request)
        );

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("Access denied: path={}, message={}",
                request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                buildError("ACCESS_DENIED", "Access denied", request)
        );

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error: path={}", request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                buildError("INTERNAL_ERROR", "Internal server error", request)
        );

    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request) {

        log.warn("INVALID_CREDENTIALS: path={}", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                buildError("INVALID_CREDENTIALS", ex.getMessage(), request)
        );
    }

    private ErrorResponseDTO buildError(String code, String message, HttpServletRequest request) {
        return new ErrorResponseDTO(
                code,
                message,
                request.getRequestURI(),
                LocalDateTime.now()
        );
    }
}