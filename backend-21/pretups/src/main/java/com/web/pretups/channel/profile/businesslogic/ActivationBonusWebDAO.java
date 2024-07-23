package com.web.pretups.channel.profile.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.ProfileMappingVO;
import com.btsl.pretups.channel.profile.businesslogic.ProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.RetSubsMappingVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.businesslogic.RedemptionVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

public class ActivationBonusWebDAO {

    private Log log = LogFactory.getLog(this.getClass().getName());
    private ActivationBonusWebQry activationBonusWebQry;

    public ActivationBonusWebDAO(){
    	activationBonusWebQry = (ActivationBonusWebQry)ObjectProducer.getObject(QueryConstants.ACTIVATION_BONUS_WEB_QRY, QueryConstants.QUERY_PRODUCER);
    }
    /**
     * rahul.dutt
     * searchSubscriberMapping searches mapping of subscriber with a retailer on
     * basis of entered subscriber msisdn
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_subscriberMsisdn
     * @return RetSubsMappingVO
     * @throws BTSLBaseException
     */
    public RetSubsMappingVO searchSubscriberMapping(Connection p_con, String p_networkCode, String p_subscriberMsisdn) throws BTSLBaseException {
        final String methodName = "searchSubscriberMapping";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode =");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_subscriberMsisdn=");
        	loggerValue.append(p_subscriberMsisdn);
            log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        RetSubsMappingVO retSubsMappingVO = null;
        try {
          
        	String sqlSelect =activationBonusWebQry.searchSubscriberMappingQry();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Select Query =");
            	loggerValue.append(sqlSelect);
                log.debug(methodName, loggerValue);
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_subscriberMsisdn);
            pstmt.setString(3, PretupsI.STATUS_ACTIVE);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                retSubsMappingVO =  RetSubsMappingVO.getInstance();
                retSubsMappingVO.setRetailername(rs.getString("user_name"));
                retSubsMappingVO.setRetailerMsisdn(rs.getString("msisdn"));
                retSubsMappingVO.setSubscriberMsisdn(rs.getString("subscriber_msisdn"));
                retSubsMappingVO.setRegisteredOn(rs.getDate("registered_on"));
                retSubsMappingVO.setPrevRetailerID(rs.getString("user_id"));
                retSubsMappingVO.setSubscriberType(rs.getString("subscriber_type"));
                retSubsMappingVO.setSetID(rs.getString("set_id"));
                retSubsMappingVO.setVersion(rs.getString("version"));
                retSubsMappingVO.setExpiryDate(rs.getDate("expiry_date"));
                retSubsMappingVO.setNetworkCode(rs.getString("network_code"));
                retSubsMappingVO.setParentName(rs.getString("parentName"));
                retSubsMappingVO.setParentMsisdn(rs.getString("parentMsisdn"));
                retSubsMappingVO.setOwnerName(rs.getString("ownerName"));
                retSubsMappingVO.setOwnerMsisdn(rs.getString("ownerMsisdn"));
                retSubsMappingVO.setCreatedBy(rs.getString("created_by"));
                retSubsMappingVO.setCreatedOn(rs.getTimestamp("created_on"));
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[searchSubscriberMapping]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[searchSubscriberMapping]", "",
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: ");
            }
        }
        return retSubsMappingVO;
    }

    /**
     * rahul.dutt
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_subscriberMsisdn
     * @param p_retailerMsisdn
     * @return RetSubsMappingVO
     * @throws BTSLBaseException
     */
    public RetSubsMappingVO searchNewRetailer(Connection p_con, String p_networkCode, String p_retailerMsisdn) throws BTSLBaseException {
        final String methodName = "searchNewRetailer";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode =");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_retailerMsisdn=");
        	loggerValue.append(p_retailerMsisdn);
            log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        RetSubsMappingVO retSubsMappingVO = null;
        try {

            String sqlSelect = activationBonusWebQry.searchNewRetailerQry();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Select Query =");
            	loggerValue.append(sqlSelect);
                log.debug(methodName, loggerValue);
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_retailerMsisdn);
            pstmt.setString(3, PretupsI.USER_STATUS_ACTIVE);
            pstmt.setString(4, PretupsI.USER_TYPE_CHANNEL);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                retSubsMappingVO = new RetSubsMappingVO();
                retSubsMappingVO.setNewRetailerMsisdn(rs.getString("msisdn"));
                retSubsMappingVO.setNewRetailername(rs.getString("user_name"));
                retSubsMappingVO.setNewRetailerID(rs.getString("user_id"));
                retSubsMappingVO.setParentName(rs.getString("parentName"));
                retSubsMappingVO.setParentMsisdn(rs.getString("parentMsisdn"));
                retSubsMappingVO.setOwnerName(rs.getString("ownerName"));
                retSubsMappingVO.setOwnerMsisdn(rs.getString("ownerMsisdn"));
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[searchNewRetailer]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[searchNewRetailer]", "", "", "",
                "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: retSubsMappingVO=" + retSubsMappingVO);
            }
        }
        return retSubsMappingVO;
    }

    /**
     * this method check whether association exist between user and Subscriber
     * activation profile
     * i.e Add Retailer-Subscriber Mapping
     * 
     * @param p_con
     * @param p_subsMSISDN
     * @return boolean value
     * @throws BTSLBaseException
     * @author Vikas.kumar
     */
    public boolean isSubscriberExist(Connection p_con, String p_subsMSISDN) throws BTSLBaseException {
        final String methodName = "isSubscriberExist";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered params p_subsMSISDN:");
        	loggerValue.append(p_subsMSISDN);
            log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer();
        sqlBuff.append("SELECT 1 FROM ACT_BONUS_SUBS_MAPPING ");
        sqlBuff.append("WHERE status<>'N'AND SUBSCRIBER_MSISDN = ? ");
        final String selectQuery = sqlBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Select Query =");
        	loggerValue.append(selectQuery);
            log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_subsMSISDN);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[isSubscriberExist]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(e.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[isSubscriberExist]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isSubscriberExist found=" + found);
            }
        }
        return found;
    }

    /**
     * rahul.dutt
     * rretailerSubsMappList
     * returns the list of subscribers activated by a retailer between a time
     * period
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_retailerMsisdn
     * @param p_fromDate
     * @param p_toDate
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList retailerSubsMappList(Connection p_con, String p_networkCode, String p_retailerMsisdn, String p_fromDate, String p_toDate) throws BTSLBaseException {
        final String methodName = "retailerSubsMappList";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode =");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_retailerMsisdn=");
        	loggerValue.append(p_retailerMsisdn);
        	loggerValue.append(" p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append(" p_toDate=");
        	loggerValue.append(p_toDate);
            log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        RetSubsMappingVO retSubsMappingVO = null;
        ArrayList list = null;
        try {
            list = new ArrayList();
            final java.util.Date fromDate = BTSLUtil.getDateFromDateString(p_fromDate);
            final java.util.Date toDate = BTSLUtil.getDateFromDateString(p_toDate);
            
             String sqlSelect = activationBonusWebQry.retailerSubsMappListQry();

            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Select Query =");
            	loggerValue.append(sqlSelect);
                log.debug(methodName, loggerValue);
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.USER_STATUS_TYPE);
            pstmt.setString(2, p_retailerMsisdn);
            pstmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(fromDate));
            pstmt.setDate(4, BTSLUtil.getSQLDateFromUtilDate(toDate));
            rs = pstmt.executeQuery();

            while (rs.next()) {
                retSubsMappingVO = new RetSubsMappingVO();
                retSubsMappingVO.setRetailerMsisdn(rs.getString("msisdn"));
                retSubsMappingVO.setSubscriberMsisdn(rs.getString("subscriber_msisdn"));
                retSubsMappingVO.setRetailername(rs.getString("user_name"));
                retSubsMappingVO.setRegisteredOn(rs.getDate("registered_on"));
                retSubsMappingVO.setSubscriberType(rs.getString("subscriber_type"));
                retSubsMappingVO.setExpiryDate(rs.getDate("expiry_date"));
                retSubsMappingVO.setStatus(rs.getString("lookup_name"));
                retSubsMappingVO.setApprovedOn(rs.getDate("approved_on"));
                list.add(retSubsMappingVO);
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[retailerSubsMappList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "searchSubscriberMapping", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[retailerSubsMappList]", "", "",
                "", "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: ");
            }
        }

        return list;
    }

    /**
     * This method insert the userid and profile details to user_oth_details
     * table
     * 
     * @param p_con
     * @param p_profileVO
     * @return int
     * @throws BTSLBaseException
     * @author Vikas.kumar
     */
    public int addOtherProfileForAssociate(Connection p_con, ProfileSetVO p_profileVO) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        final String methodName = "addOtherProfileForAssociate";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_profileVO= ");
        	loggerValue.append(p_profileVO);
            log.debug(methodName, loggerValue);
        }
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT INTO user_oth_profiles ( user_id, profile_type, set_id )");
            strBuff.append(" VALUES (?,?,?)");
            final String insertQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Insert Query =");
            	loggerValue.append(insertQuery);
                log.debug(methodName, loggerValue);
            }
            psmtInsert = p_con.prepareStatement(insertQuery);
            psmtInsert.setString(1, p_profileVO.getUserID());
            psmtInsert.setString(2, p_profileVO.getProfileType());
            psmtInsert.setString(3, p_profileVO.getSetId());

            insertCount = psmtInsert.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[addOtherProfileForAssociate]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(e.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[addOtherProfileForAssociate]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * This method update the association of profile with user
     * 
     * @author vikas.kumar
     * @param p_con
     * @param p_profileVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateOtherProfileForAssociation(Connection p_con, ProfileSetVO p_profileVO) throws BTSLBaseException {

        final String methodName = "updateOtherProfileForAssociation";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_profileVO= ");
        	loggerValue.append(p_profileVO);
            log.debug(methodName, loggerValue);
        }

        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;
        try {
            final StringBuffer updateQueryBuff = new StringBuffer(" UPDATE user_oth_profiles SET ");
            updateQueryBuff.append(" set_id=? WHERE user_id=? and profile_type=? ");
            final String insertQuery = updateQueryBuff.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Insert Query =");
            	loggerValue.append(insertQuery);
                log.debug(methodName, loggerValue);
            }
            pstmtUpdate = p_con.prepareStatement(insertQuery);
            pstmtUpdate.setString(1, p_profileVO.getSetId());
            pstmtUpdate.setString(2, p_profileVO.getUserID());
            pstmtUpdate.setString(3, p_profileVO.getProfileType());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[updateOtherProfileForAssociation]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, " Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusWebDAO[updateOtherProfileForAssociation]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting updateCount " + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * this method check whether association exist between user and activation
     * profile
     * 
     * @param p_con
     * @param p_userID
     * @return boolean
     * @throws BTSLBaseException
     * @author Vikas.kumar
     */
    public boolean isAssociateExist(Connection p_con, ProfileSetVO p_profileVO) throws BTSLBaseException {
        final String methodName = "isAssociateExist";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered params  p_profileVO::");
        	loggerValue.append(p_profileVO);
            log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM user_oth_profiles WHERE user_id=? and profile_type= ? ");
        final String selectQuery = sqlBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Select Query =");
        	loggerValue.append(selectQuery);
            log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_profileVO.getUserID());
            pstmtSelect.setString(2, p_profileVO.getProfileType());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            } else {
                found = false;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[isAssociateExist]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(e.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[isAssociateExist]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isExists found=" + found);
            }
        }
        return found;
    }

    /**
     * This method returns the list of profiles which are active for the
     * categories present in the available domain
     * and exist in the profile_mapping table.
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_networkCode
     * @param String
     *            p_domainCode
     * @return ArrayList
     * @throws BTSLBaseException
     * @author amit.singh
     */
    public ArrayList loadCategoryProfileMappingListByDomainCode(Connection p_con, String p_networkCode, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadCategoryProfileMappingListByDomainCode";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode =");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_domainCode=");
        	loggerValue.append(p_domainCode);
            log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT  pm.srv_class_or_category_code, c.category_name, pm.profile_type, pm.set_id, ps.set_name,  pm.is_default,");
        strBuff.append(" pm.created_on, pm.created_by, pm.modified_on, pm.modified_by, pm.network_code");
        strBuff.append(" FROM PROFILE_MAPPING PM,CATEGORIES C, PROFILE_SET ps");
        strBuff.append(" WHERE pm.NETWORK_CODE=? AND c.domain_code=?");
        strBuff.append(" AND ps.status=? AND  pm.set_id=ps.set_id");
        strBuff.append(" AND c.CATEGORY_CODE=pm.SRV_CLASS_OR_CATEGORY_CODE");
        strBuff.append(" order by pm.SRV_CLASS_OR_CATEGORY_CODE");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Select Query =");
        	loggerValue.append(sqlSelect);
            log.debug(methodName, loggerValue);
        }
        ArrayList list = null;
        try {
            list = new ArrayList();

            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_domainCode);
            pstmt.setString(3, PretupsI.STATUS_ACTIVE);
            rs = pstmt.executeQuery();
            ProfileMappingVO profileMappingVO = null;
            while (rs.next()) {
                profileMappingVO = new ProfileMappingVO();
                profileMappingVO.setCategoryCode(rs.getString("category_name"));
                profileMappingVO.setProfileType(rs.getString("profile_type"));
                profileMappingVO.setSetID(rs.getString("set_id"));
                profileMappingVO.setSetName(rs.getString("set_name"));
                profileMappingVO.setDefaultProfile(rs.getString("is_default"));
                profileMappingVO.setCreatedBy(rs.getString("created_by"));
                profileMappingVO.setModifiedBy(rs.getString("modified_by"));
                profileMappingVO.setCreatedOn(rs.getDate("created_on"));
                profileMappingVO.setModifiedOn(rs.getDate("modified_on"));
                profileMappingVO.setNetworkCode(rs.getString("network_code"));
                list.add(profileMappingVO);

            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusWebDAO[loadCategoryProfileMappingListByDomainCode]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusWebDAO[loadCategoryProfileMappingListByDomainCode]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: networkMap size=" + list.size());
            }
        }
        return list;
    }

    /**
     * This method is used to display Redemption details of channel users
     * 
     * @param p_con
     *            Connection
     * @param p_userId
     *            String
     * @param p_fromdate
     *            String
     * @param p_todate
     *            String
     * @param p_categoryCode
     *            String
     * @param p_domainCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList viewRedemptionenquiryDetails(Connection p_con, String p_userId, String p_fromdate, String p_todate, String p_categoryCode, String p_domainCode, String p_zoneCode) throws BTSLBaseException {
        final String methodName = "viewRedemptionenquiryDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId: ");
        	loggerValue.append(p_userId);
        	loggerValue.append(" fromdate=");
        	loggerValue.append(p_fromdate);
        	loggerValue.append(" todate=");
        	loggerValue.append(p_todate);
        	loggerValue.append(" domainCode=");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(" categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(" p_zoneCode=");
        	loggerValue.append(p_zoneCode);
            log.debug(methodName, loggerValue);
        }
        ArrayList redemptiondetails = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        RedemptionVO redemptionVO = null;
        try {
           
        	pstmtSelect =activationBonusWebQry.viewRedemptionenquiryDetailsQry(p_con,p_domainCode,p_categoryCode,p_userId,p_zoneCode,p_fromdate,p_todate);
           
            rs = pstmtSelect.executeQuery();
            redemptiondetails = new ArrayList();
            while (rs.next()) {
                redemptionVO = new RedemptionVO();
                redemptionVO.setUserName(rs.getString("user_name"));
                redemptionVO.setRedemptionDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("redemption_date"))));
                redemptionVO.setAmountTransfered(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("amount_transfered"))));
                redemptionVO.setMsisdn(rs.getString("msisdn"));
                redemptionVO.setPointsRedeemedStr(PretupsBL.getDisplayAmount(rs.getLong("points_redeemed")));
                redemptiondetails.add(redemptionVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[viewRedemptionenquiryDetails]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(e.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[viewRedemptionenquiryDetails]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: redemptiondetails size=" + redemptiondetails.size());
            }
        }
        return redemptiondetails;
    }

    /**
     * Method loadProfileMappingListForDelete
     * 
     * @author Vikas kumar
     *         This method is used to load the the subscribers on the basis of
     *         their
     *         MSISDN and networkCode for deleting the subscribers
     *         Method :loadSubsListForDelete
     * @param p_con
     *            java.sql.Connection
     * @param p_msisdnList
     *            ArrayList
     * @param p_networkCode
     *            String
     * @param p_invalidMsisdn
     *            StringBuffer
     * @return subList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadProfileMappingListForDelete(Connection p_con, ArrayList p_msisdnList, String p_networkCode, StringBuffer p_invalidMsisdn) throws BTSLBaseException {
        final String methodName = "loadProfileMappingListForDelete";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode =");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_msisdnList size=");
        	loggerValue.append(p_msisdnList.size());
        	loggerValue.append(" p_invalidMsisdn=");
        	loggerValue.append(p_invalidMsisdn);
            log.debug(methodName, loggerValue);
        }
        RetSubsMappingVO deleteSubsMappingVO = null;
        PreparedStatement pstmtSubList = null;
        final ArrayList subList = new ArrayList();
        ResultSet rs = null;
        try {
           String sqlSelect =activationBonusWebQry.loadProfileMappingListForDeleteQry();
            pstmtSubList = p_con.prepareStatement(sqlSelect);
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Select Query =");
            	loggerValue.append(sqlSelect);
                log.debug(methodName, loggerValue);
            }

            final int j = p_msisdnList.size();
            for (int i = 0; i < j; i++) {
                pstmtSubList.setString(1, (String) p_msisdnList.get(i));
                pstmtSubList.setString(2, p_networkCode);
                rs = pstmtSubList.executeQuery();
                pstmtSubList.clearParameters();
                if (rs.next()) {
                    deleteSubsMappingVO = new RetSubsMappingVO();
                    deleteSubsMappingVO.setAllowAction("N");
                    deleteSubsMappingVO.setSubscriberMsisdn(rs.getString("subscriber_msisdn"));
                    deleteSubsMappingVO.setRetailername(rs.getString("ch_user_name"));
                    deleteSubsMappingVO.setRetailerId(rs.getString("ch_user_id"));
                    deleteSubsMappingVO.setRetailerMsisdn(rs.getString("ch_user_msisdn"));
                    deleteSubsMappingVO.setParentName(rs.getString("p_user_name"));
                    deleteSubsMappingVO.setParentMsisdn(rs.getString("p_user_msisdn"));
                    deleteSubsMappingVO.setOwnerName(rs.getString("o_user_name"));
                    deleteSubsMappingVO.setOwnerMsisdn(rs.getString("o_user_msisdn"));
                    deleteSubsMappingVO.setActivatedOn(BTSLUtil.getDateStringFromDate(rs.getDate("registered_on")));
                    subList.add(deleteSubsMappingVO);
                } else {
                    p_invalidMsisdn.append(p_msisdnList.get(i));
                    p_invalidMsisdn.append(",");
                }
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusWebDAO[loadProfileMappingListForDelete]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(e.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusWebDAO[loadProfileMappingListForDelete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: subList.size() : =" + subList.size());
            }
        }// End of finally
        return subList;
    }

    /**
     * Method :changeMappingListStatusForDeletion
     * Method for Updating the status of the subscribers for deletion(change
     * Status Active('Y') to Delete('S'))
     * 
     * @author Vikas Kumar
     * @param p_con
     *            java.sql.Connection
     * @param p_updatedList
     *            ArrayList
     * @param p_userID
     *            String
     * @param p_networkID
     *            String
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int changeMappingListStatusForDeletion(Connection p_con, ArrayList p_updatedList, String p_userID, String p_networkID) throws BTSLBaseException {
        final String methodName = "changeMappingListStatusForDeletion";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_updatedList size =");
        	loggerValue.append(p_updatedList.size());
        	loggerValue.append(" p_userID=");
        	loggerValue.append(p_userID);
        	loggerValue.append(" p_networkID=");
        	loggerValue.append(p_networkID);
            log.debug(methodName, loggerValue);
        }
        PreparedStatement psmtUpdate = null;
        RetSubsMappingVO deleteSubsMappingVO = null;
        int updateCount = 0;
        try {
            final StringBuffer strBuff = new StringBuffer();

            strBuff.append(" UPDATE ACT_BONUS_SUBS_MAPPING SET status = ?, modified_by = ?, ");
            strBuff.append(" modified_on = ?  WHERE  status ='Y' and SUBSCRIBER_MSISDN = ? AND NETWORK_CODE = ? ");
            final String strUpdate = strBuff.toString();
            psmtUpdate = p_con.prepareStatement(strUpdate);

            if (log.isDebugEnabled()) {
                loggerValue.setLength(0);
            	loggerValue.append("Update Query =");
            	loggerValue.append(strUpdate);
                log.debug(methodName, loggerValue);
            }
            Date date = new Date();
            for (int i = 0, j = p_updatedList.size(); i < j; i++) {
                deleteSubsMappingVO = (RetSubsMappingVO) p_updatedList.get(i);
                psmtUpdate.setString(1, PretupsI.USER_STATUS_SUSPEND);
                psmtUpdate.setString(2, p_userID);
                psmtUpdate.setDate(3, BTSLUtil.getSQLDateFromUtilDate(date));
                psmtUpdate.setString(4, deleteSubsMappingVO.getSubscriberMsisdn());
                psmtUpdate.setString(5, p_networkID);
                
                updateCount = psmtUpdate.executeUpdate();
                updateCount++;
                psmtUpdate.clearParameters();
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusWebDAO[changeMappingListStatusForDeletion]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(e.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusWebDAO[changeMappingListStatusForDeletion]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting :: updateCount :" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * rahul.dutt
     * to generate list of service code that are present in table for particular
     * profile setid and version
     * 
     * @param p_con
     * @param p_actProifleSetId
     * @param p_actProfileSetVersion
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadActivationProfileServicesList(Connection p_con, String p_actProifleSetId, String p_actProfileSetVersion) throws BTSLBaseException {
        final String methodName = "loadActivationProfileServicesList";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_actProifleSetId =");
        	loggerValue.append(p_actProifleSetId);
        	loggerValue.append(" p_actProfileSetVersion=");
        	loggerValue.append(p_actProfileSetVersion);
            log.debug(methodName, loggerValue);
        }
        if (log.isDebugEnabled()) {

            log.debug(methodName, "Entered p_actProifleSetId=" + p_actProifleSetId + " p_actProfileSetVersion=" + p_actProfileSetVersion);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT DISTINCT pd.service_code,st.name FROM PROFILE_DETAILS pd,SERVICE_TYPE st ");
        strBuff.append(" WHERE pd.service_code=st.SERVICE_TYPE AND pd.set_id=? AND pd.VERSION=?");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Select Query =");
        	loggerValue.append(sqlSelect);
            log.debug(methodName, loggerValue);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_actProifleSetId);
            pstmtSelect.setString(2, p_actProfileSetVersion);
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("name"), rs.getString("service_code"));
                list.add(listValueVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusWebDAO[loadActivationProfileServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadActivationProfileServicesList()", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusWebDAO[loadActivationProfileServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadActivationProfileServicesList()", "error.general.processing");
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
                log.debug("loadActivationProfileServicesList()", "Exiting: ActivationProfileServiceList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * this method is used to load category wise associated profile list
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_categoryCode
     * @return ArrayList
     * @author vikas.kumar
     */
    public ArrayList loadProfileForOthAssociation(Connection p_con, String p_networkCode, String p_categoryCode) {
        final String methodName = "loadProfileForOthAssociation";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode =");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_categoryCode=");
        	loggerValue.append(p_categoryCode);
            log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ArrayList profileList = null;
        ProfileSetVO profileSetVO = null;
        ResultSet rs = null;
        try {
            profileList = new ArrayList();
            final StringBuffer sbf = new StringBuffer();
            sbf.append("SELECT DISTINCT ps.set_name c_set_name,pm.set_id c_set_id,ps.short_code c_short_code,pm.is_default, ");
            sbf.append("ps.profile_type c_profile_type  FROM PROFILE_SET ps,PROFILE_MAPPING pm ");
            sbf.append(" WHERE pm.is_default IN ('Y','N') AND ps.set_id = pm.SET_ID AND ps.NETWORK_CODE = pm.NETWORK_CODE ");
            sbf.append(" AND pm.SRV_CLASS_OR_CATEGORY_CODE= ? AND pm.NETWORK_CODE= ? and ps.PROFILE_TYPE=? ");

            final String selectQuery = sbf.toString();
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Select Query =");
            	loggerValue.append(selectQuery);
                log.debug(methodName, loggerValue);
            }
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_categoryCode);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, PretupsI.PROFILE_TYPE_ACTIVATION_BONUS);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileSetVO = new ProfileSetVO();
                profileSetVO.setProfileType(rs.getString("c_profile_type"));
                profileSetVO.setSetId(rs.getString("c_set_id"));
                profileSetVO.setSetName(rs.getString("c_set_name"));
                profileSetVO.setShortCode(rs.getString("c_short_code"));
                profileSetVO.setAllowAction(rs.getString("is_default"));
                profileList.add(profileSetVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[loadProfileForOthAssociation]",
                "", "", "", "SQL Exception:" + sqe.getMessage());

        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[loadProfileForOthAssociation]",
                "", "", "", "Exception:" + ex.getMessage());

        } finally {
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
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: profileList size=" + profileList.size());
            }
        }
        return profileList;
    }

    /**
     * this method check whether association exist between user and Subscriber
     * activation profile
     * 
     * @param p_con
     * @param p_msisdn
     * @return ListValueVO
     * @author Rajdeep
     * @throws BTSLBaseException
     */
    public ListValueVO userProfileMap(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String methodName = "userProfileMap";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_msisdn =");
        	loggerValue.append(p_msisdn);
            log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ListValueVO listVO = null;
        final StringBuffer sbf = new StringBuffer();
        sbf.append("SELECT u.user_id,u.msisdn,uop.set_id FROM users u,user_oth_profiles uop ");
        sbf.append("WHERE u.user_id=uop.user_id and u.msisdn=?");
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Select Query =");
        	loggerValue.append(sbf.toString());
            log.debug(methodName, loggerValue);
        }
        final String query = sbf.toString();
        try {
            pstmt = p_con.prepareStatement(query);
            pstmt.setString(1, p_msisdn);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                listVO = new ListValueVO(rs.getString("msisdn"), rs.getString("set_id"));
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[userProfileMap]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isAssociateExist", "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(e.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[userProfileMap]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                if (listVO == null) {
                    log.debug(methodName, "Exiting listVO== null");
                } else {
                    log.debug(methodName, "Exiting listVO=" + listVO.toString());
                }
            }

        }

        return listVO;

    }

    /**
     * This method checks in the USER_OTH_PROFILES Table for associated profiles
     * if any with the user of a particular category
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_str[]
     * @return int
     * @throws BTSLBaseException
     * @author amit.singh
     */

    public int checkUserAssociationForProfile(Connection p_con, String p_str[]) throws BTSLBaseException {
        final String methodName = "checkUserAssociationForProfile";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_str[].size() =");
        	loggerValue.append(p_str.length);
            log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        int insertCount = 0;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT uop.USER_ID, uop.PROFILE_TYPE, uop.SET_ID, u.CATEGORY_CODE  FROM USER_OTH_PROFILES uop, USERS u, CATEGORIES c");
        strBuff.append(" WHERE uop.SET_ID= ? AND u.USER_ID=uop.USER_ID AND uop.PROFILE_TYPE= ? AND c.CATEGORY_NAME= ?");
        strBuff.append(" AND u.CATEGORY_CODE=c.CATEGORY_CODE AND u.STATUS=?");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Select Query =");
        	loggerValue.append(sqlSelect);
            log.debug(methodName, loggerValue);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            if (p_str.length > 1) {
                pstmtSelect.setString(1, p_str[1]);
            } else {
                pstmtSelect.setString(1, "0");
            }
            pstmtSelect.setString(2, PretupsI.PROFILE_TYPE_ACTIVATION);
            pstmtSelect.setString(3, p_str[0]);
            pstmtSelect.setString(4, PretupsI.YES);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                insertCount++;
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusWebDAO[checkUserAssociationForProfile]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "checkUserAssociationForProfile()", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusWebDAO[checkUserAssociationForProfile]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "checkUserAssociationForProfile()", "error.general.processing");
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
                log.debug(methodName, "Exiting: insertcount" + insertCount);
            }
        }

        return insertCount;
    }

    /**
     * This Method is used for populating the list of retailersubsmappingVO from
     * the ACT_BONUS_SUBS_MAPPING table in database on the basis of
     * status(S/W) if any exist in the database.
     * 
     * @param Connection
     *            p_con
     * @return ArrayList
     * @throws BTSLBaseException
     * @author amit.singh
     */
    public ArrayList populateListFromTable(Connection p_con, String p_networkCode) throws BTSLBaseException {
        final String methodName = "populateListFromTable";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode =");
        	loggerValue.append(p_networkCode);
            log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
       
        RetSubsMappingVO retSubsMappingVO = null;
       String sqlSelect =activationBonusWebQry.populateListFromTableQry();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Select Query =");
        	loggerValue.append(sqlSelect);
            log.debug(methodName, loggerValue);
        }
        ArrayList list = null;
        try {
            list = new ArrayList();
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                retSubsMappingVO = new RetSubsMappingVO();
                retSubsMappingVO.setSubscriberMsisdn(rs.getString("SUBSCRIBER_MSISDN"));
                retSubsMappingVO.setRetailerMsisdn(rs.getString("Retailer_Msisdn"));
                retSubsMappingVO.setRetailername(rs.getString("Retailer_User_Name"));
                retSubsMappingVO.setParentMsisdn(rs.getString("Parent_Msisdn"));
                retSubsMappingVO.setParentName(rs.getString("Parent_User_Name"));
                retSubsMappingVO.setOwnerMsisdn(rs.getString("Owner_Msisdn"));
                retSubsMappingVO.setOwnerName(rs.getString("Owner_User_Name"));
                retSubsMappingVO.setRegisteredOn(rs.getDate("REGISTERED_ON"));
                retSubsMappingVO.setStatus(rs.getString("STATUS"));
                list.add(retSubsMappingVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[populateListFromTable]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[populateListFromTable]", "", "",
                "", "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: list size=" + list.size());
            }
        }
        return list;
    }

    /**
     * This method is used for approving the records for addition or deletion or
     * correction in ACT_BONUS_SUBS_MAPPING table
     * or updating the status of entries which are there for additon or deletion
     * or correction in ACT_BONUS_SUBS_MAPPING table
     * 
     * @param Connection
     *            con
     * @param ArrayList
     *            mappingList
     * @param ArrayList
     *            correctionList
     * @param String
     *            userId
     * @return int
     * @throws BTSLBaseException
     * @author amit.singh
     */

    public int approveMappinginDB(Connection con, ArrayList mappingList, ArrayList correctionList, String userId, String networkCode) throws BTSLBaseException {
        final String methodName = "approveMappinginDB";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: mappingList size =");
        	loggerValue.append(mappingList.size());
        	loggerValue.append(" userid=");
        	loggerValue.append(userId);
            log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        final ResultSet rs = null;
        final Date date = new Date();
        final StringBuffer strBuff = new StringBuffer();
        RetSubsMappingVO retSubsMappingVO = null;
        RetSubsMappingVO retailerSubsMappingVO = null;
        int updateCount = 0;
        strBuff.append("UPDATE ACT_BONUS_SUBS_MAPPING SET STATUS=? , MODIFIED_ON=?, MODIFIED_BY=?");
        strBuff.append(", APPROVED_ON=?, APPROVED_BY=? WHERE SUBSCRIBER_MSISDN=? AND STATUS=? AND NETWORK_CODE=?");
        final String sqlUpdate = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Update Query =");
        	loggerValue.append(sqlUpdate);
            log.debug(methodName, loggerValue);
        }
        int mappingListSize = mappingList.size();
        try {
            pstmt = con.prepareStatement(sqlUpdate);
            for (int i = 0; i < mappingListSize; i++) {
                retSubsMappingVO = (RetSubsMappingVO) mappingList.get(i);
                if (!BTSLUtil.isNullString(retSubsMappingVO.getAllowAction()) && retSubsMappingVO.getAllowAction().contains("add")) {
                    if (retSubsMappingVO.getAllowAction().contains(";")) {
                        pstmt.setString(1, PretupsI.YES);
                        pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
                        pstmt.setString(3, userId);
                        pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
                        pstmt.setString(5, userId);
                        pstmt.setString(6, retSubsMappingVO.getSubscriberMsisdn());
                        pstmt.setString(7, "W");
                        pstmt.setString(8, networkCode);
                        updateCount = pstmt.executeUpdate();
                        pstmt.clearParameters();
                        updateCount++;
                    } else if (retSubsMappingVO.getAllowAction().contains("_")) {
                        pstmt.setString(1, PretupsI.NO);
                        pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
                        pstmt.setString(3, userId);
                        pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
                        pstmt.setString(5, userId);
                        pstmt.setString(6, retSubsMappingVO.getSubscriberMsisdn());
                        pstmt.setString(7, "W");
                        pstmt.setString(8, networkCode);
                        updateCount = pstmt.executeUpdate();
                        pstmt.clearParameters();
                        updateCount++;
                    }
                } else if (!BTSLUtil.isNullString(retSubsMappingVO.getAllowAction()) && retSubsMappingVO.getAllowAction().contains("del")) {
                    if (retSubsMappingVO.getAllowAction().contains(";")) {
                        pstmt.setString(1, PretupsI.NO);
                        pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
                        pstmt.setString(3, userId);
                        pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
                        pstmt.setString(5, userId);
                        pstmt.setString(6, retSubsMappingVO.getSubscriberMsisdn());
                        pstmt.setString(7, "S");
                        pstmt.setString(8, networkCode);
                        updateCount = pstmt.executeUpdate();
                        pstmt.clearParameters();
                        updateCount++;
                    } else if (retSubsMappingVO.getAllowAction().contains("_")) {
                        pstmt.setString(1, PretupsI.YES);
                        pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
                        pstmt.setString(3, userId);
                        pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
                        pstmt.setString(5, userId);
                        pstmt.setString(6, retSubsMappingVO.getSubscriberMsisdn());
                        pstmt.setString(7, "S");
                        pstmt.setString(8, networkCode);
                        updateCount = pstmt.executeUpdate();
                        pstmt.clearParameters();
                        updateCount++;
                    }
                } else if (!BTSLUtil.isNullString(retSubsMappingVO.getAllowAction()) && retSubsMappingVO.getAllowAction().contains("cor")) {
                    int correctionListSize = correctionList.size();
                	for (int k = 0; k < correctionListSize; k++) {
                        retailerSubsMappingVO = (RetSubsMappingVO) correctionList.get(k);
                        if (retailerSubsMappingVO.getSubscriberMsisdn().equalsIgnoreCase(retSubsMappingVO.getSubscriberMsisdn())) {
                            if (retSubsMappingVO.getAllowAction().contains(";")) {
                                if ("S".equalsIgnoreCase(retailerSubsMappingVO.getStatus())) {
                                    pstmt.setString(1, PretupsI.NO);
                                    pstmt.setString(7, "S");
                                } else if ("W".equalsIgnoreCase(retailerSubsMappingVO.getStatus())) {
                                    pstmt.setString(1, PretupsI.YES);
                                    pstmt.setString(7, "W");
                                }
                                pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
                                pstmt.setString(3, userId);
                                pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
                                pstmt.setString(5, userId);
                                pstmt.setString(6, retailerSubsMappingVO.getSubscriberMsisdn());
                                pstmt.setString(8, networkCode);
                                updateCount = pstmt.executeUpdate();
                                pstmt.clearParameters();
                                updateCount++;
                            } else if (retSubsMappingVO.getAllowAction().contains("_")) {
                                if ("S".equalsIgnoreCase(retailerSubsMappingVO.getStatus())) {
                                    pstmt.setString(1, PretupsI.YES);
                                    pstmt.setString(7, "S");
                                } else if ("W".equalsIgnoreCase(retailerSubsMappingVO.getStatus())) {
                                    pstmt.setString(1, PretupsI.NO);
                                    pstmt.setString(7, "W");
                                }
                                pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
                                pstmt.setString(3, userId);
                                pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
                                pstmt.setString(5, userId);
                                pstmt.setString(6, retailerSubsMappingVO.getSubscriberMsisdn());
                                pstmt.setString(8, networkCode);
                                updateCount = pstmt.executeUpdate();
                                pstmt.clearParameters();
                                updateCount++;
                            }
                        }
                    }
                }
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[approveMappinginDB]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[approveMappinginDB]", "", "",
                "", "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method for loading Services List by Module wise.
     * service_code=service_code+service_type
     * Used in(UserAction,ChannelUserAction)
     * 
     * @author rahul.dutt
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_module
     *            String
     * @param p_catCode
     *            TODO
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadServicesList(Connection p_con, String p_networkCode, String p_module, String p_catCode) throws BTSLBaseException {
        final String methodName = "loadServicesList";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode =");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_module=");
        	loggerValue.append(p_module);
        	loggerValue.append(" p_catCode=");
        	loggerValue.append(p_catCode);
            log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT st.service_type,st.name,st.type ");
        strBuff.append("FROM service_type st,network_services ns ");
        strBuff.append(" WHERE st.external_interface = 'Y' AND st.status = 'Y' AND st.service_type = ns.service_type");
        strBuff.append(" AND ns.sender_network = ? AND ns.receiver_network = ? AND st.module = ? AND st.type <> ?");
        if (!BTSLUtil.isNullString(p_catCode)) {
            strBuff.append(" AND st.service_type in (SELECT CST.service_type FROM category_service_type CST WHERE CST.category_code=? and CST.network_code=?) ");
        }
        strBuff.append(" ORDER BY name");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Select Query =");
        	loggerValue.append(sqlSelect);
            log.debug(methodName, loggerValue);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, p_module);
            pstmt.setString(4, PretupsI.SERVICE_TYPE_IAT);
            if (!BTSLUtil.isNullString(p_catCode)) {
                pstmt.setString(5, p_catCode);
                pstmt.setString(6, p_networkCode);
            }
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type") + ":" + rs.getString("type"));
                vo.setType(rs.getString("type"));
                list.add(vo);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[loadServicesList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[loadServicesList]", "", "", "",
                "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: serviceList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * this method returns the set id of the Activation profile that is
     * associated with that retailer
     * returns null if no mapping exists
     * rahul.dutt
     * 
     * @param p_con
     * @param p_retailerID
     * @return String
     * @throws BTSLBaseException
     */
    public String searchNewRetailerProfile(Connection p_con, String p_retailerID) throws BTSLBaseException {
        final String methodName = "searchNewRetailerProfile";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_retailerID =");
        	loggerValue.append(p_retailerID);
            log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String profileSetId = null;
        try {
        	 
           pstmt= activationBonusWebQry.searchNewRetailerProfileQry(p_con,p_retailerID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                profileSetId = rs.getString("setId");
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[searchNewRetailerProfile]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[searchNewRetailerProfile]", "",
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: ");
            }
        }
        return profileSetId;
    }

    /**
     * This method is used for generating a list of active channel users in the
     * specified category within the available domain with thier
     * associated profiles if any.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            String
     * @param p_categoryCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     * @author amit.singh
     */
    public ArrayList activeUsersListWithProfiles(Connection p_con, String p_domainCode, String p_categoryCode, String p_networkCode) throws BTSLBaseException {
        final String methodName = "activeUsersListWithProfiles";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_domainCode =");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(" p_categoryCode=");
        	loggerValue.append(p_categoryCode);
            log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        RetSubsMappingVO retSubsMappingVO = null;
        String sqlSelect =activationBonusWebQry.activeUsersListWithProfilesQry();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Select Query =");
        	loggerValue.append(sqlSelect);
            log.debug(methodName, loggerValue);
        }
        ArrayList list = null;
        try {
            list = new ArrayList();
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_domainCode);
            pstmt.setString(2, p_categoryCode);
            pstmt.setString(3, p_categoryCode);
            pstmt.setString(4, p_networkCode);
            pstmt.setString(5, PretupsI.USER_TYPE_STAFF);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                retSubsMappingVO = new RetSubsMappingVO();
                retSubsMappingVO.setLoginId(rs.getString("LOGIN_ID"));
                retSubsMappingVO.setRetailerMsisdn(rs.getString("MSISDN"));
                retSubsMappingVO.setCategoryCode(rs.getString("CATEGORY_NAME"));
                retSubsMappingVO.setSetID(rs.getString("SET_ID"));
                retSubsMappingVO.setProfileType(rs.getString("PROFILE_TYPE"));
                list.add(retSubsMappingVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[activeUsersListWithProfiles]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[activeUsersListWithProfiles]",
                "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: list size=" + list.size());
            }
        }
        return list;
    }

    /**
     * This method is used for generating a list of active profiles in the
     * category available in the selected domain.
     * 
     * @param p_con
     * @param p_domainCode
     * @param p_categoryCode
     * @return ArrayList
     * @throws BTSLBaseException
     * @author amit.singh
     */
    public ArrayList activeProfilesInDomainCategory(Connection p_con, String p_domainCode, String p_categoryCode, String p_networkCode) throws BTSLBaseException {
        final String methodName = "activeProfilesInDomainCategory";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_domainCode =");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(" p_categoryCode=");
        	loggerValue.append(p_categoryCode);
            log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        ProfileSetVO profileSetVO = null;
        strBuff.append("SELECT pm.SRV_CLASS_OR_CATEGORY_CODE, c.CATEGORY_NAME, ps.SET_ID, ps.SET_NAME, ps.SHORT_CODE, pm.IS_DEFAULT");
        strBuff.append(" FROM PROFILE_SET ps, PROFILE_MAPPING pm, CATEGORIES c");
        strBuff.append(" WHERE pm.SRV_CLASS_OR_CATEGORY_CODE= CASE ? WHEN 'ALL' THEN c.CATEGORY_CODE ELSE ? END");
        strBuff.append(" AND pm.SRV_CLASS_OR_CATEGORY_CODE=c.CATEGORY_CODE AND c.DOMAIN_CODE=?");
        strBuff.append(" AND c.STATUS='Y' AND ps.STATUS='Y' AND pm.SET_ID=ps.SET_ID AND pm.NETWORK_CODE=?");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Select Query =");
        	loggerValue.append(sqlSelect);
            log.debug(methodName, loggerValue);
        }
        ArrayList list = null;
        try {
            list = new ArrayList();
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_categoryCode);
            pstmt.setString(2, p_categoryCode);
            pstmt.setString(3, p_domainCode);
            pstmt.setString(4, p_networkCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileSetVO = new ProfileSetVO();
                profileSetVO.setSetId(SqlParameterEncoder.encodeParams(rs.getString("SET_ID")) + ":" + SqlParameterEncoder.encodeParams(rs.getString("IS_DEFAULT")) + ":" +
                		SqlParameterEncoder.encodeParams(rs.getString("CATEGORY_NAME")));
                profileSetVO.setSetName(SqlParameterEncoder.encodeParams(rs.getString("SET_NAME")));
                profileSetVO.setShortCode(SqlParameterEncoder.encodeParams(rs.getString("SHORT_CODE")));
                list.add(profileSetVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusWebDAO[activeProfilesInDomainCategory]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(ex.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusWebDAO[activeProfilesInDomainCategory]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: list size=" + list.size());
            }
        }
        return list;
    }

    /**
     * This method update the user_oth_profiles table for the Batch associate
     * profile , here
     * first the record if exist is picked for the userid and depending upon the
     * input excel file setid parameter
     * an update or delete or insert query is executed.
     * 
     * @param p_con
     *            Connection
     * @param p_list
     *            ArrayList
     * @return int
     * @throws BTSLBaseException
     * @throws SQLException
     * @author amit.singh
     */
    public int updateUserOtherProfileForBatchAssociation(Connection p_con, ArrayList p_list) throws BTSLBaseException, SQLException {
        final String methodName = "updateUserOtherProfileForBatchAssociation";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_list Size =");
        	loggerValue.append(p_list.size());
            log.debug(methodName, loggerValue);
        }
        
        final StringBuffer selectQueryBuff = new StringBuffer("SELECT uop.user_id, uop.set_id, uop.profile_type ");
        selectQueryBuff.append("FROM USER_OTH_PROFILES uop, USERS u WHERE uop.USER_ID=u.USER_ID ");
        selectQueryBuff.append("AND u.LOGIN_ID=?");
        final String selectQuery = selectQueryBuff.toString();
        
        final StringBuffer selectmsisdnQueryBuff = new StringBuffer("SELECT uop.user_id, uop.set_id, uop.profile_type ");
        selectmsisdnQueryBuff.append("FROM USER_OTH_PROFILES uop, USERS u WHERE uop.USER_ID=u.USER_ID ");
        selectmsisdnQueryBuff.append("AND u.MSISDN=?");
        final String selectMsisdnQuery = selectmsisdnQueryBuff.toString();
        
        final StringBuffer updateQueryBuff = new StringBuffer("UPDATE user_oth_profiles SET set_id=? ");
        updateQueryBuff.append("WHERE user_id=?");
        final String updateQuery = updateQueryBuff.toString();
        
        final StringBuffer deleteQueryBuff = new StringBuffer("DELETE FROM user_oth_profiles ");
        deleteQueryBuff.append("WHERE user_id=?");
        final String deleteQuery = deleteQueryBuff.toString();
        
        final StringBuffer queryBuff = new StringBuffer("SELECT u.USER_ID FROM users u WHERE ");
        queryBuff.append("u.LOGIN_ID=?");
        final String selectLoginQuery = queryBuff.toString();

        final StringBuffer selectqueryBuff = new StringBuffer("SELECT u.USER_ID FROM users u WHERE ");
        selectqueryBuff.append("u.MSISDN=?");
        final String selectmsisdnQuery = selectqueryBuff.toString();

        final StringBuffer insertQueryBuff = new StringBuffer("INSERT INTO user_oth_profiles (user_id,");
        insertQueryBuff.append(" set_id, profile_type) values (?,?,?)");
        final String insertQuery = insertQueryBuff.toString();
        

        ResultSet rs = null;
        int executeCount = 0;
        try(	PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);
        		PreparedStatement pstmtSelectMsisdn = p_con.prepareStatement(selectMsisdnQuery);
        		PreparedStatement pstmtUpdate = p_con.prepareStatement(updateQuery);
        		PreparedStatement pstmtDelete = p_con.prepareStatement(deleteQuery);
        		PreparedStatement psmtselectLogin = p_con.prepareStatement(selectLoginQuery);
        		PreparedStatement psmtselectMsisdn = p_con.prepareStatement(selectmsisdnQuery);
        		PreparedStatement pstmtInsert = p_con.prepareStatement(insertQuery);
        		
        		) {
        RetSubsMappingVO retSubsMappingVO = null;
        for (int i = 0, j = p_list.size(); i < j; i++) {
            boolean bothExists = true;
            boolean checkQuery = false;
            retSubsMappingVO = (RetSubsMappingVO) p_list.get(i);
       
                if (!BTSLUtil.isNullString(retSubsMappingVO.getLoginId())) {
                    pstmtSelect.setString(1, retSubsMappingVO.getLoginId());
                    bothExists = false;
                    if (log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                    	loggerValue.append("Select Query =");
                    	loggerValue.append(selectQuery);
                        log.debug(methodName, loggerValue);
                    }
                    rs = pstmtSelect.executeQuery();
                } else if (!BTSLUtil.isNullString(retSubsMappingVO.getRetailerMsisdn()) && bothExists) {
                    pstmtSelectMsisdn.setString(1, retSubsMappingVO.getRetailerMsisdn());
                    if (log.isDebugEnabled()) {
                    	loggerValue.setLength(0);
                    	loggerValue.append("Select Query =");
                    	loggerValue.append(selectMsisdnQuery);
                        log.debug(methodName, loggerValue);
                    }
                    rs = pstmtSelectMsisdn.executeQuery();
                }
                while (rs.next()) {
                    retSubsMappingVO.setUserID(rs.getString("user_id"));
                    if (!BTSLUtil.isNullString(retSubsMappingVO.getSetID())) {
                        checkQuery = true;
                        if (log.isDebugEnabled()) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("Update Query =");
                        	loggerValue.append(updateQuery);
                            log.debug(methodName, loggerValue);
                        }
                        pstmtUpdate.setString(1, retSubsMappingVO.getSetID());
                        pstmtUpdate.setString(2, retSubsMappingVO.getUserID());
                        executeCount = pstmtUpdate.executeUpdate();
                        executeCount++;
                    }
                    if (BTSLUtil.isNullString(retSubsMappingVO.getSetID())) {
                        checkQuery = true;
                        if (log.isDebugEnabled()) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("Delete Query =");
                        	loggerValue.append(deleteQuery);
                            log.debug(methodName, loggerValue);
                        }
                        pstmtDelete.setString(1, retSubsMappingVO.getUserID());
                        executeCount = pstmtDelete.executeUpdate();
                        executeCount++;
                    }
                }
                if (!checkQuery) {
                    boolean loginExist = true;

                    if (!BTSLUtil.isNullString(retSubsMappingVO.getLoginId())) {
                        psmtselectLogin.setString(1, retSubsMappingVO.getLoginId());
                        if (log.isDebugEnabled()) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("Select Query =");
                        	loggerValue.append(selectLoginQuery);
                            log.debug(methodName, loggerValue);
                        }
                        rs = psmtselectLogin.executeQuery();
                        loginExist = false;
                    } else if (BTSLUtil.isNullString(retSubsMappingVO.getLoginId()) && loginExist) {
                        psmtselectMsisdn.setString(1, retSubsMappingVO.getRetailerMsisdn());
                        if (log.isDebugEnabled()) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("Select Query =");
                        	loggerValue.append(selectmsisdnQuery);
                            log.debug(methodName, loggerValue);
                        }
                        rs = psmtselectMsisdn.executeQuery();
                    }
                    while (rs.next()) {
                        retSubsMappingVO.setUserID(rs.getString("USER_ID"));
                    }
                    if (!BTSLUtil.isNullString(retSubsMappingVO.getSetID())) {
                        if (log.isDebugEnabled()) {
                        	loggerValue.setLength(0);
                        	loggerValue.append("Insert Query =");
                        	loggerValue.append(insertQuery);
                            log.debug(methodName, loggerValue);
                        }
                        pstmtInsert.setString(1, retSubsMappingVO.getUserID());
                        pstmtInsert.setString(2, retSubsMappingVO.getSetID());
                        pstmtInsert.setString(3, PretupsI.ACT_PROF_TYPE);
                        executeCount = pstmtInsert.executeUpdate();
                        executeCount++;
                    }
                }
            }
        } catch (SQLException sqle) {
            	loggerValue.setLength(0);
            	loggerValue.append("SQL Exception : ");
            	loggerValue.append(sqle.getMessage());
                log.error(methodName, loggerValue);
                log.errorTrace(methodName, sqle);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBounsDAO[updateUserOtherProfileForBatchAssociation]", "", "", "", "SQL Exception:" + sqle.getMessage());
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            } catch (Exception e) {
            	loggerValue.setLength(0);
            	loggerValue.append(" Exception : ");
            	loggerValue.append(e.getMessage());
            	log.error(methodName, loggerValue);
                log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusWebDAO[updateUserOtherProfileForBatchAssociation]", "", "", "", "Exception:" + e.getMessage());
                throw new BTSLBaseException(this, methodName, "error.general.processing");
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception ex) {
                    log.errorTrace(methodName, ex);
                }
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "Exiting executeCount= " + executeCount);
                }
            }
        
   
        return executeCount;
    }

    /**
     * added by vikas kumar for check the association
     * exist of channel user with
     * 
     * @param p_con
     * @param p_setID
     * @param p_userID
     * @return boolean value
     * @throws BTSLBaseException
     */
    public boolean isProfileAssociateWithUserExist(Connection p_con, String p_setID, String p_userID) throws BTSLBaseException {
        final String methodName = "isProfileAssociateWithUserExist";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: params p_setID: =");
        	loggerValue.append(p_setID);
        	loggerValue.append(" p_userID=");
        	loggerValue.append(p_userID);
            log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer();
        sqlBuff.append("SELECT 1 FROM USER_OTH_PROFILES WHERE USER_ID = ? AND ");
        sqlBuff.append(" PROFILE_TYPE = ? AND SET_ID = ? ");

        final String selectQuery = sqlBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Select Query =");
        	loggerValue.append(selectQuery);
            log.debug(methodName, loggerValue);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userID);
            pstmtSelect.setString(2, PretupsI.ACT_PROF_TYPE);
            pstmtSelect.setString(3, p_setID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            } else {
                return false;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusWebDAO[isProfileAssociateWithUserExist]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(e.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[isProfileAssociateWithUserExist]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isProfileAssociateWithUserExist found=" + found);
            }
        }
        return found;
    }

    /**
     * this method is used for check the user is valid or not
     * in the based of selection criteria for redemption report
     * 
     * @param p_con
     * @param p_zoneCode
     * @param p_domainCode
     * @param p_categoryCode
     * @param p_channelUser
     * @return ArrayList of Users
     * @author vikas.kumar
     * @throws BTSLBaseException
     */
    // public ArrayList validateUserdetails(Connection p_con,String
    // p_zoneCode,String p_domainCode,String p_categoryCode,String
    // p_channelUser)
    public ArrayList validateUserdetails(Connection p_con, String p_zoneCode, String p_domainCode, String p_categoryCode, String p_channelUser, String p_userIdLoggedUser) throws BTSLBaseException {
        final String methodName = "validateUserdetails";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_zoneCode =");
        	loggerValue.append(p_zoneCode);
            log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList userList = new ArrayList();
       
        

        try {
        	pstmtSelect =activationBonusWebQry.validateUserdetailsQry(p_con,p_zoneCode,p_userIdLoggedUser,p_domainCode,p_categoryCode,p_channelUser);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                userList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqle.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[validateUserdetails]", "", "",
                "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(e.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[validateUserdetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting validateUserdetails list size=" + userList.size());
            }
        }
        return userList;
    }

    /**
     * method loadBonusPointDetails()is used to load bonus point enquiry
     * 
     * @param p_con
     * @param p_userId
     * @param p_fromdate
     * @param p_todate
     * @param p_categoryCode
     * @param p_domainCode
     * @param p_zoneCode
     * @return
     * @throws BTSLBaseException
     * @author vikas.kumar
     */
    public ArrayList loadBonusPointDetails(Connection p_con, String p_userId, String p_fromdate, String p_todate, String p_categoryCode, String p_domainCode, String p_zoneCode) throws BTSLBaseException {
        final String methodName = "loadBonusPointDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userId =");
        	loggerValue.append(p_userId);
        	loggerValue.append(" fromdate=");
        	loggerValue.append(p_fromdate);
        	loggerValue.append(" todate=");
        	loggerValue.append(p_todate);
        	loggerValue.append(" domainCode=");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(" categoryCode=");
        	loggerValue.append(p_categoryCode);
        	loggerValue.append(" p_zoneCode=");
        	loggerValue.append(p_zoneCode);
            log.debug(methodName, loggerValue);
        }
        ArrayList detailsList = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        RedemptionVO redemptionVO = null;
        try {
            pstmtSelect =activationBonusWebQry.loadBonusPointDetailsQry(p_con,p_zoneCode,p_userId,p_domainCode,p_categoryCode,p_fromdate,p_todate);
            rs = pstmtSelect.executeQuery();
            detailsList = new ArrayList();
            // Added by Diwakar on 05/05/15 for hadning of LMS bonus
            String profileType = null;
            while (rs.next()) {
                profileType = new String();
                profileType = rs.getString("profile_type");

                redemptionVO = new RedemptionVO();
                redemptionVO.setUserName(rs.getString("user_name"));
                redemptionVO.setPointDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("points_date"))));
                redemptionVO.setMsisdn(rs.getString("msisdn"));
                if (PretupsI.LMS_PROFILE_TYPE.equalsIgnoreCase(profileType)) {
                    redemptionVO.setPointsRedeemedStr(String.valueOf(rs.getLong("points")));
                    redemptionVO.setAccmulatedPointsStr(String.valueOf(rs.getLong("ACCUMULATED_POINTS")));
                } else {
                    redemptionVO.setPointsRedeemedStr(PretupsBL.getDisplayAmount(rs.getLong("points")));
                    redemptionVO.setAccmulatedPointsStr(PretupsBL.getDisplayAmount(rs.getLong("ACCUMULATED_POINTS")));
                }
                redemptionVO.setTransferIdStr(rs.getString("TRANSFER_ID"));
                redemptionVO.setRedemptionIdStr(rs.getString("LAST_REDEMPTION_ID"));
                redemptionVO.setProfileType(profileType);
                try {
                    redemptionVO.setRedemptionDateStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("LAST_REDEMPTION_ON"))));
                } catch (RuntimeException e) {
                    log.errorTrace(methodName, e);
                }
                redemptionVO.setEntryDateStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("ENTRY_DATE"))));
                detailsList.add(redemptionVO);
                profileType = null;
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
            log.error(methodName, loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[loadBonusPointDetails]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append(" Exception : ");
        	loggerValue.append(e.getMessage());
        	log.error(methodName, loggerValue);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusWebDAO[loadBonusPointDetails]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: redemptiondetails size=" + detailsList.size());
            }
        }
        return detailsList;
    }
}
