package com.assignment.users.src.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class ServiceResponse {

    private String resMsg;
    private String userId;
    private List<ValidationError> valErrors;

    public ServiceResponse(String resMsg, String userId, List<ValidationError> valErrors) {
        this.resMsg = resMsg;
        this.userId = userId;
        this.valErrors = valErrors;
    }

    public String getResMsg() {
        return resMsg;
    }

    public String getUserId() {
        return userId;
    }

    public List<ValidationError> getValErrors() {
        return valErrors;
    }

    void setValErrors(List<ValidationError> valErrors) {
        this.valErrors = valErrors;
    }
}
