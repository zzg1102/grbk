package org.example.ganggrbkbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.ganggrbkbackend.domain.dto.CategorySaveDTO;
import org.example.ganggrbkbackend.domain.entity.Category;
import org.example.ganggrbkbackend.domain.vo.CategoryVO;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<CategoryVO> getAllCategories();

    CategoryVO getCategoryById(Long id);

    Long saveCategory(CategorySaveDTO saveDTO);

    void updateCategory(CategorySaveDTO saveDTO);

    void deleteCategory(Long id);

    List<CategoryVO> getEnabledCategories();

    void incrementViewCount(Long categoryId);
}