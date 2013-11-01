package com.dhcc.pos.constant;

import java.util.HashMap;
import java.util.Map;

public class Constant {

	/*private final static String encode = "GBK";*/
	public final static String jsonEncode = "GBK";
	public final static String xmlEncode = "UTF-8";
	
	public static Map<String, Object> cache = null;
	
	public static boolean isLinux = false;
	
	/*isOk true：写入 false:不写入 reqMsgFile_json:请求报文写入路径（json格式） respMsgFile_json:响应报文写入路径（json格式） respMsgFile_xml：响应报文写入路径（xml格式）*/
	public static boolean isOk = false;
	public static String reqMsgFile_json;
	public static String respMsgFile_json;
	public static String respMsgFile_xml;
	/* path:储存图片路径 */
	public static String path;
	/**
	 * 流水号存放路径
	 */
	public static String numPath;
	
	
	private Constant() {
		
	}

	static {
		if(isLinux){
			reqMsgFile_json = "/home/aps/posTemp/reqMsg";
			respMsgFile_json = "/home/aps/posTemp/respMsg";
			respMsgFile_xml = "/home/aps/posTemp/respMsg";
			path = "/home/aps/posTemp/img/";
			
		}else{
			/*mac*/
			reqMsgFile_json = "/Users/xs/Desktop/posTmp/reqMsg";
			respMsgFile_json = "/Users/xs/Desktop/posTmp/respMsg";
			respMsgFile_xml = "/Users/xs/Desktop/posTmp/respMsg";
			path = "/Users/xs/Desktop/img/";
			numPath = "/Users/xs/Desktop/temp/";
		}
		
	}

	public static Map<String, Object> getCache() {
		return cache;
	}

	public static String getRespMsgFile_json() {
		return respMsgFile_json;
	}

	public static String getRespMsgFile_xml() {
		return respMsgFile_xml;
	}

	public static void setCache(Map<String, Object> cache) {
		Constant.cache = cache;
	}

	public static String getReqMsgFile_json() {
		return reqMsgFile_json;
	}

	public static void setRespMsgFile_json(String respMsgFile_json) {
		Constant.respMsgFile_json = respMsgFile_json;
	}

	public static void setRespMsgFile_xml(String respMsgFile_xml) {
		Constant.respMsgFile_xml = respMsgFile_xml;
	}

	public static Map<String, Object> getCacheInstance() {
		if (cache == null) {
			cache = new HashMap<String,Object>();
		}
		return cache;
	}

	public static String getXmlEncode() {
		return xmlEncode;
	}

	public static String getJsonEncode() {
		return jsonEncode;
	}
	

/*	public static String getEncode() {
		return encode;
	}*/
}
