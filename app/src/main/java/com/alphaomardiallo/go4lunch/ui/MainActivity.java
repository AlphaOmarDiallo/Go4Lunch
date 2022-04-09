package com.alphaomardiallo.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.databinding.ActivityMainBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_ONE_TAP = 9001;  // Can be any integer unique to the Activity.
    private static final int REC_SIGN_IN = 120;
    private boolean showOneTapUI = true;
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setupBackgroundImage();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            Log.e(TAG, "onCreate: user " + user, null);
            //send to activity
        } else {
            Log.e(TAG, "onCreate: user is null", null);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id_google))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.bGoogleSignIn.setOnClickListener(view1 -> signInGoogle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInClient oneTapClient = null;
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        Log.d(TAG, "Got ID token.");
                    }
                } catch (ApiException e) {
                    e.getStatus();
                }
                break;
        }
    }

    private void signInGoogle() {
        Intent signInGoogleIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInGoogleIntent, REC_SIGN_IN);
    }

    private void setupBackgroundImage() {
        Glide.with(this)
                .load("https://images.pexels.com/photos/3184188/pexels-photo-3184188.jpeg?cs=srgb&dl=pexels-fauxels-3184188.jpg&fm=jpg")
                .into(binding.ivBackgroundLogin);
    }
}