package com.example.Educational_Platform.service;

import com.example.Educational_Platform.Utils.PaginationResponse;
import com.example.Educational_Platform.dtos.CourseDto;

public interface CourseService {
    CourseDto createCourse(CourseDto courseDTO);
    CourseDto updateCourse(String courseId, CourseDto courseDTO);
    void deleteCourse(String courseId);
    CourseDto getCourseById(String courseId);
    PaginationResponse getAllCourses(Integer limit, String lastEvaluatedKey);
    PaginationResponse getCoursesByBranch(String branchName, Integer limit, String lastEvaluatedKey);
    PaginationResponse getCoursesByInstructor(String instructorId, Integer limit, String lastEvaluatedKey);
}