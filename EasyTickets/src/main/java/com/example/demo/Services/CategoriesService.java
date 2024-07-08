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
    public CategoriesService(CategoryRepository categoryRepository, UserRepository userRepository){
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

    // TODO: FIX IT - Nothing happened
    public void deleteLikedCategory(String token, List<Long> categoryIds) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email))
                .getId();

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            List<Category> categoriesToRemove = categoryRepository.findAllById(categoryIds);

            // Remove categories from the user's liked categories
            user.getLikedCategories().removeAll(categoriesToRemove);

            // Update bidirectional relationship
            for (Category category : categoriesToRemove) {
                category.getLikes().remove(user);
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
}