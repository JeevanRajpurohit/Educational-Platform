package com.example.Educational_Platform.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.Educational_Platform.model.Course;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CourseRepository {
    private final DynamoDBMapper dynamoDBMapper;

    public CourseRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public Course save(Course course) {
        dynamoDBMapper.save(course);
        return course;
    }

    public Optional<Course> findById(String courseId) {
        return Optional.ofNullable(dynamoDBMapper.load(Course.class, courseId));
    }

    public void delete(Course course) {
        dynamoDBMapper.delete(course);
    }

    public ScanResultPage<Course> findByBranchName(String branchName, Integer limit, Map<String, AttributeValue> exclusiveStartKey) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":branchName", new AttributeValue().withS(branchName));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("branchName = :branchName")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withLimit(limit)
                .withExclusiveStartKey(exclusiveStartKey);

        return dynamoDBMapper.scanPage(Course.class, scanExpression);
    }

    public ScanResultPage<Course> findByInstructorId(String instructorId, Integer limit, Map<String, AttributeValue> exclusiveStartKey) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":instructorId", new AttributeValue().withS(instructorId));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("instructorId = :instructorId")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withLimit(limit)
                .withExclusiveStartKey(exclusiveStartKey);

        return dynamoDBMapper.scanPage(Course.class, scanExpression);
    }

    public ScanResultPage<Course> findAllPaginated(Integer limit, Map<String, AttributeValue> exclusiveStartKey) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withLimit(limit)
                .withExclusiveStartKey(exclusiveStartKey);

        return dynamoDBMapper.scanPage(Course.class, scanExpression);
    }
}
