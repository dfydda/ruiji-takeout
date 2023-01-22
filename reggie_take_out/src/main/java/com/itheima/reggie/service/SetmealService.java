package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;
import java.util.Set;


public interface SetmealService extends IService<Setmeal> {
    //新增套餐，同时需要保存套餐和菜品的关联关系
     void saveWithDish(SetmealDto setmealDto);
    //根据菜品IDs集合，查询对应套餐Ids集合
     Set<Long> getIdsByDishId(List<Long> dishIds);
    //删除套餐，同时需要删除套餐和菜品的关联数据
     void removeWithDish(List<Long> ids);
     //回显套餐数据：根据套餐id查询套餐
     SetmealDto getData(Long id);
     //根据套餐id修改售卖状态
    void updateSetmealStatusById(Integer status,List<Long> ids);


}
