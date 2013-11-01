package org.maple.dxp.packets;

public class CustomMsgInfo {

	public CustomMsgInfo(String id, int len, String cond, String value,
			String bitMap) {
		this.id = id;
		this.len = len;
		this.cond = cond;
		this.value = value;
		this.bitMap = bitMap;
	}

	String id = null;
	int len = -1;
	String cond = null;
	String value = null;
	String bitMap = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public String getCond() {
		return cond;
	}

	public void setCond(String cond) {
		this.cond = cond;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getBitMap() {
		return bitMap;
	}

	public void setBitMap(String bitMap) {
		this.bitMap = bitMap;
	}

	public String toString() {

		return String.format(
				" id :%s, len :%d, cond :%s, value :%s, bitMap :%s", id, len,
				cond, value, bitMap);

	}

}
