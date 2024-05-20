package com.auth.auth_service.service;

import com.auth.auth_service.model.User;
import com.auth.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(userName).orElseThrow(()-> new UsernameNotFoundException("User with email = "+userName+" not exist!"));
        return new CustomUsrDetails(user);
    }
    public Optional<User> getUserById(Long id ) throws RuntimeException {
        return userRepository.getUserById(id);
    }
}
