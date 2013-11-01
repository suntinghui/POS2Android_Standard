package com.dhcc.pos.core;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.maple.dxp.packets.FieldInfo;

import com.dhc.pos.R;
import com.dhc.pos.agent.client.ApplicationEnvironment;
import com.dhcc.pos.packets.CnMessage;
import com.dhcc.pos.packets.CnMessageFactory;
import com.dhcc.pos.packets.cnType;
import com.dhcc.pos.packets.parse.cnConfigParser;
import com.dhcc.pos.packets.parse.cnFieldParseInfo;
import com.dhcc.pos.packets.util.ClassLoaderUtil;
import com.dhcc.pos.packets.util.ConvertUtil;
import com.dhcc.pos.packets.util.StringUtil;

public class TxActionImp implements TxAction {

	SocketTransport socketTransport;

	CnMessageFactory mfact;
	
	CnMessage m;

	/* 注入交易处理码 */
	Map<String, Object> resp_code_map;

	/* 联机交易流水类交易码 */
	static Map<String, Object> transCode;
	/**
	 * */
	static Map<String, Object> transTypeMap;
	
	private TransDispatcher transDispatcher;
//	static TxContext txContext = null;
	static Map<String, Object> req_map = null;
	
	/*DOTO:输出请求报文*/
	String msgIn = null;
	/*报文类型（交易码）*/
	static String msgType = null;
	String resp_json = null;

	/* 静动开关，true为动态 非true为静态（回路） */
	private String isDynamic = "true";

	/* 是否为linux 环境true为是 false为不是 */
	private boolean isLinux = true;

	/*
	 * 静态(回路) responsePath 为静态响应报文路径
	 * 
	 * 例：D:\\responseXml\\800003.xml responsePath="D:\\responseXml\\"
	 */
	private String responsePath;

