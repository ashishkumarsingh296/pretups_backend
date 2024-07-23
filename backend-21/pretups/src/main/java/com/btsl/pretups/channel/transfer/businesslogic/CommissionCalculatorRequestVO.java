package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class CommissionCalculatorRequestVO {
	
	@JsonProperty("reqGatewayLoginId")
	private String reqGatewayLoginId;
	
	private CommissionCalculatorData data;
	
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

	
	public CommissionCalculatorData getData() {
		return data;
	}

	public void setData(CommissionCalculatorData data) {
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
	
	class CommissionCalculatorData{
		
		@JsonProperty("fromdate")
		String fromdate;
		
		@JsonProperty("todate")
		String todate;
		
		@JsonProperty("msisdn")
		String msisdn;
		
		@JsonProperty("extnwcode")
		String extnwcode;
		
		@JsonProperty("msisdn2")
		String msisdn2;
        
		@JsonProperty("pin")
		String pin;
        
		@JsonProperty("loginid")
		String loginid;
		
		@JsonProperty("loginid2")
		String loginid2;
        
		@JsonProperty("password")
		String password;
        
		@JsonProperty("extcode")
		String extcode;
		
		@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
	    @JsonProperty("extnwcode")
		public String getExtnwcode() {
			return extnwcode;
		}

		public void setExtnwcode(String extnwcode) {
			this.extnwcode = extnwcode;
		}

		@io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = true/* , defaultValue = "" */)
	    @JsonProperty("msisdn2")
		public String getMsisdn2() {
			return msisdn2;
		}

		public void setMsisdn2(String msisdn2) {
			this.msisdn2 = msisdn2;
		}

		@io.swagger.v3.oas.annotations.media.Schema(example = "2468", required = true/* , defaultValue = "" */)
		@JsonProperty("pin")
		public String getPin() {
			return pin;
		}

		public void setPin(String pin) {
			this.pin = pin;
		}

		@io.swagger.v3.oas.annotations.media.Schema(example = "ydist", required = true/* , defaultValue = "" */)
	    @JsonProperty("loginid")
		public String getLoginid() {
			return loginid;
		}

		public void setLoginid(String loginid) {
			this.loginid = loginid;
		}

		@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	    @JsonProperty("password")
		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@io.swagger.v3.oas.annotations.media.Schema(example = "ABC", required = true/* , defaultValue = "" */)
		@JsonProperty("extcode")
		public String getExtcode() {
			return extcode;
		}
		
		
		@io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required = true/* , defaultValue = "" */)
		@JsonProperty("loginid2")
		public String getLoginid2() {
			return loginid2;
		}

		public void setLoginid2(String loginid2) {
			this.loginid2 = loginid2;
		}

		public void setExtcode(String extcode) {
			this.extcode = extcode;
		}

		@JsonProperty("fromdate")
		@io.swagger.v3.oas.annotations.media.Schema(example = "11/03/20", required = true/* , defaultValue = "" */)
		public String getFromDate() {
			return fromdate;
		}

		public void setFromDate(String fromDate) {
			this.fromdate = fromDate;
		}

		@JsonProperty("todate")
		@io.swagger.v3.oas.annotations.media.Schema(example = "18/03/20", required = true/* , defaultValue = "" */)
		public String getToDate() {
			return todate;
		}

		public void setToDate(String todate) {
			this.todate = todate;
		}

		@JsonProperty("msisdn")
		@io.swagger.v3.oas.annotations.media.Schema(example = "72525252", required = true/* , defaultValue = "" */)
		public String getMsisdn() {
			return msisdn;
		}

		public void setMsisdn(String msisdn) {
			this.msisdn = msisdn;
		}

		@Override
		public String toString() {
			return "CommissionCalculatorData [fromdate=" + fromdate + ", todate=" + todate + ", msisdn=" + msisdn
					+ ", extnwcode=" + extnwcode + ", msisdn2=" + msisdn2 + ", pin=" + pin + ", loginid=" + loginid
					+ ", loginid2=" + loginid2 + ", password=" + password + ", extcode=" + extcode + "]";
		}


		
		
	}

