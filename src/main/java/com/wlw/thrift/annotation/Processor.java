package com.wlw.thrift.annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Processor {
	String server() default "";//服务器名称（和启动server的时候的名称一致）客户端调用时使用
	String service() default "";// 服务接口名称

}