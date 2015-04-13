package com.zzc.codec;

import java.io.ByteArrayOutputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.caucho.hessian.io.Hessian2Output;

public class HessianEncoder extends ProtocolEncoderAdapter {
	private int maxDataLength;
	
	public HessianEncoder(int maxDataLength){
		this.maxDataLength = maxDataLength;
	}

	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		
		System.out.println("------>encoder");
		
//		UserBean userBean = (UserBean)message;
		
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
			
			//申请ioBuff
			IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
			//准备写入数据
			byte[] object = byteOutputStream.toByteArray();
			buffer.putInt(object.length);//先放入对象长度
			System.out.println("对象长度为----->"+object.length);
			buffer.put(object);//写入序列化对象
			
			//写入
			buffer.flip();//切换到读模式
			out.write(buffer);//写入
			out.flush();
		}catch(Exception e){
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
