package com.bemonovoid.playqd.datasource.jdbc.config;

import java.util.Optional;

import com.bemonovoid.playqd.core.service.SecurityService;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityService.getCurrentUserName());
    }
}
