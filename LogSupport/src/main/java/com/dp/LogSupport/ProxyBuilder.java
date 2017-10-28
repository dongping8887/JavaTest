package com.dp.LogSupport;

import net.sf.cglib.proxy.Enhancer;
import org.apache.spark.LogUtils;

/**
 * cglib代理对象构造器
 * Created by dongp on 2017/9/10.
 */
public class ProxyBuilder {
    private Enhancer enhancer= new Enhancer();

    public ProxyBuilder setRealObj(Object obj) throws Exception{
        enhancer.setSuperclass(obj.getClass());
        enhancer.setCallback(new LogMethodIntercepter(obj));
        return this;
    }

    public Object buildProxy() throws Exception{
        Object obj = enhancer.create();
        LogUtils.initLog(obj);
        return obj;
    }

    public Object buildProxy(Object obj) throws Exception{
        return setRealObj(obj).buildProxy();
    }

}
