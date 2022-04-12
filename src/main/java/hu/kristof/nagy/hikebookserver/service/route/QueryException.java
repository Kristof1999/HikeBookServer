package hu.kristof.nagy.hikebookserver.service.route;

public class QueryException extends RuntimeException {
    public QueryException() {
    }

    public QueryException(String message) {
        super(message);
    }
}
