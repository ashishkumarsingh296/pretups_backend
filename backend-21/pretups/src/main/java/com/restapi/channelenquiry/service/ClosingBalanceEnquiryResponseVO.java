package com.restapi.channelenquiry.service;

import java.util.ArrayList;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.channel.reports.businesslogic.UserClosingBalanceVO;

public class ClosingBalanceEnquiryResponseVO extends BaseResponseMultiple{
	
	private ArrayList<UserClosingBalanceVO> balanceList;
	private ArrayList<ArrayList<String>> modifiedData;
	private ArrayList<String> dateColumnLabels;
	private String fileName;
	private String fileType;
	private String fileAttachment;
	

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileAttachment() {
		return fileAttachment;
	}

	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}

	public ArrayList<String> getDateColumnLabels() {
		return dateColumnLabels;
	}

	public void setDateColumnLabels(ArrayList<String> dateColumnLabels) {
		this.dateColumnLabels = dateColumnLabels;
	}

	public ArrayList<ArrayList<String>> getModifiedData() {
		return modifiedData;
	}
	
	

	public void setModifiedData(ArrayList<ArrayList<String>> modifiedData) {
		this.modifiedData = modifiedData;
	}

	@Override
	public String toString() {
		return "ClosingBalanceEnquiryResponseVO [balanceList=" + balanceList + ", modifiedData=" + modifiedData + "]";
	}

	public ArrayList<UserClosingBalanceVO> getBalanceList() {
		return balanceList;
	}

	public void setBalanceList(ArrayList<UserClosingBalanceVO> balanceList) {
		this.balanceList = balanceList;
	}

}
