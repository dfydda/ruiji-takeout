package com.itheima.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Forum;
import com.itheima.reggie.mapper.ForumMapper;
import com.itheima.reggie.service.ForumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ForumServiceImpl extends ServiceImpl<ForumMapper, Forum> implements ForumService {
}
