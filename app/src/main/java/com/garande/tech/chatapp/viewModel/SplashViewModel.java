package com.garande.tech.chatapp.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.garande.tech.chatapp.model.User;
import com.garande.tech.chatapp.repositories.SplashRepository;


public class SplashViewModel extends AndroidViewModel {
    private SplashRepository splashRepository;
    public LiveData<User> isUserAuthenticatedLiveData;

    public SplashViewModel(Application application) {
        super(application);
        splashRepository = new SplashRepository();
    }

    public void verifyUserAuthentication(){
        isUserAuthenticatedLiveData = splashRepository.verifyUserAuthentication();
    }

}
