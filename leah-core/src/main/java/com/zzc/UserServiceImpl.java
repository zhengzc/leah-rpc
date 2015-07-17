package com.zzc;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceImpl implements UserService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

	public void add(UserBean userBean){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("---add--->"+JSONObject.toJSONString(userBean));
	}
	
	public UserBean query(int userId){
		UserBean userBean = new UserBean(userId);
		logger.info("---query--->"+JSONObject.toJSONString(userBean));
		return userBean;
	}
	
	public UserBean query(int userId,String userName){
		UserBean userBean = new UserBean(userId);
		userBean.setUserName(userName);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("---query--->"+JSONObject.toJSONString(userBean));
		return userBean;
	}

    @Override
    public void testException() {
        throw new RuntimeException("test Exception!!");
    }
}
