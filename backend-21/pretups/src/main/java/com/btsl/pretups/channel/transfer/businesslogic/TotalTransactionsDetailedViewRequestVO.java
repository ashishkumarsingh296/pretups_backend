package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class TotalTransactionsDetailedViewRequestVO {

	
	@JsonProperty("reqGatewayLoginId")
	private String reqGatewayLoginId;
	
	@JsonProperty("data")
	private TotalTransactionsDetailedViewData data;
	
	@JsonProperty("sourceType")
	private String sourceType;
	
	@JsonProperty("reqGatewayType")
	private String reqGatewayType;
	
	@JsonProperty("reqGatewayPassword")
	private String reqGatewayPassword;
	
	@JsonProperty("servicePort")
	private String servicePort;
	
	@JsonProperty("reqGatewayCode")
	private String reqGatewayCode;

	
	@JsonProperty("reqGatewayLoginId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "pretups", required = true/* , defaultValue = "" */)
	public String getReqGatewayLoginId() {
		return reqGatewayLoginId;
	}

	public void setReqGatewayLoginId(String reqGatewayLoginId) {
		this.reqGatewayLoginId = reqGatewayLoginId;
	}

	
	public TotalTransactionsDetailedViewData getData() {
		return data;
	}

	public void setData(TotalTransactionsDetailedViewData data) {
		this.data = data;
	}

	
	@JsonProperty("sourceType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required = true/* , defaultValue = "" */)
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	
	@JsonProperty("reqGatewayType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = true/* , defaultValue = "" */)
	public String getReqGatewayType() {
		return reqGatewayType;
	}

	public void setReqGatewayType(String reqGatewayType) {
		this.reqGatewayType = reqGatewayType;
	}

	
	@JsonProperty("reqGatewayPassword")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getReqGatewayPassword() {
		return reqGatewayPassword;
	}

	public void setReqGatewayPassword(String reqGatewayPassword) {
		this.reqGatewayPassword = reqGatewayPassword;
	}

	
	@JsonProperty("servicePort")
	@io.swagger.v3.oas.annotations.media.Schema(example = "190", required = true/* , defaultValue = "" */)
	public String getServicePort() {
		return servicePort;
	}

	public void setServicePort(String servicePort) {
		this.servicePort = servicePort;
	}

	
	@JsonProperty("reqGatewayCode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "REST", required = true/* , defaultValue = "" */)
	public String getReqGatewayCode() {
		return reqGatewayCode;
	}

	public void setReqGatewayCode(String reqGatewayCode) {
		this.reqGatewayCode = reqGatewayCode;
	}
}
	
	class TotalTransactionsDetailedViewData{
		
		@JsonProperty("fromDate")
		String fromDate;
		
		@JsonProperty("toDate")
		String toDate;
		
		@JsonProperty("status")
		String status;
		
		@JsonProperty("msisdn")
		String msisdn;
		
		@JsonProperty("loginid")
		String loginId;
		
		@JsonProperty("password")
		String password;
		
		@JsonProperty("extcode")
		String extCode;
		
		public String getExtCode() {
			return extCode;
		}

		public void setExtCode(String extCode) {
			this.extCode = extCode;
		}




		@JsonProperty("transactionID")
		String transactionID;
		
		@JsonProperty("extnwcode")
		String extnwcode;
		
		@JsonProperty("fromRow")
		String fromRow;
		
		@JsonProperty("toRow")
		String toRow;
		
		@JsonProperty("msisdn2")
		String msisdn2;
		
		@JsonProperty("pin")
		String pin;
		
		
		
		
		@JsonProperty("fromRow")
		@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = false, defaultValue = "1")
		public String getFromRow() {
			return fromRow;
		}

		public void setFromRow(String fromRow) {
			this.fromRow = fromRow;
		}

		@JsonProperty("toRow")
		@io.swagger.v3.oas.annotations.media.Schema(example = "10", required = false, defaultValue = "10")
		public String getToRow() {
			return toRow;
		}

		public void setToRow(String toRow) {
			this.toRow = toRow;
		}
		
		@JsonProperty("extnwcode")
		@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = true/* , defaultValue = "" */)
		public String getExtnwcode() {
			return extnwcode;
		}

		public void setExtnwcode(String extnwcode) {
			this.extnwcode = extnwcode;
		}
		@JsonProperty("loginid")
		@io.swagger.v3.oas.annotations.media.Schema(example = "", required = false/* , defaultValue = "" */)
		public String getLoginId() {
			return loginId;
		}

		public void setLoginId(String loginId) {
			this.loginId = loginId;
		}
		@JsonProperty("password")
		@io.swagger.v3.oas.annotations.media.Schema(example = "", required = false/* , defaultValue = "" */)
		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
		@JsonProperty("transactionID")
		@io.swagger.v3.oas.annotations.media.Schema(example = "", required = false/* , defaultValue = "" */)
		public String getTransactionID() {
			return transactionID;
		}

		public void setTransactionID(String transactionID) {
			this.transactionID = transactionID;
		}
		@JsonProperty("pin")
		@io.swagger.v3.oas.annotations.media.Schema(example = "", required = false/* , defaultValue = "" */)
		public String getPin() {
			return pin;
		}

		public void setPin(String pin) {
			this.pin = pin;
		}




		
		
	
		@io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = true, defaultValue = "Msisdn of user whose transaction details you want to get")
		public String getMsisdn2() {
			return msisdn2;
		}

		public void setMsisdn2(String msisdn2) {
			this.msisdn2 = msisdn2;
		}

		@JsonProperty("fromDate")
		@io.swagger.v3.oas.annotations.media.Schema(example = "11/03/20", required = true/* , defaultValue = "" */)
		public String getFromDate() {
			return fromDate;
		}

		public void setFromDate(String fromDate) {
			this.fromDate = fromDate;
		}

		@JsonProperty("toDate")
		@io.swagger.v3.oas.annotations.media.Schema(example = "18/03/20", required = true/* , defaultValue = "" */)
		public String getToDate() {
			return toDate;
		}

		public void setToDate(String toDate) {
			this.toDate = toDate;
		}

		@JsonProperty("msisdn")
		@io.swagger.v3.oas.annotations.media.Schema(example = "72525252"/* , defaultValue = "" */)
		public String getMsisdn() {
			return msisdn;
		}

		public void setMsisdn(String msisdn) {
			this.msisdn = msisdn;
		}
		
		
		

		@JsonProperty("status")
		@io.swagger.v3.oas.annotations.media.Schema(example = "PASS", required=true,defaultValue = "PASS/FAIL/ALL")
		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
		
		
		
		
		

		@Override
		public String toString() {
			return "TotalTransactionsDetailedViewRequestVO[fromDate=" + fromDate + ", toDate=" + toDate + ", msisdn=" + msisdn
					+ ", status="+ status+ ",transactionId="+ transactionID + "]";
		}


}
