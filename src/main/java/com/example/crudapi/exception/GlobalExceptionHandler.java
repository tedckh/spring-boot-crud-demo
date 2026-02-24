package com.example.crudapi.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.crudapi.dto.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Object> apiResponse = ApiResponse.error(status.value(), errors);
        return new ResponseEntity<>(apiResponse, status);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", ex.getReason());

        ApiResponse<Object> apiResponse = ApiResponse.error(ex.getStatusCode().value(), errorDetails);
        return new ResponseEntity<>(apiResponse, ex.getStatusCode());
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<ApiResponse<Object>> handleAll(Exception ex, WebRequest request) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", "An unexpected error occurred");
        errorDetails.put("detail", ex.getLocalizedMessage());

        ApiResponse<Object> apiResponse = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
