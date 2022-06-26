package hu.kristof.nagy.hikebookserver.service.authenthication;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.User;
import hu.kristof.nagy.hikebookserver.service.TestUtils;
import hu.kristof.nagy.hikebookserver.service.authentication.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RegisterTest {
    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanUsers(db);
    }

    @Test
    void testCorrectRegistration() {
        boolean res = RegisterService.registerUser(db, new User("asd", "asd")).getSuccessResult();

        assertTrue(res);
    }

    @Test
    void testIncorrectRegistration() {
        User user = new User("asd", "asd");
        RegisterService.registerUser(db, user);

        boolean res = RegisterService.registerUser(db, user).getSuccessResult();

        assertFalse(res);
    }
}
