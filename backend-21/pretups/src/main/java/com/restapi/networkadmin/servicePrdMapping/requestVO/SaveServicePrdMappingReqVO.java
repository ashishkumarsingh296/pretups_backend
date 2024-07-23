package com.restapi.networkadmin.servicePrdMapping.requestVO;

import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.BaseRequest;

public class SaveServicePrdMappingReqVO  {
	
	private long amount;
	private String selectorCode;
	private String serviceType;
	private String modifyAllowed;

	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public String getSelectorCode() {
		return selectorCode;
	}
	public void setSelectorCode(String selectorCode) {
		this.selectorCode = selectorCode;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	
	public String getModifyAllowed() {
		return modifyAllowed;
	}
	public void setModifyAllowed(String modifyAllowed) {
		this.modifyAllowed = modifyAllowed;
	}
	
	
	
	
	
	

}
