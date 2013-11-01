package org.maple.dxp.packets;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dhcc.pos.packets.util.StringUtil;

/**
 * 消息工厂
 */
/**
 * @author maple
 *
 */
public class MessageFactory {
	
	
	// 当flag 为true解析自定义域，为false时不解析
	boolean flag = false;
	
	/*是否 校验请求及响应报文必输项 以及必输项是否有值
	 * true为校验	false为不校验
	 * */
	boolean isCheck = false;
	
	private static MessageFactory instance = new MessageFactory();

	private MessageFactory() {
		try {
//			ConfigParser.createFromXMLConfigFile(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MessageFactory getInstance() {

		return instance;

	}

	// 字段信息
	Map<String, FieldInfo> map_Field = new HashMap<String, FieldInfo>();

	Map<String, List<FieldInfo>> req_map_Field = new HashMap<String, List<FieldInfo>>();
	Map<String, List<FieldInfo>> resp_map_Field = new HashMap<String, List<FieldInfo>>();

	Map<String, List<CustomFieldInfo>> resp_custom_Field = new HashMap<String, List<CustomFieldInfo>>();
	Map<String, List<CustomMsgInfo>> customMsgInfo = new HashMap<String, List<CustomMsgInfo>>();

	
	public static Document openXML(byte[] msg) {
		Document document = null;
		InputStream is = new ByteArrayInputStream(msg);
		try {
			SAXReader reader = new SAXReader();
			document = reader.read(is);
			System.out.println("openXML() successful ..." + "\r");
		} catch (Exception e) {
			System.out.println("openXML() Exception:" + e.getMessage() + "\r");
		}
		return document;
	}
	
	
	/**创建xml
	 * 提取map中key value值 对应到xml 节点
	 * 例如：
	 *map  
	 *	{fieldTranscode=800003,field11=000000}
	 *xml
	 *	<fieldTranscode>800003</fieldTranscode><field11>000000</field11>
	 * @param map 
	 * @return
	 */
	public String createRequest(Map<String, Object> map) {
		System.out.println("####################createRequest####################" + "\r");
		String transCode = (String) map.get("fieldTrancode");
		try { 
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("root");
//			Element trancode = root.addElement("fieldTrancode");
//			trancode.addText(transCode);
			List<FieldInfo> req_list = req_map_Field.get(transCode);
			if (req_list == null || req_list.size() == 0)
				throw new RuntimeException("\t" + "get trans_code:" + transCode
						+ "  request paramInfo is error! " + "\t");
			if(isCheck == true){
				for (FieldInfo info : req_list) {
					String name = info.getName();
					/*System.out.println(name);*/
					boolean isMustFlag = false;
					info = map_Field.get(name);
					
//					System.out.println("map_Field.get(name):" + map_Field.get(name));
					if (info.getIsMustFiled() && map.get(name) == null)
						throw new IllegalArgumentException(name
								+ " is must input! ");
					
					if (map.get(name) != null ) {
						if( !map.get(name).equals("")){
							String value = info.parse((String)map.get(name)).toString();
							
							/*System.out.println(value + "\r");*/
							Element element = root.addElement(name);
							element.addText(value);
						}else{
							Element element = root.addElement(name);
							element.addText("");
							/*System.out.println("" + "\r");*/
						}
					}
	
					if (map.get(name) == null
							&& StringUtil.isNotNull(info.getValue())) {
						String value = info.getValue();
					/*	System.out.println(value + "\r");*/
						Element element = root.addElement(name);
						element.addText(value);
					}
				}
			}else{
				for (FieldInfo info : req_list) {
					String name = info.getName();
					/*System.out.println(name);*/
					boolean isMustFlag = false;
					info = map_Field.get(name);
					
					if (map.get(name) != null ) {
						if( !map.get(name).equals("")){
							String value = info.parse((String)map.get(name)).toString();
							
							/*System.out.println(value + "\r");*/
							Element element = root.addElement(name);
							element.addText(value);
						}else{
							Element element = root.addElement(name);
							element.addText("");
							/*System.out.println("" + "\r");*/
						}
					}
	
					if (map.get(name) == null && StringUtil.isNotNull(info.getValue())) {
						String value = info.getValue();
						/*System.out.println(value + "\r");*/
						Element element = root.addElement(name);
						element.addText(value);
					}
				}
			}
			/*以字符串形式返回*/
			return document.asXML();
		} catch (NullPointerException e) {

			throw new NullPointerException();

		} catch (RuntimeException e) {

			throw new RuntimeException(e);
		}
	}

	// 解析xml 
	public Map<String, Object> parseMessage(byte[] buf, String transCode) {
		System.out.println("####################parseMessage####################");
		List<FieldInfo> resp_list = new ArrayList<FieldInfo>();
		Map<String, Object> map = new HashMap<String, Object>();
		
			//打开byte类型的xml文件
			Document document = openXML(buf);
			if(document != null){
				Element root = document.getRootElement();
				
				System.out.println("\r" + "root:" + "\t" + root.asXML() + "\r");
				
				/*resp_map_Field 调用ConfigParser通过xml文件创建消息工厂；
				 * 以交易码对应（域名、值、isflag）的形式加载进该数组map（域名="",value="",isflag=""）;
				 * 域名有、值为空（值获取见举例解释），isflag有
				 * 举例如下：
				 * Document document = openXML(buf);
					String name = "filedTrancode";
					if(document != null){
					Element root = document.getRootElement();
					String value = root.elementText(name);
				 * */
				
				resp_list = resp_map_Field.get(transCode);
				
				/*Iterator<FieldInfo> it = resp_list.iterator();
				while(it.hasNext()){
					
					System.out.println("it value:" +root.elementText(it.next().getName()) );
				}*/
				
				
					
				/*校验是否必输项及必输项是否有值*/
				if(isCheck == true){
					for (FieldInfo info : resp_list) {
						String name = info.getName();
						
						/*System.out.println(name);*/
						  if (info.getIsMustFiled() && root.elementText(name) == null){
							throw new IllegalArgumentException("\t" + name
									+ " is must input! " + "\t");
						}
						if (root.elementText(name) != null) {
							String value = root.elementText(name);
							
						/*	System.out.println(value + "\r");*/
							
							map.put(name, value);
						}
					}
				}else{
					for (FieldInfo info : resp_list) {
						String name = info.getName();
						
					/*	System.out.println(name);*/
						if (root.elementText(name) == null){
							
							String value = "";
							
							/*System.out.println(value + "\r");*/
							
							map.put(name, value);
						}else if (root.elementText(name) != null) {
							String value = root.elementText(name);
							
							/*System.out.println(value + "\r");*/
							
							map.put(name, value);
						}
					}
				}
			}else{
				throw new IllegalArgumentException("接收到的报文为空或不合法");
				//空指针 需做处理
			}
			if (flag) {
				/* 解析自定义域 */
				parseCustomFieldInfo(transCode, map);
			}
		
		return map;
	}

	/**解析自定义域 
	 * @param transCode 交易码
	 * @param map	
	 */
	public void parseCustomFieldInfo(String transCode, Map<String, Object> map) {
		System.out.println("####################parseCustomFieldInfo####################");
		List<CustomMsgInfo> listCustomMsg = customMsgInfo.get(transCode);
		for (CustomMsgInfo customMsginfo : listCustomMsg) {
			String id = null;
			int len = -1;
			String cond = null;
			String value = null;
			String bitMapId = null;

			bitMapId = customMsginfo.getBitMap();
			id = customMsginfo.getId();
			len = customMsginfo.len;
			cond = customMsginfo.getCond();

			// 有条件域，条件域取值不能为空
			if (StringUtil.isNotNull(cond) && StringUtil.isNull((String) map.get(cond)))
				throw new RuntimeException("\t" + "Conditional value is null! " + "\t");
			/*System.out.println("\t" + "id:" + id + "\t");*/
			value = (String) map.get(id);

			// 自定义域长度必须符合折解的长度
			// System.out.println(value.length()+":"+len);
			/*System.out.println("\t" + "value:" + value + "\t");
			System.out.println("\t" + "value.length:" + value.length() + "\t");*/
			if (value.length() < len) {
				throw new RuntimeException("\t" + "custom field  length is error ! " + "\t");
			}

			// 折解
			List<CustomFieldInfo> custom_bitMap = resp_custom_Field
					.get(bitMapId);
			for (int i = 0; i < custom_bitMap.size(); i++) {
				String fname = null;
				String fvalue = null;
				CustomFieldInfo cusInfo = custom_bitMap.get(i);
				/*System.out.println("\t" + "cusInfo:" + cusInfo + "\t");*/
				fname = cusInfo.getName();
				/*System.out.println("\t" + "fname:" + fname + "\t");*/
				fvalue = value.substring(0, cusInfo.getLen());
				/*System.out.println("\t" + "fvalue:" + fvalue + "\t");*/
				value = value.substring(cusInfo.getLen());

				if (i != 0)
					map.put(fname, fvalue);

			}
		}
	}
	
	/*set get 方法*/
	
	public Map<String, FieldInfo> getMap_Field() {
		return map_Field;
	}

	public void setMap_Field(Map<String, FieldInfo> map_Field) {
		this.map_Field = map_Field;
	}

	public Map<String, List<FieldInfo>> getReq_map_Field() {
		return req_map_Field;
	}

	public void setReq_map_Field(Map<String, List<FieldInfo>> req_map_Field) {
		this.req_map_Field = req_map_Field;
	}

	public Map<String, List<FieldInfo>> getResp_map_Field() {
		return resp_map_Field;
	}


	public void setResp_map_Field(Map<String, List<FieldInfo>> resp_map_Field) {
		this.resp_map_Field = resp_map_Field;
	}

	

	public Map<String, List<CustomFieldInfo>> getResp_custom_Field() {
		return resp_custom_Field;
	}

	public void setResp_custom_Field(
			Map<String, List<CustomFieldInfo>> resp_custom_Field) {
		this.resp_custom_Field = resp_custom_Field;
	}

	public Map<String, List<CustomMsgInfo>> getCustomMsgInfo() {
		return customMsgInfo;
	}

	public void setCustomMsgInfo(Map<String, List<CustomMsgInfo>> customMsgInfo) {
		this.customMsgInfo = customMsgInfo;
	}


}
