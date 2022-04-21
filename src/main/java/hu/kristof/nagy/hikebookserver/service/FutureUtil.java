package hu.kristof.nagy.hikebookserver.service;

import java.util.concurrent.ExecutionException;

public class FutureUtil {
    /**
     * Executes the given lambda, and catches
     * ExecutionException and InterruptedException exceptions,
     * and throws a generic error message for the client.
     * @param f the lambda to execute
     * @return the result of the lambda
     */
    public static <P> P handleFutureGet(FutureGet<P> f) {
        // TODO: refactor using Supplier
        try {
            return f.getFuture();
        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalArgumentException(Util.GENERIC_ERROR_MSG);
        }
    }

    @FunctionalInterface
    public interface FutureGet<P> {
        P getFuture() throws InterruptedException, ExecutionException;
    }
}
