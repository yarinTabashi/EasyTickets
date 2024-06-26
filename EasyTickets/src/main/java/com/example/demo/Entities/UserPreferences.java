package com.example.demo.Entities;

import jakarta.persistence.*;

// TODO: Refactor the user preferences (maybe issue of relationships, names or types) . It's cause to crash!
@Entity
@Table(name = "user_preferences", schema = "public")
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@ManyToOne
    //@JoinColumn(name = "user_id")
    @Column(name = "user_id")
    private Long user;

    //@ManyToOne
    //@JoinColumn(name = "category_id")
    @Column(name = "category_id")
    private Long category;

    public UserPreferences(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserPreferences(Long user, Long category){
        this.user = user;
        this.category = category;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }
}