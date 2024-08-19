package com.example.demo.DTOs;

public class CategoryDTO {
    private String name;
    private Long categoryImageId;

    public CategoryDTO(String name, Long categoryImageId) {
        this.name = name;
        this.categoryImageId = categoryImageId;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getCategoryImageId() { return categoryImageId; }
    public void setCategoryImageId(Long categoryImageId) { this.categoryImageId = categoryImageId; }
}