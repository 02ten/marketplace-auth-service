package com.auth.auth_service.repository;

import com.auth.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String userName);
    boolean existsByLogin(String login);
    Optional<User> getUserById(Long id);
}
