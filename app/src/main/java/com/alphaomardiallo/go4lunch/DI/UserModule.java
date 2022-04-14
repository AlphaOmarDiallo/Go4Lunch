package com.alphaomardiallo.go4lunch.DI;

import com.alphaomardiallo.go4lunch.data.repositories.UserRepository;
import com.alphaomardiallo.go4lunch.data.repositories.UserRepositoryImp;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@InstallIn(ViewModelComponent.class)
@Module
public abstract class UserModule {
    @Binds
    public abstract UserRepository bindUserRepository(UserRepositoryImp imp);
}
