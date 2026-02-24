package com.example.crudapi.advice;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.crudapi.controller.TaskController;
import com.example.crudapi.dto.ApiResponse;
import com.example.crudapi.dto.PaginatedData;

@RestControllerAdvice(basePackageClasses = TaskController.class)
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {

        int statusCode = ((ServletServerHttpResponse) response).getServletResponse().getStatus();

        if (body instanceof ApiResponse) {
            response.setStatusCode(
                    org.springframework.http.HttpStatus.valueOf(((ApiResponse<?>) body).getStatusCode()));
            return body;
        }

        if (statusCode == 204) {
            return null;
        }

        if (body instanceof Page) {
            PaginatedData<?> paginatedData = new PaginatedData<>((Page<?>) body);
            return ApiResponse.success(statusCode, paginatedData);
        }

        return ApiResponse.success(statusCode, body);
    }
}
