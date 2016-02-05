package com.zzc.proxy;

/**
 * 此接口定义了执行rpc调用需要的参数信息
 * 此接口的实现类就是进行rpc调用传递调用信息的类
 *
 * @author ying
 */
public interface Invocation {
    /**
     * 获取要调用的方法名字
     *
     * @return
     */
    public String getMethodName();

    /**
     * 获取要调用的方法的参数列表的类型
     *
     * @return
     */
    public Class[] getArgumentsType();

    /**
     * 获取要调用的方法的参数列表
     *
     * @return
     */
    public Object[] getArguments();

    /**
     * 要调用的接口名
     *
     * @return
     */
    public Class<?> getInterface();

    /**
     * 每次调用的唯一标示
     *
     * @return
     */
    public String getToken();

    /**
     * 调用接口版本号
     *
     * @return
     */
    public String getVersion();
}
