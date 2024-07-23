package com.txn.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;


public interface ChannelUserTxnQry {
	Log LOG = LogFactory.getLog(ChannelUserTxnQry.class.getName());
	public String loadChannelUserDetailsForTransferIfReqExtgwQry(String extCode);
	public PreparedStatement loadOtherUserBalanceVOQry(Connection con, String userCode, ChannelUserVO channelUserVO) throws SQLException;
	public String loadUserChannelInTransferListQry(ChannelUserVO channelUserVO);
	public String loadUserChannelOutTransferListQry(ChannelUserVO channelUserVO);
	public PreparedStatement loadUserSubscriberOutTransferListQry(Connection con,OperatorUtilI operatorUtilI,ChannelUserVO channelUserVO,java.util.Date p_date) throws SQLException, BTSLBaseException;
	public PreparedStatement loadERPChnlUserDetailsByExtCodeQry(Connection con,String extCode) throws SQLException;
	public PreparedStatement validateParentAndOwnerQry(Connection con,String userGeography,ChannelUserVO parentVO) throws SQLException ;
	public PreparedStatement loadUsersListForExtApiQry(Connection con, String loginId, String parentMsisdn, String ownerMsisdn, String statusUsed, String status)throws SQLException;
	public String loadChannelUserOutChildTransferListQry(ChannelUserVO pChannelUserVO);
}
