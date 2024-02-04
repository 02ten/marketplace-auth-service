package com.auth.auth_service.dto;

import lombok.*;
import org.springframework.stereotype.Component;

@Data
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest {
    private String login;
    private String password;

}
