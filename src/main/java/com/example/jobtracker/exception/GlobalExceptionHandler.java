package com.example.jobtracker.exception;

import com.example.jobtracker.dto.ErrorResponseDTO;
import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(
            BadRequestException ex,
            WebRequest request
    ){
      var response = new ErrorResponseDTO(
              HttpStatus.BAD_REQUEST.value(),
              "BAD_REQUEST",
              ex.getMessage(),
              request.getDescription(false).replace("uri=", ""),
              LocalDateTime.now(),
              null
      );

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(
            NotFoundException ex,
            WebRequest request
    ){
        var response = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleServerErrors(
            Exception ex,
            WebRequest request
    ){
        var response = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVICE_ERROR",
                "Something went wrong",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                null
        );

//        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex,
                                                                   WebRequest request) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (msg1, msg2) -> msg1 // if duplicate keys
                ));

        var response = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Validation failed",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponseDTO> handleOptimisticLock(
            OptimisticLockException ex,
            WebRequest request) {

        var response = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                "CONCURRENT_MODIFICATION",
                "The record was updated by another user. Please refresh and retry.",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }


}
