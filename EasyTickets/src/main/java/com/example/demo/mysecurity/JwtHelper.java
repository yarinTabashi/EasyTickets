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

public class JwtHelper {
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final int MINUTES = 60;
    @Autowired
    private UserRepository userRepository;

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

    public static String extractUsername(String token) {
        return getTokenBody(token).getSubject();
    }
    public Long getUserId(String token) {
        String username = extractUsername(token);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found for username: " + username));

        return user.getId();
    }

    public static Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

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

    private static boolean isTokenExpired(String token) {
        Claims claims = getTokenBody(token);
        return claims.getExpiration().before(new Date());
    }
}