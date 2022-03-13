package hu.kristof.nagy.hikebookserver;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import hu.kristof.nagy.hikebookserver.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class HikeBookServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HikeBookServerApplication.class, args);
	}
}
