package hu.kristof.nagy.hikebookserver.service;

import hu.kristof.nagy.hikebookserver.data.UserSource;
import hu.kristof.nagy.hikebookserver.model.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;

public class Register {

    @Autowired
    private UserSource userSource;

    public boolean registerUser(UserAuth user) {
        for (UserAuth registeredUser : userSource.users) {
            if (registeredUser.equals(user)) {
                return false;
            }
        }

        userSource.users.add(user);
        return true;
    }
}
