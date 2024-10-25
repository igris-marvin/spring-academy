package com.example.cashcard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    
    @Bean
    private SecurityFilterChain filterChain(
        HttpSecurity security
    ) throws Exception {

        security
            .authorizeHttpRequests(request -> request
                    .requestMatchers("/cashcards/**")
                    // .authenticated())
                    .hasAnyRole("CARD-OWNER")
                    .requestMatchers("").hasRole(""))
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable());

        return security.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
