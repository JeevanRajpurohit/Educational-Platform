package com.example.Educational_Platform.service.serviceImplementation;

import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.example.Educational_Platform.Utils.PaginationResponse;
import com.example.Educational_Platform.dtos.CourseDto;
import com.example.Educational_Platform.model.Course;
import com.example.Educational_Platform.model.Role;
import com.example.Educational_Platform.model.User;
import com.example.Educational_Platform.repository.CourseRepository;
import com.example.Educational_Platform.repository.UserRepository;
import com.example.Educational_Platform.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public CourseDto createCourse(CourseDto courseDTO) {
        User instructor = userRepository.findById(courseDTO.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + courseDTO.getInstructorId()));

        if (!instructor.getRole().equals(Role.INSTRUCTOR.name())) {
            throw new IllegalArgumentException("User is not an instructor");
        }

        Course course = modelMapper.map(courseDTO, Course.class);
        course.setCreatedAt(new Date());
        course.setUpdatedAt(new Date());

        Course savedCourse = courseRepository.save(course);
        return mapToCourseDTO(savedCourse, instructor.getName());
    }

    @Override
    public CourseDto updateCourse(String courseId, CourseDto courseDTO) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        User instructor = userRepository.findById(courseDTO.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + courseDTO.getInstructorId()));

        if (!instructor.getRole().equals(Role.INSTRUCTOR.name())) {
            throw new IllegalArgumentException("User is not an instructor");
        }

        course.setCourseName(courseDTO.getCourseName());
        course.setDescription(courseDTO.getDescription());
        course.setBranchName(courseDTO.getBranchName());
        course.setInstructorId(courseDTO.getInstructorId());
        course.setUpdatedAt(new Date());

        Course updatedCourse = courseRepository.save(course);
        return mapToCourseDTO(updatedCourse, instructor.getName());
    }

    @Override
    public void deleteCourse(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        courseRepository.delete(course);
    }

    @Override
    public CourseDto getCourseById(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        User instructor = userRepository.findById(course.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + course.getInstructorId()));

        return mapToCourseDTO(course, instructor.getName());
    }

    @Override
    public PaginationResponse getAllCourses(Integer limit, String lastEvaluatedKey) {
        Map<String, AttributeValue> exclusiveStartKey = getExclusiveStartKey(lastEvaluatedKey);
        ScanResultPage<Course> scanResult = courseRepository.findAllPaginated(limit, exclusiveStartKey);

        return buildPaginationResponse(scanResult, limit);
    }

    @Override
    public PaginationResponse getCoursesByBranch(String branchName, Integer limit, String lastEvaluatedKey) {
        Map<String, AttributeValue> exclusiveStartKey = getExclusiveStartKey(lastEvaluatedKey);
        ScanResultPage<Course> scanResult = courseRepository.findByBranchName(branchName, limit, exclusiveStartKey);

        return buildPaginationResponse(scanResult, limit);
    }

    @Override
    public PaginationResponse getCoursesByInstructor(String instructorId, Integer limit, String lastEvaluatedKey) {
        Map<String, AttributeValue> exclusiveStartKey = getExclusiveStartKey(lastEvaluatedKey);
        ScanResultPage<Course> scanResult = courseRepository.findByInstructorId(instructorId, limit, exclusiveStartKey);

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        List<CourseDto> courses = scanResult.getResults().stream()
                .map(course -> mapToCourseDTO(course, instructor.getName()))
                .collect(Collectors.toList());

        String nextKey = scanResult.getLastEvaluatedKey() != null ?
                scanResult.getLastEvaluatedKey().get("courseId").getS() : null;

        return new PaginationResponse(
                courses,
                nextKey,
                limit,
                scanResult.getLastEvaluatedKey() != null
        );
    }

    private Map<String, AttributeValue> getExclusiveStartKey(String lastEvaluatedKey) {
        if (lastEvaluatedKey == null || lastEvaluatedKey.isEmpty()) {
            return null;
        }
        Map<String, AttributeValue> exclusiveStartKey = new HashMap<>();
        exclusiveStartKey.put("courseId", new AttributeValue().withS(lastEvaluatedKey));
        return exclusiveStartKey;
    }

    private PaginationResponse buildPaginationResponse(ScanResultPage<Course> scanResult, Integer limit) {
        List<CourseDto> courses = scanResult.getResults().stream()
                .map(course -> {
                    User instructor = userRepository.findById(course.getInstructorId())
                            .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
                    return mapToCourseDTO(course, instructor.getName());
                })
                .collect(Collectors.toList());

        String nextKey = scanResult.getLastEvaluatedKey() != null ?
                scanResult.getLastEvaluatedKey().get("courseId").getS() : null;

        return new PaginationResponse(
                courses,
                nextKey,
                limit,
                scanResult.getLastEvaluatedKey() != null
        );
    }

    private CourseDto mapToCourseDTO(Course course, String instructorName) {
        CourseDto dto = modelMapper.map(course, CourseDto.class);
        dto.setInstructorName(instructorName);
        return dto;
    }
}
