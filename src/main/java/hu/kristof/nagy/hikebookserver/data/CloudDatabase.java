package hu.kristof.nagy.hikebookserver.data;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CloudDatabase {
    private Firestore db;
    private static final Logger log = LoggerFactory.getLogger(CloudDatabase.class);

    public CloudDatabase() {
        try {
            InputStream serviceAccount = new FileInputStream(
                    "C:\\Users\\36203\\hikebook-595dc-firebase-adminsdk-9nnbq-595a8f1501.json"
            );
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp.initializeApp(options);

            db = FirestoreClient.getFirestore();
            log.info("Initialized Firestore");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Firestore getDb() {
        return db;
    }
}