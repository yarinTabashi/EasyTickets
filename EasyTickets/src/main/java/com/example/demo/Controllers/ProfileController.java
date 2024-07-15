package com.example.demo.Controllers;
import com.example.demo.DTOs.ProfileDTO;
import com.example.demo.DTOs.UpdateProfileDTO;
import com.example.demo.Services.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService){
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ProfileDTO> getProfile(@RequestHeader("Authorization") String token){
        try {
            ProfileDTO profileDTO = profileService.getUserDetails(token);
            return ResponseEntity.ok(profileDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateProfileDTO request) {

        profileService.setUserDetails(token, request.firstName(), request.lastName(), request.email());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
