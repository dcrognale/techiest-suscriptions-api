package com.app.stripe.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

  @Bean
  public FilterRegistrationBean<WwwRedirectFilter> wwwRedirectFilter(WwwRedirectFilter filter) {
    FilterRegistrationBean<WwwRedirectFilter> registration = new FilterRegistrationBean<>(filter);
    // Orden más alto = se ejecuta primero, antes que cualquier otro filtro
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
    registration.addUrlPatterns("/*");
    return registration;
  }
}