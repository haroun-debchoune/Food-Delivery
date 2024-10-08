package com.example.fooddelivery.viewmodel;

import static androidx.fragment.app.FragmentManager.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fooddelivery.helper.Constants;
import com.example.fooddelivery.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class UserViewModel extends ViewModel {
    private FirebaseAuth auth;
    private MutableLiveData<Boolean> isSignUpSuccessfully = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isLogged = new MutableLiveData<>();
    private MutableLiveData<Boolean> isEmailAlreadyInUse = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isSignedIn = new MutableLiveData<>();
    private MutableLiveData<Boolean> isUserInfoAdded = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isResetPasswordSent = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollection = db.collection(Constants.USER_COLLECTION);

    public UserViewModel() {
        auth = FirebaseAuth.getInstance();
    }

    public LiveData<Boolean> getIsSignUpSuccessfully() {
        return isSignUpSuccessfully;
    }
    public LiveData<Boolean> getIsResetPasswordSent(){
        return isResetPasswordSent;
    }
    public LiveData<Boolean> getIsUserInfoAdded(){
        return isUserInfoAdded;
    }

    public LiveData<Boolean> getIsLogged() {
        return isLogged;
    }

    public LiveData<Boolean> getIsEmailAlreadyInUse() {
        return isEmailAlreadyInUse;
    }

    public void SignUpWithEmailAndPassword(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = new User(auth.getCurrentUser().getUid(), email,password,"","","","");
                userCollection.document(user.getId())
                        .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                isSignUpSuccessfully.setValue(true);
                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        auth.signOut();
                                    }

                        }).addOnFailureListener( e -> {

                        });}



                }).addOnFailureListener(e -> {
                    Log.d("TAG1", e.getMessage().toString());
                });
            }
        }).addOnFailureListener(e -> {
            if (e instanceof FirebaseAuthUserCollisionException) {
                isEmailAlreadyInUse.setValue(true);
            } else {
                Log.d("TAG2", e.getMessage());
            }
        });
    }

    public void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task2 -> {
            if (task2.isSuccessful()) {
                isLogged.setValue(true);
            }
        }).addOnFailureListener(e -> {
            Log.d("TAG3", e.getMessage().toString());
            isLogged.setValue(false);
        });
    }

    public void checkEmailVerification(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task2 -> {
            if (task2.isSuccessful()) {
                auth.getCurrentUser().reload().addOnCompleteListener(task3 -> {
                    if (task3.isSuccessful()) {
                        if (auth.getCurrentUser().isEmailVerified()) {
                            isSignedIn.setValue(true);
                        } else {
                            isSignedIn.setValue(false);
                            auth.signOut();
                            Log.d("TAG3", "Email not verified.");
                        }
                    }
                });
            } else {
                Log.d("TAG3", "Sign in failed.");
            }
        }).addOnFailureListener(e -> {
            Log.d("TAG2", e.getMessage());
            isSignedIn.setValue(false);
        });
    }

    public void forgotPassword(String email){
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        isResetPasswordSent.setValue(true);
                    }
                }).addOnFailureListener( e -> {
                    isResetPasswordSent.setValue(false);
                });
    }

    public void registerUserInfo(User user){


    }
    public LiveData<Boolean> getIsSignedIn() {
        return isSignedIn;
    }

}
