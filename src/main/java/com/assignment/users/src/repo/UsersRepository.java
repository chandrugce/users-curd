package com.assignment.users.src.repo;

import com.assignment.users.src.models.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Interface to access mongo operations
 */
public interface UsersRepository extends MongoRepository<UserDocument, String> {


}
