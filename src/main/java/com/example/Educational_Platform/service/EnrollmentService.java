package com.example.Educational_Platform.service;

import com.example.Educational_Platform.Utils.PaginationResponse;
import com.example.Educational_Platform.dtos.CourseDto;

public interface EnrollmentService {
    void enrollStudent(String studentId, String courseId);
    void unenrollStudent(String studentId, String courseId);
    PaginationResponse getEnrolledCourses(String studentId, Integer limit, String lastEvaluatedKey);
    boolean isEnrolled(String studentId, String courseId);
}