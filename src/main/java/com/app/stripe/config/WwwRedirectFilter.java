package com.app.stripe.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
public class WwwRedirectFilter extends OncePerRequestFilter {

  /**
   * Prefijos que NUNCA deben recibir un redirect.
   * Stripe trata cualquier respuesta 3xx como fallo de entrega del webhook.
   * https://support.stripe.com/questions/webhooks-what-to-do-when-the-http-status-code-starts-with-a-three-(3xx)
   */
  private static final List<String> BYPASS_PREFIXES = List.of(
          "/webhook",
          "/actuator"
  );

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain) throws ServletException, IOException {

    String requestUri = request.getRequestURI();

    // Cortocircuito: nunca redirigir estos endpoints
    boolean isBypassUri = BYPASS_PREFIXES.stream().anyMatch(requestUri::startsWith);
    if (isBypassUri) {
      filterChain.doFilter(request, response);
      return;
    }

    String host = request.getHeader("Host");
    if (host != null && host.startsWith("www.")) {
      String nonWwwHost = host.substring(4);
      String queryString = request.getQueryString();

      String redirectUrl = request.getScheme()
              + "://"
              + nonWwwHost
              + requestUri
              + (queryString != null ? "?" + queryString : "");

      log.info("WWW redirect: {} → {}", host + requestUri, redirectUrl);
      response.setHeader("Location", redirectUrl);
      response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
      return;
    }

    filterChain.doFilter(request, response);
  }
}