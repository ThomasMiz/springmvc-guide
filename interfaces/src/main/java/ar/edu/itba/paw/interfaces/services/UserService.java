package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User create(final String email, final String password);

    Optional<User> findById(long userId);

    Optional<User> findByEmail(String email);

    List<User> getAll(int pageNumber, int pageSize);

    void changePassword(String email, String password);

    void sendWelcomeEmail(String email);

    void someScheduledOperation();
}
