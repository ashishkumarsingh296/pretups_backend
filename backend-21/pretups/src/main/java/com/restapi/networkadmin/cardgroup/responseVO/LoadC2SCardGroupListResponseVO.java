package com.restapi.networkadmin.cardgroup.responseVO;

import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class LoadC2SCardGroupListResponseVO extends BaseResponse{
	
	private String reversalModifiedDateAsString;
	
	private Date reversalModifiedDate;
	private ArrayList amountTypeList;
	private ArrayList validityTypeList;
	private ArrayList bonusBundleList;
	private Integer locationIndex;
	private String setStatus;
	private ArrayList tempAccList;
	private String cardGroupSubServiceName;
	private String serviceTypedesc;
	private String setTypeName;
	
	
	@Override
	public String toString() {
		final StringBuilder sbd= new StringBuilder();
		sbd.append("LoadC2SCardGroupListResponseVO [reversalModifiedDateAsString=");
		sbd.append(reversalModifiedDateAsString);
		sbd.append(", reversalModifiedDate=");
		sbd.append(reversalModifiedDate);
		sbd.append(", amountTypeList=");
		sbd.append(amountTypeList);
		sbd.append(", validityTypeList=");
		sbd.append(validityTypeList);
		sbd.append(", bonusBundleList=");
		sbd.append(bonusBundleList);
		sbd.append(", locationIndex=");
		sbd.append(locationIndex);
		sbd.append(", setStatus=");
		sbd.append(setStatus);
		sbd.append(", tempAccList=");
		sbd.append(tempAccList);
		sbd.append(", cardGroupSubServiceName=");
		sbd.append(cardGroupSubServiceName);
		sbd.append(", serviceTypedesc=");
		sbd.append(serviceTypedesc);
		sbd.append(", setTypeName=");
		sbd.append(setTypeName);
		sbd.append( "]");
		
		
		return sbd.toString();
		
		
		
		
		
		
		
		
	}
	
	

}
