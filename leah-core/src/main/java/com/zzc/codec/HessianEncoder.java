package com.zzc.codec;

import java.io.ByteArrayOutputStream;

import com.caucho.hessian.io.SerializerFactory;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.caucho.hessian.io.Hessian2Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hessian编码器，此编码器将对象编码为二进制，并且在头部写入一个int表示对象长度，用来标示对象的长度
 */
public class HessianEncoder extends ProtocolEncoderAdapter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private SerializerFactory serializerFactory;

    @Deprecated
    private final int INIT_CAPACITY = 500;//IoBuffer初始化容量 废弃不用，IoBuffer长度可以预先计算
    private final int INT_BYTE_SIZE = Integer.SIZE / 8;
    private int maxDataLength;//发送对象大小

    public HessianEncoder(int maxDataLength, SerializerFactory serializerFactory) {
        this.maxDataLength = maxDataLength;
        this.serializerFactory = serializerFactory;
    }

    @Override
    public void encode(IoSession session, Object message,
                       ProtocolEncoderOutput out) throws Exception {
        long s = System.currentTimeMillis();

        ByteArrayOutputStream byteOutputStream = null;
        Hessian2Output hessian2Output = null;

        Transaction t = Cat.newTransaction("encoder", "encode");
        try {
            //声明二进制数组
            byteOutputStream = new ByteArrayOutputStream();
            hessian2Output = new Hessian2Output(byteOutputStream);
            /**
             * 设置serializerFactory能将hessian序列化的效率提高几倍，如果不设置会导致最初的几次序列化效率低，出现阻塞的情况。
             * 主要原因是如果hessian2Output中的serializerFactory为空的话，writeObject的时候创建这个对象的时候会出现阻塞，导致最初几次调用耗时过长
             */
            hessian2Output.setSerializerFactory(this.serializerFactory);
            //写入序列化信息
//			hessian2Output.startMessage();
            hessian2Output.writeObject(message);
//			hessian2Output.completeMessage();

            hessian2Output.flush();//将序列化信息发送出去

            //准备写入数据
//            byteOutputStream.flush();
            byte[] object = byteOutputStream.toByteArray();
//            byteOutputStream.reset();
//            hessian2Output.reset();
            int objectLength = object.length;
            if (objectLength > this.maxDataLength) {
                logger.error("send Object is to long! max long is {}", this.maxDataLength);
                throw new IllegalArgumentException("send Object is to long! max long is" + this.maxDataLength);
            }

            //申请ioBuff
//            IoBuffer buffer = IoBuffer.allocate(INIT_CAPACITY).setAutoExpand(true);
            IoBuffer buffer = IoBuffer.allocate(objectLength + INT_BYTE_SIZE);//换成这个，提高性能

            buffer.putInt(objectLength);//先放入对象长度
            Cat.logEvent("encoder", "encode object size", Event.SUCCESS, String.valueOf(objectLength));
            buffer.put(object);//写入序列化对象

            //写入
            buffer.flip();//切换到读模式
            out.write(buffer);//写入
            out.flush();
//            buffer.free();//释放，可能会提高效率

            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            if (byteOutputStream != null) {
                byteOutputStream.close();
            }

            if (hessian2Output != null) {
                hessian2Output.close();
            }

            t.complete();

            logger.debug("encoder 耗时:{}ms,{}", System.currentTimeMillis() - s, System.currentTimeMillis());
        }

    }

    @Override
    public void dispose(IoSession session) throws Exception {

    }
}
