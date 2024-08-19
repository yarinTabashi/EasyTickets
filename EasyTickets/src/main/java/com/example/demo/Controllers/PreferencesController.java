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

    /**
     *  Set the user preferences.
     *  @param token The token of the user.
     *  @param categoryIds The list of category ids to be liked.
     *  @return A response entity with a message.
     *  @throws EntityNotFoundException If the user or category is not found.
     * */
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

    /**
     *  Get the liked categories of the user.
     *  @param token The token of the user.
     *  @return A response entity with the list of liked categories.
     *  @throws EntityNotFoundException If the user is not found.
     * */
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

    /**
     *  Get all possible categories.
     *  @return A list of all possible categories.
     * */
    @GetMapping("/all")
    public List<Category> getAllPossible(){
        return categoriesService.getAllPossible();
    }

    /**
     *  Set the user preferences mapping.
     *  @param token The token of the user.
     *  @param preferencesMap The map of category ids and their preferences (true or false).
     *  @return A response entity with a message.
     *  @throws EntityNotFoundException If the user is not found.
     * */
    @PostMapping("/map")
    public ResponseEntity<Void> setUserPreferencesMapping(@RequestHeader(name = "Authorization") String token,
                                                          @RequestBody Map<String, Boolean> preferencesMap) {
        try {
            boolean isUpdateSucceed = categoriesService.setUserPreferencesMapping(token, preferencesMap);
            if (isUpdateSucceed) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     *  Get the user preferences mapping.
     *  @param token The token of the user.
     *  @return A response entity with the user preferences mapping.
     *  @throws EntityNotFoundException If the user is not found.
     * */
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