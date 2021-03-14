package com.bemonovoid.playqd.controller;

import java.security.Principal;

import com.bemonovoid.playqd.core.model.UserAuthToken;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.security.jwt.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Security Login", description = "Logs a user in and provided api access token")
@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = RequestMethod.POST)
class LoginController {

    private final JwtService jwtService;

    LoginController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping
    UserAuthToken login(Principal principal) {
        Authentication authentication = SecurityService.getCurrentAuthentication();
        return new UserAuthToken(jwtService.create(authentication));
    }
}
