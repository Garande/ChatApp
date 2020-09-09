package com.garande.tech.chatapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.garande.tech.chatapp.model.User;
import com.garande.tech.chatapp.repositories.AuthRepository;
import com.google.firebase.auth.AuthCredential;

public class AuthViewModel extends AndroidViewModel {
    private AuthRepository authRepository;

    public LiveData<User> authenticatedUserLiveData;
    public LiveData<User> createdUserLiveData;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
    }

    public void authenticateUserWithPhoneNumber(AuthCredential credential){
        authenticatedUserLiveData = authRepository.authenticateUserWithCredentials(credential);
    }

    public void createUser(User user) {
        createdUserLiveData = authRepository.createUser(user);
    }
}
