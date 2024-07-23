/**
 * @# ChannelUserTransferDAO.java
 * 
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    Sandeep Goel Aug 30, 2005 Initial creation
 *    Sandeep Goel Oct 25, 2005 Modification ID CUT001
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.channel.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// commented for DB2 import oracle.jdbc.OraclePreparedStatement;
import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.TypesI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.UserOtpDAO;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.user.businesslogic.ChannelUserTransferWebDAO;
/**
 * 
 */
public class ChannelUserTransferDAO {
    /**
     * Field _log. This field is used to display the logs for debugging purpose.
     */
    private static Log log = LogFactory.getLog(ChannelUserTransferDAO.class.getName());
    private ChannelUserTransferQry channelUserTransferQry;
    private static final  String CLASSNAME = "ChannelUserTransferDAO";
    
	
    /**
     * Constructor for ChannelUserTransferDAO.
     */
    public ChannelUserTransferDAO() {
        super();
    	channelUserTransferQry = (ChannelUserTransferQry)ObjectProducer.getObject(QueryConstants.CHANNEL_USER_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);

    }

    /**
     * Method parentBalanceUpdateValue.
     * 
     * @param p_con
     * @param p_toParentID
     *            String
     * @return ArrayList
     * @author nilesh.kumar
     * @throws BTSLBaseException
     */
    public ArrayList parentBalanceUpdateValue(Connection p_con, String p_toParentID, Long _autoC2CAmount, String product_code) throws BTSLBaseException {
        final String methodName = "parentBalanceUpdateValue";
        if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.ENTERED + " p_toParentID=" + p_toParentID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelTransferItemsVO transferItemsVO = null;
        final ArrayList transferItemsVOList = new ArrayList();

        final StringBuffer strBuff = new StringBuffer("SELECT U.network_code, U.network_code_for, ");
        strBuff.append("U.product_code, U.balance, U.prev_balance, U.last_transfer_type, U.last_transfer_no,");
        strBuff.append("U.last_transfer_on,P.unit_value,U.daily_balance_updated_on,P.short_name FROM user_balances U,products P  ");
        strBuff.append("WHERE user_id=? AND U.product_code=P.product_code and U.product_code = ?");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_toParentID);
            pstmtSelect.setString(2, product_code);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                if (log.isDebugEnabled()) {
                    log.debug("transferChannelUser", "Before transferItemsVO construction");
                }
                transferItemsVO = new ChannelTransferItemsVO();
                // transferItemsVO.setSerialNum(++k);
                transferItemsVO.setProductCode(rs.getString("product_code"));
                transferItemsVO.setRequiredQuantity(_autoC2CAmount);
                transferItemsVO.setRequestedQuantity(String.valueOf(_autoC2CAmount));
                transferItemsVO.setApprovedQuantity(_autoC2CAmount);
                transferItemsVO.setUnitValue(rs.getLong("unit_value"));
                transferItemsVO.setNetworkCode(rs.getString("network_code"));
                transferItemsVO.setShortName(rs.getString("short_name"));
                transferItemsVOList.add(transferItemsVO);
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "transferItemsVO " + transferItemsVO);
                }

            }
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTransferDAO[parentBalanceUpdateValue]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTransferDAO[parentBalanceUpdateValue]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: geoList size =" + transferItemsVOList.size());
            }
        }
        return transferItemsVOList;
    }

    /*
     * Method getTransferlistForAutoFOC.
     * 
     * @param p_con
     * 
     * @param p_touserID String
     * 
     * @return ArrayList
     * 
     * @author gaurav.pandey
     * 
     * @throws BTSLBaseException
     */

    public ArrayList getTransferlistForAutoFOC(Connection p_con, String p_toUserID, Long _autoFOCAmount,String p_productCode) throws BTSLBaseException {
        final String methodName = "getTransferlistForAutoFOC";
        if (log.isDebugEnabled()) {
            log.debug(methodName, PretupsI.ENTERED+": p_touserID=" + p_toUserID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelTransferItemsVO transferItemsVO = null;
        final ArrayList transferItemsList = new ArrayList();

        final StringBuffer strBuff = new StringBuffer("SELECT U.network_code, U.network_code_for, ");
        strBuff.append("U.product_code, U.balance, U.prev_balance, U.last_transfer_type, U.last_transfer_no,");
        strBuff.append("U.last_transfer_on,P.unit_value,U.daily_balance_updated_on,P.short_name FROM user_balances U,products P  ");
        strBuff.append("WHERE user_id=? AND U.product_code=P.product_code AND P.product_code= ? ");
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
            strBuff.append(" and U.balance_type=? ");
        }

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_toUserID);
            pstmtSelect.setString(2,p_productCode);
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                pstmtSelect.setString(3, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
            }
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                transferItemsVO = new ChannelTransferItemsVO();
                // transferItemsVO.setSerialNum(++k);
                transferItemsVO.setProductCode(rs.getString("product_code"));
                transferItemsVO.setRequiredQuantity(_autoFOCAmount);
                transferItemsVO.setRequestedQuantity(String.valueOf(_autoFOCAmount));
                transferItemsVO.setApprovedQuantity(_autoFOCAmount);
                transferItemsVO.setUnitValue(rs.getLong("unit_value"));
                transferItemsVO.setNetworkCode(rs.getString("network_code"));
                transferItemsVO.setShortName(rs.getString("short_name"));
                transferItemsVO.setReceiverCreditQty(_autoFOCAmount);
                transferItemsList.add(transferItemsVO);
            }
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTransferDAO[getTransferlistForAutoFOC]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "parentBalanceUpdateValue", "error.general.sql.processing");
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTransferDAO[getTransferlistForAutoFOC]",
                "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "parentBalanceUpdateValue", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: transferItemsList size =" + transferItemsList.size());
            }
        }
        return transferItemsList;
    }

    /**
     * Method parentBalanceUpdateValue.
     * 
     * @param p_con
     * @param p_toParentID
     *            String
     * @return ArrayList
     * @author nilesh.kumar
     * @throws BTSLBaseException
     */
    public ArrayList parentBalanceUpdateValue(Connection p_con, String p_toParentID, Long _autoC2CAmount) throws BTSLBaseException {
        final String methodName = "parentBalanceUpdateValue";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_toParentID=" + p_toParentID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ChannelTransferItemsVO transferItemsVO = null;
        final ArrayList transferItemsVOList = new ArrayList();

        final StringBuffer strBuff = new StringBuffer("SELECT U.network_code, U.network_code_for, ");
        strBuff.append("U.product_code, U.balance, U.prev_balance, U.last_transfer_type, U.last_transfer_no,");
        strBuff.append("U.last_transfer_on,P.unit_value,U.daily_balance_updated_on,P.short_name FROM user_balances U,products P  ");
        strBuff.append("WHERE user_id=? AND U.product_code=P.product_code");
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
            strBuff.append(" and U.balance_type=? ");
        }
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_toParentID);
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                pstmtSelect.setString(2, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
            }
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                if (log.isDebugEnabled()) {
                    log.debug("transferChannelUser", "Before transferItemsVO construction");
                }
                transferItemsVO = new ChannelTransferItemsVO();
                // transferItemsVO.setSerialNum(++k);
                transferItemsVO.setProductCode(rs.getString("product_code"));
                transferItemsVO.setRequiredQuantity(_autoC2CAmount);
                transferItemsVO.setRequestedQuantity(String.valueOf(_autoC2CAmount));
                transferItemsVO.setApprovedQuantity(_autoC2CAmount);
                transferItemsVO.setUnitValue(rs.getLong("unit_value"));
                transferItemsVO.setNetworkCode(rs.getString("network_code"));
                transferItemsVO.setShortName(rs.getString("short_name"));
                transferItemsVOList.add(transferItemsVO);
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "transferItemsVO " + transferItemsVO);
                }

            }
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTransferDAO[parentBalanceUpdateValue]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserTransferDAO[parentBalanceUpdateValue]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: geoList size =" + transferItemsVOList.size());
            }
        }
        return transferItemsVOList;
    }

  /**
     * ChannelUserTransferDAO.java
     * @param p_con
     * @param p_userVO
     * @param _loggedinUserID
     * @return
     * @throws SQLException
     * @throws Exception
     * int
     * akanksha.gupta
     * 01-Sep-2016 3:49:00 pm
     */
    public int addUserTransferInitiate(java.sql.Connection con, ChannelUserVO userVO,String loggedinUserID) throws  BTSLBaseException {
    	   final String methodName = "addUserTransferInitiate";
    	     
        if (log.isDebugEnabled())
            log.debug(CLASSNAME+methodName, PretupsI.ENTERED +" with user_id=" + userVO.getUserID() + " loggedinUserID=" + loggedinUserID);
        PreparedStatement pstmtS = null;
        PreparedStatement pstmtS1 = null;
        PreparedStatement pstmtS2 = null;
        PreparedStatement pstmtS3 = null;
        int count = 0;
        ResultSet rs = null;
        ResultSet rs1 = null;
         try {
        	UserOtpDAO userOtpDAO = new UserOtpDAO();
           String transferRequest = "Select count(1) count from USER_MIGRATION_REQUEST where to_user_id=? and status = ?  and created_on < ? ";
            if (log.isDebugEnabled())
                log.info(CLASSNAME+methodName, " Query transferRequest : " + transferRequest +"CurrentTime : "+ new java.util.Date(System.currentTimeMillis()-1000*((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_TIMEOUT_INSEC))).intValue()));
            pstmtS = con.prepareStatement(transferRequest);
           pstmtS.setString(1, userVO.getUserID());
            pstmtS.setString(2, TypesI.YES);
            pstmtS.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(new java.util.Date(System.currentTimeMillis()-1000*((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_TIMEOUT_INSEC))).intValue())));
            
            rs = pstmtS.executeQuery();

            if (rs.next() && rs.getInt("count") > 0) {
                count = 1;
            }
            String updateUserTransfer = "update USER_MIGRATION_REQUEST set status =? ,invalid_otp_count= ?, created_on=? ,created_by=?, modified_on=? ,modified_by=? ,from_user_id = ?,otp = ? where to_user_id=? and status = ? ";
    		
        	if (log.isDebugEnabled())
    			log.debug(CLASSNAME+methodName, " Query updateUserTransfer :" + updateUserTransfer+"CurrentTime : "+ new java.util.Date(System.currentTimeMillis()-1000*((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_TIMEOUT_INSEC))).intValue()));
    	
            if (count > 0) {
            	int otpcount = userOtpDAO.generateSendOTP(con, userVO,PretupsI.CHANNEL_USER_TRANSFER);
            	if(otpcount>0)
            	{
            		int i = 1;
            		pstmtS1 = con.prepareStatement(updateUserTransfer);
            		pstmtS1.setString(i++, TypesI.YES);
            		pstmtS1.setInt(i++, 0);
            		pstmtS1.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
            		pstmtS1.setString(i++, loggedinUserID);
            		pstmtS1.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
            		pstmtS1.setString(i++, loggedinUserID);
            		pstmtS1.setString(i++, loggedinUserID);
            		pstmtS1.setString(i++, BTSLUtil.encryptText(userVO.getOTP()));
            		pstmtS1.setString(i++, userVO.getUserID());
            		pstmtS1.setString(i++, TypesI.YES);
            		int updateCount = pstmtS1.executeUpdate();
            		if (updateCount > 0) {
            			count = updateCount;
            			con.commit();

            		}

            	}  
            	else{
        		    throw new BTSLBaseException(this, CLASSNAME+methodName, PretupsErrorCodesI.CHANNEL_USER_TRANSFER_CANNOT_INITIATED,new String[]{userVO.getMsisdn()}); 
        		}
            } else {
            	
            	
                String alreadyInitiatedRequest = "Select count(1) count from USER_MIGRATION_REQUEST where to_user_id=? and status = ?  and created_on > ? ";
                if (log.isDebugEnabled())
                    log.info(CLASSNAME+methodName, " Query transferRequest : " + alreadyInitiatedRequest);
                pstmtS2 = con.prepareStatement(alreadyInitiatedRequest);
                pstmtS2.setString(1, userVO.getUserID());
                pstmtS2.setString(2, TypesI.YES);
                pstmtS2.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(new java.util.Date(System.currentTimeMillis()-1000*((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_TIMEOUT_INSEC))).intValue())));
                rs1 = pstmtS2.executeQuery();

                
                
                if (rs1.next() && rs1.getInt("count") > 0) {
                    count = 1;
                }
            	
            	if(count > 0 )
            	{
            		  throw new BTSLBaseException(this, CLASSNAME+methodName, PretupsErrorCodesI.CHANNEL_USER_TRANSFER_ALREADY_INITIATED ,new String[]{userVO.getMsisdn()});
            	}
            	else
            	{
            		int otpcount = userOtpDAO.generateSendOTP(con, userVO,PretupsI.CHANNEL_USER_TRANSFER);
            		if(otpcount>0)
            		{
            			int i = 1;
            			String insertUserTransfer = "insert into user_migration_request(from_user_id, to_user_id, otp, status,invalid_otp_count, created_on, created_by, modified_on, modified_by) values (?,?,?,?,?,?,?,?,?)";
            			if (log.isDebugEnabled())
            				log.debug(CLASSNAME+methodName, " Query insertUserTransfer :" + insertUserTransfer);
            			pstmtS3 = con.prepareStatement(insertUserTransfer);
            			pstmtS3.setString(i++, loggedinUserID);
            			pstmtS3.setString(i++, userVO.getUserID());
            			pstmtS3.setString(i++, BTSLUtil.encryptText(userVO.getOTP()));
            			pstmtS3.setString(i++, TypesI.YES);
            			pstmtS3.setInt(i++, 0);
                		pstmtS3.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
            			pstmtS3.setString(i++, loggedinUserID);
            			pstmtS3.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
            			pstmtS3.setString(i++, loggedinUserID);
            			int insertCount = pstmtS3.executeUpdate();
            			if (insertCount > 0) {
            				count= insertCount;
            				con.commit();
            			}
            		}
            		else{
            		    throw new BTSLBaseException(this, CLASSNAME+methodName, PretupsErrorCodesI.CHANNEL_USER_TRANSFER_CANNOT_INITIATED); 
            		}
            	}
            }

            if (log.isDebugEnabled())
                log.debug(CLASSNAME+methodName, " update user_otp for user id=" + userVO.getUserID());
        } catch (SQLException|BTSLBaseException sqe) {
            log.error(CLASSNAME+methodName, PretupsI.BTSLEXCEPTION + sqe);
            log.errorTrace(CLASSNAME+methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this,CLASSNAME+methodName,sqe.getMessage());
        }
        catch (Exception ex) {
            log.error(CLASSNAME+methodName, PretupsI.EXCEPTION + ex);
            log.errorTrace(CLASSNAME+methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, CLASSNAME+methodName, ex.getMessage());
        } finally {
        	try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        	try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        	try {
                if (pstmtS != null)
                    pstmtS.close();
            } catch (Exception ex) {
                log.error(CLASSNAME+methodName, PretupsI.EXCEPTION +" : in closing preparedstatement for Update" + ex);
            }
            try {
                if (pstmtS1 != null)
                    pstmtS1.close();
            } catch (Exception ex) {
                log.error(CLASSNAME+methodName, PretupsI.EXCEPTION +" : in closing preparedstatement for Update" + ex);
            }
            try {
                if (pstmtS2 != null)
                    pstmtS2.close();
            } catch (Exception ex) {
                log.error(CLASSNAME+methodName, PretupsI.EXCEPTION +" : in closing preparedstatement for Update" + ex);
            }
            try {
                if (pstmtS3 != null)
                    pstmtS3.close();
            } catch (Exception ex) {
                log.error(CLASSNAME+methodName, PretupsI.EXCEPTION +" : in closing preparedstatement for Update" + ex);
            }
            
        }
        if (log.isDebugEnabled())
            log.debug(CLASSNAME+methodName, PretupsI.EXITED+" count=" + count);
        return count;
    }
   


    /**
     * ChannelUserTransferDAO.java
     * @param p_con
     * @param p_parentGraphDomainCode
     * @param p_categoryCode
     * @param p_domainCode
     * @param p_statusUsed
     * @param p_status
     * @param p_loggedinUserID
     * @return
     * @throws BTSLBaseException
     * ArrayList
     * akanksha.gupta
     * 01-Sep-2016 3:49:23 pm
     */
    public List loadInitiatedUserListForUserTransfer(Connection con, String parentGraphDomainCode, String categoryCode, String domainCode, String statusUsed, String status,String ploggedinUserID) throws BTSLBaseException {
        final String methodName = "#loadInitiatedUserListForUserTransfer";
        if (log.isDebugEnabled()) {
            log.debug(
            		CLASSNAME+methodName,
            		PretupsI.ENTERED+"  p_parentGraphDomainCode=" + parentGraphDomainCode + " p_categoryCode=" + categoryCode + ",p_domainCode=" + domainCode + ",p_stausUsed=" + statusUsed + ",p_staus=" + status+",ploggedinUserID"+ploggedinUserID);
        }
        final List<ChannelUserTransferVO> list = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
           
          String sqlSelect = channelUserTransferQry.createStatementForLloadInitiatedUserListForUserTransferQry(status, statusUsed);
          if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
          }
          pstmt = con.prepareStatement(sqlSelect);
          int i = 1;
          pstmt.setString(i++,  categoryCode);
          pstmt.setString(i++, domainCode);
          pstmt.setString(i++, ploggedinUserID);
          pstmt.setString(i++, ploggedinUserID);
          pstmt.setString(i++, TypesI.YES);
          pstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date(System.currentTimeMillis()-1000*((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_TIMEOUT_INSEC))).intValue())));
          pstmt.setString(i++, parentGraphDomainCode);
          rs = pstmt.executeQuery();
          ChannelUserTransferVO userTransferVO = null;
            while (rs.next()) {
            	userTransferVO = new ChannelUserTransferVO();
            	userTransferVO.setUserID(rs.getString("user_id"));
            	userTransferVO.setUserName(rs.getString("user_name"));
            	userTransferVO.setFromOwnerID(rs.getString("owner_name"));
            	userTransferVO.setFromParentID(rs.getString("parent_name"));
            	userTransferVO.setDomainCode(domainCode);
            	userTransferVO.setUserCategoryCode(categoryCode);
            	userTransferVO.setCategoryName(rs.getString("category_name"));
            	userTransferVO.setCreatedBy(rs.getString("created_by"));
            	userTransferVO.setCreatedOn(rs.getTimestamp("created_on"));
            	userTransferVO.setMsisdn(rs.getString("msisdn"));
            	userTransferVO.setNetworkCode(rs.getString("network_code"));
            	userTransferVO.setStatus(rs.getString("status"));
            	userTransferVO.setZoneCode(parentGraphDomainCode);
            	userTransferVO.setLoginId(rs.getString("login_id"));
            	list.add(userTransferVO);
            }

        } catch (SQLException sqe) {
            log.error(CLASSNAME+methodName, PretupsI.SQLEXCEPTION + sqe);
            log.errorTrace(CLASSNAME+methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, CLASSNAME+methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(CLASSNAME+methodName, PretupsI.EXCEPTION+ ex);
            log.errorTrace(CLASSNAME+methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,CLASSNAME+methodName, "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, CLASSNAME+methodName, "error.general.processing");
        } finally {
				try {
					if(rs!=null)
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					if(pstmt!=null)
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
        	if (log.isDebugEnabled()) {
        		log.debug(CLASSNAME+methodName, PretupsI.EXITED+"userList size=" + list.size());
        	}
        }
        return list;
    }

    
	/**
     * ChannelUserTransferDAO.java
     * @param p_con
     * @param p_msisdn
     * @param p_statusUsed
     * @param p_status
     * @param p_loggedinUserID
     * @return
     * @throws BTSLBaseException
     * ArrayList
     * akanksha.gupta
     * 01-Sep-2016 3:48:49 pm
     */
    public List<ChannelUserTransferVO> loadInitiatedUserbyMsisdnForUserTransfer(Connection con, String msisdn, String statusUsed, String status,String ploggedinUserID) throws BTSLBaseException {
        final String methodName = "#loadInitiatedUserbyMsisdnForUserTransfer";
        if (log.isDebugEnabled()) {
            log.debug(
                CLASSNAME+methodName,
                PretupsI.ENTERED +"  p_msisdn=" + msisdn + ",p_stausUsed=" + statusUsed + ",p_staus=" + status+",ploggedinUserID"+ploggedinUserID);
        }
        final List<ChannelUserTransferVO> list = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
            try
            {
            	final StringBuilder strBuff = new StringBuilder();
                strBuff.append("select u.user_name, u.user_id,c.domain_code, c.category_code, u.login_id,up.msisdn,umg.created_on,umg.created_by,pu.USER_NAME parent_name,ou.USER_NAME owner_name ");
                strBuff.append(", c.category_name, u.status,u.network_code,umg.created_on ");
                strBuff.append("FROM users u, user_geographies ug, categories c,user_migration_request umg,users pu,users ou,user_phones up ");
                strBuff.append("WHERE u.user_type ='CHANNEL' ");
                  if ((status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
                    strBuff.append(" AND u.barred_deletion_batchid IS NULL ");
                }
                // end
                if (statusUsed.equals(PretupsI.STATUS_IN)) {
                    strBuff.append("AND u.status IN (" + status + ")");
                }
                strBuff.append("AND umg.TO_USER_ID= u.USER_ID  AND u.parent_id = pu.user_id  AND u.owner_id = ou.user_id AND u.user_id = up.USER_ID AND up.PRIMARY_NUMBER='Y' ");
                strBuff.append("AND u.user_id = ug.user_id ");
                strBuff.append("AND u.category_code=c.category_code ");
                strBuff.append("AND umg.FROM_USER_ID=?  AND umg.created_by = ? AND umg.status= ? AND u.user_code = ? and umg.created_on > ? ");
                
           	     strBuff.append("ORDER BY user_name ");

                final String sqlSelect = strBuff.toString();
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
                }
               
                pstmt = con.prepareStatement(sqlSelect);
               	int i = 1;
                pstmt.setString(i++, ploggedinUserID);
                pstmt.setString(i++, ploggedinUserID);
                pstmt.setString(i++, TypesI.YES);
                pstmt.setString(i++, msisdn);
                pstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date(System.currentTimeMillis()-1000*((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_TIMEOUT_INSEC))).intValue())));
                rs = pstmt.executeQuery();
            	ChannelUserTransferVO userTransferVO = null;
            	while (rs.next()) {
            		userTransferVO = new ChannelUserTransferVO();
            		userTransferVO.setUserID(rs.getString("user_id"));
            		userTransferVO.setUserName(rs.getString("user_name"));
            		userTransferVO.setFromOwnerID(rs.getString("owner_name"));
            		userTransferVO.setFromParentID(rs.getString("parent_name"));
            		userTransferVO.setDomainCode(rs.getString("domain_code"));
            		userTransferVO.setUserCategoryCode(rs.getString("category_code"));
            		userTransferVO.setCategoryName(rs.getString("category_name"));
            		userTransferVO.setCreatedBy(rs.getString("created_by"));
            		userTransferVO.setCreatedOn(rs.getTimestamp("created_on"));
            		userTransferVO.setMsisdn(rs.getString("msisdn"));
            		userTransferVO.setNetworkCode(rs.getString("network_code"));
            		userTransferVO.setStatus(rs.getString("status"));
            		userTransferVO.setLoginId(rs.getString("login_id"));
            		list.add(userTransferVO);
            	}
            
        } catch (SQLException sqe) {
            log.error(methodName, PretupsI.SQLEXCEPTION + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, CLASSNAME+methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(CLASSNAME+methodName, PretupsI.EXCEPTION + ex);
            log.errorTrace(CLASSNAME+methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, CLASSNAME+methodName, "error.general.processing");
        } finally {
        	try {
				if(rs!=null)
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	
			try {
				if(pstmt!=null)
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
            if (log.isDebugEnabled() ) {
            	if(list!=null)
            			log.debug(CLASSNAME+methodName, PretupsI.EXITED+" userList size=" + list.size());
            }
        }
        return list;
    }
    

/**
 * ChannelUserTransferDAO.java
 * @param con
 * @param p_userID
 * @param p_loggedinuserID
 * @param p_otp
 * @param statusIn
 * @param status
 * @return
 * @throws BTSLBaseException
 * ChannelUserTransferVO
 * akanksha.gupta
 * 01-Sep-2016 3:48:43 pm
 */
public ChannelUserTransferVO loadInitiatedUserListForSelectedRecord(Connection con, String userID, String ploggedinuserID,
		String otp, String statusIn, String status) throws BTSLBaseException {
    final String methodName = "#loadInitiatedUserListForSelectedRecord";
    ChannelUserTransferVO userTransferVO = null;
    
    if (log.isDebugEnabled()) {
        log.debug(
            methodName,
            PretupsI.ENTERED+"   p_userID=" + userID + "  ploggedinuserID=" + ploggedinuserID + ",p_otp=" + otp + ",statusIn=" + statusIn + ",status=" + status);
    }
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    	try{
    		final StringBuilder strBuff = new StringBuilder();
    		strBuff.append("select u.user_name, u.user_id, u.login_id,up.msisdn,umg.created_on,umg.created_by,umg.otp,pu.USER_NAME parent_name,ou.USER_NAME owner_name,u.category_code ,c.domain_code,ug.grph_domain_code ");
    		strBuff.append(", c.category_name, u.status,u.network_code,umg.invalid_otp_count ");
    		strBuff.append("FROM users u, user_geographies ug,user_migration_request umg,users pu,users ou,user_phones up ,categories c ");
    		strBuff.append("WHERE u.user_type ='CHANNEL' ");
    		if ((status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST)) || (status.contains(PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE))) {
    			strBuff.append(" AND u.barred_deletion_batchid IS NULL ");
    		}
    		// end
    		if (statusIn.equals(PretupsI.STATUS_IN)) {
    			strBuff.append("AND u.status IN (" + status + ")");
    		} 
    		strBuff.append("AND u.category_code=c.category_code ");
    		strBuff.append("AND umg.TO_USER_ID= ? AND u.USER_ID= ?  AND u.parent_id = pu.user_id  AND u.owner_id = ou.user_id AND u.user_id = up.USER_ID AND up.PRIMARY_NUMBER='Y' ");
    		strBuff.append("AND u.user_id = ug.user_id ");
    		strBuff.append("AND umg.FROM_USER_ID=?  AND umg.created_by = ? AND umg.status= ?  ");

    		final String sqlSelect = strBuff.toString();
    		if (log.isDebugEnabled()) {
    			log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
    		}

    			pstmt = con.prepareStatement(sqlSelect);
    			int i = 1;
    			pstmt.setString(i++, userID);
    			pstmt.setString(i++, userID);
    			pstmt.setString(i++, ploggedinuserID);
    			pstmt.setString(i++, ploggedinuserID);
    			pstmt.setString(i++, TypesI.YES);
    			rs = pstmt.executeQuery();
    	     while (rs.next())  {

    			userTransferVO = new ChannelUserTransferVO();
    			userTransferVO.setUserID(rs.getString("user_id"));
    			userTransferVO.setUserName(rs.getString("user_name"));
    			userTransferVO.setFromOwnerID(rs.getString("owner_name"));
    			userTransferVO.setFromParentID(rs.getString("parent_name"));
    			userTransferVO.setParentUserName(rs.getString("parent_name"));
    			userTransferVO.setDomainCode(rs.getString("domain_code"));
    			userTransferVO.setUserCategoryCode(rs.getString("category_code"));
    			userTransferVO.setCategoryName(rs.getString("category_name"));
    			userTransferVO.setCreatedBy(rs.getString("created_by"));
    			userTransferVO.setCreatedOn(rs.getTimestamp("created_on"));
    			userTransferVO.setMsisdn(rs.getString("msisdn"));
    			userTransferVO.setNetworkCode(rs.getString("network_code"));
    			userTransferVO.setStatus(rs.getString("status"));
    			userTransferVO.setZoneCode(rs.getString("grph_domain_code"));
    			userTransferVO.setLoginId(rs.getString("login_id"));
    			userTransferVO.setOtp(BTSLUtil.decryptText(rs.getString("otp")));
    			userTransferVO.setInvalidOtpCount(rs.getInt("invalid_otp_count"));
    			
    			if(!validateOTPUserMigrationReuest(con, userTransferVO,otp,ploggedinuserID ))
    				throw new BTSLBaseException(this, methodName, "usermovement.validateotp.error.otp.expired");
    			
    			
   		}
  		
    	
    }  catch (SQLException|BTSLBaseException be) {
        log.error(methodName, PretupsI.SQLEXCEPTION + be);
        log.errorTrace(methodName, be);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, "", "", "",
            "SQL Exception:" + be.getMessage());
        throw new BTSLBaseException(this, methodName, be.getMessage());
    }  catch (Exception ex) {
        log.error(methodName, "Exception : " + ex);
        log.errorTrace(methodName, ex);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, "", "", "",
            "Exception:" + ex.getMessage());
        throw new BTSLBaseException(this, CLASSNAME+methodName, "error.general.processing");
    } finally {
    	try {
			if(rs!=null)
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		try {
			if(pstmt!=null)
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
       
        if (log.isDebugEnabled()) {
            log.debug(CLASSNAME+methodName, "Exiting: userTransferVO " + userTransferVO);
        }
    }
    return userTransferVO;
}



private boolean validateOTPUserMigrationReuest(Connection con, ChannelUserTransferVO userTransferVO, String otp, String ploggedinuserID) throws BTSLBaseException {

	final String methodName = "#validateOTPUserMigrationReuest";
	if (log.isDebugEnabled()) {
		log.debug(methodName,PretupsI.ENTERED+"userTransferVO=" + userTransferVO+"otp="+otp+"ploggedinuserID="+ploggedinuserID );
	}
	boolean isvalidOTP = false;
	int count = 0;
	int maxOTPValidationFailCount = 0;
	java.util.Date date = new java.util.Date();
	java.sql.Timestamp currenTimestamp = new Timestamp(date.getTime());
	long timeout = (Integer) PreferenceCache.getSystemPreferenceValue("OTP_TIMEOUT_INSEC");
	int invalidOTPCount = userTransferVO.getInvalidOtpCount();

	try {
		maxOTPValidationFailCount = ((Integer) PreferenceCache.getControlPreference(PreferenceI.OTP_AUTHENTICATION_COUNT, userTransferVO.getNetworkCode(), userTransferVO.getUserCategoryCode())).intValue();
	} catch (Exception e) {
		log.error(methodName, PretupsI.EXCEPTION + e);

	}
	if (log.isDebugEnabled()) {
		log.debug(methodName, "Max RSA validation fail count=" + maxOTPValidationFailCount);
	}
	if (log.isDebugEnabled())
		log.info(methodName, " Query cretaed on : " +userTransferVO.getCreatedOn()+"-"+userTransferVO.getCreatedOn().getTime() +"CurrentTime :"+date+"-"+ currenTimestamp.getTime()+",timeout:"+timeout);
	try {
		if ((currenTimestamp.getTime() - userTransferVO.getCreatedOn().getTime()) >= timeout*1000) {
			userTransferVO.setStatus(PretupsI.NO);
			count = updateUserMigrationRequestStatus(con, userTransferVO ,ploggedinuserID);
			isvalidOTP = false;
			throw new BTSLBaseException(this, methodName, "usermovement.validateotp.error.otp.expired");  
		} 
		else if (invalidOTPCount >= maxOTPValidationFailCount) {

			userTransferVO.setStatus(PretupsI.NO);
			count = updateUserMigrationRequestStatus(con, userTransferVO ,ploggedinuserID);
			isvalidOTP = false;
			throw new BTSLBaseException("usermovement.validateotp.error.otp.invalid.maxcount", "index");
			
		} else if (!otp.equals(userTransferVO.getOtp())){

			userTransferVO.setInvalidOtpCount(invalidOTPCount+1);
			count = updateUserMigrationRequestStatus(con, userTransferVO ,ploggedinuserID);
			isvalidOTP = false;
			throw new BTSLBaseException(this, methodName, "usermovement.validateotp.error.otp.incorrect");

		}
		else{
			isvalidOTP = true;
		}
		
		return isvalidOTP;
		
	} catch (BTSLBaseException e) {
		  log.error(methodName, PretupsI.SQLEXCEPTION + e);
	        log.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, "", "", "",
	            PretupsI.EXCEPTION + e.getMessage());
	        throw new BTSLBaseException(this, methodName, e.getMessage());
	    }
	finally{
		try {
		if(count>0){
			if(con!=null)
				con.commit();}
		else if(con !=null)
			con.rollback();		
		}
		 catch (SQLException e) {
				log.error(CLASSNAME+methodName,"Error during closing connection.");
				 log.errorTrace(methodName, e);
			}
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName,PretupsI.EXITED);
		}
		
	}
	
	
}


