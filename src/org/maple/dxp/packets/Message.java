package org.maple.dxp.packets;
import java.util.HashMap;
import java.util.Map;


public class Message {
	//响应码
	String transCode;
	//报头
	Map <String, String>map_head = new HashMap<String, String>();
	//消息
	 Map<String, String> map_body = new HashMap<String, String>();
	//循环体
	Map <String, String>map_cycle=new HashMap<String, String>();

	
	public Map<String, String> getMap_head() {
		return map_head;
	}

	public void setMap_head(Map<String, String> map_head) {
		this.map_head = map_head;
	}

	public Map<String, String> getMap_body() {
		return map_body;
	}

	public void setMap_body(Map<String, String> map_body) {
		this.map_body = map_body;
	}

	public Map<String, String> getMap_cycle() {
		return map_cycle;
	}

	public void setMap_cycle(Map<String, String> map_cycle) {
		this.map_cycle = map_cycle;
	}

	public  void setbody(String key,String value) {
	
		map_body.put(key,value);
	}
	
	public  void setcycle(String key,String value) {
	
		map_cycle.put(key,value);
	}
	
	public  void sethead(String key,String value) {
		
		map_cycle.put(key,value);
	}
	
	
	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}

	public static void main(String[] args) {
	
	}

}
