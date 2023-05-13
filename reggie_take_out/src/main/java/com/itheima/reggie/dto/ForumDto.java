package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Forum;
import lombok.Data;

@Data
public class ForumDto extends Forum {
    private String username;
}
