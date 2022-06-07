package com.alphaomardiallo.go4lunch.ui;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.alphaomardiallo.go4lunch.data.viewModels.MainSharedViewModel;
import com.alphaomardiallo.go4lunch.databinding.ActivityMainBinding;
import com.alphaomardiallo.go4lunch.domain.PermissionUtils;
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
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_PERMISSIONS_LOCATION = 567;
    private static final String KEY_SELECTED_RESTAURANT_ID = "placeID";
    private static final String KEY_SELECTED_RESTAURANT_NAME = "placeName";
    private static final String KEY_SEARCH_QUERY = "Query";
    private static final String KEY_LOCATION_STRING = "Location";
    private static final PermissionUtils permission = new PermissionUtils();
    private static final String USER_ID = "userID";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final MapsFragment mapsFragment = new MapsFragment();
    private final ListViewFragment listViewFragment = new ListViewFragment();
    private final WorkmatesFragment workmatesFragment = new WorkmatesFragment();
    private User currentUser;
    private ActivityMainBinding binding;
    private ActionBarDrawerToggle toggle;
    private Location currentLocation;
    private SearchView searchView;
    private MainSharedViewModel viewModel;

    /**
     * Contracts to start activities
     */
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private final ActivityResultLauncher<Intent> searchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent returnedIntent = result.getData();
                        if (returnedIntent != null) {
                            String selectedRestaurantID = returnedIntent.getStringExtra(KEY_SELECTED_RESTAURANT_ID);
                            String selectedRestaurantName = returnedIntent.getStringExtra(KEY_SELECTED_RESTAURANT_NAME);
                            viewModel.setIdRestaurantToFocusOn(selectedRestaurantID);
                            showSnackBarMessage(String.format(getString(R.string.message_activity_result_ok), selectedRestaurantName));
                        }
                    } else {
                        showSnackBarMessage(getString(R.string.message_activity_result_not_ok));
                    }
                }
            }
    );

    /**
     * OnCreate
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate: Called");
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(MainSharedViewModel.class);
        setContentView(view);

        setupToolBar();

        setupBottomNavBar();

        setupNavDrawer();

        observePermissions();

        observeBookings();

    }

    /**
     * UI update methods
     */
    private void setupToolBar() {
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(binding.mainLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void checkIfUserIsSignedIn() {
        if (viewModel.isCurrentUserNotLoggedIn()) {
            createSignInIntent();
        } else {
            observeUsers();
        }
    }

    /**
     * Bottom navigation bar setup
     */

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
     * Observing users
     */

    private void observeUsers() {
        viewModel.getDataBaseInstanceUser();
        viewModel.getAllUsersFromDatabase();
        viewModel.getCurrentUserDataFromFireStore(viewModel.getCurrentUser().getUid());
        viewModel.observeCurrentUser().observe(this, this::setupNavigationHeader);
    }

    /**
     * Observing bookings
     */

    private void observeBookings() {
        viewModel.getDatabaseInstanceBooking();
        viewModel.observeBookingsFromDataBase();
    }

    /**
     * Observing current location
     */

    private void observeLocation() {
        viewModel.startTrackingLocation(this, this);
        viewModel.getCurrentLocation().observe(this, this::updateLocationAndFetchRestaurantList);
    }

    private void updateLocationAndFetchRestaurantList(Location location) {
        currentLocation = location;
        viewModel.getAllRestaurantList(this, currentLocation);
    }

    /**
     * SearchBar setup
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        searchView = (SearchView) menu.findItem(R.id.searchActivity).getActionView();
        searchView.setBackgroundColor(getResources().getColor(R.color.white));
        searchView.setGravity(Gravity.START);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.search_query_hint));
        searchView.getOverlay();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.length() > 0) {
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    intent.putExtra(KEY_SEARCH_QUERY, query);
                    intent.putExtra(KEY_LOCATION_STRING, String.format(getString(R.string.location_to_string), currentLocation.getLatitude(), currentLocation.getLongitude()));
                    searchLauncher.launch(intent);
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
                    //TODO implement your lunch activity
                    showSnackBarMessage("your lunch");
                    break;
                case R.id.settingsNavDrawer:
                    Intent settingIntent = new Intent(this, SettingsActivity.class);
                    settingIntent.putExtra(USER_ID, currentUser.getUid());
                    startActivity(settingIntent);
                    break;
                case R.id.logoutNavDrawer:
                    viewModel.signOut(MainActivity.this).addOnSuccessListener(aVoid -> createSignInIntent());
                    break;
            }
            return true;
        });
    }

    private void setupNavigationHeader(User user) {
        currentUser = user;

        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        ImageView background = headerView.findViewById(R.id.iVBackgroundHeaderMenu);
        ImageView userAvatar = headerView.findViewById(R.id.iVUserAvatarDrawerMenu);
        TextView userName = headerView.findViewById(R.id.tVUserNameDrawerMenu);
        TextView userEmail = headerView.findViewById(R.id.tVUserEmailDrawerMenu);

        Glide.with(this)
                .load(getString(R.string.nav_drawer_background))
                .into(background);
        if (user.getUrlPicture() != null) {
            Glide.with(this)
                    .load(viewModel.getCurrentUser().getPhotoUrl())
                    .circleCrop()
                    .into(userAvatar);
        } else {
            Glide.with(this)
                    .load(getString(R.string.fake_avatar))
                    .circleCrop()
                    .into(userAvatar);
        }

        userName.setText(user.getUsername());
        userEmail.setText(user.getUserEmail());
    }

    /**
     * FirebaseUI related methods
     */
    public void createSignInIntent() {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                // Unstable twitter login, follow matter here: https://github.com/firebase/firebase-js-sdk/issues/4256
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
            viewModel.createUserInDataBase();
            observeUsers();
            showSnackBarMessage(getString(R.string.connection_succeed));
        } else {
            if (response == null) {
                showSnackBarMessage(getString(R.string.error_authentication_canceled));
            } else if (response.getError() != null) {
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBarMessage(getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBarMessage(getString(R.string.error_unknown_error));
                }
            }
            handler.postDelayed(this::checkIfUserIsSignedIn, 1000);
        }
    }

    /**
     * Permission handling
     */

    private void observePermissions() {
        viewModel.liveDataHasPermission(this).observe(this, this::setupAppAccordingToPermissions);
    }

    private void setupAppAccordingToPermissions(Boolean hasPermissions) {
        if (hasPermissions) {
            viewModel.permissionSet(true);
            observeLocation();
        } else {
            viewModel.permissionSet(false);
            requestPermission();
        }
    }

    public void requestPermission() {
        if (permission.hasLocationPermissions(this)) {
            return;
        }

        EasyPermissions.requestPermissions(
                this,
                "This app needs location permission to function properly",
                REQUEST_PERMISSIONS_LOCATION,
                ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        );
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        viewModel.permissionSet(true);
        observeLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            requestPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * LifeCycle related methods
     */

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume: called");
        super.onResume();

        handler.postDelayed(this::checkIfUserIsSignedIn, 1000);

        if (searchView != null) {
            searchView.setQuery(getString(R.string.remove_query), false);
        }
        binding.toolbar.collapseActionView();
    }
}