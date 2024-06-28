package com.example.demo.Entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cat_name")
    private String name;

    @Column(name = "image")
    private Long image;

    @ManyToMany(mappedBy = "likedCategories")
    @JsonIgnore
    Set<User> likes;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    List<Event> eventsList;

    // Constructors
    public Category(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Event> getEventsList() {
        return eventsList;
    }

    public void setEventsList(List<Event> eventsList) {
        this.eventsList = eventsList;
    }

    public Category(String name, Long image) {
        this.name = name;
        this.image = image;
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
        return this.image;
    }

    public void setImage_id(Long image){
        this.image = image;
    }
}