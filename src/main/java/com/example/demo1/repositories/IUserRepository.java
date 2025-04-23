package com.example.demo1.repositories;

import com.example.demo1.models.entidades.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findByUuid(UUID uuid);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);


}
