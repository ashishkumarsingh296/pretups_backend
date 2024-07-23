/**
 * @# ActivationBonusDAO.java
 *    related to activation bonus module
 *    Created on Created by History
 *    --------------------------------------------------------------------------
 *    ------
 *    Feb 6, 2009 Rahul Dutt Initial creation
 * 
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2009 Bharti Telesoft Ltd.
 **/
package com.btsl.pretups.channel.profile.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

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
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

/**
 * @author rahul.dutt
 * 
 */
/**
 * @author sushma.salve
 * 
 *         
 */
public class ActivationBonusDAO {

    private static final Log log = LogFactory.getLog(ActivationBonusDAO.class.getName());
    private ActivationBonusQry activationBonusQry = (ActivationBonusQry)ObjectProducer.getObject(QueryConstants.ACTIVATION_BONUS_QRY, QueryConstants.QUERY_PRODUCER);

    /**
     * rahul.dutt
     * this method is used for correcting subscriber retailer mapping
     * 
     * @param p_con
     * @param p_subsMsisdn
     * @param p_user_id
     * @return int
     * @throws BTSLBaseException
     */
    public int updateSubsMapping(Connection p_con, String p_subsMsisdn, String p_user_id) throws BTSLBaseException {
        final String methodName = "updateSubsMapping";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_user_id=");
        	loggerValue.append(p_user_id);
        	loggerValue.append(" p_subsMsisdn=");
        	loggerValue.append(p_subsMsisdn);
        	
            log.debug(methodName, loggerValue );
        }
       
        StringBuilder strBuff = null;
        int updateCount = 0;
        try {
            final java.util.Date date = new Date();
            strBuff = new StringBuilder();
            strBuff.append("UPDATE act_bonus_subs_mapping ");
            strBuff.append("SET status=?,modified_on=?,modified_by=?  ");
            strBuff.append(" where subscriber_msisdn=? and status=? ");
            final String updateQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("updateQuery:" );
            	loggerValue.append(updateQuery);
                log.debug(methodName, loggerValue);
            }
            try(PreparedStatement psmtUpdate = p_con.prepareStatement(updateQuery);)
            {

            psmtUpdate.setString(1, PretupsI.USER_STATUS_SUSPEND);
            psmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
            psmtUpdate.setString(3, p_user_id);
            psmtUpdate.setString(4, p_subsMsisdn);
            psmtUpdate.setString(5, PretupsI.STATUS_ACTIVE);
            updateCount = psmtUpdate.executeUpdate();

        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException:" );
        	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue );
            log.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:"  );
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[updateSubsMapping]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	 loggerValue.setLength(0);
         	loggerValue.append("Exception: " );
         	loggerValue.append(e.getMessage());
            log.error(methodName,   loggerValue);
            log.errorTrace(methodName, e);
            loggerValue.setLength(0);
         	loggerValue.append("Exception: " );
         	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[updateSubsMapping]", "", "", "",
                 loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
           
