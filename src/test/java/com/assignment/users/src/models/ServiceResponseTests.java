package com.assignment.users.src.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class ServiceResponseTests {

    private static final String EXP_MSG = "{\"resMsg\":\"Message\",\"userId\":\"test-user-id\",\"valErrors\":[{\"code\":\"400\",\"field\":\"DOB\",\"message\":\"DOB field  not found\"},{\"code\":\"404\",\"field\":\"fName\",\"message\":\"first name is empty\"}]}";

    @Test
    public void testGetServiceResponse() throws Exception {
        List<ValidationError> valErrors = new ArrayList<>();
        valErrors.add(new ValidationError("400", "DOB", "DOB field  not found"));
        valErrors.add(new ValidationError("404", "fName", "first name is empty"));

        ServiceResponse response = new ServiceResponse("Message", "test-user-id", valErrors);
        response.setValErrors(valErrors);

        ObjectMapper mapper = new ObjectMapper();
        String responseStr = mapper.writer().writeValueAsString(response);

        assertEquals("Message", EXP_MSG, responseStr);
    }

}