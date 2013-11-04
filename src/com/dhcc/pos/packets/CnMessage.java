package com.dhcc.pos.packets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dhcc.pos.packets.util.ConvertUtil;

/**
 * iso 8583 消息信息   报文总长度（2-4字节）+报头信息+报文类型+位图（8（64位图）或16（128位图））+各字段域+结束符
 */
public class CnMessage {
	public String bitMap = null;

	/** 消息类型 */
	private String msgtypeid;

	/** 如果设置为true, 报文中的各报文域按照二进制组成报文 */
	private boolean isbinary;

	/** bit map 位图 */
	private Map<Integer, cnValue<?>> fields = new ConcurrentHashMap<Integer, cnValue<?>>();

	/**
	 * 报头信息
	 */
	private byte[] msgTPDU;
	
	/**
	 * 报头信息
	 */
	private byte[] msgHeader;

	/**
	 * 消息结束符，表示消息是否已完成，默认-1表示没结束
	 */
	private int etx = -1;

	public CnMessage() {
		
	}

	/**
	 * 创建 一个指定类型的8583消息
	 * 
	 * @param msgtypeid
	 *            消息类型
	 * @param msgTPDUlength ,msgHeaderlength
	 */
	public CnMessage(String msgtypeid,int msgTPDUlength, int msgHeaderlength) {
		this.msgtypeid = msgtypeid;
		
		/*赋给msgHeader的容量*/
		msgHeader = new byte[msgHeaderlength];
		
		/*赋给msgTPDU的容量*/
		msgTPDU = new byte[msgTPDUlength];
	}

	/** 获取报头信息 */
	public byte[] getmsgHeader() {
		return msgHeader;
	}

	/** 获取TPDU信息 */
	public byte[] getmsgTPDU() {
		return msgTPDU;
	}
	
	/**
	 * 设置报文头的数据，由于不同的报文报文的格式完全不同，所以直接设置报文的字节数据。
	 * 
	 * @param startindex
	 *            待设置报文头的起始字节位置。（0为第一个位置）
	 * @param data
	 *            要设置的数据，（长度为data的长度，startindex和data的长度的和应小于报文头的总长度）
	 * @return 是否设置成功
	 */
	public boolean setMessageHeaderData(int startindex, byte[] data) {
		if (startindex + data.length > msgHeader.length) {
			return false;
		}
		for (int i = 0; i < data.length; i++) {
			msgHeader[startindex + i] = data[i];
		}
		return true;
	}

	public boolean setMessageTPDUData(int startindex, byte[] data) {
		if (startindex + data.length > msgTPDU.length) {
			return false;
		}
		for (int i = 0; i < data.length; i++) {
			msgTPDU[startindex + i] = data[i];
		}
		return true;
	}
	
	/**
	 * 从报文头中取得数据
	 * 
	 * @param startindex
	 *            起始字节位置（0为第一个位置，应小于报文头的总长度）
	 * @param count
	 *            需要取得的字节数（正整数） 如遇到报文尾，则取得实际能取道的最大字节数
	 * @return 取得的数据（如未取得则返回null）
	 */
	public byte[] getMessageHeaderData(int startindex, int count) {
		if (startindex >= msgHeader.length) {
			return null;
		}
		byte[] b = null;
		if (msgHeader.length - startindex < count)
			b = new byte[msgHeader.length - startindex];
		else
			b = new byte[count];
		for (int i = 0; i < b.length; i++) {
			b[i] = msgHeader[startindex + i];
		}
		return b;
	}

	/** 设置消息类型. 应该为4字节字符串 */
	public void setMsgTypeID(String msgtypeid) {
		this.msgtypeid = msgtypeid;
	}

	/**  获取消息类型. */
	public String getMsgTypeID() {
		return msgtypeid;
	}

	/**
	 * 设置各报文域是否按照料二进制组成报文,  默认false
	 * 如果设置为true, 报文中的各报文域按照二进制组成报文。(报文头、报文类型标示和位图不受影响)
	 */
	public void setBinary(boolean flag) {
		isbinary = flag;
	}

	/**
	 * 
	 * 报文中的各报文域按照二进制组成报文。(报文头、报文类型标示和位图不受影响)
	 */
	public boolean isBinary() {
		return isbinary;
	}

	/**
	 * 报文组装完成设置结束符
	 */
	public void setEtx(int value) {
		etx = value;
	}


	/**
	 * 返回字段域数值（ 应该在2-128范围，1字段域用来存放位图）
	 * @param fieldid 字段域id 
	 * @return
	 */
	public Object getObjectValue(int fieldid) {
		cnValue<?> v = fields.get(fieldid);
		if (v == null) {
			return null;
		}
		return v.getValue();
	}

