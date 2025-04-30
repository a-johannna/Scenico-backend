package com.example.demo1.repositories;

import com.example.demo1.models.entidades.Rols.Role;
import com.example.demo1.models.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
