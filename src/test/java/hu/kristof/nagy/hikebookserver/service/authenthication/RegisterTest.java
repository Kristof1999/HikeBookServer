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
    private RegisterService register;

    @Autowired
    private Firestore db;

    @BeforeEach
    void setUp() {
        TestUtils.cleanUsers(db);
    }

    @Test
    void testCorrectRegistration() {
        boolean res = register.registerUser(new User("asd", "asd")).getSuccessResult();

        assertTrue(res);
    }

    @Test
    void testIncorrectRegistration() {
        User user = new User("asd", "asd");
        register.registerUser(user);

        boolean res = register.registerUser(user).getSuccessResult();

        assertFalse(res);
    }
}
