package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.ForumDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.entity.Forum;
import com.itheima.reggie.service.EmployeeService;
import com.itheima.reggie.service.ForumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/forum")
@Slf4j
public class ForumController {
    @Autowired
    private ForumService forumService;
    @Autowired
    private EmployeeService employeeService;

    /**
     * 添加论坛
     * @param forum
     * @return
     */
    @PostMapping
    private R<String> add(@RequestBody Forum forum){
        log.info("Forum:{}",forum);

        forumService.save(forum);
        return R.success("添加成功");
    }

    /**
     * 根据id删除论坛
     * @param id
     * @return
     */
    @DeleteMapping
    private R<String> delete(Long id){
        log.info("删除论坛，id为{}",id);
        forumService.removeById(id);
        return R.success("删除成功");
    }

    /**
     * 根据id修改论坛
     * @param forum
     * @return
     */
    @PutMapping
    private R<String> update(@RequestBody Forum forum){
        log.info(forum.toString());
        forumService.updateById(forum);
        return R.success("修改成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<ForumDto>> page(int page , int pageSize,String name){
        //分页查询
        Page<Forum> pageInfo = new Page<>(page,pageSize);
        Page<ForumDto> pageInfo1 = new Page<>();
        //条件查询
        LambdaQueryWrapper<Forum> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Forum::getName,name);
        //添加排序条件，根据updatetime进行排序
        queryWrapper.orderByDesc(Forum::getUpdateTime);
        //调用数据层的分页查询方法，此时forumPage中已经有值了
        forumService.page(pageInfo,queryWrapper);

        // 把pageInfo中的属性值复制到pageInfo1中，但要忽略pageInfo中的records
        // 这是因为records中的数据是真正展示到浏览器上的，我们要处理一下records中的数据
        BeanUtils.copyProperties(pageInfo,pageInfo1,"records");//ignoreProperties忽视，record实体记录
        //使用stream处理pageInfo集合，目的是处理成pageInfoDtoRecords的集合
        List<Forum> forums = pageInfo.getRecords();//获取Records实体记录
        //通过stream流处理forumRecords，目的是要根据员工ID查询employee表，最终获得员工名称
        List<ForumDto> Dto=forums.stream().map((item)->{//遍历forumRecords集合中的每个forum，进行如下操作
            //创建 forum对象
            ForumDto forumDto = new ForumDto();
            //先把forum对象的所有属性拷贝到forumDto对象，然后再添加没有的员工名称
            BeanUtils.copyProperties(item,forumDto);
            Long createUserID = item.getCreateUser();//获取CreateUser的id
            String username = employeeService.getById(createUserID).getName();
            forumDto.setUsername(username);//设置员工名称
            return forumDto;//返回全部赋值完成的forumDto
        }).collect(Collectors.toList());// stream流的终止操作：返回一个List集合
        pageInfo1.setRecords(Dto);//把处理好的forumDtoRecords赋回forumDtoPage中
        return R.success(pageInfo1);
    }

    /**
     * 根据id查询论坛内容
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Forum> getById(@PathVariable Long id){
        log.info("根据id查询文章信息...");
        Forum forum =forumService.getById(id);
        int num = forum.getNumber();
        forum.setNumber(num+1);
        forumService.updateById(forum);
        if(forum != null){
            return R.success(forum);
        }
            return R.error("文章查询失败");
    }
}
