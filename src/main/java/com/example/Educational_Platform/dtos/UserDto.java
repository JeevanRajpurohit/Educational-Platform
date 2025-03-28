package com.example.Educational_Platform.dtos;
import com.example.Educational_Platform.Validations.ValidationGroups;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Null(groups = ValidationGroups.OnLogin.class)
    @NotBlank(groups = ValidationGroups.OnRegister.class, message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Null(groups = ValidationGroups.OnLogin.class)
    @NotNull(groups = ValidationGroups.OnRegister.class, message = "Date of birth is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dob;

    @Null(groups = ValidationGroups.OnLogin.class)
    @NotBlank(groups = ValidationGroups.OnRegister.class, message = "Gender is required")
    private String gender;

    @Null(groups = ValidationGroups.OnLogin.class)
    @NotBlank(groups = ValidationGroups.OnRegister.class, message = "Role is required")
    private String role;

    private String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
}
