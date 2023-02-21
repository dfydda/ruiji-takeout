package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.User;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface UserService extends IService<User> {

     //修改用户状态
     Boolean updateStatus(Integer status, List<Long> ids);
     //批量删除或单个删除
     void deleteByIds(List<Long> ids);
}
