package com.example.Educational_Platform.service;

import com.example.Educational_Platform.Utils.PaginationResponse;
import com.example.Educational_Platform.dtos.UserDto;
import com.example.Educational_Platform.model.User;

public interface UserService {
    UserDto getUserById(String id);
    UserDto updateUser(String id, User userDetails);
    PaginationResponse getAllUsersByRole(String role, Integer limit, String lastEvaluatedKey);
}
