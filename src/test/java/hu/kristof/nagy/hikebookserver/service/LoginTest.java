package hu.kristof.nagy.hikebookserver.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

// For local emulator suite: set environment variables:
// FIRESTORE_EMULATOR_HOST=localhost:8000
// GCLOUD_PROJECT=hikebook

@SpringBootTest
class LoginTest {

    @Autowired
    private LoginService login;

    @Autowired
    private RegisterService register;

    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        for(DocumentReference doc : db.collection(DbPathConstants.COLLECTION_USER).listDocuments()) {
            try {
                doc.delete().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void testCorrectLogin() {
        register.registerUser(new User("asd", "asd"));

        boolean res = login.loginUser(new User("asd","asd"));

        assertTrue(res);
    }

    @Test
    void testIncorrectLogin() {
        boolean res = login.loginUser(new User("a", "asd"));

        assertFalse(res);

        res = login.loginUser(new User("a", "a"));

        assertFalse(res);

        res = login.loginUser(new User("asd", "a"));

        assertFalse(res);
    }
}