package com.example.demo.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.advice.PageableHandlerMethodArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver;

    public WebConfig(PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver) {
        this.pageableHandlerMethodArgumentResolver = pageableHandlerMethodArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(pageableHandlerMethodArgumentResolver);
    }
}
