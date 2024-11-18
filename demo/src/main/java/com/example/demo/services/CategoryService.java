package com.example.demo.services;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.models.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryDTO categoryDTO);
    Category getCategoryById(long id);
    List<Category> getAllCategories();
    Category updateCategory(long id, CategoryDTO categoryDTO);
    void deleteCategory(long id);
}
