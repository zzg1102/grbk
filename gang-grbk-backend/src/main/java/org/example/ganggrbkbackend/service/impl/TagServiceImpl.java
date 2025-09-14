package org.example.ganggrbkbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.ganggrbkbackend.common.exception.BusinessException;
import org.example.ganggrbkbackend.domain.dto.TagSaveDTO;
import org.example.ganggrbkbackend.domain.entity.Tag;
import org.example.ganggrbkbackend.domain.vo.TagVO;
import org.example.ganggrbkbackend.mapper.TagMapper;
import org.example.ganggrbkbackend.service.TagService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    private final TagMapper tagMapper;

    @Override
    @Cacheable(value = "tags", key = "'all'")
    public List<TagVO> getAllTags() {
        List<Tag> tags = this.list(new LambdaQueryWrapper<Tag>()
                .orderByDesc(Tag::getCreateTime));

        return tags.stream()
                .map(tag -> {
                    TagVO vo = new TagVO();
                    BeanUtil.copyProperties(tag, vo);
                    // TODO: 查询标签下的文章数量
                    vo.setArticleCount(0);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "tags", key = "#id")
    public TagVO getTagById(Long id) {
        Tag tag = this.getById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }

        TagVO vo = new TagVO();
        BeanUtil.copyProperties(tag, vo);
        // TODO: 查询标签下的文章数量
        vo.setArticleCount(0);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "tags", allEntries = true)
    public Long saveTag(TagSaveDTO saveDTO) {
        // 检查标签名称是否重复
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, saveDTO.getName());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("标签名称已存在");
        }

        Tag tag = new Tag();
        BeanUtil.copyProperties(saveDTO, tag);

        // 设置默认值
        if (tag.getColor() == null) {
            tag.setColor("#409EFF");
        }

        this.save(tag);
        return tag.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "tags", allEntries = true)
    public void updateTag(TagSaveDTO saveDTO) {
        if (saveDTO.getId() == null) {
            throw new BusinessException("标签ID不能为空");
        }

        Tag existTag = this.getById(saveDTO.getId());
        if (existTag == null) {
            throw new BusinessException("标签不存在");
        }

        // 检查标签名称是否重复（排除自己）
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, saveDTO.getName())
                .ne(Tag::getId, saveDTO.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("标签名称已存在");
        }

        Tag tag = new Tag();
        BeanUtil.copyProperties(saveDTO, tag);
        this.updateById(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "tags", allEntries = true)
    public void deleteTag(Long id) {
        Tag tag = this.getById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }

        // TODO: 检查是否有文章关联此标签

        this.removeById(id);
    }

    @Override
    @Cacheable(value = "tags", key = "'enabled'")
    public List<TagVO> getEnabledTags() {
        List<Tag> tags = this.list(new LambdaQueryWrapper<Tag>()
                .orderByDesc(Tag::getCreateTime));

        return tags.stream()
                .map(tag -> {
                    TagVO vo = new TagVO();
                    BeanUtil.copyProperties(tag, vo);
                    // TODO: 查询标签下的文章数量
                    vo.setArticleCount(0);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "tags", key = "'hot:' + #limit")
    public List<TagVO> getHotTags(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        // TODO: 按照文章数量排序获取热门标签，暂时按创建时间排序
        List<Tag> tags = this.list(new LambdaQueryWrapper<Tag>()
                .orderByDesc(Tag::getCreateTime)
                .last("LIMIT " + limit));

        return tags.stream()
                .map(tag -> {
                    TagVO vo = new TagVO();
                    BeanUtil.copyProperties(tag, vo);
                    // TODO: 查询标签下的文章数量
                    vo.setArticleCount(0);
                    return vo;
                })
                .collect(Collectors.toList());
    }
}