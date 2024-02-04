package com.auth.auth_service.repository;

import com.auth.auth_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByVale(String vale);
}
