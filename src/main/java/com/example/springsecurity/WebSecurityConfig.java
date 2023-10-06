package com.example.springsecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                //authorizationManagerRequestMatcherRegistry --> auth
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/")
                        .permitAll()
                        .anyRequest()
                        .authenticated())

                //httpSecurityFormLoginConfigurer --> formLoging
                .formLogin(formLoging -> formLoging
                        .successHandler(successAuth()) //Path redirected after logging in
                        .permitAll()
                )


                //httpSecuritySessionManagementConfigurer --> sessionManagement
                .sessionManagement(sessionManagement -> sessionManagement
                        /*
                        ALWAYS = create an HttpSession

                        NEVER = create an HttpSession, but will use the
                        HttpSession if it already exists

                        If_REQUIRED = only create an HttpSession if required

                        STATELESS = never create an HttpSession and it will
                        never use it to obtain the SecurityContext
                        */
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)

                        // When an invalid session ID is submitted, redirect
                        .invalidSessionUrl("/login")
                        //
                        .maximumSessions(1)
                        //
                        .expiredUrl("/login")

                        // Enable and configure session tracking functionality.
                        //> access done in /session RestController
                        .sessionRegistry(getSessionRegistry()))


                //httpSecuritySessionManagementConfigurer --> sessionManagement
                .sessionManagement(sessionManagement -> sessionManagement
                        /*
                        migrateSession():
                        new session should be created and the session attributes
                        from the original HttpSession should be retained

                        newSession():
                        new session should be created, but the session attributes
                        from the original HttpSession should not be retained.

                        none(): deactivate the session fixation attack protect
                        */
                        // Equivalent: .sessionFixation(config -> config.migrateSession()))
                        .sessionFixation(SessionManagementConfigurer
                                .SessionFixationConfigurer::migrateSession))


                //httpSecurityHttpBasicConfigurer --> httpBasic
                .httpBasic(AbstractHttpConfigurer::disable)

                .build();
    }

    public AuthenticationSuccessHandler successAuth() {
        return (request, response, authentication) ->
                response.sendRedirect("/session");
    }

    @Bean
    public SessionRegistry getSessionRegistry() {
        return new SessionRegistryImpl();
    }
}

