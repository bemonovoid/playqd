package com.bemonovoid.playqd.config.security;

import javax.sql.DataSource;

import com.bemonovoid.playqd.controller.Endpoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.addAllowedHeader("*");
                    corsConfiguration.addAllowedMethod("*");
                    corsConfiguration.addAllowedOrigin("*");
                    return corsConfiguration;
        });
        if (StringUtils.hasText(activeProfile) && activeProfile.equalsIgnoreCase("local")) {
             http.authorizeRequests().antMatchers("/**").permitAll();
        } else {
                http.authorizeRequests()
                        .antMatchers(HttpMethod.POST, Endpoints.USER_ACCOUNT_BASE_PATH).permitAll()
                        .antMatchers(HttpMethod.OPTIONS, Endpoints.USER_ACCOUNT_BASE_PATH).permitAll()
                        .antMatchers("/api/**").authenticated()
                        .and()
                        .formLogin().disable()
                        .httpBasic().authenticationEntryPoint(new UnauthorizedBasicAuthEntryPoint());
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource);
    }

    @Bean
    @Primary
    UserDetailsManager jdbcUserDetailsManager() {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
