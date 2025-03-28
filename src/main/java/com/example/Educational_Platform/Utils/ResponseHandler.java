package com.example.Educational_Platform.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseHandler {
    private Object data;
    private String message;
    private int status;
    private boolean success;
    private String entity;
}
