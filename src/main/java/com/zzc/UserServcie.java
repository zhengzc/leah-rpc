package com.zzc;


public interface UserServcie {
	public void add(UserBean userBean);
	
	public UserBean query(int userId);
	
	public UserBean query(int userId,String userName);
}
