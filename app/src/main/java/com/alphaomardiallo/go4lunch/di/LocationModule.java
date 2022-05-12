package com.alphaomardiallo.go4lunch.di;

import com.alphaomardiallo.go4lunch.data.repositories.LocationRepository;
import com.alphaomardiallo.go4lunch.data.repositories.LocationRepositoryImp;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@InstallIn(ViewModelComponent.class)
@Module
public abstract class LocationModule {
    @Binds
    public abstract LocationRepository bindLocationRepository (LocationRepositoryImp imp);

}
