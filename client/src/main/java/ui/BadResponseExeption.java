package ui;

public class BadResponseExeption extends RuntimeException {
    public BadResponseExeption(String message) {
        super(message);
    }
    public BadResponseExeption(String message, Throwable ex) {
        super(message, ex);
    }
}
