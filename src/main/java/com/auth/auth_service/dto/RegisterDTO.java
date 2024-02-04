package com.auth.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
}
