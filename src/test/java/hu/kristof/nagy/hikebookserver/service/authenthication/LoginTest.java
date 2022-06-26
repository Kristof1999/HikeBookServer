package hu.kristof.nagy.hikebookserver.service.authenthication;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.User;
import hu.kristof.nagy.hikebookserver.service.TestUtils;
import hu.kristof.nagy.hikebookserver.service.authentication.LoginService;
import hu.kristof.nagy.hikebookserver.service.authentication.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

// For local emulator suite: set environment variables:
// FIRESTORE_EMULATOR_HOST=localhost:8000
// GCLOUD_PROJECT=hikebook

@SpringBootTest
class LoginTest {
    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanUsers(db);
    }

    @Test
    void testCorrectLogin() {
        RegisterService.registerUser(db, new User("asd", "asd"));

        boolean res = LoginService.loginUser(db, new User("asd","asd")).getSuccessResult();

        assertTrue(res);
    }

    @Test
    void testIncorrectLogin() {
        boolean res = LoginService.loginUser(db, new User("a", "asd")).getSuccessResult();

        assertFalse(res);

        res = LoginService.loginUser(db, new User("a", "a")).getSuccessResult();

        assertFalse(res);

        res = LoginService.loginUser(db, new User("asd", "a")).getSuccessResult();

        assertFalse(res);
    }
}