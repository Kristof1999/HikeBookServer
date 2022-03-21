package hu.kristof.nagy.hikebookserver.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RegisterTest {

    @Autowired
    private RegisterService register;

    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanUsers(db);
    }

    @Test
    void testCorrectRegistration() {
        boolean res = register.registerUser(new User("asd", "asd"));

        assertTrue(res);
    }

    @Test
    void testIncorrectRegistration() {
        User user = new User("asd", "asd");
        register.registerUser(user);

        TestUtils.wait(10);
        boolean res = register.registerUser(user);

        assertFalse(res);
    }
}