	/**
	 * 返回字段域数值（ 应该在2-128范围，1字段域用来存放位图）
	 * @param fieldid 字段域id 
	 * @return
	 */
	public cnValue<?> getField(int fieldid) {
		return fields.get(fieldid);
	}

	/**
	 * 设置字段域，由于字段域1被 用来存放位图，设置字段域应从2开始
	 * @param fieldid 字段域id 
	 * @param field 字段数值
	 */
	public void setField(int fieldid, cnValue<?> field) {
		if (fieldid < 2 || fieldid > 128) {
			throw new IndexOutOfBoundsException(
					"Field index must be between 2 and 128");
		}
		if (field == null) {
			fields.remove(fieldid);
		} else {
			fields.put(fieldid, field);
		}
	}


	/**
	 * 设置字段域，由于字段域1被 用来存放位图，设置字段域应从2开始
	 * @param fieldid 字段域id 
	 * @param value  数值
	 * @param t  类型
	 * @param length 长度
	 */
	public void setValue(int fieldid, Object value, cnType type, int length) {
		if (fieldid < 2 || fieldid > 128) {
			throw new IndexOutOfBoundsException(
					"Field index must be between 2 and 128");
		}
		if (value == null) {
			fields.remove(fieldid);
		} else {
			cnValue<?> v = null;
			if (type.needsLength() | type == cnType.LLNVAR | type == cnType.LLLNVAR) {
				v = new cnValue<Object>(type, value, length);
			} else {
				v = new cnValue<Object>(type, value);
			}
			fields.put(fieldid, v);
		}
	}

	/**
	 * 是否存在该字段
	 * @param fieldid
	 * @return
	 */
	public boolean hasField(int fieldid) {
		return fields.get(fieldid) != null;
	}

	/**
	 * 将消息写入到输出流中
	 * @param outs 输出流
	 * @param lengthBytes  报文长度头（2或4个字节，用来存放整个报文长度，一搬4个字节） 
	 * @param radixoflengthBytes   表示整个报文长度的字节（lengthBytes）的表示进制（只能取10或16）
	 * @throws IllegalArgumentException
	 *             if the specified length header is more than 4 bytes.
	 * @throws IOException
	 *             if there is a problem writing to the stream.
	 */
	public void write(OutputStream outs, int lengthBytes, int radixoflengthBytes)
			throws IOException {
		
		if (lengthBytes > 4) {
			throw new IllegalArgumentException(
					"The length header can have at most 4 bytes");
		}
		byte[] data = writeInternal();

		int len = data.length;
		if (etx > -1) {
			len++;
		}
		if (lengthBytes >= 2) {
			if (radixoflengthBytes == 16) { // 如果以十六进制表示

				byte[] buf = new byte[lengthBytes];
				int pos = 0;
				if (lengthBytes == 4) {
					buf[0] = (byte) ((len & 0xff000000) >> 24);
					pos++;
				}
				if (lengthBytes > 2) {
					buf[pos] = (byte) ((len & 0xff0000) >> 16);
					pos++;
				}
				if (lengthBytes > 1) {
					buf[pos] = (byte) ((len & 0xff00) >> 8);
					pos++;
				}
				buf[pos] = (byte) (len & 0xff);
				outs.write(buf);

			} else if (radixoflengthBytes == 10) { // 如果为10进制
				int l = data.length;
				if (etx > -1) {
					l++;
				}
				byte[] buf = new byte[lengthBytes];
				int temp = 1;
				for (int i = 0; i < lengthBytes; i++) {
					buf[lengthBytes - 1 - i] = (byte) (0x30 + ((len / (temp)) % 10));
					temp = temp * 10;
				}
				outs.write(buf);

			} else {
				throw new IllegalArgumentException("参数错，进制只能为10或16");
			}
		}

		outs.write(data);
		
		// ETX
		if (etx > -1) {
			outs.write(etx);
		}
		outs.flush();
	}

	
	/**
	 * 创建一个字节缓冲区，包含报头，消息数据长度，结束符长度
	 * @param lengthBytes（2或4个字节，用来存放整个报文长度） 
	 * @return
	 */
	public ByteBuffer writeToBufferx(int lengthBytes) {
		if (lengthBytes > 4) {
			throw new IllegalArgumentException(
					"The length header can have at most 4 bytes");
		}

		byte[] data = writeInternal();
		ByteBuffer buf = ByteBuffer.allocate(lengthBytes + data.length
				+ (etx > -1 ? 1 : 0));
		if (lengthBytes > 0) {
			int l = data.length;
			if (etx > -1) {
				l++;
			}
			byte[] bbuf = new byte[lengthBytes];
			int pos = 0;
			if (lengthBytes == 4) {
				bbuf[0] = (byte) ((l & 0xff000000) >> 24);
				pos++;
			}
			if (lengthBytes > 2) {
				bbuf[pos] = (byte) ((l & 0xff0000) >> 16);
				pos++;
			}
			if (lengthBytes > 1) {
				bbuf[pos] = (byte) ((l & 0xff00) >> 8);
				pos++;
			}
			bbuf[pos] = (byte) (l & 0xff);
			buf.put(bbuf);
		}
		buf.put(data);
		// ETX
		if (etx > -1) {
			buf.put((byte) etx);
		}
		buf.flip();
		return buf;
	}


