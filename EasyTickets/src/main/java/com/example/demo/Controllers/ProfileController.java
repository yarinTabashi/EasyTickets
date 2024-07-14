package com.example.demo.Controllers;
import com.example.demo.DTOs.ProfileDTO;
import com.example.demo.Services.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService){
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ProfileDTO> getProfile(@RequestHeader("Authorization") String token){
        Optional<ProfileDTO> optionalProfileDTO = profileService.getUserDetails(token);

        return optionalProfileDTO
                .map(ResponseEntity::ok) // Map to ResponseEntity.ok() if present
                .orElse(ResponseEntity.notFound().build()); // Return 404 if empty
    }

    @PutMapping
    public ResponseEntity<Void>  updateProfile(@RequestHeader("Authorization") String token, ProfileDTO profileDTO){
        profileService.setUserDetails(token, profileDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
