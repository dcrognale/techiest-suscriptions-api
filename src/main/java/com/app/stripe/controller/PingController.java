package com.app.stripe.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/webhook")
public class PingController {

  @PostMapping("/ping")
  public ResponseEntity<String> ping() {
    log.info("Ping received - info");
    log.error("Ping received - error");
    log.warn("Ping received - warn");
    return ResponseEntity.ok("Pong");
  }
}
