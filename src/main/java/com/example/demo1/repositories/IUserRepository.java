package com.example.demo1.repositories;

import com.example.demo1.models.entidades.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findByUuid(UUID uuid);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);


}
