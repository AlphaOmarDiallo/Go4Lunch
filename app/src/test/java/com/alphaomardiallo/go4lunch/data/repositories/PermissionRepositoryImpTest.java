package com.alphaomardiallo.go4lunch.data.repositories;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.content.Context;

import androidx.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Test;

public class PermissionRepositoryImpTest {

    PermissionRepositoryImp mockPermissionRepositoryImp = mock(PermissionRepositoryImp.class);
    PermissionRepositoryImp permissionRepositoryImp = new PermissionRepositoryImp();
    LiveData<Boolean> hasPermissionLiveData;
    boolean hasPermission;
    Context context = mock(Context.class);

    @Before
    public void inti() {
    }

    @Test
    public void hasLocationPermissions() {
        assertThat(mockPermissionRepositoryImp.hasPermissions).isEqualTo(null);
        verify(mockPermissionRepositoryImp.hasPermissions);
    }

    @Test
    public void liveDataHasLocationPermission() {
        assertThat(mockPermissionRepositoryImp.liveDataHasLocationPermission(any())).isNull();
    }
}