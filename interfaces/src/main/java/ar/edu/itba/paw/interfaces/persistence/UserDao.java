package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserDao {
    User createUser(final String email, final String password);

    Optional<User> findById(long id);
}
