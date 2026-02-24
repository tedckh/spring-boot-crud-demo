package com.example.crudapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ApiResponse<T> {

    private int statusCode;
    private T data;
    private Object error;

    public ApiResponse(int statusCode, T data, Object error) {
        this.statusCode = statusCode;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(int statusCode, T data) {
        return new ApiResponse<>(statusCode, data, null);
    }

    public static <T> ApiResponse<T> error(int statusCode, Object error) {
        return new ApiResponse<>(statusCode, null, error);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }
}
