package com.btsl.voms.vomsprocesses.businesslogic;

public interface CummulativeInventoryAmtReportQry {
	
	String getVoucherSummInfoLocQry(int noOfdays);
	
	String getVoucherArchiveInfoLocQry();
	
	String getVoucherCountQry();
	
	String getVoucherSummaryInfoLocQry();
	
	String getBatchListForLocQry();

}
