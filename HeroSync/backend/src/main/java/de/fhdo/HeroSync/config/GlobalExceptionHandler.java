package de.fhdo.HeroSync.config;

import de.fhdo.HeroSync.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

/**
 * Centralised exception handler for all REST controllers.
 *
 * <p>Maps common Spring exceptions to structured {@link ApiError} responses so
 * the frontend always receives a consistent error envelope regardless of where
 * the error originated.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles Bean Validation failures triggered by {@code @Valid} on request
   * bodies. Extracts the first field-level violation message for the response.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                   HttpServletRequest req) {
    String msg = ex.getBindingResult().getFieldErrors().stream()
      .findFirst().map(e -> e.getField() + ": " + e.getDefaultMessage())
      .orElse("Validation failed");
    return ResponseEntity.badRequest().body(
      new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", msg, req.getRequestURI())
    );
  }

  /**
   * Handles binding failures and constraint violations not caught by
   * {@link MethodArgumentNotValidException} (e.g. path variable constraints).
   */
  @ExceptionHandler({ BindException.class, ConstraintViolationException.class })
  public ResponseEntity<ApiError> handleBind(Exception ex, HttpServletRequest req) {
    return ResponseEntity.badRequest().body(
      new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), req.getRequestURI())
    );
  }

  /**
   * Handles {@link ResponseStatusException} thrown from service and controller
   * layers (e.g. 401, 404, 409). The HTTP status and reason phrase from the
   * exception are forwarded directly to the client.
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex) {
    ApiError error = new ApiError();
    error.setStatus(ex.getStatusCode().value());
    error.setError(ex.getStatusCode().toString());
    error.setMessage(ex.getReason());
    return ResponseEntity.status(ex.getStatusCode()).body(error);
  }

  /**
   * Catch-all handler for any unhandled exception.
   * Returns 500 Internal Server Error with the exception message.
   * In production consider hiding the raw message behind a generic phrase.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", ex.getMessage(), req.getRequestURI())
    );
  }
}
