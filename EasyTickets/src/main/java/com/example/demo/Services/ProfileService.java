package com.example.demo.Services;
import com.example.demo.DTOs.ProfileDTO;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.mysecurity.JwtHelper;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProfileService {
    private final UserRepository userRepository;
    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ProfileDTO getUserDetails(String token){
        // Get the user
        Optional<User> optionalUser = getUserFromToken(token);
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));
        return mapUserToProfileDTO(user, token);
    }

    private ProfileDTO mapUserToProfileDTO(User user, String jwt) {
        ProfileDTO profileDTO = new ProfileDTO(user.getFirst_name(), user.getLast_name(), user.getEmail(), jwt.replace("Bearer ", ""));
        return profileDTO;
    }

    public void setUserDetails(String token, String firstName, String lastName, String email) {
        // Get the user
        Optional<User> optionalUser = getUserFromToken(token);
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirst_name(firstName);
        user.setLast_name(lastName);
        user.setEmail(email);
        userRepository.save(user);
    }

    private Optional<User> getUserFromToken(String token) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        return userRepository.findByEmail(email);
    }
}
