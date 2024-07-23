package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;

public class UserTransferEnquiryDAO {
	
	private static final Log LOG = LogFactory.getLog(UserTransferEnquiryDAO.class.getName());
	
	 /**
     * Method loadServiceWiseTransferCounts.
     * This method load the list of the UserTransferEnquiryVO object for the C2S Transfers.
     *  
     * @param con Connection
     * @param senderMsisdn String
     * @param date Date
     * @return ArrayList<UserTransferEnquiryVO>
     * @throws BTSLBaseException
     */
	public ArrayList<UserTransferEnquiryVO> loadServiceWiseTransferCounts(Connection con, String senderMsisdn , Date date) throws BTSLBaseException {
		//local_index_implemented
		final String methodName = "UserTransferEnquiryDAO[loadServiceWiseTransferCounts()]";
		if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_senderMSISDN=" + senderMsisdn + " p_date:" + date );
        }
		PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        UserTransferEnquiryVO userTransferEnquiryVO = null;
        final ArrayList<UserTransferEnquiryVO> userTransferEnquiryVOList = new ArrayList<>();
        StringBuilder strBuff = null;
                
        try {
        	//query for fetching trnasfer counts
        	strBuff = new StringBuilder("select service_type, COUNT(TRANSFER_ID) AS TOTAL_COUNT, COUNT(case when TRANSFER_STATUS = ? then 1 else null end) AS SUCCESS_COUNT , ");
        	strBuff.append("COUNT(case when TRANSFER_STATUS = ? then 1 else null end) AS AMBIGUOUS_COUNT , ");
        	strBuff.append("COUNT(case when TRANSFER_STATUS = ? then 1 else null end) AS UNDER_PROCESS_COUNT , ");
        	strBuff.append("COUNT(case when TRANSFER_STATUS = ? then 1 else null end) AS FAIL_COUNT ");
        	strBuff.append("from C2s_transfers where TRANSFER_DATE =? AND SENDER_MSISDN=? group by service_type order by lower(service_type)");
        	
        	String transferEnquiryQuery = strBuff.toString();
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "transferEnquiryQuery =" + transferEnquiryQuery);
            }
        	pstmtSelect = con.prepareStatement(transferEnquiryQuery);
        	
        	 int i = 1;
             pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_SUCCESS );
             pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS );
             pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS );
             pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_FAIL );
             pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(date) );
             pstmtSelect.setString(i++, senderMsisdn );
             rs = pstmtSelect.executeQuery();
             
             while (rs.next()) {
            	 userTransferEnquiryVO = new UserTransferEnquiryVO();
            	 userTransferEnquiryVO.setUserMsisdn(senderMsisdn);
            	 userTransferEnquiryVO.setServiceType(rs.getString("service_type"));
            	 userTransferEnquiryVO.setTotalCount(rs.getInt("TOTAL_COUNT"));
            	 userTransferEnquiryVO.setSuccessCount(rs.getInt("SUCCESS_COUNT"));
            	 userTransferEnquiryVO.setAmbiguousCount(rs.getInt("AMBIGUOUS_COUNT"));
            	 userTransferEnquiryVO.setUnderProcessCount(rs.getInt("UNDER_PROCESS_COUNT"));
            	 userTransferEnquiryVO.setFailCount(rs.getInt("FAIL_COUNT"));
            	 
            	 userTransferEnquiryVOList.add(userTransferEnquiryVO);
             }
        	
        	
        } catch (SQLException sqe) {
        	if (LOG.isDebugEnabled()) 
        		LOG.debug(methodName, sqe);
    	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,methodName,"","","","SQL Exception:"+sqe.getMessage());
    	    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        	
        } finally {
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	OracleUtil.closeQuietly(con);
        	
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userTransferEnquiryVOList.size()=" + userTransferEnquiryVOList.size());
            }
        }
        return userTransferEnquiryVOList;
	}
	
	/**
     * Method loadUserTransferBalancesCount.
     * This method load the list of the UserBalancesVO object for the User balance.
     *  
     * @param con Connection
     * @param userID String
     * @return ArrayList<UserBalancesVO>
     * @throws BTSLBaseException
     */
	public ArrayList<UserBalancesVO> loadUserTransferBalancesCount(Connection con, String userID) throws BTSLBaseException {
		final String methodName = "UserTransferEnquiryDAO[loadUserTransferBalancesCount()]";
		if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_userID=" + userID );
        }
		PreparedStatement pstmtSelect = null;
		PreparedStatement pstmtOpeningSelect = null;
		PreparedStatement pstmtTotalSelect = null;
		PreparedStatement pstmtChildrenSelect = null;
        ResultSet rs = null;
        ResultSet rsOpening = null;
        ResultSet rsTotal = null;
        ResultSet rsChildren = null;
        UserBalancesVO userBalancesVO = null;
        final ArrayList<UserBalancesVO> userBalanceList = new ArrayList<>();
        StringBuilder strBuff = null;
        
        try {
        	//query for fetching user balance
        	strBuff = new StringBuilder("SELECT sum(ub.balance) AS CURRENT_BALANCE, ub.product_code FROM user_balances ub WHERE ub.user_id = ? ");
        	strBuff.append("GROUP BY ub.product_code");
        	
        	String currentBalanceQuery = strBuff.toString();
        	
        	//Query for Opening Balance
        	UserTransferEnquiryQry userTransferEnquiryQry = (UserTransferEnquiryQry) ObjectProducer.getObject(QueryConstants.USER_TRANS_ENQ_QRY, QueryConstants.QUERY_PRODUCER);
        	String openingBalanceQuery = userTransferEnquiryQry.loadUserTransferBalancesCountSelectOpeningBalanceQry();
        	
        	
        	
        	String totalBalanceQuery = strBuff.toString();
        	
        	//Query for Total Children Balance
        	strBuff = new StringBuilder("SELECT SUM(ub.balance) AS TOTAL_CHILD_BALANCE, ub.product_code FROM users u, user_balances ub WHERE u.user_id = ub.user_id ");
        	strBuff.append("AND u.network_code = ub.network_code AND u.parent_id = ? AND ub.product_code = ? GROUP BY ub.product_code ");
        	
        	String totalChildrenBalanceQuery = strBuff.toString();
        	
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "currentBalanceQuery =" + currentBalanceQuery);
                LOG.debug(methodName, "openingBalanceQuery =" + openingBalanceQuery);
                LOG.debug(methodName, "totalBalanceQuery =" + totalBalanceQuery);
                LOG.debug(methodName, "totalChildrenBalanceQuery =" + totalChildrenBalanceQuery);
            }
        	pstmtSelect = con.prepareStatement(currentBalanceQuery);
        	
        	pstmtSelect.setString(1, userID);
        	rs = pstmtSelect.executeQuery();
            
            while (rs.next()) {
            	userBalancesVO = new UserBalancesVO();
            	userBalancesVO.setUserID(userID);
            	userBalancesVO.setProductCode(rs.getString("product_code"));
            	userBalancesVO.setBalance(rs.getLong("CURRENT_BALANCE"));
            	
            	pstmtOpeningSelect = con.prepareStatement(openingBalanceQuery);
            	
            	pstmtOpeningSelect.setString(1, userID);
            	pstmtOpeningSelect.setString(2, userBalancesVO.getProductCode());
            	
            	rsOpening = pstmtOpeningSelect.executeQuery();
            	
            	while (rsOpening.next()) {
            		userBalancesVO.setOpeningBalance(rsOpening.getLong("OPENING_BALANCE"));
            	}
            	
            	pstmtTotalSelect = con.prepareStatement(totalBalanceQuery);
            	
            	pstmtTotalSelect.setString(1, userBalancesVO.getProductCode());
            	pstmtTotalSelect.setString(2, userID);
            	
            	rsTotal = pstmtTotalSelect.executeQuery();
            	
            	while (rsTotal.next()) {
            		userBalancesVO.setTotalHierarchyBalance(rsTotal.getLong("TOTAL_BALANCE"));
            	}
            	
            	pstmtChildrenSelect = userTransferEnquiryQry.loadUserTransferBalancesCountTotalHierarchyBalanceQry(con, userID, userBalancesVO.getProductCode());
            	
            	rsChildren = pstmtChildrenSelect.executeQuery();
            	
            	while (rsChildren.next()) {
            		userBalancesVO.setTotalChildrenBalance(rsChildren.getLong("TOTAL_CHILD_BALANCE"));
            	}
            	
            	userBalanceList.add(userBalancesVO);
            }
        }catch (SQLException sqe) {
        	if (LOG.isDebugEnabled()) 
        		LOG.debug(methodName, sqe);
    	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,methodName,"","","","SQL Exception:"+sqe.getMessage());
    	    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        	
        } finally {
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing statement.", e);
            }
        	try{
        		if (pstmtOpeningSelect!= null){
        			pstmtOpeningSelect.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtTotalSelect!= null){
        			pstmtTotalSelect.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtChildrenSelect!= null){
        			pstmtChildrenSelect.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
    		try{
            	if (rsOpening!= null){
            		rsOpening.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }
    		try{
            	if (rsTotal!= null){
            		rsTotal.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }
    		try{
            	if (rsChildren!= null){
            		rsChildren.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }
    		try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
        	OracleUtil.closeQuietly(con);
        	
        	if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting userBalanceList.size()=" + userBalanceList.size());
            }
        }
        
        return userBalanceList;
	}
}
