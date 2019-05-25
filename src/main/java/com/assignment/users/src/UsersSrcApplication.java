package com.assignment.users.src;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class UsersSrcApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersSrcApplication.class, args);
    }

}
