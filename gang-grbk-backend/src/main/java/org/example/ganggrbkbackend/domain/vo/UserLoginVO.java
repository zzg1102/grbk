package org.example.ganggrbkbackend.domain.vo;

import lombok.Data;

@Data
public class UserLoginVO {

    private String token;

    private String username;

    private String nickname;

    private String avatar;

    private String email;

    private Long userId;

    private Integer userType;
}