package com.garande.tech.chatapp.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.garande.tech.chatapp.model.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthRepository {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = dbRef.collection(User.KeyTableName);

    public MutableLiveData<User> authenticateUserWithCredentials(AuthCredential credential) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(authTask -> {
            if(authTask.isSuccessful()){
                boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null){
                    User user = new User();
                    user.isNewUser = isNewUser;
                    user.loginId = firebaseUser.getUid();
                    authenticatedUserMutableLiveData.setValue(user);
                }
            }else {
                Log.e(AuthRepository.class.getSimpleName(), null, authTask.getException());
            }
        });
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<User> createUser(User user) {
        MutableLiveData<User> newCreatedUserMutableLiveData = new MutableLiveData<>();
        DocumentReference documentReference = usersRef.document(user.userId);
        documentReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                if(!documentSnapshot.exists()){
                    documentReference.set(user).addOnCompleteListener(createdUserTask -> {
                        if(createdUserTask.isSuccessful()){
                            user.isCreated = true;
                            newCreatedUserMutableLiveData.setValue(user);
                        }else {
                            Log.e(AuthRepository.class.getSimpleName(), null, createdUserTask.getException());
                        }
                    });
                }else {
                    newCreatedUserMutableLiveData.setValue(user);
                }
            }else {
                Log.e(AuthRepository.class.getSimpleName(), null, task.getException());
            }
        });
        return newCreatedUserMutableLiveData;
    }
}