/**
 * ChannelUserTransferDAO.java
 * @param con
 * @param p_userID
 * @param p_loggedinuserID
 * @param p_otp
 * @return
 * @throws BTSLBaseException
 * int
 * akanksha.gupta
 * 01-Sep-2016 3:48:37 pm
 */
public int updateTransferedUserData(Connection con, String userID, String ploggedinuserID,
		String otp,ChannelUserTransferVO userTransferVO) throws BTSLBaseException {
	final String methodName = "#updateTransferedUserData";
	if (log.isDebugEnabled()) {
		log.debug(
				CLASSNAME+methodName,
				"Entered  p_userID=" + userID + " ploggedinuserID=" + ploggedinuserID + ",p_otp=" + otp +",channelUserTransferVO"+userTransferVO);
	}

	int updateCount =0;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	try {
		final StringBuilder selectBuff = new StringBuilder();
		selectBuff.append("select umg.created_on,umg.otp,umg.invalid_otp_count,u.network_code,u.category_code from user_migration_request umg,users u where umg.to_user_id= ? and umg.status = ? and umg.from_user_id =? and umg.to_user_id=u.user_id");
		final String sqlSelect = selectBuff.toString();
		int i = 1;
		pstmt = con.prepareStatement(sqlSelect);
		pstmt.setString(i++, userID);
		pstmt.setString(i++, PretupsI.YES);
		pstmt.setString(i++, ploggedinuserID);
		rs = pstmt.executeQuery();
		if (rs.next()) {
			userTransferVO.setCreatedOn(rs.getTimestamp("created_on"));
			userTransferVO.setNetworkCode(rs.getString("network_code"));
			userTransferVO.setUserCategoryCode(rs.getString("category_code"));  
			userTransferVO.setOtp(BTSLUtil.decryptText(rs.getString("otp")));
			userTransferVO.setInvalidOtpCount(Integer.parseInt(rs.getString("invalid_otp_count")));

			if(validateOTPUserMigrationReuest(con, userTransferVO,otp,ploggedinuserID ))   
			{
			ChannelUserTransferWebDAO  channelUserTransferwebDAO = new ChannelUserTransferWebDAO();
			int updateCount1 = channelUserTransferwebDAO.transferChannelUser(con, userTransferVO);
			if(	updateCount1 >0){
				userTransferVO.setStatus( PretupsI.USER_APPROVE);
				userTransferVO.setInvalidOtpCount(0);
				userTransferVO.setUserID(userID);
				updateCount = updateUserMigrationRequestStatus(con, userTransferVO ,ploggedinuserID);
			if (updateCount > 0 && con!=null) {
    				con.commit();
				} 
			else if(updateCount<=0)
				throw new BTSLBaseException(this, CLASSNAME+methodName, "error.general.sql.processing");
	
				}
			}
			else
				throw new BTSLBaseException(this, CLASSNAME+methodName,"usermovement.validateotp.error.otp.expired" );
			
		}
		
		
	}

	catch (SQLException|BTSLBaseException be) {
		log.error(CLASSNAME+methodName, PretupsI.SQLEXCEPTION + be);
		log.errorTrace(CLASSNAME+methodName, be);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, "", "", "",
				PretupsI.SQLEXCEPTION + be.getMessage());
		throw new BTSLBaseException(this, CLASSNAME+methodName, be.getMessage());
	} 
	catch (Exception ex) {
		log.error(CLASSNAME+methodName, PretupsI.EXCEPTION + ex);
		log.errorTrace(CLASSNAME+methodName, ex);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+methodName, "", "", "",
				PretupsI.EXCEPTION + ex.getMessage());
		throw new BTSLBaseException(this, CLASSNAME+methodName, "error.general.processing");
	} finally {
		try {
			if(rs!=null)
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		try {
			if(pstmt!=null)
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.EXITED+" updateCount=" + updateCount);
		}
	}
	return updateCount;
}

