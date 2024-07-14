package com.example.demo.Repositories;
import com.example.demo.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for managing Category entities in the database.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Long findIdByName(String name);
    Category findCategoryByName(String name); // Finds a category entity by its name.
    List<Category> findAll(); // Retrieves all categories
}