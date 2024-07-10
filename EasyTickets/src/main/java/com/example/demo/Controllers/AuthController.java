package com.example.demo.Controllers;
import com.example.demo.Services.AuthService;
import com.example.demo.Services.EmailService;
import com.example.demo.mysecurity.JwtHelper;
import com.example.demo.requests.LoginRequest;
import com.example.demo.requests.LoginResponse;
import com.example.demo.requests.SignupRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final EmailService emailService;

    public AuthController(AuthenticationManager authenticationManager, AuthService authService, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.emailService = emailService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException e) {
            authService.addLoginAttempt(request.email(), false);
            throw e;
        }

        String token = JwtHelper.generateToken(request.email());
        authService.addLoginAttempt(request.email(), true);

        emailService.sendOtpEmail("yarintabashi@gmail.com", "1234");
        return ResponseEntity.ok(new LoginResponse(request.email(), token));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest requestDto) {
        authService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}