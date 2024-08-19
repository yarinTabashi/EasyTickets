package com.example.demo.Services;
import com.example.demo.DTOs.CategoryDTO;
import com.example.demo.Entities.Category;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.CategoryRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.mysecurity.JwtHelper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

// The service handles on categories repository and user preferences repository.
@Service
public class CategoriesService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public CategoriesService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<Category> getAllPossible() {
        return categoryRepository.findAll();
    }

    public void likeCategories(String token, List<Long> categoryIds) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email))
                .getId();

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            List<Category> categoriesToAdd = categoryRepository.findAllById(categoryIds);
            user.getLikedCategories().addAll(categoriesToAdd);

            // Update bidirectional relationship
            for (Category category : categoriesToAdd) {
                category.getLikes().add(user);
            }

            userRepository.save(user);
        } else {
            throw new EntityNotFoundException("User not found for id: " + userId);
        }
    }

    public Set<CategoryDTO> getAllLikedCategories(String token) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email))
                .getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));

        Set<CategoryDTO> likedCategoriesDTO = user.getLikedCategories().stream()
                .map(category -> new CategoryDTO(category.getName(), category.getImage_id()))
                .collect(Collectors.toSet());

        return likedCategoriesDTO;
    }

    public Map<String, Boolean> getUserPreferencesMapping(String token) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Category> likedCategories = user.getLikedCategories();

            // Fetch all categories from the database
            List<Category> allCategories = categoryRepository.findAll();

            // Create a map to store category names and whether user likes each category
            Map<String, Boolean> preferencesMap = new HashMap<>();

            // Initialize all categories with false (not liked)
            allCategories.forEach(category -> preferencesMap.put(category.getName(), false));

            // Set true for liked categories
            likedCategories.forEach(category -> preferencesMap.put(category.getName(), true));

            return preferencesMap;
        } else {
            System.out.println("User not found");
            return null;
        }
    }

    public boolean setUserPreferencesMapping(String token, Map<String, Boolean> updatedPreferencesMap) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Category> previousLikedCategories = user.getLikedCategories();

            for (Map.Entry<String, Boolean> entry : updatedPreferencesMap.entrySet()) {
                Category category = categoryRepository.findCategoryByName(entry.getKey());

                if (category != null) {
                    boolean isLiked = entry.getValue();

                    if (isLiked) {
                        // Add category to liked categories if not already liked
                        if (!previousLikedCategories.contains(category)) {
                            user.getLikedCategories().add(category);
                        }
                    } else {
                        // Remove category from liked categories if already liked
                        if (previousLikedCategories.contains(category)) {
                            user.getLikedCategories().remove(category);
                        }
                    }
                }
            }
            //categoryRepository.save(category);
            userRepository.save(user); // Save the updated user entity
            return true; // Return true if preferences were successfully updated
        }
        return false; // Return false if user is not found by email
    }
}
