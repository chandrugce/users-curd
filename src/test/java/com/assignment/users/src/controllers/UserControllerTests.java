package com.assignment.users.src.controllers;

import com.assignment.users.src.models.ServiceResponse;
import com.assignment.users.src.models.UserDocument;
import com.assignment.users.src.models.UserModel;
import com.assignment.users.src.repo.UsersRepository;
import junit.framework.TestCase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.mongodb.client.result.UpdateResult.acknowledged;
import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;
import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.util.StringUtils.isEmpty;

@SuppressWarnings("deprecation")
public class UserControllerTests {

    @Mock
    UsersRepository repo;
    @InjectMocks
    UserController controller;
    @Mock
    MongoTemplate template;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(repo.insert((UserDocument) any())).thenReturn(mockUserDoc());
    }


    @Test
    public void testCreateUserBaseline() throws Exception {
        ResponseEntity<ServiceResponse> response = controller.createUser(mockCreateRequest());

        assertNotNull("Response body should not be null", response.getBody());
        assertEquals("Return http status", HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertEquals("resMsg", "success", response.getBody().getResMsg());
        assertFalse("userId should not be empty", isEmpty(response.getBody().getUserId()));
        assertEquals("No validation errors", 0, response.getBody().getValErrors().size());
    }

    @Test
    public void testCreateUserFirstNameMissing() throws Exception {
        UserModel request = mockCreateRequest();
        request.setfName(null);
        ResponseEntity<ServiceResponse> response = controller.createUser(request);

        assertNotNull("Response body should not be null", response.getBody());
        assertEquals("Return http status", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("resMsg", "Invalid Request", response.getBody().getResMsg());
        assertTrue("userId should not be returned", isEmpty(response.getBody().getUserId()));
        assertEquals("1 validation error1", 1, response.getBody().getValErrors().size());
        assertEquals("Validation error code", "Bad Request", response.getBody().getValErrors().get(0).getCode());
        assertEquals("Validation error message", "First Name is empty", response.getBody().getValErrors().get(0).getMessage());
        assertEquals("Validation error field", "fName", response.getBody().getValErrors().get(0).getField());
    }

    @Test
    public void testCreateUserLastNameMissing() throws Exception {
        UserModel request = mockCreateRequest();
        request.setlName(null);
        ResponseEntity<ServiceResponse> response = controller.createUser(request);

        assertNotNull("Response body should not be null", response.getBody());
        assertEquals("Return http status", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("resMsg", "Invalid Request", response.getBody().getResMsg());
        assertTrue("userId should not be returned", isEmpty(response.getBody().getUserId()));
        assertEquals("1 validation error1", 1, response.getBody().getValErrors().size());
        assertEquals("Validation error code", "Bad Request", response.getBody().getValErrors().get(0).getCode());
        assertEquals("Validation error message", "Last Name is empty", response.getBody().getValErrors().get(0).getMessage());
        assertEquals("Validation error field", "lName", response.getBody().getValErrors().get(0).getField());
    }

    @Test
    public void testCreateUserEmailMissing() throws Exception {
        UserModel request = mockCreateRequest();
        request.setEmail(null);
        ResponseEntity<ServiceResponse> response = controller.createUser(request);

        assertNotNull("Response body should not be null", response.getBody());
        assertEquals("Return http status", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("resMsg", "Invalid Request", response.getBody().getResMsg());
        assertTrue("userId should not be returned", isEmpty(response.getBody().getUserId()));
        assertEquals("1 validation error1", 1, response.getBody().getValErrors().size());
        assertEquals("Validation error code", "Bad Request", response.getBody().getValErrors().get(0).getCode());
        assertEquals("Validation error message", "Email is not valid", response.getBody().getValErrors().get(0).getMessage());
        assertEquals("Validation error field", "email", response.getBody().getValErrors().get(0).getField());
    }

    @Test
    public void testCreateUserInvalidEmail() throws Exception {
        UserModel request = mockCreateRequest();
        request.setEmail("invalid");
        ResponseEntity<ServiceResponse> response = controller.createUser(request);

        assertNotNull("Response body should not be null", response.getBody());
        assertEquals("Return http status", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("resMsg", "Invalid Request", response.getBody().getResMsg());
        assertTrue("userId should not be returned", isEmpty(response.getBody().getUserId()));
        assertEquals("1 validation error1", 1, response.getBody().getValErrors().size());
        assertEquals("Validation error code", "Bad Request", response.getBody().getValErrors().get(0).getCode());
        assertEquals("Validation error message", "Email is not valid", response.getBody().getValErrors().get(0).getMessage());
        assertEquals("Validation error field", "email", response.getBody().getValErrors().get(0).getField());
    }

    @Test
    public void testCreateUserPinCodeMissing() throws Exception {
        UserModel request = mockCreateRequest();
        request.setPinCode("");
        ResponseEntity<ServiceResponse> response = controller.createUser(request);

        assertNotNull("Response body should not be null", response.getBody());
        assertEquals("Return http status", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("resMsg", "Invalid Request", response.getBody().getResMsg());
        assertTrue("userId should not be returned", isEmpty(response.getBody().getUserId()));
        assertEquals("1 validation error1", 1, response.getBody().getValErrors().size());
        assertEquals("Validation error code", "Bad Request", response.getBody().getValErrors().get(0).getCode());
        assertEquals("Validation error message", "Pin code is empty", response.getBody().getValErrors().get(0).getMessage());
        assertEquals("Validation error field", "pinCode", response.getBody().getValErrors().get(0).getField());
    }

    @Test
    public void testCreateUserInvalidPinCode() throws Exception {
        UserModel request = mockCreateRequest();
        request.setPinCode("abcded123");
        ResponseEntity<ServiceResponse> response = controller.createUser(request);

        assertNotNull("Response body should not be null", response.getBody());
        assertEquals("Return http status", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("resMsg", "Invalid Request", response.getBody().getResMsg());
        assertTrue("userId should not be returned", isEmpty(response.getBody().getUserId()));
        assertEquals("1 validation error1", 1, response.getBody().getValErrors().size());
        assertEquals("Validation error code", "Bad Request", response.getBody().getValErrors().get(0).getCode());
        assertEquals("Validation error message", "Pin Code is not valid", response.getBody().getValErrors().get(0).getMessage());
        assertEquals("Validation error field", "pinCode", response.getBody().getValErrors().get(0).getField());
    }


    @Test
    public void testCreateUserDOBYearInFuture() throws Exception {
        UserModel request = mockCreateRequest();
        request.setBirthDate("01-JAN-3000");
        ResponseEntity<ServiceResponse> response = controller.createUser(request);

        assertNotNull("Response body should not be null", response.getBody());
        assertEquals("Return http status", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("resMsg", "Invalid Request", response.getBody().getResMsg());
        assertTrue("userId should not be returned", isEmpty(response.getBody().getUserId()));
        assertEquals("1 validation error1", 1, response.getBody().getValErrors().size());
        assertEquals("Validation error code", "Bad Request", response.getBody().getValErrors().get(0).getCode());
        assertEquals("Validation error message", "Date of birth is not valid", response.getBody().getValErrors().get(0).getMessage());
        assertEquals("Validation error field", "birthDate", response.getBody().getValErrors().get(0).getField());
    }

    @Test
    public void testCreateUserDOBInvalidMonth() throws Exception {
        UserModel request = mockCreateRequest();
        request.setBirthDate("01-THU-1990");
        ResponseEntity<ServiceResponse> response = controller.createUser(request);

        assertNotNull("Response body should not be null", response.getBody());
        assertEquals("Return http status", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("resMsg", "Invalid Request", response.getBody().getResMsg());
        assertTrue("userId should not be returned", isEmpty(response.getBody().getUserId()));
        assertEquals("1 validation error1", 1, response.getBody().getValErrors().size());
        assertEquals("Validation error code", "Bad Request", response.getBody().getValErrors().get(0).getCode());
        assertEquals("Validation error message", "Date of birth is not valid", response.getBody().getValErrors().get(0).getMessage());
        assertEquals("Validation error field", "birthDate", response.getBody().getValErrors().get(0).getField());
    }

    @Test
    public void testCreateUserDOBInvalidDate() throws Exception {
        UserModel request = mockCreateRequest();
        request.setBirthDate("32-JAN-1990");
        ResponseEntity<ServiceResponse> response = controller.createUser(request);

        assertNotNull("Response body should not be null", response.getBody());
        assertEquals("Return http status", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("resMsg", "Invalid Request", response.getBody().getResMsg());
        assertTrue("userId should not be returned", isEmpty(response.getBody().getUserId()));
        assertEquals("1 validation error1", 1, response.getBody().getValErrors().size());
        assertEquals("Validation error code", "Bad Request", response.getBody().getValErrors().get(0).getCode());
        assertEquals("Validation error message", "Date of birth is not valid", response.getBody().getValErrors().get(0).getMessage());
        assertEquals("Validation error field", "birthDate", response.getBody().getValErrors().get(0).getField());
    }

    @Test
    public void testCreateUserDOBEmpty() throws Exception {
        UserModel request = mockCreateRequest();
        request.setBirthDate("");
        ResponseEntity<ServiceResponse> response = controller.createUser(request);

        assertNotNull("Response body should not be null", response.getBody());
        assertEquals("Return http status", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("resMsg", "Invalid Request", response.getBody().getResMsg());
        assertTrue("userId should not be returned", isEmpty(response.getBody().getUserId()));
        assertEquals("1 validation error", 1, response.getBody().getValErrors().size());
        assertEquals("Validation error code", "Bad Request", response.getBody().getValErrors().get(0).getCode());
        assertEquals("Validation error message", "Date of birth should not be empty", response.getBody().getValErrors().get(0).getMessage());
        assertEquals("Validation error field", "birthDate", response.getBody().getValErrors().get(0).getField());
    }

    @Test
    public void testCreateUserDuplicateEmail() throws Exception {
        when(repo.insert((UserDocument) any())).thenThrow(new DuplicateKeyException("email"));

        UserModel request = mockCreateRequest();
        ResponseEntity<ServiceResponse> response = controller.createUser(request);

        assertNotNull("Response body should not be null", response.getBody());
        assertEquals("Return http status", HttpStatus.PRECONDITION_FAILED.value(), response.getStatusCodeValue());
        assertEquals("resMsg", "User already exists", response.getBody().getResMsg());
        assertTrue("userId should not be returned", isEmpty(response.getBody().getUserId()));
        assertEquals("1 validation error1", 0, response.getBody().getValErrors().size());
    }

    @Test
    public void testCreateUserUnexpectedError() throws Exception {
        when(repo.insert((UserDocument) any())).thenThrow(new RuntimeException("unexpected error"));

        UserModel request = mockCreateRequest();
        ResponseEntity<ServiceResponse> response = controller.createUser(request);

        assertNotNull("Response body should not be null", response.getBody());
        assertEquals("Return http status", HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCodeValue());
        assertEquals("resMsg", "Unexpected error, message:unexpected error", response.getBody().getResMsg());
        assertTrue("userId should not be returned", isEmpty(response.getBody().getUserId()));
        assertEquals("1 validation error1", 0, response.getBody().getValErrors().size());
    }


    @Test
    public void testParseDate() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        Date parse = format.parse("25-DEC-1990");
        assertEquals("date", 25, parse.getDate());
        assertEquals("month", 11, parse.getMonth());
        assertEquals("year", 90, parse.getYear());
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserModel request = new UserModel();
        request.setBirthDate("02-MAY-1998");
        request.setPinCode("1222");
        when(template.updateFirst(anyObject(), anyObject(), eq(UserDocument.class))).thenReturn(acknowledged(1, 1L, null));

        ResponseEntity<ServiceResponse> response = controller.updateUser("test", request);

        assertEquals("status code", HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals("userId", "test", requireNonNull(response.getBody()).getUserId());
        assertEquals("response message", "success", response.getBody().getResMsg());
        assertEquals("validation errors", 0, response.getBody().getValErrors().size());
    }

    @Test
    public void testUpdateUserWithOnlyDOB() throws Exception {
        UserModel request = new UserModel();
        request.setBirthDate("02-MAY-1998");

        when(template.updateFirst(anyObject(), anyObject(), eq(UserDocument.class))).thenReturn(acknowledged(1, 1L, null));

        ResponseEntity<ServiceResponse> response = controller.updateUser("test", request);

        assertEquals("status code", HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals("userId", "test", requireNonNull(response.getBody()).getUserId());
        assertEquals("response message", "success", response.getBody().getResMsg());
        assertEquals("validation errors", 0, response.getBody().getValErrors().size());
    }

    @Test
    public void testUpdateUserWithOnlyPin() throws Exception {
        UserModel request = new UserModel();
        request.setPinCode("1222");

        when(template.updateFirst(anyObject(), anyObject(), eq(UserDocument.class))).thenReturn(acknowledged(1, 1L, null));

        ResponseEntity<ServiceResponse> response = controller.updateUser("test", request);

        assertEquals("status code", HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals("userId", "test", requireNonNull(response.getBody()).getUserId());
        assertEquals("response message", "success", response.getBody().getResMsg());
        assertEquals("validation errors", 0, response.getBody().getValErrors().size());
    }

    @Test
    public void testUpdateUserWithEmptyRequest() throws Exception {
        UserModel request = new UserModel();

        when(template.updateFirst(anyObject(), anyObject(), eq(UserDocument.class))).thenReturn(acknowledged(1, 1L, null));

        ResponseEntity<ServiceResponse> response = controller.updateUser("test", request);

        assertEquals("status code", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertTrue("userId", isEmpty(requireNonNull(response.getBody()).getUserId()));
        assertEquals("response message", "Invalid Request", response.getBody().getResMsg());
        assertEquals("validation errors", 1, response.getBody().getValErrors().size());
        assertEquals("Validation error code", "Bad Request", response.getBody().getValErrors().get(0).getCode());
        assertEquals("Validation error message", "PinCode or DOB required for update", response.getBody().getValErrors().get(0).getMessage());
        assertEquals("Validation error field", "pinCode, birthDate", response.getBody().getValErrors().get(0).getField());

    }

    @Test
    public void testUpdateUserWithInvalidPin() throws Exception {
        UserModel request = new UserModel();
        request.setPinCode("12abcd");

        when(template.updateFirst(anyObject(), anyObject(), eq(UserDocument.class))).thenReturn(acknowledged(1, 1L, null));

        ResponseEntity<ServiceResponse> response = controller.updateUser("test", request);

        assertEquals("status code", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertTrue("userId", isEmpty(requireNonNull(response.getBody()).getUserId()));
        assertEquals("response message", "Invalid Request", response.getBody().getResMsg());
        assertEquals("validation errors", 1, response.getBody().getValErrors().size());
        assertEquals("Validation error code", "Bad Request", response.getBody().getValErrors().get(0).getCode());
        assertEquals("Validation error message", "Pin Code is not valid", response.getBody().getValErrors().get(0).getMessage());
        assertEquals("Validation error field", "pinCode", response.getBody().getValErrors().get(0).getField());

    }

    @Test
    public void testUpdateUserWithInvalidDOB() throws Exception {
        UserModel request = new UserModel();
        request.setBirthDate("01-ABC-1990");

        when(template.updateFirst(anyObject(), anyObject(), eq(UserDocument.class))).thenReturn(acknowledged(1, 1L, null));

        ResponseEntity<ServiceResponse> response = controller.updateUser("test", request);

        assertEquals("status code", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertTrue("userId", isEmpty(requireNonNull(response.getBody()).getUserId()));
        assertEquals("response message", "Invalid Request", response.getBody().getResMsg());
        assertEquals("validation errors", 1, response.getBody().getValErrors().size());
        assertEquals("Validation error code", "Bad Request", response.getBody().getValErrors().get(0).getCode());
        assertEquals("Validation error message", "Date of birth is not valid", response.getBody().getValErrors().get(0).getMessage());
        assertEquals("Validation error field", "birthDate", response.getBody().getValErrors().get(0).getField());

    }

    @Test
    public void testUpdateUserWithEmptyUidAndInvalidDOB() throws Exception {
        UserModel request = new UserModel();
        request.setBirthDate("01-ABC-1990");

        when(template.updateFirst(anyObject(), anyObject(), eq(UserDocument.class))).thenReturn(acknowledged(1, 1L, null));

        ResponseEntity<ServiceResponse> response = controller.updateUser("", request);

        assertEquals("status code", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertTrue("userId", isEmpty(requireNonNull(response.getBody()).getUserId()));
        assertEquals("response message", "Invalid Request", response.getBody().getResMsg());
        assertEquals("validation errors", 2, response.getBody().getValErrors().size());

    }

    @Test
    public void testUpdateUserUidNotFound() throws Exception {
        UserModel request = new UserModel();
        request.setBirthDate("01-MAR-1990");

        when(template.updateFirst(anyObject(), anyObject(), eq(UserDocument.class))).thenReturn(acknowledged(0, 0L, null));

        ResponseEntity<ServiceResponse> response = controller.updateUser("test", request);

        assertEquals("status code", NOT_FOUND.value(), response.getStatusCodeValue());
        assertEquals("userId", "test", requireNonNull(response.getBody()).getUserId());
        assertEquals("response message", "userId not exists", response.getBody().getResMsg());
        assertEquals("validation errors", 0, response.getBody().getValErrors().size());

    }

    @Test
    public void testUpdateUserAsInactive() {
        when(template.updateFirst(anyObject(), anyObject(), eq(UserDocument.class))).thenReturn(acknowledged(1, 1L, null));

        ResponseEntity<ServiceResponse> response = controller.updateUserAsInactive("test");

        assertEquals("status code", OK.value(), response.getStatusCodeValue());
        assertEquals("userId", "test", requireNonNull(response.getBody()).getUserId());
        assertEquals("response message", "success", response.getBody().getResMsg());
        assertEquals("validation errors", 0, response.getBody().getValErrors().size());
    }

    @Test
    public void testUpdateUserAsInactiveUserNotFound() {
        when(template.updateFirst(anyObject(), anyObject(), eq(UserDocument.class))).thenReturn(acknowledged(0, 1L, null));

        ResponseEntity<ServiceResponse> response = controller.updateUserAsInactive("test");

        assertEquals("status code", NOT_FOUND.value(), response.getStatusCodeValue());
        assertEquals("userId", "test", requireNonNull(response.getBody()).getUserId());
        assertEquals("response message", "userId not exists", response.getBody().getResMsg());
        assertEquals("validation errors", 0, response.getBody().getValErrors().size());
    }

    @Test
    public void testUpdateUserAsInactiveEmptyUser() {
        ResponseEntity<ServiceResponse> response = controller.updateUserAsInactive("");

        assertEquals("status code", BAD_REQUEST.value(), response.getStatusCodeValue());
        assertTrue("userId", isEmpty(requireNonNull(response.getBody()).getUserId()));
        assertEquals("response message", "Invalid Request", response.getBody().getResMsg());
        assertEquals("validation errors", 1, response.getBody().getValErrors().size());
    }


    @Test
    public void testGetUserWhoseDOBInCurrentMonth() {
        when(template.find(anyObject(), eq(UserDocument.class))).thenReturn(Arrays.asList(mockUserDoc(), mockUserDoc()));

        ResponseEntity<List<UserModel>> response = controller.getUsersByBirthDateInCurrentMonth();

        assertEquals("status code", OK.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        for (UserModel request : response.getBody()) {
            TestCase.assertEquals("test@domain.com", request.getEmail());
            TestCase.assertNotNull(request.getUserId());
        }
    }

    @Test
    public void testGetUserWhoseDOBInCurrentMonthNoData() {
        when(template.find(anyObject(), eq(UserDocument.class))).thenReturn(Collections.emptyList());

        ResponseEntity<List<UserModel>> response = controller.getUsersByBirthDateInCurrentMonth();

        assertEquals("status code", OK.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());

    }

    @Test
    public void testGetCountOfUsersByMonthOfBirth() {
        AggregationResults<Map> mockAggregateResults = new AggregationResults<>(getMockMappedResult(), new Document("key", "value"));

        when(template.aggregate(anyObject(), eq(Map.class))).thenReturn(mockAggregateResults);
        ResponseEntity<Map<String, Integer>> response = controller.getDistintCountByMob();

        assertEquals("status code", OK.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(12, response.getBody().size());

        response.getBody().forEach((k, v) -> {
            switch (k) {
                case "January":
                    TestCase.assertEquals("value", 14, v.intValue());
                    break;
                case "February":
                    TestCase.assertEquals("value", 999, v.intValue());
                    break;
                case "December":
                    TestCase.assertEquals("value", 87654, v.intValue());
                    break;
                default:
                    TestCase.assertEquals(0, v.intValue());
                    break;
            }
        });

    }


    private static UserModel mockCreateRequest() {
        UserModel request = new UserModel();
        request.setBirthDate("01-JAN-1990");
        request.setEmail("test@gmail.com");
        request.setfName("testFirstName");
        request.setlName("testLastName");
        request.setPinCode("123456");
        return request;
    }

    private static UserDocument mockUserDoc() {
        UserDocument document = new UserDocument();
        document.setId(new ObjectId());
        document.setDob("01-JAN-1990");
        document.setEmail("test@domain.com");
        document.setFirst("Testfirst");
        document.setLast("Testlast");
        document.setPin(123456);
        document.setUid(randomUUID().toString());
        return document;
    }


    private static List<Map> getMockMappedResult() {
        HashMap<Object, Object> janCount;
        HashMap<Object, Object> febCount;

        HashMap<Object, Object> decCount;

        janCount = new HashMap<>();
        janCount.put("_id", 1);
        janCount.put("count", 14);

        febCount = new HashMap<>();
        febCount.put("_id", 2);
        febCount.put("count", 999);

        decCount = new HashMap<>();
        decCount.put("_id", 12);
        decCount.put("count", 87654);

        return Arrays.asList(janCount, febCount, decCount);
    }

}