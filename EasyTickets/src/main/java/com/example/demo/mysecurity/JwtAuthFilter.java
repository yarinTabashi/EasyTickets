package com.example.demo.mysecurity;
import com.example.demo.CustomExceptions.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filter class to intercept incoming requests and validate JWT authentication tokens.
 * If a valid token is found, sets the authentication in the security context.
 * Handles exceptions related to access denied scenarios.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;

    /**
     * Constructor for JwtAuthFilter.
     * @param userDetailsService The user details service to load user information.
     * @param objectMapper The object mapper for JSON serialization of error responses.
     */
    public JwtAuthFilter(UserDetailsServiceImpl userDetailsService, ObjectMapper objectMapper) {
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    /**
     * Filters incoming requests to validate JWT tokens and set authentication in security context.
     * If token is valid, sets authentication using Spring Security's SecurityContextHolder.
     * If token is missing or invalid, allows request to pass to next filter in chain.
     * Handles AccessDeniedException by returning a 403 Forbidden response with an error message.
     * @param request The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @param filterChain The filter chain for invoking the next filter.
     * @throws ServletException If an exception occurs during request processing.
     * @throws IOException If an I/O exception occurs while handling the request or response.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String username = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = JwtHelper.extractUsername(token);
            }
            // If the accessToken is null. It will pass the request to next filter in the chain.
            // Any login and signup requests will not have jwt token in their header, therefore they will be passed to next filter chain.
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // If any accessToken is present, then it will validate the token and then authenticate the request in security context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (JwtHelper.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, null);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (AccessDeniedException e) {
            ApiErrorResponse errorResponse = new ApiErrorResponse(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(toJson(errorResponse));
        }
    }

    /**
     * Converts an ApiErrorResponse object to JSON format using ObjectMapper.
     * @param response The ApiErrorResponse object containing error details.
     * @return JSON representation of the ApiErrorResponse object.
     */
    private String toJson(ApiErrorResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return ""; // Return an empty string if serialization fails
        }
    }
}
