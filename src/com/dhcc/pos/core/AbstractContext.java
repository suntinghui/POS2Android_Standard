package com.dhcc.pos.core;
/*
 * Copyright 2006-2007 ZHC Information Technology Corp. All rights reserved.
 * ZHC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */


import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Context抽象实现类
 * </p>
 * 
 */
public abstract class AbstractContext{
	
	private Map<String, Object> dataMap;
	private String operationId;
	private String returnCode;
	private String returnMsg;
	private String returnview;
	private String channel;
	private String remoteAddr;
	private Map<String, Object> confirmMap;
	private boolean confirmFlag;
	private boolean transferFlag;
	private boolean queryFlag;
	
	public AbstractContext() {
		dataMap = new HashMap();
	}

	public AbstractContext(String operationId) {
		this.operationId = operationId;
		dataMap = new HashMap();
	}

	public Map<String, Object> getConfirmMap() {
		return confirmMap;
	}

	public boolean isTransferFlag() {
		return transferFlag;
	}

	public void setTransferFlag(boolean transferFlag) {
		this.transferFlag = transferFlag;
	}

	public boolean isQueryFlag() {
		return queryFlag;
	}

	public void setQueryFlag(boolean queryFlag) {
		this.queryFlag = queryFlag;
	}

	public void setConfirmMap(Map<String, Object> confirmMap) {
		this.confirmMap = confirmMap;
	}

	public boolean isConfirmFlag() {
		return confirmFlag;
	}

	public void setConfirmFlag(boolean confirmFlag) {
		this.confirmFlag = confirmFlag;
	}

	public Object getData(String s) {
		return dataMap.get(s);
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public String getOperationId() {
		return operationId;
	}

	public void setData(String s, Object obj) {
		dataMap.put(s, obj);
	}

	public void addData(Map<String, Object> data) {
		dataMap.putAll(data);
	}

	public void setDataMap(Map<String, Object> map) {
		dataMap = map;
	}

	public void setOperationId(String s) {
		operationId = s;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public String getReturnview() {
		return returnview;
	}

	public void setReturnview(String returnview) {
		this.returnview = returnview;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String toString() {
		String operationType = "";
		if (queryFlag && !confirmFlag && !transferFlag) {
			operationType = "query";
		} else if (!queryFlag && confirmFlag && !transferFlag) {
			operationType = "confirm";
		} else if (!queryFlag && !confirmFlag && transferFlag) {
			operationType = "transfer";
		} else {
			operationType = "unknown type";
		}
		return "Context [operationId=" + operationId + ", operationType="
				+ operationType + ", channel=" + channel + ", remoteAddr="
				+ remoteAddr + ", returnview=" + returnview + ", returnCode="
				+ returnCode + ", returnMsg=" + returnMsg + "]" + ", dataMap="
				+ dataMap + ", confirmMap=" + confirmMap;
	}
}
