package com.assignment.users.src.controllers;

import com.assignment.users.src.helper.DistinctMobCountUtil;
import com.assignment.users.src.models.ServiceResponse;
import com.assignment.users.src.models.UserDocument;
import com.assignment.users.src.models.UserModel;
import com.assignment.users.src.models.ValidationError;
import com.assignment.users.src.repo.UsersRepository;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.assignment.users.src.models.UserDocument.*;
import static java.lang.Integer.valueOf;
import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.util.StringUtils.isEmpty;

@SuppressWarnings("deprecation")
@RestController(value = "/users")
public class UserController {

    private final UsersRepository mongoRepo;
    private final MongoTemplate mongoTemplate;

    private static final String MEDIA_TYPE_APP_JSON = "application/json";
    private ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("dd-MMM-yyyy"));

    @Autowired
    public UserController(UsersRepository repo, MongoTemplate mongoTemplate) {
        this.mongoRepo = repo;
        this.mongoTemplate = mongoTemplate;
    }


    @PostMapping(consumes = MEDIA_TYPE_APP_JSON, produces = MEDIA_TYPE_APP_JSON)
    public ResponseEntity<ServiceResponse> createUser(@RequestBody UserModel request) throws Exception {

        List<ValidationError> validationErrors = validateRequest(request);
        if (validationErrors.size() > 0) {
            ServiceResponse response = new ServiceResponse("Invalid Request", null, validationErrors);
            return new ResponseEntity<>(response, BAD_REQUEST);
        }

        UserDocument document = new UserDocument();
        document.setDob(request.getBirthDate());
        document.setEmail(request.getEmail().toLowerCase()); //normalize email to lowercase
        document.setFirst(request.getfName());
        document.setLast(request.getlName());
        document.setPin(valueOf(request.getPinCode()));
        document.setUid(randomUUID().toString());
        document.setMob(dateFormatThreadLocal.get().parse(request.getBirthDate()).getMonth() + 1);
        document.setAct(true);

        UserDocument inserted;
        try {
            inserted = mongoRepo.insert(document);
        } catch (DuplicateKeyException dupeEx) {
            ServiceResponse response = new ServiceResponse("User already exists", "", Collections.emptyList());
            return new ResponseEntity<>(response, PRECONDITION_FAILED);
        } catch (Exception e) {
            ServiceResponse response = new ServiceResponse("Unexpected error, message:" + e.getMessage(), "", Collections.emptyList());
            return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR);
        }
        ServiceResponse response = new ServiceResponse("success", inserted.getUid(), Collections.emptyList());
        return new ResponseEntity<>(response, CREATED);
    }

    @PutMapping(value = "/users/{uid}", consumes = MEDIA_TYPE_APP_JSON, produces = MEDIA_TYPE_APP_JSON)
    public ResponseEntity<ServiceResponse> updateUser(@PathVariable(value = "uid") String uid, @RequestBody UserModel request) throws Exception {

        List<ValidationError> errors = validateUpdateRequest(uid, request);
        if (errors.size() > 0) {
            ServiceResponse response = new ServiceResponse("Invalid Request", null, errors);
            return new ResponseEntity<>(response, BAD_REQUEST);
        }

        Query find = new Query(Criteria.where(UID).is(uid));
        Update update = new Update();
        if (!isEmpty(request.getPinCode())) {
            update.set(PIN, valueOf(request.getPinCode()));
        }

        if (!isEmpty(request.getBirthDate())) {
            update.set(DOB, request.getBirthDate());
            update.set(MOB, dateFormatThreadLocal.get().parse(request.getBirthDate()).getMonth() + 1);
        }

        return updateAndGetResponse(uid, find, update);
    }

    @DeleteMapping(value = "/users/{uid}", produces = MEDIA_TYPE_APP_JSON)
    public ResponseEntity<ServiceResponse> updateUserAsInactive(@PathVariable(value = "uid") String uid) {
        if (isEmpty(uid)) {
            ServiceResponse response = new ServiceResponse("Invalid Request", null,
                    singletonList(new ValidationError(BAD_REQUEST.getReasonPhrase(), "UserId", "User is empty")));
            return new ResponseEntity<>(response, BAD_REQUEST);
        }

        Query find = new Query(Criteria.where(UID).is(uid));
        Update update = new Update();
        update.set(ACT, false);
        return updateAndGetResponse(uid, find, update);
    }

    @GetMapping(value = "/users/dob/current-month", produces = MEDIA_TYPE_APP_JSON)
    public ResponseEntity<List<UserModel>> getUsersByBirthDateInCurrentMonth() {
        Query find = new Query(Criteria.where(MOB).is(new Date().getMonth() + 1));
        List<UserDocument> userDocuments = mongoTemplate.find(find, UserDocument.class);
        return new ResponseEntity<>(mapToUserModel(userDocuments), OK);
    }

    @GetMapping(value = "/users/dob/month/distinct-count", produces = MEDIA_TYPE_APP_JSON)
    public ResponseEntity<Map<String, Integer>> getDistintCountByMob() {
        Field field = Fields.field(MOB, "mob");

        GroupOperation groupOp = new GroupOperation(Fields.from(field)).count().as("count");
        TypedAggregation aggregate = new TypedAggregation<>(UserDocument.class, groupOp);
        List<Map> results = mongoTemplate.aggregate(aggregate, Map.class).getMappedResults();
        Map<String, Integer> mobCountResults = DistinctMobCountUtil.parse(results);

        return new ResponseEntity<>(mobCountResults, OK);
    }

    private List<UserModel> mapToUserModel(List<UserDocument> userDocuments) {
        List<UserModel> requests = new ArrayList<>();

        userDocuments.stream().filter(UserDocument::isAct).forEach(doc -> {
            UserModel request = new UserModel();
            request.setBirthDate(doc.getDob());
            request.setPinCode(String.valueOf(doc.getPin()));
            request.setEmail(doc.getEmail());
            request.setfName(doc.getFirst());
            request.setlName(doc.getLast());
            request.setUserId(doc.getUid());
            requests.add(request);
        });

        return requests;
    }

    private ResponseEntity<ServiceResponse> updateAndGetResponse(String uid, Query find, Update update) {
        UpdateResult updateResult = mongoTemplate.updateFirst(find, update, UserDocument.class);
        if (updateResult.getMatchedCount() == 0) {
            ServiceResponse response = new ServiceResponse("userId not exists", uid, Collections.emptyList());
            return new ResponseEntity<>(response, NOT_FOUND);
        }

        ServiceResponse response = new ServiceResponse("success", uid, Collections.emptyList());
        return new ResponseEntity<>(response, OK);
    }


    private List<ValidationError> validateUpdateRequest(@PathVariable String uid, @RequestBody UserModel request) {
        List<ValidationError> valError = new ArrayList<>();
        if (isEmpty(uid)) {
            valError.add(new ValidationError(BAD_REQUEST.getReasonPhrase(), "userId", "userId is empty"));
        }

        if (isEmpty(request.getPinCode()) && isEmpty(request.getBirthDate())) {
            valError.add(new ValidationError(BAD_REQUEST.getReasonPhrase(), "pinCode, birthDate", "PinCode or DOB required for update"));
        }

        if (!isEmpty(request.getPinCode())) {
            validatePinCode(request, valError);
        }

        if (!isEmpty(request.getBirthDate())) {
            validateDOB(request, valError);
        }
        return valError;
    }

    private List<ValidationError> validateRequest(UserModel request) {
        List<ValidationError> result = new ArrayList<>();
        if (isEmpty(request.getfName())) {
            result.add(new ValidationError(BAD_REQUEST.getReasonPhrase(), "fName", "First Name is empty"));
        }

        if (isEmpty(request.getlName())) {
            result.add(new ValidationError(BAD_REQUEST.getReasonPhrase(), "lName", "Last Name is empty"));
        }

        if (isEmpty(request.getEmail()) || !request.getEmail().contains("@")) {
            result.add(new ValidationError(BAD_REQUEST.getReasonPhrase(), "email", "Email is not valid"));
        }

        validatePinCode(request, result);

        validateDOB(request, result);
        return result;

    }

    private void validateDOB(UserModel request, List<ValidationError> result) {
        String birthDate = request.getBirthDate();
        if (isEmpty(birthDate)) {
            result.add(new ValidationError(BAD_REQUEST.getReasonPhrase(), "birthDate", "Date of birth should not be empty"));
        }

        if (!isEmpty(birthDate)) {
            ValidationError dobValError = new ValidationError(BAD_REQUEST.getReasonPhrase(), "birthDate", "Date of birth is not valid");
            try {
                Date dob = dateFormatThreadLocal.get().parse(birthDate);
                if (dob.getYear() > new Date().getYear()) {
                    result.add(dobValError);
                }
                if (!birthDate.contains("-") || birthDate.split("-").length != 3 || valueOf(birthDate.split("-")[0]) > 31) {
                    result.add(dobValError);
                }
            } catch (ParseException e) {
                result.add(dobValError);
            }
        }
    }

    private void validatePinCode(UserModel request, List<ValidationError> result) {
        if (isEmpty(request.getPinCode())) {
            result.add(new ValidationError(BAD_REQUEST.getReasonPhrase(), "pinCode", "Pin code is empty"));
        }

        if (!isEmpty(request.getPinCode())) {
            try {
                valueOf(request.getPinCode());
            } catch (Exception e) {
                result.add(new ValidationError(BAD_REQUEST.getReasonPhrase(), "pinCode", "Pin Code is not valid"));
            }
        }
    }


}
