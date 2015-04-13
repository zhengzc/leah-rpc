package com.zzc.result;

/**
 * 处理返回结果的主题
 * @author ying
 *
 */
public interface ResultSubject {
	/**
	 * 注册一个订阅者
	 * @param resultObserver 订阅者
	 */
	public void register(ResultObserver resultObserver);
	
	/**
	 * 删除一个订阅者
	 * @param resultObserver
	 */
	public void remove(ResultObserver resultObserver);
	
	/**
	 * 调用此方法来通知一个订阅者
	 * @param result 
	 */
	public void notifyOberver(Result result);
}
