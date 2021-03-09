package com.bemonovoid.playqd.core.service;

import java.util.Optional;

import com.bemonovoid.playqd.core.model.TokenUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public interface SecurityService {

    static String getCurrentUserName() {
        TokenUserDetails tokenUserDetails = getCurrentUserDetails();
        return Optional.ofNullable(tokenUserDetails).map(TokenUserDetails::getUsername).orElse(null);
    }

    static String getCurrentUserToken() {
        TokenUserDetails tokenUserDetails = getCurrentUserDetails();
        return Optional.ofNullable(tokenUserDetails).map(TokenUserDetails::getToken).orElse(null);
    }

    static TokenUserDetails getCurrentUserDetails() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null) {
            if (TokenUserDetails.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
                return (TokenUserDetails) authentication.getPrincipal();
            }
        }
        return null;
    }

    static Authentication getCurrentAuthentication() {
        if (SecurityContextHolder.getContext() != null) {
            return SecurityContextHolder.getContext().getAuthentication();
        }
        return null;
    }
}
