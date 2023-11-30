package com.itheima.reggie.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//标记性注解，用于定位aop编程位置（当想要对某个方法进行切面编程，就添加注解）
@Retention(RetentionPolicy.RUNTIME)//这行代码指定了注解的保留策略为运行时，即注解信息在程序运行时仍然可用。
@Target(ElementType.METHOD)//这行代码指定了注解的适用目标为方法，即这个注解可以应用在Java方法上。通过@Target注解指定了注解可以应用的目标元素类型，而ElementType关键词则用于具体指定这些目标类型。
public @interface log {
    //这部分代码定义了一个名为"log"的注解。它是一个自定义注解，使用了关键字@interface来声明。在这个注解中没有定义任何属性或方法，所以它是一个空注解。

}
