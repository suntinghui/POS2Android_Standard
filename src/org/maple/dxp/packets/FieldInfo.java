package org.maple.dxp.packets;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FieldInfo {

	private String name;

	private FiledType type;

	// 消息长度
	private int length;
	//值
	private String value;
	// 精度
	private boolean isMustFiled;

	public FieldInfo(FiledType type, String name, int length) {
		this.name = name;
		this.type = type;
		this.length = length;
	}

	public FieldInfo(String name, String value,boolean isMustFiled) {
		this.name = name;
		this.isMustFiled = isMustFiled;
		this.value = value;
	}


	/**
	 * 解析字段信息
	 * 
	 * @param buf
	 * @param pos
	 * @return
	 * @throws ParseException
	 */
	public FiledValue<?> parse(String msg) throws RuntimeException {
		try{
		if (type == FiledType.NUMERIC || type == FiledType.ALPHA) {
			return new FiledValue<String>(type, msg, length);
		} else if (type == FiledType.LLVAR) {
			return new FiledValue<String>(type, msg, length);
		} else if (type == FiledType.LLLVAR) {
			return new FiledValue<String>(type, msg, length);

		} else if (type == FiledType.AMOUNT) {
			return new FiledValue<BigDecimal>(type, new BigDecimal(msg),
					length, 4);
		} else if (type == FiledType.TIME) {
			Date data = new SimpleDateFormat("HHmmss").parse(msg);
			return new FiledValue<Date>(type, data);

		} else if (type == FiledType.DATE4) {
			Date data = new SimpleDateFormat("MMdd").parse(msg);
			return new FiledValue<Date>(type, data);
		
		} else if (type == FiledType.DATE8) {
			Date data = new SimpleDateFormat("yyyyMMdd").parse(msg);
			return new FiledValue<Date>(type, data);

		} else if (type == FiledType.DATE10) {
			Date data = new SimpleDateFormat("MMddHHmmss").parse(msg);
			return new FiledValue<Date>(type, data);

		} else if (type == FiledType.DATE_EXP) {
			Date data = new SimpleDateFormat("yyMM").parse(msg);
			return new FiledValue<Date>(type, data);

		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean getIsMustFiled() {
		return isMustFiled;
	}

	public void setIsMustFiled(boolean isMustFiled) {
		this.isMustFiled = isMustFiled;
	}
}
