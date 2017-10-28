package com.dp.LogSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 不需要在方法出入口打印日志的方法注解，类注解必须为ILogClass，
 * 否则无效，从此类方法列表中去除无需打印日志的方法
 * Created by dongp on 2017/9/9.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ILogMethodNot {

}
