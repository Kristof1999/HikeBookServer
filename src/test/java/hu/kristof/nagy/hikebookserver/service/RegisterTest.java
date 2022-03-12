package hu.kristof.nagy.hikebookserver.service;

import hu.kristof.nagy.hikebookserver.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RegisterTest {

    @Autowired
    private Register register;

    @Test
    private void testCorrectRegistration() {
        boolean res = register.registerUser(new User("asd", "asd"));
        assertTrue(res);
    }

    @Test
    private void testIncorrectRegistration() {
        boolean res = register.registerUser(new User("asd", "asd"));
        assertTrue(res);
    }
}
