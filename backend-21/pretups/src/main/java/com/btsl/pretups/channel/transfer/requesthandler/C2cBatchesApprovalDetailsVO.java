package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;

public class C2cBatchesApprovalDetailsVO extends BaseResponseMultiple{
	
	private ArrayList c2cBatchTransferList;
	private ArrayList c2cBatchWithdrawalList;
	
	
	public ArrayList getC2cBatchTransferList() {
		return c2cBatchTransferList;
	}
	public void setC2cBatchTransferList(ArrayList c2cBatchTransferList) {
		this.c2cBatchTransferList = c2cBatchTransferList;
	}
	public ArrayList getC2cBatchWithdrawalList() {
		return c2cBatchWithdrawalList;
	}
	public void setC2cBatchWithdrawalList(ArrayList c2cBatchWithdrawalList) {
		this.c2cBatchWithdrawalList = c2cBatchWithdrawalList;
	}
	
	
	@Override
	public String toString() {
		return "C2cBatchesApprovalDetailsVO [c2cBatchTransferList=" + c2cBatchTransferList + ", c2cBatchWithdrawalList="
				+ c2cBatchWithdrawalList + "]";
	}


	
	

}
