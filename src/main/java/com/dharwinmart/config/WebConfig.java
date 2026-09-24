package com.dharwinmart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                        "/admin", "/admin/**",
                        "/seller", "/seller/**",
                        "/buyer", "/buyer/**",
                        "/wishlist", "/wishlist/**"
                )
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/favicon.ico");
    }
}
