package com.bemonovoid.playqd.core.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public interface SecurityService {

    static String getCurrentUser() {
        if (SecurityContextHolder.getContext() != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                Object principal = authentication.getPrincipal();
                if (principal != null) {
                    return principal.toString();
                }
            }
        }
        return null;
    }
}
