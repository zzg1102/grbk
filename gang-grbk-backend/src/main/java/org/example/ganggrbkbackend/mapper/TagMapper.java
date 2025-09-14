package org.example.ganggrbkbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.ganggrbkbackend.domain.entity.Tag;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}