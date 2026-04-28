package com.app.stripe.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class WwwRedirectFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain) throws ServletException, IOException {

    String host = request.getHeader("Host");

    if (host != null && host.startsWith("www.")) {
      String nonWwwHost = host.substring(4); // elimina "www."
      String queryString = request.getQueryString();
      String requestUri = request.getRequestURI();

      String redirectUrl = request.getScheme()
              + "://"
              + nonWwwHost
              + requestUri
              + (queryString != null ? "?" + queryString : "");

      log.info("WWW redirect: {} → {}", host + requestUri, redirectUrl);
      response.setHeader("Location", redirectUrl);
      response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY); // 301
      return;
    }

    filterChain.doFilter(request, response);
  }
}