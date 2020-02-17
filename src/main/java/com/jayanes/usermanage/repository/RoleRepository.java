package com.jayanes.usermanage.repository;

import com.jayanes.usermanage.model.Role;
import com.jayanes.usermanage.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(RoleName roleName);
}
