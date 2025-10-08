package service;

/**
 * Indicates there was an error connecting to the database
 */
public class ServiceException extends Exception{
    public ServiceException(String message) {
        super(message);
    }
    public ServiceException(String message, Throwable ex) {
        super(message, ex);
    }
}
