package com.dhc.pos;

public enum TransferMethodEnum {

	LOGIN("80001", "Login"), SIGNIN("80002", "Signin");

	private final String transferCode;
	private final String methodName;

	private TransferMethodEnum(String transferCode, String methodName) {
		this.transferCode = transferCode;
		this.methodName = methodName;
	}

	public String getTransferCode() {
		return this.transferCode;
	}

	public String getMethodName() {
		return this.methodName;
	}

}
