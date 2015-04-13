package com.zzc.result.impl;

import java.io.Serializable;

import com.zzc.result.Result;

/**
 * 默认的返回值
 * @author ying
 *
 */
public class DefaultResult implements Result,Serializable {
	private String token;
	private Object result;
	
	private static final long serialVersionUID = -917929309672614713L;
	
	public DefaultResult(String token,Object result){
		this.token = token;
		this.result = result;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String getToken() {
		return this.token;
	}

	@Override
	public Object getResult() {
		return this.result;
	}

}
