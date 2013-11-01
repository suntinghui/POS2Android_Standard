package org.maple.dxp.packets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dhcc.pos.packets.util.ClassLoaderUtil;
import com.dhcc.pos.packets.util.StringUtil;

/**
 * 配置文件解析器
 * 
 * @author maple
 * 
 */
public class ConfigParser {
	static String path = null;

	public ConfigParser(String path) {
		this.path = path;
	}

	public ConfigParser() {
		try {
			path = ClassLoaderUtil.getExtendResource2("config.msgConfig.xml");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过xml文件创建消息工厂
	 * 
	 * @param filepath
	 *            xml 文件完整路径
	 * @return
	 * @throws Exception
	 */
	public static MessageFactory createFromXMLConfigFile(String filePath){
		MessageFactory mfact = null;
		if (filePath == null)
			path = getpath();
		else
			path = filePath;
		InputStream ins = null;
		try {
			ins = new FileInputStream(path);
			mfact = MessageFactory.getInstance();
			
			if (ins != null) {
				System.out.println("\r\rparsing config from xml file: 【" + path + "】" + "\r");
				try {
					// 解析
					parse(mfact, ins);
				} finally {
					try {
						ins.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("\r" + "File not found: 【" + path + "】" + "\r");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
//			throws new FileNotFoundException("找不到文件");
		}
		return mfact;
	}

	/**
	 * /** 解析xml文件并初始化相关配置信息
	 * 
	 * @param mfact
	 * @param stream
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected static void parse(MessageFactory mfact, InputStream stream) {

		@SuppressWarnings("unused")
		final DocumentBuilderFactory docfact = DocumentBuilderFactory
				.newInstance();

		Document doc = null;
		
		try {
			SAXReader reader = new SAXReader();
			doc = reader.read(stream);
			System.out.println("openXML() successful ..." + "\r");
		} catch (Exception e) {
			System.out.println("openXML() Exception:" + e.getMessage() + "\r");
		}

		// 字段顺序解析
		// List<FieldInfo> fieldlist = new ArrayList<FieldInfo>();

		// 字段信息
		Map<String, FieldInfo> map_Field = new HashMap<String, FieldInfo>();
		Map<String, List<FieldInfo>> req_map_Field = new HashMap<String, List<FieldInfo>>();
		Map<String, List<FieldInfo>> resp_map_Field = new HashMap<String, List<FieldInfo>>();

		Map<String, List<CustomFieldInfo>> resp_custom_Field = new HashMap<String, List<CustomFieldInfo>>();
		Map<String, List<CustomMsgInfo>> customMsgInfo = new HashMap<String, List<CustomMsgInfo>>();

		List<Element> el_fieldlist = (List<Element>)doc.selectNodes("//parseinfo//field");
		Element el_req_fields = (Element) doc.selectSingleNode("//request");
		Element el_resp_fields = (Element) doc.selectSingleNode("//response");

		// Map<String, FieldInfo> custome_FieldInfo = new HashMap<String,
		// FieldInfo>();
		Element cus_fieldInfo = (Element) doc
				.selectSingleNode("//parseCustomfield");
		Element el_resp_cusfields = (Element) doc
				.selectSingleNode("//respCustomMsg");

		parseFieldInfo(el_fieldlist, map_Field);
		parseMsgInfo(el_req_fields, req_map_Field);
		parseMsgInfo(el_resp_fields, resp_map_Field);

		parseCustomfield(cus_fieldInfo, resp_custom_Field);// parseCustomfield
		parseRespCustomMsg(el_resp_cusfields, customMsgInfo);// respCustomMsg

		// printfield(req_map_Field);
		// printfield(resp_map_Field);
		mfact.setMap_Field(map_Field);
		mfact.setReq_map_Field(req_map_Field);
		mfact.setResp_map_Field(resp_map_Field);
		mfact.setCustomMsgInfo(customMsgInfo);
		mfact.setResp_custom_Field(resp_custom_Field);
	}

	/*=================parseCustomfield========================*/
	/**
	 * 解析 节点config/parseCustomfield
	 * 
	 * @param node
	 * @param map
	 */
	public static void parseCustomfield(Element node,
			Map<String, List<CustomFieldInfo>> map) {
		// System.out.println(String.format("nodeName :  %s, path :%s ",node.getName(),node.getPath()));
		List<CustomFieldInfo> list = null;
		List<Element> nodes = (List<Element>)node.elements("bitMap");
		// <customfield sn="" name="" len="" desc="" />
		for (Element msg : nodes) {
			list = new ArrayList<CustomFieldInfo>();
			String id = msg.attributeValue("id");

			List<Element> el_list = (List<Element>)msg.elements("customfield");

//			System.out.println(String.format("<parseCustomfield> bitMap 【 id : %s】", id));

			for (Element field : el_list) {

				getBitMapOfCustomMsg(list, field);

			}
			map.put(id, list);
		}
	}

	/**
	 * 解析 节点config/parseCustomfield/bitMap/customfield
	 * 
	 * @param list
	 * @param node
	 */
	public static void getBitMapOfCustomMsg(List<CustomFieldInfo> list,
			Element node) {
		// System.out.println(String.format("nodeName :  %s, path :%s ",node.getName(),node.getPath()));
		// int len = 0; // 长度
		String sn = null;// 序号
		String name = null;// 名称
		String length = null;// 名称
		String desc = null; // 描述

		CustomFieldInfo info = null;

		boolean flag = false;
		sn = node.attributeValue("sn");
		name = node.attributeValue("name");

		desc = node.attributeValue("desc");

//		System.out.println(String.format(
//				"<bitMap> customfield 【 sn :%s, name :%s, len :%d,  desc:%s】",
//				sn, name, StringUtil.convert(node.attributeValue("len")), desc));

		info = new CustomFieldInfo(sn, name, StringUtil.convert(node
				.attributeValue("len")), desc);
		list.add(info);
	}
	
	/*=================respsCustomMsg========================*/
	/**
	 * 解析 节点config/respCustomMsg
	 * 
	 * @param node
	 * @param map
	 */
	public static void parseRespCustomMsg(Element node,
			Map<String, List<CustomMsgInfo>> map) {
		// System.out.println(String.format("nodeName :  %s, path :%s ",node.getName(),node.getPath()));

		List<CustomMsgInfo> list = null;
		List<Element> nodes = node.elements("customMsg");
		// <customfield sn="" name="" len="" desc="" />
		for (Element msg : nodes) {
			list = new ArrayList<CustomMsgInfo>();
			String id = msg.attributeValue("id");

			List<Element> el_list = msg.elements("customfield");

//			System.out.println(String.format("<respCustomMsg> customMsg【id : %s 】", id));

			for (Element field : el_list) {
//				String a = field.attributeValue("id");
//				String b = field.attributeValue("len");
//				String c =field.attributeValue("cond");
//				String d =field.attributeValue("value");
//				String e =field.attributeValue("bitMap");
//				System.out.println(String.format("abcde:%s,%s,%s,%s,%s", a,b,c,d,e));
				getCustomeMsgInfo(list, field);

			}
			map.put(id, list);
		}
	}

	/**
	 * 解析 节点config/respCustomMsg/customMsg
	 * 
	 * @param list
	 * @param node
	 */
	public static void getCustomeMsgInfo(List<CustomMsgInfo> list, Element node) {

		// System.out.println(String.format("nodeName :  id : %s, path :%s ",node.getName(),node.getPath()));

		String id = null;

		String cond = null;
		String value = null;
		String bitMap = null;
		CustomMsgInfo info = null;

		id = node.attributeValue("id");
		cond = node.attributeValue("cond");

		value = node.attributeValue("value");
		bitMap = node.attributeValue("bitMap");
//		System.out.println(String
//				.format("<customMsg> customfield 【  id :%s, len : %d ,cond :%s ,value : %s ,bitMap :%s】 ",
//						id, StringUtil.convert(node.attributeValue("len")),
//						cond, value, bitMap));

		info = new CustomMsgInfo(id, StringUtil.convert(node
				.attributeValue("len")), cond, value, bitMap);
		list.add(info);
	}
	
	/*请求或响应报文的*/
	public static void parseMsgInfo(Element node,
			Map<String, List<FieldInfo>> map) {
		List<FieldInfo> list = null;
		List<Element> nodes = (ArrayList<Element>)node.elements("msg");
		for (Element msg : nodes) {
			list = new ArrayList<FieldInfo>();
			String id = msg.attributeValue("id");
			String name = msg.attributeValue("name");
			String des = msg.attributeValue("des");

			List<Element> el_list = (ArrayList<Element>)msg.elements("field");

			// System.out.println(
			// String.format("msg : %s,  id : %s, name: %s, count :%d",msg.getName(),id,name,el_list.size()));

			for (Element field : el_list) {
				/*field节点中的值放入list中*/
				getFieldInfo(list, field);

			}
			/*根据配置文件msgConfig中的id所对应的交易码来存放多个响应消息报文参数
			 * list：存放field节点中的数据（id,value,isflag）id=域名 ，value=域值 ，isflag= 真/假 （是否必填项）
			 * */
			map.put(id, list);
		}
	}

	/**
	 * @param list
	 * @param el
	 */
	public static void getFieldInfo(List<FieldInfo> list, Element el) {

		FieldInfo info = null;
		String id = null;
		String value = null;
		String isflag = null;
		boolean flag = false;
		id = el.attribute("id").getText();
		if (StringUtil.isNotNull(el.attributeValue("isflag"))) {
			isflag = el.attributeValue("isflag");
		} else {
			isflag = "N";
		}

		//
		if (StringUtil.isNotNull(el.attributeValue("value")))
			value = el.attributeValue("value");

		// System.out.println("=========="+id+"==========");
		if (isflag.equalsIgnoreCase("Y")){
			flag = true;
		}else{
			flag = false;
		}

		// System.out.println(
		// String.format("msgType:%s,id :%s, flag:%s",el.getParent().attributeValue("id"),id,flag));
		
		/*调用FieldInfo第二个构造函数
		 * 赋值给FieldInfo (name,value,isflag)*/
		info = new FieldInfo(id, value, flag);
		
		/*把单个的FiledInfo赋值给list*/
		list.add(info);
	}

	public static void parseFieldInfo(List<Element> nodes,
			Map<String, FieldInfo> map) {
		for (Element e : nodes) {
			String name = null;
			String type = null;
			String length = null;

			name = e.attributeValue("id");
			type = e.attributeValue("datatype");
			if (StringUtil.isNotNull(e.attributeValue("length")));
			length = e.attributeValue("length");

			FiledType datatype = FiledType.valueOf(type);
			
			/*调用FieldInfo第一个构造函数
			赋值给 FieldInfo(FiledType type, String name, int length)
			*/
			
			FieldInfo info = new FieldInfo(datatype, name,StringUtil.convert(length));
			
			/*把单个的FiledInfo赋值给map*/
			map.put(name, info);
		}
	}

	/**
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static Document openXML(String path) {
		Document document = null;

		try {
			InputStream ins = new FileInputStream(path);
			SAXReader reader = new SAXReader();
			document = reader.read(ins);
			// System.out.println(document.asXML());
			Element root = document.getRootElement();

			List<Element> list = document.selectNodes("//parseinfo//field");
			for (Element e : list)
				System.out.println(e.asXML());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return document;
	}

	protected static String getpath() {
		String path = null;
		try {
			path = ClassLoaderUtil.getExtendResource2("msgConfig.xml");
		} catch (MalformedURLException e) {

			e.printStackTrace();
		}
		return path;
	}

	public static void printfield(Map<String, List<FieldInfo>> map) {
		System.out.println("=======================");
		for (Map.Entry<String, List<FieldInfo>> entry : map.entrySet()) {
			System.out.println("key:" + entry.getKey());
			System.out.println("Value:" + entry.getValue());
			List<FieldInfo> list = entry.getValue();
			for (FieldInfo info : list)
				System.out.println("FieldName:" + info.getName());
		}
		System.out.println("=======================");
	}
}
