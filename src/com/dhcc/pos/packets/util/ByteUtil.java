package com.dhcc.pos.packets.util;

import java.util.Arrays;



/***
 * 格式化工具
 * @author maple
 *
 */
public class ByteUtil {
	/**
	 * 把一个字节数组的串格式化成十六进制形式. 
	 * 格式化后的样式如下<br></br>
	 * <blockquote> 
	 *  00000H  61 62 63 64 D6 D0 B9 FA 73 73 73 73 73 73 73 73 ; abcd中国ssssssss  <br></br>
 	 *  00016H  73 73 73 73 73 73 73 73 73 B1 B1 BE A9 64 64 64 ; sssssssss北京ddd  <br></br>
 	 *  00032H  64 64 64 64 64 64 64 64 64 64 64 64 64 64 64 64 ; dddddddddddddddd  <br></br>
 	 * </blockquote> 
	 * @param b 需要格式化的字节数组
	 * @return 格式化后的串，其内容如上。可以直接输出。
	 */
	public static String formatbytes2Hex(byte[] b) {
		String result_str = "";
		byte[] chdata = new byte[19];  // 只保存十六进制串后面的字符串 (" : " 就占了三个字节，后面为16个字节)
		for (int i = 0; i < b.length; i++) {
			String hex_of_one_byte = Integer.toHexString((int) b[i]).toUpperCase();
			if (i % 16 == 0) {		
				result_str = result_str + new String(chdata) + "\n ";
				Arrays.fill(chdata, (byte)0x00);
				System.arraycopy(" ; ".getBytes(), 0, chdata, 0, 3);
				for (int j = 0; j < 5 - String.valueOf(i).length(); j++) 
					result_str = result_str + "0";
				result_str = result_str + i + "H ";
			}
			if (hex_of_one_byte.length() >= 2) 
				result_str = result_str + " " + hex_of_one_byte.substring(hex_of_one_byte.length() - 2);
			else 
				result_str = result_str + " 0" + hex_of_one_byte.substring(hex_of_one_byte.length() - 1);
			System.arraycopy(b, i, chdata, 3 + (i %16), 1);
		}
		for(int j = 0; j < (16 - (b.length % 16 )) %16; j++)
			result_str = result_str + "   ";
		result_str = result_str + new String(chdata);
		return result_str;
	}
	
	/**
	 * <p> 将1~4字节的byte数组内容转换为一个int,如果byte数组的长度为1~3,则高位补零 </p>
	 * @param b
	 * @return 转化后的值，如果转化失败，则返回为 Integer.MIN_VALUE
	 * @author:张瑜平
	 * @throws IllegalArgumentException 参数非法时
	 */
	public static int byte2int(byte[] b) throws IllegalArgumentException {
		int count = b.length;
		if (count < 1 || count > 4) {
			return Integer.MIN_VALUE;
		}
		int result = 0; // 初始为0
		for (int i = 0; i < count; i++) {
			result = (b[i] & 0xFF) | result;
			if (i < count - 1) // 如不是最后一次循环才左移8为，最后一次不左移。
				result = result << 8;
		}
		return result;
	}
	
