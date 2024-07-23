package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class TransferRulesListResponseVO extends BaseResponse{
	private ArrayList transferRulesList = null;
	private String domainName = null;
	private String toDomainName = null;
	
	
	public ArrayList getTransferRulesList() {
		return transferRulesList;
	}
	public void setTransferRulesList(ArrayList transferRulesList) {
		this.transferRulesList = transferRulesList;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getToDomainName() {
		return toDomainName;
	}
	public void setToDomainName(String toDomainName) {
		this.toDomainName = toDomainName;
	}
	
	
}
