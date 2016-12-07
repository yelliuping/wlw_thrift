package com.wlw.thrift.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;

public class ThriftUri {
	
	
	private static void uri() throws URISyntaxException {
		String mUriStr = "thrift://127.0.0.1:8080/yourpath/fileName.htm?stove=10&path=32&id=4#harvic"; 
		URI mUri = new URI(mUriStr);  
	    String scheme=	mUri.getScheme();
	    String path   =	mUri.getPath();
	    String host    =	mUri.getHost();
	    int port      =	mUri.getPort();
	   String rawQuery= mUri.getRawQuery();
	   System.out.println("scheme:"+scheme);
	   System.out.println("path:"+path);
	   System.out.println("host:"+host);
	   System.out.println("port:"+port);
	   System.out.println("rawQuery:"+rawQuery);
	   System.out.println(getParams(rawQuery));
	}
	private static LinkedHashMap<String, String> getParams(String query){
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		if(query==null||query.trim().length()==0){
			return params;
		}
		String [] querys=query.split("&");
		if(querys.length==0){
			return params;
		}
		String [] temParams=null;
		for(String temParam:querys){
			if(temParam.contains("=")){
				temParams=temParam.split("=");
				if(temParams.length==2){
					params.put(temParams[0], temParams[1]);
				}
				
			}
		}
		return params;
	}
	

}
