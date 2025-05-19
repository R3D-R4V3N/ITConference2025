package exceptions;

public class LokaalNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LokaalNotFoundException(String message) {
        super(message);
    }
}
