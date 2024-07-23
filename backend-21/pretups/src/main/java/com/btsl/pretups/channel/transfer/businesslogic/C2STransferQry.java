package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.user.businesslogic.UserPhoneVO;

public interface C2STransferQry {
	
	public PreparedStatement loadC2STransferVOListQry(Connection pCon,Date p_fromDate, Date p_toDate,String p_senderMsisdn,String isSenderPrimary,String p_receiverMsisdn, String p_transferID, String p_networkCode, String p_serviceType,UserPhoneVO phoneVo ) throws SQLException;
	public String updateReconcilationStatusQry( C2STransferVO p_c2sTransferVO);
	public PreparedStatement loadC2STransferItemsVOList_oldQry(Connection p_con, String p_transferID)throws SQLException;
	public PreparedStatement loadC2STransferItemsVOListQry(Connection p_con, String p_transferID)throws SQLException, ParseException;
	public String loadLastTransfersStatusVOForC2SWithExtRefNumQry();
	public PreparedStatement loadC2STransferVOListQry(Connection p_con, String p_networkCode, Date p_fromDate, Date p_toDate, ArrayList userList, String p_receiverMsisdn, String p_transferID, String p_serviceType, String senderCat,ListValueVO user ) throws SQLException;
	public PreparedStatement loadC2SReconciliationListQry(Connection p_con, String p_networkCode, Date p_fromDate, Date p_toDate, String p_serviceType ) throws SQLException;
	//public String getChanneltransAmtDatewiseQry(Date p_fromDate, Date p_toDate,String p_amount);
	//public PreparedStatement loadLastXCustTransfersQry(Connection p_con,String receiverMsisdn, String p_user_id, int p_noLastTxn, ArrayList transfersList)throws SQLException;
	
	//public PreparedStatement getReversalTransactionsQry(String msisdn,Connection con, String senderMsisdn,String txID,Date date,String time) throws SQLException;
	public PreparedStatement loadOldTxnIDForReversalQry(Connection con,C2STransferVO c2sTransferVO, RequestVO requestVO, String serviceType,Date date,String time) throws SQLException, ParseException;
	public String insertTPSDetailsQry(Map<Date,Integer> pTpsMap) ;
	
	 
}
