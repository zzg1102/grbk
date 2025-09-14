package org.example.ganggrbkbackend.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVO {

    private Long id;

    private Long articleId;

    private Long userId;

    private String username;

    private String nickname;

    private String avatar;

    private Long parentId;

    private String content;

    private String ipAddress;

    private String ipSource;

    private Integer isReview;

    private Integer likeCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<CommentVO> replyComments;

    private Boolean isAuthor;
}