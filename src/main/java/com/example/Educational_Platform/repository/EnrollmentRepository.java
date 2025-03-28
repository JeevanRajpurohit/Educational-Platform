package com.example.Educational_Platform.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.Educational_Platform.model.Enrollment;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class EnrollmentRepository {
    private final DynamoDBMapper dynamoDBMapper;

    public EnrollmentRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public Enrollment save(Enrollment enrollment) {
        dynamoDBMapper.save(enrollment);
        return enrollment;
    }

    public Optional<Enrollment> findByStudentIdAndCourseId(String studentId, String courseId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":studentId", new AttributeValue().withS(studentId));
        eav.put(":courseId", new AttributeValue().withS(courseId));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("studentId = :studentId and courseId = :courseId")
                .withExpressionAttributeValues(eav);

        List<Enrollment> results = dynamoDBMapper.scan(Enrollment.class, scanExpression);
        return results.stream().findFirst();
    }

    public boolean existsByStudentIdAndCourseId(String studentId, String courseId) {
        return findByStudentIdAndCourseId(studentId, courseId).isPresent();
    }

    public void delete(Enrollment enrollment) {
        dynamoDBMapper.delete(enrollment);
    }

    public QueryResultPage<Enrollment> findByStudentIdPaginated(String studentId, Integer limit, Map<String, AttributeValue> exclusiveStartKey) {
        DynamoDBQueryExpression<Enrollment> queryExpression = new DynamoDBQueryExpression<Enrollment>()
                .withIndexName("studentId-index")
                .withConsistentRead(false)
                .withKeyConditionExpression("studentId = :studentId")
                .withExpressionAttributeValues(Map.of(":studentId", new AttributeValue().withS(studentId)))
                .withLimit(limit)
                .withExclusiveStartKey(exclusiveStartKey);

        return dynamoDBMapper.queryPage(Enrollment.class, queryExpression);
    }


    public List<Enrollment> findByCourseId(String courseId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":courseId", new AttributeValue().withS(courseId));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("courseId = :courseId")
                .withExpressionAttributeValues(eav);

        return dynamoDBMapper.scan(Enrollment.class, scanExpression);
    }
}