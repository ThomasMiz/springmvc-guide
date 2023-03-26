package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.User;

public interface UserDao {
    User createUser(final String email, final String password);
}
