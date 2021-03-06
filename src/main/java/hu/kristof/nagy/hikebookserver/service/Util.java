package hu.kristof.nagy.hikebookserver.service;

import com.google.cloud.firestore.DocumentSnapshot;
import hu.kristof.nagy.hikebookserver.service.route.QueryException;

import java.util.List;
import java.util.function.Function;

public class Util {
    public static String GENERIC_ERROR_MSG = "Valami hiba történt.";

    /**
     * Executes the given lambda, if the provided list's size is 1.
     * In the other cases, it throws QueryException.
     * @param documentSnapshots the list to check it's size
     * @param  f the lambda to execute
     * @return the result of the lambda
     */
    public static <R> R handleListSize(
            List<? extends DocumentSnapshot> documentSnapshots,
            Function<List<? extends DocumentSnapshot>, R> f
    ) {
        if (documentSnapshots.size() > 1) {
            throw new QueryException("Got more than 1 document snapshot, but was only expecting 1.");
        } else if (documentSnapshots.isEmpty()) {
            throw new QueryException("Got no document snapshot, but was only expecting 1.");
        } else {
            return f.apply(documentSnapshots);
        }
    }
}
