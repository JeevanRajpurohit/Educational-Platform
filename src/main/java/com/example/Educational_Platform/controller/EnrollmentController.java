package com.example.Educational_Platform.controller;

import com.example.Educational_Platform.Utils.MessageUtil;
import com.example.Educational_Platform.Utils.PaginationResponse;
import com.example.Educational_Platform.Utils.ResponseHandler;
import com.example.Educational_Platform.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final MessageUtil messageUtil;

    public EnrollmentController(EnrollmentService enrollmentService, MessageUtil messageUtil) {
        this.enrollmentService = enrollmentService;
        this.messageUtil = messageUtil;
    }

    @PostMapping("/{courseId}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<?> enrollCourse(@PathVariable String courseId, @RequestParam String studentId) {
        try {
            enrollmentService.enrollStudent(studentId, courseId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("enrollment.success"),
                            HttpStatus.CREATED.value(),
                            true,
                            "enrollment"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("enrollment.error"),
                            HttpStatus.BAD_REQUEST.value(),
                            false,
                            "error"));
        }
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<?> unenrollCourse(@PathVariable String courseId, @RequestParam String studentId) {
        try {
            enrollmentService.unenrollStudent(studentId, courseId);
            return ResponseEntity.ok(new ResponseHandler(
                    null,
                    messageUtil.getMessage("enrollment.unenroll.success"),
                    HttpStatus.OK.value(),
                    true,
                    "enrollment"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("enrollment.unenroll.error"),
                            HttpStatus.BAD_REQUEST.value(),
                            false,
                            "error"));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','STUDENT')")
    public ResponseEntity<?> getEnrolledCourses(@RequestParam String studentId, @RequestParam(defaultValue = "10") Integer limit, @RequestParam(required = false) String lastEvaluatedKey) {
        try {
            PaginationResponse paginationResponse = enrollmentService.getEnrolledCourses(studentId, limit, lastEvaluatedKey);
            return ResponseEntity.ok(new ResponseHandler(
                    paginationResponse,
                    messageUtil.getMessage("enrollment.retrieve.success"),
                    HttpStatus.OK.value(),
                    true,
                    "courses"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("enrollment.retrieve.error"),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "error"));
        }
    }
}