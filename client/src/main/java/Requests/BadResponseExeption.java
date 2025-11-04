package Requests;

public class BadResponseExeption extends RuntimeException {
    public BadResponseExeption(String message) {
        super(message);
    }
}
