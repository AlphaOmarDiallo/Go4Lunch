package com.alphaomardiallo.go4lunch.di;

import com.alphaomardiallo.go4lunch.data.repositories.AutocompleteRepository;
import com.alphaomardiallo.go4lunch.data.repositories.AutocompleteRepositoryImp;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public abstract class AutocompleteModule {
    @Binds
    public abstract AutocompleteRepository bindAutoCompleteRepository(AutocompleteRepositoryImp autocompleteRepositoryImp);
}
