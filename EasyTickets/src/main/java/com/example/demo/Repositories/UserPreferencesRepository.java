package com.example.demo.Repositories;
import com.example.demo.Entities.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    boolean existsByUserIdAndCategoryId(Long user, Long category);

    Optional<UserPreferences> findByUserIdAndCategoryId(Long user, Long category);

    List<UserPreferences> findAllByUserId(Long user);
}
