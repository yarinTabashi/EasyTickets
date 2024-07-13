package com.example.demo.Entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String first_name;

    @Column(name = "last_name")
    private String last_name;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "secret_key")
    private String secret_key;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Reservation> reservations;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "user_preferences",
            joinColumns = @JoinColumn(name = "`user`"), // Use backticks to escape 'user' as a column name (reserved word)
            inverseJoinColumns = @JoinColumn(name = "category"))
    Set<Category> likedCategories;

    public User(){

    }

    public User(String first_name, String last_name, String username, String email, String password, String secret_key) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.secret_key = secret_key;
    }

    public Set<Category> getLikedCategories() {
        return likedCategories;
    }

    public void setLikedCategories(Set<Category> likedCategories) {
        this.likedCategories = likedCategories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSecret_key(String secret_key){
        this.secret_key = secret_key;
    }

    public String getSecret_key(){
        return this.secret_key;
    }
}