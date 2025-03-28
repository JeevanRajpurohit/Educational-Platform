package com.example.Educational_Platform.service;

import com.example.Educational_Platform.Utils.PaginationResponse;
import com.example.Educational_Platform.dtos.AnswerDto;
import com.example.Educational_Platform.dtos.QuestionDto;

public interface ForumService {
    QuestionDto postQuestion(QuestionDto questionDTO, String studentId);
    AnswerDto postAnswer(AnswerDto answerDTO, String questionId, String respondentId);
    PaginationResponse getQuestionsByCourse(String courseId, Integer limit, String lastEvaluatedKey);
    QuestionDto getQuestionWithAnswers(String questionId);
    PaginationResponse getQuestionsByStudent(String studentId, Integer limit, String lastEvaluatedKey);
}