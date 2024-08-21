package com.example.demo.Controllers;
import com.example.demo.Services.AuthService;
import com.example.demo.Services.EmailService;
import com.example.demo.mysecurity.JwtHelper;
import com.example.demo.DTOs.LoginRequest;
import com.example.demo.DTOs.LoginResponse;
import com.example.demo.DTOs.SignupRequest;
import com.example.demo.DTOs.TOTPRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

/**
 * Controller class for handling authentication-related HTTP requests.
 * Provides endpoints for user login, signup, password update, OTP generation and verification.
 */
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

    /**
     * Authenticates a user with the provided email and password.
     * If the credentials are valid, a JWT token is generated and returned.
     * @param request The login request containing the user's email and password.
     */
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

        return ResponseEntity.ok(new LoginResponse(request.email(), token));
    }

    /**
    * Registers a new user with the provided email and password.
        * If the email is already in use, a BadCredentialsException is thrown.
        * @param request The signup request containing the user's email and password.
    */
    @PostMapping(value = "/signup")
    public ResponseEntity<LoginResponse> login(@RequestBody SignupRequest request) throws Exception {
        try {
            authService.signup(request);
        } catch (BadCredentialsException e) {
            authService.addLoginAttempt(request.email(), false);
            throw e;
        }

        String token = JwtHelper.generateToken(request.email());
        authService.addLoginAttempt(request.email(), true);

        return ResponseEntity.ok(new LoginResponse(request.email(), token));
    }

    /**
     * Updates the password of the user with the provided JWT token.
     * @param token The JWT token of the user.
     * @param newPassword The new password to be set for the user.
     * */
    @PostMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@RequestHeader(name = "Authorization") String token,
                                               @RequestBody String newPassword) {
        authService.updatePassword(token, newPassword);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Generates totp and send it with an email to the user.
    /**
     * Generates a TOTP for the user with the provided email and sends it via email.
     * @param email The email of the user.
     * @return 200 OK if the TOTP was sent successfully, 404 NOT FOUND otherwise.
     * @throws IOException If an error occurs while sending the email.
     * */
    @PostMapping("/send-otp")
    public ResponseEntity<Void> totpVerification(@RequestBody String email) throws IOException {
        int totp = authService.getTOTP(email);
        if (totp != -1){
            System.out.println("OTP is: " + totp);
            emailService.sendOtpEmail("yarintabashi@gmail.com", Integer.toString(totp)); // Send TOTP via email
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Verifies the TOTP for the user with the provided email.
     * @param totpRequest The TOTP request containing the user's email and OTP.
     * @return 200 OK if the TOTP is correct, 404 NOT FOUND otherwise.
     * @throws IOException If an error occurs while verifying the TOTP.
     * */
    @PostMapping("/verify-otp")
    public ResponseEntity<LoginResponse> verifyTotp(@RequestBody TOTPRequest totpRequest) throws IOException {
        if (authService.verifyTOTP(totpRequest.email(), totpRequest.otp())){
            String token = JwtHelper.generateToken(totpRequest.email());
            authService.addLoginAttempt(totpRequest.email(), true);
            return ResponseEntity.ok(new LoginResponse(totpRequest.email(), token));
        }
        return ResponseEntity.notFound().build(); // Return 404 if OTP is incorrect
    }
}