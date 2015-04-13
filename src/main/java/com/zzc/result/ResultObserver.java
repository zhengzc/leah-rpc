package com.zzc.result;

/**
 * 订阅者接口
 * @author ying
 *
 */
public interface ResultObserver {
	/**
	 * 设置result
	 * @param result 设置结果
	 * @return
	 */
	public void setResult(Result result);
	
	/**
	 * 获取token值
	 * @return
	 */
	public String getToken();
}
