package hu.kristof.nagy.hikebookserver;

public class FirestoreInitilizationException extends RuntimeException{
    public FirestoreInitilizationException() {
        super();
    }
    public FirestoreInitilizationException(String msg) {
        super(msg);
    }
}