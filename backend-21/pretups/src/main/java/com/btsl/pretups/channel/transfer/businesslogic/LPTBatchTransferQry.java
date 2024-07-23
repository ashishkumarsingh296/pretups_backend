package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface LPTBatchTransferQry {
	public static final Log LOG = LogFactory.getLog(LPTBatchTransferDAO.class.getClass().getName());
	public PreparedStatement loadBatchLPTMasterDetailsQry(Connection p_con, String p_goeDomain, String p_domain, String p_productCode, String p_batchid, String p_msisdn, Date p_fromDate, Date p_toDate, String p_loginID, String p_type)throws SQLException;
	public String loadBatchDetailsListQry();
}
