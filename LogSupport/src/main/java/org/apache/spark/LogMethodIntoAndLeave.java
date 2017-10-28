package org.apache.spark;

import com.dp.LogSupport.PackageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 函数出入口日志打印工具类
 * Created by dongp on 2017/9/10.
 */
public class LogMethodIntoAndLeave {
    private static Map<String, List<String>> loggingBeans = new HashMap<>();
    public static final String BEGIN = "Begin";
    public static final String END = "End";


    /**
     * 从包名列表中加载所有需要打印函数出入口的类方法名列表
     *
     * @param pkgNames
     * @throws Exception
     */
    public static void loadPackages(List<String> pkgNames) throws Exception {
        loggingBeans = PackageUtils.getLoggingBeans(pkgNames);
    }

    /**
     * 从包名列表中加载所有需要打印函数出入口的类方法名列表
     *
     * @param filePath
     * @throws Exception
     */
    public static void loadPackages(String filePath) throws Exception {
        loadPackages(PropertyUtils.getPackageNameList(filePath));
    }

    /**
     * 从包名列表中加载所有需要打印函数出入口的类方法名列表
     *
     * @throws Exception
     */
    public static void loadPackages() throws Exception {
        String path = LogMethodIntoAndLeave.class.getResource("/").getPath() + "LogMethod.properties";
        loadPackages(path);
    }

    /**
     * 在方法出入口打印日志的集合中是否有此对应的项
     * @param clsName
     * @param methodName
     * @return
     */
    public static boolean contains(String clsName, String methodName) {
        List<String> methods = loggingBeans.get(clsName);
        if (methods != null && methods.contains(methodName)) {
            return true;
        }
        return false;
    }
}
