package com.alphaomardiallo.go4lunch.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.viewModels.MainViewModel;
import com.alphaomardiallo.go4lunch.databinding.ActivityMainBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    public MainViewModel viewModel;

    /**
     * setup to get back data from FirebaseUI activity if sign in needed
     */
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setContentView(view);

        binding.bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        binding.bottomNavigationView.setSelectedItemId(R.id.menuItemMapView);

        checkIfSignedIn();
    }

    /**
     * UI update methods
     */

    private void showSnackBar(String message) {
        Snackbar.make(binding.mainLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void checkIfSignedIn() {
        if (viewModel.isCurrentUserNotLoggedIn()) {
            createSignInIntent();
        } else {
            String userName = getString(R.string.welcome_back, viewModel.getCurrentUser().getDisplayName());
            showSnackBar(userName);
        }
    }

    /**
     * Bottom navigation bar setup
     */

    MapViewFragment mapViewFragment = new MapViewFragment();
    ListViewFragment listViewFragment = new ListViewFragment();
    WorkmatesFragment workmatesFragment = new WorkmatesFragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemMapView:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, mapViewFragment).commit();
                return true;

            case R.id.menuItemListView:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, listViewFragment).commit();
                return true;

            case R.id.menuItemWorkmates:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, workmatesFragment).commit();
                return true;
        }
        return false;
    }

    /**
     * Navigation drawer setup
     */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * FirebaseUI related methods
     */
    public void createSignInIntent() {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true)
                .setLockOrientation(true)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            showSnackBar(getString(R.string.connection_succeed));
        } else {
            if (response == null) {
                showSnackBar(getString(R.string.error_authentication_canceled));
            } else if (response.getError() != null) {
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(getString(R.string.error_unknown_error));
                }
            }
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(this::checkIfSignedIn, 1000);
        }
    }
}