package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class PromoDepDropdownlistRespVO extends BaseResponse {
	
	private ArrayList geographicalDomainType;
	private ArrayList geographyListAll;
	private ArrayList gradeListByCategory;
	private ArrayList cellGroupList;
	private ArrayList serviceClassList;
	private ArrayList subscriberStatusValueList;
	private ArrayList cardGroupList;
	private ArrayList serviceProviderGroupList;
	private ArrayList subServiceList;
	private int noOfRows;
	
	
 public	PromoDepDropdownlistRespVO(){
	}
	
	public ArrayList getGeographicalDomainType() {
		return geographicalDomainType;
	}
	public void setGeographicalDomainType(ArrayList geographicalDomainType) {
		this.geographicalDomainType = geographicalDomainType;
	}
	public ArrayList getGeographyListAll() {
		return geographyListAll;
	}
	public void setGeographyListAll(ArrayList geographyListAll) {
		this.geographyListAll = geographyListAll;
	}
	public ArrayList getGradeListByCategory() {
		return gradeListByCategory;
	}
	public void setGradeListByCategory(ArrayList gradeListByCategory) {
		this.gradeListByCategory = gradeListByCategory;
	}

	public ArrayList getCellGroupList() {
		return cellGroupList;
	}

	public void setCellGroupList(ArrayList cellGroupList) {
		this.cellGroupList = cellGroupList;
	}

	public ArrayList getServiceClassList() {
		return serviceClassList;
	}

	public void setServiceClassList(ArrayList serviceClassList) {
		this.serviceClassList = serviceClassList;
	}

	public ArrayList getSubscriberStatusValueList() {
		return subscriberStatusValueList;
	}

	public void setSubscriberStatusValueList(ArrayList subscriberStatusValueList) {
		this.subscriberStatusValueList = subscriberStatusValueList;
	}

	public ArrayList getCardGroupList() {
		return cardGroupList;
	}

	public void setCardGroupList(ArrayList cardGroupList) {
		this.cardGroupList = cardGroupList;
	}

	public ArrayList getServiceProviderGroupList() {
		return serviceProviderGroupList;
	}

	public void setServiceProviderGroupList(ArrayList serviceProviderGroupList) {
		this.serviceProviderGroupList = serviceProviderGroupList;
	}

	public ArrayList getSubServiceList() {
		return subServiceList;
	}

	public void setSubServiceList(ArrayList subServiceList) {
		this.subServiceList = subServiceList;
	}

	public int getNoOfRows() {
		return noOfRows;
	}

	public void setNoOfRows(int noOfRows) {
		this.noOfRows = noOfRows;
	}
	

}