	/**
	 * 
	 * 返回报文内容 ,不包含前报文总长度及结束符
	 * @return位图[8字节]+ 11域【3字节BCD码】+其余所有域值（个别域值前加上BCD码压缩的2个字节的长度值_左补0）
	 */
	public byte[] writeInternal() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		// Bitmap
		ArrayList<Integer> keys = new ArrayList<Integer>();
		keys.addAll(fields.keySet());
		Collections.sort(keys);
		BitSet bs = new BitSet(64);
		
		for (Integer i : keys) { // BitSet可以自动扩展大小
			bs.set(i - 1, true);
		}
		// Extend to 128 if needed
		if (bs.length() > 64) {
			BitSet b2 = new BitSet(128);
			b2.or(bs); // 得到位图(根据域的个数，可能自动扩展)
			bs = b2;
			/*当bs长度大于64时 设定第一位为true*/
			bs.set(0, true);
		}
		// Write bitmap into stream
		int pos = 128; // 用来做位运算： -- 1000 0000（初值最高位为1，然后右移一位，等等）
		int b = 0; // 用来做位运算：初值二进制位全0
		for (int i = 0; i < bs.size(); i++) {
			if (bs.get(i)) {
				b |= pos;
			}
			pos >>= 1;
		
			if (pos == 0) { // 到一个字节时（8位），就写入
				bout.write(b);
				pos = 128;
				b = 0;
			}
			
		}
		System.out.println("位图长度:\t" + bout.toByteArray().length + "\r十六进制位图：\r"+ConvertUtil.trace(bout.toByteArray()));

		
		bitMap = ConvertUtil.bytesToHexString(bout.toByteArray());
		
		System.out.println("bitMap[" + bitMap + "]");
		
		/**Fields
		 * 紧跟着位图后面 位图所有域的值
		 * */ 
		for (Integer i : keys) {
			cnValue v = fields.get(i);
			/**当i不等于该域时 证明该域不需要加长度值*/
			if(i!=52){
				if(v.getType()==cnType.LLVAR|v.getType()==cnType.LLNVAR){
					int length = v.getValue().toString().length();
					
					byte[] byteFieldLLVAR = ConvertUtil.str2Bcd_(String.format("%02d", length));
					
					try {
						bout.write(byteFieldLLVAR);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else if(v.getType()==cnType.LLLVAR|v.getType()==cnType.LLLNVAR){
					int length = v.getValue().toString().length();
					
					byte[] byteFieldLLLVAR = ConvertUtil.str2Bcd_(String.format("%04d", length));
					
					try {
						bout.write(byteFieldLLLVAR);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			
			try {
				v.write(bout, isbinary,i);
			} catch (IOException ex) {
				// should never happen, writing to a ByteArrayOutputStream
			}
		}
		return bout.toByteArray();
	}

	/**
	 * 根据当前的报文内容，估计最终报文的的长度（单位为字节）
	 * 
	 * @return 估算出来的报文字节个数（含报文头、报文类型标示、位图和各个有效的报文域）
	 */
	public int estimatetotalmsglength() {
		int totalmsglen = 0;
		// 报文头长度
		if (msgHeader != null) 
			totalmsglen += msgHeader.length;
		
		//报文类型标示长度
		if (msgtypeid != null) // 报文类型标示
			totalmsglen += msgtypeid.length();

		// 位图
		ArrayList<Integer> keys = new ArrayList<Integer>();
		keys.addAll(fields.keySet());
		Collections.sort(keys);
		if (keys.get(keys.size() - 1) <= 64) // 如果最大的一个域ID小于等于64
			totalmsglen += 8;
		else
			totalmsglen += 16;

		// 报文域
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		for (Integer i : keys) {
			cnValue<?> v = fields.get(i);
			try {
				v.write(bout, isbinary,i);
			} catch (IOException ex) {
				// should never happen, writing to a ByteArrayOutputStream
			}
		}
		totalmsglen += bout.toByteArray().length;
		return totalmsglen;
	}
}
