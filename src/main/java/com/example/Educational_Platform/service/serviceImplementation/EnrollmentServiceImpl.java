package com.example.Educational_Platform.service.serviceImplementation;

import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.example.Educational_Platform.Utils.PaginationResponse;
import com.example.Educational_Platform.dtos.CourseDto;
import com.example.Educational_Platform.model.Course;
import com.example.Educational_Platform.model.Enrollment;
import com.example.Educational_Platform.model.Role;
import com.example.Educational_Platform.model.User;
import com.example.Educational_Platform.repository.CourseRepository;
import com.example.Educational_Platform.repository.EnrollmentRepository;
import com.example.Educational_Platform.repository.UserRepository;
import com.example.Educational_Platform.service.EnrollmentService;
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
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    @Override
    public void enrollStudent(String studentId, String courseId) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalArgumentException("Student is already enrolled in this course");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (!student.getRole().equals(Role.STUDENT.name())) {
            throw new IllegalArgumentException("User is not a student");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setEnrolledAt(new Date());
        enrollmentRepository.save(enrollment);
    }

    @Override
    public void unenrollStudent(String studentId, String courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        enrollmentRepository.delete(enrollment);
    }

    @Override
    public PaginationResponse getEnrolledCourses(String studentId, Integer limit, String lastEvaluatedKey) {
        Map<String, AttributeValue> exclusiveStartKey = getExclusiveStartKey(lastEvaluatedKey);
        QueryResultPage<Enrollment> queryResult = enrollmentRepository.findByStudentIdPaginated(studentId, limit, exclusiveStartKey);

        List<CourseDto> courses = queryResult.getResults().stream()
                .map(enrollment -> {
                    Course course = courseRepository.findById(enrollment.getCourseId())
                            .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
                    User instructor = userRepository.findById(course.getInstructorId())
                            .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
                    return mapToCourseDTO(course, instructor.getName());
                })
                .collect(Collectors.toList());

        String nextKey = queryResult.getLastEvaluatedKey() != null ?
                queryResult.getLastEvaluatedKey().get("enrollmentId").getS() : null;

        return new PaginationResponse(
                courses,
                nextKey,
                limit,
                queryResult.getLastEvaluatedKey() != null
        );
    }


    @Override
    public boolean isEnrolled(String studentId, String courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    private Map<String, AttributeValue> getExclusiveStartKey(String lastEvaluatedKey) {
        if (lastEvaluatedKey == null || lastEvaluatedKey.isEmpty()) {
            return null;
        }
        Map<String, AttributeValue> exclusiveStartKey = new HashMap<>();
        exclusiveStartKey.put("enrollmentId", new AttributeValue().withS(lastEvaluatedKey));
        return exclusiveStartKey;
    }

    private CourseDto mapToCourseDTO(Course course, String instructorName) {
        CourseDto dto = modelMapper.map(course, CourseDto.class);
        dto.setInstructorName(instructorName);
        return dto;
    }
}