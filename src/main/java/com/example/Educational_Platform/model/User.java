package com.example.Educational_Platform.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.example.Educational_Platform.Utils.DateConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "User")
public class User {
    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String id;

    @DynamoDBAttribute
    private String name;


    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "email-index")
    private String email;

    @DynamoDBAttribute
    private String password;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = DateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dob;

    @DynamoDBAttribute
    private String gender;

    @DynamoDBAttribute
    private String role;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = DateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = DateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
}