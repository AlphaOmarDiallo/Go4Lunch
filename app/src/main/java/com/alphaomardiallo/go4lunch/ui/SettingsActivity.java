package com.alphaomardiallo.go4lunch.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragmentCompat;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.alphaomardiallo.go4lunch.data.viewModels.SettingsViewModel;
import com.alphaomardiallo.go4lunch.databinding.SettingsActivityBinding;
import com.bumptech.glide.Glide;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsActivity extends AppCompatActivity {

    private static final String USER_ID = "userID";
    private SettingsActivityBinding binding;
    private SettingsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        setContentView(view);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        getUserData();

        setupToolBar();
    }

    /**
     * Managing user data
     */

    private void getUserData() {
        Intent intent = getIntent();
        String userID = intent.getStringExtra(USER_ID);

        if (userID != null) {
            observeCurrentUser(userID);
        }
    }

    private void observeCurrentUser(String userID) {
        viewModel.getDataBaseInstanceUser();
        viewModel.getCurrentUserDataFromFireStore(userID);
        viewModel.observeCurrentUserFromFireStore().observe(this, this::setupUI);
    }

    /**
     * Setting up preferences fragment
     */

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    /**
     * Toolbar Setup
     */

    private void setupToolBar() {
        setSupportActionBar(binding.toolbarSettings);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        binding.toolbarSettings.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        binding.toolbarSettings.setNavigationOnClickListener(view -> finish());
    }

    /**
     * UI setup
     */

    private void setupUI(User user) {
        if (user.getUrlPicture() != null) {
            Glide.with(getApplicationContext())
                    .load(user.getUrlPicture())
                    .circleCrop()
                    .into(binding.iVUserPictureSettings);
        } else {
            Glide.with(getApplicationContext())
                    .load(getString(R.string.fake_avatar))
                    .circleCrop()
                    .into(binding.iVUserPictureSettings);
        }

        binding.tVUserNameSettings.setText(user.getUsername());

        binding.tVUserPEmailSettings.setText(user.getUserEmail());

        binding.buttonDeleteAccount.setOnClickListener(view -> accountDeletionAlertDialog());
    }

    /**
     * Delete account setup
     */

    private void accountDeletionAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
// Add the buttons
        builder.setPositiveButton(R.string.OK, (dialog, id) -> {
            viewModel.signOut(SettingsActivity.this);
            viewModel.deleteUser(SettingsActivity.this);
            viewModel.deleteUserFromFirestore();
            finish();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
// Set other dialog properties
        builder.setCancelable(true);
        builder.setMessage(R.string.dialog_account_delete)
                .setTitle(R.string.dialog_account_title);

// Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}