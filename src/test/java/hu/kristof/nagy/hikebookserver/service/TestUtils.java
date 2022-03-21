package hu.kristof.nagy.hikebookserver.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;

import java.util.concurrent.ExecutionException;

public class TestUtils {
    public static void cleanUsers(Firestore db) {
        for(DocumentReference doc : db.collection(DbPathConstants.COLLECTION_USER).listDocuments()) {
            try {
                doc.delete().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void cleanRoutes(Firestore db) {
        for(DocumentReference doc : db.collection(DbPathConstants.COLLECTION_ROUTE).listDocuments()) {
            try {
                doc.delete().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
