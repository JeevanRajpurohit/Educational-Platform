package com.example.Educational_Platform.controller;

import com.example.Educational_Platform.Utils.MessageUtil;
import com.example.Educational_Platform.Utils.ResponseHandler;
import com.example.Educational_Platform.Validations.ValidationGroups;
import com.example.Educational_Platform.dtos.UserDto;
import com.example.Educational_Platform.service.AuthService;
import jakarta.validation.groups.Default;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final MessageUtil messageUtil;

    public AuthController(AuthService authService, MessageUtil messageUtil) {
        this.authService = authService;
        this.messageUtil = messageUtil;
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseHandler> register(@Validated({ValidationGroups.OnRegister.class, Default.class}) @RequestBody UserDto registrationDTO) {
        try {
            UserDto userResponse = authService.registerUser(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseHandler(
                            userResponse,
                            messageUtil.getMessage("user.register.success"),
                            HttpStatus.CREATED.value(),
                            true,
                            "user"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("user.register.error"),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "error"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseHandler> login(@Validated({ValidationGroups.OnLogin.class, Default.class}) @RequestBody UserDto loginDTO) {
        try {
            String token = authService.authenticateUser(loginDTO);
            return ResponseEntity.ok(new ResponseHandler(
                    token,
                    messageUtil.getMessage("user.login.success"),
                    HttpStatus.OK.value(),
                    true,
                    "auth"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("user.login.error"),
                            HttpStatus.UNAUTHORIZED.value(),
                            false,
                            "error"));
        }
    }
}