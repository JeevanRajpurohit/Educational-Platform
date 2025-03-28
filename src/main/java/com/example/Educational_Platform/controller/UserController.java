package com.example.Educational_Platform.controller;

import com.example.Educational_Platform.Utils.MessageUtil;
import com.example.Educational_Platform.Utils.PaginationResponse;
import com.example.Educational_Platform.Utils.ResponseHandler;
import com.example.Educational_Platform.dtos.UserDto;
import com.example.Educational_Platform.model.User;
import com.example.Educational_Platform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final MessageUtil messageUtil;

    public UserController(UserService userService, MessageUtil messageUtil) {
        this.userService = userService;
        this.messageUtil = messageUtil;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT', 'INSTRUCTOR')")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        try {
            UserDto user = userService.getUserById(id);
            return ResponseEntity.ok(new ResponseHandler(
                    user,
                    messageUtil.getMessage("user.retrieve.success"),
                    HttpStatus.OK.value(),
                    true,
                    "user"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("user.not.found"),
                            HttpStatus.NOT_FOUND.value(),
                            false,
                            "error"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT')")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User userDetails) {
        try {
            UserDto updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(new ResponseHandler(
                    updatedUser,
                    messageUtil.getMessage("user.update.success"),
                    HttpStatus.OK.value(),
                    true,
                    "user"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("user.update.error"),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "error"));
        }
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role, @RequestParam(defaultValue = "10") Integer limit, @RequestParam(required = false) String lastEvaluatedKey) {
        try {
            PaginationResponse paginationResponse = userService.getAllUsersByRole(role, limit, lastEvaluatedKey);
            return ResponseEntity.ok(new ResponseHandler(
                    paginationResponse,
                    messageUtil.getMessage("user.retrieve.by_role.success"),
                    HttpStatus.OK.value(),
                    true,
                    "users"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("user.retrieve.error"),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "error"));
        }
    }
}