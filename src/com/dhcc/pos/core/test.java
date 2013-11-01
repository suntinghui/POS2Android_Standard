package com.dhcc.pos.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import com.dhcc.pos.constant.Constant;
import com.dhcc.pos.packets.util.FileUtil;


public class test {
	/* 联机交易流水类交易码 */
	static Map<String, Object> transCode;
//	static Map<String, Object> transTypeMap;

	static TransDispatcher transDispatcher;
	static TxContext txContext = null;
	static Map<String, Object> req_map = null;

	/* 报文类型（交易码） */
	static String msgType = null;

	static{
		transCode = new HashMap<String, Object>();
		transCode.put("0200", "消费");
		
//		transTypeMap = new HashMap<String, Object>();
//		transTypeMap.put("000000", "txAction");
	}
	
	
	private static void tdw_test_map() throws IOException,
	UnsupportedEncodingException {
		
	Random random = new Random();
	String rand = "";
	
	for (int j = 0; j < 6; j++) {
		int number = random.nextInt(10);
		rand = rand.concat(new Integer(number).toString());
	}
	
	String rand8 = "";
	
	for (int j = 0; j < 8; j++) {
		int number = random.nextInt(10);
		rand8 = rand8.concat(new Integer(number).toString());
	}
	
	
	
	/**
	 * 1、首先读取文件拿到文件中的起始值（流水号）
	 * 2、然后根据拿到的流水号做先+1处理 
	 * 3、把+1后的流水号存入文件（替换原有的值）并把该流水号赋值给需要的报文域
	 * **/
	String num = FileUtil.readerFile(Constant.numPath, "num",1);
	
	int f11 = Integer.parseInt(num.trim());
	
	String 	field11 = String.format("%06d", ++f11);
	
	FileUtil.writeFile(Constant.numPath, "num",field11,false);
	
	/*
	 * ——数据元长度 ——60.1 消息类型码 ——60.2 批次号 ——60.3 网络管理信息码 ——60.4 终端读取能力 ——60.5
	 * 基于PBOC借/贷记标准的IC卡条件代码N1
	 */
	StringBuffer buf60 = new StringBuffer();
//	buf60.append("011");
	/**
	 * 交易类型码 
	 * 00签到 查询01  10预授权 10追加预授权 11预授权撤销 20预授权完成（联机） 21预授权完成撤销 24预授权完成（离线） 22消费 23消费撤销 25退货 30结算 32结算调整  34结算调整（追加消费） 36离线消费  
	 * */
	buf60.append("00");
	/*消费*/
//	buf60.append("22");
//	int rand2 = CnMessageFactory.SystraceNumGen.nextTrace();
//	System.out.println("rand:" + rand2);
	/*批次号*/
	buf60.append("000002");
	
	/**网络管理信息码
	 * 001:签到（单倍长秘钥算法终端） 
	 * 002:签退
	 * 003:签到（双倍长密钥算法)）
	 * 201:结算、上送
	 * 
	 * 自定义(佰付通)
	 * 101：POS终端主密钥下载预申请
	 * 102：POS终端主密钥下载
	 * 
	 * */
	buf60.append("001");
//	buf60.append("5");
//	buf60.append("0");
	
	
	
	
	

	/*信息下载*/
	Map<String, Object> f0820 = new TreeMap<String, Object>();
	f0820.put("fieldTrancode", "0820");
	f0820.put("field11", field11);
	f0820.put("field44", "00000000000000000001");
	f0820.put("field60", buf60.toString());
	f0820.put("field63", "001");
	
	
	
	
	/* 签到 */
	Map<String, Object> login_map = new TreeMap<String, Object>();
	login_map.put("fieldTrancode", "0800");
	login_map.put("field11", field11);
	login_map.put("field41", "19530024");
	login_map.put("field42", "195300430101001");
	login_map.put("field44", "00191120120820000273");
	login_map.put("field60", buf60.toString());
	login_map.put("field63", "001");

	/* 签退 */
	buf60.delete(0, buf60.length());
	buf60.append("01");
	buf60.append("000002");
	buf60.append("003");
	buf60.append("0");
	buf60.append("0");
	/*左靠右补0*/
	buf60.append("0");
	Map<String, Object> logout_map = new HashMap<String, Object>();
	logout_map.put("fieldTrancode", "0820");
	logout_map.put("field11",  field11);
	logout_map.put("field41", "19530024");
	logout_map.put("field42", "195300430101001");
	logout_map.put("field60", buf60.toString());
	logout_map.put("field63", "63");
	logout_map.put("fieldMerchPWD", "111111");
	
	
	/* 余额查询 */
	buf60.delete(0, buf60.length());
	buf60.append("01");
	buf60.append("000002");
	buf60.append("003");
//	buf60.append("0");
//	buf60.append("0");
	/*左靠右补0*/
//	buf60.append("0");
	Map<String, Object> queryBal_map = new HashMap<String, Object>();
	queryBal_map.put("fieldTrancode", "0200");
//	queryBal_map.put("field2", "6226800005404551");
	queryBal_map.put("field3", "310000");
	queryBal_map.put("field11", field11);// <!--终端流水号累加-->
//	queryBal_map.put("field14", "1506");
	/*02磁条 第三位：1交易中包含 PIN 2交易中不包含 PIN*/
	queryBal_map.put("field22", "021");
//	queryBal_map.put("field23", "143");
	/*服务点条件码*/
	queryBal_map.put("field25", "00");
	queryBal_map.put("field26", "12");
	queryBal_map.put("field35", "4392257501725638D090610117539137");
//	queryBal_map.put("field36", "");
	queryBal_map.put("field41", "19530024");
	queryBal_map.put("field42", "195300430101001");
	queryBal_map.put("field49", "156");
	queryBal_map.put("field52", "94A9EE84A2F88450");
//	queryBal_map.put("field53", "00");
//	queryBal_map.put("field55","");
	queryBal_map.put("field60", buf60.toString());
//	queryBal_map.put("field62", "1");
//	queryBal_map.put("fieldMAB", "fieldTrancode;field2;field3;field11;field25;field41;field42;field53;termMobile;ReaderID;PSAMID");
//	queryBal_map.put("fieldMAC", "");
//	queryBal_map.put("termMobile", "18600153271");
//	queryBal_map.put("termMobile", "13912345678");
//	queryBal_map.put("ReaderID", "00191020121020010023");
//	queryBal_map.put("PSAMID", "11111111");
	queryBal_map.put("field64", "C32A3E53");
	
	
	
	/* 消费 */
	buf60.delete(0, buf60.length());
	buf60.append("22");
	buf60.append("000002");
	buf60.append("000");
	buf60.append("5");
	buf60.append("2");
	buf60.append("1");
	/*左靠右补0*/
//	buf60.append("0");
	
	Map<String, Object> purchase_map = new HashMap<String, Object>();
	purchase_map.put("fieldTrancode", "0200");
//	purchase_map.put("field2", "6226800005404551");
	/*004000 当第三位为4时通用账户 0时默认账户 */
	purchase_map.put("field3", "000000");
	/*交易金额*/
	double purchase_field4 = 11.11;
	purchase_map.put("field4", (BigDecimal.valueOf(purchase_field4)));
	/*系统跟踪号*/
	purchase_map.put("field11", field11);// <!--终端流水号累加-->
	/*卡有效期*/
//	purchase_map.put("field14", "1506");
	/*02磁条 第三位：1交易中包含 PIN 2交易中不包含 PIN*/
	purchase_map.put("field22", "021");
	/*卡序列号*/
//	purchase_map.put("field23", "143");
	/*服务点条件码*/
	purchase_map.put("field25", "00");
	/*服务点PIN获取码 (22域指明PIN可输入且持卡人输入了PIN)*/
	purchase_map.put("field26", "12");
	
	StringBuffer purchase_map_35 = new StringBuffer();
	purchase_map_35.append("4392257501725638D090610117539137");
	/**左靠右补0*/
//	purchase_map_35.append("0");
	/*2磁道数据*/
	purchase_map.put("field35", purchase_map_35.toString());//<!--二磁-->
	/*三磁可空*/
//	purchase_map.put("field36", "");
	/*受卡机终端标识码(终端代码)*/
	purchase_map.put("field41", "00021679");
	/*受卡方标识码(商户代码)*/
	purchase_map.put("field42", "105360170110159");
//	purchase_map.put("termMobile", "18600153271");
//	purchase_map.put("ReaderID", "00191020121020010023");
//	purchase_map.put("PSAMID", "11111111");
	/*交易货币代码 159人民币*/
	purchase_map.put("field49", "156");
	/*个人标识码数据 22域如果第三位为1此域出现*/
	purchase_map.put("field52", "94A9EE84A2F88450");
	/*安全控制信息*/
	purchase_map.put("field53", "0600000000000000");
//	purchase_map.put("field55","");
	purchase_map.put("field60", buf60.toString());
//	purchase_map.put("field62", "1");
//	purchase_map.put("fieldMAB", "fieldTrancode;field2;field3;field4;field11;field25;field41;field42;field53;fieldMerchID;termMobile;ReaderID;PSAMID");
//	purchase_map.put("fieldMAC", "");
	purchase_map.put("field64", "C32A3E53");
	
	/* 批结算 */
	Map<String, Object> batchSettlement_map = new HashMap<String, Object>();
	batchSettlement_map.put("fieldTrancode", "500201");
	batchSettlement_map.put("field11", rand);
	batchSettlement_map.put("field13", "1020");
	batchSettlement_map.put("field41", "11111111");
	batchSettlement_map.put("field42", "503055158120010");
	batchSettlement_map.put("field48", "");
	batchSettlement_map.put("field49", "156");
	String batchSettlement_map_f60 = "22000014000500";
	batchSettlement_map.put("field60", batchSettlement_map_f60);
	batchSettlement_map.put("field63", "111");
	batchSettlement_map.put("fieldMerchPWD", "");
	batchSettlement_map.put("fieldMAB", "");
	batchSettlement_map.put("fieldMAC", "");
	batchSettlement_map.put("termMobile", "18600153271");
	batchSettlement_map.put("ReaderID", "00191020120920010000");
	batchSettlement_map.put("PSAMID", "11111111");
	
	/* 付款 */
	Map<String, Object> transfer_map = new HashMap<String, Object>();
	transfer_map.put("fieldTrancode", "200001111");
	
	transfer_map.put("field2", "6226800005404551"); 
	transfer_map.put("field3","004000"); 
	transfer_map.put("field4", "000000000005");
	transfer_map.put("field7", "000000");
	transfer_map.put("field11",rand );// <!--终端流水号累加-->
	transfer_map.put("field12", "000000"); 
	transfer_map.put("field13","0000");
	transfer_map.put("field14", "1102");
	transfer_map.put("field22", "021"); 
	transfer_map.put("field25","00"); 
	transfer_map.put("field26", "12"); 
	transfer_map.put("field32",""); 
	transfer_map.put("field33", ""); 
	transfer_map.put("field41","11111111"); 
	transfer_map.put("field42", "503055158120010");
	transfer_map.put("field43", ""); 
	transfer_map.put("field46", "");
	transfer_map.put("field47", ""); 
	transfer_map.put("termMobile", "18600153271");
	transfer_map.put("ReaderID", "00191020120920010000");
	transfer_map.put("PSAMID", "11111111");
	transfer_map.put("field49", "156");
	transfer_map.put("field52", "94A9EE84A2F88450"); 
	String transfer_map_f60 = "01000014000500"; 
	transfer_map.put("field60",transfer_map_f60); 
	transfer_map.put("field62", "1");
	transfer_map.put("field102", "111111"); 
	transfer_map.put("field103","111111"); 
	transfer_map.put("fieldAddress", "116.47215314255,39.897608228432,北京市北京市朝阳区"); 
	transfer_map.put("fieldMAB", ""); 
	transfer_map.put("fieldMAC", "");
	/* ========================================================== */
	/* 图片toBase64 */
	//String picString = ImageToBase64.GetImageToStr("D:/psu.jpg");
	
	/* 签购单上传 */
	Map<String, Object> preserveImage_map = new HashMap<String, Object>();
	preserveImage_map.put("fieldTrancode", "500000001");
	preserveImage_map.put("field7", "0810");
	preserveImage_map.put("field11", "243648");// <!--原交易终端流水号-->
	preserveImage_map.put("field41", "19820405");
	preserveImage_map.put("field42", "C19800101010101");
	preserveImage_map.put("termMobile", "");
	preserveImage_map.put("ReaderID", "");
	preserveImage_map.put("PSAMID", "");
	String preserveImage_map_f60 = "00600019";
	preserveImage_map.put("field60", preserveImage_map_f60);
	//preserveImage_map.put("fieldImage", picString);
	
	/* 校验商户密码 */
	Map<String, Object> verifyMerchPassword_map = new HashMap<String, Object>();
	verifyMerchPassword_map.put("fieldTrancode", "100005");
	verifyMerchPassword_map.put("fieldVersion", "1");
	verifyMerchPassword_map.put("field11", rand);
	verifyMerchPassword_map.put("field41", "19820468");
	verifyMerchPassword_map.put("field42", "496651407420001");
	verifyMerchPassword_map.put("termMobile", "18600153271");
	verifyMerchPassword_map.put("ReaderID", "00191020121020010022");
	verifyMerchPassword_map.put("PSAMID", "12345678");
	verifyMerchPassword_map.put("fieldMerchPWD", "803A8A0AC4CE757C05A6F19774544D7421893E006D7F5BB80752C33E6A0005E55711FCE2933CD5E55CD18DA8350E2F80B6137604028859AB59B49437E8E4B02827F8D9FC814FF0D34562B3FCAD75728D4A250387F6C2ABD3A1073E05E9F6FE05B04F775FF36C8BC448BE1DBD6283441C9AD83107D9183B0AE162F7699827BE69");
	
	/* 查询验证码 */
	Map<String, Object> queryCaptcha_map = new HashMap<String, Object>();
	queryCaptcha_map.put("fieldTrancode", "999000003");
	queryCaptcha_map.put("fieldChannel", "06");
	queryCaptcha_map.put("condition", "111111");
	
}
	
