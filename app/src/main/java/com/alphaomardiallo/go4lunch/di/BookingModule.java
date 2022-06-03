package com.alphaomardiallo.go4lunch.di;

import com.alphaomardiallo.go4lunch.data.repositories.BookingRepository;
import com.alphaomardiallo.go4lunch.data.repositories.BookingRepositoryImp;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@InstallIn(ViewModelComponent.class)
@Module
public abstract class BookingModule {
    @Binds
    public abstract BookingRepository bindBookingRepository(BookingRepositoryImp imp);
}
