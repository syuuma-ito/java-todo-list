package com.example.taskmanager.controller;

import com.example.taskmanager.dto.response.ApiResponse;
import com.example.taskmanager.exception.InvalidTaskException;
import com.example.taskmanager.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(InvalidTaskException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTask(InvalidTaskException exception) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "ValidationError", exception.getMessage());
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequest(Exception exception) {
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "ValidationError",
                "リクエストの形式または値が不正です");
    }

    @ExceptionHandler({
            NotFoundException.class,
            NoResourceFoundException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleNotFound(Exception exception) {
        String message = "指定されたリソースが見つかりません";
        if (exception instanceof NotFoundException) {
            message = exception.getMessage();
        }
        return createErrorResponse(HttpStatus.NOT_FOUND, "NotFoundError", message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedError(Exception exception) {
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "InternalServerError",
                "サーバー内部でエラーが発生しました");
    }

    private ResponseEntity<ApiResponse<Void>> createErrorResponse(
            HttpStatus status, String type, String message) {
        return ResponseEntity.status(status)
                .body(ApiResponse.failure(status, type, message));
    }
}
