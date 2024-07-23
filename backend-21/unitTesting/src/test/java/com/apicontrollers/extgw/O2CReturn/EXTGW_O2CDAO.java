package com.apicontrollers.extgw.O2CReturn;

import java.util.HashMap;

public class EXTGW_O2CDAO {

	private HashMap<String, String> apiData;
	private String loginID;
	private String category;
	private String domain;
	private String TCPName;
	private String productCode;
	
	public HashMap<String, String> getApiData() {
		return apiData;
	}
	public void setApiData(HashMap<String, String> apiData) {
		this.apiData = apiData;
	}
	public String getLoginID() {
		return loginID;
	}
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getTCPName() {
		return TCPName;
	}
	public void setTCPName(String tCPName) {
		TCPName = tCPName;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
}
