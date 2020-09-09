package com.garande.tech.chatapp.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.garande.tech.chatapp.model.User;
import com.garande.tech.chatapp.viewModel.SplashViewModel;

public class SplashActivity extends AppCompatActivity {
    SplashViewModel splashViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSplashViewModel();
        verifyUserAuthentication();
    }

    private void verifyUserAuthentication() {
        splashViewModel.verifyUserAuthentication();
        splashViewModel.isUserAuthenticatedLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (!user.isAuthenticated) {
                    goToAuthActivity();
                } else {
                    goToMainActivity();
                }
                finish();
            }
        });
    }

    private void goToMainActivity(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void goToAuthActivity() {
        Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
        startActivity(intent);
    }


    private void initSplashViewModel() {
        splashViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(SplashViewModel.class);
    }
}