package com.garande.tech.chatapp.views.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.garande.tech.chatapp.R;
import com.garande.tech.chatapp.model.User;
import com.garande.tech.chatapp.util.Constants;
import com.garande.tech.chatapp.viewModel.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneNumberField;
    TextView phoneNumberText;
    PinView pinView;

    LinearLayout phoneNumberLayout, pinEntryLayout;

    private AuthViewModel authViewModel;


    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        initViews();
        initViewModel();
        initGoogleSignInClient();
        initPhoneAuthCallbacks();
    }


    private void signInWithAuthCredentials(AuthCredential credential){
        authViewModel.authenticateUserWithPhoneNumber(credential);
        authViewModel.authenticatedUserLiveData.observe(this, user -> {
            if(user.isNewUser) createNewUser(user);
            else goToMainActivity(user);
        });
    }

    private void goToMainActivity(User user) {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        intent.putExtra(Constants.Extras.USER, user);
        startActivity(intent);
        finish();
    }

    private void createNewUser(User user) {
        user.phoneNumber = countryCodePicker.getFullNumberWithPlus();
        user.countryCode = countryCodePicker.getSelectedCountryCode();
        user.name = googleSignInAccount.getDisplayName();
        user.email = googleSignInAccount.getEmail();
        user.photoUrl = googleSignInAccount.getPhotoUrl().toString();
        user.userId = generateUserId();

        authViewModel.createUser(user);
        authViewModel.createdUserLiveData.observe(this, createdUser -> {
            if(createdUser.isCreated){
                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
            }
            goToMainActivity(createdUser);
        });

    }

    private String generateUserId() {
        String fullNumber = countryCodePicker.getFormattedFullNumber();
        String countryCode = countryCodePicker.getSelectedCountryCode();
        String shortNumber = fullNumber.substring(countryCode.length());
        String countryCodeName = countryCodePicker.getSelectedCountryNameCode();
        String id = countryCodeName + shortNumber;
        return id.replaceAll(" ", "");
    }

    private void initPhoneAuthCallbacks() {
        phoneAuthCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                pinView.setText(phoneAuthCredential.getSmsCode());
                signInWithAuthCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e(AuthActivity.class.getSimpleName(), null, e);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mVerificationId = verificationId;
                token = forceResendingToken;
            }
        };
    }

    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(AuthActivity.this, googleSignInOptions);
    }


    private void initViewModel(){
        authViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AuthViewModel.class);
    }


    private void initViews() {
        countryCodePicker = (CountryCodePicker)findViewById(R.id.countryCodePicker);
        phoneNumberField = (EditText)findViewById(R.id.userPhoneNumber);
        phoneNumberText = (TextView)findViewById(R.id.phoneNumberText);
        pinView = (PinView)findViewById(R.id.pinView);

        phoneNumberLayout = (LinearLayout)findViewById(R.id.phoneNumberView);
        pinEntryLayout = (LinearLayout)findViewById(R.id.pinEntryView);

        countryCodePicker.registerCarrierNumberEditText(phoneNumberField);
    }

    public void handleLogin(View view) {
        if(phoneNumberField.getText().length() >= 8){
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, Constants.PermissionCodes.GOOGLE_SIGN_IN_CODE);
        }
    }

    public void submitOtp(View view) {
        String verificationCode = pinView.getText().toString();
        if (verificationCode.isEmpty()){
            Toast.makeText(this, "Enter a valid code", Toast.LENGTH_SHORT).show();
        }else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
            signInWithAuthCredentials(credential);
        }
    }

    private void getGoogleAuthCredentials(GoogleSignInAccount userGoogleAccount) {
        googleSignInAccount = userGoogleAccount;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                countryCodePicker.getFullNumberWithPlus(),
                60,
                TimeUnit.SECONDS,
                AuthActivity.this,
                phoneAuthCallbacks
        );

        phoneNumberLayout.setVisibility(View.GONE);
        phoneNumberText.setText(countryCodePicker.getFullNumberWithPlus());
        pinEntryLayout.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.PermissionCodes.GOOGLE_SIGN_IN_CODE){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount userGoogleAccount = task.getResult(ApiException.class);
                if(userGoogleAccount != null){
                    getGoogleAuthCredentials(userGoogleAccount);
                }

            }catch (ApiException e){
                Log.e(AuthActivity.class.getSimpleName(), null, e);
            }
        }
    }


}