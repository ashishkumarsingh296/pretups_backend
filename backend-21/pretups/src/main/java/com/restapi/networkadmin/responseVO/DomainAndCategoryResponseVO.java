package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.btsl.common.BaseResponse;

@Component
public class DomainAndCategoryResponseVO  extends BaseResponse{
	private ArrayList domainList;
	private ArrayList categoryList;
	private ArrayList geoType;
	private ArrayList geoDomain;
	private String geoTypeCode;
	private String geoTypeDesc;
	private ArrayList cellGroupList;
	private ArrayList serviceGroupList;
	
	
	public ArrayList getGeoDomain() {
		return geoDomain;
	}

	public void setGeoDomain(ArrayList geoDomain) {
		this.geoDomain = geoDomain;
	}

	public String getGeoTypeCode() {
		return geoTypeCode;
	}

	public void setGeoTypeCode(String geoTypeCode) {
		this.geoTypeCode = geoTypeCode;
	}

	public ArrayList getGeoType() {
		return geoType;
	}

	public void setGeoType(ArrayList geoType) {
		this.geoType = geoType;
	}

	public ArrayList getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}

	public ArrayList getDomainList() {
		return domainList;
	}

	public void setDomainList(ArrayList domainList) {
		this.domainList = domainList;
	}
	

	public String getGeoTypeDesc() {
		return geoTypeDesc;
	}

	public void setGeoTypeDesc(String geoTypeDesc) {
		this.geoTypeDesc = geoTypeDesc;
	}

	public ArrayList getCellGroupList() {
		return cellGroupList;
	}

	public void setCellGroupList(ArrayList cellGroupList) {
		this.cellGroupList = cellGroupList;
	}

	public ArrayList getServiceGroupList() {
		return serviceGroupList;
	}

	public void setServiceGroupList(ArrayList serviceGroupList) {
		this.serviceGroupList = serviceGroupList;
	}

	@Override
	public String toString() {
		return "DomainAndCategoryResponseVO [domainList=" + domainList + ", categoryList=" + categoryList + ", geoType="
				+ geoType + ", geoDomain=" + geoDomain + ", geoTypeCode=" + geoTypeCode + ", geoTypeDesc=" + geoTypeDesc
				+ ", cellGroupList=" + cellGroupList + ", serviceGroupList=" + serviceGroupList + "]";
	}
	
}
