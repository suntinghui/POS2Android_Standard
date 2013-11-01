package com.dhcc.pos.packets.util;

public class StringUtil {


	public static boolean isNotNull(String strIn) {
		if (strIn == null || "".equals(strIn)) {
			return false;
		}
		return true;
	}

	public static boolean isNull(String strIn) {
		if (strIn == null || "".equals(strIn)) {
			return true;
		}
		return false;
	}

	public static int convert(String value) {
		int result = 0;

		if (isNotNull(value)) {
			result = Integer.parseInt(value);
		}

		return result;
	}


}
