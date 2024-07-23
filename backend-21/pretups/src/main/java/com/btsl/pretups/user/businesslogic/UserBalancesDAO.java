package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.user.businesslogic.wallet.UserProductWalletMappingVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.LowBalanceAlertVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.user.businesslogic.UserLoanVO;
/**
 */
public class UserBalancesDAO {

    /**
     * Field _log.
     */
    private final Log _log = LogFactory.getLog(this.getClass().getName());
    private UserBalancesQry userBalancesQry = (UserBalancesQry) ObjectProducer.getObject(QueryConstants.USER_BALANCE_QRY, QueryConstants.QUERY_PRODUCER);

    /**
     * Method to load the balance for the user for a particular prodct
     * 
     * @param p_con
     * @param p_requestID
     * @param p_userID
     * @param p_networkID
     * @param p_networkFor
     * @param p_productCode
     * @return long
     * @throws BTSLBaseException
     */
    public long loadUserBalanceForProduct(Connection p_con, String p_requestID, String p_userID, String p_networkID, String p_networkFor, String p_productCode) throws BTSLBaseException {
        final String methodName = "loadUserBalanceForProduct";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,
                            "Entered p_requestID = " + p_requestID + ", p_userID : " + p_userID + ", p_networkID = " + p_networkID + ", p_networkFor = " + p_networkFor + ", p_productCode = " + p_productCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        long balance = 0;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer("SELECT balance  ");
            selectQueryBuff.append("FROM user_balances ");
            selectQueryBuff.append("WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? ");
            /** Birendra: START: 1FEB2015 */
            selectQueryBuff.append("AND balance_type = ?");
            /** Birendra: STOP: 1FEB2015 */
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query : " + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userID);
            pstmtSelect.setString(2, p_productCode);
            pstmtSelect.setString(3, p_networkID);
            pstmtSelect.setString(4, p_networkFor);

            /** Birendra: START: 1FEB2015 */
            pstmtSelect.setString(5, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
            /** Birendra: STOP: 1FEB2015 */

            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                balance = rs.getLong("balance");
                
            }
            
         
            
            return balance;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            // sqle.printStackTrace();
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[loadUserBalanceForProduct]",
                            p_requestID, "", p_networkID, "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadUserBalanceForProduct", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            // e.printStackTrace();
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[loadUserBalanceForProduct]",
                            p_requestID, "", p_networkID, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
            	 _log.error(methodName, "Exception " + e.getMessage());
                 _log.errorTrace(methodName, e);
               
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting balance:" + balance);
            }
        }// end of finally
    }

    /**
     * Method to debit the user balances for a user and a product. Also checks
     * the transfer counts before final debit
     * 
     * @param p_con
     * @param p_userBalancesVO
     * @param p_transferProfileID
     * @param p_productID
     * @param p_isCheckMinBalance
     *            boolean
     * @param p_categoryCode
     * @return int
     * @throws BTSLBaseException
     */
    public int debitUserBalances(Connection p_con, UserBalancesVO p_userBalancesVO, String p_transferProfileID, String p_productID, boolean p_isCheckMinBalance, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "debitUserBalances";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userBalancesVO : " + p_userBalancesVO + ",p_isCheckMinBalance = " + p_isCheckMinBalance + ", p_categoryCode = " + p_categoryCode);
        }

        final String METHOD_NAME = "debitUserBalances";
        int updateCount = 0;
        PreparedStatement pstmt = null;
        long balance = 0;
        String balanceType = null;

        long mainBlanace = 0;
        long bonusBalance = 0;
        boolean bonusExistsForReversal = false;
        long newBalance = 0;
        // added by vikram
        // long thresholdValue=-1;

        ResultSet rs = null;
        // added by nilesh

        TransferProfileProductVO transferProfileProductVO = null;      

        final StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append("UPDATE user_balances SET prev_balance = balance, balance = ?, last_transfer_type = ?, ");
        strBuffUpdate.append("last_transfer_no = ?, last_transfer_on = ? ");
        strBuffUpdate.append("WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? ");
        /** START: Birendra: 01FEB2015 */
        strBuffUpdate.append("AND balance_type = ?");
        /** STOP: Birendra: 01FEB2015 */

        // thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,p_userBalancesVO.getNetworkCode(),
        // p_categoryCode); //threshold value
        String[] strArr = null;
        try {
        	
            final String sqlSelect = userBalancesQry.selectForUserbalancesQry();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect = " + sqlSelect);
            }
            try{
	            pstmt = p_con.prepareStatement(sqlSelect);
	            /** START: Birendra: 01FEB2015 */
	            // pstmt.setString(1, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
	            /** STOP: Birendra: 01FEB2015 */
	            pstmt.setString(1, p_userBalancesVO.getUserID());
	            pstmt.setString(2, p_userBalancesVO.getProductCode());
	            pstmt.setString(3, p_userBalancesVO.getNetworkCode());
	            pstmt.setString(4, p_userBalancesVO.getNetworkFor());
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	                balance = rs.getLong("balance");
	                balanceType = rs.getString("balance_type");
	                if (!balanceType.equalsIgnoreCase(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)))) {
	                    bonusBalance = balance;
	
	                } else {
	                    mainBlanace = balance;
	                }
	            }
	            pstmt.clearParameters();
            }
            finally {
            	if(pstmt!=null)
            	{
                    pstmt.close();	
            	}
            }
            long totalbalance = mainBlanace+bonusBalance;
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue() && p_userBalancesVO.getType().equals(PretupsI.TRANSFER_TYPE_DIFFCR)) {
            	 if(bonusBalance>p_userBalancesVO.getQuantityToBeUpdated()){
                bonusExistsForReversal = true;
                 }
            }
            ChannelUserVO userVO = null;
            if (BTSLUtil.isNullString(p_categoryCode) || BTSLUtil.isNullString(p_transferProfileID)) {
                userVO = new ChannelUserDAO().loadChannelUser(p_con, p_userBalancesVO.getUserID());
                if (BTSLUtil.isNullString(p_categoryCode)) {
                    p_categoryCode = userVO.getCategoryCode();
                }
                if (BTSLUtil.isNullString(p_transferProfileID)) {
                    p_transferProfileID = userVO.getTransferProfileID();
                }
            }

            transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(p_transferProfileID, p_productID);
            if(!p_userBalancesVO.getLRFlag())
            {
            if (p_isCheckMinBalance && (totalbalance - p_userBalancesVO.getQuantityToBeUpdated() < transferProfileProductVO.getMinResidualBalanceAsLong()) ) {
                strArr = new String[] { p_userBalancesVO.getProductShortName(), String.valueOf(PretupsBL.getDisplayAmount(p_userBalancesVO.getQuantityToBeUpdated())), String
                                .valueOf(PretupsBL.getDisplayAmount(balance)), String.valueOf(PretupsBL.getDisplayAmount(transferProfileProductVO
                                .getMinResidualBalanceAsLong())) };
                throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL, 0, strArr, null);
            }
            }

            if (totalbalance - p_userBalancesVO.getQuantityToBeUpdated() < 0 ) {
                strArr = new String[] { p_userBalancesVO.getProductShortName(), String.valueOf(PretupsBL.getDisplayAmount(p_userBalancesVO.getQuantityToBeUpdated())), String
                                .valueOf(PretupsBL.getDisplayAmount(balance)) };
                throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE, 0, strArr, null);
            }
            if (bonusExistsForReversal) {
                newBalance = bonusBalance - p_userBalancesVO.getQuantityToBeUpdated();
            } else {
                newBalance = mainBlanace - p_userBalancesVO.getQuantityToBeUpdated();
            }

            final String updateQuery = strBuffUpdate.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Update query = " + updateQuery);
            }

            pstmt = p_con.prepareStatement(updateQuery);
            if (bonusExistsForReversal) {
                p_userBalancesVO.setPreviousBalance(bonusBalance);
            } else {
                p_userBalancesVO.setPreviousBalance(mainBlanace);
            }
            p_userBalancesVO.setBalance(newBalance);
            pstmt.setLong(1, newBalance);
            pstmt.setString(2, p_userBalancesVO.getLastTransferType());
            pstmt.setString(3, p_userBalancesVO.getLastTransferID());
            pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
            pstmt.setString(5, p_userBalancesVO.getUserID());
            pstmt.setString(6, p_userBalancesVO.getProductCode());
            pstmt.setString(7, p_userBalancesVO.getNetworkCode());
            pstmt.setString(8, p_userBalancesVO.getNetworkFor());
            /** START: Birendra: 01FEB2015 */
            if (bonusExistsForReversal) {
                pstmt.setString(9, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
            } else {
                pstmt.setString(9, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));

            }
            /** STOP: Birendra: 01FEB2015 */
            updateCount = pstmt.executeUpdate();
        	if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)).booleanValue() && p_userBalancesVO.getUserLoanVOList()!=null ) {
				try {
					ArrayList<UserLoanVO> userLoanVOList = new ArrayList<UserLoanVO>();
					for(int index=0;index<p_userBalancesVO.getUserLoanVOList().size();index++){
						userLoanVOList.add(p_userBalancesVO.getUserLoanVOList().get(index));
					}
					new com.btsl.pretups.channel.transfer.businesslogic.UserLoanCreditBL().userLoanCredit(userLoanVOList, p_userBalancesVO.getUserID(), newBalance,p_userBalancesVO.getPreviousBalance(),p_userBalancesVO.getProductCode(),p_userBalancesVO.getProductType());
				} catch (BTSLBaseException ex) {
					_log.errorTrace(methodName, ex);
				}
				
				
			}
            else if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue() && p_userBalancesVO.getChannelSoSVOList() != null)
            {
            	try {
            		ArrayList< ChannelSoSVO> channeluserList = new ArrayList<ChannelSoSVO>();
            		for(int index=0;index<p_userBalancesVO.getChannelSoSVOList().size();index++){
            		channeluserList.add(p_userBalancesVO.getChannelSoSVOList().get(index));
            		}
            		new com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSAlertBL().channelSoSEligibilityAlert(channeluserList, p_userBalancesVO.getUserID(), newBalance, p_userBalancesVO.getPreviousBalance());
            	} catch (BTSLBaseException ex) {
            		_log.errorTrace(methodName, ex);
            	}
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException = " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "SQL Exception: " + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception = " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "Exception: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            // for zero balance counter..
            insertUserThresholdCounters(p_con, p_userBalancesVO, transferProfileProductVO, balance, newBalance, p_categoryCode);

            if (_log.isDebugEnabled()) {
                _log.debug(methodName,
                                "Exiting for Transfer ID = " + p_userBalancesVO.getLastTransferID() + ", User ID = " + p_userBalancesVO.getUserID() + ", New Balance = " + newBalance);
            }
        }// end of finally
        return updateCount;
    }
    
    
    /**
     * Method to debit the user balances for a user and a product. Also checks
     * the transfer counts before final debit
     * 
     * @param p_con
     * @param p_userBalancesVO
     * @param p_transferProfileID
     * @param p_productID
     * @param p_isCheckMinBalance
     *            boolean
     * @param p_categoryCode
     * @return int
     * @throws BTSLBaseException
     */
    public long[] debitUserBalancesModified(Connection p_con, UserBalancesVO p_userBalancesVO, String p_transferProfileID, String p_productID, boolean p_isCheckMinBalance, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "debitUserBalances";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userBalancesVO : " + p_userBalancesVO + ",p_isCheckMinBalance = " + p_isCheckMinBalance + ", p_categoryCode = " + p_categoryCode);
        }

        final String METHOD_NAME = "debitUserBalances";
        int updateCount = 0;
        PreparedStatement pstmt = null;
        long balance = 0;
        String balanceType = null;
        long[] arr = new long[3];
        long mainBlanace = 0;
        long bonusBalance = 0;
        boolean bonusExistsForReversal = false;
        long newBalance = 0;
        // added by vikram
        // long thresholdValue=-1;

        ResultSet rs = null;
        // added by nilesh

        TransferProfileProductVO transferProfileProductVO = null;      

        final StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append("UPDATE user_balances SET prev_balance = balance, balance = ?, last_transfer_type = ?, ");
        strBuffUpdate.append("last_transfer_no = ?, last_transfer_on = ? ");
        strBuffUpdate.append("WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? ");
        /** START: Birendra: 01FEB2015 */
        strBuffUpdate.append("AND balance_type = ?");
        /** STOP: Birendra: 01FEB2015 */

        // thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,p_userBalancesVO.getNetworkCode(),
        // p_categoryCode); //threshold value
        String[] strArr = null;
        try {
        	
            final String sqlSelect = userBalancesQry.selectForUserbalancesQry();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect = " + sqlSelect);
            }
            try{
	            pstmt = p_con.prepareStatement(sqlSelect);
	            /** START: Birendra: 01FEB2015 */
	            // pstmt.setString(1, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
	            /** STOP: Birendra: 01FEB2015 */
	            pstmt.setString(1, p_userBalancesVO.getUserID());
	            pstmt.setString(2, p_userBalancesVO.getProductCode());
	            pstmt.setString(3, p_userBalancesVO.getNetworkCode());
	            pstmt.setString(4, p_userBalancesVO.getNetworkFor());
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	                balance = rs.getLong("balance");
	                balanceType = rs.getString("balance_type");
	                if (!balanceType.equalsIgnoreCase(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)))) {
	                    bonusBalance = balance;
	
	                } else {
	                    mainBlanace = balance;
	                }
	            }
	            pstmt.clearParameters();
            }
            finally {
            	if(pstmt!=null)
            	{
                    pstmt.close();	
            	}
            }
            long totalbalance = mainBlanace+bonusBalance;
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue() && p_userBalancesVO.getType().equals(PretupsI.TRANSFER_TYPE_DIFFCR)) {
            	 if(bonusBalance>p_userBalancesVO.getQuantityToBeUpdated()){
                bonusExistsForReversal = true;
                 }
            }
            ChannelUserVO userVO = null;
            if (BTSLUtil.isNullString(p_categoryCode) || BTSLUtil.isNullString(p_transferProfileID)) {
                userVO = new ChannelUserDAO().loadChannelUser(p_con, p_userBalancesVO.getUserID());
                if (BTSLUtil.isNullString(p_categoryCode)) {
                    p_categoryCode = userVO.getCategoryCode();
                }
                if (BTSLUtil.isNullString(p_transferProfileID)) {
                    p_transferProfileID = userVO.getTransferProfileID();
                }
            }

            transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(p_transferProfileID, p_productID);
            if(!p_userBalancesVO.getLRFlag())
            {
            if (p_isCheckMinBalance && (totalbalance - p_userBalancesVO.getQuantityToBeUpdated() < transferProfileProductVO.getMinResidualBalanceAsLong()) ) {
                strArr = new String[] { p_userBalancesVO.getProductShortName(), String.valueOf(PretupsBL.getDisplayAmount(p_userBalancesVO.getQuantityToBeUpdated())), String
                                .valueOf(PretupsBL.getDisplayAmount(balance)), String.valueOf(PretupsBL.getDisplayAmount(transferProfileProductVO
                                .getMinResidualBalanceAsLong())) };
                throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL, 0, strArr, null);
            }
            }

            if (totalbalance - p_userBalancesVO.getQuantityToBeUpdated() < 0 ) {
                strArr = new String[] { p_userBalancesVO.getProductShortName(), String.valueOf(PretupsBL.getDisplayAmount(p_userBalancesVO.getQuantityToBeUpdated())), String
                                .valueOf(PretupsBL.getDisplayAmount(balance)) };
                throw new BTSLBaseException("ChannelUserBL", methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE, 0, strArr, null);
            }
            if (bonusExistsForReversal) {
                newBalance = bonusBalance - p_userBalancesVO.getQuantityToBeUpdated();
            } else {
                newBalance = mainBlanace - p_userBalancesVO.getQuantityToBeUpdated();
            }

            final String updateQuery = strBuffUpdate.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Update query = " + updateQuery);
            }

            pstmt = p_con.prepareStatement(updateQuery);
            if (bonusExistsForReversal) {
                p_userBalancesVO.setPreviousBalance(bonusBalance);
            } else {
                p_userBalancesVO.setPreviousBalance(mainBlanace);
            }
            p_userBalancesVO.setBalance(newBalance);
            pstmt.setLong(1, newBalance);
            pstmt.setString(2, p_userBalancesVO.getLastTransferType());
            pstmt.setString(3, p_userBalancesVO.getLastTransferID());
            pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
            pstmt.setString(5, p_userBalancesVO.getUserID());
            pstmt.setString(6, p_userBalancesVO.getProductCode());
            pstmt.setString(7, p_userBalancesVO.getNetworkCode());
            pstmt.setString(8, p_userBalancesVO.getNetworkFor());
            /** START: Birendra: 01FEB2015 */
            if (bonusExistsForReversal) {
                pstmt.setString(9, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
            } else {
                pstmt.setString(9, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));

            }
            /** STOP: Birendra: 01FEB2015 */
            updateCount = pstmt.executeUpdate();
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue() && p_userBalancesVO.getChannelSoSVOList() != null)
            {
            	try {
            		ArrayList< ChannelSoSVO> channeluserList = new ArrayList<ChannelSoSVO>();
            		for(int index=0;index<p_userBalancesVO.getChannelSoSVOList().size();index++){
            		channeluserList.add(p_userBalancesVO.getChannelSoSVOList().get(index));
            		}
            		new com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSAlertBL().channelSoSEligibilityAlert(channeluserList, p_userBalancesVO.getUserID(), newBalance, p_userBalancesVO.getPreviousBalance());
            	} catch (BTSLBaseException ex) {
            		_log.errorTrace(methodName, ex);
            	}
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException = " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "SQL Exception: " + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception = " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "Exception: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
        	
        	arr[0] = updateCount;
        	arr[1] = balance;
        	arr[2] = newBalance;
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            // for zero balance counter..
           // insertUserThresholdCounters(p_con, p_userBalancesVO, transferProfileProductVO, balance, newBalance, p_categoryCode);

            if (_log.isDebugEnabled()) {
                _log.debug(methodName,
                                "Exiting for Transfer ID = " + p_userBalancesVO.getLastTransferID() + ", User ID = " + p_userBalancesVO.getUserID() + ", New Balance = " + newBalance);
            }
        }// end of finally
        return arr;
    }
    

    /**
     * Method to Credit back the user balances for a user and a product
     * 
     * @param p_con
     * @param p_userBalancesVO
     * @param p_categoryCode
     * @return int
     * @throws BTSLBaseException
     */
    public int creditUserBalances(Connection p_con, UserBalancesVO p_userBalancesVO, String p_categoryCode) throws BTSLBaseException {
        final String METHOD_NAME = "creditUserBalances";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_userBalancesVO : " + p_userBalancesVO + " , p_categoryCode: " + p_categoryCode);
        }

        int updateCount = 0;
        PreparedStatement pstmt = null;
        long balance = 0;
        String balanceType = null;

        long mainBlanace = 0;
        long bonusBalance = 0;
        boolean bonusExistsForReversal = false;
        long newBalance = 0;

        ResultSet rs = null;
       
        
        final StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
        strBuffUpdate.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? ");
        /** START: Birendra: 01FEB2015 */
        strBuffUpdate.append("AND balance_type = ?");
        
        try {
            final String sqlSelect = userBalancesQry.selectForUserbalancesQry();
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "QUERY sqlSelect = " + sqlSelect);
            }
            try{
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, p_userBalancesVO.getUserID());
            pstmt.setString(2, p_userBalancesVO.getProductCode());
            pstmt.setString(3, p_userBalancesVO.getNetworkCode());
            pstmt.setString(4, p_userBalancesVO.getNetworkFor());
            rs = pstmt.executeQuery();
            

            while (rs.next()) {
                balance = rs.getLong("balance");
                balanceType = rs.getString("balance_type");
                if (!balanceType.equalsIgnoreCase(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)))) {
                    bonusBalance = balance;

                } else {
                    mainBlanace = balance;
                }
            }
            pstmt.clearParameters();
            }
            finally
            {
            	if(pstmt!=null)
            	{
                    pstmt.close();
            	}
            }
            
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue() && p_userBalancesVO.getType().equals(PretupsI.TRANSFER_TYPE_DIFFCR)) {
                bonusExistsForReversal = true;
            }
            if (bonusExistsForReversal) {
                newBalance = bonusBalance + p_userBalancesVO.getQuantityToBeUpdated();
            } else {
                newBalance = mainBlanace + p_userBalancesVO.getQuantityToBeUpdated();
            }


            final String updateQuery = strBuffUpdate.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Update query : " + updateQuery);
            }

            
            
            if (bonusExistsForReversal) {
                p_userBalancesVO.setPreviousBalance(bonusBalance);
            } else {
                p_userBalancesVO.setPreviousBalance(mainBlanace);
            }
            pstmt = p_con.prepareStatement(updateQuery);
            p_userBalancesVO.setPreviousBalance(p_userBalancesVO.getPreviousBalance());
            p_userBalancesVO.setBalance(newBalance);
            pstmt.setLong(1, newBalance);
            pstmt.setString(2, p_userBalancesVO.getLastTransferType());
            pstmt.setString(3, p_userBalancesVO.getLastTransferID());
            pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
            pstmt.setString(5, p_userBalancesVO.getUserID());
            pstmt.setString(6, p_userBalancesVO.getProductCode());
            pstmt.setString(7, p_userBalancesVO.getNetworkCode());
            pstmt.setString(8, p_userBalancesVO.getNetworkFor());
            if (bonusExistsForReversal) {
                pstmt.setString(9, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
            } else {
                pstmt.setString(9, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));

            }
            updateCount = pstmt.executeUpdate();

        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[creditUserBalances]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "creditUserBalances", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error("creditUserBalances", "Exception : " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[creditUserBalances]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
           
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME,
                                "Exiting for Transfer ID : " + p_userBalancesVO.getLastTransferID() + ", User ID = " + p_userBalancesVO.getUserID() + ", New Balance = " + newBalance);
            }
        }// end of finally
        return updateCount;
    }

    /**
     * Method to load the user balances for the various products
     * 
     * @param p_con
     * @param p_userID
     * @param p_networkID
     * @param p_networkFor
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList<UserBalancesVO> loadUserBalanceList(Connection p_con, String p_userID, String p_networkID, String p_networkFor) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserBalanceList";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered  p_userID : " + p_userID + ", p_networkID = " + p_networkID + ", p_networkFor = " + p_networkFor);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList<UserBalancesVO> userBalancesList = null;
        try {
            final StringBuffer selectQueryBuff = new StringBuffer("SELECT UB.product_code,UB.balance, ");
            selectQueryBuff.append(" PROD.product_short_code, PROD.short_name, ");
            /** START:Birendra:28JAN2015 */
            selectQueryBuff.append(" UB.balance_type");
            /** STOP:Birendra:28JAN2015 */
            selectQueryBuff.append(" FROM user_balances UB,products PROD ");
            selectQueryBuff.append(" WHERE UB.user_id = ?  AND UB.network_code = ? AND UB.network_code_for = ? AND UB.product_code=PROD.product_code ");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "select query : " + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userID);
            pstmtSelect.setString(2, p_networkID);
            pstmtSelect.setString(3, p_networkFor);
            rs = pstmtSelect.executeQuery();
            UserBalancesVO balancesVO = null;
            userBalancesList = new ArrayList<UserBalancesVO>();
            while (rs.next()) {
                balancesVO = new UserBalancesVO();
                balancesVO.setProductCode(rs.getString("product_code"));
                balancesVO.setBalance(rs.getLong("balance"));
                balancesVO.setProductShortCode(rs.getString("product_short_code"));
                balancesVO.setProductShortName(rs.getString("short_name"));
                /** START:Birendra:28JAN2015 */
                balancesVO.setWalletCode(rs.getString("balance_type"));
                /** STOP:Birendra:28JAN2015 */
                userBalancesList.add(balancesVO);
            }

        }// end of try
        catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[loadUserBalanceList]", "", "",
                            p_networkID, "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[loadUserBalanceList]", "", "",
                            p_networkID, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting balance:" + userBalancesList.size());
            }
        }// end of finally

        return userBalancesList;
    }

    /**
     * Method loadUserBalances.
     * This method loads the balnaces of the user corresponding to the user id
     * 
     * @param p_con
     *            Connection
     * @param p_userId
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList<C2sBalanceQueryVO> loadUserBalances(Connection p_con, String p_userId) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserBalances", "Entered p_userId=" + p_userId);
        }

        final String METHOD_NAME = "loadUserBalances";
        final ArrayList<C2sBalanceQueryVO> userList = new ArrayList<C2sBalanceQueryVO>();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT ub.balance,ub.prev_balance,p.product_short_code,p.product_name,u.msisdn,u.user_name,u.address1,p.product_code,p.unit_value ");
            selectQuery.append("FROM users u,user_balances ub,products p WHERE ub.product_code=p.product_code ");
            selectQuery.append("AND u.user_id=ub.user_id AND ub.user_id=? AND u.status<>'N' AND u.status<>'C' ");
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserBalances", "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_userId);
            rs = pstmtSelect.executeQuery();
            C2sBalanceQueryVO balanceVO = null;
            while (rs.next()) {
                balanceVO = new C2sBalanceQueryVO();
                balanceVO.setBalance(rs.getLong("balance"));
                balanceVO.setProductShortCode(rs.getString("product_short_code"));
                balanceVO.setProductName(rs.getString("product_name"));
                balanceVO.setUserName(rs.getString("user_name"));
                balanceVO.setAddress(rs.getString("address1"));
                balanceVO.setMsisdn(rs.getString("msisdn"));
                // added by nilesh
                balanceVO.setProductCode(rs.getString("product_code"));
                balanceVO.setUnitValue(rs.getString("unit_value"));
                userList.add(balanceVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadUserBalances", "SQLException:" + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[loadUserBalances]", "", "", "",
                            "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserBalances", "error.general.sql.processing",sqe);
        } catch (Exception e) {
            _log.error("loadUserBalances", "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[loadUserBalances]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadUserBalances", "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserBalances", "Exiting:list size=" + userList.size());
            }
        }
        return userList;
    }

    /**
     * Method updateUserDailyBalances.
     * Method to update the user balances
     * 
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_executionDate
     *            Date
     * @param p_userBalancesVO
     *            UserBalancesVO
     * @return int
     * @throws BTSLBaseException
     */

    public int updateUserDailyBalances(Connection p_con, Date p_executionDate, UserBalancesVO p_userBalancesVO) throws BTSLBaseException {
    	final String METHOD_NAME = "updateUserDailyBalances";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_executionDate=" + p_executionDate + ", p_userBalancesVO = " + p_userBalancesVO);
        }
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdate = null;
        ResultSet rs = null;
        int count = 1;
        String selectStrBuff = userBalancesQry.updateUserDailyBalancesQry();
        final StringBuffer updateStrBuff = new StringBuffer();
        updateStrBuff.append("UPDATE user_balances SET daily_balance_updated_on = ? ");
        updateStrBuff.append("WHERE user_id = ? ");

        final StringBuffer insertStrBuff = new StringBuffer();
        insertStrBuff.append("INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
        insertStrBuff.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
        insertStrBuff.append("last_transfer_no, last_transfer_on, created_on,creation_type ");
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
            insertStrBuff.append(" ,balance_type ");
        }
        insertStrBuff.append(")");
        insertStrBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,? ");
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
            insertStrBuff.append(" ,? ");
        }
        insertStrBuff.append(")");

        if (_log.isDebugEnabled()) {
            _log.debug("updateUserDailyBalances ", "Select Query=" + selectStrBuff);
            _log.debug("updateUserDailyBalances ", "Insert Query=" + insertStrBuff);
            _log.debug("updateUserDailyBalances ", "Update Query=" + updateStrBuff);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectStrBuff);
            pstmtUpdate = p_con.prepareStatement(updateStrBuff.toString());
            pstmtInsert = p_con.prepareStatement(insertStrBuff.toString());

            Date dailyBalanceUpdatedOn = null;

            int dayDifference = 0;

            // select the record form the userBalances table.
            pstmtSelect.setString(1, p_userBalancesVO.getUserID());
            pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_executionDate));
            rs = pstmtSelect.executeQuery();
            pstmtSelect.clearParameters();

            while (rs.next()) {
                dailyBalanceUpdatedOn = rs.getDate("daily_balance_updated_on");
                // if record exist check updated on date with current date
                // day differences to maintain the record of previous days.
                //Handling of 00 hours Transactions by Diwakar
                dayDifference = BTSLUtil.getDifferenceInUtilDates2(dailyBalanceUpdatedOn, p_executionDate);
                if (dayDifference > 0) {
                    // if dates are not equal get the day differencts and
                    // execute insert qurery no of times of the
                    if (_log.isDebugEnabled()) {
                        _log.debug(METHOD_NAME, "Till now daily Stock is not updated on " + p_executionDate + ", day differences = " + dayDifference);
                    }

                    for (int k = 0; k < dayDifference; k++) {
                        pstmtInsert.setDate(1, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn, k)));
                        pstmtInsert.setString(2, rs.getString("user_id"));
                        pstmtInsert.setString(3, rs.getString("network_code"));

                        pstmtInsert.setString(4, rs.getString("network_code_for"));
                        pstmtInsert.setString(5, rs.getString("product_code"));
                        pstmtInsert.setLong(6, rs.getLong("balance"));
                        pstmtInsert.setLong(7, rs.getLong("prev_balance"));
                        pstmtInsert.setString(8, p_userBalancesVO.getLastTransferType());

                        pstmtInsert.setString(9, p_userBalancesVO.getLastTransferID());
                        pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                        pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_executionDate));
                        pstmtInsert.setString(12, PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                            pstmtInsert.setString(13, rs.getString("balance_type"));
                        }
                        count = pstmtInsert.executeUpdate();
						// added to make code compatible with insertion in partitioned table in postgres
						count = BTSLUtil.getInsertCount(count); 
                        if (count <= 0) {
                            pstmtInsert.close();
                            pstmtSelect.close();
                            pstmtUpdate.close();
                            rs.close();
                            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
                        }
                        pstmtInsert.clearParameters();
                    }
                    pstmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_executionDate));
                    pstmtUpdate.setString(2, p_userBalancesVO.getUserID());
                    count = pstmtUpdate.executeUpdate();
                    if (count <= 0) {
                        pstmtInsert.close();
                        pstmtSelect.close();
                        pstmtUpdate.close();
                        rs.close();
                        throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
                    }
                    pstmtUpdate.clearParameters();
                } else {
                	if (_log.isDebugEnabled()) {
                        _log.debug(METHOD_NAME, "dayDifference="+dayDifference +", p_executionDate="+ p_executionDate + ", dailyBalanceUpdatedOn=" + dailyBalanceUpdatedOn);
                    }
                }  
            }// end of while loop
        } catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserBalancesDAO[updateUserDailyBalances]", "", "", "",
                            "BTSLBaseException:" + be.getMessage());
            throw be;
        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[updateUserDailyBalances]", "", "", "",
                            "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[updateUserDailyBalances]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting count = " + count);
            }
        }
        return count;
    }
    
    
    /**
     * Method updateUserDailyBalances.
     * Method to update the user balances
     * 
     * @author Yogesh.dixit
     * @param p_con
     *            Connection
     * @param p_executionDate
     *            Date
     * @param p_userBalancesVO
     *            UserBalancesVO
     * @return int
     * @throws BTSLBaseException
     */

    public int updateUserDailyBalances(Connection p_con, Date p_executionDate, UserBalancesVO p_userBalancesVO,ArrayList<UserBalancesVO> balanceList) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateUserDailyBalances", "Entered p_executionDate=" + p_executionDate + ", p_userBalancesVO = " + p_userBalancesVO);
        }
        final String METHOD_NAME = "updateUserDailyBalances";
        //PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdate = null;
        ResultSet rs = null;
        int count = 1;
      //  String selectStrBuff = userBalancesQry.updateUserDailyBalancesQry();
        final StringBuffer updateStrBuff = new StringBuffer();
        updateStrBuff.append("UPDATE user_balances SET daily_balance_updated_on = ? ");
        updateStrBuff.append("WHERE user_id = ? ");

        final StringBuffer insertStrBuff = new StringBuffer();
        insertStrBuff.append("INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
        insertStrBuff.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
        insertStrBuff.append("last_transfer_no, last_transfer_on, created_on,creation_type ");
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
            insertStrBuff.append(" ,balance_type ");
        }
        insertStrBuff.append(")");
        insertStrBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,? ");
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
            insertStrBuff.append(" ,? ");
        }
        insertStrBuff.append(")");

        if (_log.isDebugEnabled()) {
           // _log.debug("updateUserDailyBalances ", "Select Query=" + selectStrBuff);
            _log.debug("updateUserDailyBalances ", "Insert Query=" + insertStrBuff);
            _log.debug("updateUserDailyBalances ", "Update Query=" + updateStrBuff);
        }
        try {
           // pstmtSelect = p_con.prepareStatement(selectStrBuff);
            pstmtUpdate = p_con.prepareStatement(updateStrBuff.toString());
            pstmtInsert = p_con.prepareStatement(insertStrBuff.toString());

            Date dailyBalanceUpdatedOn = null;

            int dayDifference = 0;

			/*
			 * // select the record form the userBalances table. pstmtSelect.setString(1,
			 * p_userBalancesVO.getUserID()); pstmtSelect.setDate(2,
			 * BTSLUtil.getSQLDateFromUtilDate(p_executionDate)); rs =
			 * pstmtSelect.executeQuery(); pstmtSelect.clearParameters();
			 */

            for(UserBalancesVO balobj :balanceList){
                dailyBalanceUpdatedOn = balobj.getDailyBalanceUpdatedOn();
                // if record exist check updated on date with current date
                // day differences to maintain the record of previous days.
                dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn, p_executionDate);
                if (dayDifference > 0) {
                    // if dates are not equal get the day differencts and
                    // execute insert qurery no of times of the
                    if (_log.isDebugEnabled()) {
                        _log.debug("updateUserDailyBalances ", "Till now daily Stock is not updated on " + p_executionDate + ", day differences = " + dayDifference);
                    }

                    for (int k = 0; k < dayDifference; k++) {
                        pstmtInsert.setDate(1, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn, k)));
                        pstmtInsert.setString(2,balobj.getUserID());
                        pstmtInsert.setString(3, balobj.getNetworkCode());

                        pstmtInsert.setString(4,balobj.getNetworkFor());
                        pstmtInsert.setString(5, balobj.getProductCode());
                        pstmtInsert.setLong(6, balobj.getBalance());
                        pstmtInsert.setLong(7, balobj.getPreviousBalance());
                        pstmtInsert.setString(8, p_userBalancesVO.getLastTransferType());

                        pstmtInsert.setString(9, p_userBalancesVO.getLastTransferID());
                        pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                        pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_executionDate));
                        pstmtInsert.setString(12, PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                            pstmtInsert.setString(13,balobj.getBalanceType());
                        }
                        count = pstmtInsert.executeUpdate();
						// added to make code compatible with insertion in partitioned table in postgres
						count = BTSLUtil.getInsertCount(count); 
                        if (count <= 0) {
                            pstmtInsert.close();
                           // pstmtSelect.close();
                            pstmtUpdate.close();
                            rs.close();
                            throw new BTSLBaseException(this, "updateUserDailyBalances", "error.general.sql.processing");
                        }
                        pstmtInsert.clearParameters();
                    }
                    pstmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_executionDate));
                    pstmtUpdate.setString(2, p_userBalancesVO.getUserID());
                    count = pstmtUpdate.executeUpdate();
                    if (count <= 0) {
                        pstmtInsert.close();
                        //pstmtSelect.close();
                        pstmtUpdate.close();
                        rs.close();
                        throw new BTSLBaseException(this, "updateUserDailyBalances", "error.general.sql.processing");
                    }
                    pstmtUpdate.clearParameters();
                }
            }// end of while loop
        } catch (BTSLBaseException be) {
            _log.error("updateUserDailyBalances", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserBalancesDAO[updateUserDailyBalances]", "", "", "",
                            "BTSLBaseException:" + be.getMessage());
            throw be;
        } catch (SQLException sqle) {
            _log.error("updateUserDailyBalances", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[updateUserDailyBalances]", "", "", "",
                            "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateUserDailyBalances", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error("updateUserDailyBalances", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[updateUserDailyBalances]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateUserDailyBalances", "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
			/*
			 * try { if (pstmtSelect != null) { pstmtSelect.close(); } } catch (Exception e)
			 * { _log.errorTrace(METHOD_NAME, e); }
			 */
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("updateUserDailyBalances", "Exiting count = " + count);
            }
        }
        return count;
    }
    
    
    
    /**
     * Method getUserDailyBalancesList.
     * Method to update the user balances
     * 
     * @author yogesh.dixit
     * @param p_con
     *            Connection
     * @param p_executionDate
     *            Date
     * @param p_userBalancesVO
     *            UserBalancesVO
     * @return ArrayList<UserBalancesVO>
     * @throws BTSLBaseException
     */
    public ArrayList<UserBalancesVO> getUserDailyBalancesList(Connection p_con, Date p_executionDate, UserBalancesVO p_userBalancesVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("getUserDailyBalancesList", "Entered "  + ", p_userBalancesVO = " + p_userBalancesVO);
        }
        final String METHOD_NAME = "getUserDailyBalancesList";
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String selectStrBuff = userBalancesQry.updateUserDailyBalancesQry();
        if (_log.isDebugEnabled()) {
            _log.debug("updateUserDailyBalances ", "Select Query=" + selectStrBuff);
        }
        ArrayList<UserBalancesVO> ballist = new  ArrayList<UserBalancesVO>();
        try {
            pstmtSelect = p_con.prepareStatement(selectStrBuff);
            pstmtSelect.setString(1, p_userBalancesVO.getUserID());
            pstmtSelect.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_executionDate));
            rs = pstmtSelect.executeQuery();
            pstmtSelect.clearParameters();
            while (rs.next()) {
                UserBalancesVO bal = (UserBalancesVO) p_userBalancesVO.clone();
                bal.setDailyBalanceUpdatedOn(rs.getDate("daily_balance_updated_on"));
                bal.setProductCode(rs.getString("product_code"));
                bal.setBalance(rs.getLong("balance"));
                bal.setPreviousBalance(rs.getLong("prev_balance"));
                bal.setBalanceType(rs.getString("balance_type"));
                ballist.add(bal);
            }
            
        }  catch (SQLException sqle) {
            _log.error("updateUserDailyBalances", "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[updateUserDailyBalances]", "", "", "",
                            "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateUserDailyBalances", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error("updateUserDailyBalances", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[updateUserDailyBalances]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateUserDailyBalances", "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("getUserDailyBalancesList", "Exiting   " + ballist.size());
            }
        }
        return ballist;
    }
    

    /**
     * This method debits the balance from one or more wallets.
     * 
     * @author birendra.mishra
     * @param p_con
     * @param p_userBalancesVO
     * @param p_transferProfileID
     * @param p_productID
     * @param p_isCheckMinBalance
     * @param p_categoryCode
     * @param p_pdaWalletList
     * @return
     * @throws BTSLBaseException
     */
    public int[] debitUserBalancesFromWallets(Connection p_con, UserBalancesVO p_userBalancesVO, String p_transferProfileID, String p_productID, boolean p_isCheckMinBalance, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "debitUserBalancesFromWallets";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,
                            "Entered p_userBalancesVO : " + p_userBalancesVO + ", p_transferProfileID = " + p_transferProfileID + ", p_productID = " + p_productID + ", p_isCheckMinBalance = " + p_isCheckMinBalance + ", p_categoryCode = " + p_categoryCode);
        }
        int loopCounter = 0;
        long newBalance = 0;
        final ResultSet rs = null;
        String walletCode = "";
        long walletBalance = 0;
        String[] strArr = null;
        long amountRequired = 0;
        int[] updateCount = null;
        long thresholdValue = -1;
        ChannelUserVO userVO = null;
        PreparedStatement pstmtUpdateUserBalance = null;
        long minResidualBalanceLong = 0;
        long balanceToBeDebitedFromThisAc = 0;
        UserProductWalletMappingVO walletVO = null;
        boolean isSufficientAmtFoundAcrsWallets = false;
        PreparedStatement psmtInsertUserThreshold = null;
        TransferProfileProductVO transferProfileProductVO = null;
        final List<UserProductWalletMappingVO> pdaWalletList = p_userBalancesVO.getPdaWalletList();

        /**
         * Creating the Query to Update the balance in user_balance table to
         * debit the recahrge amount from one or more wallets
         */
        final StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
        strBuffUpdate.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? ");
        strBuffUpdate.append(" AND balance_type = ?");

        /** Query to insert the record in user_threshold_counter. */
        final StringBuffer strBuffThresholdInsert = new StringBuffer();
        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
        strBuffThresholdInsert.append(" (user_id, transfer_id, entry_date, entry_date_time, network_code, product_code, ");
        strBuffThresholdInsert.append(" type, transaction_type, record_type, category_code, previous_balance, current_balance, threshold_value, threshold_type, remark ) ");
        strBuffThresholdInsert.append(" VALUES ");
        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

        /**
         * To Get all the balance from <strong>USER_BALANCE<strong> table for
         * all existing wallets and whose PDA flag is active
         */
        long totalBalanceAcrossPDAWallets = loadUserBalanceForProductAndWalletsRS(p_con, p_userBalancesVO, p_userBalancesVO.getPdaWalletList());
        long totalPreviousBalanceAcrossPDAWallets = p_userBalancesVO.getPreviousBalance();

        /**
         * To get <strong> categoryCode </strong> and <strong> transferProfileId
         * </strong> if already not available.
         */
        if (p_isCheckMinBalance) {
            if (BTSLUtil.isNullString(p_categoryCode) || BTSLUtil.isNullString(p_transferProfileID)) {
                userVO = new ChannelUserDAO().loadChannelUser(p_con, p_userBalancesVO.getUserID());
                p_categoryCode = userVO.getCategoryCode();
                p_transferProfileID = userVO.getTransferProfileID();
            }
            transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(p_transferProfileID, p_productID);
            minResidualBalanceLong = transferProfileProductVO.getMinResidualBalanceAsLong();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "minResidualBalance = " + minResidualBalanceLong,
                                ", p_categoryCode = " + p_categoryCode + ", p_transferProfileID = " + p_transferProfileID);
            }
        }

        if (p_isCheckMinBalance && totalBalanceAcrossPDAWallets - p_userBalancesVO.getQuantityToBeUpdated() < transferProfileProductVO.getMinResidualBalanceAsLong()) {
            strArr = new String[] { p_userBalancesVO.getProductShortName(), String.valueOf(PretupsBL.getDisplayAmount(p_userBalancesVO.getQuantityToBeUpdated())), String
                            .valueOf(PretupsBL.getDisplayAmount(totalBalanceAcrossPDAWallets)), String.valueOf(PretupsBL.getDisplayAmount(transferProfileProductVO
                            .getMinResidualBalanceAsLong())) };
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL, 0, strArr, null);
        }

        if (totalBalanceAcrossPDAWallets - p_userBalancesVO.getQuantityToBeUpdated() < 0) {
            strArr = new String[] { p_userBalancesVO.getProductShortName(), String.valueOf(PretupsBL.getDisplayAmount(p_userBalancesVO.getQuantityToBeUpdated())), String
                            .valueOf(PretupsBL.getDisplayAmount(totalBalanceAcrossPDAWallets)) };
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE, 0, strArr, null);
        }

        amountRequired = p_userBalancesVO.getQuantityToBeUpdated();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "amountRequired = " + amountRequired + " before Entering the Loop");
        }

        /**
         * To Iterate over All PDA wallets as per their priority in table
         * USER_PRODUCT_WALLET_MAPPING and to Debit each wallet with the
         * required amount until the total recharge amount is not acheived.
         */
		 for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletList.iterator(); iterator.hasNext();) {
        	 walletVO = iterator.next();
        	 walletVO.setDebitBalance(0);
        }
        for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletList.iterator(); iterator.hasNext();) {
            walletVO = iterator.next();
            walletCode = walletVO.getAccountCode();
            walletBalance = walletVO.getBalance();
			// walletVO.setDebitBalance(0);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName,
                                "loopCounter = " + (++loopCounter) + ", Balance Type = " + walletCode + ", balance = " + walletBalance + ", Total Balance = " + totalBalanceAcrossPDAWallets);
                _log.debug(methodName, "p_isCheckMinBalance = " + p_isCheckMinBalance);
            }

            if (walletBalance > 0) {
                if (walletBalance < amountRequired) {
                    walletVO.setPreviousBalance(walletBalance);
                    balanceToBeDebitedFromThisAc = walletBalance;
                    amountRequired = amountRequired - walletBalance;
                    newBalance = walletBalance - balanceToBeDebitedFromThisAc;
                    walletVO.setBalance(newBalance);
                    walletVO.setDebitBalance(balanceToBeDebitedFromThisAc);
                    isSufficientAmtFoundAcrsWallets = false;
                    continue;
                } else if (walletBalance >= amountRequired) {
                    walletVO.setPreviousBalance(walletBalance);
                    balanceToBeDebitedFromThisAc = amountRequired;
                    amountRequired = 0;
                    newBalance = walletBalance - balanceToBeDebitedFromThisAc;
                    walletVO.setBalance(newBalance);
                    walletVO.setDebitBalance(balanceToBeDebitedFromThisAc);
                    isSufficientAmtFoundAcrsWallets = true;
                    break;
                }
            }

        }

        /**
         * When the required amount to recharge couldn't be fulfilled across all
         * the PDA wallets.
         */
        if (!isSufficientAmtFoundAcrsWallets) {
            strArr = new String[] { p_userBalancesVO.getProductShortName(), String.valueOf(PretupsBL.getDisplayAmount(p_userBalancesVO.getQuantityToBeUpdated())), String
                            .valueOf(PretupsBL.getDisplayAmount(totalBalanceAcrossPDAWallets)), String.valueOf(PretupsBL.getDisplayAmount(transferProfileProductVO
                            .getMinResidualBalanceAsLong())) };
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL, 0, strArr, null);
        }

        try {
            final String updateQuery = strBuffUpdate.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Update query = " + updateQuery);
            }

            totalBalanceAcrossPDAWallets = 0L;
            totalPreviousBalanceAcrossPDAWallets = 0L;
            pstmtUpdateUserBalance = p_con.prepareStatement(updateQuery);

            /**
             * Creating the batch update to update each wallets which
             * participated in the Debit, with their new balance.
             */
            for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletList.iterator(); iterator.hasNext();) {

                final UserProductWalletMappingVO walletMappingVO = iterator.next();
                final long balance = walletMappingVO.getBalance();
                final long prevBalance = walletMappingVO.getPreviousBalance();
                final long debitedBalance = walletMappingVO.getDebitBalance();
		        
                final String accountCode = walletMappingVO.getAccountCode();

                totalBalanceAcrossPDAWallets = totalBalanceAcrossPDAWallets + balance;
                totalPreviousBalanceAcrossPDAWallets = totalPreviousBalanceAcrossPDAWallets + prevBalance;

                if (debitedBalance > 0) {

                    pstmtUpdateUserBalance.setLong(1, balance);
                    pstmtUpdateUserBalance.setString(2, p_userBalancesVO.getLastTransferType());
                    pstmtUpdateUserBalance.setString(3, p_userBalancesVO.getLastTransferID());
                    pstmtUpdateUserBalance.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                    pstmtUpdateUserBalance.setString(5, p_userBalancesVO.getUserID());
                    pstmtUpdateUserBalance.setString(6, p_userBalancesVO.getProductCode());
                    pstmtUpdateUserBalance.setString(7, p_userBalancesVO.getNetworkCode());
                    pstmtUpdateUserBalance.setString(8, p_userBalancesVO.getNetworkFor());
                    pstmtUpdateUserBalance.setString(9, accountCode);
                    pstmtUpdateUserBalance.addBatch();
                }
            }

            updateCount = pstmtUpdateUserBalance.executeBatch();
            /**
             * Calculating the new Total Balance and Total Prev_Balance across
             * all PDA wallets.
             */
            p_userBalancesVO.setBalance(totalBalanceAcrossPDAWallets);
            p_userBalancesVO.setPreviousBalance(totalPreviousBalanceAcrossPDAWallets);
            if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)).booleanValue() && p_userBalancesVO.getUserLoanVOList()!=null ) {
				try {
					ArrayList<UserLoanVO> userLoanVOList = new ArrayList<UserLoanVO>();
					for(int index=0;index<p_userBalancesVO.getUserLoanVOList().size();index++){
						userLoanVOList.add(p_userBalancesVO.getUserLoanVOList().get(index));
					}
					new com.btsl.pretups.channel.transfer.businesslogic.UserLoanCreditBL().userLoanCredit(userLoanVOList, p_userBalancesVO.getUserID(), p_userBalancesVO.getBalance(),p_userBalancesVO.getPreviousBalance(),p_userBalancesVO.getProductCode(),p_userBalancesVO.getProductType());
				} catch (BTSLBaseException ex) {
					_log.errorTrace(methodName, ex);
				}
				
				
			}
            else if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue() && p_userBalancesVO.getChannelSoSVOList() != null)
            {
            	try {
            		ArrayList< ChannelSoSVO> channeluserList = new ArrayList<ChannelSoSVO>();
            		for(int index=0;index<p_userBalancesVO.getChannelSoSVOList().size();index++){
            		channeluserList.add(p_userBalancesVO.getChannelSoSVOList().get(index));
            		}
            		new com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSAlertBL().channelSoSEligibilityAlert(channeluserList, p_userBalancesVO.getUserID(), p_userBalancesVO.getBalance(), p_userBalancesVO.getPreviousBalance());
            	} catch (BTSLBaseException ex) {
            		_log.errorTrace(methodName, ex);
            	}
            }

        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "SQL Exception: " + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "Exception: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateUserBalance != null) {
                    pstmtUpdateUserBalance.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                int m = 0;
                final String insertUserThreshold = strBuffThresholdInsert.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "QUERY insertUserThreshold = " + insertUserThreshold);
                }
                psmtInsertUserThreshold = p_con.prepareStatement(insertUserThreshold);
                thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
                String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
                final String remark = null;
                walletCode = "";

                if (totalBalanceAcrossPDAWallets <= transferProfileProductVO.getAltBalanceLong() && totalBalanceAcrossPDAWallets >= transferProfileProductVO
                                .getMinResidualBalanceAsLong()) {
                    thresholdValue = transferProfileProductVO.getAltBalanceLong();
                    threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
                }

                if ((totalPreviousBalanceAcrossPDAWallets >= thresholdValue && totalBalanceAcrossPDAWallets <= thresholdValue) || (totalPreviousBalanceAcrossPDAWallets <= thresholdValue && totalBalanceAcrossPDAWallets <= thresholdValue)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName,
                                        "Entry in threshold counter = " + thresholdValue + ", prvbal = " + totalPreviousBalanceAcrossPDAWallets + "nbal = " + totalBalanceAcrossPDAWallets);
                    }
                    psmtInsertUserThreshold.clearParameters();
                    m = 0;
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getUserID());
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getLastTransferID());
                    psmtInsertUserThreshold.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                    psmtInsertUserThreshold.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getNetworkCode());
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getProductCode());
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getType());
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getLastTransferType());
                    psmtInsertUserThreshold.setString(++m, PretupsI.BELOW_THRESHOLD_TYPE);
                    psmtInsertUserThreshold.setString(++m, p_categoryCode);
                    psmtInsertUserThreshold.setLong(++m, totalPreviousBalanceAcrossPDAWallets);
                    psmtInsertUserThreshold.setLong(++m, totalBalanceAcrossPDAWallets);
                    psmtInsertUserThreshold.setLong(++m, thresholdValue);
                    psmtInsertUserThreshold.setString(++m, threshold_type);
                    psmtInsertUserThreshold.setString(++m, remark);
                    psmtInsertUserThreshold.executeUpdate();
                }
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue() && p_userBalancesVO.getChannelSoSVOList() != null)
                {
                	try {
                		ArrayList< ChannelSoSVO> channeluserList = new ArrayList<ChannelSoSVO>();
                		for(int index=0;index<p_userBalancesVO.getChannelSoSVOList().size();index++){
                		channeluserList.add(p_userBalancesVO.getChannelSoSVOList().get(index));
                		}
                		new com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSAlertBL().channelSoSEligibilityAlert(channeluserList, p_userBalancesVO.getUserID(), newBalance, p_userBalancesVO.getPreviousBalance());
                	} catch (BTSLBaseException ex) {
                		_log.errorTrace(methodName, ex);
                	}
                }
                
            } catch (SQLException sqle) {
                _log.error(methodName, "SQLException " + sqle.getMessage());
                _log.errorTrace(methodName, sqle);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[debitUserBalances]",
                                p_userBalancesVO.getLastTransferID(), "", p_userBalancesVO.getNetworkCode(),
                                "Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
            }
            try {
                if (psmtInsertUserThreshold != null) {
                    psmtInsertUserThreshold.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName,
                                "Exiting for Transfer ID : " + p_userBalancesVO.getLastTransferID() + ", User ID = " + p_userBalancesVO.getUserID() + ", New Balance = " + totalBalanceAcrossPDAWallets);
            }
        }
        return updateCount;
    }
    
    
    /**
     * This method debits the balance from one or more wallets.
     * 
     * @author birendra.mishra
     * @param p_con
     * @param p_userBalancesVO
     * @param p_transferProfileID
     * @param p_productID
     * @param p_isCheckMinBalance
     * @param p_categoryCode
     * @param p_pdaWalletList
     * @return
     * @throws BTSLBaseException
     */
    public int[] debitUserBalancesFromWalletsModified(Connection p_con, UserBalancesVO p_userBalancesVO, String p_transferProfileID, String p_productID, boolean p_isCheckMinBalance, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "debitUserBalancesFromWalletsModified";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,
                            "Entered p_userBalancesVO : " + p_userBalancesVO + ", p_transferProfileID = " + p_transferProfileID + ", p_productID = " + p_productID + ", p_isCheckMinBalance = " + p_isCheckMinBalance + ", p_categoryCode = " + p_categoryCode);
        }
        int loopCounter = 0;
        long newBalance = 0;
        final ResultSet rs = null;
        String walletCode = "";
        long walletBalance = 0;
        String[] strArr = null;
        long amountRequired = 0;
        int[] updateCount = null;
        long thresholdValue = -1;
        ChannelUserVO userVO = null;
        PreparedStatement pstmtUpdateUserBalance = null;
        long minResidualBalanceLong = 0;
        long balanceToBeDebitedFromThisAc = 0;
        UserProductWalletMappingVO walletVO = null;
        boolean isSufficientAmtFoundAcrsWallets = false;
        PreparedStatement psmtInsertUserThreshold = null;
        TransferProfileProductVO transferProfileProductVO = null;
        final List<UserProductWalletMappingVO> pdaWalletList = p_userBalancesVO.getPdaWalletList();

        /**
         * Creating the Query to Update the balance in user_balance table to
         * debit the recahrge amount from one or more wallets
         */
        final StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
        strBuffUpdate.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? ");
        strBuffUpdate.append(" AND balance_type = ?");

        /** Query to insert the record in user_threshold_counter. */
        final StringBuffer strBuffThresholdInsert = new StringBuffer();
        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
        strBuffThresholdInsert.append(" (user_id, transfer_id, entry_date, entry_date_time, network_code, product_code, ");
        strBuffThresholdInsert.append(" type, transaction_type, record_type, category_code, previous_balance, current_balance, threshold_value, threshold_type, remark ) ");
        strBuffThresholdInsert.append(" VALUES ");
        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        /**
         * To get <strong> categoryCode </strong> and <strong> transferProfileId
         * </strong> if already not available.
         */
        if (p_isCheckMinBalance) {
            if (BTSLUtil.isNullString(p_categoryCode) || BTSLUtil.isNullString(p_transferProfileID)) {
                userVO = new ChannelUserDAO().loadChannelUser(p_con, p_userBalancesVO.getUserID());
                p_categoryCode = userVO.getCategoryCode();
                p_transferProfileID = userVO.getTransferProfileID();
            }
            transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(p_transferProfileID, p_productID);
            minResidualBalanceLong = transferProfileProductVO.getMinResidualBalanceAsLong();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "minResidualBalance = " + minResidualBalanceLong,
                                ", p_categoryCode = " + p_categoryCode + ", p_transferProfileID = " + p_transferProfileID);
            }
        }
        
        /**
         * To Get all the balance from <strong>USER_BALANCE<strong> table for
         * all existing wallets and whose PDA flag is active
         */
        long totalBalanceAcrossPDAWallets = loadUserBalanceForProductAndWalletsRS(p_con, p_userBalancesVO, p_userBalancesVO.getPdaWalletList());
        long totalPreviousBalanceAcrossPDAWallets = p_userBalancesVO.getPreviousBalance();

        if (p_isCheckMinBalance && totalBalanceAcrossPDAWallets - p_userBalancesVO.getQuantityToBeUpdated() < transferProfileProductVO.getMinResidualBalanceAsLong()) {
            strArr = new String[] { p_userBalancesVO.getProductShortName(), String.valueOf(PretupsBL.getDisplayAmount(p_userBalancesVO.getQuantityToBeUpdated())), String
                            .valueOf(PretupsBL.getDisplayAmount(totalBalanceAcrossPDAWallets)), String.valueOf(PretupsBL.getDisplayAmount(transferProfileProductVO
                            .getMinResidualBalanceAsLong())) };
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL, 0, strArr, null);
        }

        if (totalBalanceAcrossPDAWallets - p_userBalancesVO.getQuantityToBeUpdated() < 0) {
            strArr = new String[] { p_userBalancesVO.getProductShortName(), String.valueOf(PretupsBL.getDisplayAmount(p_userBalancesVO.getQuantityToBeUpdated())), String
                            .valueOf(PretupsBL.getDisplayAmount(totalBalanceAcrossPDAWallets)) };
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_INSUFF_BALANCE, 0, strArr, null);
        }

        amountRequired = p_userBalancesVO.getQuantityToBeUpdated();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "amountRequired = " + amountRequired + " before Entering the Loop");
        }

        /**
         * To Iterate over All PDA wallets as per their priority in table
         * USER_PRODUCT_WALLET_MAPPING and to Debit each wallet with the
         * required amount until the total recharge amount is not acheived.
         */
		 for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletList.iterator(); iterator.hasNext();) {
        	 walletVO = iterator.next();
        	 walletVO.setDebitBalance(0);
        }
        for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletList.iterator(); iterator.hasNext();) {
            walletVO = iterator.next();
            walletCode = walletVO.getAccountCode();
            walletBalance = walletVO.getBalance();
			// walletVO.setDebitBalance(0);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName,
                                "loopCounter = " + (++loopCounter) + ", Balance Type = " + walletCode + ", balance = " + walletBalance + ", Total Balance = " + totalBalanceAcrossPDAWallets);
                _log.debug(methodName, "p_isCheckMinBalance = " + p_isCheckMinBalance);
            }

            if (walletBalance > 0) {
                if (walletBalance < amountRequired) {
                    walletVO.setPreviousBalance(walletBalance);
                    balanceToBeDebitedFromThisAc = walletBalance;
                    amountRequired = amountRequired - walletBalance;
                    newBalance = walletBalance - balanceToBeDebitedFromThisAc;
                    walletVO.setBalance(newBalance);
                    walletVO.setDebitBalance(balanceToBeDebitedFromThisAc);
                    isSufficientAmtFoundAcrsWallets = false;
                    continue;
                } else if (walletBalance >= amountRequired) {
                    walletVO.setPreviousBalance(walletBalance);
                    balanceToBeDebitedFromThisAc = amountRequired;
                    amountRequired = 0;
                    newBalance = walletBalance - balanceToBeDebitedFromThisAc;
                    walletVO.setBalance(newBalance);
                    walletVO.setDebitBalance(balanceToBeDebitedFromThisAc);
                    isSufficientAmtFoundAcrsWallets = true;
                    break;
                }
            }

        }

        /**
         * When the required amount to recharge couldn't be fulfilled across all
         * the PDA wallets.
         */
        if (!isSufficientAmtFoundAcrsWallets) {
            strArr = new String[] { p_userBalancesVO.getProductShortName(), String.valueOf(PretupsBL.getDisplayAmount(p_userBalancesVO.getQuantityToBeUpdated())), String
                            .valueOf(PretupsBL.getDisplayAmount(totalBalanceAcrossPDAWallets)), String.valueOf(PretupsBL.getDisplayAmount(transferProfileProductVO
                            .getMinResidualBalanceAsLong())) };
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHNL_ERROR_SNDR_BAL_LESS_RESIDUAL, 0, strArr, null);
        }

        try {
            final String updateQuery = strBuffUpdate.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Update query = " + updateQuery);
            }

            totalBalanceAcrossPDAWallets = 0L;
            totalPreviousBalanceAcrossPDAWallets = 0L;
            pstmtUpdateUserBalance = p_con.prepareStatement(updateQuery);

            /**
             * Creating the batch update to update each wallets which
             * participated in the Debit, with their new balance.
             */
            for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletList.iterator(); iterator.hasNext();) {

                final UserProductWalletMappingVO walletMappingVO = iterator.next();
                final long balance = walletMappingVO.getBalance();
                final long prevBalance = walletMappingVO.getPreviousBalance();
                final long debitedBalance = walletMappingVO.getDebitBalance();
		        
                final String accountCode = walletMappingVO.getAccountCode();

                totalBalanceAcrossPDAWallets = totalBalanceAcrossPDAWallets + balance;
                totalPreviousBalanceAcrossPDAWallets = totalPreviousBalanceAcrossPDAWallets + prevBalance;

                if (debitedBalance > 0) {

                    pstmtUpdateUserBalance.setLong(1, balance);
                    pstmtUpdateUserBalance.setString(2, p_userBalancesVO.getLastTransferType());
                    pstmtUpdateUserBalance.setString(3, p_userBalancesVO.getLastTransferID());
                    pstmtUpdateUserBalance.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                    pstmtUpdateUserBalance.setString(5, p_userBalancesVO.getUserID());
                    pstmtUpdateUserBalance.setString(6, p_userBalancesVO.getProductCode());
                    pstmtUpdateUserBalance.setString(7, p_userBalancesVO.getNetworkCode());
                    pstmtUpdateUserBalance.setString(8, p_userBalancesVO.getNetworkFor());
                    pstmtUpdateUserBalance.setString(9, accountCode);
                    pstmtUpdateUserBalance.addBatch();
                }
            }

            updateCount = pstmtUpdateUserBalance.executeBatch();
            /**
             * Calculating the new Total Balance and Total Prev_Balance across
             * all PDA wallets.
             */
            p_userBalancesVO.setBalance(totalBalanceAcrossPDAWallets);
            p_userBalancesVO.setPreviousBalance(totalPreviousBalanceAcrossPDAWallets);
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue() && p_userBalancesVO.getChannelSoSVOList() != null)
            {
            	try {
            		ArrayList< ChannelSoSVO> channeluserList = new ArrayList<ChannelSoSVO>();
            		for(int index=0;index<p_userBalancesVO.getChannelSoSVOList().size();index++){
            		channeluserList.add(p_userBalancesVO.getChannelSoSVOList().get(index));
            		}
            		new com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSAlertBL().channelSoSEligibilityAlert(channeluserList, p_userBalancesVO.getUserID(), p_userBalancesVO.getBalance(), p_userBalancesVO.getPreviousBalance());
            	} catch (BTSLBaseException ex) {
            		_log.errorTrace(methodName, ex);
            	}
            }
            // Commiting Changes Related to balances 
            if (updateCount != null || updateCount.length > 0) {
            	p_con.commit();
            }

        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "SQL Exception: " + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "Exception: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateUserBalance != null) {
                    pstmtUpdateUserBalance.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                int m = 0;
                final String insertUserThreshold = strBuffThresholdInsert.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "QUERY insertUserThreshold = " + insertUserThreshold);
                }
                psmtInsertUserThreshold = p_con.prepareStatement(insertUserThreshold);
                thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
                String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
                final String remark = null;
                walletCode = "";

                if (totalBalanceAcrossPDAWallets <= transferProfileProductVO.getAltBalanceLong() && totalBalanceAcrossPDAWallets >= transferProfileProductVO
                                .getMinResidualBalanceAsLong()) {
                    thresholdValue = transferProfileProductVO.getAltBalanceLong();
                    threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
                }

                if ((totalPreviousBalanceAcrossPDAWallets >= thresholdValue && totalBalanceAcrossPDAWallets <= thresholdValue) || (totalPreviousBalanceAcrossPDAWallets <= thresholdValue && totalBalanceAcrossPDAWallets <= thresholdValue)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName,
                                        "Entry in threshold counter = " + thresholdValue + ", prvbal = " + totalPreviousBalanceAcrossPDAWallets + "nbal = " + totalBalanceAcrossPDAWallets);
                    }
                    psmtInsertUserThreshold.clearParameters();
                    m = 0;
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getUserID());
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getLastTransferID());
                    psmtInsertUserThreshold.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                    psmtInsertUserThreshold.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getNetworkCode());
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getProductCode());
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getType());
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getLastTransferType());
                    psmtInsertUserThreshold.setString(++m, PretupsI.BELOW_THRESHOLD_TYPE);
                    psmtInsertUserThreshold.setString(++m, p_categoryCode);
                    psmtInsertUserThreshold.setLong(++m, totalPreviousBalanceAcrossPDAWallets);
                    psmtInsertUserThreshold.setLong(++m, totalBalanceAcrossPDAWallets);
                    psmtInsertUserThreshold.setLong(++m, thresholdValue);
                    psmtInsertUserThreshold.setString(++m, threshold_type);
                    psmtInsertUserThreshold.setString(++m, remark);
                    psmtInsertUserThreshold.executeUpdate();
                }
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue() && p_userBalancesVO.getChannelSoSVOList() != null)
                {
                	try {
                		ArrayList< ChannelSoSVO> channeluserList = new ArrayList<ChannelSoSVO>();
                		for(int index=0;index<p_userBalancesVO.getChannelSoSVOList().size();index++){
                		channeluserList.add(p_userBalancesVO.getChannelSoSVOList().get(index));
                		}
                		new com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSAlertBL().channelSoSEligibilityAlert(channeluserList, p_userBalancesVO.getUserID(), newBalance, p_userBalancesVO.getPreviousBalance());
                	} catch (BTSLBaseException ex) {
                		_log.errorTrace(methodName, ex);
                	}
                }
                
            } catch (SQLException sqle) {
                _log.error(methodName, "SQLException " + sqle.getMessage());
                _log.errorTrace(methodName, sqle);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[debitUserBalances]",
                                p_userBalancesVO.getLastTransferID(), "", p_userBalancesVO.getNetworkCode(),
                                "Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
            } catch (Exception e) {
                _log.error(methodName, "Exception " + e.getMessage());
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[debitUserBalances]",
                                p_userBalancesVO.getLastTransferID(), "", p_userBalancesVO.getNetworkCode(),
                                "Error while updating user_threshold_counter table Exception Occured:" + e.getMessage());
            }
            try {
                if (psmtInsertUserThreshold != null) {
                    psmtInsertUserThreshold.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName,
                                "Exiting for Transfer ID : " + p_userBalancesVO.getLastTransferID() + ", User ID = " + p_userBalancesVO.getUserID() + ", New Balance = " + totalBalanceAcrossPDAWallets);
            }
        }
        return updateCount;
    }
    
    
    
    
    
    
    

    public int creditUserBalanceForBonusAcc(Connection p_con, UserBalancesVO p_userBalancesVO, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "creditUserBalanceForBonusAcc";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userBalancesVO : " + p_userBalancesVO + ", p_categoryCode : " + p_categoryCode);
        }

        long balance = 0;
        long newBalance = 0;
        ResultSet rs = null;
        int updateCount = 0;
        boolean recordExist = false;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtInsertUserBalance = null;

        final String sqlSelect =  userBalancesQry.creditUserBalanceForBonusAccQry();

        final StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
        strBuffUpdate.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? AND balance_type = ? ");

        final StringBuffer strBuffInsert = new StringBuffer("INSERT INTO user_balances(user_id, network_code,");
        strBuffInsert.append(" network_code_for, product_code, balance, prev_balance, last_transfer_type,");
        strBuffInsert.append(" last_transfer_no, last_transfer_on, balance_type) VALUES(?,?,?,?,?,?,?,?,?,?)");

        // added by nilesh:added two new columns threshold_type and remark

        try {
            try{
            	pstmt = p_con.prepareStatement(sqlSelect);
            	pstmt.setString(1, p_userBalancesVO.getUserID());
            	pstmt.setString(2, p_userBalancesVO.getProductCode());
            	pstmt.setString(3, p_userBalancesVO.getNetworkCode());
            	pstmt.setString(4, p_userBalancesVO.getNetworkFor());
            	pstmt.setString(5, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
            	rs = pstmt.executeQuery();
           
            if (rs.next()) {
                balance = rs.getLong("balance");
                recordExist = true;
            }
            pstmt.clearParameters();
            }
            finally{
            	if(pstmt!=null) {
                	pstmt.close();
            	}
            }

            if (!recordExist) {
                newBalance = p_userBalancesVO.getQuantityToBeUpdated();
                pstmtInsertUserBalance = p_con.prepareStatement(strBuffInsert.toString());
                pstmtInsertUserBalance.setString(1, p_userBalancesVO.getUserID());
                pstmtInsertUserBalance.setString(2, p_userBalancesVO.getNetworkCode());
                pstmtInsertUserBalance.setString(3, p_userBalancesVO.getNetworkFor());
                pstmtInsertUserBalance.setString(4, p_userBalancesVO.getProductCode());
                pstmtInsertUserBalance.setLong(5, newBalance);
                pstmtInsertUserBalance.setLong(6, 0L);
                pstmtInsertUserBalance.setString(7, p_userBalancesVO.getLastTransferType());
                pstmtInsertUserBalance.setString(8, p_userBalancesVO.getLastTransferID());
                pstmtInsertUserBalance.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                /** START: Birendra */
                pstmtInsertUserBalance.setString(10, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
                /** STOP: Birendra */
                updateCount = pstmtInsertUserBalance.executeUpdate();
            } else {

                newBalance = balance + p_userBalancesVO.getQuantityToBeUpdated();
                final String updateQuery = strBuffUpdate.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Update query = " + updateQuery);
                }

                pstmt = p_con.prepareStatement(updateQuery);
                pstmt.setLong(1, newBalance);
                pstmt.setString(2, p_userBalancesVO.getLastTransferType());
                pstmt.setString(3, p_userBalancesVO.getLastTransferID());
                pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                pstmt.setString(5, p_userBalancesVO.getUserID());
                pstmt.setString(6, p_userBalancesVO.getProductCode());
                pstmt.setString(7, p_userBalancesVO.getNetworkCode());
                pstmt.setString(8, p_userBalancesVO.getNetworkFor());
                /** START: Birendra: */
                pstmt.setString(9, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.WALLET_FOR_ADNL_CMSN)));
                /** STOP: Birendra: */
                updateCount = pstmt.executeUpdate();
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserBalance != null) {
                	pstmtInsertUserBalance.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            // for zero balance counter..

            /**
             * To Get all the balance from <strong>USER_BALANCE<strong> table
             * for all existing wallets and whose PDA flag is active
             */
            final long totalBalanceAcrossPDAWallets = p_userBalancesVO.getBalance() +p_userBalancesVO.getQuantityToBeUpdated();
            final long totalPreviousBalanceAcrossPDAWallets = p_userBalancesVO.getPreviousBalance();

            insertThreshHoldCounter(p_con, p_userBalancesVO, p_categoryCode, totalPreviousBalanceAcrossPDAWallets, totalBalanceAcrossPDAWallets);
            p_userBalancesVO.setBalance(totalBalanceAcrossPDAWallets);
            p_userBalancesVO.setPreviousBalance(totalPreviousBalanceAcrossPDAWallets);

            if (_log.isDebugEnabled()) {
                _log.debug(methodName,
                                "Exiting for Transfer ID : " + p_userBalancesVO.getLastTransferID() + ", User ID = " + p_userBalancesVO.getUserID() + ", New Balance = " + newBalance);
            }
        }
        return updateCount;
    }

    /**
     * To Insert record into table user_threshold_counter table based upon the
     * different cases.
     * 
     * @author birendra.mishra
     * @param p_con
     * @param p_userBalancesVO
     * @param p_categoryCode
     * @param p_balance
     * @param p_newBalance
     * @return
     * @throws BTSLBaseException
     */
    private int insertThreshHoldCounter(Connection p_con, UserBalancesVO p_userBalancesVO, String p_categoryCode, long p_balance, long p_newBalance) throws BTSLBaseException {

        final String methodName = "insertThreshHoldCounter";

        final String remark = null;
        long thresholdValue = -1;
        ChannelUserVO userVO = null;
         
        TransferProfileProductVO transferProfileProductVO = null;
        final StringBuffer strBuffThresholdInsert = new StringBuffer();
        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
        strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code,");
        strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
        strBuffThresholdInsert.append(" VALUES ");
        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

        try {
            int m = 0;
            final String insertUserThreshold = strBuffThresholdInsert.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY insertUserThreshold = " + insertUserThreshold);
            }
            try(PreparedStatement psmtInsertUserThreshold = p_con.prepareStatement(insertUserThreshold);)
            {
            String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;

            if (BTSLUtil.isNullString(p_categoryCode) || BTSLUtil.isNullString(p_userBalancesVO.getTransferProfileID())) {
                userVO = new ChannelUserDAO().loadChannelUser(p_con, p_userBalancesVO.getUserID());
                if (BTSLUtil.isNullString(p_categoryCode)) {
                    p_categoryCode = userVO.getCategoryCode();
                }
                if (BTSLUtil.isNullString(p_userBalancesVO.getTransferProfileID())) {
                    p_userBalancesVO.setTransferProfileID(userVO.getTransferProfileID());
                }
            }
            transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(p_userBalancesVO.getTransferProfileID(), p_userBalancesVO.getProductCode());
            thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
            if (p_balance <= transferProfileProductVO.getAltBalanceLong() && p_newBalance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
                thresholdValue = transferProfileProductVO.getAltBalanceLong();
                threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
            }

            if ((p_balance <= thresholdValue && p_newBalance >= thresholdValue) || (p_balance <= thresholdValue && p_newBalance <= thresholdValue)) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Entry in threshold counter = " + thresholdValue + ", prvbal = " + p_balance + ", nbal = " + p_newBalance);
                }

                psmtInsertUserThreshold.clearParameters();
                psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getUserID());
                psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getLastTransferID());
                psmtInsertUserThreshold.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                psmtInsertUserThreshold.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getNetworkCode());
                psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getProductCode());
                psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getType());
                psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getLastTransferType());
                if (p_balance >= thresholdValue) {
                    psmtInsertUserThreshold.setString(++m, PretupsI.ABOVE_THRESHOLD_TYPE);
                } else {
                    psmtInsertUserThreshold.setString(++m, PretupsI.BELOW_THRESHOLD_TYPE);
                }
                psmtInsertUserThreshold.setString(++m, p_categoryCode);
                psmtInsertUserThreshold.setLong(++m, p_balance);
                psmtInsertUserThreshold.setLong(++m, p_newBalance);
                psmtInsertUserThreshold.setLong(++m, thresholdValue);
                psmtInsertUserThreshold.setString(++m, threshold_type);
                psmtInsertUserThreshold.setString(++m, remark);

                psmtInsertUserThreshold.executeUpdate();
            }
        } 
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException = " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "Error while updating user_threshold_counter table SQL Exception : " + sqle
                            .getMessage());
        }

        return 0;
    }

    /**
     * @author birendra.mishra
     *         This method shall query the balance and previous balance of all
     *         such wallets whose PDA flag is active. Only PDA wallets/account
     *         can participate
     *         in the recharge txn.
     * @param p_con
     * @param p_requestID
     * @param p_userID
     * @param p_networkID
     * @param p_networkFor
     * @param p_productCode
     * @param p_c2STransferVO
     * @throws BTSLBaseException
     */
    public void loadUserBalanceForPDAWallets(Connection p_con, String p_requestID, String p_userID, String p_networkID, String p_networkFor, String p_productCode, C2STransferVO p_c2STransferVO) throws BTSLBaseException {
        final String methodName = "loadUserBalanceForPDAWallets";

        if (_log.isDebugEnabled()) {
            _log.debug(methodName,
                            "Entered p_requestID = " + p_requestID + ", p_userID = " + p_userID + ", p_networkID = " + p_networkID + ", p_networkFor = " + p_networkFor + ", p_productCode = " + p_productCode);
        }

        ResultSet rs = null;
        String queryPlaceHolder = "";
        String walletTypeCondition = "";
        PreparedStatement pstmtSelect = null;
        long pdaWalletsTotalBalance = 0;
        long pdaWalletsTotalPrevBalance = 0;
        final List<UserProductWalletMappingVO> pdaWalletList = p_c2STransferVO.getPdaWalletList();

        try {
            /** Will create a string of PlaceHolders */
            if (pdaWalletList.size() == 1) {
                walletTypeCondition = " AND balance_type = ?";
            } else if (pdaWalletList.size() > 1) {

                for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletList.iterator(); iterator.hasNext();) {
                    iterator.next();
                    queryPlaceHolder = queryPlaceHolder + "?" + ",";
                }
                if (queryPlaceHolder.charAt(queryPlaceHolder.length() - 1) == ',') {
                    queryPlaceHolder = queryPlaceHolder.substring(0, queryPlaceHolder.length() - 1);
                }
                walletTypeCondition = "AND balance_type in (" + queryPlaceHolder + ")";
            }

            final StringBuffer selectQueryBuff = new StringBuffer("SELECT balance, balance_type, prev_balance ");
            selectQueryBuff.append("FROM user_balances ");
            selectQueryBuff.append("WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? ");
            selectQueryBuff.append(walletTypeCondition);

            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query : " + selectQuery);
            }

            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userID);
            pstmtSelect.setString(2, p_productCode);
            pstmtSelect.setString(3, p_networkID);
            pstmtSelect.setString(4, p_networkFor);

            for (int i = 0, placeHolderPos = 5; i < pdaWalletList.size(); i++) {
                pstmtSelect.setString(placeHolderPos + i, pdaWalletList.get(i).getAccountCode());
            }
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                final String walletCode = rs.getString("balance_type");
                final long walletBalance = rs.getLong("balance");
                 long walletPreviousBalance = rs.getLong("prev_balance");
                 // This is done because if in case current balance becomes zero then in that case it takes previous balance which was done of last transaction
                if(walletBalance==0)
                	walletPreviousBalance = 0;
                	
                	
                for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletList.iterator(); iterator.hasNext();) {
                    final UserProductWalletMappingVO userProductWalletMappingVO = iterator.next();

                    if (walletCode.equals(userProductWalletMappingVO.getAccountCode())) {
                        userProductWalletMappingVO.setBalance(walletBalance);
                        userProductWalletMappingVO.setPreviousBalance(walletPreviousBalance);
                        break;
                    }
                }
                pdaWalletsTotalBalance = pdaWalletsTotalBalance + walletBalance;
                pdaWalletsTotalPrevBalance = pdaWalletsTotalPrevBalance + walletPreviousBalance;
            }
            p_c2STransferVO.setTotalBalanceAcrossPDAWallets(pdaWalletsTotalBalance);
            p_c2STransferVO.setTotalPreviousBalanceAcrossPDAWallets(pdaWalletsTotalPrevBalance);
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_requestID, "",
                            p_networkID, "SQL Exception : " + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_requestID, "",
                            p_networkID, "Exception : " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting balance : " + pdaWalletsTotalBalance);
            }
        }
    }

    /**
     * This Method Queries the user_balance table for all PDA wallets
     * (balance_type) and populates each UserProductWalletMappingVO with
     * balance & prev_balance.
     * 
     * @author birendra.mishra
     * @param p_con
     * @param p_userBalancesVO
     * @param p_pdaWalletList
     * @return
     * @throws BTSLBaseException
     */
    public long loadUserBalanceForProductAndWalletsRS(Connection p_con, UserBalancesVO p_userBalancesVO, List<UserProductWalletMappingVO> p_walletList) throws BTSLBaseException {
        final String methodName = "loadUserBalanceForProductAndWalletsRS";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered...");
        }

        ResultSet rs = null;
        String walletName = "";
        long walletBalance = 0L;
        String queryPlaceHolder = "";
        long walletPreviousBalance = 0L;
        String walletTypeCondition = "";
        PreparedStatement pstmtSelect = null;
        long totalBalanceAcrossPDAWallets = 0;
        long totalPreviousBalanceAcrossPDAWallets = 0;

        try {
            /** Will create a string of PlaceHolders */
            if (p_walletList.size() == 1) {
                walletTypeCondition = " AND balance_type = ?";
            } else if (p_walletList.size() > 1) {
                for (final Iterator<UserProductWalletMappingVO> iterator = p_walletList.iterator(); iterator.hasNext();) {
                    iterator.next();
                    queryPlaceHolder = queryPlaceHolder + "?" + ",";
                }
                if (queryPlaceHolder.charAt(queryPlaceHolder.length() - 1) == ',') {
                    queryPlaceHolder = queryPlaceHolder.substring(0, queryPlaceHolder.length() - 1);
                }
                walletTypeCondition = "AND balance_type in (" + queryPlaceHolder + ")";
            }
           
            final String selectQuery =  userBalancesQry.loadUserBalanceForProductAndWalletsRSQry(walletTypeCondition);

            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userBalancesVO.getUserID());
            pstmtSelect.setString(2, p_userBalancesVO.getProductCode());
            pstmtSelect.setString(3, p_userBalancesVO.getNetworkCode());
            pstmtSelect.setString(4, p_userBalancesVO.getNetworkFor());

            /**
             * Loop to iterate over the number of wallets to put the walletCodes
             * in the query placeholder (?).
             */
            for (int i = 0, param = 5; i < p_walletList.size(); i++) {
                pstmtSelect.setString(param + i, p_walletList.get(i).getAccountCode());
            }
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                walletName = rs.getString("balance_type");
                walletBalance = rs.getLong("balance");
                walletPreviousBalance = rs.getLong("prev_balance");
             // This is done because if in case current balance becomes zero then in that case it takes previous balance which was done of last transaction
                if(walletBalance==0)
                	walletPreviousBalance=0;

                /**
                 * Loop to iterater over the availabe list of PDA wallets and
                 * check if the current resultset balance_type data matchees
                 * with the walletCode. If yes, populates the
                 * UserProductWalletMappingVO with balance and prev_balance.
                 */
                for (int i = 0; i < p_walletList.size(); i++) {
                    final UserProductWalletMappingVO userProductWalletMappingVO = p_walletList.get(i);
                    if (walletName.equals(userProductWalletMappingVO.getAccountCode())) {
                        userProductWalletMappingVO.setBalance(walletBalance);
                        userProductWalletMappingVO.setPreviousBalance(walletPreviousBalance);
                        break;
                    }
                }
                /**
                 * Calculating the totalBalance and totalPrevBalance across all
                 * wallets which are PDA (Partial Deduction Allowed).
                 */
                totalBalanceAcrossPDAWallets = totalBalanceAcrossPDAWallets + walletBalance;
                totalPreviousBalanceAcrossPDAWallets = totalPreviousBalanceAcrossPDAWallets + walletPreviousBalance;
            }
            p_userBalancesVO.setBalance(totalBalanceAcrossPDAWallets);
            p_userBalancesVO.setPreviousBalance(totalPreviousBalanceAcrossPDAWallets);

            return totalBalanceAcrossPDAWallets;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException = " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting balance : " + totalBalanceAcrossPDAWallets);
            }
        }
    }

    /**
     * This method To insert all wallets information into user_daily_balance
     * which are available in user_balance table.
     * Will also synchronize the balance, prev_balance from table to VO.
     * 
     * @author birendra.mishra
     * @param p_con
     * @param p_executionDate
     * @param p_userBalancesVO
     * @param p_c2STransferVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateUserDailyBalancesForWallets(Connection p_con, Date p_executionDate, UserBalancesVO p_userBalancesVO, C2STransferVO p_c2STransferVO) throws BTSLBaseException {
        final String methodName = "updateUserDailyBalancesForWallets";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_executionDate = " + p_executionDate + ", p_userBalancesVO = " + p_userBalancesVO);
        }

        int count = 1;
        ResultSet rs = null;
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdate = null;
        final StringBuffer selectStrBuff = new StringBuffer();
        List<UserProductWalletMappingVO> walletsForNetAndPrdct = null;

        if (p_c2STransferVO != null) {
            walletsForNetAndPrdct = PretupsBL.getPrtSortWalletsForNetIdAndPrdId(p_c2STransferVO.getNetworkCode(), p_c2STransferVO.getProductCode());
        } else {
            walletsForNetAndPrdct = PretupsBL.getPrtSortWalletsForNetIdAndPrdType(p_userBalancesVO.getNetworkCode(), p_userBalancesVO.getProductType());
        }

        String queryPlaceHolder = "";
        String forUpdateClause = "";
        String walletTypeCondition = "";

        /**
         * If there is only wallet then a simple where clause, else the dynamic
         * where clause
         */
        if (walletsForNetAndPrdct.size() == 1) {
            walletTypeCondition = " AND balance_type = ?";
        } else if (walletsForNetAndPrdct.size() > 1) {
            /**
             * To Create the number of PlaceHolders matching the number of
             * wallets configured for the current network and product.
             */
            for (final Iterator<UserProductWalletMappingVO> iterator = walletsForNetAndPrdct.iterator(); iterator.hasNext();) {
                iterator.next();
                queryPlaceHolder = queryPlaceHolder + "?" + ",";
            }
            /**
             * For Removing the Last (,) character from the Query PlaceHolder
             * String.
             */
            if (queryPlaceHolder.charAt(queryPlaceHolder.length() - 1) == ',') {
                queryPlaceHolder = queryPlaceHolder.substring(0, queryPlaceHolder.length() - 1);
            }

            walletTypeCondition = "AND balance_type in (" + queryPlaceHolder + ")";
        }

        forUpdateClause = userBalancesQry.updateUserDailyBalancesForWalletsSelectForUpdateQry();

        selectStrBuff.append(" SELECT user_id, network_code, network_code_for, product_code, balance, prev_balance, ");
        selectStrBuff.append(" last_transfer_type, last_transfer_no, last_transfer_on, daily_balance_updated_on, balance_type ");
        selectStrBuff.append(" FROM user_balances ");
        selectStrBuff.append(" WHERE user_id = ? ");
        selectStrBuff.append(walletTypeCondition);
        selectStrBuff.append(forUpdateClause);

        final StringBuffer updateStrBuff = new StringBuffer();
        updateStrBuff.append("UPDATE user_balances SET daily_balance_updated_on = ? ");
        updateStrBuff.append("WHERE user_id = ? ");
        updateStrBuff.append("AND balance_type = ?");

        final StringBuffer insertStrBuff = new StringBuffer();
        insertStrBuff.append("INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
        insertStrBuff.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
        insertStrBuff.append("last_transfer_no, last_transfer_on, created_on, creation_type, balance_type )");
        insertStrBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?) ");

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query = " + selectStrBuff);
            _log.debug(methodName, "Insert Query = " + insertStrBuff);
            _log.debug(methodName, "Update Query = " + updateStrBuff);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectStrBuff.toString());
            pstmtUpdate = p_con.prepareStatement(updateStrBuff.toString());
            pstmtInsert = p_con.prepareStatement(insertStrBuff.toString());

            Date dbDailyBalanceUpdatedOn = null;
            String dbBalanceType = "";
            long dbBalance, dbPreviousBalance = 0L;

            int dayDifference = 0;

            pstmtSelect.setString(1, p_userBalancesVO.getUserID());

            int slctQueryValuesPosNo = 2;
            for (int i = 0; i < walletsForNetAndPrdct.size(); i++) {
                pstmtSelect.setString(slctQueryValuesPosNo + i, walletsForNetAndPrdct.get(i).getAccountCode());
            }
            slctQueryValuesPosNo = slctQueryValuesPosNo + 1;
            pstmtSelect.setDate(slctQueryValuesPosNo, BTSLUtil.getSQLDateFromUtilDate(p_executionDate));

            rs = pstmtSelect.executeQuery();
            pstmtSelect.clearParameters();

            while (rs.next()) {
                dbDailyBalanceUpdatedOn = rs.getDate("daily_balance_updated_on");
                dbBalanceType = rs.getString("balance_type");
                dbBalance = rs.getLong("balance");
                dbPreviousBalance = rs.getLong("prev_balance");

                /** Birendra: To Synchronize with USER_BALANCE table. */
                if (p_c2STransferVO != null && !(p_c2STransferVO.getPdaWalletList().isEmpty())) {

                    final List<UserProductWalletMappingVO> pdaWalletList = p_c2STransferVO.getPdaWalletList();
                    for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletList.iterator(); iterator.hasNext();) {

                        final UserProductWalletMappingVO userProductWalletMappingVO = iterator.next();

                        if (dbBalanceType != null && dbBalanceType.equals(userProductWalletMappingVO.getAccountCode())) {
                            if (userProductWalletMappingVO.getBalance() != dbBalance) {
                                userProductWalletMappingVO.setBalance(dbBalance);
                            }
                            if (userProductWalletMappingVO.getPreviousBalance() != dbPreviousBalance) {
                                userProductWalletMappingVO.setPreviousBalance(dbPreviousBalance);
                            }
                        }
                    }
                }

                // if record exist check updated on date with current date
                // day differences to maintain the record of previous days.
                dayDifference = BTSLUtil.getDifferenceInUtilDates(dbDailyBalanceUpdatedOn, p_executionDate);
                if (dayDifference > 0) {
                    // if dates are not equal get the day differencts and
                    // execute insert qurery no of times of the
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Till now daily Stock is not updated on = " + p_executionDate + ", day differences = " + dayDifference);
                    }

                    for (int counter = 0; counter < dayDifference; counter++) {
                        pstmtInsert.setDate(1, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dbDailyBalanceUpdatedOn, counter)));
                        pstmtInsert.setString(2, rs.getString("user_id"));
                        pstmtInsert.setString(3, rs.getString("network_code"));
                        pstmtInsert.setString(4, rs.getString("network_code_for"));
                        pstmtInsert.setString(5, rs.getString("product_code"));
                        pstmtInsert.setLong(6, rs.getLong("balance"));
                        pstmtInsert.setLong(7, rs.getLong("prev_balance"));
                        pstmtInsert.setString(8, p_userBalancesVO.getLastTransferType());
                        pstmtInsert.setString(9, p_userBalancesVO.getLastTransferID());
                        pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                        pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_executionDate));
                        pstmtInsert.setString(12, PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
                        /** Added By Birendra. */
                        pstmtInsert.setString(13, dbBalanceType);

                        count = pstmtInsert.executeUpdate();
						
						// added to make code compatible with insertion in partitioned table in postgres
						count = BTSLUtil.getInsertCount(count); 
                        if (count <= 0) {
                            pstmtInsert.close();
                            pstmtSelect.close();
                            pstmtUpdate.close();
                            rs.close();
                            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                        }
                        pstmtInsert.clearParameters();
                    }

                    pstmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_executionDate));
                    pstmtUpdate.setString(2, p_userBalancesVO.getUserID());
                    /** Added By Birendra. */
                    pstmtUpdate.setString(3, dbBalanceType);

                    count = pstmtUpdate.executeUpdate();
                    if (count <= 0) {
                        pstmtInsert.close();
                        pstmtSelect.close();
                        pstmtUpdate.close();
                        rs.close();
                        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                    }
                    pstmtUpdate.clearParameters();
                }
            }
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(methodName, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "UserBalancesDAO[ " + methodName + " ]", "", "", "",
                            "BTSLBaseException:" + be.getMessage());
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[ " + methodName + " ]", "", "", "",
                            "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[ " + methodName + " ]", "", "", "",
                            "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting count = " + count);
            }
        }
        return count;
    }

    /**
     * @author birendra.mishra
     * @param p_con
     * @param p_userBalancesVO
     * @param p_categoryCode
     * @return
     * @throws BTSLBaseException
     */
    public int[] creditUserBalancesForWallets(Connection p_con, UserBalancesVO p_userBalancesVO, String p_categoryCode) throws BTSLBaseException {

        final String methodName = "debitUserBalancesFromWallets";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userBalancesVO : " + p_userBalancesVO + ", p_categoryCode = " + p_categoryCode);
        }
        final ResultSet rs = null;
        int[] updateCount = null;
        long thresholdValue = -1;
        ChannelUserVO userVO = null;
        PreparedStatement pstmtUpdateUserBalance = null;
        PreparedStatement psmtInsertUserThreshold = null;
        TransferProfileProductVO transferProfileProductVO = null;
        final List<UserProductWalletMappingVO> pdaWalletList = p_userBalancesVO.getPdaWalletList();

        /**
         * Creating the Query to Update the balance in user_balance table to
         * credit the debited amount from one or more wallets
         */
        final StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
        strBuffUpdate.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? ");
        strBuffUpdate.append(" AND balance_type = ?");

        /** Query to insert the record in user_threshold_counter. */
        final StringBuffer strBuffThresholdInsert = new StringBuffer();
        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
        strBuffThresholdInsert.append(" (user_id, transfer_id, entry_date, entry_date_time, network_code, product_code, ");
        strBuffThresholdInsert.append(" type, transaction_type, record_type, category_code, previous_balance, current_balance, threshold_value, threshold_type, remark ) ");
        strBuffThresholdInsert.append(" VALUES ");
        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

        /**
         * To Get all the balance from <strong>USER_BALANCE<strong> table for
         * all existing wallets and whose PDA flag is active
         */
        long totalBalanceAcrossPDAWallets = loadUserBalanceForProductAndWalletsRS(p_con, p_userBalancesVO, pdaWalletList);
        long totalPreviousBalanceAcrossPDAWallets = p_userBalancesVO.getPreviousBalance();

        try {
            final String updateQuery = strBuffUpdate.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Update query : " + updateQuery);
            }

            totalBalanceAcrossPDAWallets = 0L;
            totalPreviousBalanceAcrossPDAWallets = 0L;
            pstmtUpdateUserBalance = p_con.prepareStatement(updateQuery);

            /**
             * Creating the batch update to update each wallets which
             * participated in the Debit, with their new balance.
             */
            for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletList.iterator(); iterator.hasNext();) {

                final UserProductWalletMappingVO walletMappingVO = iterator.next();

                final long debitedBalance = walletMappingVO.getDebitBalance();
                final String accountCode = walletMappingVO.getAccountCode();

                if (debitedBalance > 0) {

                    walletMappingVO.setPreviousBalance(walletMappingVO.getBalance());
                    walletMappingVO.setBalance(walletMappingVO.getBalance() + debitedBalance);

                    pstmtUpdateUserBalance.setLong(1, walletMappingVO.getBalance());
                    pstmtUpdateUserBalance.setString(2, p_userBalancesVO.getLastTransferType());
                    pstmtUpdateUserBalance.setString(3, p_userBalancesVO.getLastTransferID());
                    pstmtUpdateUserBalance.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                    pstmtUpdateUserBalance.setString(5, p_userBalancesVO.getUserID());
                    pstmtUpdateUserBalance.setString(6, p_userBalancesVO.getProductCode());
                    pstmtUpdateUserBalance.setString(7, p_userBalancesVO.getNetworkCode());
                    pstmtUpdateUserBalance.setString(8, p_userBalancesVO.getNetworkFor());
                    pstmtUpdateUserBalance.setString(9, accountCode);
                    pstmtUpdateUserBalance.addBatch();
                }

                final long balance = walletMappingVO.getBalance();
                final long prevBalance = walletMappingVO.getPreviousBalance();

                totalBalanceAcrossPDAWallets = totalBalanceAcrossPDAWallets + balance;
                totalPreviousBalanceAcrossPDAWallets = totalPreviousBalanceAcrossPDAWallets + prevBalance;
            }

            updateCount = pstmtUpdateUserBalance.executeBatch();
            /**
             * Calculating the new Total Balance and Total Prev_Balance across
             * all PDA wallets.
             */
            p_userBalancesVO.setBalance(totalBalanceAcrossPDAWallets);
            p_userBalancesVO.setPreviousBalance(totalPreviousBalanceAcrossPDAWallets);

        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "SQL Exception: " + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "Exception: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateUserBalance != null) {
                    pstmtUpdateUserBalance.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            try {
                if (BTSLUtil.isNullString(p_categoryCode) || BTSLUtil.isNullString(p_userBalancesVO.getTransferProfileID())) {
                    userVO = new ChannelUserDAO().loadChannelUser(p_con, p_userBalancesVO.getUserID());
                    if (BTSLUtil.isNullString(p_categoryCode)) {
                        p_categoryCode = userVO.getCategoryCode();
                    }
                    if (BTSLUtil.isNullString(p_userBalancesVO.getTransferProfileID())) {
                        p_userBalancesVO.setTransferProfileID(userVO.getTransferProfileID());
                    }
                }
                transferProfileProductVO = TransferProfileProductCache.getTransferProfileDetails(p_userBalancesVO.getTransferProfileID(), p_userBalancesVO.getProductCode());

                int m = 0;
                final String insertUserThreshold = strBuffThresholdInsert.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "QUERY insertUserThreshold = " + insertUserThreshold);
                }
                psmtInsertUserThreshold = p_con.prepareStatement(insertUserThreshold);
                thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
                String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
                final String remark = null;

                if (totalBalanceAcrossPDAWallets <= transferProfileProductVO.getAltBalanceLong() && totalBalanceAcrossPDAWallets >= transferProfileProductVO
                                .getMinResidualBalanceAsLong()) {
                    thresholdValue = transferProfileProductVO.getAltBalanceLong();
                    threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
                }

                if ((totalPreviousBalanceAcrossPDAWallets >= thresholdValue && totalBalanceAcrossPDAWallets <= thresholdValue) || (totalPreviousBalanceAcrossPDAWallets <= thresholdValue && totalBalanceAcrossPDAWallets <= thresholdValue)) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName,
                                        "Entry in threshold counter = " + thresholdValue + ", prvbal = " + totalPreviousBalanceAcrossPDAWallets + "nbal = " + totalBalanceAcrossPDAWallets);
                    }
                    psmtInsertUserThreshold.clearParameters();
                    m = 0;
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getUserID());
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getLastTransferID());
                    psmtInsertUserThreshold.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                    psmtInsertUserThreshold.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getNetworkCode());
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getProductCode());
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getType());
                    psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getLastTransferType());
                    psmtInsertUserThreshold.setString(++m, PretupsI.BELOW_THRESHOLD_TYPE);
                    psmtInsertUserThreshold.setString(++m, p_categoryCode);
                    psmtInsertUserThreshold.setLong(++m, totalPreviousBalanceAcrossPDAWallets);
                    psmtInsertUserThreshold.setLong(++m, totalBalanceAcrossPDAWallets);
                    psmtInsertUserThreshold.setLong(++m, thresholdValue);
                    psmtInsertUserThreshold.setString(++m, threshold_type);
                    psmtInsertUserThreshold.setString(++m, remark);

                    psmtInsertUserThreshold.executeUpdate();
                }

            } catch (SQLException sqle) {
                _log.error(methodName, "SQLException " + sqle.getMessage());
                _log.errorTrace(methodName, sqle);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[debitUserBalances]",
                                p_userBalancesVO.getLastTransferID(), "", p_userBalancesVO.getNetworkCode(),
                                "Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
            }

            try {
                if (psmtInsertUserThreshold != null) {
                    psmtInsertUserThreshold.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName,
                                "Exiting for Transfer ID : " + p_userBalancesVO.getLastTransferID() + ", User ID = " + p_userBalancesVO.getUserID() + ", New Balance = " + totalBalanceAcrossPDAWallets);
            }

        }
        return updateCount;
    }
    
    /**
     * @author yogesh.dixit
     * @param p_con
     * @param p_userBalancesVO
     * @param p_categoryCode
     * @return
     * @throws BTSLBaseException
     */
    public int[] creditUserBalancesForWalletsModified(Connection p_con, UserBalancesVO p_userBalancesVO, String p_categoryCode) throws BTSLBaseException {

        final String methodName = "debitUserBalancesFromWallets";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userBalancesVO : " + p_userBalancesVO + ", p_categoryCode = " + p_categoryCode);
        }
        final ResultSet rs = null;
        int[] updateCount = null;
        long thresholdValue = -1;
        ChannelUserVO userVO = null;
        PreparedStatement pstmtUpdateUserBalance = null;
        final List<UserProductWalletMappingVO> pdaWalletList = p_userBalancesVO.getPdaWalletList();

        /**
         * Creating the Query to Update the balance in user_balance table to
         * credit the debited amount from one or more wallets
         */
        final StringBuffer strBuffUpdate = new StringBuffer();
        strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
        strBuffUpdate.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? ");
        strBuffUpdate.append(" AND balance_type = ?");

        long totalBalanceAcrossPDAWallets = loadUserBalanceForProductAndWalletsRS(p_con, p_userBalancesVO, pdaWalletList);
        long totalPreviousBalanceAcrossPDAWallets = p_userBalancesVO.getPreviousBalance();

        try {
            final String updateQuery = strBuffUpdate.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Update query : " + updateQuery);
            }

            totalBalanceAcrossPDAWallets = 0L;
            totalPreviousBalanceAcrossPDAWallets = 0L;
            pstmtUpdateUserBalance = p_con.prepareStatement(updateQuery);

            for (final Iterator<UserProductWalletMappingVO> iterator = pdaWalletList.iterator(); iterator.hasNext();) {

                final UserProductWalletMappingVO walletMappingVO = iterator.next();

                final long debitedBalance = walletMappingVO.getDebitBalance();
                final String accountCode = walletMappingVO.getAccountCode();

                if (debitedBalance > 0) {

                    walletMappingVO.setPreviousBalance(walletMappingVO.getBalance());
                    walletMappingVO.setBalance(walletMappingVO.getBalance() + debitedBalance);
                    pstmtUpdateUserBalance.setLong(1, walletMappingVO.getBalance());
                    pstmtUpdateUserBalance.setString(2, p_userBalancesVO.getLastTransferType());
                    pstmtUpdateUserBalance.setString(3, p_userBalancesVO.getLastTransferID());
                    pstmtUpdateUserBalance.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                    pstmtUpdateUserBalance.setString(5, p_userBalancesVO.getUserID());
                    pstmtUpdateUserBalance.setString(6, p_userBalancesVO.getProductCode());
                    pstmtUpdateUserBalance.setString(7, p_userBalancesVO.getNetworkCode());
                    pstmtUpdateUserBalance.setString(8, p_userBalancesVO.getNetworkFor());
                    pstmtUpdateUserBalance.setString(9, accountCode);
                    pstmtUpdateUserBalance.addBatch();
                }

                final long balance = walletMappingVO.getBalance();
                final long prevBalance = walletMappingVO.getPreviousBalance();

                totalBalanceAcrossPDAWallets = totalBalanceAcrossPDAWallets + balance;
                totalPreviousBalanceAcrossPDAWallets = totalPreviousBalanceAcrossPDAWallets + prevBalance;
            }

            updateCount = pstmtUpdateUserBalance.executeBatch();
            /**
             * Calculating the new Total Balance and Total Prev_Balance across
             * all PDA wallets.
             */
            p_userBalancesVO.setBalance(totalBalanceAcrossPDAWallets);
            p_userBalancesVO.setPreviousBalance(totalPreviousBalanceAcrossPDAWallets);

        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "SQL Exception: " + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[" + methodName + "]", p_userBalancesVO
                            .getLastTransferID(), "", p_userBalancesVO.getNetworkCode(), "Exception: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateUserBalance != null) {
                    pstmtUpdateUserBalance.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName,
                                "Exiting for Transfer ID : " + p_userBalancesVO.getLastTransferID() + ", User ID = " + p_userBalancesVO.getUserID() + ", New Balance = " + totalBalanceAcrossPDAWallets);
            }

        }
        return updateCount;
    }
    
    
    
    
    
    
    
    

    public int insertUserThresholdCounters(Connection p_con, UserBalancesVO p_userBalancesVO, TransferProfileProductVO transferProfileProductVO, long balance, long newBalance, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "insertUserThresholdCounters";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userBalancesVO : " + p_userBalancesVO + ",newBalance = " + newBalance + ", p_categoryCode = " + p_categoryCode);
        }
        final String METHOD_NAME = "insertUserThresholdCounters";
        int updateCount = 0;
        PreparedStatement psmtInsertUserThreshold = null;
        long thresholdValue = -1;
        try {
            thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
            String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
            final String remark = null;
            if (newBalance <= transferProfileProductVO.getAltBalanceLong() && newBalance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
                thresholdValue = transferProfileProductVO.getAltBalanceLong();
                threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
            }
            /*
             * if previous bal is above threshold and current bal is below
             * threshold then entry in user_threshold_counter.
             * Also,if previous bal is already below threshold and current bal
             * is also below threshold
             * then also entry in user_threshold_counter table(Discussed with
             * Ved Sir and Protim Sir)
             */
            if (newBalance <= thresholdValue) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Entry in threshold counter = " + thresholdValue + ", prvbal = " + balance + ", nbal = " + newBalance);
                }
      
                realTimeLowBalAlertAndAutoCredit(p_userBalancesVO, p_categoryCode);
       
                int m = 0;
                final StringBuffer strBuffThresholdInsert = new StringBuffer();
                strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
                strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
                strBuffThresholdInsert
                                .append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
                strBuffThresholdInsert.append(" VALUES ");
                strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

                final String insertUserThreshold = strBuffThresholdInsert.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "QUERY insertUserThreshold = " + insertUserThreshold);
                }
                psmtInsertUserThreshold = p_con.prepareStatement(insertUserThreshold);

                psmtInsertUserThreshold.clearParameters();
                psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getUserID());
                psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getLastTransferID());
                psmtInsertUserThreshold.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                psmtInsertUserThreshold.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getNetworkCode());
                psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getProductCode());
                // psmtInsertUserThreshold.setLong(++m,
                // p_userBalancesVO.getUnitValue());
                psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getType());
                psmtInsertUserThreshold.setString(++m, p_userBalancesVO.getLastTransferType());
                psmtInsertUserThreshold.setString(++m, PretupsI.BELOW_THRESHOLD_TYPE);
                psmtInsertUserThreshold.setString(++m, p_categoryCode);
                psmtInsertUserThreshold.setLong(++m, balance);
                psmtInsertUserThreshold.setLong(++m, newBalance);
                psmtInsertUserThreshold.setLong(++m, thresholdValue);
                // added by nilesh
                psmtInsertUserThreshold.setString(++m, threshold_type);
                psmtInsertUserThreshold.setString(++m, remark);
                updateCount = psmtInsertUserThreshold.executeUpdate();

            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException = " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[insertUserThresholdCounters]",
                            p_userBalancesVO.getLastTransferID(), "", p_userBalancesVO.getNetworkCode(),
                            "Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
        } finally {
            try {
                if (psmtInsertUserThreshold != null) {
                    psmtInsertUserThreshold.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }
        return updateCount;
    }

    public void realTimeLowBalAlertAndAutoCredit(UserBalancesVO p_userBalancesVO, String p_categoryCode) {
        final String methodName = "realTimeAutoAlertAndCredit";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userBalancesVO : " + p_userBalancesVO);
        }
        try {
            // boolean realTimeAutoC2C = true;
            final boolean realTimeAutoC2C = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.REALTIME_AUTO_C2C_ALLOWED, p_userBalancesVO.getNetworkCode(),
                            p_categoryCode)).booleanValue();
            if (realTimeAutoC2C) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Real time auto C2C enabled and start here realTimeAutoC2C:" + realTimeAutoC2C);
                }
                final LowBalanceAlertVO alertVO = new LowBalanceAlertVO();
                alertVO.setUserId(p_userBalancesVO.getUserID());
                alertVO.setProductCode(p_userBalancesVO.getProductCode());
                alertVO.setNetworkCode(p_userBalancesVO.getNetworkCode());
                alertVO.setCategoryCode(p_categoryCode);
                try {
                    new com.btsl.pretups.channel.transfer.businesslogic.AutoC2CBL().realTimeAutoC2C(alertVO);
                } catch (BTSLBaseException ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            /*
             * boolean realTimeAutoO2C = false;
             * if(realTimeAutoO2C){
             * if (_log.isDebugEnabled()) {
             * _log.debug(methodName,
             * "Real time auto O2C enabled and start here");
             * }
             * //new
             * com.btsl.pretups.channel.transfer.businesslogic.AutoC2CBL().
             * realTimeAutoC2C(alertVO);
             * }
             * 
             * boolean realTimeLowBalanceAlert = false;
             * if(realTimeLowBalanceAlert){
             * if (_log.isDebugEnabled()) {
             * _log.debug(methodName,
             * "Real time low Balance alert enabled and push message here");
             * }
             * //new
             * com.btsl.pretups.channel.transfer.businesslogic.AutoC2CBL().
             * realTimeAutoC2C(alertVO);
             * }
             */

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "SQLException = " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[insertUserThresholdCounters]",
                            p_userBalancesVO.getLastTransferID(), "", p_userBalancesVO.getNetworkCode(),
                            "Error while updating user_threshold_counter table SQL Exception:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting for Transfer ID = " + p_userBalancesVO.getLastTransferID() + ", User ID = " + p_userBalancesVO.getUserID());
            }
        }
    }

    /**
     * Method to loads max balance for the user for a particular product when
     * Partial Deduction is N for any wallet
     * 
     * @param p_con
     * @param p_requestID
     * @param p_userID
     * @param p_networkID
     * @param p_networkFor
     * @param p_productCode
     * @return long
     * @throws BTSLBaseException
     */
    public ArrayList<UserProductWalletMappingVO> loadUserBalanceWhenPDAIsN(Connection p_con, String p_requestID, String p_userID, String p_networkID, String p_networkFor, String p_productCode) throws BTSLBaseException {
        final String methodName = "loadUserBalanceForProduct";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,
                            "Entered p_requestID = " + p_requestID + ", p_userID : " + p_userID + ", p_networkID = " + p_networkID + ", p_networkFor = " + p_networkFor + ", p_productCode = " + p_productCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final long balance = 0;
        final ArrayList<UserProductWalletMappingVO> list = new ArrayList<UserProductWalletMappingVO>();
        try {
            UserProductWalletMappingVO userProductWalletMappingVO = null;
            final StringBuffer selectQueryBuff = new StringBuffer("SELECT distinct upm.account_priority,ub.balance,ub.balance_type,ub.prev_balance  ");
            selectQueryBuff.append(" FROM user_balances ub,user_wallet_product_mapping upm ");
            selectQueryBuff.append(" WHERE ub.user_id = ? AND ub.product_code = ? AND ub.network_code = ? AND ub.network_code_for = ?  AND upm.account_code=ub.balance_type");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query : " + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userID);
            pstmtSelect.setString(2, p_productCode);
            pstmtSelect.setString(3, p_networkID);
            pstmtSelect.setString(4, p_networkFor);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                userProductWalletMappingVO = new UserProductWalletMappingVO();
                userProductWalletMappingVO.setAccountPriority(rs.getInt("account_priority"));
                userProductWalletMappingVO.setBalance(rs.getLong("balance"));
                userProductWalletMappingVO.setBalanceType(rs.getString("balance_type"));
             // This is done because if in case current balance becomes zero then in that case it takes previous balance which was done of last transaction
               if(userProductWalletMappingVO.getBalance()==0)
                	userProductWalletMappingVO.setPreviousBalance(0);
                else
                userProductWalletMappingVO.setPreviousBalance(rs.getLong("prev_balance"));
                list.add(userProductWalletMappingVO);

            }
            Collections.sort(list);
            return list;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[loadUserBalanceForProduct]",
                            p_requestID, "", p_networkID, "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadUserBalanceForProduct", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesDAO[loadUserBalanceForProduct]",
                            p_requestID, "", p_networkID, "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting balance:" + balance);
            }
        }// end of finally
    }

    // 6.5
    public int updateUserBalancesForDeleteO2C(Connection p_con, String p_userId, ChannelTransferVO p_channelTransferVO, long p_balance, String p_balanceType) throws BTSLBaseException {
        final String methodName = "updateUserBalancesForDeleteO2C";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userID : " + p_userId);
        }
        PreparedStatement psmtUpdate = null;
        final ResultSet rs = null;
        int updateCount = 0;
        try {

            final StringBuffer strBuffUpdate = new StringBuffer();
            strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance , balance = ? , last_transfer_type = ? , ");
            strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ?");
            strBuffUpdate.append(" WHERE ");
            strBuffUpdate.append(" user_id = ? ");
            strBuffUpdate.append(" AND ");
            strBuffUpdate.append(" network_code = ? AND network_code_for = ? AND product_code = ? AND balance_type=? ");
            final String updateQuery = strBuffUpdate.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query : " + updateQuery);
            }
            //
            psmtUpdate = p_con.prepareStatement(updateQuery);
            // psmtUpdate.setLong(1,p_previousBalance);
            psmtUpdate.setLong(1, p_balance);
            psmtUpdate.setString(2, p_channelTransferVO.getTransferType());
            psmtUpdate.setString(3, p_channelTransferVO.getTransferID());
            psmtUpdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getTransferDate()));
            psmtUpdate.setString(5, p_userId);
            // psmtUpdate.setString(6, p_channelTransferVO.getProductCode());
            psmtUpdate.setString(6, p_channelTransferVO.getNetworkCode());
            psmtUpdate.setString(7, p_channelTransferVO.getNetworkCodeFor());
            psmtUpdate.setString(8, p_channelTransferVO.getProductCode());
            psmtUpdate.setString(9, p_balanceType);
            //
            updateCount = psmtUpdate.executeUpdate();
            if (updateCount <= 0) {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,
            // EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
            // "UserBalancesDAO[loadUserBalanceForProduct]", "SQL Exception:" +
            // sqle.getMessage());
            throw new BTSLBaseException(this, "loadUserBalanceForProduct", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,
            // EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
            // "UserBalancesDAO[loadUserBalanceForProduct]", "Exception:" +
            // e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting balance:" + p_balance);
            }
        }// end of finally
    }

    // 6.5

    /**
     * Method to update the balance of the leaf user who is to be deleted
     * 
     * @author akanksha
     * @param p_con
     * @param p_userID
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList<UserBalancesVO> loadUserBalanceForDelete(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "loadUserBalanceForDelete";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userID : " + p_userId);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final long balance = 0;
        final ArrayList<UserBalancesVO> list = new ArrayList<UserBalancesVO>();
        try {
            UserBalancesVO userBalancesVO = null;
            final StringBuffer selectQueryBuff = new StringBuffer("SELECT ub.balance,ub.prev_balance, ub.product_code,ub.network_code,ub.network_code_for,ub.balance_type,ub.last_transfer_no ");
            selectQueryBuff.append(" FROM user_balances ub ");
            selectQueryBuff.append(" WHERE ub.user_id = ? ");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query : " + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userId);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                userBalancesVO = new UserBalancesVO();
                userBalancesVO.setBalance(rs.getLong("balance"));
                userBalancesVO.setBalanceType(rs.getString("balance_type"));
                userBalancesVO.setPreviousBalance(rs.getLong("prev_balance"));
                userBalancesVO.setProductCode(rs.getString("product_code"));
                userBalancesVO.setNetworkCode(rs.getString("network_code"));
                userBalancesVO.setNetworkFor(rs.getString("network_code_for"));
                userBalancesVO.setLastTransferID(rs.getString("last_transfer_no"));
                list.add(userBalancesVO);
            }
            return list;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,
            // EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
            // "UserBalancesDAO[loadUserBalanceForProduct]", "SQL Exception:" +
            // sqle.getMessage());
            throw new BTSLBaseException(this, "loadUserBalanceForProduct", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,
            // EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
            // "UserBalancesDAO[loadUserBalanceForProduct]", "Exception:" +
            // e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting list size:" + list.size());
            }
        }// end of finally
    }

    public int updateUserBalancesForDelete(Connection p_con, String p_userId, ChannelTransferVO p_channelTransferVO, long p_balance, String p_balanceType) throws BTSLBaseException {
        final String methodName = "updateUserBalancesForDeleteO2C";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userID : " + p_userId);
        }
        PreparedStatement psmtUpdate = null;
        final ResultSet rs = null;
        final long balance = 0;
        int updateCount = 0;
        try {

            final StringBuffer strBuffUpdate = new StringBuffer();
            strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
            strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ?");
            strBuffUpdate.append(" WHERE ");
            strBuffUpdate.append(" user_id = ? ");
            strBuffUpdate.append(" AND ");
            strBuffUpdate.append(" product_code = ? AND network_code = ? AND network_code_for = ? And balance_type=? ");
            final String updateQuery = strBuffUpdate.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query : " + updateQuery);
            }
            //
            psmtUpdate = p_con.prepareStatement(updateQuery);
            psmtUpdate.setLong(1, balance);
            psmtUpdate.setString(2, p_channelTransferVO.getTransferType());
            psmtUpdate.setString(3, p_channelTransferVO.getTransferID());
            psmtUpdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getTransferDate()));
            psmtUpdate.setString(5, p_userId);
            psmtUpdate.setString(6, p_channelTransferVO.getProductCode());
            psmtUpdate.setString(7, p_channelTransferVO.getNetworkCode());
            psmtUpdate.setString(8, p_channelTransferVO.getNetworkCodeFor());
            psmtUpdate.setString(9, p_balanceType);
            updateCount = psmtUpdate.executeUpdate();

            //
            return updateCount;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,
            // EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
            // "UserBalancesDAO[loadUserBalanceForProduct]", "SQL Exception:" +
            // sqle.getMessage());
            throw new BTSLBaseException(this, "loadUserBalanceForProduct", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,
            // EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
            // "UserBalancesDAO[loadUserBalanceForProduct]", "Exception:" +
            // e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting balance:" + balance);
            }
        }// end of finally
    }

    // 6.5

    /**
     * Method to update the balance of the owner user on deletion process
     * 
     * @author akanksha
     * @param p_con
     * @param p_channelTransferVO
     * @param p_userID
     * @param p_balanceType
     * @throws BTSLBaseException
     */
    public int updateCreditUserBalancesForDeleteC2C(Connection p_con, String p_userId, ChannelTransferVO p_channelTransferVO, long p_balance, String p_balanceType) throws BTSLBaseException {
        final String methodName = "updateCreditUserBalancesForDeleteC2C";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,
                            "Entered p_userID : " + p_userId + " p_channelTransferVO " + p_channelTransferVO + " p_balance " + p_balance + " p_balanceType " + p_balanceType);
        }
         
        
        ResultSet rs = null;
        long balance = 0;
        int updateCount = 0;
        try {
             
            final StringBuffer selectQueryBuff = new StringBuffer("SELECT balance ");
            selectQueryBuff.append("FROM user_balances ");
            selectQueryBuff.append("WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? ");
            selectQueryBuff.append("AND balance_type = ?");

            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query : " + selectQuery);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_userId);
            pstmtSelect.setString(2, p_channelTransferVO.getProductCode());
            pstmtSelect.setString(3, p_channelTransferVO.getNetworkCode());
            pstmtSelect.setString(4, p_channelTransferVO.getNetworkCodeFor());
            pstmtSelect.setString(5, p_balanceType);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                balance = rs.getLong("balance");
           
            final StringBuffer strBuffUpdate = new StringBuffer();
            strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
            strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ?");
            strBuffUpdate.append(" WHERE ");
            strBuffUpdate.append(" user_id = ? ");
            strBuffUpdate.append(" AND ");
            strBuffUpdate.append(" product_code = ? AND network_code = ? AND network_code_for = ? And balance_type=? ");
            final String updateQuery = strBuffUpdate.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query : " + updateQuery);
            }
            try(PreparedStatement psmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            psmtUpdate.setLong(1, (balance + p_balance));
            psmtUpdate.setString(2, p_channelTransferVO.getTransferType());
            psmtUpdate.setString(3, p_channelTransferVO.getTransferID());
            psmtUpdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getTransferDate()));
            psmtUpdate.setString(5, p_userId);
            psmtUpdate.setString(6, p_channelTransferVO.getProductCode());
            psmtUpdate.setString(7, p_channelTransferVO.getNetworkCode());
            psmtUpdate.setString(8, p_channelTransferVO.getNetworkCodeFor());
            psmtUpdate.setString(9, p_balanceType);
            updateCount = psmtUpdate.executeUpdate();
            return updateCount;
            }
            }
            else {
            	final StringBuffer strBuffInsert = new StringBuffer();
            	strBuffInsert.append(" Insert into user_balances  (prev_balance, balance, last_transfer_type, ");
            	strBuffInsert.append(" last_transfer_no, last_transfer_on,user_id,");
            	strBuffInsert.append(" product_code,network_code,network_code_for,balance_type)");
            	strBuffInsert.append(" VALUES(?,?,?,?,?,?,?,?,?,?)");
                final String insertQuery = strBuffInsert.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "select query : " + insertQuery);
                }
                try(PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
                {
                psmtInsert.setLong(1, 0);
                psmtInsert.setLong(2, (balance + p_balance));
                psmtInsert.setString(3, p_channelTransferVO.getTransferType());
                psmtInsert.setString(4, p_channelTransferVO.getTransferID());
                psmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_channelTransferVO.getTransferDate()));
                psmtInsert.setString(6, p_userId);
                psmtInsert.setString(7, p_channelTransferVO.getProductCode());
                psmtInsert.setString(8, p_channelTransferVO.getNetworkCode());
                psmtInsert.setString(9, p_channelTransferVO.getNetworkCodeFor());
                psmtInsert.setString(10, p_balanceType);
                updateCount = psmtInsert.executeUpdate();
                return updateCount;
            }
            }
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,
            // EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
            // "UserBalancesDAO[loadUserBalanceForProduct]", "SQL Exception:" +
            // sqle.getMessage());
            throw new BTSLBaseException(this, "loadUserBalanceForProduct", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,
            // EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
            // "UserBalancesDAO[loadUserBalanceForProduct]", "Exception:" +
            // e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
           
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting balance:" + p_balance);
            }
        }// end of finally
    }

    /**
     * Method updateUserDailyBalancesForMultipleProductAndWallet.
     * Method to update the user balances
     * 
     * @author brajesh.prasad
     * @param p_con
     *            Connection
     * @param p_executionDate
     *            Date
     * @param p_userBalancesVO
     *            UserBalancesVO
     * @return int
     * @throws BTSLBaseException
     */

    public int updateUserDailyBalancesForMultipleProductAndWallet(Connection p_con, Date p_executionDate, UserBalancesVO p_userBalancesVO) throws BTSLBaseException {
        final String METHOD_NAME = "updateUserDailyBalancesForMultipleProductAndWallet";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_executionDate=" + p_executionDate + ", p_userBalancesVO = " + p_userBalancesVO);
        }
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdate = null;
        ResultSet rs = null;
        int count = 1;
      
        String selectStrBuff = userBalancesQry.updateUserDailyBalancesForMultipleProductAndWalletQry();
        final StringBuffer updateStrBuff = new StringBuffer();
        updateStrBuff.append("UPDATE user_balances SET daily_balance_updated_on = ? ");
        updateStrBuff.append("WHERE user_id = ? AND product_code= ? ");
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
            updateStrBuff.append(" AND  balance_type= ? ");
        }
        final StringBuffer insertStrBuff = new StringBuffer();
        insertStrBuff.append("INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
        insertStrBuff.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
        insertStrBuff.append("last_transfer_no, last_transfer_on, created_on,creation_type ");
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
            insertStrBuff.append(" ,balance_type ");
        }
        insertStrBuff.append(")");
        insertStrBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,? ");
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
            insertStrBuff.append(" ,? ");
        }
        insertStrBuff.append(")");
        if (_log.isDebugEnabled()) {
            _log.debug("updateUserDailyBalances ", "Select Query=" + selectStrBuff);
            _log.debug("updateUserDailyBalances ", "Insert Query=" + insertStrBuff);
            _log.debug("updateUserDailyBalances ", "Update Query=" + updateStrBuff);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectStrBuff);
            pstmtUpdate = p_con.prepareStatement(updateStrBuff.toString());
            pstmtInsert = p_con.prepareStatement(insertStrBuff.toString());
            Date dailyBalanceUpdatedOn = null;
            int dayDifference = 0;
            pstmtSelect.setString(1, p_userBalancesVO.getUserID());
            pstmtSelect.setString(2, p_userBalancesVO.getProductCode());
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                pstmtSelect.setString(3, p_userBalancesVO.getBalanceType());
                pstmtSelect.setDate(4, BTSLUtil.getSQLDateFromUtilDate(p_executionDate));
            } else {
                pstmtSelect.setDate(3, BTSLUtil.getSQLDateFromUtilDate(p_executionDate));
            }
            rs = pstmtSelect.executeQuery();
            pstmtSelect.clearParameters();
            while (rs.next()) {
                dailyBalanceUpdatedOn = rs.getDate("daily_balance_updated_on");
                dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn, p_executionDate);
                if (dayDifference > 0) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("updateUserDailyBalances ", "Till now daily Stock is not updated on " + p_executionDate + ", day differences = " + dayDifference);
                    }
                    for (int k = 0; k < dayDifference; k++) {
                        pstmtInsert.setDate(1, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn, k)));
                        pstmtInsert.setString(2, rs.getString("user_id"));
                        pstmtInsert.setString(3, rs.getString("network_code"));
                        pstmtInsert.setString(4, rs.getString("network_code_for"));
                        pstmtInsert.setString(5, rs.getString("product_code"));
                        pstmtInsert.setLong(6, rs.getLong("balance"));
                        pstmtInsert.setLong(7, rs.getLong("prev_balance"));
                        pstmtInsert.setString(8, p_userBalancesVO.getLastTransferType());
                        pstmtInsert.setString(9, p_userBalancesVO.getLastTransferID());
                        pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_userBalancesVO.getLastTransferOn()));
                        pstmtInsert.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_executionDate));
                        pstmtInsert.setString(12, PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                            pstmtInsert.setString(13, rs.getString("balance_type"));
                        }
                        count = pstmtInsert.executeUpdate();
						// added to make code compatible with insertion in partitioned table in postgres
						count = BTSLUtil.getInsertCount(count); 
                        if (count <= 0) {
                            pstmtInsert.close();
                            pstmtSelect.close();
                            pstmtUpdate.close();
                            rs.close();
                            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
                        }
                        pstmtInsert.clearParameters();
                    }
                    pstmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_executionDate));
                    pstmtUpdate.setString(2, p_userBalancesVO.getUserID());
                    pstmtUpdate.setString(3, p_userBalancesVO.getProductCode());
                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                        pstmtUpdate.setString(4, p_userBalancesVO.getBalanceType());
                    }
                    count = pstmtUpdate.executeUpdate();
                    if (count <= 0) {
                        pstmtInsert.close();
                        pstmtSelect.close();
                        pstmtUpdate.close();
                        rs.close();
                        throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
                    }
                    pstmtUpdate.clearParameters();
                }
            }// end of while loop
        } catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                            "UserBalancesDAO[updateUserDailyBalancesForMultipleProductAndWallet]", "", "", "", "BTSLBaseException:" + be.getMessage());
            throw be;
        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "UserBalancesDAO[updateUserDailyBalancesForMultipleProductAndWallet]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "UserBalancesDAO[updateUserDailyBalancesForMultipleProductAndWallet]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting count = " + count);
            }
        }
        return count;
    }
    
    
    /**
     * Method getUserBalanceafterDeletionC2C.
     * Method to retrieve previous balance on deletion of leaf user for entry in channel transfer items
     * 
     * @author akanksha
     * @param p_con
     *            Connection
     * @param p_userId
     *            String
     * @param p_balanceType
     *            String
     * @param p_netcode
     * 			 String
     * @return long
     * @throws BTSLBaseException
     */
    public long getUserBalanceafterDeletionC2C(Connection p_con, String p_userId, String p_balanceType,String p_netcode) throws BTSLBaseException {
        final String methodName = "getUserBalanceafterDeletionC2C";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,
                            "Entered p_userID : " + p_userId + " p_balanceType " + p_balanceType);
        }
        PreparedStatement psmtUpdate = null;
         
        long prev_balance = 0;
        try {
           
            final StringBuffer selectQueryBuff = new StringBuffer("SELECT prev_balance ");
            selectQueryBuff.append("FROM user_balances ");
            selectQueryBuff.append("WHERE user_id = ? AND network_code = ?  ");
            selectQueryBuff.append("AND balance_type = ?");

            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query : " + selectQuery);
            }
            try( PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_userId);
            pstmtSelect.setString(2, p_netcode);
            pstmtSelect.setString(3, p_balanceType);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                prev_balance = rs.getLong("prev_balance");
            }
            return prev_balance;
        }
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            throw new BTSLBaseException(this, "getUserBalanceafterDeletionC2C", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
           
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting balance:" + prev_balance);
            }
        }// end of finally
    }
	public int diffCreditAndDebitUserBalances(Connection p_con, UserBalancesVO p_userBalancesDebitVO, UserBalancesVO p_userBalancesCreditVO) throws BTSLBaseException
	{
		final String methodName="diffCreditAndDebitUserBalances";
		if (_log.isDebugEnabled()) 
			_log.debug("diffCreditAndDebitUserBalances", "Entered p_userBalancesVO : " + p_userBalancesDebitVO+", p_userBalancesDebitVO.getUserID()="+p_userBalancesDebitVO.getUserID());
		int updateCount = 0;
		PreparedStatement pstmt = null;
		long balance=0;
		long newBalance=0;
		ResultSet rs = null;
		
		final String sqlSelect = userBalancesQry.diffCreditAndDebitUserBalancesQry();
		if (_log.isDebugEnabled())  
			_log.debug("diffCreditAndDebitUserBalances", "QUERY sqlSelect=" + sqlSelect);
		StringBuffer strBuffUpdate = new StringBuffer();
		strBuffUpdate.append(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , "); 
		strBuffUpdate.append(" last_transfer_no = ? , last_transfer_on = ? ");
		strBuffUpdate.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? ");
		String updateQuery = strBuffUpdate.toString();
		if (_log.isDebugEnabled())  
			_log.debug("diffCreditAndDebitUserBalances", "Update query:" + updateQuery);
	    String [] strArr=null;
		try
		{
			
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, p_userBalancesCreditVO.getUserID());
			pstmt.setString(2, p_userBalancesCreditVO.getProductCode());
			pstmt.setString(3, p_userBalancesCreditVO.getNetworkCode());
			pstmt.setString(4, p_userBalancesCreditVO.getNetworkFor());
			rs = pstmt.executeQuery();
			
			if (rs.next())
			{
				balance=rs.getLong("balance");
			}else{
				StringBuffer insertUserBalanceBuff =new StringBuffer("INSERT INTO user_balances(user_id, network_code, ");
				insertUserBalanceBuff.append("network_code_for, product_code, balance, prev_balance, last_transfer_type,");
				insertUserBalanceBuff.append("last_transfer_no, last_transfer_on) VALUES(?,?,?,?,?,?,?,?,?)");
				
				pstmt = p_con.prepareStatement(insertUserBalanceBuff.toString());
				if(_log.isDebugEnabled())
					_log.debug("transferChannelUser","insertUserBalance query="+insertUserBalanceBuff);
				newBalance=p_userBalancesCreditVO.getQuantityToBeUpdated();
				pstmt.setString(1,p_userBalancesCreditVO.getUserID());
				pstmt.setString(2,p_userBalancesCreditVO.getNetworkCode());
				pstmt.setString(3,p_userBalancesCreditVO.getNetworkFor());
				pstmt.setString(4,p_userBalancesCreditVO.getProductCode());
				pstmt.setLong(5,p_userBalancesCreditVO.getQuantityToBeUpdated());
				pstmt.setLong(6,0L);
				pstmt.setString(7,p_userBalancesCreditVO.getLastTransferType());
				pstmt.setString(8,p_userBalancesCreditVO.getLastTransferID());
				pstmt.setTimestamp(9,BTSLUtil.getTimestampFromUtilDate(p_userBalancesCreditVO.getLastTransferOn()));
				updateCount =	pstmt.executeUpdate();
				if(updateCount<=0)
					throw new BTSLBaseException(this, "diffCreditAndDebitUserBalances", PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
				if(_log.isDebugEnabled())
					_log.debug("diffCreditAndDebitUserBalances","After inserting new user balances information");
				}
			if(_log.isDebugEnabled())
				_log.debug("diffCreditAndDebitUserBalances","updateCount = "+updateCount+",newBalance="+newBalance+",balance="+balance);
			if(updateCount == 0)
			{
				newBalance=balance+p_userBalancesCreditVO.getQuantityToBeUpdated();
				pstmt.clearParameters();
				pstmt = p_con.prepareStatement(updateQuery);
				p_userBalancesCreditVO.setPreviousBalance(balance);
				p_userBalancesCreditVO.setBalance(newBalance);
				pstmt.setLong(1, newBalance);
				pstmt.setString(2, p_userBalancesCreditVO.getLastTransferType());
				pstmt.setString(3, p_userBalancesCreditVO.getLastTransferID());
				pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userBalancesCreditVO.getLastTransferOn()));
				pstmt.setString(5, p_userBalancesCreditVO.getUserID());
				pstmt.setString(6, p_userBalancesCreditVO.getProductCode());
				pstmt.setString(7, p_userBalancesCreditVO.getNetworkCode());
				pstmt.setString(8, p_userBalancesCreditVO.getNetworkFor());
				updateCount = pstmt.executeUpdate();
			}
				if(updateCount<=0)
					throw new BTSLBaseException(this, "diffCreditAndDebitUserBalances",PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
				else{/*
					if(_log.isDebugEnabled())
						_log.debug("diffCreditAndDebitUserBalances","updateCount 2= "+updateCount+",newBalance="+newBalance+",balance="+balance);
					pstmt = p_con.prepareStatement(sqlSelect);
					pstmt.setString(1, p_userBalancesDebitVO.getUserID());
					pstmt.setString(2, p_userBalancesDebitVO.getProductCode());
					pstmt.setString(3, p_userBalancesDebitVO.getNetworkCode());
					pstmt.setString(4, p_userBalancesDebitVO.getNetworkFor());
					rs = pstmt.executeQuery();
					if (rs.next())
					{
						balance=rs.getLong("balance");
					}
					newBalance=balance-p_userBalancesDebitVO.getQuantityToBeUpdated();
					if(_log.isDebugEnabled())
						_log.debug("diffCreditAndDebitUserBalances","updateCount 3= "+updateCount+",newBalance="+newBalance+",balance="+balance);
					if(balance  < p_userBalancesDebitVO.getQuantityToBeUpdated()){
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserBalanceDAO[diffCreditAndDebitUserBalances]","","","","Owner current balance is less than required balance for requested Adjustment Dr Amt.");	
						if (_log.isDebugEnabled())  
							_log.debug("diffCreditAndDebitUserBalances", "Owner Current Bal:" + balance + "And required Dr Amt : " + p_userBalancesDebitVO.getQuantityToBeUpdated());
					}
					pstmt.clearParameters();
					pstmt = p_con.prepareStatement(updateQuery);
					p_userBalancesDebitVO.setPreviousBalance(balance);
					p_userBalancesDebitVO.setBalance(newBalance);
					pstmt.setLong(1, newBalance);
					pstmt.setString(2, p_userBalancesDebitVO.getLastTransferType());
					pstmt.setString(3, p_userBalancesDebitVO.getLastTransferID());
					pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_userBalancesDebitVO.getLastTransferOn()));
					pstmt.setString(5, p_userBalancesDebitVO.getUserID());
					pstmt.setString(6, p_userBalancesDebitVO.getProductCode());
					pstmt.setString(7, p_userBalancesDebitVO.getNetworkCode());
					pstmt.setString(8, p_userBalancesDebitVO.getNetworkFor());
					updateCount = pstmt.executeUpdate();
				*/}
		}
		catch (SQLException sqle)
		{
			_log.error("diffCreditAndDebitUserBalances", "SQLException " + sqle.getMessage());
			_log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserBalancesDAO[diffCreditAndDebitUserBalances]",p_userBalancesDebitVO.getLastTransferID(),"",p_userBalancesDebitVO.getNetworkCode(),"SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, "diffCreditAndDebitUserBalances", "error.general.sql.processing");
		}// end of catch
		catch (Exception e)
		{
			_log.error("diffCreditAndDebitUserBalances", "Exception " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserBalancesDAO[diffCreditAndDebitUserBalances]",p_userBalancesDebitVO.getLastTransferID(),"",p_userBalancesDebitVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this, "diffCreditAndDebitUserBalances", "error.general.processing");
		}// end of catch
		finally
		{
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				_log.error("diffCreditAndDebitUserBalances", "SQLException " + e.getMessage());
				_log.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
				_log.errorTrace("diffCreditAndDebitUserBalances",e);
			}
			if (_log.isDebugEnabled()) 	
				_log.debug("diffCreditAndDebitUserBalances", "Exiting for Transfer ID:" + p_userBalancesDebitVO.getLastTransferID()+" User ID ="+p_userBalancesDebitVO.getUserID()+" New Balance="+newBalance);
		}// end of finally		
		return updateCount;
	}
	public int insertUserBalancesMappGw(Connection p_con,String userId,String productCode,String nwCode) throws BTSLBaseException
	{
		final String methodName="insertUserBalancesMappGw";
		if (_log.isDebugEnabled()) 
			_log.debug("insertUserBalancesMappGw", "Entered userId : " + userId+", productCode="+productCode);
		int updateCount = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		 try{
				StringBuffer insertUserBalanceBuff =new StringBuffer("INSERT INTO user_balances(user_id, network_code, ");
				insertUserBalanceBuff.append("network_code_for, product_code, balance, prev_balance, last_transfer_type,");
				insertUserBalanceBuff.append("last_transfer_no, last_transfer_on,daily_balance_updated_on,balance_type) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				
				pstmt = p_con.prepareStatement(insertUserBalanceBuff.toString());
				if(_log.isDebugEnabled())
					_log.debug("transferChannelUser","insertUserBalance query="+insertUserBalanceBuff);
				pstmt.setString(1,userId);
				pstmt.setString(2,nwCode);
				pstmt.setString(3,nwCode);
				pstmt.setString(4,productCode);
				pstmt.setLong(5,0L);
				pstmt.setLong(6,0L);
				pstmt.setString(7,"TRANSFER");
				pstmt.setString(8,"");
				pstmt.setTimestamp(9,BTSLUtil.getTimestampFromUtilDate(new Date()));
				pstmt.setTimestamp(10,BTSLUtil.getTimestampFromUtilDate(new Date()));
				pstmt.setString(11,((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
				updateCount =	pstmt.executeUpdate();
				if(updateCount<=0)
					throw new BTSLBaseException(this, "insertUserBalancesMappGw", PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
				if(_log.isDebugEnabled())
					_log.debug("insertUserBalancesMappGw","After inserting new user balances information");
				
				
		}
		catch (SQLException sqle)
		{
			_log.error("insertUserBalancesMappGw", "SQLException " + sqle.getMessage());
			_log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserBalancesDAO[insertUserBalancesMappGw]",productCode,"","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, "insertUserBalancesMappGw", "error.general.sql.processing");
		}// end of catch
		catch (Exception e)
		{
			_log.error("insertUserBalancesMappGw", "Exception " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserBalancesDAO[insertUserBalancesMappGw]",productCode,"","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, "insertUserBalancesMappGw", "error.general.processing");
		}// end of catch
		finally
		{
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				_log.error("insertUserBalancesMappGw", "SQLException " + e.getMessage());
				_log.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Exception e) {
				_log.errorTrace("insertUserBalancesMappGw",e);
			}
			if (_log.isDebugEnabled()) 	
				_log.debug("insertUserBalancesMappGw", "Exiting for Transfer ID:" + productCode+" User ID ="+userId+" New Balance=");
		}// end of finally		
		return updateCount;
	}

}
