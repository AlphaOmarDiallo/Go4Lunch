package com.alphaomardiallo.go4lunch.data.repositories;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Test;

public class PermissionRepositoryImpTest {

    PermissionRepositoryImp permissionRepositoryImp = new PermissionRepositoryImp();

    @Test
    public void hasLocationPermissions() {
        when(permissionRepositoryImp.hasPermissions.getValue()).thenReturn(true);
    }

    @Test
    public void liveDataHasLocationPermission() {
    }
}