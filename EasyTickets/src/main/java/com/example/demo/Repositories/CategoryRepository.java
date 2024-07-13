package com.example.demo.Repositories;
import com.example.demo.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Long findIdByName(String name);
    Category findCategoryByName(String name);
    List<Category> findAll();
}