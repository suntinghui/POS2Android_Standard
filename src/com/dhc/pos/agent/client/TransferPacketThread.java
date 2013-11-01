package com.dhc.pos.agent.client;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.dhc.pos.activity.BaseActivity;
import com.dhc.pos.agent.client.db.ReversalDBHelper;
import com.dhc.pos.client.exception.HttpException;
import com.dhc.pos.fsk.FSKOperator;
import com.dhc.pos.fsk.FSKService;
import com.dhc.pos.model.FieldModel;
import com.dhc.pos.model.ReversalModel;
import com.dhc.pos.model.TransferModel;
import com.dhcc.pos.core.TxAction;
import com.dhcc.pos.core.TxActionImp;
import com.dhcc.pos.core.TxContext;
import com.itron.android.ftf.Util;
import com.itron.protol.android.CommandReturn;

/**
 * @author sth
 * 
 */

public class TransferPacketThread extends Thread{
	
	private String transferCode;  // 交易码
	private TransferModel transferModel;
	private HashMap<String,String> map; // 字段值，需要替换由config解析出来的value值
	private Handler handler; // 响应的Handler
	
	private JSONStringer sendJSONStringer;
	private HashMap<String, String> sendFieldMap;
	private HashMap<String, String> receiveFieldMap;
	
	public TransferPacketThread(String transferCode, HashMap<String,String> map, Handler handler){
		this.transferCode = transferCode;
		this.map = map;
		this.handler = handler;
	}
	
	public HashMap<String, String> getSendMap(){
		return sendFieldMap;
	}
	
	public HashMap<String, String> getReceMap(){
		return receiveFieldMap;
	}
	
	@Override
	public void run() {
		Looper.prepare();
		this.pack(this.map);
		Looper.loop();
	}
	
	/**
	 * 根据配置文件生成JSON
	 * 
	 * @param tranCode 交易码
	 * @param dataMap 如果在界面中的值需要参与JSON的拼装
	 * @return 返回拼装的报文
	 */
	private void pack(HashMap<String, String> dataMap){
		try{
			transferModel = TransferLogic.getInstance().parseConfigXML("con_req_"+ this.transferCode +".xml");
			
			// 在报文的配置中，有可能值来自于本报文中某一个域的值，为了检索的效率，在tempMap中将已解析的值存储，在后面用到时直接在tmepMap中查找
			// 取本报文的值用$做前缀，此时一定要注意，取此值时，前面一定要已经有这个域的值
			sendFieldMap = new HashMap<String, String>();
			StringBuilder macsb = new StringBuilder();
			
			sendJSONStringer = new JSONStringer();
			sendJSONStringer.object();
			
			for (FieldModel model : transferModel.getFieldList()){
				StringBuffer sb = new StringBuffer();
				
				String[] values = model.getValue().split("#");
				for(String value : values){
					if (value.startsWith("$")){
						// 如果报文中某一域的值取自此报文的其他域的值，其值规定为将key的首末用'$'做前后缀
						// for example：field60  - 012#__PASMNO#$field11$
						if (sendFieldMap.containsKey(value.substring(1, value.length()-1))){
							sb.append(sendFieldMap.get(value.substring(1, value.length()-1)));
						} else {
							Log.e("conf_req_"+this.transferCode+".xml WRONG", "Set the value of '"+model.getKey()+"' before setting the value of '"+value.substring(1, value.length()-1)+"' !!!");
						}
						
					} else if (value.startsWith("__")){
						// 首先检查此值是否来自界面输入
						if (null != dataMap && dataMap.containsKey(value.substring(2))){
							sb.append(dataMap.get(value.substring(2)));
						} else{
							// 如果不是来自界面，那么就在AppDataCenter中寻找这个值。
							sb.append(AppDataCenter.getValue(value));
						}
						
					}else{
						// 如果不带下划线则直接将值拼装。
						sb.append(value);
					}
				}
				
				model.setValue(sb.toString());
				
				// 进行一步特殊处理，fieldImage为上传签购单的图片内容，一般为20-30K。我认为在其他地方不会使用该值，所以不在map中保存。
				if (!model.getKey().equals("fieldImage")){
					sendFieldMap.put(model.getKey(), model.getValue());
				}
				
				sendJSONStringer.key(model.getKey()).value(model.getValue());
				
				// 判断是否有参与mac计算的域
				/*
				if (model.isMacField()){
					mabsb.append(model.getKey()).append(";");
					macsb.append(model.getValue());
				}
				*/
				
				if (!model.getKey().trim().equals("fieldTrancode")){
					macsb.append(FormatFieldValue.format(model.getKey(), model.getValue()));
				}
			}
			
			// 如果该交易需要进行冲正，则将其记入数据库冲正表中。注意，这里可能会有问题，因为有可能网络不通，直接打回，也就是说没有从手机发出交易就需要进行充正。
			if (AppDataCenter.getReversalMap().containsKey(this.transferCode)){
				ReversalModel model = new ReversalModel();
				model.setTraceNum(sendFieldMap.get("field11"));
				model.setDate(AppDataCenter.getValue("__yyyy-MM-dd"));
				model.setContent(sendFieldMap);
				
				ReversalDBHelper helper = new ReversalDBHelper();
				helper.insertATransaction(model);
			}
			
			if (transferModel.shouldMac()){ // 需要进行MAC计算
				CalcMacHandler calcHandler = new CalcMacHandler();
				
				Log.e("calc mac:", macsb.toString());
				
				FSKOperator.execute("Get_MAC|int:0,int:1,string:null,string:" + macsb.toString(), calcHandler);
				
			} else{
				sendJSONStringer.endObject();
				Log.e("send JSON", sendJSONStringer.toString());
				
				this.sendPacket();
			}
			
		} catch(JSONException e){
			Log.e("TransferPacket", "parseConfigXML JSONException :"+ e.getMessage());
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			TransferLogic.getInstance().gotoCommonFaileActivity(e.getMessage());
		} 
	}
	
