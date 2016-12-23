namespace java com.freedom.rpc.thrift.common

service HelloWorldService {

 string helloWorldString(1:string content)

 bool   helloWorldBoolean(1:i32 number)

}
