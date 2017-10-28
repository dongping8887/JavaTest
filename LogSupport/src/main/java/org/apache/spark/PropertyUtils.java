package org.apache.spark;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 属性文件工具类
 * Created by dongp on 2017/9/10.
 */
public class PropertyUtils {


    /**
     * 从指定路径读取配置文件
     *
     * @param filePath
     * @throws Exception
     */
    public static Properties load(String filePath) throws Exception {
        Properties pros = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(filePath);
            pros.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return pros;
    }

    /**
     * 从指定文件路径中取得所有指定的包名，包名指定字段必须为package.names，包之间用“;”分割
     */
    public static List<String>  getPackageNameList(String filePath) throws Exception{
        Properties pros = load(filePath);
        String pkgNameString = pros.getProperty("package.names");
        String [] pkgNameArray = pkgNameString.split(";");

        List<String> pkgNames = new ArrayList<>();
        for (String pkgName :  pkgNameArray){
            pkgNames.add(pkgName);
        }
        return pkgNames;
    }


}
