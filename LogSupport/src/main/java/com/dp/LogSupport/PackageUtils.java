package com.dp.LogSupport;

import cn.fh.pkgscanner.PkgScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 包工具类
 * Created by dongp on 2017/9/10.
 */
public class PackageUtils {

    /**
     * 将要在方法出入口进行日志打印的集合，通过包扫描得到
     */
    //public static Map<String, List<String>> loggingBeans = new HashMap<>();


    /**
     * 获取所有需要打印函数出入口的类方法名列表
     *
     * @param pkgNames
     * @return
     * @throws Exception
     */
    public static Map<String, List<String>> getLoggingBeans(List<String> pkgNames) throws Exception {
        Map<String, List<String>> loggingBeans = new HashMap<>();
        for (String pkgName : pkgNames){
            loggingBeans.putAll(getLoggingBeans(pkgName));
        }
        return loggingBeans;
    }

    /**
     * 获取所有需要打印函数出入口的类方法名列表
     *
     * @param pkgName
     * @return
     * @throws Exception
     */
    public static Map<String, List<String>> getLoggingBeans(String pkgName) throws Exception {
        Map<String, List<String>> loggingBeans = new HashMap<>();
        PkgScanner scanner = new PkgScanner(pkgName);
        //包中的所有类名
        List<String> list = scanner.scan();

        //过滤所有类注释为ILogClass的类
        List<String> listAn = filterComponents(list, ILogClass.class);
        //过滤其中方法注解为ILogMethodNot的类
        Map<String, List<String>> loggingBeansFirst = filterMethodWithOutAnnotation(listAn, ILogMethodNot.class);
        loggingBeans.putAll(loggingBeansFirst);

        //在其他类注解非ILogClass中过滤出方法注解是ILogMethod的类方法集合
        List<String> listOther = new ArrayList<>(list);
        listOther.removeAll(listAn);
        Map<String, List<String>> loggingBeansOther = filterMethodWithAnnotation(listOther, ILogMethod.class);
        loggingBeans.putAll(loggingBeansOther);
        return loggingBeans;
    }

    /**
     * 过滤去除非指定注解类的类名
     *
     * @param classList
     * @param anClazz
     * @return
     */
    private static List<String> filterComponents(List<String> classList, Class anClazz) {
        List<String> newList = new ArrayList(20);
        classList.forEach((name) -> {
            try {
                Class clazz = Class.forName(name);
                Annotation an = clazz.getAnnotation(anClazz);
                if (null != an) {
                    newList.add(name);
                }
            } catch (ClassNotFoundException var5) {
                var5.printStackTrace();
            }
        });
        return newList;
    }

    /**
     * 获取指定或指定除外的注解类的方法名列表
     *
     * @param clzName
     * @param clz
     * @return
     * @throws ClassNotFoundException
     */
    private static List<String> filterMethodWithAnnotation(String clzName, Class clz, boolean withOut) throws ClassNotFoundException {
        List<String> newList = new ArrayList(20);
        List<Method> methods = filterOwnerMethod(clzName);

        for (Method method : methods) {
            Annotation an = method.getAnnotation(clz);
            if (withOut) {
                if (null == an) {
                    newList.add(method.getName());
                }
            } else {
                if (null != an) {
                    newList.add(method.getName());
                }
            }

        }
        return newList;
    }

    /**
     * 获取指定注解类的方法名列表
     *
     * @param clzNames
     * @param clz
     * @return
     * @throws ClassNotFoundException
     */
    private static Map<String, List<String>> filterMethodWithAnnotation(List<String> clzNames, Class<?> clz) throws ClassNotFoundException {
        Map<String, List<String>> loggingBeans = new HashMap<>();
        for (String clzName : clzNames) {
            List<String> newList = filterMethodWithAnnotation(clzName, clz, false);
            if (!newList.isEmpty()){
                loggingBeans.put(clzName, newList);
            }
        }
        return loggingBeans;
    }

    /**
     * 获取非指定注解类的方法名列表
     *
     * @param clzNames
     * @param clz
     * @return
     * @throws ClassNotFoundException
     */
    private static Map<String, List<String>> filterMethodWithOutAnnotation(List<String> clzNames, Class<?> clz) throws ClassNotFoundException {
        Map<String, List<String>> loggingBeans = new HashMap<>();
        for (String clzName : clzNames) {
            List<String> newList = filterMethodWithAnnotation(clzName, clz, true);
            loggingBeans.put(clzName, newList);
        }
        return loggingBeans;
    }

    /**
     * 获取接口上的所有方法名称，包括接口的父类中的所有方法
     *
     * @param clzName
     * @return
     * @throws ClassNotFoundException
     */
    private static List<String> getMethodInterfaces(String clzName) throws ClassNotFoundException {
        List<String> newList = new ArrayList(20);
        Class clz = Class.forName(clzName);
        Class[] interfaces = clz.getInterfaces();
        for (Class cls : interfaces) {
            Method[] methods = cls.getMethods();
            for (Method method : methods) {
                newList.add(method.getName());
            }

        }
        return newList;
    }

    /**
     * 只获取自身类中的方法，不包含父类和接口中的方法
     *
     * @param clzName
     * @return
     * @throws ClassNotFoundException
     */
    private static List<Method> filterOwnerMethod(String clzName) throws ClassNotFoundException {
        List<Method> newList = new ArrayList(20);
        Method[] methods = Class.forName(clzName).getDeclaredMethods();
        List<String> MethodInterfaceNameList = getMethodInterfaces(clzName);
        for (Method method : methods) {
            if (!MethodInterfaceNameList.contains(method.getName())) {
                newList.add(method);
            }
        }

        return newList;
    }
}
