package com.zzc.codec;

import com.caucho.hessian.io.SerializerFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * hessian序列化工厂
 */
public class HessianCodecFactory implements ProtocolCodecFactory {
    private final int MAX_DATA_LENGTH = 8 * 1024 * 1024 * 100;//最多100M数据
    private final HessianEncoder encoder;
    private final HessianDecoder decoder;
    /**
     * hessian序列化的时候，初始化SerializerFactory能极大的提高序列化的效率
     */
    private final SerializerFactory serializerFactory = new SerializerFactory();

    public HessianCodecFactory() {
        this.encoder = new HessianEncoder(MAX_DATA_LENGTH, serializerFactory);
        this.decoder = new HessianDecoder(MAX_DATA_LENGTH, serializerFactory);
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
