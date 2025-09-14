package org.example.ganggrbkbackend.domain.dto;

import lombok.Data;

@Data
public class UserQueryDTO {

    private Integer current = 1;

    private Integer size = 10;

    private String username;

    private String nickname;

    private String email;

    private Integer status;

    private String startTime;

    private String endTime;
}