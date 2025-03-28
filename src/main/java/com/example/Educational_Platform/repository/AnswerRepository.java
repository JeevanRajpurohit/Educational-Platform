package com.example.Educational_Platform.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.Educational_Platform.model.Answer;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AnswerRepository {
    private final DynamoDBMapper dynamoDBMapper;

    public AnswerRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public Answer save(Answer answer) {
        dynamoDBMapper.save(answer);
        return answer;
    }

    public List<Answer> findByQuestionId(String questionId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":questionId", new AttributeValue().withS(questionId));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("questionId = :questionId")
                .withExpressionAttributeValues(eav);

        return dynamoDBMapper.scan(Answer.class, scanExpression);
    }

    public void delete(Answer answer) {
        dynamoDBMapper.delete(answer);
    }
}