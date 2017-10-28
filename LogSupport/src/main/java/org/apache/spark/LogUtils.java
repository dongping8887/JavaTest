package org.apache.spark;

import com.dp.LogSupport.ILogClass;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.log4j.Category;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

@ILogClass
public class LogUtils {
    //    private static String FQCN = LogUtils.class.getName();
    public static String FQCN = "org.apache.spark.Logging$class";
    private static String LOG_NAME = "org$apache$spark$Logging$$log_";
    private static org.apache.log4j.Logger logger4j;
    private static org.slf4j.Logger logger;
    public static List<String> logMethodName = new ArrayList<>();


    static {
        logMethodName.add("logInfo");
        logMethodName.add("logDebug");
        logMethodName.add("logTrace");
        logMethodName.add("logWarning");
        logMethodName.add("logError");

        init1();
    }

    private static void init1() {
        try {
            Enhancer eh = new Enhancer();
            eh.setSuperclass(Logger.class);
            eh.setCallbackType(LogInterceptor.class);
            Class c = eh.createClass();
            Enhancer.registerCallbacks(c, new LogInterceptor[]{new LogInterceptor()});

            Constructor<Logger> constructor = c.getConstructor(String.class);
            Logger loggerProxy = constructor.newInstance(Logger.class.getName());
            logger4j = loggerProxy;

            LoggerRepository loggerRepository = LogManager.getLoggerRepository();
            LoggerFactory lf = org.apache.spark.ReflectionUtil.getFieldValue(loggerRepository, "defaultFactory");
            Object loggerFactoryProxy = Proxy.newProxyInstance(
                    LoggerFactory.class.getClassLoader(),
                    new Class[]{LoggerFactory.class},
                    new NewLoggerHandler(loggerProxy)
            );

            org.apache.spark.ReflectionUtil.setFieldValue(loggerRepository, "defaultFactory", loggerFactoryProxy);
            logger = org.slf4j.LoggerFactory.getLogger(FQCN);
            org.apache.spark.ReflectionUtil.setFieldValue(loggerRepository, "defaultFactory", lf);

            //还需要代理LoggingEvent中的
        } catch (
                IllegalAccessException |
                        NoSuchMethodException |
                        InvocationTargetException |
                        InstantiationException e) {
            throw new RuntimeException("初始化Logger失败", e);
        }
    }


    public static Constructor<LoggingEvent> createConstructorLoggingEvent() {
        Constructor<LoggingEvent> constructorLoggingEvent = null;
        try {
            Enhancer eh = new Enhancer();
            eh.setSuperclass(LoggingEvent.class);
            eh.setCallbackType(LoggingEventInterceptor.class);
            Class c = eh.createClass();
            Enhancer.registerCallbacks(c, new LoggingEventInterceptor[]{new LoggingEventInterceptor()});

            constructorLoggingEvent = c.getConstructor(String.class, Category.class, org.apache.log4j.Priority.class, Object.class, Throwable.class);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("初始化Logger失败", e);
        }
        return constructorLoggingEvent;
    }

