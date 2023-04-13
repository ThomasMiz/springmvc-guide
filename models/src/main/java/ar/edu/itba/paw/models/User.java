package ar.edu.itba.paw.models;

public class User {

    private final long userId;
    private final String email;
    private String password;

    public User(final long userId, final String email, final String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
