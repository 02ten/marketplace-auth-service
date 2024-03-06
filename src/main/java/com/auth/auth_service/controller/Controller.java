package com.auth.auth_service.controller;

import com.auth.auth_service.jwt.JwtAuthentication;
import com.auth.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class Controller {
    private final AuthService authService;

    @GetMapping("hello/user")
    public ResponseEntity<JwtAuthentication> helloUser(){
        final JwtAuthentication authInfo = authService.getAuthInfo();
        System.out.println("Here");
        return ResponseEntity.ok(authInfo);
    }
    @GetMapping("hello/admin")
    public ResponseEntity<String> helloAdmin(){
        final JwtAuthentication authInfo = authService.getAuthInfo();
        return ResponseEntity.ok("Hello admin" + authInfo.getName());
    }
}
