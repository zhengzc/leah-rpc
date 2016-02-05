package com.zzc;


public interface UserService {
    public void add(UserBean userBean);

    public UserBean query(int userId);

    public UserBean query(int userId, String userName);

    public String testStr(String str);

    public UserBean testObj(UserBean userBean);

    public void testException();
}
