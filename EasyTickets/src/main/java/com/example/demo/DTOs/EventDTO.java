package com.example.demo.DTOs;

import com.example.demo.Entities.Category;

import java.util.Date;

public record EventDTO(String name, String desc, Date date, String venue, Category category) {
}
