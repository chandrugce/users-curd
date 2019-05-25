package com.assignment.users.src.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * DAO mapping to the document that will be inserted to DB
 */
@Document(collection = "users")
public class UserDocument {

    public static final String UID = "uid";
    public static final String DOB = "dob";
    public static final String MOB = "mob";
    public static final String ACT = "act";
    public static final String PIN = "pin";

    @Id
    private ObjectId id;

    @Indexed(unique = true, background = true)
    private String uid;

    private String dob;

    @Indexed(background = true)
    private int mob;

    private String first;

    private String last;

    @Indexed(unique = true, background = true)
    private String email;

    private long pin;

    private boolean act = true;

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }


    public void setMob(int mob) {
        this.mob = mob;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPin() {
        return pin;
    }

    public void setPin(long pin) {
        this.pin = pin;
    }

    public boolean isAct() {
        return act;
    }

    public void setAct(boolean act) {
        this.act = act;
    }
}
