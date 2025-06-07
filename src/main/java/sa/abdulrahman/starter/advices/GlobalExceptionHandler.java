package sa.abdulrahman.starter.advices;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sa.abdulrahman.starter.exceptions.*;

import java.util.HashMap;
import java.util.Map;

import static sa.abdulrahman.starter.utils.TimeUtil.getCurrentTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatusCode status, String message, Object errors) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", getCurrentTime());
        response.put("errors", errors);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatusCode status, String message) {
        return buildResponse(status, message, null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException exception) {
        log.error("Resource not found: {}", exception.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<Map<String, Object>> handleTakenException(UsernameAlreadyTakenException exception) {
        log.error("Already Taken: {}", exception.getMessage());
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({InvalidPasswordException.class, InvalidOTPException.class, InvalidTokenException.class})
    public ResponseEntity<Map<String, Object>> handleForbiddenExceptions(Exception exception) {
        log.warn("Access denied: {}", exception.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN,  exception.getMessage());
    }

    @ExceptionHandler({AuthenticationException.class, UnauthorizedException.class})
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(Exception exception) {
        log.warn("Authentication failed: {}", exception.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return buildResponse(HttpStatus.valueOf(422), "Invalid request parameters", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("Constraint violation: {}", e.getMessage());
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return buildResponse(HttpStatus.valueOf(422), "Invalid request parameters", errors);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, JsonParseException.class})
    public ResponseEntity<Map<String, Object>> handleJsonParsingError(Exception e) {
        log.warn("JSON parsing error: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed or unreadable JSON");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleInternalServerErrorException(Exception exception) {
        log.error("Internal server error: {}", exception.getMessage(), exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }
}
