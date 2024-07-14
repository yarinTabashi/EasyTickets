package com.example.demo.mysecurity;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.CustomExceptions.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's UserDetailsService interface.
 * Retrieves user details (username and password) from the UserRepository.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Constructor for UserDetailsServiceImpl.
     * @param repository UserRepository instance to fetch user details from the database.
     */
    public UserDetailsServiceImpl(UserRepository repository) {
        this.userRepository = repository;
    }

    /**
     * Retrieves the user details (username and password) based on the provided email.
     * Throws UsernameNotFoundException if the user is not found.
     * @param email Email of the user to load details for.
     * @return UserDetails object containing user's email (as username) and password.
     * @throws UsernameNotFoundException If no user with the given email exists.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException(String.format("User does not exist, email: %s", email)));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}