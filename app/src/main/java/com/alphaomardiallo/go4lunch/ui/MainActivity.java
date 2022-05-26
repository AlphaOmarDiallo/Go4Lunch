package com.alphaomardiallo.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.viewModels.MainViewModel;
import com.alphaomardiallo.go4lunch.databinding.ActivityMainBinding;
import com.alphaomardiallo.go4lunch.ui.fragments.ListViewFragment;
import com.alphaomardiallo.go4lunch.ui.fragments.MapsFragment;
import com.alphaomardiallo.go4lunch.ui.fragments.WorkmatesFragment;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ActivityMainBinding binding;
    public MainViewModel viewModel;
    private ActionBarDrawerToggle toggle;
    private Location currentLocation;

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

        setupToolBar();

        setupBottomNavBar();

        setupNavDrawer();

        observeLocation();

        binding.ibLauchAutoComplete.setVisibility(View.INVISIBLE);

        handler.postDelayed(this::checkIfSignedIn, 1000);

    }

    /**
     * UI update methods
     */
    private void setupToolBar() {
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.mainLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void checkIfSignedIn() {
        if (viewModel.isCurrentUserNotLoggedIn()) {
            createSignInIntent();
        } else {
            setupNavigationHeader();
        }
    }

    /**
     * Bottom navigation bar setup
     */
    MapsFragment mapsFragment = new MapsFragment();
    ListViewFragment listViewFragment = new ListViewFragment();
    WorkmatesFragment workmatesFragment = new WorkmatesFragment();

    private void setupBottomNavBar() {
        binding.bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        binding.bottomNavigationView.setSelectedItemId(R.id.menuItemMapView);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemMapView:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, mapsFragment).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.i_m_hungry));
                return true;

            case R.id.menuItemListView:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, listViewFragment).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.i_m_hungry));
                return true;

            case R.id.menuItemWorkmates:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, workmatesFragment).commit();
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.available_workmates));
                return true;
        }
        return false;
    }

    /**
     * Observing current location
     */

    private void observeLocation() {
        viewModel.startTrackingLocation(this, this);
        viewModel.getLocation().observe(this, this::updateLocation);
    }

    private void updateLocation(Location location) {
        currentLocation = location;
    }

    /**
     * SearchBar setup
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.searchActivity).getActionView();
        searchView.setBackgroundColor(getResources().getColor(R.color.white));
        searchView.setIconifiedByDefault(false);
        searchView.getOverlay();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() > 0) {
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    intent.putExtra("Query", s);
                    intent.putExtra("Location", String.format("%s,%s", currentLocation.getLatitude(), currentLocation.getLongitude()));
                    startActivity(intent);
                }
                return true;
            }
        });

        return true;
    }


    /**
     * Navigation drawer setup
     */
    private void setupNavDrawer() {
        toggle = new ActionBarDrawerToggle(this, binding.mainLayout, R.string.Open_drawer_menu, R.string.Close_drawer_menu);
        binding.mainLayout.addDrawerListener(toggle);
        toggle.syncState();
        handleClickNavDrawer();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    private void handleClickNavDrawer() {
        binding.navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.yourLunchNavDrawer:
                    showSnackBar("your lunch");
                    break;
                case R.id.settingsNavDrawer:
                    Intent settingIntent = new Intent(this, SettingsActivity.class);
                    startActivity(settingIntent);
                    break;
                case R.id.logoutNavDrawer:
                    viewModel.signOut(MainActivity.this).addOnSuccessListener(aVoid -> {
                        Log.e(TAG, "onNavigationItemSelected: after logout " + viewModel.getCurrentUser(), null);
                        createSignInIntent();
                    });
                    break;
            }
            return true;
        });
    }

    private void setupNavigationHeader() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        ImageView background = headerView.findViewById(R.id.iVBackgroundHeaderMenu);
        ImageView userAvatar = headerView.findViewById(R.id.iVUserAvatarDrawerMenu);
        TextView userName = headerView.findViewById(R.id.tVUserNameDrawerMenu);
        TextView userEmail = headerView.findViewById(R.id.tVUserEmailDrawerMenu);

        Glide.with(this)
                .load(getString(R.string.nav_drawer_background))
                .into(background);
        Glide.with(this)
                .load(viewModel.getCurrentUser().getPhotoUrl())
                .circleCrop()
                .into(userAvatar);
        userName.setText(viewModel.getCurrentUser().getDisplayName());
        userEmail.setText(viewModel.getCurrentUser().getEmail());
    }

    /**
     * FirebaseUI related methods
     */
    public void createSignInIntent() {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                //Unstable twitter login, follow matter here: https://github.com/firebase/firebase-js-sdk/issues/4256
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
            setupNavigationHeader();
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
            handler.postDelayed(this::checkIfSignedIn, 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Search activity", "onResume: called", null);
        binding.toolbar.collapseActionView();
    }
}