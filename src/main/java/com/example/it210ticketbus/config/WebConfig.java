// === FILE: com/example/it210ticketbus/config/WebConfig.java ===
package com.example.it210ticketbus.config;

import com.example.it210ticketbus.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/auth/**", 
                    "/css/**", 
                    "/js/**",
                    "/images/**", 
                    "/", 
                    "/search", 
                    "/seat-map",
                    "/api/**",
                    "/webjars/**",
                    "/favicon.ico"
                );
    }
}
