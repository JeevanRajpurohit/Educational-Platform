package com.example.Educational_Platform.service;

import com.example.Educational_Platform.dtos.UserDto;
import com.example.Educational_Platform.model.User;

public interface AuthService {
    UserDto registerUser(UserDto registrationDTO);
    String authenticateUser(UserDto loginDTO);
}