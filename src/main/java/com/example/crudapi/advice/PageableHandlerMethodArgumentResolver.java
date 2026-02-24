package com.example.crudapi.advice;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class PageableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Pageable.class);
    }

    @Override
    public Pageable resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        String offsetString = webRequest.getParameter("offset");
        String limitString = webRequest.getParameter("limit");
        String sortBy = webRequest.getParameter("sortBy");

        int offset = (offsetString != null && !offsetString.isEmpty()) ? Integer.parseInt(offsetString) : 0;
        int limit = (limitString != null && !limitString.isEmpty()) ? Integer.parseInt(limitString) : 20;

        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            String property = sortBy;
            Sort.Direction direction = Sort.Direction.ASC;
            if (sortBy.startsWith("-")) {
                property = sortBy.substring(1);
                direction = Sort.Direction.DESC;
            }
            sort = Sort.by(direction, property);
        } else {
            sort = Sort.by(Sort.Direction.ASC, "createdDate");
        }

        if (limit <= 0) {
            limit = 20;
        }

        int page = offset / limit;
        return PageRequest.of(page, limit, sort);
    }
}
