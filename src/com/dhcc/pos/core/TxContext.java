package com.dhcc.pos.core;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author maple
 *	继承序列化
 */
public class TxContext extends AbstractContext implements Serializable{
	private static final long serialVersionUID = 1668248019557919236L;


	/**
	 * 
	 */
	public TxContext() {
		super();
		req_map = new HashMap<String, Object>();
		resp_map = new HashMap<String, Object>();
	}

	public TxContext(String operationId,String msgType) {
		super(operationId);
		this.msgType = msgType;
		req_map = new HashMap<String, Object>();
		resp_map = new HashMap<String, Object>();
	}
	
	String merchant_id ; 
	String merchant_name ;
	/*报文类型（交易码）*/
	String msgType;
	/*终端报文类型（交易码）*/
	String client_msgType;
	String des;//失败、错误、异常描述
	
	
	String OptionStatus=null;//-1未知，00成功 2 失败
	
	String termnl_id;

	String result_des=null;
	
	
	byte[] resp_byte = null;

	String req_json=null;
	String resp_json=null;
	
	String req_xml=null;
	String resp_xml=null;
	
	Map<String, Object> req_map ;
	
	Map<String, Object> resp_map ;

	
	public byte[] getResp_byte() {
		return resp_byte;
	}

	public void setResp_byte(byte[] resp_byte) {
		this.resp_byte = resp_byte;
	}

	public String getMerchant_id() {
		return merchant_id;
	}

	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}

	public String getMerchant_name() {
		return merchant_name;
	}

	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	
	public String getClient_msgType() {
		return client_msgType;
	}

	public void setClient_msgType(String client_msgType) {
		this.client_msgType = client_msgType;
	}

	public String getOptionStatus() {
		return OptionStatus;
	}

	public void setOptionStatus(String optionStatus) {
		OptionStatus = optionStatus;
	}

	public String getTermnl_id() {
		return termnl_id;
	}

	public void setTermnl_id(String termnl_id) {
		this.termnl_id = termnl_id;
	}

	public String getResult_des() {
		return result_des;
	}

	public void setResult_des(String result_des) {
		this.result_des = result_des;
	}

	public String getReq_json() {
		return req_json;
	}

	public void setReq_json(String req_json) {
		this.req_json = req_json;
	}

	public String getResp_json() {
		return resp_json;
	}

	public void setResp_json(String resp_json) {
		this.resp_json = resp_json;
	}

	public String getReq_xml() {
		return req_xml;
	}

	public void setReq_xml(String req_xml) {
		this.req_xml = req_xml;
	}

	public String getResp_xml() {
		return resp_xml;
	}

	public void setResp_xml(String resp_xml) {
		this.resp_xml = resp_xml;
	}

	public Map<String, Object> getReq_map() {
		return req_map;
	}

	public void setReq_map(Map<String, Object> req_map) {
		this.req_map = req_map;
	}

	public Map<String, Object> getResp_map() {
		return resp_map;
	}

	public void setResp_map(Map<String, Object> resp_map) {
		this.resp_map = resp_map;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}
	
}
