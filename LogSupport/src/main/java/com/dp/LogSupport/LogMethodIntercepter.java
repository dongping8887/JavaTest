package com.dp.LogSupport;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.spark.LogMethodIntoAndLeave;
import org.apache.spark.LogUtils;

import java.lang.reflect.Method;

/**
 * Created by dongp on 2017/9/10.
 */
public class LogMethodIntercepter implements MethodInterceptor {
    private Object obj;

    public LogMethodIntercepter(Object obj) {
        this.obj = obj;
    }

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String realObjClassName = obj.getClass().getName();
        String methodName = method.getName();
        if (LogMethodIntoAndLeave.contains(realObjClassName, methodName)) {
            //before
            String baseInfo = "[" + realObjClassName + "." + methodName + "]: ";
            LogUtils.getLogger().info(baseInfo + LogMethodIntoAndLeave.BEGIN);
            Object tmp = methodProxy.invokeSuper(o, objects);
            //after
            LogUtils.getLogger().info(baseInfo + LogMethodIntoAndLeave.END);
            return tmp;
        } else {
            return methodProxy.invokeSuper(o, objects);
        }
    }

}
