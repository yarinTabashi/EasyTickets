package com.example.demo.mysecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for Spring Security settings.
 * Configures authentication, authorization, and filters for JWT-based authentication.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;


    /**
     * Constructor for SecurityConfig.
     * @param userDetailsService The custom user details service for authentication.
     * @param jwtAuthFilter The JWT authentication filter for token validation and processing.
     */
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Bean definition for PasswordEncoder.
     * @return BCryptPasswordEncoder instance for encoding passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain with custom settings.
     * @param http HttpSecurity object to configure security settings.
     * @param authenticationManager AuthenticationManager bean for authenticating users.
     * @return SecurityFilterChain configured with custom settings.
     * @throws Exception If configuration fails.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // -- Set permissions on endpoints --
                .authorizeHttpRequests(auth -> auth
                // Set the public endpoints:
                        .requestMatchers(HttpMethod.POST, "/auth/signup/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "auth/send-otp/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "auth/verify-otp/**").permitAll()
                 // Set the private endpoints:
                        .anyRequest().authenticated())
                .authenticationManager(authenticationManager)

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Bean definition for AuthenticationManager.
     * Configures user details service and password encoder for authentication.
     * @param http HttpSecurity object to obtain shared AuthenticationManagerBuilder.
     * @return AuthenticationManager bean for authenticating users.
     * @throws Exception If configuration fails.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}