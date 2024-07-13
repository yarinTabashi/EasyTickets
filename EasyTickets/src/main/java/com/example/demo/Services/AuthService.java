package com.example.demo.Services;
import com.example.demo.Entities.LoginAttempt;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.LoginAttemptRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.CustomExceptions.DuplicateException;
import com.example.demo.mysecurity.JwtHelper;
import com.example.demo.requests.SignupRequest;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, LoginAttemptRepository loginAttemptRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.loginAttemptRepository = loginAttemptRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void signup(SignupRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.email());
        if (existingUser.isPresent()) {
            throw new DuplicateException(String.format("User with the email address '%s' already exists.", request.email()));
        }

        User user = new User(request.first_name(), request.last_name(), request.username(), request.email(), passwordEncoder.encode(request.password()), generateTOTPSecretKey(request.username()));
        userRepository.save(user);
    }

    @Transactional
    public void addLoginAttempt(String email, boolean success) {
        loginAttemptRepository.save(new LoginAttempt(email, success, LocalDateTime.now()));
    }

    // Called during the register
    private String generateTOTPSecretKey(String email) {
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(60000) // Set time step size to 60 seconds (1 minute)
                .build();

        GoogleAuthenticator gAuth = new GoogleAuthenticator(config);
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    // Called when want to verify the user
    public int getTOTP(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email); // Retrieve the secret key from the database
        if (optionalUser.isPresent()){
            String secretKey = optionalUser.get().getSecret_key();
            GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                    .setTimeStepSizeInMillis(60000) // Set time step size to 60 seconds (1 minute)
                    .build();

            GoogleAuthenticator gAuth = new GoogleAuthenticator(config);
            return gAuth.getTotpPassword(secretKey); // Create TOTP (according to the user's secret key and time).
        }
        return -1;
    }

    public boolean verifyTOTP(String email, int verificationCode) {
        Optional<User> optionalUser = userRepository.findByEmail(email); // Retrieve the secret key from the database
        if (optionalUser.isPresent()){
            String secretKey = optionalUser.get().getSecret_key();
            GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                    .setTimeStepSizeInMillis(60000) // Set time step size to 60 seconds (1 minute)
                    .build();

            GoogleAuthenticator gAuth = new GoogleAuthenticator(config);
            return gAuth.authorize(secretKey, verificationCode);
        }
        System.out.println("Cannot find user's secret key in order to verify TOTP.");
        return false;
    }

    public void updatePassword(String token, String newPassword) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            existingUser.get().setPassword(passwordEncoder.encode(newPassword));
        }
    }
}