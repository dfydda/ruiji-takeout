package com.itheima.reggie.common;
/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id,id为Long型
 */
public class BaseContext {
    //ThreadLocal并不是一个Thread,而是Thread的局部变量
    //ThreadLocal为每个线程提供单独一份存储空间，具有线程隔离的效果，只有在县城内才能获取到对应的值，线程外则不能访问
 private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
 public static void setCurrentId(Long id){

     threadLocal.set(id);
 }

    /**
     * 获取值
     * @return
     */
 public static Long getCurrentId(){

     return threadLocal.get();
 }

}
