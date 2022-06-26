package hu.kristof.nagy.hikebookserver.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbCollections;

import java.util.concurrent.ExecutionException;

public class TestUtils {
    public static void cleanGroupHikes(Firestore db) {
        cleanCollection(db, DbCollections.GROUP_HIKE);
    }

    public static void cleanGroups(Firestore db) {
        cleanCollection(db, DbCollections.GROUP);
    }

    public static void cleanUsers(Firestore db) {
        cleanCollection(db, DbCollections.USER);
    }

    public static void cleanRoutes(Firestore db) {
        cleanCollection(db, DbCollections.ROUTE);
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
