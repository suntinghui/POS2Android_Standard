package com.dhcc.pos.core;

import java.util.HashMap;
import java.util.Map;

import com.dhcc.pos.packets.util.StringUtil;



/**
 * 实现ApplicationContextAware接口
 * 可得到applicationContext
 * @author maple
 *
 */
public class TransDispatcher {
	static Map<String, Object> transTypeMap;

	static{
		transTypeMap = new HashMap<String, Object>();
		transTypeMap.put("000000", "txAction");
	}

	public TxAction dispatcher(String trancode) {
		TxAction action = null;
		String transType = null;

		transType = (String) transTypeMap.get(trancode);
		if (StringUtil.isNotNull(transType)){
//			action = new TxActionImp();
		}else{
			/*transTypeMap 中没有特殊的处理 都进人txAction 中进行处理*/
			transType = (String) transTypeMap.get("000000");
			action = new TxActionImp();
		}
		return action;
	}


	/* set get方法 */
	public void setTransTypeMap(Map<String, Object> transTypeMap) {
		this.transTypeMap = transTypeMap;
	}

	public Map getTransTypeMap() {
		return transTypeMap;
	}


}
