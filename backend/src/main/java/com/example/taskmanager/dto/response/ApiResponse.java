package com.example.taskmanager.dto.response;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;

public class ApiResponse<T> {
    private final int status;
    private final boolean ok;
    private final List<ApiError> errors;
    private final T data;

    public ApiResponse(int status, boolean ok, List<ApiError> errors, T data) {
        this.status = status;
        this.ok = ok;
        this.errors = errors;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return new ApiResponse<>(status.value(), true, new ArrayList<>(), data);
    }

    public static ApiResponse<Void> failure(HttpStatus status, String type, String message) {
        List<ApiError> errors = new ArrayList<>();
        errors.add(new ApiError(type, message));
        return new ApiResponse<>(status.value(), false, errors, null);
    }

    public int getStatus() {
        return status;
    }

    public boolean isOk() {
        return ok;
    }

    public List<ApiError> getErrors() {
        return errors;
    }

    public T getData() {
        return data;
    }
}
