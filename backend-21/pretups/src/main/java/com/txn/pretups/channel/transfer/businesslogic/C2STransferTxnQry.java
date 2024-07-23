package com.txn.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.pretups.channel.transfer.businesslogic.AddtnlCommSummryReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersReqDTO;

public interface C2STransferTxnQry {
	
	public String loadLastTransfersStatusVOForC2SWithExtRefNumQry();
	public PreparedStatement loadLastXCustTransfersQry(Connection p_con, String p_user_id, int p_noLastTxn, String receiverMsisdn, Date p_date) throws SQLException;

	public PreparedStatement getChanneltransAmtDatewiseQry(Connection p_con,
			String p_networkCode, Date p_fromDate, Date p_toDate,
			String p_senderMsisdn, String p_receiverMsisdn, String p_amount) throws SQLException;
	PreparedStatement loadLastC2STransfersBySubscriberMSISDNQry(Connection con,String senderMsisdn, String receiverMsisdn, Date fromDate) throws SQLException;
	
	public PreparedStatement getC2SnProdTxnDetails(Connection con,String userId,Date fromDate,Date toDate,String ServiceType,String value) throws  SQLException;
	
	
	public String getC2STransferCommissiondetails(C2STransferCommReqDTO c2sTransferCommReqDTO);
	
	public String getAddtnlCommSummaryDets(AddtnlCommSummryReqDTO addtnlCommSummryReqDTO);
	
	public String getPassbookOthersQuery(PassbookOthersReqDTO PassbookOthersReqDTO);
	
		
	

}
