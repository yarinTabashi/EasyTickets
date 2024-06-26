package com.example.demo.Services;
import com.example.demo.Entities.Category;
import com.example.demo.Entities.UserPreferences;
import com.example.demo.Repositories.CategoryRepository;
import com.example.demo.Repositories.UserPreferencesRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.mysecurity.JwtHelper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

// The service handles on categories repository and user preferences repository.
@Service
public class CategoriesService {
    private final CategoryRepository categoryRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserRepository userRepository;

    @Autowired
    public CategoriesService(CategoryRepository categoryRepository, UserPreferencesRepository userPreferencesRepository, UserRepository userRepository){
        this.categoryRepository = categoryRepository;
        this.userPreferencesRepository = userPreferencesRepository;
        this.userRepository = userRepository;
    }

    public List<Category> getAllPossible() {
        return categoryRepository.findAll();
    }

    public void addPreferences(String token, List<Long> categoryIds) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email))
                .getId();

        for (Long categoryId : categoryIds) {
            // Check if the user already has a preference for this category
            if (!userPreferencesRepository.existsByUserIdAndCategoryId(userId, categoryId)) {
                // If not exists, save the preference
                userPreferencesRepository.save(new UserPreferences(userId, categoryId));
            }
        }
    }

    public void deletePreferences(String token, List<Long> categoryIds) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email))
                .getId();

        for (Long categoryId : categoryIds) {
            // Check if the preference exists
            UserPreferences userPreference = userPreferencesRepository.findByUserIdAndCategoryId(userId, categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Preference not found for userId: " + userId + ", categoryId: " + categoryId));

            // Delete the preference
            userPreferencesRepository.delete(userPreference);
        }
    }

    public List<Long> getAllPreferences(String token) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email))
                .getId();

        List<Long> preferences = userPreferencesRepository.findAllByUserId(userId)
                .stream()
                .map(UserPreferences::getCategory)
                .collect(Collectors.toList());
        return preferences;
    }
}