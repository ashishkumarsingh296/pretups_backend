package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.btsl.common.BaseResponse;
@Component
public class DownloadFileResponseVO extends BaseResponse{
	
	private ArrayList subscriberTypeList;
	private ArrayList subscriberServiceTypeList;
	
	private ArrayList cardGroupIdList;
	private ArrayList serviceTypeList;
	private ArrayList subServiceTypeIdList;
	private String subscriberStatus;
	private ArrayList subscriberStatusList;
	private ArrayList gradeList;
	private ArrayList categoryList;
	private ArrayList geoDomainCodeList;
	private String geoTypeDesc;
	private ArrayList cellGroupList;
	private ArrayList serviceGroupList;
	private Map exelMasterData;
	private String fileName;
	private String fileType;
	private String fileAttachment;
	
	
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileAttachment() {
		return fileAttachment;
	}
	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public ArrayList getSubscriberTypeList() {
		return subscriberTypeList;
	}
	public void setSubscriberTypeList(ArrayList subscriberTypeList) {
		this.subscriberTypeList = subscriberTypeList;
	}
	public ArrayList getSubscriberServiceTypeList() {
		return subscriberServiceTypeList;
	}
	public void setSubscriberServiceTypeList(ArrayList subscriberServiceTypeList) {
		this.subscriberServiceTypeList = subscriberServiceTypeList;
	}
	public ArrayList getCardGroupIdList() {
		return cardGroupIdList;
	}
	public void setCardGroupIdList(ArrayList cardGroupIdList) {
		this.cardGroupIdList = cardGroupIdList;
	}
	public ArrayList getServiceTypeList() {
		return serviceTypeList;
	}
	public void setServiceTypeList(ArrayList serviceTypeList) {
		this.serviceTypeList = serviceTypeList;
	}
	public ArrayList getSubServiceTypeIdList() {
		return subServiceTypeIdList;
	}
	public void setSubServiceTypeIdList(ArrayList subServiceTypeIdList) {
		this.subServiceTypeIdList = subServiceTypeIdList;
	}
	public String getSubscriberStatus() {
		return subscriberStatus;
	}
	public void setSubscriberStatus(String subscriberStatus) {
		this.subscriberStatus = subscriberStatus;
	}
	public ArrayList getSubscriberStatusList() {
		return subscriberStatusList;
	}
	public void setSubscriberStatusList(ArrayList subscriberStatusList) {
		this.subscriberStatusList = subscriberStatusList;
	}
	public ArrayList getGradeList() {
		return gradeList;
	}
	public void setGradeList(ArrayList gradeList) {
		this.gradeList = gradeList;
	}
	public ArrayList getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}
	public ArrayList getGeoDomainCodeList() {
		return geoDomainCodeList;
	}
	public void setGeoDomainCodeList(ArrayList geoDomainCodeList) {
		this.geoDomainCodeList = geoDomainCodeList;
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
	public Map getExelMasterData() {
		return exelMasterData;
	}
	public void setExelMasterData(Map exelMasterData) {
		this.exelMasterData = exelMasterData;
	}
	@Override
	public String toString() {
		return "DownloadFileResponseVO [subscriberTypeList=" + subscriberTypeList + ", subscriberServiceTypeList="
				+ subscriberServiceTypeList + ", cardGroupIdList=" + cardGroupIdList + ", serviceTypeList="
				+ serviceTypeList + ", subServiceTypeIdList=" + subServiceTypeIdList + ", subscriberStatus="
				+ subscriberStatus + ", subscriberStatusList=" + subscriberStatusList + ", gradeList=" + gradeList
				+ ", categoryList=" + categoryList + ", geoDomainCodeList=" + geoDomainCodeList + ", geoTypeDesc="
				+ geoTypeDesc + ", cellGroupList=" + cellGroupList + ", serviceGroupList=" + serviceGroupList
				+ ", exelMasterData=" + exelMasterData + "]";
	}

}
