package com.auth.auth_service.controller;

import com.auth.auth_service.model.User;
import com.auth.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        try{
            User user = userService.getUserById(id).orElseThrow(()->new RuntimeException("User not found"));
            user.setPassword(null);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch (RuntimeException ex){
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
