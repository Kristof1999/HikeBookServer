package hu.kristof.nagy.hikebookserver.service;

import hu.kristof.nagy.hikebookserver.data.UserSource;
import hu.kristof.nagy.hikebookserver.model.UserAuth;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;

public class Login {

    @Autowired
    private UserSource userSource;

    public boolean loginUser(UserAuth user) {
        for (UserAuth registeredInUser : userSource.users) {
            if (registeredInUser.equals(user)) {
                return true;
            }
        }
        // TODO: give back more meaningful error messages
        return false;
    }
}
