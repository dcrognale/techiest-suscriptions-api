package com.app.stripe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    // Spring Boot 3.x removed trailing slash redirect by default.
    // This ensures /webhook/ and /webhook both resolve correctly.
    configurer.setUseTrailingSlashMatch(true);
  }
}