// added for channel user transfer
    public ArrayList<GeographicalDomainVO> loadGeogphicalHierarchyListByToParentId(Connection p_con,String p_toParentID) throws BTSLBaseException
	{
	    	final String methodName = "loadGeogphicalHierarchyListByToParentId";
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.ENTERED+" p_toParentID="+p_toParentID);
			}
	    	PreparedStatement pstmtSelect = null;
	    	ResultSet rs = null;
	    	GeographicalDomainVO geoGraphicaldomainVO=null;
	    	ArrayList<GeographicalDomainVO> geoList=new ArrayList();
	    	
	    	try
	    	{
	    		pstmtSelect= channelUserTransferQry.loadGeogphicalHierarchyListByToParentIdQry(p_con,p_toParentID);
	    	    rs = pstmtSelect.executeQuery();
	    	    while(rs.next())
	    	    {
	    	    	geoGraphicaldomainVO = new GeographicalDomainVO(); 
	    	    	geoGraphicaldomainVO.setGrphDomainCode(rs.getString("grph_domain_code"));
	    	    	geoGraphicaldomainVO.setNetworkCode(rs.getString("network_code"));
	    	    	geoGraphicaldomainVO.setGrphDomainName(rs.getString("grph_domain_name"));
	    	    	geoGraphicaldomainVO.setParentDomainCode(rs.getString("parent_grph_domain_code"));
	    	    	geoGraphicaldomainVO.setGrphDomainType(rs.getString("grph_domain_type"));
	    	       
	    	    	geoList.add(geoGraphicaldomainVO);
	    	    }
	    	} 
	    	catch (SQLException sqe)
	    	{
	    		log.errorTrace(methodName, sqe);
	    	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserTransferDAO[loadGeogphicalHierarchyListByToParentId]","","","","SQL Exception:"+sqe.getMessage());
	    	    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	    	} 
	    	catch (Exception ex)
	    	{
	    		log.errorTrace(methodName, ex);
	    	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserTransferDAO[loadGeogphicalHierarchyListByToParentId]","","","","Exception:"+ex.getMessage());
	    	    throw new BTSLBaseException(this, methodName, "error.general.processing");
	    	}
	    	finally
	    	{
	    		try {
	    			if (rs != null) {
	    				rs.close();
	    			}
	    		} catch (Exception e) {
	    			log.errorTrace(methodName, e);
	    		}
	    		try {
	    			if (pstmtSelect != null) {
	    				pstmtSelect.close();
	    			}
	    		} catch (Exception e) {
	    			log.errorTrace(methodName, e);
	    		}
	    		if (log.isDebugEnabled()) {
	    			log.debug(methodName,PretupsI.EXITED+": geoList size =" + geoList.size());
	    		}
			}
	    	return geoList;
	}
    // added for channel user transfer
    public int transferChannelUser(Connection p_con,ChannelUserTransferVO p_channelUserTransferVO) throws BTSLBaseException
	{
		final String methodName = "transferChannelUser";
		if(log.isDebugEnabled()) {
			log.debug(methodName,PretupsI.ENTERED+" p_channelUserTransferVO="+p_channelUserTransferVO);
		}
		int addCount=0;
		ArrayList userList = p_channelUserTransferVO.getUserHierarchyList();
		
		PreparedStatement pstmtUpdateUser= null;
		PreparedStatement pstmtInsertUser= null;
		PreparedStatement pstmtSelectUserBalance= null;
		ResultSet rsSelectUserBalance = null;
		PreparedStatement pstmtUpdateUserBalance= null;
		PreparedStatement pstmtInsertUserBalance= null;
		
		PreparedStatement pstmtSelectUserDomains= null;
		ResultSet rsSelectUserDomains= null;
		PreparedStatement pstmtInsertUserDomains= null;
		
		PreparedStatement pstmtSelectUserGeographies= null;
		ResultSet rsSelectUserGeographies= null;
		PreparedStatement pstmtInsertUserGeographies= null;
		
		PreparedStatement pstmtSelectUserPhones= null;
		ResultSet rsSelectUserPhones= null;
		PreparedStatement pstmtInsertUserPhones= null;
		
		PreparedStatement pstmtSelectUserProductTypes= null;
		ResultSet rsSelectUserProductTypes= null;
		PreparedStatement pstmtInsertUserProductTypes= null;
		
		PreparedStatement pstmtSelectUserRoles= null;
		ResultSet rsSelectUserRoles= null;
		PreparedStatement pstmtInsertUserRoles= null;
    	
		PreparedStatement pstmtSelectUserServices=null;
		ResultSet rsSelectUserServices= null;
		PreparedStatement pstmtInsertUserServices= null;
		
		PreparedStatement pstmtSelectUserTransferCounts= null;
    	ResultSet rsSelectUserTransferCounts= null;
		PreparedStatement pstmtInsertUserTransferCounts= null;
		
		PreparedStatement pstmtSelectChannelUserInfo= null;
    	ResultSet rsSelectChannelUserInfo= null;
    	PreparedStatement pstmtInsertChannelUserInfo= null;
    	//added by manisha
		PreparedStatement pstmtSelectStaff= null;
		PreparedStatement pstmtupdateStaffPhones= null;
		ChannelUserVO channelUserVO = null;
		PreparedStatement pstmtupdateTransferRules= null;		
		try{
			//Query to update old user
			StringBuilder updateUserBuff =new StringBuilder("UPDATE users SET user_code=?,reference_id=?,status=?, ");
			updateUserBuff.append("modified_by=?, modified_on=?,login_id=?,to_moved_user_id=? WHERE user_id=?");
	    	pstmtUpdateUser = p_con.prepareStatement(updateUserBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"updateUser query="+updateUserBuff);
			}
			
			StringBuilder insertUserBuff =new StringBuilder("INSERT INTO users(user_id, user_name, network_code, ");
			insertUserBuff.append("login_id, password, category_code, parent_id, owner_id, allowed_ip, allowed_days,");
			insertUserBuff.append("from_time, to_time, last_login_on, employee_code, status, email,company,fax, pswd_modified_on,");   //company,fax added by deepika aggarwal
			insertUserBuff.append("contact_person, contact_no, designation, division, department, msisdn, user_type, ");
			insertUserBuff.append("created_by, created_on, modified_by, modified_on, ");
			insertUserBuff.append("address1, address2, city, state, country, ssn, user_name_prefix, external_code, ");
			insertUserBuff.append("user_code, short_name, reference_id, invalid_password_count, level1_approved_by, ");
			insertUserBuff.append("level1_approved_on, level2_approved_by, level2_approved_on, appointment_date, ");
			insertUserBuff.append("password_count_updated_on,previous_status,firstname,lastname) ");//firstname,lastname added by deepika aggarwal //by Naveen
			insertUserBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
			pstmtInsertUser =p_con.prepareStatement(insertUserBuff.toString());
	    	if(log.isDebugEnabled()) {
				log.debug(methodName,"insertUser query="+insertUserBuff);
			}
			
			//ends here
			
			//Query for user balance updation
	    	StringBuilder selectUserBalanceBuff =new StringBuilder("SELECT network_code, network_code_for, ");
			selectUserBalanceBuff.append("U.product_code, balance, prev_balance, last_transfer_type, last_transfer_no,");
			selectUserBalanceBuff.append("last_transfer_on,P.unit_value,daily_balance_updated_on FROM user_balances U,products P  ");
			selectUserBalanceBuff.append("WHERE user_id=? AND U.product_code=P.product_code");
			pstmtSelectUserBalance = p_con.prepareStatement(selectUserBalanceBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"selectUserBalance query="+selectUserBalanceBuff);
			}
						
			StringBuilder updateUserBalanceBuff =new StringBuilder("UPDATE user_balances SET balance=?, prev_balance=? ");
			updateUserBalanceBuff.append("WHERE user_id=? AND product_code=? ");
			pstmtUpdateUserBalance = p_con.prepareStatement(updateUserBalanceBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"updateUserBalance query="+updateUserBalanceBuff);
			}
			
			StringBuilder insertUserBalanceBuff =new StringBuilder("INSERT INTO user_balances(user_id, network_code, ");
			insertUserBalanceBuff.append("network_code_for, product_code, balance, prev_balance, last_transfer_type,");
			insertUserBalanceBuff.append("last_transfer_no, last_transfer_on) VALUES(?,?,?,?,?,?,?,?,?)");
			pstmtInsertUserBalance = p_con.prepareStatement(insertUserBalanceBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"insertUserBalance query="+insertUserBalanceBuff);
			//ends here
			}
			
			StringBuilder selectUserDomainsBuff =new StringBuilder("SELECT domain_code FROM user_domains WHERE user_id = ?");
			pstmtSelectUserDomains= p_con.prepareStatement(selectUserDomainsBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"selectUserDomains query="+selectUserDomainsBuff);
			}
			
			StringBuilder insertUserDomainsBuff =new StringBuilder("INSERT INTO user_domains(user_id ,domain_code) ");
			insertUserDomainsBuff.append("VALUES(?,?)");
			pstmtInsertUserDomains= p_con.prepareStatement(insertUserDomainsBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"insertUserDomains query="+insertUserDomainsBuff);
			//ends here
			}
			
			//Query for user Geographics transfer
			StringBuilder selectUserGeographiesBuff =new StringBuilder("SELECT grph_domain_code, application_id FROM user_geographies ");
			selectUserGeographiesBuff.append("WHERE user_id = ?");
			pstmtSelectUserGeographies= p_con.prepareStatement(selectUserGeographiesBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"selectUserGeographies query="+selectUserGeographiesBuff);
			}
			
			StringBuilder insertUserGeographiesBuff =new StringBuilder("INSERT INTO user_geographies(user_id ,");
			
			//for Zebra and Tango By sanjeew  date 05/07/07
			insertUserGeographiesBuff.append(" application_id, ");
			//end Zebra and Tango
			
			insertUserGeographiesBuff.append("grph_domain_code) VALUES(?,?,?)");
			pstmtInsertUserGeographies= p_con.prepareStatement(insertUserGeographiesBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"insertUserGeographies query="+insertUserGeographiesBuff);
			//ends here
			}
			
			//query for user phones updation
			StringBuilder selectUserPhonesBuff =new StringBuilder("SELECT user_phones_id, msisdn, description, ");
			selectUserPhonesBuff.append("primary_number, sms_pin, pin_required, phone_profile, phone_language, ");
			selectUserPhonesBuff.append("country, invalid_pin_count, last_transaction_status, last_transaction_on, ");
			selectUserPhonesBuff.append("pin_modified_on, created_by, created_on, modified_by, modified_on, ");
			selectUserPhonesBuff.append("last_transfer_id, last_transfer_type, prefix_id, temp_transfer_id, ");
			
			//for Zebra and Tango By sanjeew  date 05/07/07
			selectUserPhonesBuff.append(" access_type, from_time, to_time, allowed_days, allowed_ip, last_login_on, ");
			//end Zebra and Tango
			
			//PIN RESET VALUE as same as old
			selectUserPhonesBuff.append(" PIN_RESET,");
			
			selectUserPhonesBuff.append("first_invalid_pin_time FROM user_phones WHERE user_id=?");
			pstmtSelectUserPhones= p_con.prepareStatement(selectUserPhonesBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"selectUserPhones query="+selectUserPhonesBuff);
			}
			
			
			StringBuilder insertUserPhonesBuff =new StringBuilder("INSERT INTO user_phones(user_id, user_phones_id, ");
			insertUserPhonesBuff.append("msisdn, description, primary_number, sms_pin, pin_required, phone_profile, ");
			insertUserPhonesBuff.append("phone_language, country, invalid_pin_count, last_transaction_status, ");
			insertUserPhonesBuff.append("last_transaction_on, pin_modified_on, created_by, created_on, modified_by,");
			insertUserPhonesBuff.append("modified_on, last_transfer_id, last_transfer_type, prefix_id, ");
			insertUserPhonesBuff.append("temp_transfer_id, first_invalid_pin_time,pin_reset, ");

			//for Zebra and Tango By sanjeew  date 05/07/07
			insertUserPhonesBuff.append(" access_type, from_time, to_time, allowed_days, allowed_ip, last_login_on ");
			insertUserPhonesBuff.append(") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			//end Zebra and Tango
			
			pstmtInsertUserPhones= p_con.prepareStatement(insertUserPhonesBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"insertUserPhones query="+insertUserPhonesBuff);
			//ends here
			}
			
			//Query for user product type transfer
			StringBuilder selectUserProductTypesBuff =new StringBuilder("SELECT product_type FROM user_product_types ");
			selectUserProductTypesBuff.append("WHERE user_id = ?");
			pstmtSelectUserProductTypes= p_con.prepareStatement(selectUserProductTypesBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"selectUserProductTypes query="+selectUserProductTypesBuff);
			}
			
			StringBuilder insertUserProductTypesBuff=new StringBuilder("INSERT INTO user_product_types(user_id ,");
			insertUserProductTypesBuff.append("product_type) VALUES(?,?)");
			pstmtInsertUserProductTypes= p_con.prepareStatement(insertUserProductTypesBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"insertUserProductTypes query="+insertUserProductTypesBuff);
			//ends here
			}
			
			//Query for roles transfer
			StringBuilder selectUserRolesBuff =new StringBuilder("SELECT role_code, gateway_types FROM user_roles WHERE user_id = ?");
			pstmtSelectUserRoles= p_con.prepareStatement(selectUserRolesBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"selectUserRoles query="+selectUserRolesBuff);
			}
						
			StringBuilder insertUserRolesBuff=new StringBuilder("INSERT INTO user_roles(user_id ,role_code, gateway_types) VALUES(?,?,?)");
			pstmtInsertUserRoles= p_con.prepareStatement(insertUserRolesBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"insertUserRoles query="+insertUserRolesBuff);
			//ends here
			}
			
			//Query for user services transfer
			//Modification for Service Management [by Vipul]
			StringBuilder selectUserServicesBuff =new StringBuilder("SELECT US.service_type,US.status");
			selectUserServicesBuff.append(" FROM user_services US,users U,category_service_type CST");
			selectUserServicesBuff.append(" WHERE US.user_id=? AND CST.NETWORK_CODE = ? AND U.user_id=US.user_id AND U.category_code=CST.category_code AND CST.service_type=US.service_type ");
			
			pstmtSelectUserServices= p_con.prepareStatement(selectUserServicesBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"selectUserServices query="+selectUserServicesBuff);
			}
			
			StringBuilder insertUserServicesBuff=new StringBuilder("INSERT INTO user_services(user_id,service_type,");
			insertUserServicesBuff.append("status) VALUES(?,?,?)");
			pstmtInsertUserServices= p_con.prepareStatement(insertUserServicesBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"insertUserServices query="+insertUserServicesBuff);
			//ends here
			}
			
			//query for user transfer counts update
			StringBuilder selectUserTransferCountsBuff =new StringBuilder("SELECT daily_in_count, daily_in_value, ");
			selectUserTransferCountsBuff.append("weekly_in_count, weekly_in_value, monthly_in_count, ");
			selectUserTransferCountsBuff.append("monthly_in_value, daily_out_count, daily_out_value, ");
			selectUserTransferCountsBuff.append("weekly_out_count, weekly_out_value, monthly_out_count, ");
			selectUserTransferCountsBuff.append("monthly_out_value, outside_daily_in_count, outside_daily_in_value, ");
			selectUserTransferCountsBuff.append("outside_weekly_in_count, outside_weekly_in_value, ");
			selectUserTransferCountsBuff.append("outside_monthly_in_count, outside_monthly_in_value, ");
			selectUserTransferCountsBuff.append("outside_last_in_time, last_in_time, last_out_time, ");
			selectUserTransferCountsBuff.append("outside_last_out_time, outside_daily_out_count, ");
			selectUserTransferCountsBuff.append("outside_daily_out_value, outside_weekly_out_count, ");
			selectUserTransferCountsBuff.append("outside_weekly_out_value, outside_monthly_out_count, ");
			selectUserTransferCountsBuff.append("outside_monthly_out_value, daily_subscriber_out_count, ");
			selectUserTransferCountsBuff.append("daily_subscriber_out_value, weekly_subscriber_out_count, ");
			selectUserTransferCountsBuff.append("weekly_subscriber_out_value, monthly_subscriber_out_count,");
			selectUserTransferCountsBuff.append("monthly_subscriber_out_value, last_transfer_id, ");
			selectUserTransferCountsBuff.append("last_transfer_date FROM user_transfer_counts WHERE user_id=?");
			pstmtSelectUserTransferCounts= p_con.prepareStatement(selectUserTransferCountsBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"selectUserTransferCounts query="+selectUserTransferCountsBuff);
			}
			
			StringBuilder insertUserTransferCountsBuff=new StringBuilder("INSERT INTO user_transfer_counts(user_id, ");
			insertUserTransferCountsBuff.append("daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, ");
			insertUserTransferCountsBuff.append("monthly_in_count, monthly_in_value, daily_out_count, ");
			insertUserTransferCountsBuff.append("daily_out_value, weekly_out_count, weekly_out_value, ");
			insertUserTransferCountsBuff.append("monthly_out_count, monthly_out_value, outside_daily_in_count, ");
			insertUserTransferCountsBuff.append("outside_daily_in_value, outside_weekly_in_count, ");
			insertUserTransferCountsBuff.append("outside_weekly_in_value, outside_monthly_in_count, ");
			insertUserTransferCountsBuff.append("outside_monthly_in_value, outside_last_in_time, last_in_time, ");
			insertUserTransferCountsBuff.append("last_out_time, outside_last_out_time, outside_daily_out_count, ");
			insertUserTransferCountsBuff.append("outside_daily_out_value, outside_weekly_out_count, ");
			insertUserTransferCountsBuff.append("outside_weekly_out_value, outside_monthly_out_count, ");
			insertUserTransferCountsBuff.append("outside_monthly_out_value, daily_subscriber_out_count, ");
			insertUserTransferCountsBuff.append("daily_subscriber_out_value, weekly_subscriber_out_count, ");
			insertUserTransferCountsBuff.append("weekly_subscriber_out_value, monthly_subscriber_out_count, ");
			insertUserTransferCountsBuff.append("monthly_subscriber_out_value, last_transfer_id, last_transfer_date)");
			insertUserTransferCountsBuff.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmtInsertUserTransferCounts= p_con.prepareStatement(insertUserTransferCountsBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"insertUserTransferCounts query="+insertUserTransferCountsBuff);
			}

			
			//query for user transfer counts update
			StringBuilder selectChannelUserInfoBuff =new StringBuilder("SELECT user_grade, contact_person, ");
			selectChannelUserInfoBuff.append("transfer_profile_id, comm_profile_set_id, in_suspend, out_suspend, ");
			selectChannelUserInfoBuff.append("outlet_code, suboutlet_code ");
			
			//for Zebra and Tango By sanjeew  date 05/07/07
			selectChannelUserInfoBuff.append(" ,activated_on,application_id, mpay_profile_id, user_profile_id, is_primary, mcommerce_service_allow, low_bal_alert_allow ");
			//end Zebra and Tango

			selectChannelUserInfoBuff.append("FROM channel_users ");
			selectChannelUserInfoBuff.append("WHERE user_id=? ");
			pstmtSelectChannelUserInfo= p_con.prepareStatement(selectChannelUserInfoBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"selectChannelUserInfoBuff query="+selectChannelUserInfoBuff);
			}

			StringBuilder insertChannelUserInfoBuff=new StringBuilder("INSERT INTO channel_users(user_id, user_grade, ");
			insertChannelUserInfoBuff.append("contact_person, transfer_profile_id, comm_profile_set_id, in_suspend, ");
			insertChannelUserInfoBuff.append("out_suspend, outlet_code, suboutlet_code, activated_on ");
			
			//for Zebra and Tango By sanjeew  date 05/07/07
			insertChannelUserInfoBuff.append(", application_id, mpay_profile_id, user_profile_id, is_primary, mcommerce_service_allow, low_bal_alert_allow ");
			//end Zebra and Tango
			
			insertChannelUserInfoBuff.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
			//commented for DB2  pstmtInsertChannelUserInfo=(OraclePreparedStatement) p_con.prepareStatement(insertChannelUserInfoBuff.toString());
			pstmtInsertChannelUserInfo=p_con.prepareStatement(insertChannelUserInfoBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"insertChannelUserInfoBuff query="+insertChannelUserInfoBuff);
			}

			//ends here
			
			//added by manisha
			StringBuilder staffUserBuffer= new StringBuilder(" UPDATE users SET parent_id=? , owner_id=?, modified_by=?, modified_on=?, ");
            staffUserBuffer.append("created_by =? , created_on =?  WHERE parent_id=? and user_type=? and status not in('N','C') ");
			if(log.isDebugEnabled()) {
				log.debug(methodName,"staffUserBuffer query="+staffUserBuffer.toString());
			}
			pstmtSelectStaff=p_con.prepareStatement(staffUserBuffer.toString());
			
			StringBuilder staffPhonesBuffer= new StringBuilder(" update user_phones set modified_by=?, modified_on=?, created_by =? , created_on = ? ");
			staffPhonesBuffer.append("where user_id in (select user_id from users where parent_id= ? and user_type= ? and status not in ('N','C'))");
	        if(log.isDebugEnabled()) {
				log.debug(methodName,"staffPhonesBuffer query="+staffPhonesBuffer.toString());
			}
	        pstmtupdateStaffPhones=p_con.prepareStatement(staffPhonesBuffer.toString());
			
			StringBuffer updatePromotionalTransferRulesBuff =new StringBuffer("UPDATE transfer_rules SET SENDER_SUBSCRIBER_TYPE=? ");
	    	updatePromotionalTransferRulesBuff.append("WHERE SENDER_SUBSCRIBER_TYPE=? and RULE_TYPE=?");
	    	pstmtupdateTransferRules = p_con.prepareStatement(updatePromotionalTransferRulesBuff.toString());
			if(log.isDebugEnabled()) {
				log.debug(methodName,"updatePromotionalTransferRules query="+updatePromotionalTransferRulesBuff);
			}
	        if(userList!=null)
			{
				String networkCode=p_channelUserTransferVO.getNetworkCode();
				String newUserID;
				String userIDPrifix;
				int k=0;
				ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
				ChannelTransferVO channelTransferVO=new  ChannelTransferVO();
				ChannelTransferItemsVO transferItemsVO = null;
				ArrayList transferItemsVOList=null;
				HashMap parentKeyMap=new HashMap();
				String parentID;
                CommonUtil commonUtil=new CommonUtil();
                Map oldUserMap=null;
                Map newUserMap=null;
				for(int i=0,j=userList.size();i<j;i++)
				{
					transferItemsVOList=new ArrayList();
					channelUserVO=(ChannelUserVO )userList.get(i);
					userIDPrifix=channelUserVO.getUserIDPrefix();
					newUserID=this.generateUserId(networkCode,userIDPrifix);
					if(log.isDebugEnabled()) {
						log.debug(methodName,"Before starting transfer:::: channelUserVO="+channelUserVO);
					}
						
					//user information updation
					if(log.isDebugEnabled()) {
						log.debug(methodName,"Before updating user information");
					}
					pstmtUpdateUser.setString(1,channelUserVO.getUserID());
					pstmtUpdateUser.setString(2,newUserID);
					pstmtUpdateUser.setString(3,PretupsI.USER_STATUS_DELETED);
					pstmtUpdateUser.setString(4,PretupsI.SYSTEM);
					pstmtUpdateUser.setTimestamp(5,BTSLUtil.getTimestampFromUtilDate(p_channelUserTransferVO.getModifiedOn()));
					pstmtUpdateUser.setString(6,channelUserVO.getUserID());
					pstmtUpdateUser.setString(7,newUserID);
					pstmtUpdateUser.setString(8,channelUserVO.getUserID());
					addCount =	pstmtUpdateUser.executeUpdate();
					if(addCount<=0) {
						throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
					}
					pstmtUpdateUser.clearParameters();
					if(log.isDebugEnabled()) {
						log.debug(methodName,"After updating user information");
					//ends here 
					}

					//new user infomation insertion
					if(log.isDebugEnabled()) {
						log.debug(methodName,"Before inserting new user information");
					}
					k=1;
				
					//setting the old and new parentID in the map for the future user
					parentKeyMap.put(channelUserVO.getUserID(),newUserID);
					
					//getting the parentID of the user.
					parentID=(String)parentKeyMap.get(channelUserVO.getParentID());
					if(BTSLUtil.isNullString(parentID)) {
						parentID=p_channelUserTransferVO.getToParentID();
					}
					
					pstmtInsertUser.setString(k++,newUserID);
					//for multilanguage support
					//commented for DB2  pstmtInsertUser.setFormOfUse(k, OraclePreparedStatement.FORM_NCHAR);
					pstmtInsertUser.setString(k++, channelUserVO.getUserName());

					pstmtInsertUser.setString(k++,channelUserVO.getNetworkID());
					pstmtInsertUser.setString(k++,channelUserVO.getLoginID());
					pstmtInsertUser.setString(k++,channelUserVO.getPassword());
					pstmtInsertUser.setString(k++,channelUserVO.getCategoryCode());
					pstmtInsertUser.setString(k++,parentID);
					pstmtInsertUser.setString(k++,p_channelUserTransferVO.getToOwnerID());
					pstmtInsertUser.setString(k++,channelUserVO.getAllowedIps());
					pstmtInsertUser.setString(k++,channelUserVO.getAllowedDays());
					pstmtInsertUser.setString(k++,channelUserVO.getFromTime());
					pstmtInsertUser.setString(k++,channelUserVO.getToTime());
					pstmtInsertUser.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(channelUserVO.getLastLoginOn()));
					pstmtInsertUser.setString(k++,channelUserVO.getEmpCode());
					pstmtInsertUser.setString(k++, channelUserVO.getPreviousStatus());
					//pstmtInsertUser.setString(k++,PretupsI.USER_STATUS_ACTIVE);
					pstmtInsertUser.setString(k++,channelUserVO.getEmail());
					//Added by Deepika aggarwal
					pstmtInsertUser.setString(k++,channelUserVO.getCompany());
					pstmtInsertUser.setString(k++,channelUserVO.getFax());
					//end added by deepika aggarwal
					pstmtInsertUser.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(channelUserVO.getPasswordModifiedOn()));
					pstmtInsertUser.setString(k++,channelUserVO.getContactPerson());
					pstmtInsertUser.setString(k++,channelUserVO.getContactNo());
					pstmtInsertUser.setString(k++,channelUserVO.getDesignation());
					pstmtInsertUser.setString(k++,channelUserVO.getDivisionCode());
					pstmtInsertUser.setString(k++,channelUserVO.getDepartmentCode());
					pstmtInsertUser.setString(k++,channelUserVO.getMsisdn());
					pstmtInsertUser.setString(k++,channelUserVO.getUserType());
					pstmtInsertUser.setString(k++,channelUserVO.getCreatedBy());
					pstmtInsertUser.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
					pstmtInsertUser.setString(k++,PretupsI.SYSTEM);
					pstmtInsertUser.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(p_channelUserTransferVO.getModifiedOn()));
					pstmtInsertUser.setString(k++,channelUserVO.getAddress1());
					pstmtInsertUser.setString(k++,channelUserVO.getAddress2());
					pstmtInsertUser.setString(k++,channelUserVO.getCity());
					pstmtInsertUser.setString(k++,channelUserVO.getState());
					pstmtInsertUser.setString(k++,channelUserVO.getCountry());
					pstmtInsertUser.setString(k++,channelUserVO.getSsn());
					pstmtInsertUser.setString(k++,channelUserVO.getUserNamePrefix());
					pstmtInsertUser.setString(k++,channelUserVO.getExternalCode());
					pstmtInsertUser.setString(k++,channelUserVO.getUserCode());
					pstmtInsertUser.setString(k++,channelUserVO.getShortName());
					pstmtInsertUser.setString(k++,channelUserVO.getUserID());
					pstmtInsertUser.setInt(k++,channelUserVO.getInvalidPasswordCount());
					pstmtInsertUser.setString(k++,channelUserVO.getLevel1ApprovedBy());
					pstmtInsertUser.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(channelUserVO.getLevel1ApprovedOn()));
					pstmtInsertUser.setString(k++,channelUserVO.getLevel2ApprovedBy());
					pstmtInsertUser.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(channelUserVO.getLevel2ApprovedOn()));
					pstmtInsertUser.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(channelUserVO.getAppointmentDate()));
					pstmtInsertUser.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(channelUserVO.getPasswordCountUpdatedOn()));
					pstmtInsertUser.setString(k++,channelUserVO.getPreviousStatus());
					//added by deepika aggarwal
					pstmtInsertUser.setString(k++,channelUserVO.getFirstName());
					pstmtInsertUser.setString(k++,channelUserVO.getLastName());
					//pstmtInsertUser.setString(k++,channelUserVO.getOriginId());  //by Naveen
					//end added by deepika aggarwal
					addCount =	pstmtInsertUser.executeUpdate();
					if(addCount<=0) {
						throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
					}
					pstmtInsertUser.clearParameters();
					if(log.isDebugEnabled()) {
						log.debug(methodName,"After inserting new user information");
					//ends here
					}

					//user phones transfer
					if(log.isDebugEnabled()) {
						log.debug(methodName,"Before inserting new user phones information");
					}
					pstmtSelectUserPhones.setString(1,channelUserVO.getUserID());
					rsSelectUserPhones=pstmtSelectUserPhones.executeQuery();
					UserPhoneVO userPhoneVO=null;
					while(rsSelectUserPhones.next())
					{
						k=1;
						pstmtInsertUserPhones.setString(k++,newUserID);
						pstmtInsertUserPhones.setString(k++,String.valueOf(IDGenerator.getNextID("PHONE_ID",TypesI.ALL)));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("msisdn"));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("description"));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("primary_number"));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("sms_pin"));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("pin_required"));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("phone_profile"));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("phone_language"));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("country"));
						//pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("invalid_pin_count"));
						pstmtInsertUserPhones.setInt(k++,Integer.parseInt(rsSelectUserPhones.getString("invalid_pin_count")));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("last_transaction_status"));
						pstmtInsertUserPhones.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(rsSelectUserPhones.getDate("last_transaction_on")));
						pstmtInsertUserPhones.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(rsSelectUserPhones.getDate("pin_modified_on")));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("created_by"));
						pstmtInsertUserPhones.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(rsSelectUserPhones.getDate("created_on")));
						pstmtInsertUserPhones.setString(k++,p_channelUserTransferVO.getModifiedBy());
						pstmtInsertUserPhones.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(p_channelUserTransferVO.getModifiedOn()));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("last_transfer_id"));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("last_transfer_type"));
						//pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("prefix_id"));
						pstmtInsertUserPhones.setInt(k++, Integer.parseInt(rsSelectUserPhones.getString("prefix_id")));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("temp_transfer_id"));
						pstmtInsertUserPhones.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(rsSelectUserPhones.getDate("first_invalid_pin_time")));
						pstmtInsertUserPhones.setString(k++ , rsSelectUserPhones.getString("pin_reset"));
						
						//for Zebra and Tango By sanjeew  date 05/07/07
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("access_type"));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("from_time"));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("to_time"));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("allowed_days"));
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("allowed_ip"));
						pstmtInsertUserPhones.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(rsSelectUserPhones.getDate("last_login_on")));
						//PIN_RESET Value
						pstmtInsertUserPhones.setString(k++,rsSelectUserPhones.getString("PIN_RESET"));
						//end Zebra and Tango
						
						addCount =	pstmtInsertUserPhones.executeUpdate();
						if(addCount<=0) {
							throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
						}
						
						if(rsSelectUserPhones.getString("msisdn").equals(channelUserVO.getMsisdn()))
						{
							userPhoneVO=new UserPhoneVO();
							userPhoneVO.setCountry(rsSelectUserPhones.getString("country"));
							userPhoneVO.setPhoneLanguage(rsSelectUserPhones.getString("phone_language"));
							channelUserVO.setUserPhoneVO(userPhoneVO);
						}
						pstmtInsertUserPhones.clearParameters();
					}
					pstmtSelectUserPhones.clearParameters();
					try {
						if (rsSelectUserPhones != null) {
							rsSelectUserPhones.close();
						}
					} catch (Exception ex) {
						log.errorTrace(methodName, ex);
					}
					if(log.isDebugEnabled()) {
						log.debug(methodName,"After inserting new user phones information");
					// ends here
					}
					
					/*//user DailyBalances transfer
					if(log.isDebugEnabled())
						log.debug("transferChannelUser","Before inserting new user daily balances information");
					pstmtSelectUserDailyBalances.setString(1,channelUserVO.getUserID());
					rsSelectUserDailyBalances =pstmtSelectUserDailyBalances.executeQuery();
					while(rsSelectUserDailyBalances.next())
					{
						pstmtInsertUserDailyBalances.setString(1,newUserID);
						pstmtInsertUserDailyBalances.setTimestamp(2,BTSLUtil.getTimestampFromUtilDate(rsSelectUserDailyBalances.getDate("balance_date")));
						pstmtInsertUserDailyBalances.setString(3,rsSelectUserDailyBalances.getString("network_code"));
						pstmtInsertUserDailyBalances.setString(4,rsSelectUserDailyBalances.getString("network_code_for"));
						pstmtInsertUserDailyBalances.setString(5,rsSelectUserDailyBalances.getString("product_code"));
						pstmtInsertUserDailyBalances.setLong(6,rsSelectUserDailyBalances.getLong("balance"));
						pstmtInsertUserDailyBalances.setLong(7,rsSelectUserDailyBalances.getLong("prev_balance"));
						pstmtInsertUserDailyBalances.setString(8,rsSelectUserDailyBalances.getString("last_transfer_type"));
						pstmtInsertUserDailyBalances.setString(9,rsSelectUserDailyBalances.getString("last_transfer_no"));
						pstmtInsertUserDailyBalances.setTimestamp(10,BTSLUtil.getTimestampFromUtilDate(rsSelectUserDailyBalances.getDate("last_transfer_on")));
						pstmtInsertUserDailyBalances.setTimestamp(11,BTSLUtil.getTimestampFromUtilDate(rsSelectUserDailyBalances.getDate("created_on")));
						addCount =	pstmtInsertUserDailyBalances.executeUpdate();
						if(addCount<=0)
							throw new BTSLBaseException(this, "transferChannelUser", "channeluser.viewuserhierarchy.msg.trfunsuccess");
						pstmtInsertUserDailyBalances.clearParameters();
					}
					pstmtSelectUserDailyBalances.clearParameters();
					try{if (rsSelectUserDailyBalances != null)rsSelectUserDailyBalances.close();}catch (Exception ex){}
					if(log.isDebugEnabled())
						log.debug("transferChannelUser","After inserting new user daily balances information");
					// ends here
					*/
					//user domains transfer
					if(log.isDebugEnabled()) {
						log.debug(methodName,"Before inserting new user domains information");
					}
					pstmtSelectUserDomains.setString(1,channelUserVO.getUserID());
					rsSelectUserDomains=pstmtSelectUserDomains.executeQuery();
					while(rsSelectUserDomains.next())
					{
						pstmtInsertUserDomains.setString(1,newUserID);
						pstmtInsertUserDomains.setString(2,rsSelectUserDomains.getString("domain_code"));
						addCount =	pstmtInsertUserDomains.executeUpdate();
						if(addCount<=0) {
							throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
						}
						if(log.isDebugEnabled()) {
							log.debug(methodName,"After inserting new user domains information");
						}
						pstmtInsertUserDomains.clearParameters();
					}
					pstmtSelectUserDomains.clearParameters();
					try {
						if (rsSelectUserDomains != null) {
							rsSelectUserDomains.close();
						}
					} catch (Exception ex) {
						log.errorTrace(methodName, ex);
					}
					// ends here
					
					//user geographics transfer 
					if(log.isDebugEnabled()) {
						log.debug(methodName,"Before inserting new user geographics information");
					}
					pstmtSelectUserGeographies.setString(1,channelUserVO.getUserID());
					rsSelectUserGeographies=pstmtSelectUserGeographies.executeQuery();
					while(rsSelectUserGeographies.next())
					{
						pstmtInsertUserGeographies.setString(1,newUserID);
						
						//for Zebra and Tango By sanjeew  date 05/07/07
						pstmtInsertUserGeographies.setString(2,rsSelectUserGeographies.getString("application_id"));
						//end Zebra and Tango
						
						/*pstmtInsertUserGeographies.setString(3,rsSelectUserGeographies.getString("grph_domain_code"));
						// for channel transfer table
						channelUserVO.setGeographicalCode(rsSelectUserGeographies.getString("grph_domain_code"));*/
						pstmtInsertUserGeographies.setString(3,channelUserVO.getGeographicalCode());
						// ends here
						addCount =	pstmtInsertUserGeographies.executeUpdate();
						if(addCount<=0) {
							throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
						}
						pstmtInsertUserGeographies.clearParameters();
					}
					pstmtSelectUserGeographies.clearParameters();
					try {
						if (rsSelectUserGeographies != null) {
							rsSelectUserGeographies.close();
						}
					} catch (Exception ex) {
						log.errorTrace(methodName, ex);
					}
					if(log.isDebugEnabled()) {
						log.debug(methodName,"After inserting new user geographics information");
					// ends here
					}
					
					//user product types transfer 
					if(log.isDebugEnabled()) {
						log.debug(methodName,"Before inserting new user product types information");
					}
					pstmtSelectUserProductTypes.setString(1,channelUserVO.getUserID());
					rsSelectUserProductTypes=pstmtSelectUserProductTypes.executeQuery();
					while(rsSelectUserProductTypes.next())
					{
						pstmtInsertUserProductTypes.setString(1,newUserID);
						pstmtInsertUserProductTypes.setString(2,rsSelectUserProductTypes.getString("product_type"));
						addCount =	pstmtInsertUserProductTypes.executeUpdate();
						if(addCount<=0) {
							throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
						}
						pstmtInsertUserProductTypes.clearParameters();
					}
					pstmtSelectUserProductTypes.clearParameters();
					try {
						if (rsSelectUserProductTypes != null) {
							rsSelectUserProductTypes.close();
						}
					} catch (Exception ex) {
						log.errorTrace(methodName, ex);
					}
					if(log.isDebugEnabled()) {
						log.debug(methodName,"After inserting new user product types information");
					// ends here
					}
					
					//user Roles transfer 
					if(log.isDebugEnabled()) {
						log.debug(methodName,"Before inserting new user roles information");
					}
					pstmtSelectUserRoles.setString(1,channelUserVO.getUserID());
					rsSelectUserRoles=pstmtSelectUserRoles.executeQuery();
					while(rsSelectUserRoles.next())
					{
						pstmtInsertUserRoles.setString(1,newUserID);
						pstmtInsertUserRoles.setString(2,rsSelectUserRoles.getString("role_code"));
						
						//for Zebra and Tango By sanjeew  date 05/07/07
						pstmtInsertUserRoles.setString(3,rsSelectUserRoles.getString("gateway_types"));
						//end Zebra and Tango
						
						addCount =	pstmtInsertUserRoles.executeUpdate();
						if(addCount<=0) {
							throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
						}
						pstmtInsertUserRoles.clearParameters();
					}
					pstmtSelectUserRoles.clearParameters();
					try {
						if (rsSelectUserRoles != null) {
							rsSelectUserRoles.close();
						}
					} catch (Exception ex) {
						log.errorTrace(methodName, ex);
					}
					if(log.isDebugEnabled()) {
						log.debug(methodName,"After inserting new user roles information");
					// ends here
					}
					
					//user Services transfer 
					if(log.isDebugEnabled()) {
						log.debug(methodName,"Before inserting new user services information");
					}
					pstmtSelectUserServices.setString(1,channelUserVO.getUserID());
					pstmtSelectUserServices.setString(2,channelUserVO.getNetworkID());
					rsSelectUserServices=pstmtSelectUserServices.executeQuery();
					while(rsSelectUserServices.next())
					{
						pstmtInsertUserServices.setString(1,newUserID);
						pstmtInsertUserServices.setString(2,rsSelectUserServices.getString("service_type"));
						pstmtInsertUserServices.setString(3,rsSelectUserServices.getString("status"));
						addCount =	pstmtInsertUserServices.executeUpdate();
						if(addCount<=0) {
							throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
						}
						pstmtInsertUserServices.clearParameters();
					}
					pstmtSelectUserServices.clearParameters();
					try {
						if (rsSelectUserServices != null) {
							rsSelectUserServices.close();
						}
					} catch (Exception ex) {
						log.errorTrace(methodName, ex);
					}
					if(log.isDebugEnabled()) {
						log.debug(methodName,"After inserting new user services information");
					// ends here	
					}


					//user transfer counts update 
					if(log.isDebugEnabled()) {
						log.debug(methodName,"Before inserting new user transfer counts information");
					}
					pstmtSelectUserTransferCounts.setString(1,channelUserVO.getUserID());
					rsSelectUserTransferCounts=pstmtSelectUserTransferCounts.executeQuery();
					while(rsSelectUserTransferCounts.next())
					{
						k=1;
						pstmtInsertUserTransferCounts.setString(k++,newUserID);
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("daily_in_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("daily_in_value"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("weekly_in_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("weekly_in_value"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("monthly_in_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("monthly_in_value"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("daily_out_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("daily_out_value"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("weekly_out_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("weekly_out_value"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("monthly_out_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("monthly_out_value"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("outside_daily_in_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("outside_daily_in_value"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("outside_weekly_in_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("outside_weekly_in_value"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("outside_monthly_in_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("outside_monthly_in_value"));
						pstmtInsertUserTransferCounts.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(rsSelectUserTransferCounts.getDate("outside_last_in_time")));
						pstmtInsertUserTransferCounts.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(rsSelectUserTransferCounts.getDate("last_in_time")));
						pstmtInsertUserTransferCounts.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(rsSelectUserTransferCounts.getDate("last_out_time")));
						pstmtInsertUserTransferCounts.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(rsSelectUserTransferCounts.getDate("outside_last_out_time")));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("outside_daily_out_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("outside_daily_out_value"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("outside_weekly_out_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("outside_weekly_out_value"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("outside_monthly_out_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("outside_monthly_out_value"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("daily_subscriber_out_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("daily_subscriber_out_value"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("weekly_subscriber_out_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("weekly_subscriber_out_value"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("monthly_subscriber_out_count"));
						pstmtInsertUserTransferCounts.setLong(k++,rsSelectUserTransferCounts.getLong("monthly_subscriber_out_value"));
						pstmtInsertUserTransferCounts.setString(k++,rsSelectUserTransferCounts.getString("last_transfer_id"));
						pstmtInsertUserTransferCounts.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(rsSelectUserTransferCounts.getDate("last_transfer_date")));
						addCount =	pstmtInsertUserTransferCounts.executeUpdate();
						if(addCount<=0) {
							throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
						}
						pstmtInsertUserTransferCounts.clearParameters();
					}
					pstmtSelectUserTransferCounts.clearParameters();
					try {
						if (rsSelectUserTransferCounts != null) {
							rsSelectUserTransferCounts.close();
						}
					} catch (Exception ex) {
						log.errorTrace(methodName, ex);
					}
					if(log.isDebugEnabled()) {
						log.debug(methodName,"After inserting new user transfer counts information");
					// ends here	
					}

					//user balance transfer
					if(log.isDebugEnabled()) {
						log.debug(methodName,"Before updating old user balances information");
					}
					pstmtSelectUserBalance.setString(1,channelUserVO.getUserID());
					rsSelectUserBalance=pstmtSelectUserBalance.executeQuery();
					k=0;
					while(rsSelectUserBalance.next())
					{
						pstmtInsertUserBalance.setString(1,newUserID);
						pstmtInsertUserBalance.setString(2,rsSelectUserBalance.getString("network_code"));
						pstmtInsertUserBalance.setString(3,rsSelectUserBalance.getString("network_code_for"));
						pstmtInsertUserBalance.setString(4,rsSelectUserBalance.getString("product_code"));
						pstmtInsertUserBalance.setLong(5,rsSelectUserBalance.getLong("balance"));
						pstmtInsertUserBalance.setLong(6,0L);
						pstmtInsertUserBalance.setString(7,rsSelectUserBalance.getString("last_transfer_type"));
						pstmtInsertUserBalance.setString(8,rsSelectUserBalance.getString("last_transfer_no"));
						pstmtInsertUserBalance.setTimestamp(9,BTSLUtil.getTimestampFromUtilDate(rsSelectUserBalance.getDate("last_transfer_on")));
						addCount =	pstmtInsertUserBalance.executeUpdate();
						if(addCount<=0) {
							throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
						}
						if(log.isDebugEnabled()) {
							log.debug(methodName,"After inserting new user balances information");
						}

						pstmtInsertUserBalance.clearParameters();
						
						pstmtUpdateUserBalance.setLong(1,0);
						pstmtUpdateUserBalance.setLong(2,rsSelectUserBalance.getLong("balance"));
						pstmtUpdateUserBalance.setString(3,channelUserVO.getUserID());
						pstmtUpdateUserBalance.setString(4,rsSelectUserBalance.getString("product_code"));
						addCount =	pstmtUpdateUserBalance.executeUpdate();
						if(addCount<=0) {
							throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
						}
						pstmtUpdateUserBalance.clearParameters();
						if(log.isDebugEnabled()) {
							log.debug(methodName,"Before transferItemsVO construction");
						}
						transferItemsVO = new ChannelTransferItemsVO();
						transferItemsVO.setSerialNum(++k);
						transferItemsVO.setProductCode(rsSelectUserBalance.getString("product_code"));
						transferItemsVO.setRequiredQuantity(rsSelectUserBalance.getLong("balance"));
						transferItemsVO.setRequestedQuantity(rsSelectUserBalance.getString("balance"));
						transferItemsVO.setApprovedQuantity(rsSelectUserBalance.getLong("balance"));
						transferItemsVO.setUnitValue(rsSelectUserBalance.getLong("unit_value"));
						transferItemsVO.setNetworkCode(p_channelUserTransferVO.getNetworkCode());
						transferItemsVOList.add(transferItemsVO);
						if(log.isDebugEnabled()) {
							log.debug(methodName,"transferItemsVO "+transferItemsVO);
						}

					}
					if(log.isDebugEnabled()) {
						log.debug(methodName,"After updating old user balances information");
					// ends here 
					}
					
					//channel user insertion
					if(log.isDebugEnabled()) {
						log.debug(methodName,"before selecting channel User information");
					}
					pstmtSelectChannelUserInfo.setString(1,channelUserVO.getUserID());
					rsSelectChannelUserInfo=pstmtSelectChannelUserInfo.executeQuery();
					if(rsSelectChannelUserInfo.next())
					{
						if(log.isDebugEnabled())
						{
							log.debug(methodName,"after selecting channel User information");
							log.debug(methodName,"before inserting channel User information");
						}
						k=1;
						pstmtInsertChannelUserInfo.setString(k++,newUserID);
						pstmtInsertChannelUserInfo.setString(k++,rsSelectChannelUserInfo.getString("user_grade"));
						//for multilanguage support
						//commented for DB2  pstmtInsertChannelUserInfo.setFormOfUse(k, OraclePreparedStatement.FORM_NCHAR);
						pstmtInsertChannelUserInfo.setString(k++,rsSelectChannelUserInfo.getString("contact_person"));
						pstmtInsertChannelUserInfo.setString(k++,rsSelectChannelUserInfo.getString("transfer_profile_id"));
						pstmtInsertChannelUserInfo.setString(k++,rsSelectChannelUserInfo.getString("comm_profile_set_id"));
						pstmtInsertChannelUserInfo.setString(k++,rsSelectChannelUserInfo.getString("in_suspend"));
						pstmtInsertChannelUserInfo.setString(k++,rsSelectChannelUserInfo.getString("out_suspend"));
						pstmtInsertChannelUserInfo.setString(k++,rsSelectChannelUserInfo.getString("outlet_code"));
						pstmtInsertChannelUserInfo.setString(k++,rsSelectChannelUserInfo.getString("suboutlet_code"));
						pstmtInsertChannelUserInfo.setTimestamp(k++,BTSLUtil.getTimestampFromUtilDate(rsSelectChannelUserInfo.getTimestamp("activated_on")));
						//for Zebra and Tango By sanjeew  date 05/07/07
						pstmtInsertChannelUserInfo.setString(k++,rsSelectChannelUserInfo.getString("application_id"));
						pstmtInsertChannelUserInfo.setString(k++,rsSelectChannelUserInfo.getString("mpay_profile_id"));
						pstmtInsertChannelUserInfo.setString(k++,newUserID);
						pstmtInsertChannelUserInfo.setString(k++,rsSelectChannelUserInfo.getString("is_primary"));
						pstmtInsertChannelUserInfo.setString(k++,rsSelectChannelUserInfo.getString("mcommerce_service_allow"));
						pstmtInsertChannelUserInfo.setString(k++,rsSelectChannelUserInfo.getString("low_bal_alert_allow"));
						//end Zebra and Tango
						
						addCount=pstmtInsertChannelUserInfo.executeUpdate();
						pstmtInsertChannelUserInfo.clearParameters();
						
						// for channel transfer table
						channelUserVO.setUserGrade(rsSelectChannelUserInfo.getString("user_grade"));
						channelUserVO.setTransferProfileID(rsSelectChannelUserInfo.getString("transfer_profile_id"));
						channelUserVO.setCommissionProfileSetID(rsSelectChannelUserInfo.getString("comm_profile_set_id"));
                        channelUserVO.setMpayProfileID(rsSelectChannelUserInfo.getString("mpay_profile_id"));
						channelUserVO.setFxedInfoStr(newUserID);
						//ends here
						if(addCount<=0) {
							throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
						}
	
						if(log.isDebugEnabled()) {
							log.debug(methodName,"after inserting channel User information");
						}
					}
					// ends here
					if(log.isDebugEnabled()) {
						log.debug(methodName,"before updating channel transfer information");
					}

					channelTransferVO.setCreatedBy(p_channelUserTransferVO.getCreatedBy());
					channelTransferVO.setCreatedOn(p_channelUserTransferVO.getCreatedOn());
					channelTransferVO.setModifiedBy(p_channelUserTransferVO.getModifiedBy());
					channelTransferVO.setModifiedOn(p_channelUserTransferVO.getModifiedOn());
					channelTransferVO.setTransferInitatedBy(p_channelUserTransferVO.getCreatedBy());
					channelTransferVO.setFromUserID(channelUserVO.getUserID());
					channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
					channelTransferVO.setSenderGradeCode(channelUserVO.getUserGrade());
					channelTransferVO.setReceiverGradeCode(channelUserVO.getUserGrade());
					channelTransferVO.setCommProfileSetId(channelUserVO.getCommissionProfileSetID());
					channelTransferVO.setSenderTxnProfile(channelUserVO.getTransferProfileID());
					channelTransferVO.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
					channelTransferVO.setTransferDate(p_channelUserTransferVO.getCreatedOn());
					channelTransferVO.setToUserID(newUserID);
					channelTransferVO.setNetworkCode(p_channelUserTransferVO.getNetworkCode());
					channelTransferVO.setNetworkCodeFor(p_channelUserTransferVO.getNetworkCode());
					channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
					channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
					channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
					channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
					channelTransferVO.setDomainCode(p_channelUserTransferVO.getDomainCode());
					channelTransferVO.setCategoryCode(channelUserVO.getCategoryCode());
					channelTransferVO.setReceiverCategoryCode(channelUserVO.getCategoryCode());
					channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
					channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
					channelTransferVO.setRequestGatewayCode(PretupsI.REQUEST_SOURCE_TYPE_WEB);
					channelTransferVO.setRequestGatewayType(PretupsI.GATEWAY_TYPE_WEB);
					channelTransferVO.setControlTransfer(PretupsI.CONTROL_LEVEL_ADJ);
					//By Sandeep Goel ID CUT001
					// Some new field added for the sender and receiver informaiton and some new 
					// constraints added in the table.
					channelTransferVO.setCommProfileVersion("0");
					channelTransferVO.setReceiverGgraphicalDomainCode(channelUserVO.getGeographicalCode());
					channelTransferVO.setReceiverDomainCode(p_channelUserTransferVO.getDomainCode());
					channelTransferVO.setFromUserCode(channelUserVO.getUserCode());
					channelTransferVO.setToUserCode(channelUserVO.getUserCode());
					channelTransferVO.setActiveUserId(p_channelUserTransferVO.getCreatedBy());
					//ends here
					channelTransferVO.setChannelTransferitemsVOList(transferItemsVOList);
					ChannelTransferBL.genrateChnnlToChnnlTrfID(channelTransferVO);
					channelTransferDAO.addChannelTransfer(p_con, channelTransferVO);
					pstmtSelectUserBalance.clearParameters();
					try {
						if (rsSelectUserBalance != null) {
							rsSelectUserBalance.close();
						}
					} catch (Exception ex) {
						log.errorTrace(methodName, ex);
					}
					if(log.isDebugEnabled()) {
						log.debug(methodName,"after updating channel transfer information");
					}
					// ends here
					int staffUpdateCount=0;
					pstmtSelectStaff.clearParameters();
					pstmtSelectStaff.setString(1, newUserID);
					pstmtSelectStaff.setString(2, p_channelUserTransferVO.getToOwnerID());
					pstmtSelectStaff.setString(3,p_channelUserTransferVO.getModifiedBy());
					pstmtSelectStaff.setTimestamp(4,BTSLUtil.getTimestampFromUtilDate(p_channelUserTransferVO.getModifiedOn()));
					pstmtSelectStaff.setString(5,newUserID);
					pstmtSelectStaff.setTimestamp(6,BTSLUtil.getTimestampFromUtilDate(p_channelUserTransferVO.getCreatedOn()));
					pstmtSelectStaff.setString(7,channelUserVO.getUserID());
					pstmtSelectStaff.setString(8,PretupsI.STAFF_USER_TYPE);
					staffUpdateCount=pstmtSelectStaff.executeUpdate();
					if(staffUpdateCount<0)
					{
						throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
					}
					pstmtupdateStaffPhones.clearParameters();
					pstmtupdateStaffPhones.setString(1,p_channelUserTransferVO.getModifiedBy());
					pstmtupdateStaffPhones.setTimestamp(2,BTSLUtil.getTimestampFromUtilDate(p_channelUserTransferVO.getModifiedOn()));
					pstmtupdateStaffPhones.setString(3,newUserID);
					pstmtupdateStaffPhones.setTimestamp(4,BTSLUtil.getTimestampFromUtilDate(p_channelUserTransferVO.getCreatedOn()));
					pstmtupdateStaffPhones.setString(5, newUserID);
					pstmtupdateStaffPhones.setString(6,PretupsI.STAFF_USER_TYPE);
					staffUpdateCount=pstmtupdateStaffPhones.executeUpdate();
					if(staffUpdateCount<0)
					{
						throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess");
					}
					/*if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue())
                    {
                        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX.equals(channelUserVO.getMcommerceServiceAllow()))
                        {
                            oldUserMap=commonUtil.getMapfromUserVO(channelUserVO);
                            channelUserVO.setUserID(newUserID);
                            newUserMap=commonUtil.getMapfromUserVO(channelUserVO);
                            if(!commonUtil.transferUser(p_con, newUserMap, oldUserMap))
                                throw new BTSLBaseException(this, "transferChannelUser", "channeluser.viewuserhierarchy.msg.mobiquitychangenotupdate");
                            newUserMap=null;
                            oldUserMap=null;
                        }
                    }*/
					int UpdatetransferRuleCount=0;
						pstmtupdateTransferRules.clearParameters();
						pstmtupdateTransferRules.setString(1, newUserID);
						pstmtupdateTransferRules.setString(2, channelUserVO.getUserID());
						pstmtupdateTransferRules.setString(3, PretupsI.TRANSFER_RULE_PROMOTIONAL);
						UpdatetransferRuleCount=pstmtupdateTransferRules.executeUpdate();
					
				}
			}
		}//end of try
		catch (SQLException sqle)
		{
			addCount=0;
			log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASSNAME+"#"+methodName,"","","","Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}//end of catch
		catch (Exception e)
		{
			log.errorTrace(methodName, e);
			addCount=0;
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASSNAME+"#"+methodName,"","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}//end of catch
		finally
 {
			try {
				if (pstmtUpdateUser != null) {
					pstmtUpdateUser.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertUser != null) {
					pstmtInsertUser.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (rsSelectUserGeographies != null) {
					rsSelectUserGeographies.close();
				}
			} catch (Exception ex) {
				log.errorTrace(methodName, ex);
			}
			try {
				if (pstmtSelectUserGeographies != null) {
					pstmtSelectUserGeographies.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertUserGeographies != null) {
					pstmtInsertUserGeographies.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (rsSelectUserDomains != null) {
					rsSelectUserDomains.close();
				}
			} catch (Exception ex) {
				log.errorTrace(methodName, ex);
			}
			try {
				if (pstmtSelectUserDomains != null) {
					pstmtSelectUserDomains.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertUserDomains != null) {
					pstmtInsertUserDomains.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (rsSelectUserProductTypes != null) {
					rsSelectUserProductTypes.close();
				}
			} catch (Exception ex) {
				log.errorTrace(methodName, ex);
			}
			try {
				if (pstmtSelectUserProductTypes != null) {
					pstmtSelectUserProductTypes.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertUserProductTypes != null) {
					pstmtInsertUserProductTypes.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (rsSelectUserRoles != null) {
					rsSelectUserRoles.close();
				}
			} catch (Exception ex) {
				log.errorTrace(methodName, ex);
			}
			try {
				if (pstmtSelectUserRoles != null) {
					pstmtSelectUserRoles.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertUserRoles != null) {
					pstmtInsertUserRoles.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (rsSelectUserServices != null) {
					rsSelectUserServices.close();
				}
			} catch (Exception ex) {
				log.errorTrace(methodName, ex);
			}
			try {
				if (pstmtSelectUserServices != null) {
					pstmtSelectUserServices.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertUserServices != null) {
					pstmtInsertUserServices.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (rsSelectUserBalance != null) {
					rsSelectUserBalance.close();
				}
			} catch (Exception ex) {
				log.errorTrace(methodName, ex);
			}
			try {
				if (pstmtSelectUserBalance != null) {
					pstmtSelectUserBalance.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertUserBalance != null) {
					pstmtInsertUserBalance.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (pstmtUpdateUserBalance != null) {
					pstmtUpdateUserBalance.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (rsSelectUserPhones != null) {
					rsSelectUserPhones.close();
				}
			} catch (Exception ex) {
				log.errorTrace(methodName, ex);
			}
			try {
				if (pstmtSelectUserPhones != null) {
					pstmtSelectUserPhones.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertUserPhones != null) {
					pstmtInsertUserPhones.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			/*
			 * try{if (rsSelectUserDailyBalances !=
			 * null)rsSelectUserDailyBalances.close();}catch (Exception ex){}
			 * try{if(pstmtSelectUserDailyBalances !=null)
			 * pstmtSelectUserDailyBalances .close();}catch(Exception e){}
			 * try{if(pstmtInsertUserDailyBalances !=null)
			 * pstmtInsertUserDailyBalances .close();}catch(Exception e){}
			 */
			try {
				if (rsSelectUserTransferCounts != null) {
					rsSelectUserTransferCounts.close();
				}
			} catch (Exception ex) {
				log.errorTrace(methodName, ex);
			}
			try {
				if (pstmtSelectUserTransferCounts != null) {
					pstmtSelectUserTransferCounts.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertUserTransferCounts != null) {
					pstmtInsertUserTransferCounts.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (rsSelectChannelUserInfo != null) {
					rsSelectChannelUserInfo.close();
				}
			} catch (Exception ex) {
				log.errorTrace(methodName, ex);
			}
			try {
				if (pstmtSelectChannelUserInfo != null) {
					pstmtSelectChannelUserInfo.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertChannelUserInfo != null) {
					pstmtInsertChannelUserInfo.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectStaff != null) {
					pstmtSelectStaff.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (pstmtupdateStaffPhones != null) {
					pstmtupdateStaffPhones.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			
			try {
				if (pstmtupdateTransferRules != null) {
					pstmtupdateTransferRules.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			if(log.isDebugEnabled()) {
				log.debug(methodName,PretupsI.EXITED+" addCount="+addCount);
			}
		}//end of finally
		return addCount;
	}
	// added for channel user transfer
	/**
	 * Method for checking Is MSISDN with status Y already exist or not.
	 * @param p_con java.sql.Connection
	 * @param ChannelUserTransferVO
	 * @return flag boolean
	 * @throws  BTSLBaseException
	 * @author 
	 */
	public boolean isMSISDNExist(Connection con, ChannelUserTransferVO p_channelUserTransferVO) throws BTSLBaseException
	{

		final String methodName = "isMSISDNExist";
		if (log.isDebugEnabled())
		{
			log.debug(CLASSNAME+"#"+methodName, "Entered: p_channelUserTransferVO="+con);
		}
		ArrayList userList = p_channelUserTransferVO.getUserHierarchyList();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean existFlag = false;
		ChannelUserVO channelUserVO = null;
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT U.msisdn,U.status from users U ");
		strBuff.append(" WHERE U.msisdn = ? AND U.status = ?");
		String sqlSelect = strBuff.toString();
		if (log.isDebugEnabled())
		{
		    log.debug(CLASSNAME+"#"+methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		try
		{
			if(userList!=null)
			{
			
				pstmt = con.prepareStatement(sqlSelect);
				for(int i=0,j=userList.size();i<j;i++)
				{
					channelUserVO=(ChannelUserVO )userList.get(i);
					pstmt.clearParameters();
					pstmt.setString(1, channelUserVO.getMsisdn());
					pstmt.setString(2, PretupsI.USER_STATUS_ACTIVE);
					rs = pstmt.executeQuery();
					if (rs.next())
					{
						existFlag = true;
						break;
					}
				}
			}
		} catch (SQLException sqe)
		{
			log.errorTrace(CLASSNAME+"#"+methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASSNAME+"#"+methodName,"","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, CLASSNAME+"#"+methodName, "error.general.sql.processing");
		} catch (Exception ex)
		{
			log.errorTrace(CLASSNAME+"#"+methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASSNAME+"#"+methodName,"","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, CLASSNAME+"#"+methodName, "error.general.processing");
		} finally
		{
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			if (log.isDebugEnabled())
			{
				log.debug(methodName, "Exiting: existFlag=" + existFlag);
			}
		}
		return existFlag;
	}
    /**
	 * Method generateUserId.
	 * @param p_networkCode String
	 * @param p_prefix String
	 * @return String
	 * @throws Exception
	 */
	
	private String generateUserId(String networkCode,String prefix) throws Exception
    {
        final String methodName = "generateUserId";
		if (log.isDebugEnabled()) {
            log.debug(methodName , "Entered p_networkCode="+networkCode+" p_prefix="+prefix);
        }
        int length = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        String id = BTSLUtil.padZeroesToLeft((IDGenerator.getNextID(TypesI.USERID,TypesI.ALL,networkCode))+"",length);
        
 		id = networkCode+prefix+id;
        if (log.isDebugEnabled()) {
            log.debug(methodName , "Exiting id="+id);
        }
        return id;
    }
	
	
    public int updateUserMigrationRequestStatus(java.sql.Connection con, ChannelUserTransferVO userTransferVO,String loggedinUserID) throws  BTSLBaseException {
    	  final String methodName = "#updateUserMigrationRequestStatus";
          
    	if (log.isDebugEnabled())
            log.debug(CLASSNAME+"#"+methodName, PretupsI.ENTERED+" ..loggedinUserID"+loggedinUserID+"p_userTransferVO"+userTransferVO);
       int count = 0;
        int i = 1;
        String updateUserTransfer = "update USER_MIGRATION_REQUEST set status =? , modified_on=? ,modified_by=?,invalid_otp_count =? where to_user_id=? and status = ?  and from_user_id =? ";
    	if (log.isDebugEnabled())
    		log.debug(CLASSNAME+"#"+methodName, " Entered..updateUserTransfer="+updateUserTransfer);

        try (PreparedStatement pstmtS = con.prepareStatement(updateUserTransfer);){

        	
        	i = 1;
        	
        	pstmtS.setString(i++, userTransferVO.getStatus());
        	pstmtS.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(new java.util.Date()));
        	pstmtS.setString(i++,loggedinUserID);
        	pstmtS.setInt(i++, userTransferVO.getInvalidOtpCount());
        	pstmtS.setString(i++, userTransferVO.getUserID());
        	pstmtS.setString(i++, PretupsI.YES);
        	pstmtS.setString(i++,loggedinUserID);
        	int updateCount = pstmtS.executeUpdate();
        	if (updateCount > 0) {
        		count = updateCount;
        		con.commit();

        	} 
        	else
        		con.rollback();
        	if (log.isDebugEnabled())
                log.debug(CLASSNAME+"#"+methodName, PretupsI.EXITED+"  count=" + count);
        	return count;
        }
        catch (SQLException sqe) {
            log.error(CLASSNAME+"#"+methodName, PretupsI.SQLEXCEPTION + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+"#"+methodName, "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, CLASSNAME+"#"+methodName, "error.general.processing");
        } catch (Exception ex) {
            log.error("updateUserMigrationRequestStatus() ::",  PretupsI.EXCEPTION + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASSNAME+"#"+methodName, "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, CLASSNAME+"#"+methodName, "error.general.processing");
        } 
       
    }
	

}
