package com.auth.auth_service.service;

import com.auth.auth_service.dto.JwtRequest;
import com.auth.auth_service.dto.JwtResponse;
import com.auth.auth_service.dto.RegisterDTO;
import com.auth.auth_service.jwt.JwtProvider;
import com.auth.auth_service.model.Role;
import com.auth.auth_service.model.User;
import com.auth.auth_service.repository.RoleRepository;
import com.auth.auth_service.repository.UserRepository;
import jakarta.security.auth.message.AuthException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    @Test
    void login_ValidCredentials_ReturnsJwtResponse() throws AuthException {
        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);
        User user = new User(1L, "validLogin", "validPassword", "Alexander",
                "Ten", Set.of(new Role("USER")));
        JwtRequest authRequest = new JwtRequest("validLogin", "validPassword");
        Mockito.when(userRepository.existsByLogin(authRequest.getLogin())).thenReturn(true);
        Mockito.when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        Mockito.when(userRepository.findByLogin("validLogin")).thenReturn(Optional.of(user));
        Mockito.when(jwtProvider.generateAccessToken(user)).thenReturn("fakeAccessToken");
        Mockito.when(jwtProvider.generateRefreshToken(user)).thenReturn("fakeRefreshToken");
        Mockito.when(redisTemplate.opsForValue()).thenReturn((valueOperationsMock));
        JwtResponse jwtResponse = authService.login(authRequest);

        assertNotNull(jwtResponse);
        assertEquals("fakeAccessToken", jwtResponse.getAccessToken());
        assertEquals("fakeRefreshToken", jwtResponse.getRefreshToken());
    }

    @Test
    void login_BadCredentials_ThrowsAuthException() {
        JwtRequest authRequest = new JwtRequest("badLogin", "badPassword");
        Mockito.when(userRepository.existsByLogin("badLogin")).thenReturn(false);
        Assertions.assertThrows(AuthException.class, () -> authService.login(authRequest));
    }

    @Test
    void register_Valid_SuccessfulSaving() throws AuthException {
        RegisterDTO registeredUser = new RegisterDTO("validLogin", "validPassword",
                "Alexander", "Ten");
        Mockito.when(userRepository.existsByLogin(registeredUser.getLogin())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(registeredUser.getPassword())).thenReturn("encryptedPassword");
        Mockito.when(roleRepository.findByVale("USER")).thenReturn(new Role("USER"));
        authService.register(registeredUser);
    }
    @Test
    void register_Bad_ThrowsAuthException(){
        RegisterDTO registerDTO = new RegisterDTO("badLogin", "validPassword", "Alexander", "Ten");
        Mockito.when(userRepository.existsByLogin(registerDTO.getLogin())).thenReturn(true);
        Assertions.assertThrows(AuthException.class, () -> authService.register(registerDTO));
    }
}
