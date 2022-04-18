package hu.kristof.nagy.hikebookserver.service;

import java.util.concurrent.ExecutionException;

public class FutureUtil {
    public static <P> P handleFutureGet(FutureGet<P> f) {
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
