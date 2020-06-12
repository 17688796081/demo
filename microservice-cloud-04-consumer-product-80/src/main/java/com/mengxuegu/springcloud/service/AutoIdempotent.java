package com.mengxuegu.springcloud.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author HuangCheng
 * @date 2020/3/28 12:13
 * @desc: 自定义注解，将此注解添加到需要实现的幂等的方法上，凡是某个方法加了这个注解，它都会自动实现幂等
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoIdempotent {
}
