package com.zzc;

import com.alibaba.fastjson.JSONObject;

public class UserServcieImpl implements UserServcie{
	public void add(UserBean userBean){
		System.out.println("---add--->"+JSONObject.toJSONString(userBean));
	}
	
	public UserBean query(int userId){
		UserBean userBean = new UserBean(userId);
		System.out.println("---query--->"+JSONObject.toJSONString(userBean));
		return userBean;
	}
	
	public UserBean query(int userId,String userName){
		UserBean userBean = new UserBean(userId);
		userBean.setUserName(userName);
		System.out.println("---query--->"+JSONObject.toJSONString(userBean));
		return userBean;
	}
}