	private void sendPacket(){
		if (Constant.isEFET){
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.putAll(this.sendFieldMap);
			
			/*初始化上下文*/
			TxContext txContext = new TxContext();
			txContext.setReq_map(tempMap);
			
			TxAction action = new TxActionImp();
			action.first(txContext);
			
			
			HashMap<String, Object> respMap = (HashMap<String, Object>) txContext.getResp_map();
			Log.e("---", respMap.toString());
			
			receiveFieldMap = new HashMap<String, String>();
			for (String key : respMap.keySet()){
				this.receiveFieldMap.put(key, (String)respMap.get(key));
			}
			
			checkField39();
			
//			if (transferModel.shouldMac()){
//				CheckMacHandler checkHandler = new CheckMacHandler();
//				FSKOperator.execute("Get_CheckMAC|int:0,int:0,string:null,string:"+macsb.toString()+receiveFieldMap.get("field64"), checkHandler);//  计算MAC的数据+MAC（8字节）
//				
//			} else {
//				checkField39();
//			}
			
			return ;
		}
			
		// 如果是冲正则提示冲正。
		if (AppDataCenter.getReversalMap().containsValue(this.transferCode)){
			BaseActivity.getTopActivity().showDialog( "正在进行冲正，请稍候 ", transferCode);
			
		} else if (this.transferCode.equals("100005")){ // 登陆 (校验密码)
			//BaseActivity.getTopActivity().showDialog(BaseActivity.PROGRESS_DIALOG, "正在登录请稍候...");
			BaseActivity.getTopActivity().showDialog("正在登录请稍候...", transferCode);
			
		} else if(this.transferCode.equals("500000001")){ // 上传签购单，静默
			// do nothing
			
		} else if(this.transferCode.equals("999000003")){
			BaseActivity.getTopActivity().showDialog("正在获取验证码", transferCode);
			
		} else {
			BaseActivity.getTopActivity().showDialog("正在处理交易，请稍候 ", transferCode);
			
		}
		
		try{
			byte[] respByte = null;
			if (Constant.isStatic){
				respByte = StaticNetClient.getMessageByTransCode(this.transferCode);
			} else{
				respByte = HttpManager.getInstance().sendRequest(HttpManager.URL_JSON_TYPE, sendJSONStringer.toString().getBytes("GBK"));
			}
			parse(new String(respByte, "GBK"));
			BaseActivity.getTopActivity().hideDialog(BaseActivity.COUNTUP_DIALOG);
			
		}catch(HttpException e){
			BaseActivity.getTopActivity().hideDialog(BaseActivity.COUNTUP_DIALOG);
			BaseActivity.getTopActivity().hideDialog(BaseActivity.PROGRESS_DIALOG);
			TransferLogic.getInstance().gotoCommonFaileActivity(e.getMessage());
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
		} catch(Exception e){
			TransferLogic.getInstance().gotoCommonFaileActivity("服务器响应异常，请重试");
		}
	}
	
