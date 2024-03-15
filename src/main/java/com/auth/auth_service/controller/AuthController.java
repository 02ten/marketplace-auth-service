package com.auth.auth_service.controller;

import com.auth.auth_service.dto.JwtRequest;
import com.auth.auth_service.dto.JwtResponse;
import com.auth.auth_service.dto.RefreshJwtRequest;
import com.auth.auth_service.dto.RegisterDTO;
import com.auth.auth_service.service.AuthService;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Log4j
public class AuthController{
    private final AuthService authService;
    @Autowired
    private Tracer tracer;

    @PostMapping("login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) throws AuthException {
        final JwtResponse token = authService.login(authRequest);
        Span span = tracer.buildSpan("login").start();
        Tags.HTTP_METHOD.set(span, "POST");
        Tags.HTTP_URL.set(span,"api/auth/login");
        span.finish();
        return ResponseEntity.ok(token);
    }
    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO user) throws AuthException {
        String status = authService.register(user);
        return ResponseEntity.ok(status);
    }
    @PostMapping("token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) throws AuthException {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) throws AuthException {
        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

}
