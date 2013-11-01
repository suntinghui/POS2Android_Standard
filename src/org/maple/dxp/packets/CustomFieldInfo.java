package org.maple.dxp.packets;

public class CustomFieldInfo {
	
	public CustomFieldInfo(String sn, String name, int len, String desc) {
		this.sn = sn;
		this.name = name;
		this.len = len;
		this.desc = desc;
	}

	String sn = null;// 序号
	String name = null;// 名称
	int len = -1; // 长度
	String desc = null; // 描述

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String toString() {

		return String.format("sn :%s , name :%s, len :%d, desc :%s", sn, name,
				len, desc);

	}
}
