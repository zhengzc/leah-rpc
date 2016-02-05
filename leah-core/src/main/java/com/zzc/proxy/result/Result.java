package com.zzc.proxy.result;


/**
 * 统一的值返回接口
 * 此接口是rpc调用的返回结果
 *
 * @author ying
 */
public interface Result {
    /**
     * 获取token
     *
     * @return
     */
    public String getToken();

    /**
     * 获取返回值
     *
     * @return
     */
    public Object getResult();

    /**
     * 获取异常，正常情况下这个字段为null 如果服务端异常，这里则是异常信息
     *
     * @return
     */
    public Throwable getException();

//    /**
//     * 判断是否调用成功
//     * @return
//     */
//    public Boolean getIsSuccess();
}