            if (log.isDebugEnabled()) {
            	 loggerValue.setLength(0);
              	loggerValue.append("Exiting: updateCount=" );
              	loggerValue.append(updateCount);
                log.debug(methodName,  loggerValue );
            }
        } // end of finally

        return updateCount;
    }

    /**
     * rahul.dutt
     * this method is used for correcting subscriber retailer mapping
     * 
     * @param conn
     * @param pretSubsMappingList
     * @param p_newRetailerID
     * @param p_user_id
     * @return int
     * @throws BTSLBaseException
     */
    public int insertSubsMapping(Connection conn, ArrayList pretSubsMappingList, String p_newRetailerID, String p_user_id) throws BTSLBaseException {
         
        StringBuilder strBuff = null;
        int insertCount = 0;
        RetSubsMappingVO p_retSubsMappingVO = null;
        final String methodName = "insertSubsMapping";
        StringBuilder loggerValue= new StringBuilder(); 
        enterInsertSubsMapping(pretSubsMappingList, p_newRetailerID, methodName);
        try {
            final Iterator itr = pretSubsMappingList.iterator();
            strBuff = new StringBuilder();
            strBuff.append("INSERT into  act_bonus_subs_mapping ( user_id, ");
            strBuff.append(" subscriber_msisdn, subscriber_type, set_id, version,expiry_date, registered_on, activation_bonus_given, status, created_on, created_by, modified_on,modified_by,");
            strBuff.append(" NETWORK_CODE ) values ");
            strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            final String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query insertQuery");
            	loggerValue.append(insertQuery);
                log.debug(methodName, loggerValue );
            }
            try(PreparedStatement psmtInsert = conn.prepareStatement(insertQuery);)
            {
            while (itr.hasNext()) {
                p_retSubsMappingVO = (RetSubsMappingVO) itr.next();
                // add new mapping
                psmtInsert.setString(1, p_newRetailerID);
                psmtInsert.setString(2, p_retSubsMappingVO.getSubscriberMsisdn());
                psmtInsert.setString(3, p_retSubsMappingVO.getSubscriberType());
                psmtInsert.setString(4, p_retSubsMappingVO.getSetID());
                psmtInsert.setString(5, p_retSubsMappingVO.getVersion());
                psmtInsert.setDate(6, BTSLUtil.getSQLDateFromUtilDate(p_retSubsMappingVO.getExpiryDate()));
                psmtInsert.setDate(7, BTSLUtil.getSQLDateFromUtilDate(p_retSubsMappingVO.getRegisteredOn()));
                psmtInsert.setString(8, PretupsI.STATUS_DELETE);
                psmtInsert.setString(9, PretupsI.USER_STATUS_NEW);
                psmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(p_retSubsMappingVO.getCreatedOn()));
                psmtInsert.setString(11, p_retSubsMappingVO.getCreatedBy());
                psmtInsert.setTimestamp(12, BTSLUtil.getTimestampFromUtilDate(p_retSubsMappingVO.getModifiedOn()));
                psmtInsert.setString(13, p_user_id);
                psmtInsert.setString(14, p_retSubsMappingVO.getNetworkCode());
                insertCount = psmtInsert.executeUpdate();
                psmtInsert.clearParameters();
                // check the status of the insert
                if (insertCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }
        } 
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException: ");
        	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue );
            log.errorTrace(methodName, sqle);
            
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[insertSubsMapping]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[insertSubsMapping]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
   
            
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                log.debug(methodName, loggerValue);
            }
        } // end of finally

        return insertCount;
    }

	private void enterInsertSubsMapping(ArrayList pretSubsMappingList,
			String p_newRetailerID, final String methodName) {
		if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_retSubsMappingList=" + pretSubsMappingList.size() + " p_newRetailerID=" + p_newRetailerID);
        }
	}

    /**
     * rahul.dutt
     * this method is used for correcting subscriber retailer mapping
     * 
     * @param p_con
     * @param p_retailer
     * @param p_isMsisdn
     * @param p_fromDate
     * @param p_toDate
     * @return RetSubsMappingVO
     * @throws BTSLBaseException
     */
    public RetSubsMappingVO loadMappingSummary(Connection p_con, String p_retailer, boolean p_isMsisdn, String p_fromDate, String p_toDate) throws BTSLBaseException {
        
         
        RetSubsMappingVO retSubsMappingVO = null;
        final String methodName = "loadMappingSummary";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_retailer=");
        	loggerValue.append(p_retailer);
        	loggerValue.append(" p_isMsisdn=");
        	loggerValue.append(p_isMsisdn);
        	loggerValue.append(" p_fromDate=" );
        	loggerValue.append(p_fromDate);
        	loggerValue.append(" p_toDate=");
        	loggerValue.append(" p_toDate=" );
            log.debug(methodName,  loggerValue);
        }
        try {
        	
            final String selectQuery = activationBonusQry.loadMappingSummaryQry(p_isMsisdn);
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query selectQuery");
            	loggerValue.append(selectQuery);
                log.debug(methodName,  loggerValue);
            }
            try(PreparedStatement pstmt = p_con.prepareStatement(selectQuery);)
            {
            pstmt.setString(1, p_retailer);
            pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_fromDate)));
            pstmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_toDate)));
            try(ResultSet rs = pstmt.executeQuery();){
            retSubsMappingVO = new RetSubsMappingVO();
            if (rs.next()) {
                retSubsMappingVO.setUserID(rs.getString("user_id"));
                retSubsMappingVO.setSubscriberMsisdn(rs.getString("msisdn"));
                retSubsMappingVO.setNoOfActivatedSubs(rs.getLong("noOfSubs"));
            }
        }
        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException: ");
        	loggerValue.append(sqle.getMessage());
            log.error(methodName,  loggerValue);
            log.errorTrace(methodName, sqle);
            
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadMappingSummary]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadMappingSummary]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
        	
           
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: retSubsMappingVO=");
            	loggerValue.append(retSubsMappingVO);
                log.debug(methodName,  loggerValue.toString());
            }
        } // end of finally

        return retSubsMappingVO;
    }

    /**
     * rahul.dutt
     * this method is used for correcting subscriber retailer mapping
     * 
     * @param p_con
     * @param p_activatedSubs
     * @param p_retailerID
     * @return int
     * @throws BTSLBaseException
     */
    public int updateMappingSummary(Connection p_con, long p_activatedSubs, String p_retailerID) throws BTSLBaseException {
        
        int updateCount = 0;
        final String methodName = "updateMappingSummary";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_activatedSubs=");
        	loggerValue.append(p_activatedSubs);
        	loggerValue.append(" p_retailerID=");
        	loggerValue.append(p_retailerID);
            log.debug(methodName,  loggerValue );
        }
        try {
            final java.util.Date date = new Date();
            final String updateQuery = activationBonusQry.updateMappingSummaryQry();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query updateQuery:" );
            	loggerValue.append(updateQuery);
                log.debug("updateSubsRetMapping",loggerValue);
            }
         
            try(PreparedStatement psmtUpdate = p_con.prepareStatement(updateQuery);)
            {
            psmtUpdate.setInt(1, Integer.parseInt(Long.toString(p_activatedSubs)));
            psmtUpdate.setString(2, p_retailerID);
            psmtUpdate.setDate(3, BTSLUtil.getSQLDateFromUtilDate(date));
            updateCount = psmtUpdate.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append( "SQLException: " );
        	loggerValue.append(sqle.getMessage());
        	
            log.error(methodName,loggerValue );
            log.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append( "SQL Exception:" );
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[updateMappingSummary]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append( "Exception:" );
        	loggerValue.append( e.getMessage() );
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            
            loggerValue.setLength(0);
        	loggerValue.append( "Exception:" );
        	loggerValue.append(  e.getMessage() );
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[updateMappingSummary]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally

        return updateCount;
    }

    /**
     * rahul.dutt
     * this method is used for correcting subscriber retailer mapping
     * 
     * @param p_con
     * @param p_userId
     * @param p_listSize
     * @return int
     * @throws BTSLBaseException
     */
    public int insertMappingSummary(Connection p_con, String p_userId, int p_listSize) throws BTSLBaseException {
         
        StringBuilder strBuff = null;
        int insertCount = 0;
        final String methodName = "insertMappingSummary";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_userId=");
        	loggerValue.append(p_userId);
        	loggerValue.append(" listSize=" );
        	loggerValue.append(p_listSize );
            log.debug(methodName,  loggerValue );
        }
        try {
            final java.util.Date date = new Date();

            strBuff = new StringBuilder();
            strBuff.append("INSERT INTO subs_activation_summary (user_id,  ");
            strBuff.append(" activated_users,activation_date ) values ");
            strBuff.append("(?,?,?)");
            final String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query insertQuery:");
            	loggerValue.append(insertQuery);
                log.debug(methodName,  loggerValue );
            }
     
            try(PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);)
            {
            psmtInsert.setString(1, p_userId);
            psmtInsert.setInt(2, p_listSize);
            psmtInsert.setDate(3, BTSLUtil.getSQLDateFromUtilDate(date));
            insertCount = psmtInsert.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException: ");
        	loggerValue.append(sqle.getMessage());
            log.error(methodName,  loggerValue );
            log.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
        	loggerValue.append( "SQL Exception:" );
        	loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[insertMappingSummary]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append( "Exception:" );
        	loggerValue.append( e.getMessage() );
            log.error(methodName, loggerValue );
            log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append( "Exception:" );
        	loggerValue.append( e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[insertMappingSummary]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
           
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append( "Exiting: insertCount=" );
            	loggerValue.append(insertCount);
                log.debug(methodName,  loggerValue );
            }
        } // end of finally

        return insertCount;
    }

    /**
     * This method is findout the channel user whether it is exist or not .
     * it is done for add mapping of subscriber and retailer from the front end
     * .
     * 
     * @param p_con
     * @param p_networkID
     * @param p_userMsisdn
     * @author vikas.kumar
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isChannelUserExist(Connection p_con, String p_networkID, String p_userMsisdn) throws BTSLBaseException {
        final String methodName = "isChannelUserExist";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered params  p_networkID::");
        	loggerValue.append(p_networkID);
        	loggerValue.append("p_userMsisdn::");
        	loggerValue.append(p_userMsisdn);
        
            log.debug(methodName,  loggerValue );
        }

         
        
        boolean found = false;
        final StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM users WHERE  NETWORK_CODE = ? and MSISDN = ?");
        final String selectQuery = sqlBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query::" + selectQuery);
        }
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
            
            pstmtSelect.setString(1, p_networkID);
            pstmtSelect.setString(2, p_userMsisdn);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                found = true;
            }
        } 
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isChannelUserExist]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[isChannelUserExist]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        
        	
        
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isExists found=" + found);
            }
        }
        return found;
    }

    /**
     * this method check whether association exist between user and Subscriber
     * activation profile
     * i.e Add Retailer-Subscriber Mapping
     * 
     * @param p_con
     * @param p_userMSISDN
     * @param p_subsMSISDN
     * @return boolean
     * @throws BTSLBaseException
     * @author Vikas.kumar
     */
    public boolean isMappingExist(Connection p_con, String p_userMSISDN, String p_subsMSISDN) throws BTSLBaseException {
        final String methodName = "isMappingExist";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered params  p_userMSISDN::");
        	loggerValue.append(p_userMSISDN);
        	loggerValue.append("p_subsMSISDN");
        	loggerValue.append(p_subsMSISDN);
            log.debug(methodName, loggerValue );
        }

         
       
        boolean found = false;
        final StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM USERS U,ACT_BONUS_SUBS_MAPPING absm ");
        sqlBuff.append("WHERE u.USER_ID=absm.USER_ID AND u.MSISDN =? AND absm.SUBSCRIBER_MSISDN=?");
        final String selectQuery = sqlBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query::" + selectQuery);
        }

        try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);) {
            
            pstmtSelect.setString(1, p_userMSISDN);
            pstmtSelect.setString(2, p_subsMSISDN);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                found = true;
            } else {
                found = false;
            }
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isMappingExist]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isAssociateExist", "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[isMappingExist]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isExists found=" + found);
            }
        }
        return found;
    }

    /**
     * this method delete the mapping of user and profile from user_oth_profiles
     * 
     * @param p_con
     * @param p_setID
     * @param p_userID
     * @return int
     * @author Rajdeep
     * @throws BTSLBaseException
     */
    public int deleteUserProfileMapping(Connection p_con, String p_userID, String p_setID) throws BTSLBaseException {
        final String methodName = "deleteUserProfileMapping";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered ");
        	loggerValue.append(p_userID);
        	loggerValue.append(".......");
        	loggerValue.append(p_setID);
            log.debug(methodName, loggerValue);
        }
        
        int deleteCount = 0;
        final StringBuilder sbf = new StringBuilder();
        sbf.append("DELETE from user_oth_profiles WHERE user_id=? and set_id=?");
        final String query = sbf.toString();
        try(PreparedStatement  pstmt = p_con.prepareStatement(query);) {
          
            pstmt.setString(1, p_userID);
            pstmt.setString(2, p_setID);
            deleteCount = pstmt.executeUpdate();
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue );
            log.errorTrace(methodName, sqle);
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception:");
            loggerValue.append(sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[deleteUserProfileMapping]", "", "",
                "",  loggerValue.toString() );
            throw new BTSLBaseException(this, "isAssociateExist", "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            log.error(methodName,  loggerValue);
            log.errorTrace(methodName, e);
            loggerValue.setLength(0);
            loggerValue.append("Exception ");
            loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[deleteUserProfileMapping]", "", "",
                "",  loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting ");
            }
        }
        return deleteCount;
    }
}
