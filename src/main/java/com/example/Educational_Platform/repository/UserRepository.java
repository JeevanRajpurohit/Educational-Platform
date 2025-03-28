package com.example.Educational_Platform.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.Educational_Platform.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepository {
    private final DynamoDBMapper dynamoDBMapper;

    public UserRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public User save(User user) {
        dynamoDBMapper.save(user);
        return user;
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(dynamoDBMapper.load(User.class, id));
    }

    public Optional<User> findByEmail(String email) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":email", new AttributeValue().withS(email));

        DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                .withIndexName("email-index")
                .withConsistentRead(false)
                .withKeyConditionExpression("email = :email")
                .withExpressionAttributeValues(eav);

        PaginatedQueryList<User> results = dynamoDBMapper.query(User.class, queryExpression);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public ScanResultPage<User> findByRolePaginated(String role, Integer limit, Map<String, AttributeValue> exclusiveStartKey) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":role", new AttributeValue().withS(role));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#role = :role")
                .withExpressionAttributeNames(Map.of("#role", "role"))
                .withExpressionAttributeValues(expressionAttributeValues)
                .withLimit(limit)
                .withExclusiveStartKey(exclusiveStartKey);

        return dynamoDBMapper.scanPage(User.class, scanExpression);
    }

    public void delete(User user) {
        dynamoDBMapper.delete(user);
    }
}