package com.web.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.util.OperatorUtilI;

public interface C2STransferWebQry {
	
	public PreparedStatement loadC2STransferVOListQry(Connection con, String networkCode, Date fromDate, Date toDate, ArrayList userList, String receiverMsisdn, String transferID, String serviceType, String senderCat,
			 ListValueVO user,OperatorUtilI operatorUtilI) throws BTSLBaseException, SQLException;
	
	public String loadC2SReconciliationList();

	public PreparedStatement getReversalTransactionsQry(String msisdn,Connection con, String senderMsisdn,String txID,Date date,String time) throws SQLException;

	public String loadC2SReconciliationQry();
	
}
