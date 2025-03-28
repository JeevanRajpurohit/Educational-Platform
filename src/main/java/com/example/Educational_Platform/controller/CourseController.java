package com.example.Educational_Platform.controller;

import com.example.Educational_Platform.Utils.MessageUtil;
import com.example.Educational_Platform.Utils.PaginationResponse;
import com.example.Educational_Platform.Utils.ResponseHandler;
import com.example.Educational_Platform.Validations.ValidationGroups;
import com.example.Educational_Platform.dtos.CourseDto;
import com.example.Educational_Platform.service.CourseService;
import jakarta.validation.groups.Default;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;
    private final MessageUtil messageUtil;

    public CourseController(CourseService courseService, MessageUtil messageUtil) {
        this.courseService = courseService;
        this.messageUtil = messageUtil;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public ResponseEntity<?> createCourse(@Validated({ValidationGroups.OnCreate.class, Default.class}) @RequestBody CourseDto courseDTO) {
        try {
            CourseDto course = courseService.createCourse(courseDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseHandler(
                            course,
                            messageUtil.getMessage("course.create.success"),
                            HttpStatus.CREATED.value(),
                            true,
                            "course"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("course.create.error"),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "error"));
        }
    }

    @PutMapping("/{courseId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public ResponseEntity<?> updateCourse(
            @PathVariable String courseId,
            @Validated({ValidationGroups.OnUpdate.class, Default.class})
            @RequestBody CourseDto courseDTO) {
        try {
            CourseDto course = courseService.updateCourse(courseId, courseDTO);
            return ResponseEntity.ok(new ResponseHandler(
                    course,
                    messageUtil.getMessage("course.update.success"),
                    HttpStatus.OK.value(),
                    true,
                    "course"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("course.update.error"),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "error"));
        }
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public ResponseEntity<?> deleteCourse(@PathVariable String courseId) {
        try {
            courseService.deleteCourse(courseId);
            return ResponseEntity.ok(new ResponseHandler(
                    null,
                    messageUtil.getMessage("course.delete.success"),
                    HttpStatus.OK.value(),
                    true,
                    "course"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("course.delete.error"),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "error"));
        }
    }

    @GetMapping("/{courseId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT', 'INSTRUCTOR')")
    public ResponseEntity<?> getCourseById(@PathVariable String courseId) {
        try {
            CourseDto course = courseService.getCourseById(courseId);
            return ResponseEntity.ok(new ResponseHandler(
                    course,
                    messageUtil.getMessage("course.retrieve.by_Id.success"),
                    HttpStatus.OK.value(),
                    true,
                    "course"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("course.not.found"),
                            HttpStatus.NOT_FOUND.value(),
                            false,
                            "error"));
        }
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT', 'INSTRUCTOR')")
    public ResponseEntity<?> getAllCourses(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(required = false) String lastEvaluatedKey) {
        try {
            PaginationResponse paginationResponse = courseService.getAllCourses(limit, lastEvaluatedKey);
            return ResponseEntity.ok(new ResponseHandler(
                    paginationResponse,
                    messageUtil.getMessage("course.retrieve.success"),
                    HttpStatus.OK.value(),
                    true,
                    "courses"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("course.retrieve.error"),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "error"));
        }
    }

    @GetMapping("/branch/{branchName}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT', 'INSTRUCTOR')")
    public ResponseEntity<?> getCoursesByBranch(@PathVariable String branchName, @RequestParam(defaultValue = "10") Integer limit, @RequestParam(required = false) String lastEvaluatedKey) {
        try {
            PaginationResponse paginationResponse = courseService.getCoursesByBranch(branchName, limit, lastEvaluatedKey);
            return ResponseEntity.ok(new ResponseHandler(
                    paginationResponse,
                    messageUtil.getMessage("course.retrieve.by_branch.success"),
                    HttpStatus.OK.value(),
                    true,
                    "courses"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("course.retrieve.error"),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "error"));
        }
    }

    @GetMapping("/instructor/{instructorId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<?> getCoursesByInstructor(@PathVariable String instructorId, @RequestParam(defaultValue = "10") Integer limit, @RequestParam(required = false) String lastEvaluatedKey) {
        try {
            PaginationResponse paginationResponse = courseService.getCoursesByInstructor(instructorId, limit, lastEvaluatedKey);
            return ResponseEntity.ok(new ResponseHandler(
                    paginationResponse,
                    messageUtil.getMessage("course.retrieve.by_instructor.success"),
                    HttpStatus.OK.value(),
                    true,
                    "courses"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("course.retrieve.error"),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "error"));
        }
    }
}