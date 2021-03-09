package com.bemonovoid.playqd.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.bemonovoid.playqd.core.helpers.ResourceIdHelper;
import com.bemonovoid.playqd.core.model.LibraryResourceId;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtEncodedAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/playqd/api/library/resource")) {
            String encodedResourceId = request.getParameter("resourceId");
            if (StringUtils.hasText(encodedResourceId)) {
                LibraryResourceId resourceIdDecoded = ResourceIdHelper.decode(encodedResourceId);
                filterChain.doFilter(new AddHeaderRequestWrapper(resourceIdDecoded.getAuthToken(), request), response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private static class AddHeaderRequestWrapper extends HttpServletRequestWrapper {

        private final String authToken;

        public AddHeaderRequestWrapper(String authToken, HttpServletRequest request) {
            super(request);
            this.authToken = authToken;
        }

        @Override
        public String getHeader(String name) {
            if (name.equals(HttpHeaders.AUTHORIZATION)) {
                return "Bearer " + authToken;
            }
            return super.getHeader(name);
        }
    }
}
