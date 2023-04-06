package ar.edu.itba.paw.webapp.exceptions;

public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -4439804381464928244L;

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
