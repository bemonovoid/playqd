package com.bemonovoid.playqd.security.jwt;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenNotFoundException extends AuthenticationException {

    public JwtTokenNotFoundException(String msg) {
        super(msg);
    }
}
