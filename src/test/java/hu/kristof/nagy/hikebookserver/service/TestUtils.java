package hu.kristof.nagy.hikebookserver.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;

import java.util.concurrent.ExecutionException;

public class TestUtils {
    public static void cleanGroups(Firestore db) {
        cleanCollection(db, DbPathConstants.COLLECTION_GROUP);
    }

    public static void cleanUsers(Firestore db) {
        cleanCollection(db, DbPathConstants.COLLECTION_USER);
    }

    public static void cleanRoutes(Firestore db) {
        cleanCollection(db, DbPathConstants.COLLECTION_ROUTE);
    }

    private static void cleanCollection(Firestore db, String collection) {
        for(DocumentReference doc : db.collection(collection).listDocuments()) {
            try {
                doc.delete().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
