package com.zzc.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicLong;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;

/**
 * 包装类
 * 此类将自动生成代理对象，对目标类进行包装
 * @author ying
 *
 */
public abstract class JavassistWrapper {
	
	private static AtomicLong COUNTER = new AtomicLong(0);//自增的计数器
	
	/**
	 * 包装的代理方法，代理对象统一使用此方法调用真实需要调用的方法
	 * @param o 真实的对象
	 * @param method 需要调用的方法
	 * @param paramType 调用的参数列表类型
	 * @param value 调用的参数列表值
	 * @return
	 */
	public abstract Object invoke(Object o, String method, Class[] paramType, Object[] value) throws InvocationTargetException;
	
	/**
	 * 创建一个代理对象
	 * @param c 要代理的目标对象(一般为接口名)
	 * @return
	 * @throws Exception
	 */
	public static JavassistWrapper create(Class<?> c) throws Exception{
		if(c.isPrimitive()){
			throw new IllegalArgumentException("基本类型无法生成代理");
		}
		
		String name = c.getName();//类名
		
		//构建invoke方法
		StringBuilder invokeMethod = new StringBuilder("public Object invoke(Object o, String method, Class[] paramType, Object[] value) throws ")
										.append(InvocationTargetException.class.getName())
										.append("{")
										.append(name)
										.append(" obj; try{ obj = ((").append(name).append(")$1); }catch(Throwable e){ throw new IllegalArgumentException(e); }");
		
		invokeMethod.append("System.out.println(\""+name+"\");");//test
		
		//获取当前类的方法列表
		Method[] methods = c.getMethods();
		if(hasMethods(methods)){
			invokeMethod.append(" try{");
			
			/**
			 * 参数名相同，参数个数相同，且参数类型相同，说明是同一个方法
			 */
			for(Method m : methods){
				if( m.getDeclaringClass() == Object.class ){ //忽略掉object中继承来的方法
					continue;
				}
				
				String mName = m.getName();//获取方法名
				invokeMethod.append(" if(\"")//方法名相同的
					.append(mName)
					.append("\".equals($2)");
				
				int mParamLength = m.getParameterTypes().length;//参数长度
				invokeMethod.append(" && ")
					.append(" $3.length == ")
					.append(mParamLength);//参数长度也相同
				
				//判断是否存在方法重写
				boolean override = false;
				for(Method m2 : methods){
					if(m2.equals(m) && m2.getName().equals(m.getName())){//不是同一个方法且方法类型相同
						override = true;
						break;
					}
				}
				//重写的话我们要判断所有参数类型一致
				if(override){
					if (mParamLength > 0) {
						for (int l = 0; l < mParamLength; l ++) {
							invokeMethod.append(" && ").append(" $3[").append(l).append("].getName().equals(\"")
								.append(m.getParameterTypes()[l].getName()).append("\")");
						}
					}
				}
				
				invokeMethod.append("){");
				
				//加入一段调试代码
				invokeMethod.append("System.out.println(\""+mName+"\");");//test
				
				//生成调用代码
				if( m.getReturnType() == Void.TYPE){//此方法没有返回值，直接调用
					invokeMethod.append("obj.")
						.append(mName)
						.append("(")
						.append(genArgs(m.getParameterTypes(), "$4"))
						.append(");return null;");
				}else{
					invokeMethod.append("return ($w)obj.")
						.append(mName)
						.append("(")
						.append(genArgs(m.getParameterTypes(), "$4"))
						.append(");");
				}
				
				invokeMethod.append("}");
			}
			
			invokeMethod.append(" } catch(Throwable e) { " );
			invokeMethod.append("     throw new java.lang.reflect.InvocationTargetException(e); " );
			invokeMethod.append(" }");
		}
		
		invokeMethod.append(" throw new ")
			.append(NoSuchMethodException.class.getName())
			.append("(\"Not found method \\\"\"+$2+\"\\\" in class ")
			.append(c.getName())
			.append(".\"); }");
		
		//构建对象
//		ClassLoader cl = c.getClassLoader();
		ClassPool pool = ClassPool.getDefault();//全部使用默认类加载器
		long id = COUNTER.getAndIncrement();
		CtClass cc = pool.makeClass((Modifier.isPublic(c.getModifiers())? JavassistWrapper.class.getName() : c.getName()+"$sw")+id);//起一个名字
		try {
			cc.setSuperclass(pool.get(JavassistWrapper.class.getName()));//增加父类
			cc.addConstructor(CtNewConstructor.defaultConstructor(cc));//增加默认构造方法
			
			//增加方法
			CtMethod method = CtNewMethod.make(invokeMethod.toString(), cc);
			cc.addMethod(method);
			
			//生成class
			Class<?> targetClass = cc.toClass();
			return (JavassistWrapper) targetClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			cc.detach();
		}
	}
	
	/**
	 * @param cs
	 * @param value
	 * @return
	 */
	private static String genArgs(Class<?>[] cs,String value){
		int len = cs.length;
		if( len == 0 ) return "";
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<len;i++)
		{
			if( i > 0 )
				sb.append(',');
			sb.append(arg(cs[i],value+"["+i+"]"));
		}
		return sb.toString();
	}
	
	/**
	 * 根据传入类型，返回方法的参数代码，比如
	 * (java.lang.String) name
	 * @param cl
	 * @param name
	 * @return
	 */
	private static String arg(Class<?> cl, String value){
		if( cl.isPrimitive() )
		{
			if( cl == Boolean.TYPE )
				return "((Boolean)" + value + ").booleanValue()";
			if( cl == Byte.TYPE )
				return "((Byte)" + value + ").byteValue()";
			if( cl == Character.TYPE )
				return "((Character)" + value + ").charValue()";
			if( cl == Double.TYPE )
				return "((Number)" + value + ").doubleValue()";
			if( cl == Float.TYPE )
				return "((Number)" + value + ").floatValue()";
			if( cl == Integer.TYPE )
				return "((Number)" + value + ").intValue()";
			if( cl == Long.TYPE )
				return "((Number)" + value + ").longValue()";
			if( cl == Short.TYPE )
				return "((Number)" + value + ").shortValue()";
			throw new RuntimeException("Unknown primitive type: " + cl.getName());
		}
		return "(" + classToStr(cl) + ")" + value;
	}
	
	/**
	 * 将参数类型解析为字符串
	 * 比如c为 java.lang.Object[]
	 * 		  java.lang.Ojbect
	 * @param c
	 * @return
	 */
	public static String classToStr(Class<?> c){
		if( c.isArray() )
		{
			StringBuilder sb = new StringBuilder();
			do
			{
				sb.append("[]");
				c = c.getComponentType();
			}
			while( c.isArray() );

			return c.getName() + sb.toString();
		}
		return c.getName();
	}
	
	/**
	 * 判断方法列表是否为空
	 * @param methods
	 * @return
	 */
	private static boolean hasMethods(Method[] methods){
	    if(methods == null || methods.length == 0){
	        return false;
	    }
	    for(Method m : methods){
	        if(m.getDeclaringClass() != Object.class){
	            return true;
	        }
	    }
	    return false;
	}
}
