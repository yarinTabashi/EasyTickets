package com.example.demo.Services;
import com.example.demo.Entities.LoginAttempt;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.LoginAttemptRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.CustomExceptions.DuplicateException;
import com.example.demo.requests.SignupRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

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

        User user = new User(request.first_name(), request.last_name(), request.username(), request.email(), passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    @Transactional
    public void addLoginAttempt(String email, boolean success) {
        loginAttemptRepository.save(new LoginAttempt(email, success, LocalDateTime.now()));
    }

//    public List<LoginAttempt> findRecentLoginAttempts(String email) {
//        return loginAttemptRepository.findByEmailOrderByCreatedAtDesc(email);
//    }
}