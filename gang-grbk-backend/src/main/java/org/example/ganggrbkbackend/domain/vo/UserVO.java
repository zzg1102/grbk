package org.example.ganggrbkbackend.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String phone;

    private String avatar;

    private String intro;

    private String website;

    private Integer status;

    private Integer gender;

    private Integer articleCount;

    private Integer commentCount;

    private LocalDateTime lastLoginTime;

    private String lastLoginIp;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}