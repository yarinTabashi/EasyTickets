package com.example.demo.Entities;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cat_name")
    private String name;

    @Column(name = "image_id")
    private Long image_id;

    @ManyToMany(mappedBy = "likedCategories")
    Set<User> likes;

    // Constructors
    public Category(){

    }
    public Category(String name, Long image_id) {
        this.name = name;
        this.image_id = image_id;
    }

    // Getters and setters

    public Set<User> getLikes() {
        return likes;
    }

    public void setLikes(Set<User> likes) {
        this.likes = likes;
    }

    public Long getCategoryID() {
        return id;
    }

    public void setCategoryID(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getImage_id(){
        return this.image_id;
    }

    public void setImage_id(Long imageId){
        this.image_id = imageId;
    }
}