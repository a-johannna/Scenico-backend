package com.example.demo1.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("upload-photo");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/upload-photo/**")
                .addResourceLocations("file:" + uploadPath + "/");

    }
}
