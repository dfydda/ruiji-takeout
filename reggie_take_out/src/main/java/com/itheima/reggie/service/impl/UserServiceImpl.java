package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.mapper.UserMapper;
import com.itheima.reggie.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{

    /**
     * 修改账号使用状态
     * @param status
     * @param ids
     * @return
     */
    public Boolean updateStatus(Integer status, List<Long> ids){
        //1.根据用户id集合批量查询
        List<User> users = this.listByIds(ids);
        //2.使用集合的stream流修改售卖状态
        users = users.stream().peek(user ->{
            user.setStatus(status);
        }).collect(Collectors.toList());
        //3.批量修改status
        return this.updateBatchById(users);
    }

    /**
     * 账号批量删除或单个删除
     * @param ids
     */
    @Override
    public void deleteByIds(List<Long> ids) {
        //构造条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //先查询该账号是否在使用，如果是则抛出业务异常
        queryWrapper.in(ids!=null,User::getId,ids);
        List<User> list = this.list(queryWrapper);
        for(User user: list){
            Integer status = user.getStatus();
            //如果不是在售卖，则可以删除
            if(status ==0){
                this.removeById(user.getId());
            }else{
                //此时应该回滚，因为可能前面的删除了，但是后面的是正在售卖
                throw new CustomException("删除账号中有启用状态的账号，无法全部删除");
            }
        }
    }
}
