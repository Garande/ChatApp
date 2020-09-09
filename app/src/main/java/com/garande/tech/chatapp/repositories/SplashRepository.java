package com.garande.tech.chatapp.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.garande.tech.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashRepository {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference userRef = FirebaseFirestore.getInstance().collection(User.KeyTableName);
    private User user = new User();

    public MutableLiveData<User> verifyUserAuthentication() {
        MutableLiveData<User> isUserAuthenticatedMutableLiveData = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            user.isAuthenticated = false;
        }else {
            user.isAuthenticated = true;
        }
        isUserAuthenticatedMutableLiveData.setValue(user);
        return isUserAuthenticatedMutableLiveData;
    }
}
