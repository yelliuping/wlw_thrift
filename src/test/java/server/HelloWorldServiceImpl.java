package server;

import org.apache.thrift.TException;


public class HelloWorldServiceImpl implements service.HelloWorldService.Iface {

	public String helloWorldString(String content) throws TException {
		return "欢迎star ---> " + content;
	}

	public boolean helloWorldBoolean(int number) throws TException {
		return false;
	}

}
