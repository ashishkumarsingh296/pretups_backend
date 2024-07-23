package com.restapi.networkadmin.cardgroup.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class LoadC2SCardGroupResponseVO extends BaseResponse{
	
	private String viewSlabCopy;
	private Integer viewCopy;
	private ArrayList cardGroupSubServiceList;
	private String cardGroupSubServiceID;
	private String cardGroupSubServiceName;
	private ArrayList serviceTypeList;
	private String serviceTypeId;
	private String serviceTypedesc;
	private ArrayList setTypeList;
	private String setType;
	private String setTypeName;
	
	

	
	@Override
	public String toString() {
		final StringBuilder sbd = new StringBuilder();
		sbd.append("LoadC2SCardGroupResponseVO [viewSlabCopy=");
		sbd.append(viewSlabCopy);
		sbd.append(", viewCopy=");
		sbd.append(viewCopy);
		sbd.append(", cardGroupSubServiceList=");
		sbd.append(cardGroupSubServiceList);
		sbd.append(", cardGroupSubServiceID=");
		sbd.append(cardGroupSubServiceID);
		sbd.append(", cardGroupSubServiceName=");
		sbd.append(cardGroupSubServiceName);
		sbd.append(", serviceTypeList=");
		sbd.append(serviceTypeList);
		sbd.append(", serviceTypeId=");
		sbd.append(serviceTypeId);
		sbd.append(", serviceTypedesc=");
		sbd.append(serviceTypedesc);
		sbd.append(", setTypeList=");
		sbd.append(setTypeList);
		sbd.append(", setType=");
		sbd.append(setType);
		sbd.append(", setTypeName=");
		sbd.append(setTypeName);
		sbd.append("]");
		
		return sbd.toString();
	}
	
	
	
}