	public static void main(String[] args) {
		
		Random random = new Random();
		String rand = "";
		
		for (int j = 0; j < 6; j++) {
			int number = random.nextInt(10);
			rand = rand.concat(new Integer(number).toString());
		}
		
		String rand8 = "";
		
		for (int j = 0; j < 8; j++) {
			int number = random.nextInt(10);
			rand8 = rand8.concat(new Integer(number).toString());
		}
		
		
		
		/**
		 * 1、首先读取文件拿到文件中的起始值（流水号）
		 * 2、然后根据拿到的流水号做先+1处理 
		 * 3、把+1后的流水号存入文件（替换原有的值）并把该流水号赋值给需要的报文域
		 * **/
		String num = FileUtil.readerFile(Constant.numPath, "num",1);
		
		int f11 = Integer.parseInt(num.trim());
		
		String 	field11 = String.format("%06d", ++f11);
		
		FileUtil.writeFile(Constant.numPath, "num",field11,false);
		
		/*
		 * ——数据元长度 ——60.1 消息类型码 ——60.2 批次号 ——60.3 网络管理信息码 ——60.4 终端读取能力 ——60.5
		 * 基于PBOC借/贷记标准的IC卡条件代码N1
		 */
		StringBuffer buf60 = new StringBuffer();
//		buf60.append("011");
		/**
		 * 交易类型码 
		 * 00签到 查询01  10预授权 10追加预授权 11预授权撤销 20预授权完成（联机） 21预授权完成撤销 24预授权完成（离线） 22消费 23消费撤销 25退货 30结算 32结算调整  34结算调整（追加消费） 36离线消费  
		 * */
		buf60.append("00");
		/*消费*/
//		buf60.append("22");
//		int rand2 = CnMessageFactory.SystraceNumGen.nextTrace();
//		System.out.println("rand:" + rand2);
		/*批次号*/
		buf60.append("000002");
		
		/**网络管理信息码
		 * 001:签到（单倍长秘钥算法终端） 
		 * 002:签退
		 * 003:签到（双倍长密钥算法)）
		 * 201:结算、上送
		 * 
		 * 自定义(佰付通)
		 * 101：POS终端主密钥下载预申请
		 * 102：POS终端主密钥下载
		 * 
		 * */
		buf60.append("001");
//		buf60.append("5");
//		buf60.append("0");
		
		
		
		
		

		/*信息下载*/
		Map<String, Object> f0820 = new TreeMap<String, Object>();
		f0820.put("fieldTrancode", "0820");
		f0820.put("field11", field11);
		f0820.put("field44", "00000000000000000001");
		f0820.put("field60", buf60.toString());
		f0820.put("field63", "001");
		
		
		
		
		/* 签到 */
		Map<String, Object> login_map = new TreeMap<String, Object>();
		login_map.put("fieldTrancode", "0800");
		login_map.put("field11", field11);
		login_map.put("field41", "19530024");
		login_map.put("field42", "195300430101001");
		login_map.put("field44", "00191120120820000273");
		login_map.put("field60", buf60.toString());
		login_map.put("field63", "001");

		/* 签退 */
		buf60.delete(0, buf60.length());
		buf60.append("01");
		buf60.append("000002");
		buf60.append("003");
		buf60.append("0");
		buf60.append("0");
		/*左靠右补0*/
		buf60.append("0");
		Map<String, Object> logout_map = new HashMap<String, Object>();
		logout_map.put("fieldTrancode", "0820");
		logout_map.put("field11",  field11);
		logout_map.put("field41", "19530024");
		logout_map.put("field42", "195300430101001");
		logout_map.put("field60", buf60.toString());
		logout_map.put("field63", "63");
		logout_map.put("fieldMerchPWD", "111111");
		
		
		/* 余额查询 */
		buf60.delete(0, buf60.length());
		buf60.append("01");
		buf60.append("000002");
		buf60.append("003");
//		buf60.append("0");
//		buf60.append("0");
		/*左靠右补0*/
//		buf60.append("0");
		Map<String, Object> queryBal_map = new HashMap<String, Object>();
		queryBal_map.put("fieldTrancode", "0200");
//		queryBal_map.put("field2", "6226800005404551");
		queryBal_map.put("field3", "310000");
		queryBal_map.put("field11", field11);// <!--终端流水号累加-->
//		queryBal_map.put("field14", "1506");
		/*02磁条 第三位：1交易中包含 PIN 2交易中不包含 PIN*/
		queryBal_map.put("field22", "021");
//		queryBal_map.put("field23", "143");
		/*服务点条件码*/
		queryBal_map.put("field25", "00");
		queryBal_map.put("field26", "12");
		queryBal_map.put("field35", "4392257501725638D090610117539137");
//		queryBal_map.put("field36", "");
		queryBal_map.put("field41", "19530024");
		queryBal_map.put("field42", "195300430101001");
		queryBal_map.put("field49", "156");
		queryBal_map.put("field52", "94A9EE84A2F88450");
//		queryBal_map.put("field53", "00");
//		queryBal_map.put("field55","");
		queryBal_map.put("field60", buf60.toString());
//		queryBal_map.put("field62", "1");
//		queryBal_map.put("fieldMAB", "fieldTrancode;field2;field3;field11;field25;field41;field42;field53;termMobile;ReaderID;PSAMID");
//		queryBal_map.put("fieldMAC", "");
//		queryBal_map.put("termMobile", "18600153271");
//		queryBal_map.put("termMobile", "13912345678");
//		queryBal_map.put("ReaderID", "00191020121020010023");
//		queryBal_map.put("PSAMID", "11111111");
		queryBal_map.put("field64", "C32A3E53");
		
		
		
		/* 消费 */
		buf60.delete(0, buf60.length());
		buf60.append("22");
		buf60.append("000002");
		buf60.append("000");
		buf60.append("5");
		buf60.append("2");
		buf60.append("1");
		/*左靠右补0*/
//		buf60.append("0");
		
		Map<String, Object> purchase_map = new HashMap<String, Object>();
		purchase_map.put("fieldTrancode", "0200");
//		purchase_map.put("field2", "6226800005404551");
		/*004000 当第三位为4时通用账户 0时默认账户 */
		purchase_map.put("field3", "000000");
		/*交易金额*/
		double purchase_field4 = 11.11;
		purchase_map.put("field4", (BigDecimal.valueOf(purchase_field4)));
		/*系统跟踪号*/
		purchase_map.put("field11", field11);// <!--终端流水号累加-->
		/*卡有效期*/
//		purchase_map.put("field14", "1506");
		/*02磁条 第三位：1交易中包含 PIN 2交易中不包含 PIN*/
		purchase_map.put("field22", "021");
		/*卡序列号*/
//		purchase_map.put("field23", "143");
		/*服务点条件码*/
		purchase_map.put("field25", "00");
		/*服务点PIN获取码 (22域指明PIN可输入且持卡人输入了PIN)*/
		purchase_map.put("field26", "12");
		
		StringBuffer purchase_map_35 = new StringBuffer();
		purchase_map_35.append("4392257501725638D090610117539137");
		/**左靠右补0*/
//		purchase_map_35.append("0");
		/*2磁道数据*/
		purchase_map.put("field35", purchase_map_35.toString());//<!--二磁-->
		/*三磁可空*/
//		purchase_map.put("field36", "");
		/*受卡机终端标识码(终端代码)*/
		purchase_map.put("field41", "00021679");
		/*受卡方标识码(商户代码)*/
		purchase_map.put("field42", "105360170110159");
//		purchase_map.put("termMobile", "18600153271");
//		purchase_map.put("ReaderID", "00191020121020010023");
//		purchase_map.put("PSAMID", "11111111");
		/*交易货币代码 159人民币*/
		purchase_map.put("field49", "156");
		/*个人标识码数据 22域如果第三位为1此域出现*/
		purchase_map.put("field52", "94A9EE84A2F88450");
		/*安全控制信息*/
		purchase_map.put("field53", "0600000000000000");
//		purchase_map.put("field55","");
		purchase_map.put("field60", buf60.toString());
//		purchase_map.put("field62", "1");
//		purchase_map.put("fieldMAB", "fieldTrancode;field2;field3;field4;field11;field25;field41;field42;field53;fieldMerchID;termMobile;ReaderID;PSAMID");
//		purchase_map.put("fieldMAC", "");
		purchase_map.put("field64", "C32A3E53");
		
		/* 批结算 */
		Map<String, Object> batchSettlement_map = new HashMap<String, Object>();
		batchSettlement_map.put("fieldTrancode", "500201");
		batchSettlement_map.put("field11", rand);
		batchSettlement_map.put("field13", "1020");
		batchSettlement_map.put("field41", "11111111");
		batchSettlement_map.put("field42", "503055158120010");
		batchSettlement_map.put("field48", "");
		batchSettlement_map.put("field49", "156");
		String batchSettlement_map_f60 = "22000014000500";
		batchSettlement_map.put("field60", batchSettlement_map_f60);
		batchSettlement_map.put("field63", "111");
		batchSettlement_map.put("fieldMerchPWD", "");
		batchSettlement_map.put("fieldMAB", "");
		batchSettlement_map.put("fieldMAC", "");
		batchSettlement_map.put("termMobile", "18600153271");
		batchSettlement_map.put("ReaderID", "00191020120920010000");
		batchSettlement_map.put("PSAMID", "11111111");
		
		/* 付款 */
		Map<String, Object> transfer_map = new HashMap<String, Object>();
		transfer_map.put("fieldTrancode", "200001111");
		
		transfer_map.put("field2", "6226800005404551"); 
		transfer_map.put("field3","004000"); 
		transfer_map.put("field4", "000000000005");
		transfer_map.put("field7", "000000");
		transfer_map.put("field11",rand );// <!--终端流水号累加-->
		transfer_map.put("field12", "000000"); 
		transfer_map.put("field13","0000");
		transfer_map.put("field14", "1102");
		transfer_map.put("field22", "021"); 
		transfer_map.put("field25","00"); 
		transfer_map.put("field26", "12"); 
		transfer_map.put("field32",""); 
		transfer_map.put("field33", ""); 
		transfer_map.put("field41","11111111"); 
		transfer_map.put("field42", "503055158120010");
		transfer_map.put("field43", ""); 
		transfer_map.put("field46", "");
		transfer_map.put("field47", ""); 
		transfer_map.put("termMobile", "18600153271");
		transfer_map.put("ReaderID", "00191020120920010000");
		transfer_map.put("PSAMID", "11111111");
		transfer_map.put("field49", "156");
		transfer_map.put("field52", "94A9EE84A2F88450"); 
		String transfer_map_f60 = "01000014000500"; 
		transfer_map.put("field60",transfer_map_f60); 
		transfer_map.put("field62", "1");
		transfer_map.put("field102", "111111"); 
		transfer_map.put("field103","111111"); 
		transfer_map.put("fieldAddress", "116.47215314255,39.897608228432,北京市北京市朝阳区"); 
		transfer_map.put("fieldMAB", ""); 
		transfer_map.put("fieldMAC", "");
		/* ========================================================== */
		/* 图片toBase64 */
		//String picString = ImageToBase64.GetImageToStr("D:/psu.jpg");
		
		/* 签购单上传 */
		Map<String, Object> preserveImage_map = new HashMap<String, Object>();
		preserveImage_map.put("fieldTrancode", "500000001");
		preserveImage_map.put("field7", "0810");
		preserveImage_map.put("field11", "243648");// <!--原交易终端流水号-->
		preserveImage_map.put("field41", "19820405");
		preserveImage_map.put("field42", "C19800101010101");
		preserveImage_map.put("termMobile", "");
		preserveImage_map.put("ReaderID", "");
		preserveImage_map.put("PSAMID", "");
		String preserveImage_map_f60 = "00600019";
		preserveImage_map.put("field60", preserveImage_map_f60);
		//preserveImage_map.put("fieldImage", picString);
		
		/* 校验商户密码 */
		Map<String, Object> verifyMerchPassword_map = new HashMap<String, Object>();
		verifyMerchPassword_map.put("fieldTrancode", "100005");
		verifyMerchPassword_map.put("fieldVersion", "1");
		verifyMerchPassword_map.put("field11", rand);
		verifyMerchPassword_map.put("field41", "19820468");
		verifyMerchPassword_map.put("field42", "496651407420001");
		verifyMerchPassword_map.put("termMobile", "18600153271");
		verifyMerchPassword_map.put("ReaderID", "00191020121020010022");
		verifyMerchPassword_map.put("PSAMID", "12345678");
		verifyMerchPassword_map.put("fieldMerchPWD", "803A8A0AC4CE757C05A6F19774544D7421893E006D7F5BB80752C33E6A0005E55711FCE2933CD5E55CD18DA8350E2F80B6137604028859AB59B49437E8E4B02827F8D9FC814FF0D34562B3FCAD75728D4A250387F6C2ABD3A1073E05E9F6FE05B04F775FF36C8BC448BE1DBD6283441C9AD83107D9183B0AE162F7699827BE69");
		
		/* 查询验证码 */
		Map<String, Object> queryCaptcha_map = new HashMap<String, Object>();
		queryCaptcha_map.put("fieldTrancode", "999000003");
		queryCaptcha_map.put("fieldChannel", "06");
		queryCaptcha_map.put("condition", "111111");
		
		/*初始化上下文*/
		txContext = new TxContext();
		txContext.setReq_map(login_map);
		
		TxAction action = new TxActionImp();
		action.first(txContext);
		
		
		txContext.getResp_map();

	}
}
