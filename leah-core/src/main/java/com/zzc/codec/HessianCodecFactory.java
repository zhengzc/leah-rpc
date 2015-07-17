package com.zzc.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * hessian序列化工厂
 */
public class HessianCodecFactory implements ProtocolCodecFactory {
	private final int MAX_DATA_LENGTH = 8 * 1024 * 1024 * 10;//最多10M数据
	private final HessianEncoder encoder;
	private final HessianDecoder decoder;
	
	public HessianCodecFactory(){
		this.encoder = new HessianEncoder(MAX_DATA_LENGTH);
		this.decoder = new HessianDecoder(MAX_DATA_LENGTH);
	}
	
	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return this.encoder;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return this.decoder;
	}

}
