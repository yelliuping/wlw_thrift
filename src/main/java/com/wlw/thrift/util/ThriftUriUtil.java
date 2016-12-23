package com.wlw.thrift.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;

import com.wlw.thrift.entity.ProtocolModel;

public class ThriftUriUtil {


	public static ProtocolModel uri(String uri) throws URISyntaxException {
		ProtocolModel model = new ProtocolModel();
		URI    mUri = new URI(uri);
		model.setScheme(mUri.getScheme());
		model.setHost( mUri.getHost());
		model.setPort(mUri.getPort());
		model.setServer( mUri.getPath().replaceAll("/", ""));
		String rawQuery = mUri.getRawQuery();
		LinkedHashMap<String, String> params=getParams(rawQuery);
		model.setServices(params.get("services"));
		return model;
	}

	private static LinkedHashMap<String, String> getParams(String query) {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		if (query == null || query.trim().length() == 0) {
			return params;
		}
		String[] querys = query.split("&");
		if (querys.length == 0) {
			return params;
		}
		String[] temParams = null;
		for (String temParam : querys) {
			if (temParam.contains("=")) {
				temParams = temParam.split("=");
				if (temParams.length == 2) {
					params.put(temParams[0], temParams[1]);
				}

			}
		}
		return params;
	}
	
	public static void main(String[] args) {
		 try {
			 String http="thrift://192.168.191.1:10004/HelloWorldServer?isMutil=true&state=1&services=[HelloWorldService]";
			ProtocolModel uri=uri(http);
			System.out.println(uri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
