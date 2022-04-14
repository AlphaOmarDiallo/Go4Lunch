package com.alphaomardiallo.go4lunch.data.repositories;

public class UserRepositoryImp implements UserRepository {

    private static volatile UserRepository instance;

    @Override
    public UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository() {
                    @Override
                    public UserRepository getInstance() {
                        return instance = this;
                    }
                };
            }
            return instance;
        }
    }
}
