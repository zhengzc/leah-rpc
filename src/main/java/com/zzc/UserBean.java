package com.zzc;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3027976208247255709L;
	
	private String userName;
	private int userId;
	private Date birthday;
	private Map<String,String> attr;
	
	public UserBean(int userId){
		this.userName = "zhengzhichao";
		this.userId = userId;
		this.birthday = new Date();
		Map<String, String> attr = new HashMap<String, String>();
		attr.put("attr1", "good");
		this.attr = attr;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Map<String, String> getAttr() {
		return attr;
	}

	public void setAttr(Map<String, String> attr) {
		this.attr = attr;
	}
}
