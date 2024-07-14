package com.example.demo.mysecurity;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import java.security.Key;
import java.util.Date;

/**
 * Helper class for JWT (JSON Web Token) operations including token generation, validation, and extraction.
 * Provides methods to generate tokens, extract username from tokens, validate tokens against user details,
 * and retrieve user ID from tokens.
 */
public class JwtHelper {
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final int MINUTES = 60;
    @Autowired
    private UserRepository userRepository;

    /**
     * Generates a JWT token based on the provided email.
     * @param email The email to set as the subject of the token.
     * @return JWT token as a String.
     */
    public static String generateToken(String email) {
        var now = new Date(); // Current date and time

        // Calculate expiration time manually
        Date expiration = new Date(now.getTime() + MINUTES * 60 * 1000); // Adding minutes in milliseconds

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username (email) from a JWT token.
     * @param token The JWT token from which to extract the username.
     * @return The username (email) extracted from the token.
     */
    public static String extractUsername(String token) {
        return getTokenBody(token).getSubject();
    }
    public Long getUserId(String token) {
        String username = extractUsername(token);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found for username: " + username));

        return user.getId();
    }

    /**
     * Validates the JWT token against the UserDetails.
     * @param token The JWT token to validate.
     * @param userDetails The UserDetails object representing the user details to validate against.
     * @return True if the token is valid for the userDetails, false otherwise.
     */
    public static Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Parses and retrieves the body (claims) of the JWT token.
     * @param token The JWT token to parse.
     * @return The claims (body) of the JWT token.
     * @throws AccessDeniedException If there's an issue parsing the token (e.g., invalid signature or expired token).
     */
    private static Claims getTokenBody(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) { // Invalid signature or expired token
            throw new AccessDeniedException("Access denied: " + e.getMessage());
        }
    }

    /**
     * Checks if a JWT token is expired.
     * @param token The JWT token to check.
     * @return True if the token is expired, false otherwise.
     */
    private static boolean isTokenExpired(String token) {
        Claims claims = getTokenBody(token);
        return claims.getExpiration().before(new Date());
    }
}