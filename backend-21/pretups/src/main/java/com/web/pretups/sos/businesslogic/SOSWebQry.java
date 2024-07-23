package com.web.pretups.sos.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public interface SOSWebQry {
	
	public PreparedStatement loadSOSReconciliationListQry(Connection p_con,Date p_fromDate, Date p_toDate, String p_networkCode) throws SQLException;
	public String updateReconcilationStatusQry();
	public PreparedStatement loadSettlementReconciliationListQry(Connection p_con,Date p_fromDate, Date p_toDate, String p_networkCode) throws SQLException;
	public String updateSettlementReconcilationStatusQry();
	public PreparedStatement loadSOSTransferDetailsListQry(Connection p_con,Date p_fromDate, Date p_toDate, String p_networkCode,String p_msisdn, String p_transid) throws SQLException;
	public String lmbForcedSettlementSelectQry();
	public String lmbForcedSettlementQry();
	public String lmbBlkUploadQry();
	
}

