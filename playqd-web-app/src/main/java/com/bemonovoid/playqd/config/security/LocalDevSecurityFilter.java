package com.bemonovoid.playqd.config.security;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

class LocalDevSecurityFilter extends AnonymousAuthenticationFilter {

    LocalDevSecurityFilter() {
        super("key", "greg", List.of(new SimpleGrantedAuthority("admin")));
    }

    @Override
    protected Authentication createAuthentication(HttpServletRequest request) {
        return new UsernamePasswordAuthenticationToken(getPrincipal(), "greg", getAuthorities());
    }
}
