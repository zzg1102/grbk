package org.example.ganggrbkbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.ganggrbkbackend.common.exception.BusinessException;
import org.example.ganggrbkbackend.domain.dto.CategorySaveDTO;
import org.example.ganggrbkbackend.domain.entity.Category;
import org.example.ganggrbkbackend.domain.entity.Article;
import org.example.ganggrbkbackend.domain.vo.CategoryVO;
import org.example.ganggrbkbackend.mapper.CategoryMapper;
import org.example.ganggrbkbackend.mapper.ArticleMapper;
import org.example.ganggrbkbackend.service.CategoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;

    @Override
    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryVO> getAllCategories() {
        List<Category> categories = this.list(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getCreateTime));

        return categories.stream()
                .map(category -> {
                    CategoryVO vo = new CategoryVO();
                    BeanUtil.copyProperties(category, vo);
                    
                    // 查询分类下已发布的文章数量
                    LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
                    articleWrapper.eq(Article::getCategoryId, category.getId())
                                 .eq(Article::getStatus, 1); // 1表示已发布
                    int articleCount = Math.toIntExact(articleMapper.selectCount(articleWrapper));
                    vo.setArticleCount(articleCount);
                    
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "categories", key = "#id")
    public CategoryVO getCategoryById(Long id) {
        Category category = this.getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        CategoryVO vo = new CategoryVO();
        BeanUtil.copyProperties(category, vo);
        
        // 查询分类下已发布的文章数量
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getCategoryId, category.getId())
                     .eq(Article::getStatus, 1); // 1表示已发布
        int articleCount = Math.toIntExact(articleMapper.selectCount(articleWrapper));
        vo.setArticleCount(articleCount);
        
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "categories", allEntries = true)
    public Long saveCategory(CategorySaveDTO saveDTO) {
        // 检查分类名称是否重复
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, saveDTO.getName());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("分类名称已存在");
        }

        Category category = new Category();
        BeanUtil.copyProperties(saveDTO, category);

        // 设置默认值
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }

        this.save(category);
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "categories", allEntries = true)
    public void updateCategory(CategorySaveDTO saveDTO) {
        if (saveDTO.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }

        Category existCategory = this.getById(saveDTO.getId());
        if (existCategory == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查分类名称是否重复（排除自己）
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, saveDTO.getName())
                .ne(Category::getId, saveDTO.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("分类名称已存在");
        }

        Category category = new Category();
        BeanUtil.copyProperties(saveDTO, category);
        this.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        Category category = this.getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // TODO: 检查是否有文章关联此分类

        this.removeById(id);
    }

    @Override
    @Cacheable(value = "categories", key = "'enabled'")
    public List<CategoryVO> getEnabledCategories() {
        List<Category> categories = this.list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getCreateTime));

        return categories.stream()
                .map(category -> {
                    CategoryVO vo = new CategoryVO();
                    BeanUtil.copyProperties(category, vo);
                    
                    // 查询分类下已发布的文章数量
                    LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
                    articleWrapper.eq(Article::getCategoryId, category.getId())
                                 .eq(Article::getStatus, 1); // 1表示已发布
                    int articleCount = Math.toIntExact(articleMapper.selectCount(articleWrapper));
                    vo.setArticleCount(articleCount);
                    
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "categories", allEntries = true)
    public void incrementViewCount(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException("分类ID不能为空");
        }

        Category category = this.getById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 增加浏览量
        category.setViewCount((category.getViewCount() == null ? 0 : category.getViewCount()) + 1);
        this.updateById(category);
    }
}