package com.btsl.pretups.iat.enquiry.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.user.businesslogic.UserPhoneVO;

public interface IATTransferQry {
	
	Log LOG = LogFactory.getLog(IATTransferQry.class.getName());
	String QUERY = "Query : ";
	
	PreparedStatement loadIATTransferVOListNewQry(Connection pCon, String pNetworkCode, Date pFromdate, Date p_toDate, String pSenderMsisdn, String pTransferID, String pServiceType, UserPhoneVO phoneVo) throws SQLException;
	PreparedStatement loadIATTransferVOListOldQry(Connection pCon, String pNetworkCode, Date pFromdate, Date p_toDate, String pSenderMsisdn, String pTransferID, String pServiceType, UserPhoneVO phoneVo) throws SQLException;
	PreparedStatement loadIATTransferItemsVOListQry(Connection pCon, String pTransferID) throws SQLException, ParseException;
	PreparedStatement loadIATTransferItemsVOListOldQry(Connection pCon, String pTransferID)throws SQLException;
}
