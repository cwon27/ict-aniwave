package com.ict.finalproject.config;

import com.ict.finalproject.JWT.JWTInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private GlobalModelInterceptor globalModelInterceptor;
    private final JWTInterceptor jwtInterceptor;

    // JwtInterceptor를 생성자 주입 방식으로 주입받음
    @Autowired
    public WebConfig(JWTInterceptor jwtInterceptor, GlobalModelInterceptor globalModelInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
        this.globalModelInterceptor = globalModelInterceptor;
    }

  /*  @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JwtInterceptor 등록
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/master/**");  // /master/** 경로에 대해 인터셉터 적용
    }*/

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:9911") // 허용할 출처(도메인) 지정
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .exposedHeaders("Authorization")
                .allowCredentials(true)  // 쿠키와 인증 정보를 허용
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("img/store/**")
                .addResourceLocations("file:src/main/webapp/img/store/");
        registry.addResourceHandler("/reviewFileUpload/**")
                .addResourceLocations("file:src/main/webapp/reviewFileUpload/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // "/master/**"로 시작하는 URL에만 인터셉터 적용
        registry.addInterceptor(globalModelInterceptor)
                .addPathPatterns("/master/**");
    }
}