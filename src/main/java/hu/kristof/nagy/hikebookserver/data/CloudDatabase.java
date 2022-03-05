package hu.kristof.nagy.hikebookserver.data;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CloudDatabase {
    private Firestore db;

    public CloudDatabase() {
        try {
            InputStream serviceAccount = new FileInputStream("C:\\Users\\36203\\hikebook-595dc-firebase-adminsdk-9nnbq-8738991677.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp.initializeApp(options);

            db = FirestoreClient.getFirestore();
            // TODO: do some logging
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Firestore getDb() {
        return db;
    }
}