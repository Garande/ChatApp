package com.garande.tech.chatapp.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class User implements Serializable {
    public static String KeyTableName = "/users";

    public String name, email, userId, photoUrl, phoneNumber, countryCode, loginId;

    @Exclude
    public boolean isAuthenticated, isNewUser, isCreated;

    public User() {
    }

    public User(String name, String email, String userId, boolean isAuthenticated) {
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.isAuthenticated = isAuthenticated;
    }
}
