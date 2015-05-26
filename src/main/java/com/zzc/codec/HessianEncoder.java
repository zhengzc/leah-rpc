package com.zzc.codec;

import java.io.ByteArrayOutputStream;

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

    private final int INIT_CAPACITY = 500;//IoBuffer初始化容量
	private int maxDataLength;//发送对象大小
	
	public HessianEncoder(int maxDataLength){
		this.maxDataLength = maxDataLength;
	}

	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		
        logger.debug("start encoder");
		
		ByteArrayOutputStream byteOutputStream = null;
		Hessian2Output hessian2Output = null;
		try{
			//声明二进制数组
			byteOutputStream = new ByteArrayOutputStream();
//	        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
	        
			//写入序列化信息
			hessian2Output = new Hessian2Output(byteOutputStream);
			hessian2Output.startMessage();
			hessian2Output.writeObject(message);
			hessian2Output.completeMessage();
			
			hessian2Output.flush();//将序列化信息发送出去
			
			//准备写入数据
            byte[] object = byteOutputStream.toByteArray();
            int objectLength = object.length;
            if(objectLength > this.maxDataLength){
                logger.error("send Object is to long! max long is {}",this.maxDataLength);
                throw new IllegalArgumentException("send Object is to long! max long is"+this.maxDataLength);
            }
            //申请ioBuff
            IoBuffer buffer = IoBuffer.allocate(INIT_CAPACITY).setAutoExpand(true);

			buffer.putInt(objectLength);//先放入对象长度
            logger.debug("Object's length is {}",objectLength);
			buffer.put(object);//写入序列化对象
			
			//写入
			buffer.flip();//切换到读模式
			out.write(buffer);//写入
			out.flush();
            buffer.free();//释放，可能会提高效率

            logger.debug("end encoder");
		}catch(Exception e){
            logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(byteOutputStream != null){
				byteOutputStream.flush();
				byteOutputStream.close();
			}
			
			if(hessian2Output != null){
				hessian2Output.close();
			}
		}
		
	}

}
