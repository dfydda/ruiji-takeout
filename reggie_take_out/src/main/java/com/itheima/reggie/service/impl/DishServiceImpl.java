package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息,从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional//作用同成功同失败
    public void updateWithFlavor(DishDto dishDto) {
        //跟新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    /**
     * (批量)停售/起售菜品
     * @param status
     * @param ids
     * @return
     */
    @Override
    public Boolean updateStatus(Integer status, List<Long> ids) {
        //业务逻辑：如果要停售则必须检查关联套餐是否停售
        if(status == 0){
            //根据菜品IDs获取其关联的套餐IDs
            Set<Long> setmealIds = setmealService.getIdsByDishId(ids);
            //如果该菜品关联的套餐IDs不为空，才继续进行下一步
            if(!setmealIds.isEmpty()){
                //创建setmeal的过滤条件封装器
                LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                //添加过滤条件：IN()套餐IDs
                lambdaQueryWrapper.in(Setmeal::getId,setmealIds);
                //添加过滤条件：在售套餐
                lambdaQueryWrapper.eq(Setmeal::getStatus,1);
                //如果满足的setmeal记录数大于0，则说明关联套餐在售，抛出业务异常
                if(setmealService.count(lambdaQueryWrapper)>0){
                    throw new CustomException("停售失败，菜品所关联套餐仍在出售，请停售相关套餐");
                }
            }
        }

        //目的：尽量减少与MySQL通信的次数
        //1.根据菜品ID集合批量查询菜品
        List<Dish> dishes = this.listByIds(ids);

        //2.使用集合的stream流修改售卖状态
        dishes = dishes.stream().peek(dish ->{
            dish.setStatus(status);
        }).collect(Collectors.toList());

        //3.批量update
        return this.updateBatchById(dishes);
    }

    /**
     * 菜品批量删除和单个删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        //构造条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<Dish>();
        //先查询该菜品是否在售卖，如果是则抛出业务异常
        queryWrapper.in(ids!=null,Dish::getId,ids);
        List<Dish> list = this.list(queryWrapper);
        for(Dish dish: list){
            Integer status = dish.getStatus();
            //如果不是在售卖，则可以删除
            if(status == 0){
                this.removeById(dish.getId());
            }else {
                //此时应该回滚，因为可能前面的删除了，但是后面的是正在售卖
                throw new CustomException("删除菜品中有正在售卖菜品，无法全部删除");
            }
        }

    }
}