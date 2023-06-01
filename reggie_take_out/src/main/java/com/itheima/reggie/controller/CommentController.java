package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.CommentDto;
import com.itheima.reggie.entity.Comment;
import com.itheima.reggie.service.CommentService;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private EmployeeService employeeService;


    @GetMapping("/page")
    private R<Page<CommentDto>> page(int page, int pageSize, Long forumID){
        //创造分页构造器
        Page<Comment> commentPage = new Page<>(page,pageSize);
        Page<CommentDto> commentDtoPage = new Page<>();
        //创造条件构造器
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(forumID !=null, Comment::getForumId,forumID);
        queryWrapper.orderByDesc(Comment::getUpdateTime);
        commentService.page(commentPage,queryWrapper);

        //把commentPage拷贝到commentDtopage中，但忽略掉records(实体记录)
        BeanUtils.copyProperties(commentPage,commentDtoPage,"records");
        List<Comment> comments = commentPage.getRecords();
        List<CommentDto> commentDtos = comments.stream().map((item)->{

            CommentDto commentDto = new CommentDto();

            Long createUserId = item.getCreateUser();
            String username = employeeService.getById(createUserId).getName();

            BeanUtils.copyProperties(item,commentDto);
            commentDto.setUsername(username);
            return commentDto;
        }).collect(Collectors.toList());
        commentDtoPage.setRecords(commentDtos);
        return R.success(commentDtoPage);
    }

    @GetMapping("/{id}")
    private R<Comment> get(@PathVariable Long id){
        Comment comment = commentService.getById(id);
        return R.success(comment);
    }

    @PostMapping
    private R<String> add(@RequestBody Comment comment){
        commentService.save(comment);
        return R.success("添加成功");
    }
    @PutMapping
    private R<String> update(@RequestBody Comment comment){
        commentService.updateById(comment);
        return R.success("修改成功");
    }
    @DeleteMapping
    private R<String> delete(Long id){
        commentService.removeById(id);
        return R.success("删除成功");
    }
}
