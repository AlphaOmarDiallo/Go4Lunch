package com.alphaomardiallo.go4lunch.di;

import com.alphaomardiallo.go4lunch.data.repositories.PermissionRepository;
import com.alphaomardiallo.go4lunch.data.repositories.PermissionRepositoryImp;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@InstallIn(ViewModelComponent.class)
@Module
public abstract class PermissionModule {
    @Binds
    public abstract PermissionRepository permissionRepository(PermissionRepositoryImp imp);
}
