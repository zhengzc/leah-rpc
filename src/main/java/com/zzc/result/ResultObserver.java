package com.zzc.result;

/**
 * 订阅者接口
 * @author ying
 *
 */
public interface ResultObserver {
	/**
	 * 设置result
	 * 此方法是主题收到消息时候通知订阅者的接口
	 * @param result 设置结果
	 * @return
	 */
	public void setResult(Result result);
	
	/**
	 * 获取token值
	 * token标示了每一个观察者唯一性
	 * @return
	 */
	public String getToken();
}