	/**
	 * <p>把一个int值（共占4字节），按字节转化成byte.（即对于每个字节，其存储的二进制不变，只是把int翻译成byte）</p>
	 * 转化的个数由count（count应大于0小于等于4）指定，顺序为从低到高。返回的长度为count的byte数组中，byte[0]放数学上的最高位，byte[count-1]放最低位。<br/>
	 * <blockquote> 
	 * 例如int数字为41111111，其存储二进制为 00000010     01110011    01001110     01000111 &nbsp &nbsp <br/>
	 * 当count为4时，则返回的byte分别对应为: &nbspbyte[0]&nbsp byte[1]&nbsp byte[2]&nbsp byte[3] &nbsp &nbsp <br/>
	 * 当count为3时，则返回的byte分别对应为: &nbsp &nbsp &nbsp &nbsp &nbsp byte[0]&nbsp byte[1]&nbsp byte[2] &nbsp &nbsp <br/>
	 * 当count为2时，则返回的byte分别对应为: &nbsp&nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp byte[0]&nbsp byte[1] &nbsp &nbsp <br/>
	 * 当count为1时，则返回的byte分别对应为: &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp &nbsp byte[0] &nbsp &nbsp <br/>
	 * </blockquote> 
	 * @param value 待转换的int数值
	 * @param count 字节数组的长度
	 * @return 转化后的字节数组
	 */
	// 把一个int值（共占4字节），按字节转化成byte。（即对于每个字节，其存储的二进制不变，只是把int翻译成byte）
	// 转化的个数由count（count应大于0小于等于4）指定，顺序为从低到高。返回的长度为count的byte数组中，byte[0]放数学上的最高位，byte[count-1]放最低位。
	// 例如int数字为41111111，其存储二进制为 00000010  01110011  01001110  01000111
	// 当count为4时，则返回的byte分别对应为: byte[0]   byte[1]   byte[2]   byte[3]
	// 当count为3时，则返回的byte分别对应为:           byte[0]   byte[1]   byte[2]
	// 当count为2时，则返回的byte分别对应为:                     byte[0]   byte[1]
	// 当count为1时，则返回的byte分别对应为:                               byte[0]
	public static byte[] int2byte(int value, int count) {
		count = (count > 4 || count < 1) ? 4 : count;
		byte[] b = new byte[count];
		for (int i = 0; i < count; i++) {
			b[count - 1 - i] = (byte) Integer.rotateRight(value, i * 8);
		}
		return b;
	}
	
	/**
	 * 把字符形式的数字转化为二进制形式
	 * 如字符形式的 '0' (0x31)  将转化为  0 (0x01)     <br/>
	 * 如已经是二进制形式，则不进行转化
	 * @param digit_c 数字串
	 * @return 二进制形式的数字串
	 */
	public static byte[] digit_c2b(byte[] digit_c){
		byte [] digit_b = new byte[digit_c.length];
		for(int i = 0; i < digit_c.length; i++ ){
			if( ((digit_c[i] > 0x09) && (digit_c[i] < 0x30))    
				|| digit_c[i] > 0x39 || digit_b[i] < 0x00)
				System.out.println("方法digit_c2b只能传入数字（字符或二进制形式）参数，方法调用失败! ");
			
			if(digit_c[i] <= 0x09)
				digit_b[i] = digit_c[i];
			else
				digit_b[i] = (byte) (digit_c[i] - 0x30);
		}
		return digit_b;
	}
	
	/**
	 * 把二进制形式的数字转化为字符形式
	 * 如字符形式的  0 (0x01) 将转化为 '0' (0x31) <br/>
	 * 如已经是字符形式，则不进行转化
	 * @param digit_b 数字串
	 * @return 字符形式的数字串
	 */
	public static byte[] digit_b2c(byte[] digit_b) throws  IllegalArgumentException{
		byte [] digit_c = new byte[digit_b.length];
		for(int i = 0; i < digit_c.length; i++ ){
			if( ((digit_b[i] > 0x09) && (digit_b[i] < 0x30))    
					|| digit_b[i] > 0x39 || digit_b[i] < 0x00)
					System.out.println("方法digit_b2c只能传入数字（字符或二进制形式）参数，方法调用失败! ");
			
			if(digit_b[i] >= 0x30)
				digit_c[i] = digit_b[i];
			else
				digit_c[i] = (byte) (digit_b[i] + 0x30);	
		}
		return digit_c;
	}
	
	/**
	 * 判断传来的参数中全是都是数字（都在 0x00 ~ 0x09, 0x30 ~ 0x3F的范围）
	 * @param b 
	 * @return 如全是数字，则true，否则false。
	 */
	public static boolean isdigitAll(byte[] b) {
		for(int i = 0; i < b.length; i++) {
			if(b[i] < 0x00 || b[i] > 0x3F 
			|| (b[i] > 0x09 && b[i] < 0x30))
				return false;
		}
		return true;
	}
}
