package com.example.Educational_Platform.controller;

import com.example.Educational_Platform.Utils.MessageUtil;
import com.example.Educational_Platform.Utils.PaginationResponse;
import com.example.Educational_Platform.Utils.ResponseHandler;
import com.example.Educational_Platform.dtos.AnswerDto;
import com.example.Educational_Platform.dtos.QuestionDto;
import com.example.Educational_Platform.service.ForumService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    private final ForumService forumService;
    private final MessageUtil messageUtil;

    public ForumController(ForumService forumService, MessageUtil messageUtil) {
        this.forumService = forumService;
        this.messageUtil = messageUtil;
    }

    @PostMapping("/questions")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<?> postQuestion(@Valid @RequestBody QuestionDto questionDTO, @RequestParam String studentId) {
        try {
            QuestionDto question = forumService.postQuestion(questionDTO, studentId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseHandler(
                            question,
                            messageUtil.getMessage("forum.question.post.success"),
                            HttpStatus.CREATED.value(),
                            true,
                            "question"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("forum.question.post.error"),
                            HttpStatus.BAD_REQUEST.value(),
                            false,
                            "error"));
        }
    }

    @PostMapping("/answers/{questionId}")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'INSTRUCTOR')")
    public ResponseEntity<?> postAnswer(@PathVariable String questionId, @Valid @RequestBody AnswerDto answerDTO, @RequestParam String respondentId) {
        try {
            AnswerDto answer = forumService.postAnswer(answerDTO, questionId, respondentId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseHandler(
                            answer,
                            messageUtil.getMessage("forum.answer.post.success"),
                            HttpStatus.CREATED.value(),
                            true,
                            "answer"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("forum.answer.post.error"),
                            HttpStatus.BAD_REQUEST.value(),
                            false,
                            "error"));
        }
    }

    @GetMapping("/questions/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'INSTRUCTOR')")
    public ResponseEntity<?> getQuestionsByCourse(@PathVariable String courseId, @RequestParam(defaultValue = "10") Integer limit, @RequestParam(required = false) String lastEvaluatedKey) {
        try {
            PaginationResponse paginationResponse = forumService.getQuestionsByCourse(courseId, limit, lastEvaluatedKey);
            return ResponseEntity.ok(new ResponseHandler(
                    paginationResponse,
                    messageUtil.getMessage("forum.questions.retrieve.success"),
                    HttpStatus.OK.value(),
                    true,
                    "questions"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("forum.questions.retrieve.error"),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "error"));
        }
    }

    @GetMapping("/questions/{questionId}")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'INSTRUCTOR')")
    public ResponseEntity<?> getQuestionWithAnswers(@PathVariable String questionId) {
        try {
            QuestionDto question = forumService.getQuestionWithAnswers(questionId);
            return ResponseEntity.ok(new ResponseHandler(
                    question,
                    messageUtil.getMessage("forum.question.retrieve.success"),
                    HttpStatus.OK.value(),
                    true,
                    "question"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("forum.question.not.found"),
                            HttpStatus.NOT_FOUND.value(),
                            false,
                            "error"));
        }
    }

    @GetMapping("/questions/student/{studentId}")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<?> getQuestionsByStudent(@PathVariable String studentId, @RequestParam(defaultValue = "10") Integer limit, @RequestParam(required = false) String lastEvaluatedKey) {
        try {
            PaginationResponse paginationResponse = forumService.getQuestionsByStudent(studentId, limit, lastEvaluatedKey);
            return ResponseEntity.ok(new ResponseHandler(
                    paginationResponse,
                    messageUtil.getMessage("forum.student.questions.retrieve.success"),
                    HttpStatus.OK.value(),
                    true,
                    "questions"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(
                            null,
                            messageUtil.getMessage("forum.student.questions.retrieve.error"),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "error"));
        }
    }
}
