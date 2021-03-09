package com.bemonovoid.playqd.core.model;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class TokenUserDetails extends User {

    private final String token;

    public TokenUserDetails(String token, UserDetails userDetails) {
        super(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
