package com.example.Educational_Platform.service.serviceImplementation;

import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.example.Educational_Platform.Utils.PaginationResponse;
import com.example.Educational_Platform.dtos.AnswerDto;
import com.example.Educational_Platform.dtos.QuestionDto;
import com.example.Educational_Platform.model.*;
import com.example.Educational_Platform.repository.*;
import com.example.Educational_Platform.service.ForumService;
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
public class ForumServiceImpl implements ForumService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    @Override
    public QuestionDto postQuestion(QuestionDto questionDTO, String studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (!student.getRole().equals(Role.STUDENT.name())) {
            throw new IllegalArgumentException("Only students can post questions");
        }

        Course course = courseRepository.findById(questionDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + questionDTO.getCourseId()));

        Question question = modelMapper.map(questionDTO, Question.class);
        question.setStudentId(studentId);
        question.setCreatedAt(new Date());
        question.setUpdatedAt(new Date());

        Question savedQuestion = questionRepository.save(question);
        return mapToQuestionDTO(savedQuestion, student.getName(), course.getCourseName());
    }

    @Override
    public AnswerDto postAnswer(AnswerDto answerDTO, String questionId, String respondentId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        User respondent = userRepository.findById(respondentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + respondentId));

        if (!respondent.getRole().equals(Role.INSTRUCTOR.name()) && !respondent.getRole().equals(Role.STUDENT.name())) {
            throw new IllegalArgumentException("Only students or instructors can answer questions");
        }

        Answer answer = modelMapper.map(answerDTO, Answer.class);
        answer.setQuestionId(questionId);
        answer.setRespondentId(respondentId);
        answer.setCreatedAt(new Date());
        answer.setUpdatedAt(new Date());

        Answer savedAnswer = answerRepository.save(answer);
        return mapToAnswerDTO(savedAnswer, respondent.getName());
    }

    @Override
    public PaginationResponse getQuestionsByCourse(String courseId, Integer limit, String lastEvaluatedKey) {
        Map<String, AttributeValue> exclusiveStartKey = getExclusiveStartKey(lastEvaluatedKey);
        QueryResultPage<Question> scanResult = questionRepository.findByCourseIdPaginated(
                courseId, limit, exclusiveStartKey);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        List<QuestionDto> questions = scanResult.getResults().stream()
                .map(question -> {
                    User student = userRepository.findById(question.getStudentId())
                            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
                    return mapToQuestionDTO(question, student.getName(), course.getCourseName());
                })
                .collect(Collectors.toList());

        String nextKey = scanResult.getLastEvaluatedKey() != null ?
                scanResult.getLastEvaluatedKey().get("questionId").getS() : null;

        return new PaginationResponse(
                questions,
                nextKey,
                limit,
                scanResult.getLastEvaluatedKey() != null
        );
    }

    @Override
    public QuestionDto getQuestionWithAnswers(String questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        User student = userRepository.findById(question.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Course course = courseRepository.findById(question.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        QuestionDto dto = mapToQuestionDTO(question, student.getName(), course.getCourseName());

        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        dto.setAnswers(answers.stream()
                .map(answer -> {
                    User respondent = userRepository.findById(answer.getRespondentId())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    return mapToAnswerDTO(answer, respondent.getName());
                })
                .collect(Collectors.toList()));

        return dto;
    }

    @Override
    public PaginationResponse getQuestionsByStudent(String studentId, Integer limit, String lastEvaluatedKey) {
        Map<String, AttributeValue> exclusiveStartKey = getExclusiveStartKey(lastEvaluatedKey);
        QueryResultPage<Question> scanResult = questionRepository.findByStudentIdPaginated(
                studentId, limit, exclusiveStartKey);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        List<QuestionDto> questions = scanResult.getResults().stream()
                .map(question -> {
                    Course course = courseRepository.findById(question.getCourseId())
                            .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
                    return mapToQuestionDTO(question, student.getName(), course.getCourseName());
                })
                .collect(Collectors.toList());

        String nextKey = scanResult.getLastEvaluatedKey() != null ?
                scanResult.getLastEvaluatedKey().get("questionId").getS() : null;

        return new PaginationResponse(
                questions,
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
        exclusiveStartKey.put("questionId", new AttributeValue().withS(lastEvaluatedKey));
        return exclusiveStartKey;
    }

    private QuestionDto mapToQuestionDTO(Question question, String studentName, String courseName) {
        QuestionDto dto = modelMapper.map(question, QuestionDto.class);
        dto.setStudentName(studentName);
        dto.setCourseName(courseName);
        return dto;
    }

    private AnswerDto mapToAnswerDTO(Answer answer, String respondentName) {
        AnswerDto dto = modelMapper.map(answer, AnswerDto.class);
        dto.setRespondentName(respondentName);
        return dto;
    }
}