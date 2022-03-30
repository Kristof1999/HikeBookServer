/*
Copyright © 2002 - 2021 Pivotal, Inc. All Rights Reserved.

Copies of this document may be made for your own use and for distribution to others,
provided that you do not charge any fee for such copies and
further provided that each copy contains this Copyright Notice,
whether distributed in print or electronically.
 */
// based on:
// https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web

package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.model.User;
import hu.kristof.nagy.hikebookserver.service.authentication.LoginService;
import hu.kristof.nagy.hikebookserver.service.authentication.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private LoginService login;

    @Autowired
    private RegisterService register;

    @PostMapping("/login")
    public boolean loginUser(@RequestBody User user) {
        return login.loginUser(user);
    }

    @PostMapping("/register")
    public boolean registerUser(@RequestBody User user) {
        return register.registerUser(user);
    }
}
