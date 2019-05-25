package com.assignment.users.src.models;

public class ValidationError {

    private String code;
    private String field;
    private String message;

    public ValidationError(String code, String field, String message) {
        this.code = code;
        this.field = field;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

}
