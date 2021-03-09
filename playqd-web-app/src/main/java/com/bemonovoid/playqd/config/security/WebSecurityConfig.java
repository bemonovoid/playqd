package com.bemonovoid.playqd.config.security;

import javax.sql.DataSource;

import com.bemonovoid.playqd.config.properties.AppProperties;
import com.bemonovoid.playqd.security.jwt.JwtAuthenticationFilter;
import com.bemonovoid.playqd.security.jwt.JwtAuthenticationProvider;
import com.bemonovoid.playqd.security.jwt.JwtEncodedAuthenticationFilter;
import com.bemonovoid.playqd.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AppProperties appProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors().configurationSource(request -> createCorsConfiguration())
                .and()
                .authorizeRequests()
//                .antMatchers(HttpMethod.POST, Endpoints.USER_ACCOUNT_BASE_PATH).permitAll()
//                .antMatchers(HttpMethod.OPTIONS, Endpoints.USER_ACCOUNT_BASE_PATH).permitAll()
                .antMatchers("/api/**").authenticated()
                .and()
                .formLogin().disable()
                .httpBasic().authenticationEntryPoint(new UnauthorizedBasicAuthEntryPoint())
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class)
                .addFilterBefore(new JwtEncodedAuthenticationFilter(), JwtAuthenticationFilter.class);

        if (StringUtils.hasText(activeProfile) && activeProfile.equalsIgnoreCase("local")) {
            http.csrf().disable();
        } else {
            http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource);
        auth.authenticationProvider(new JwtAuthenticationProvider(jwtService()));
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

    @Bean
    JwtService jwtService() {
        return new JwtService(appProperties.getSecurity().getTokenSecret());
    }

    private CorsConfiguration createCorsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        return corsConfiguration;
    }

}
