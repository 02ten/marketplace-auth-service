package com.auth.auth_service.service;

import com.auth.auth_service.dto.RegisterDTO;
import com.auth.auth_service.jwt.JwtAuthentication;
import com.auth.auth_service.jwt.JwtProvider;
import com.auth.auth_service.dto.JwtRequest;
import com.auth.auth_service.dto.JwtResponse;
import com.auth.auth_service.model.Role;
import com.auth.auth_service.model.User;
import com.auth.auth_service.repository.RoleRepository;
import com.auth.auth_service.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    public JwtResponse login(@NonNull JwtRequest authRequest) throws AuthException {
        if (!userRepository.existsByLogin(authRequest.getLogin())) throw new AuthException("Неверные данные");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword());
        Authentication auth = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = userRepository.findByLogin(authRequest.getLogin()).orElseThrow(() ->
                new UsernameNotFoundException("No user with email = "));
        final String accessToken = jwtProvider.generateAccessToken(user);
        final String refreshToken = jwtProvider.generateRefreshToken(user);
        redisTemplate.opsForValue().set(user.getLogin(), refreshToken, 45, TimeUnit.MINUTES);
        return new JwtResponse(accessToken, refreshToken);
    }

    public String register(RegisterDTO registeredUser) throws AuthException {
        if (userRepository.existsByLogin(registeredUser.getLogin()))
            throw new AuthException("Пользователь с таким логином уже существует");
        Role role = roleRepository.findByVale("USER");
        User user = new User();
        user.setLogin(registeredUser.getLogin());
        user.setPassword(passwordEncoder.encode(registeredUser.getPassword()));
        user.setFirstName(registeredUser.getFirstName());
        user.setLastName(registeredUser.getLastName());
        user.setRoles(Set.of(role));
        userRepository.save(user);
        return "Регистрация успешна";
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = redisTemplate.opsForValue().get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userRepository.findByLogin(login)
                        .orElseThrow(() -> new AuthException("Пользователь не найден"));
                final String accessToken = jwtProvider.generateAccessToken(user);
                return new JwtResponse(accessToken, null);
            }
        }
        return new JwtResponse(null, null);
    }

    public JwtResponse refresh(@NonNull String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = redisTemplate.opsForValue().get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userRepository.findByLogin(login)
                        .orElseThrow(() -> new AuthException("Пользователь не найден"));
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                redisTemplate.opsForValue().set(user.getLogin(), newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        throw new AuthException("Невалидный JWT токен");
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

}
