package com.zzc;

import org.apache.mina.core.session.IoSession;

public class UserServiceProxy implements UserServcie {
	private IoSession session;
	
	public UserServiceProxy(IoSession session){
		this.session = session;
	}
	
	@Override
	public void add(UserBean userBean) {
	}

	@Override
	public UserBean query(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserBean query(int userId, String userName) {
		// TODO Auto-generated method stub
		return null;
	}

}
