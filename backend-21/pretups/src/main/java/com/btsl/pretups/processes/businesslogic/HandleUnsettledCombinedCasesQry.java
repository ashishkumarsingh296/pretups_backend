package com.btsl.pretups.processes.businesslogic;

public interface HandleUnsettledCombinedCasesQry {

	public String loadC2STransferVOQuery();
	public String updateReconcilationStatusQuery();
	public String loadP2PReconciliationVOQuery();
	public String loadP2PReconciliationItemsListQuery();
	public String updateP2PReconcilationStatusQuery();
	public String loadChannelUserDetailQuery();
	public String loadUserBalanceQuery();
	public String loadUserProductBalanceQuery();
	public String loadTransferCountsWithLockQuery();
	public String loadC2STransferListQuery();
	public String loadP2PReconciliationList();
	
	
	public String loadSOSReconciliationList();
	public String updateSOSReconcilationStatus();
}
