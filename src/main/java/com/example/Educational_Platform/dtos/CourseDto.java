package com.example.Educational_Platform.dtos;
import com.example.Educational_Platform.Validations.ValidationGroups;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    @NotBlank(groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class},
            message = "Course name is required")
    private String courseName;

    @NotBlank(groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class},
            message = "Description is required")
    private String description;

    @NotBlank(groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class},
            message = "Branch name is required")
    private String branchName;

    @NotNull(groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class},
            message = "Instructor ID is required")
    private String instructorId;

    private String courseId;
    private String instructorName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
}