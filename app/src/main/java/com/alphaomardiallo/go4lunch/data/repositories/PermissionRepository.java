package com.alphaomardiallo.go4lunch.data.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;

public interface PermissionRepository {

    boolean hasLocationPermissions(Context context);

    LiveData<Boolean> liveDataHasLocationPermission(Context context);
}
