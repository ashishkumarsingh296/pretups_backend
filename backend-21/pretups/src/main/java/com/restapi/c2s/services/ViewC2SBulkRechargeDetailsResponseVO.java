package com.restapi.c2s.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;

public class ViewC2SBulkRechargeDetailsResponseVO extends BaseResponse {

	
	public ArrayList<ScheduleBatchDetailVO> getMsisdnList() {
		return msisdnList;
	}

	public void setMsisdnList(ArrayList<ScheduleBatchDetailVO> msisdnList) {
		this.msisdnList = msisdnList;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	private String fileName;
	
	private String file;

	private ScheduleBatchMasterVO scheduleBatchMasterVO;
	
	private ArrayList<ScheduleBatchDetailVO> msisdnList;
	 
	private LinkedHashMap<String,ScheduleBatchDetailVO> resultRows;

	public ScheduleBatchMasterVO getScheduleBatchMasterVO() {
		return scheduleBatchMasterVO;
	}

	public void setScheduleBatchMasterVO(ScheduleBatchMasterVO scheduleBatchMasterVO) {
		this.scheduleBatchMasterVO = scheduleBatchMasterVO;
	}

	
	
	 
}
