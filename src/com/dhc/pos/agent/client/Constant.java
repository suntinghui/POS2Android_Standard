package com.dhc.pos.agent.client;

public class Constant {
	
	public static boolean isStatic							= false;
	
	public static boolean isEFET							= true;
	
	public static final String APPFILEPATH 				    = "/data/data/" + ApplicationEnvironment.getInstance().getApplication().getPackageName();
	
	// assets下的文件保存路径
	public static final String ASSETSPATH 					= APPFILEPATH + "/assets/";
	// 签购单签名图片保存路径
	public static final String SIGNIMAGESPATH				= APPFILEPATH + "/signImages/";
	// 服务器下载图片保存路径
	public static final String IMAGEPATH					= APPFILEPATH + "/images/";
	
	// HTTP   220.194.44.216
	public static String XMLURL = "https://192.168.21.164:8443/pos/transfer.vurl?CHANNEL=android&locale=ch";
	public static String JSONURL = "https://192.168.21.164:8443/pos/transfer.tx?CHANNEL=android&locale=ch";
	
	public static String IMAGEURL = "http://192.168.21.164:8888/pos/img_andriod/";
	public static String FILESURL = "http://192.168.21.164:8888/pos/xmlFiles/";
	
	public static String DOWNLOADAPKURL = "http://192.168.21.164:8888/pos/client/";
	
	// Preference
	public static final String TRACEAUDITNUM 						= "traceAuditNum";
	public static final String BATCHNUM								= "batchNum";
	public static final String PASSWORD								= "password";
	public static final String PHONENUM								= "phoneNum";
	public static final String VERSION								= "version";
	
	public static final String NEWAPP								= "newApp";
	
	public static final String UUIDSTRING							= "uuidString";
	
	public static final String TRAFFIC_MONTH						= "traffic_month";
	public static final String MONTH_WIFISEND 						= "month_wifi_send";
	public static final String MONTH_WIFIRECEIVE 					= "month_wifi_receive";
	public static final String MONTH_MOBILESEND 					= "month_mobile_send";
	public static final String MONTH_MOBILESRECEIVE 				= "month_mobile_receive";
	
	public static final String TRAFFIC_DAY							= "traffic_day";
	public static final String DAY_WIFISEND 						= "day_wifi_send";
	public static final String DAY_WIFIRECEIVE 						= "day_wifi_receive";
	public static final String DAY_MOBILESEND 						= "day_mobile_send";
	public static final String DAY_MOBILESRECEIVE 					= "day_mobile_receive";
	
	// 商户名称
	public static final String MERCHERNAME							= "mercherName";
	
	// apk md5
	public static final String APKMD5VALUE							= "APKMD5Value";
	
	// 程序中最新公告编号
	public static final String SYSTEM_ANNOUNCEMENT_LASTEST_NUM		= "SystemAnnouncementLastestNum";
	public static final String SERVER_ANNOUNCEMENT_LASTEST_NUM		= "ServerAnnouncementLastestNum";
	
	// 公钥
	public static final String PUBLICKEY_MOD						= "publickey_mod";
	public static final String PUBLICKEY_EXP						= "publickey_exp";
	public static final String PUBLICKEY_VERSION					= "publickey_version";
	public static final String PUBLICKEY_TYPE						= "publickey_type";
	
	public static final String INIT_PUBLICKEY_MOD					= "D9D0D2224E6E84899184BBCD389F8EE08EB09EBA123948309804113B3F829D24D6093F1AFC153D113FAB8673114F4FABFDAAC9BB1B58B9E569B255BA4C338A2465642411A5EB0D68B78BB1B4E45AFF51580C3802AE01FF4DCF976D4CC681944C478FE3490A051F2B4894C321703C4D091E5365718509B20D23D78BBAD163E405";
	public static final String INIT_PUBLICKEY_EXP					= "010001";
	public static final String INIT_PUBLICKEY_VERSION				= "000000000000";
	
	// DeviceId
	public static final String DEVICEID								= "deviceId";
	
}