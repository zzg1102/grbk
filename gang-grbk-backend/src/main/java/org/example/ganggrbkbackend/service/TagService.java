package org.example.ganggrbkbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.ganggrbkbackend.domain.dto.TagSaveDTO;
import org.example.ganggrbkbackend.domain.entity.Tag;
import org.example.ganggrbkbackend.domain.vo.TagVO;

import java.util.List;

public interface TagService extends IService<Tag> {

    List<TagVO> getAllTags();

    TagVO getTagById(Long id);

    Long saveTag(TagSaveDTO saveDTO);

    void updateTag(TagSaveDTO saveDTO);

    void deleteTag(Long id);

    List<TagVO> getEnabledTags();

    List<TagVO> getHotTags(Integer limit);
}