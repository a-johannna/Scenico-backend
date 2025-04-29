package com.example.demo1.services;

import com.example.demo1.mappers.UserMapper;
import com.example.demo1.models.dtos.UserModel.CreateUserDTO;
import com.example.demo1.models.entidades.UserModel;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserModel createUser(CreateUserDTO dto) {
        return userMapper.toEntity(dto);
    }
}
