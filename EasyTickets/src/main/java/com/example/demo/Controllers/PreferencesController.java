package com.example.demo.Controllers;
import com.example.demo.Entities.Category;
import com.example.demo.Services.CategoriesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("preferences/")
public class PreferencesController {
    private final CategoriesService categoriesService;

    public PreferencesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    // Relates to the user preferences
    @PostMapping()
    public ResponseEntity<String> addPreferences(@RequestHeader("Authorization") String token,
                                                 @RequestBody List<Long> categoryIds) {
        try {
            categoriesService.addPreferences(token, categoryIds);
            return ResponseEntity.ok("Preferences added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add preferences: " + e.getMessage());
        }
    }

    @DeleteMapping()
    public ResponseEntity<String> deletePreferences(@RequestHeader("Authorization") String token,
                                                    @RequestBody List<Long> categoryIds) {
        try {
            categoriesService.deletePreferences(token, categoryIds);
            return ResponseEntity.ok("Preferences deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete preferences: " + e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<List<Long>> getAllPreferences(@RequestHeader("Authorization") String token) {
        try {
            List<Long> preferences = categoriesService.getAllPreferences(token);
            return ResponseEntity.ok(preferences);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Returns all the categories.
    @GetMapping("/all")
    public List<Category> getAllPossible(){
        return categoriesService.getAllPossible();
    }
}
