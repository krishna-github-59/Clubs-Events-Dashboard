package com.club.events_dashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{

    // // ---------- CORS ----------
    // // CORS
    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    //     registry.addMapping("/**")
    //             .allowedOrigins("http://localhost:3000")
    //             .allowedMethods("GET", "POST", "PUT", "DELETE")
    //             .allowedHeaders("*")
    //             .allowCredentials(true);
    // }

    // // ---------- Serve Uploaded Files ----------
    // @Override
    // public void addResourceHandlers(ResourceHandlerRegistry registry) {

    //     // Maps URL:  http://localhost:8080/uploads/...  
    //     // To Folder: uploads/  in your project root
    //     registry.addResourceHandler("/uploads/**")
    //             .addResourceLocations("file:uploads/");
    // }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