	static{
		transCode = new HashMap<String, Object>();
		transCode.put("0200", "消费");
		
		transTypeMap = new HashMap<String, Object>();
		transTypeMap.put("000000", "txAction");
	}
	/*
	 * InitializingBean接口的实现方法 bean初始化（spring中配置文件bean全部注入完后执行此函数）
	 */
	public void afterPropertiesSet() {
		// System.out.println("####################afterPropertiesSet####################"
		// + "\r");
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void first(TxContext txContext){
		req_map = txContext.getReq_map();
		// 请求报文中取得交易码
		msgType = (String) req_map.get("fieldTrancode");

		if (msgType == null)
			throw new IllegalArgumentException("请求报文未有消息类型(交易码)");
		if (msgType.length() < 4)
			throw new IllegalArgumentException("请求报文异常交易码:" + msgType);

		txContext.setClient_msgType(msgType);

		msgType = msgType.substring(0, 4);
		txContext.setMsgType(msgType);

		req_map.put("fieldTrancode", msgType);

		// 接收到的请求报文map类型传给容器
		txContext.setReq_map(req_map);
		transDispatcher = new TransDispatcher();
		
		TxAction action = transDispatcher.dispatcher(msgType);
		// 进入主处理函数 处理请求
		action.process(txContext);
	}
	
	/*
	 * 主处理函数
	 */
	public void process(TxContext context) {
		System.out.println("\t####################process####################" + "\r");
		
		
		byte[] respMsg = null;
		
		
		try {
			// 创建请求数据
			byte[] reqMsg = beforeProcess(context);
			// 发送请求数据(编码格式为gbk) 并接受响应数据
			if (isDynamic.equals("true")) {

				System.out.println("动态");
				
				socketTransport = new SocketTransport();
				
				respMsg = socketTransport.sendData(reqMsg,mfact,m,context);
				
				
				context.setResp_byte(respMsg);
				
				// 解析返回(响应)数据
				afterProcess(context);
				
				
			} else {
				System.out.println("静态（回路）");
			}

			

			// 把返回（响应）数据放到容器logAction类Resp_xml（String类型）中
			// context.setResp_xml(Resp_xml);
			// 解析返回(响应)数据
			// afterProcess(context);
			// 结果处理：日志记录，流水记录；
			// processResult(context);
		} catch (Exception e) {
			//
			e.printStackTrace();
			String error = e.getMessage();
			context.setDes(error);
			context.getResp_map().put("field39", "11");
			context.getResp_map().put("fieldMessage", context.getDes());
			// 处理异常
			processError(context);
		}
	}

	public CnMessage registerReqMsg(CnMessage m, TxContext context) {
		Map<String, Object> req_map = null;
		
		String msgType = context.getMsgType();
		
		Map<Integer, cnFieldParseInfo> parseMap = mfact
				.getParseMap(msgType);
		
		/**TPDU和header一起组装
		String TPDU = "6000120000";
		String header = "602210000000";
		byte[] tpduByte = ConvertUtil.str2Bcd(TPDU);
		byte[] headerByte = ConvertUtil.str2Bcd(header);

		
		
	
		 ByteBuffer sendBuf = ByteBuffer.allocate(tpduByte.length
				+ headerByte.length);
		sendBuf.put(tpduByte);
		sendBuf.put(headerByte);

		if (m.setMessageHeaderData(0, sendBuf.array()) == false) {
			System.out.println("设置报文头出错。");
			System.exit(-1);
		}*/
		
		// 根据模板创建并初始化一个报文对象
		//m = mfact.newMessagefromTemplate(msgType);
		
		// 对于域不使用二进制
		m.setBinary(true);
		
		String TPDU = "6000050000";
		String msgHeader = "603110000000";
		
		
		/**
		 * 设置TPDU的数据
		 * */
		if (m.setMessageTPDUData(0, TPDU.getBytes()) == false) {
			System.out.println("设置TPDU出错。");
			System.exit(-1);
		}
		/**
		 * 设置报文头的数据
		 * */
		
		if (m.setMessageHeaderData(0, msgHeader.getBytes()) == false) {
			System.out.println("设置报文头出错。");
			System.exit(-1);
		}
		

		Iterator<Integer> it = parseMap.keySet().iterator();
		/* 字段 */
		int fieldId = 0;
		/* 字段值 */
		Object value = null;
		cnFieldParseInfo xfield = null;
		
		// if(!req_map.isEmpty()){
		while (it.hasNext()) {
			fieldId = it.next();

			if (parseMap.get(fieldId) != null && !parseMap.get(fieldId).equals("")) {
				req_map = context.getReq_map();
				
				xfield = parseMap.get(fieldId);
				/**屏蔽请求报文的无值域*/
				if(req_map.get("field" + String.valueOf(fieldId))!=null && !req_map.get("field" + String.valueOf(fieldId)).equals("")){
				
					value = req_map.get("field"
						+ String.valueOf(fieldId));
					/* 当等于0时为变量域 故此拿请求过来的值求出大小 */
					if(xfield.getType() != cnType.AMOUNT){
						if (xfield.getLength() != 0 && xfield.getIsOk() == true) {
								if (value == null || value.equals("")) {
									throw new IllegalArgumentException(
											String.valueOf(fieldId) + " 域 is must input! ");
								} else if (value.toString().length() != xfield.getLength()) {
									if (value.toString().length() < xfield.getLength()) {
										throw new IllegalArgumentException(
												String.valueOf(fieldId) + " 域值 too short! ");
									} else {
										throw new IllegalArgumentException(
												String.valueOf(fieldId) + " 域值 too lang! ");
									}
			
								}
								m.setValue(fieldId, value, xfield.getType(),
										xfield.getLength());
						} else if (xfield.getLength() == 0 && xfield.getIsOk() == true) {
							if (value == null) {
								throw new IllegalArgumentException(
										String.valueOf(fieldId) + " 域 is must input! ");
							}
							
							if(xfield.getType() == cnType.AMOUNT){
								
								m.setValue(fieldId, BigDecimal.valueOf(Double.parseDouble(value.toString())), xfield.getType(),
										xfield.getLength());	
							}else{
								m.setValue(fieldId, value, xfield.getType(), value.toString().length());
							}
						} else if (xfield.getLength() != 0 && xfield.getIsOk() == false) {
							if (value != null) {
								if (value.toString().length() != xfield.getLength()) {
									if (value.toString().length() < xfield.getLength()) {
										throw new IllegalArgumentException(
												String.valueOf(fieldId)
														+ " 域值 too short! ");
									} else {
										throw new IllegalArgumentException(
												String.valueOf(fieldId)
														+ " 域值 too lang!");
									}
		
								}
								
								if(xfield.getType() == cnType.AMOUNT){
									
									m.setValue(fieldId, BigDecimal.valueOf(Double.parseDouble(value.toString())), xfield.getType(),
											xfield.getLength());	
								}else{
									m.setValue(fieldId, value, xfield.getType(), value.toString().length());
								}
								
							}
		
						} else if (xfield.getLength() == 0 && xfield.getIsOk() == false) {
							if (value != null) {
								if (!value.toString().trim().equals(""))
									m.setValue(fieldId, value, xfield.getType(),
											value.toString().length());
							}
		
						}
					} else {
						
//						m.setValue(fieldId, BigDecimal.valueOf(Double.parseDouble(value.toString())), xfield.getType(),
//								xfield.getLength());	
						m.setValue(fieldId, BigDecimal.valueOf(Double.parseDouble(value.toString())/100.00), xfield.getType(),
								xfield.getLength());	
					}
				}	
			}else{
				System.out.println("没有此'" + parseMap.get(fieldId) + "'的value");
				}
			}		
		
		return m;
	}

	/**
	 * 组装报文
	 * 
	 * @param m,context
	 * @return reqMsg (tpdu+头文件+报文类型+位图+位图对应的域值)
	 */
	private byte[] depacketize(CnMessage m, TxContext context) {
		/* 组装请求报文*/
		byte[] reqMsg = null;
		/* TPDU */
		byte[] msgTPDU = null;
		/* 头文件 */
		byte[] msgHeader = null;
		/* 报文类型 */
		byte[] msgtypeid = null;
		
		/**
		 * 进行BCD码压缩
		 * */
		msgTPDU = ConvertUtil.byte2BCD(m.getmsgTPDU());
		msgHeader = ConvertUtil.byte2BCD(m.getmsgHeader());
		msgtypeid = ConvertUtil._str2Bcd(m.getMsgTypeID());
	
		/**
		 * data :位图[8字节]+ {11域【3字节BCD码】+其余所有域值（个别域值前加上BCD码压缩的2个字节的长度值_左补0）}
		 * */
		byte[] data = m.writeInternal();

		System.out.println("位图和域值长度:\t" + data.length);
		System.out.println("位图和域值:\t" + ConvertUtil.trace(data));

		/**
		 * 组装字节类型报文；（tpdu[BCD压缩5字节]+头文件[BCD压缩6字节]）+
		 * 报文类型【BCD压缩2字节】+位图【8字节】&&位图对应的域值
		 * */
		ByteBuffer sendBuf = ByteBuffer.allocate(msgTPDU.length + msgHeader.length
				+ msgtypeid.length + data.length);
		/* TPDU */
		sendBuf.put(msgTPDU);
		/* 头文件 */
		sendBuf.put(msgHeader);
		/* 报文类型 */
		sendBuf.put(msgtypeid);
		/* 位图+位图对应的域值 */
		sendBuf.put(data);

		reqMsg = sendBuf.array();

		return reqMsg;
	}

	/*
	 * 创建消息工厂并创建请求 context容器设置tranCode、req_map、req_xml
	 */
	public byte[] beforeProcess(TxContext context) {
		System.out.println("\t####################beforeProcess####################"
				+ "\r");
		/**
		 * 变量定义
		 * */
		String req_json = null;
		String msgType = null;
		
		Map<String, Object> req_map = null;
		
		/**
		 * 通过指定的报文配置文件创建消息工厂（mfact）
		 * */
		try {
			/* 通过xml消息配置文件创建消息工厂， */
			mfact = cnConfigParser.createFromXMLConfigFile(ApplicationEnvironment.getInstance().getApplication().getResources().openRawResource(R.raw.msg_config));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 作用于 field 7
		mfact.setUseCurrentDate(false); 
		
		// 设置系统跟踪号的生成器（用于field 11）
//		mfact.setSystemTraceNumberGenerator(new cnSimpleSystemTraceNumGen((int) (System.currentTimeMillis() % 100000)));

		

		/**
		 * context容器设置交易码（消息类型）的值
		 * */
		msgType = context.getMsgType();
		
		
		/**
		 * 根据模板创建并初始化一个报文对象（如果配置文件中没有配置template会跳过）
		 * */
		m = mfact.newMessagefromTemplate(msgType);
		

		m = registerReqMsg(m, context);

		System.out.println("\n NEW MESSAGE:");

		print(m);

		/**
		 * 此处判断终端请求过来的数据是否是必须的
		 * */
		// m.hasField(fieldid);

//		Map msgMap = new HashMap();
//		msgMap.put("msgType", msgType);

		// String msg = msgFormater(m, msgMap).toString();

		// System.out.println("msg\r" + msg);
		// System.out.println("##############  " + msg.substring(1, msg.length() - 1));

		byte[] reqMsg = depacketize(m, context);

		return reqMsg;
	}


	

	/*
	 * 解析返回（响应）数据 context容器设置resp_map、resp_json
	 */
	public void afterProcess(TxContext context) {

		System.out.println("\t##########################afterProcess##########################"
				+ "\r");

		byte[] resp_byte = context.getResp_byte();
		try {
//			CnMessage resp_map = mfact.parseMessage(resp, 10);
			
			Map<String, Object> resp_map = context.getResp_map();
			
			resp_map.put("fieldTrancode", context.getClient_msgType());
			resp_map.put("fieldTransType",context.getMsgType());
			
			CnMessage resp_msg = mfact.parseMessage(resp_byte, mfact.getTPDUlengthAttr(context.getMsgType()), mfact.getHeaderLengthAttr(context.getMsgType()));
			for(int i=0;i<128;i++){
				if(resp_msg.hasField(i)){
					resp_map.put("field"+i, resp_msg.getField(i).toString());
				}
			}
			context.setResp_map(resp_map);
			
			/* =============手动加入交易码==================== */
			/*
			 * if(context.getReq_map().get("fieldTrancode").equals("800003")){
			 * 
			 * resp_map.put("fieldTrancode", "800003"); }
			 */
			/* ====================================== */

			/* 赋给容器resp_map */
			// context.setResp_map(resp_map);

			// System.out.println("\rResp_map:\r" + resp_map + "\r");
			// if (context.getTranCode().equals("100005")) {
			// if (context.getData("fieldVersion") != null
			// && !context.getData("fieldVersion").equals("")) {
			// resp_map.put("fieldVersion",
			// context.getData("fieldVersion"));
			// resp_map.put("fieldFileName",
			// context.getData("fieldFileName"));
			// }
			// }
			/* 把map类型的响应消息转换成送json付给容器 */
			// Resp_json = JSONUtil.maptoString(resp_map);

			/* 赋给容器resp_json */
			// context.setResp_json(Resp_json);
			System.out.println("\rResp_json:\r" + resp_byte + "\r");

		} catch (NullPointerException e) {
			throw new NullPointerException();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 结果处理 （记录操作日志、记录流水）
	 * 
	 * @param context
	 */
	public void processResult(TxContext context) {

		System.out.println("\t####################processResult####################");
		
		/**
		 * 变量
		 * */
		String fieldTrancode =null;
		/* 联机交易流水类交易码 */
		String tc = null;
		
		// 用来存放请求报文及响应报文中的响应码
		Map<String, Object> map = new HashMap<String, Object>();

		/* 容器中得到req_map */
		Map<String, Object> req_map = context.getReq_map();
		/* 容器中得到resp_map */
		Map<String, Object> resp_map = context.getResp_map();

		map.putAll(req_map);

		// 状态//

		/* 响应码 */
		String field39 = (String) resp_map.get("field39");
		/* 响应消息 */
		String fieldMessage = (String) resp_map.get("fieldMessage");
		/* 主卡号 */
		String field2 = (String) resp_map.get("field2");
		/* 发卡行 */
		String issuerBank = (String) resp_map.get("issuerBank");
		/* 行有效期 */
		String field14 = (String) resp_map.get("field14");
		/* 检索参考号 */
		String field37 = (String) resp_map.get("field37");

		map.put("retrieval_reference_number", field37);
		map.put("field39", field39);
		map.put("fieldMessage", fieldMessage);
		map.put("field2", field2);

		if (issuerBank != null) {
			map.put("issuer_bank", issuerBank);
		} else {
			map.put("issuer_bank", null);
		}
		if (field14 != null) {
			map.put("field14", field14);
		} else {
			map.put("field14", null);
		}

		// 交易码
//		fieldTrancode = context.getMsgType();
		fieldTrancode = context.getClient_msgType();

		if (fieldTrancode.equals("020022")) {
			map.put("trans_type", 1);
			map.put("trans_name", "收款");
		} else if (fieldTrancode.equals("020023")) {
			map.put("trans_type", 2);
			map.put("trans_name", "收款撤销");
		} else if (fieldTrancode.equals("020011")) {
			map.put("trans_type", 3);
			map.put("trans_name", "付款");
		}

		System.out.println("fieldTrancode:" + fieldTrancode);
		System.out.println("field39:" + field39);

		/* 联机交易流水类交易码 */
		tc = (String) transCode.get(fieldTrancode);

		if (tc != null) {
			System.out.println("联机交易流水类交易码? : " + "true\r");
		} else {
			System.out.println("联机交易流水类交易码? : " + "false\r");
		}

		/* 响应报文中不同的所有数据都放入map中 */
		// System.out.println("map:" + map);

		/* 记录操作日志 */
		// logAction.recordLog(map);

		// 记录日志详情

		if (StringUtil.isNotNull(tc)) {

			System.out.println("\t####################记录流水####################");

			// 记录流水
//			logAction.recordTransLog(map);

		}

		if (fieldTrancode.equals("500201")) {

			System.out.println("\t####################记录结算交易####################");

			// 记录结算交易
//			logAction.recordSettlementLog(req_map, resp_map);

		}
	}

	/*
	 * 异常处理
	 */
	public void processError(TxContext context) {
		System.out.println("\t####################processError####################"
				+ "\r");
		/**
		 * 变量
		 * */
		String fieldTrancode = null;
		String _fieldTrancode = null;
		String field39 =null;
		String fieldMessage = null;
		String tc = null;
		
		Map<String, Object> error_map = null;

		error_map = (Map<String, Object>) context
				.getReq_map();
		
		// String error_json = context.getReq_json();
		// Map<String, Object> error_map = JSONUtil.stringtoMap(error_json);
		fieldTrancode = (String) context.getResp_map().get(
				"fieldTrancode");
		field39 = (String) context.getResp_map().get("field39");

		fieldMessage = (String) context.getResp_map()
				.get("fieldMessage");

		error_map.put("fieldTrancode", fieldTrancode);
		error_map.put("field39", field39);
		error_map.put("fieldMessage", fieldMessage);

		/* 出现错误把请求报文加上处理码及交易描述发给终端 */
		context.setResp_json(resp_json);

		// 交易码
		_fieldTrancode = context.getMsgType();

		System.out.println("fieldTrancode:" + _fieldTrancode);
		System.out.println("field39:" + field39);

		/* 联机交易流水类交易码 */
		tc = (String) transCode.get(fieldTrancode);

		if (tc != null) {
			System.out.println("联机交易流水类交易码? : " + "true\r");
		} else {
			System.out.println("联机交易流水类交易码? : " + "false\r");
		}

		/* 响应报文中不同的所有数据都放入map中 */
		// System.out.println("map:" + map);

		/* 记录操作日志 */
		// logAction.recordLog(map);

		// 记录日志详情

		if (StringUtil.isNotNull(tc)) {
			System.out.println("####################记录流水####################");
			// 记录流水
//			logAction.recordTransLog(error_map);
		}

		// // 结果处理：日志记录，流水记录；
		// processResult(context);
	}

	public void printfield(Map<String, List<FieldInfo>> map) {
		System.out.println("=======================");
		for (Map.Entry<String, List<FieldInfo>> entry : map.entrySet()) {
			List<FieldInfo> list = entry.getValue();
			for (FieldInfo info : list) {
				System.out.println("FieldName:" + info.getName());
				System.out.println("FiledValue" + info.getValue());
			}
		}
		System.out.println("=======================");
	}

	// 输出一个报文内容
		private static void print(CnMessage m) {
			System.out.println("----------------------------------------------------- "
					+ m.getField(11));
			System.out.println("Message TPDU = \t[" + new String(m.getmsgTPDU()) + "]");
			System.out.println("Message Header = \t[" + new String(m.getmsgHeader()) + "]");
			System.out.println("Message TypeID = \t[" + m.getMsgTypeID() + "]");
			m.hasField(1);
			for (int i = 2; i < 128; i++) {
				if (m.hasField(i)) {
					System.out.println(
							"Field: " + i + " <" + m.getField(i).getType() + ">\t(" + m.getField(i).getLength() + ")\t[" + m.getField(i).toString() + "]" + 
							"      \t[" + m.getObjectValue(i) + "]");
				}
			}
		}
		
	public String test() {
//		return String.format("testhost: ip is :%s, port is %d  ",
//				socketTransport.getHost(), socketTransport.getPort());
		return null;
	}


	public boolean getIsLinux() {
		return isLinux;
	}

	public void setIsLinux(boolean isLinux) {
		this.isLinux = isLinux;
	}

	public void setIsDynamic(String isDynamic) {
		this.isDynamic = isDynamic;
	}

	public void setResponsePath(String responsePath) {
		this.responsePath = responsePath;
	}


	public void setSocketTransport(SocketTransport socketTransport) {
		this.socketTransport = socketTransport;
	}

	public void setMfact(CnMessageFactory mfact) {
		this.mfact = mfact;
	}

	public void setResp_code_map(Map<String, Object> resp_code_map) {
		this.resp_code_map = resp_code_map;
	}

	public void setTransCode(Map<String, Object> transCode) {
		this.transCode = transCode;
	}

}
