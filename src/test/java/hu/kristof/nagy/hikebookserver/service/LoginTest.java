package hu.kristof.nagy.hikebookserver.service;

import hu.kristof.nagy.hikebookserver.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoginTest {

    @Autowired
    private LoginService login;

    @Autowired
    private RegisterService register;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testCorrectLogin() {
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