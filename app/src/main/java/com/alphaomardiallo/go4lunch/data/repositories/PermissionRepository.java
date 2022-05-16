package com.alphaomardiallo.go4lunch.data.repositories;

import android.content.Context;

public interface PermissionRepository {

    boolean hasLocationPermissions(Context context);
}