	private void parse(String jsonStr){
		Log.e("rece JSON", jsonStr);
		
		receiveFieldMap = new HashMap<String, String>();
		StringBuilder macsb = new StringBuilder(); // mac原文
		
		try{
			JSONTokener parse = new JSONTokener(jsonStr);
			JSONObject content = (JSONObject) parse.nextValue();
			
			@SuppressWarnings("unchecked")
			Iterator<String> keys = content.keys();
			while (keys.hasNext()){
				String key = (String)keys.next();
				receiveFieldMap.put(key, content.getString(key));
				// 顺序
				if (!key.trim().equals("fieldTrancode")){
					macsb.append(content.getString(key));
				}
				
			}
			
			// 收到应答后先校验MAC
			// String mabField = receiveFieldMap.get("fieldMAB");
			
			if (Constant.isStatic){
				checkField39();
				return;
			}
			
			// 如果是上传签购单交易
			if (this.transferCode.equals("500000001")){
				if (receiveFieldMap.containsKey("field39") && receiveFieldMap.get("field39").equals("00")){
					Message message = new Message();
					message.what = 0; // 回调TransferLogic
					message.obj = receiveFieldMap;
					message.setTarget(handler);
					message.sendToTarget();
				} 
				
			} else {
				if (transferModel.shouldMac()){
					CheckMacHandler checkHandler = new CheckMacHandler();
					FSKOperator.execute("Get_CheckMAC|int:0,int:0,string:null,string:"+macsb.toString()+receiveFieldMap.get("field64"), checkHandler);//  计算MAC的数据+MAC（8字节）
					
				} else {
					checkField39();
				}
				
				/*
				if (null != mabField && !mabField.trim().equals("")){
					String[] fields = mabField.split(";");
					StringBuilder macsb = new StringBuilder(); // mac原文
					for (String field : fields){
						macsb.append(receiveFieldMap.get(field));
					}
					
					CheckMacHandler checkHandler = new CheckMacHandler();
					
					FSKOperator.execute("Get_CheckMAC|int:0,int:0,string:null,string:"+macsb.toString()+receiveFieldMap.get("field64"), checkHandler);//  计算MAC的数据+MAC（8字节）
					
				} else{ 
					checkField39();
				}
				*/
			}
			
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	private void checkField39(){
		if (receiveFieldMap.containsKey("field39")){
			String field39 = receiveFieldMap.get("field39");
			
			// 收到应答后，如果此交易是一笔可能发冲正的交易且响应码不是98，则删除冲正表中的此条交易记录
			if (!field39.equals("98") && AppDataCenter.getReversalMap().containsKey(this.transferCode)){
				ReversalDBHelper helper = new ReversalDBHelper();
				 helper.deleteAReversalTrans(receiveFieldMap.get("field11")); 
			}
			
			if(field39.equals("00")){
				// 只有在交易成功的时候取服务器日期
				if (receiveFieldMap.containsKey("field13")){
					AppDataCenter.setServerDate(receiveFieldMap.get("field13"));
				}
				
				if (AppDataCenter.getReversalMap().containsValue(this.transferCode)){
					// 交易成功。如果这笔交易是冲正交易，则要更新冲正表，将这笔交易的状态置为冲正成功。
					ReversalDBHelper helper = new ReversalDBHelper();
					helper.updateReversalState(receiveFieldMap.get("field11"));
				}
				
				Message message = new Message();
				message.what = 0; // 回调TransferLogic
				message.obj = receiveFieldMap;
				message.setTarget(handler);
				message.sendToTarget();
				
			} else if (field39.equals("98")){ // 当39域为98时要冲正。98 - 银联收不到发卡行应答
				TransferLogic.getInstance().gotoCommonFaileActivity("没有收到发卡行应答");
		 		TransferLogic.getInstance().reversalAction();
		 		
		 	} else { 
				// 39域不为00，交易失败，跳转到交易失败界面。其它失败情况比如MAC计算失败直接弹窗提示用户重新交易。
				// 如果是点付宝出现异常，已在FSKService中直接处理掉
		        
		        TransferLogic.getInstance().gotoCommonFaileActivity(receiveFieldMap.get("fieldMessage"));
			}
		} else {
			// 没有收到39域
			TransferLogic.getInstance().gotoCommonFaileActivity("交易失败，请重试 (39)");
		}
	}
	
	@SuppressLint("HandlerLeak")
	class CalcMacHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				CommandReturn cmdReturn = (CommandReturn) msg.obj;
				if (cmdReturn.Return_Result == 0){ // mac计算成功
					try {
						sendJSONStringer.key("field64").value(Util.BytesToString(cmdReturn.Return_PSAMMAC));
						sendJSONStringer.endObject();
						Log.e("send JSON", sendJSONStringer.toString());
						
						sendPacket();
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} 
					
				} else { // mac计算失败
					BaseActivity.getTopActivity().showDialog(BaseActivity.MODAL_DIALOG, "加密数据时出现异常，请重试.");
				}
				
				break;
			}
		}
	}
	
	@SuppressLint("HandlerLeak")
	class CheckMacHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				checkField39();
				break;
				
			case FSKService.RESULT_FAILED_CHECKMAC:
				// 如果是有对应冲正的交易，则发起第一次的自动冲正
				if (AppDataCenter.getReversalMap().containsKey(transferCode)){
					TransferLogic.getInstance().gotoCommonFaileActivity("校验服务器响应数据失败");
					
					TransferLogic.getInstance().reversalAction();
				} else {
					TransferLogic.getInstance().gotoCommonFaileActivity("校验服务器响应数据失败，请重新交易");
				}
				break;
			}
		}
		
	}
	
}
