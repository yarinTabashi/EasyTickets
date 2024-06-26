package com.example.demo.Repositories;
import com.example.demo.Entities.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    List<LoginAttempt> findByEmailOrderByCreatedAtDesc(String email);
}