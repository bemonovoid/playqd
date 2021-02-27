package com.bemonovoid.playqd.controller;

import java.security.Principal;

import com.bemonovoid.playqd.core.model.account.CreateAccount;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.USER_ACCOUNT_BASE_PATH)
class UserAccountController {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsManager userDetailsManager;

    UserAccountController(PasswordEncoder passwordEncoder, UserDetailsManager userDetailsManager) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsManager = userDetailsManager;
    }

    @PostMapping
    void registerNewAccount(@RequestBody CreateAccount request) {
        String passwordEncoded = passwordEncoder.encode(request.getPassword());
        UserDetails userDetails = User.builder()
                .username(request.getUserName())
                .password(passwordEncoded)
                .authorities("ADMIN")
                .build();
        userDetailsManager.createUser(userDetails);
    }

    @PostMapping("/login")
    void login(Principal principal) {
        System.out.println(principal.getName());
    }
}
