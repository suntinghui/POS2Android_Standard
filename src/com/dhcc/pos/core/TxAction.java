package com.dhcc.pos.core;


import com.dhcc.pos.packets.CnMessage;

public interface TxAction {
	/**
	 * 起始函数
	 * 
	 * @param context
	 *           请求报文
	 * 
	 */
	public void first(TxContext context);
	/**
	 * 主处理函数
	 * 
	 * @param context
	 *            上下文
	 * 
	 */
	public void process(TxContext context);


	/**注册请求报文
	 * @param m
	 * @return
	 */
	public CnMessage registerReqMsg(CnMessage m, TxContext context);
	
	
	/**
	 * 创建请求
	 * 
	 * @param context
	 *            上下文 创建请求 从容器OptionContext中得到req_json(String类型) 然后把它转换成map类型
	 *            其次通过报文工厂messageFactory利用map创建请求 请求：req_xml(String)
	 *            把req_xml(String)请求数据放入容器OptionContext类req_xml（String类型）中
	 */
	public byte[] beforeProcess(TxContext context);

	/**
	 * 解析返回（响应）数据
	 * 
	 * @param context
	 *            上下文
	 * 
	 *            解析返回（响应）数据(cbs传过来的Resp_xml(String)响应数据)
	 *            从容器OptionContext容器中得到Resp_xml(String)响应数据xml
	 *            其次利用报文工厂messageFactory解析Resp_xml
	 *            （byte数组形式）成json格式的resp_map(Map)
	 *            解析完存入OptionContext容器resp_map(Map)中
	 *            把json格式的resp_map(map)转换为json格式的Resp_json(String)
	 *            把Resp_json(String)放入容器OptionContext类中的Resp_json（String）中
	 * 
	 */
	public void afterProcess(TxContext context);

	/**
	 * 异常处理
	 * 
	 * @param context
	 * 
	 */
	public void processError(TxContext context);

	/**
	 * 結果出來（记录操作日志、记录流水）
	 * 
	 * @param context
	 *            上下文
	 */
	public void processResult(TxContext context);

	/**
	 * 測試函數
	 * 
	 * @return
	 */
	public String test();

}