    private static class LogInterceptor implements MethodInterceptor {
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            // 只拦截log方法。
            if (objects.length != 4 || !method.getName().equals("log")) {//只过滤log方法，非log方法都走这里
                if (method.getName().equals("forcedLog")) {
                    LoggingEvent loggingEventProxy = createConstructorLoggingEvent().newInstance(objects[0], o, objects[1], objects[2], objects[3]);
                    ((Logger) o).callAppenders(loggingEventProxy);
                    return null;
                }

                return methodProxy.invokeSuper(o, objects);
            }
            objects[0] = FQCN;
            return methodProxy.invokeSuper(o, objects);
        }
    }

    private static class LoggingEventInterceptor implements MethodInterceptor {
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            // 只拦截getLocationInformation方法。
            if (!method.getName().equals("getLocationInformation"))
                return methodProxy.invokeSuper(o, objects);

//            System.out.println("asdada");
//            LocationInfo locationInfoProxy = constructorLocationInfo.newInstance(new Throwable(), FQCN);
            LocationInfo locationInfo = null;
            Object oldLocationInfo = ReflectionUtil.getFieldValue(o, "locationInfo");
            if (oldLocationInfo == null) {
                locationInfo = new LocationInfo(new Throwable(), ((LoggingEvent) o).fqnOfCategoryClass);
                reGetInformation(new Throwable(), ((LoggingEvent) o).fqnOfCategoryClass, locationInfo);

                ReflectionUtil.setFieldValue(o, "locationInfo", locationInfo);
            }


            return locationInfo;
        }

        private void reGetInformation(Throwable t, String fqnOfCallingClass, LocationInfo o) throws Exception {
            String NA = "?";

            String lineNumber;
            String methodName;
            String className;
            String fileName;
            String fullInfo;

            Method getStackTraceMethod = ReflectionUtil.getFieldValue(o, "getStackTraceMethod");
            Method getClassNameMethod = ReflectionUtil.getFieldValue(o, "getClassNameMethod");
            Method getMethodNameMethod = ReflectionUtil.getFieldValue(o, "getMethodNameMethod");
            Method getFileNameMethod = ReflectionUtil.getFieldValue(o, "getFileNameMethod");
            Method getLineNumberMethod = ReflectionUtil.getFieldValue(o, "getLineNumberMethod");


            Object[] noArgs = null;
            Object[] elements = (Object[]) getStackTraceMethod.invoke(t, noArgs);
            String prevClass = NA;
            for (int i = elements.length - 1; i >= 0; i--) {
                String thisClass = (String) getClassNameMethod.invoke(elements[i], noArgs);
                if (fqnOfCallingClass.equals(thisClass)) {
                    int caller = getNumberOfCaller(elements,i);

                    if (caller < elements.length) {
                        className = prevClass;
                        methodName = (String) getMethodNameMethod.invoke(elements[caller], noArgs);
                        fileName = (String) getFileNameMethod.invoke(elements[caller], noArgs);
                        if (fileName == null) {
                            fileName = NA;
                        }
                        int line = ((Integer) getLineNumberMethod.invoke(elements[caller], noArgs)).intValue();
                        if (line < 0) {
                            lineNumber = NA;
                        } else {
                            lineNumber = String.valueOf(line);
                        }
                        StringBuffer buf = new StringBuffer();
                        buf.append(className);
                        buf.append(".");
                        buf.append(methodName);
                        buf.append("(");
                        buf.append(fileName);
                        buf.append(":");
                        buf.append(lineNumber);
                        buf.append(")");
                        fullInfo = buf.toString();

                        ReflectionUtil.setFieldValue(o, "lineNumber", lineNumber);
                        ReflectionUtil.setFieldValue(o, "methodName", methodName);
                        ReflectionUtil.setFieldValue(o, "className", className);
                        ReflectionUtil.setFieldValue(o, "fileName", fileName);
                        ReflectionUtil.setFieldValue(o, "fullInfo", fullInfo);
                    }//if
                    return;
                }//if
                prevClass = thisClass;
            }//for

        }
    }

    private static int getNumberOfCaller(Object[] elements, int currentNumber) throws Exception{
        int number = 0;
        for (int i = elements.length -1; i> currentNumber;i--){
            StackTraceElement element = (StackTraceElement)elements[i];
            if (logMethodName.contains(element.getMethodName())){
                number = i;
                break;
            }
            continue;
        }
        if (number == currentNumber){
            throw new Exception("this class is not extends Logging2");
        }
        return number + 1;
    }

    private static class NewLoggerHandler implements InvocationHandler {
        private final Logger proxyLogger;

        public NewLoggerHandler(Logger proxyLogger) {
            this.proxyLogger = proxyLogger;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return proxyLogger;
        }
    }

    public static void initLog(Object sa) throws Exception{
//        Object oldLocationInfo = ReflectionUtil.getFieldValue(sa, LOG_NAME);
//        System.out.println(oldLocationInfo);

        ReflectionUtil.setFieldValue(sa, LOG_NAME, logger,true);
    }

    public static org.slf4j.Logger getLogger() {
        return logger;
    }

    public static Logger getLogger4j() {
        return logger4j;
    }
}

