package com.web.pretups.transfer.businesslogic;

public interface TransferWebQry {
	public String loadTransferRuleListQry();
	public String loadP2PReconciliationListQry(String pnetworkCodeType);
	public String loadUserListQry();
	public String addPromotionalTransferRuleFileQry();
	public String loadTransferRuleListQry1(String status1, String gatewayCode, String domain, String category, String grade);
}
