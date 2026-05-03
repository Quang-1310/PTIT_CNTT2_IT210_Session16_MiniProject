package ra.service;


import ra.model.dto.CategoryDTO;
import ra.model.entity.Category;

import java.util.List;

public interface CategoryService {
    Category getCategoryById(Long id);
    List<Category> getAllCategories();
    void addCategory(Category category);
    void updateCategory(Category category);
    void deleteCategoryById(Long id);
}
