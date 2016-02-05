package com.zzc.spring;

import com.zzc.register.MysqlRegister;
import com.zzc.register.Register;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by ying on 15/7/8.
 * 注册中心工厂
 */
public class LeahRegisterFactory extends SpringContext implements FactoryBean {
    /**
     * 注册中心地址
     * 目前仅支持mysql注册中心
     * mysql://127.0.0.1:3306/leah
     */
    private String address;

    /**
     * 针对于有用户名密码的注册中心
     */
    private String userName;
    private String password;

    public LeahRegisterFactory() {

    }

    @Override
    public Object getObject() throws Exception {
        Register register = null;
        if (this.address.startsWith("mysql")) {
            register = new MysqlRegister("jdbc:" + address, userName, password);
        } else {
            throw new IllegalArgumentException("LeahRegisterFactory can not analysis address like " + this.address);
        }
//        ChannelManager.init(register);
        return register;
    }

    @Override
    public Class<?> getObjectType() {
        return Register.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
