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

    public Optional<ProfileDTO> getUserDetails(String token){
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            ProfileDTO mapped = mapUserToProfileDTO(optionalUser.get());
            return Optional.of(mapped);
        } else {
            return Optional.empty(); // User not found
        }
    }

    private ProfileDTO mapUserToProfileDTO(User user) {
        ProfileDTO profileDTO = new ProfileDTO(user.getFirst_name(), user.getLast_name(), user.getEmail());
        return profileDTO;
    }

    // TODO: FIX IT - the update causes a crash with the error - user not found.
    public void setUserDetails(String token, ProfileDTO profileDTO) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Update user fields based on profileDTO
            user.setFirst_name(profileDTO.first_name());
            user.setLast_name(profileDTO.last_name());
            user.setEmail(profileDTO.email());

            userRepository.save(user);
        } else {
            // Handle case where user with the email from token is not found
            throw new RuntimeException("User not found for email: " + email);
        }
    }
}
