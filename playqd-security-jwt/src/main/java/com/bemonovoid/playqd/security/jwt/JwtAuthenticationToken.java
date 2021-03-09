package com.bemonovoid.playqd.security.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public JwtAuthenticationToken(String authToken) {
        super(authToken, null);
    }

}
