package com.zzc.util;

import java.lang.reflect.Method;

/**
 * Created by ying on 16/2/1.
 */
public class ClassHelper {

    /**
     * 根据方法名获取参数列表
     *
     * @param method
     * @return
     */
    public static String[] getArgumentNames(Method method) {
        String[] rets = new String[]{};
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            rets[i] = parameters[i].getName();
        }

        return rets;
    }

    /**
     * 根据方法名获取参数字符串
     *
     * @param method
     * @return
     */
    public static String getArgumentNamesStr(Method method) {
        StringBuilder sb = new StringBuilder();
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            sb.append(parameters[i].getName());
            if (i != parameters.length - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    /**
     * 根据方法名获取参数字符串
     *
     * @param parameters
     * @return
     */
    public static String getArgumentNamesStr(Class<?>[] parameters) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            sb.append(parameters[i].getName());
            if (i != parameters.length - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

}
