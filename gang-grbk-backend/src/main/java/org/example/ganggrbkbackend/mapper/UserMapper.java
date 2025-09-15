package org.example.ganggrbkbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.ganggrbkbackend.domain.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM tb_user WHERE username = #{username} AND delete_flag = 0")
    User selectByUsername(String username);
}