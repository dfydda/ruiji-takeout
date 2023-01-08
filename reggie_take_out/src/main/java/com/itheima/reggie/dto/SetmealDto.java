package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
//DTO：数据传输对象，一般用于展示层和服务层之间的数据传输
public class SetmealDto extends Setmeal {
    //套餐菜品关系数据
    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
