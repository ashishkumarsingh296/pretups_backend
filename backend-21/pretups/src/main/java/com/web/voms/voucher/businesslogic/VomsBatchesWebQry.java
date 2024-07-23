package com.web.voms.voucher.businesslogic;

import com.btsl.user.businesslogic.UserVO;

public interface VomsBatchesWebQry {
	
	
	
	public String loadBatchListWithBatchNoNewQry();
	public String loadBatchListNewQry();
	public String loadBatchListOnDaysNewQry();
	public String loadBatchListOnStatusQry(String p_productid);
	public String loadBatchListOnStatusNewQry(String p_productid);
	public String getVomsPrinterBatchQry(String p_batchType,UserVO userVO);
	public String updateVomsPrintBatchstatusQry();
	public String getBatchInfoForUserInputsQry();
	public String getBatchInfoForUserInputsSelectQry();
	public String loadBatchListForMsisdnQry();
	public String getBatchInfoForSelectUserInputsQry();
	public String getBatchInfoForSelectUserInputsBatchBuffQry();
	public String getVomsPrinterBatchForUserQry(String p_batchType,UserVO userVO);
	String getVomsPrinterBatchByBatchIDQry(String p_batchType, UserVO userVO);
}
