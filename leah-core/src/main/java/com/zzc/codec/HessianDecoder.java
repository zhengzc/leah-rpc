package com.zzc.codec;

import java.io.ByteArrayInputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.caucho.hessian.io.Hessian2Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 继承CumulativeProtocolDecoder解码器，实现消息的拆包处理
 * 此类将根据encoder序列化数据的格式进行拆包处理，encoder写入的消息体格式为 对象长度(int)+对象(object)
 * @author ying
 *
 */
public class HessianDecoder extends CumulativeProtocolDecoder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private int maxDataLength;//发送对象大小
	
	public HessianDecoder(int maxDataLength){
		this.maxDataLength = maxDataLength;
	}
	
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		
        logger.debug("start decoder");
		
		Object ret;
		
		//先获取对象长度，拆包
//		in.flip();//切换到读模式 这里不能切换，可能是已经切换过了

//		if(in.prefixedDataAvailable(4)){//出现完整的对象,则表示收到一个请求,准备拆包,此方法可能导致ddos攻击，修改为prefixedDataAvailable(int,int)方法
		if(in.prefixedDataAvailable(4,maxDataLength)){//encoder中我们写入了一个int来表示对象长度
            int objectSize = in.getInt();//获取对象长度
            logger.debug("object size is {}",objectSize);
			
			//读取数据
			byte[] data = new byte[objectSize];
			in.get(data);
			
			//hessian转化为对象
			Hessian2Input hessian2Input = null;
			ByteArrayInputStream is = null;
			try{
				//将iobuff中数据转化为二进制数组流
				is = new ByteArrayInputStream(data);
				//hession解析二进制
				hessian2Input = new Hessian2Input(is);
				//hessian反序列化对象
				hessian2Input.startMessage();
				ret = hessian2Input.readObject();
				hessian2Input.completeMessage();
				
				//将对象写入output
				out.write(ret);
				//清除ioBuffer中已经读取过的内容
//					in.compact();
			}catch(Exception e){
				logger.error(e.getMessage(),e);
				throw e;
			}finally{
				if(hessian2Input != null){
					hessian2Input.close();
				}
				if(is != null){
					is.close();
				}
            }
            logger.debug("end decoder return true");
            return true;
        }else{//对象没有接受完毕，等待继续读取
            logger.debug("end decoder return false");
            return false;
        }

	}
}
