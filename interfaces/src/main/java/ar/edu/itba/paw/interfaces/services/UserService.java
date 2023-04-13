package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserService {
    User create(final String email, final String password);

    Optional<User> findById(long userId);
}
