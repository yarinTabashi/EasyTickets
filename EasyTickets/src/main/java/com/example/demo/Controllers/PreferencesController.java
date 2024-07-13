package com.example.demo.Controllers;
import com.example.demo.DTOs.CategoryDTO;
import com.example.demo.Entities.Category;
import com.example.demo.Repositories.CategoryRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.CategoriesService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("preferences/")
public class PreferencesController {
    private final CategoriesService categoriesService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public PreferencesController(CategoriesService categoriesService, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.categoriesService = categoriesService;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    // Relates to the user preferences.
    @PostMapping()
    public ResponseEntity<String> likeCategories(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody List<Long> categoryIds) {
        try {
            categoriesService.likeCategories(token, categoryIds);
            return ResponseEntity.ok("Categories liked successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request.");
        }
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteLikedCategory(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody List<Long> categoryIds) {

        try {
            categoriesService.deleteLikedCategory(token, categoryIds);
            return ResponseEntity.ok("Category unliked successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request.");
        }
    }

    @GetMapping()
    public ResponseEntity<Set<CategoryDTO>> getLikedCategories(
            @RequestHeader(name = "Authorization") String token) {

        try {
            Set<CategoryDTO> likedCategories = categoriesService.getAllLikedCategories(token);
            return ResponseEntity.ok(likedCategories);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Or handle with an appropriate error message
        }
    }

    // Returns all the categories.
    @GetMapping("/all")
    public List<Category> getAllPossible(){
        return categoriesService.getAllPossible();
    }

    @GetMapping("/map")
    public ResponseEntity<Map<String, Boolean>> getUserPreferencesMapping(
            @RequestHeader(name = "Authorization") String token) {
        try {
            Map<String, Boolean> likedCategories = categoriesService.getUserPreferencesMapping(token);
            return ResponseEntity.ok(likedCategories);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
