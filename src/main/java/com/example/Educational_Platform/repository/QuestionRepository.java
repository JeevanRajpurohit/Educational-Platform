package com.example.Educational_Platform.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.Educational_Platform.model.Question;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class QuestionRepository {
    private final DynamoDBMapper dynamoDBMapper;

    public QuestionRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public Question save(Question question) {
        dynamoDBMapper.save(question);
        return question;
    }

    public Optional<Question> findById(String questionId) {
        return Optional.ofNullable(dynamoDBMapper.load(Question.class, questionId));
    }

    public QueryResultPage<Question> findByCourseIdPaginated(String courseId, Integer limit,
                                                             Map<String, AttributeValue> exclusiveStartKey) {
        Question question = new Question();
        question.setCourseId(courseId);

        DynamoDBQueryExpression<Question> queryExpression = new DynamoDBQueryExpression<Question>()
                .withIndexName("courseId-index")
                .withConsistentRead(false)
                .withHashKeyValues(question)
                .withLimit(limit)
                .withExclusiveStartKey(exclusiveStartKey);

        return dynamoDBMapper.queryPage(Question.class, queryExpression);
    }

    public QueryResultPage<Question> findByStudentIdPaginated(String studentId, Integer limit,
                                                              Map<String, AttributeValue> exclusiveStartKey) {
        Question question = new Question();
        question.setStudentId(studentId);

        DynamoDBQueryExpression<Question> queryExpression = new DynamoDBQueryExpression<Question>()
                .withIndexName("studentId-index")
                .withConsistentRead(false)
                .withHashKeyValues(question)
                .withLimit(limit)
                .withExclusiveStartKey(exclusiveStartKey);

        return dynamoDBMapper.queryPage(Question.class, queryExpression);
    }

    public void delete(Question question) {
        dynamoDBMapper.delete(question);
    }
}