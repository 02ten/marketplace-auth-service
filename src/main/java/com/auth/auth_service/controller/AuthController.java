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
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> login(@RequestBody JwtRequest authRequest) throws AuthException {
        Span span = tracer.buildSpan("login").start();
        Tags.HTTP_METHOD.set(span, "POST");
        Tags.HTTP_URL.set(span,"api/auth/login");
        span.finish();
        try{
            final JwtResponse token = authService.login(authRequest);
            return new ResponseEntity<>(token, HttpStatus.OK);
        }catch (AuthException exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO user){
        Span span = tracer.buildSpan("register").start();
        Tags.HTTP_METHOD.set(span, "POST");
        Tags.HTTP_URL.set(span,"api/auth/register");
        log.info("Registration new user");
        span.finish();
        try{
            authService.register(user);
            log.info("Registration successful");
            return new ResponseEntity<>("Регистрация успешна", HttpStatus.OK);
        }catch (AuthException authException){
            log.error("Email already taken");
            return new ResponseEntity<>(authException.getMessage(), HttpStatus.BAD_REQUEST);
        }
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
