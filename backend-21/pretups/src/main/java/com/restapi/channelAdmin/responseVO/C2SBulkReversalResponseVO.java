package com.restapi.channelAdmin.responseVO;

import java.util.List;

import com.btsl.common.ListValueVO;
import com.restapi.user.service.FileDownloadResponse;
import com.web.pretups.channel.transfer.web.C2SReversalModel;

public class C2SBulkReversalResponseVO extends FileDownloadResponse{
	
	
	String procStatus;
	Boolean errorFlag;
	Integer totalRecords;
	Integer successRecords;
	Integer rejectedRecords;
	List<C2SReversalModel> c2sreversalList;
	List<ListValueVO> fileErrorList;
	
	
	
	public List<ListValueVO> getFileErrorList() {
		return fileErrorList;
	}

	public void setFileErrorList(List<ListValueVO> fileErrorList) {
		this.fileErrorList = fileErrorList;
	}

	public Boolean getErrorFlag() {
		return errorFlag;
	}

	public void setErrorFlag(Boolean errorFlag) {
		this.errorFlag = errorFlag;
	}

	public Integer getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	
	public String getProcStatus() {
		return procStatus;
	}

	public void setProcStatus(String procStatus) {
		this.procStatus = procStatus;
	}

	public List<C2SReversalModel> getC2sreversalList() {
		return c2sreversalList;
	}

	public void setC2sreversalList(List<C2SReversalModel> c2sreversalList) {
		this.c2sreversalList = c2sreversalList;
	}
	
	
	

	@Override
	public String toString() {
		return "C2SBulkReversalResponseVO [procStatus=" + procStatus + ", fileErrorList=" + fileErrorList
				+ ", errorFlag=" + errorFlag + ", totalRecords=" + totalRecords + ", successRecords=" + successRecords + "]";
	}

	public Integer getSuccessRecords() {
		return successRecords;
	}

	public void setSuccessRecords(Integer successRecords) {
		this.successRecords = successRecords;
	}

	public Integer getRejectedRecords() {
		return rejectedRecords;
	}

	public void setRejectedRecords(Integer rejectedRecords) {
		this.rejectedRecords = rejectedRecords;
	}

}
