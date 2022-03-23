package hu.kristof.nagy.hikebookserver.data;

public class FirestoreInitilizationException extends RuntimeException {
    public FirestoreInitilizationException() {}

    public FirestoreInitilizationException(String message) {
        super(message);
    }
}
