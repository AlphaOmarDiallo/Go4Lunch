package com.alphaomardiallo.go4lunch.di;

import com.alphaomardiallo.go4lunch.data.repositories.PlacesAPIRepositoryImp;
import com.alphaomardiallo.go4lunch.data.repositories.PlacesAPIRepository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@InstallIn(ViewModelComponent.class)
@Module
public abstract class PlacesAPIModule {
    @Binds
    public abstract PlacesAPIRepository bindAPIRepository(PlacesAPIRepositoryImp imp);
}
