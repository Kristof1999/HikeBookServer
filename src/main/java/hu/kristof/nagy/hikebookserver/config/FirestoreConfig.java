package hu.kristof.nagy.hikebookserver.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import hu.kristof.nagy.hikebookserver.HikeBookServerApplication;
import hu.kristof.nagy.hikebookserver.data.FirestoreInitilizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirestoreConfig {

    private static final Logger log = LoggerFactory.getLogger(HikeBookServerApplication.class);

    @Scope("singleton")
    @Bean
    public Firestore provideFireStore() {
        try {
            InputStream serviceAccount = new FileInputStream(
                    "C:\\Users\\36203\\hikebook-595dc-firebase-adminsdk-9nnbq-595a8f1501.json"
            );
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp.initializeApp(options);

            log.info("Initialized Firestore");
            return FirestoreClient.getFirestore();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new FirestoreInitilizationException("Failed to initialize Firestore.");
    }
}
