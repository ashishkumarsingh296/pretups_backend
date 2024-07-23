package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.List;
import java.util.Map;

import com.btsl.common.BaseResponse;

public class PaymentModeDetailsResponse extends BaseResponse{
	
	private Map<String, List<ProductPaymentModesVO>> details;

	public Map<String, List<ProductPaymentModesVO>> getDetails() {
		return details;
	}

	public void setDetails(Map<String, List<ProductPaymentModesVO>> details) {
		this.details = details;
	}

	@Override
	public String toString() {
		StringBuffer sbf=new StringBuffer();
		sbf.append("PaymentModeDetailsResponse [details=" + details + "]");
		return sbf.toString();
	}
	
	
	
	
	 
  
	
}
