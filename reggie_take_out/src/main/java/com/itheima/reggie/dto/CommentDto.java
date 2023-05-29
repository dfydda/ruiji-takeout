package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Comment;
import lombok.Data;

@Data
public class CommentDto extends Comment {
    private String username;
}
