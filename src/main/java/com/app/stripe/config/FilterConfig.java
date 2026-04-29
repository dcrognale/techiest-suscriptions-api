package com.app.stripe.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

  @Bean
  public FilterRegistrationBean<WwwRedirectFilter> wwwRedirectFilter() {
    FilterRegistrationBean<WwwRedirectFilter> registration =
            new FilterRegistrationBean<>(new WwwRedirectFilter());
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
    registration.addUrlPatterns("/*");
    return registration;
  }
}