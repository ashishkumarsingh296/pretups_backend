package com.btsl.pretups.channel.profile.businesslogic;

/**
 * @(#)TransferProfileDAO.java
 * 
 *                             Created on Created by History
 *                             ------------------------------------------------
 *                             --------------------------------
 *                             Jul 3, 2005 manoj kumar creation
 *                             Aug 10,2006 Ankit Zindal Changes against
 *                             ID=TRFPROFILESTATUS
 *                             ------------------------------------------------
 *                             --------------------------------
 *                             Copyright(c) 2005 Bharti Telesoft Ltd.
 * 
 * */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.OracleUtil;

/**
 * 
 */
public class TransferProfileDAO {
    /**
     * Field log.
     */
    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * Constructor for TransferProfileDAO.
     */
    public TransferProfileDAO() {
        super();
    }

    /**
     * Method :loadTransferProfileListVO
     * this method load the transfer profile List from transfer_profile table
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pProfileId
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public TransferProfileVO loadTransferProfileListVO(Connection pCon, String pProfileId) throws BTSLBaseException {
        final String methodName = "loadTransferProfileListVO";
    	StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pProfileId");
        	loggerValue.append(pProfileId);
            log.debug(methodName,loggerValue);
        }
         
         
        TransferProfileVO profileVO = null;
        final StringBuilder strBuff = new StringBuilder("SELECT profile_id,profile_name ,description,status,network_code,category_code ,modified_on ");
        strBuff.append("  FROM transfer_profile WHERE profile_id =? ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        try(PreparedStatement pstmtSelect = pCon.prepareStatement(sqlSelect);) {
           
            pstmtSelect.setString(1, pProfileId);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                profileVO = new TransferProfileVO();
                profileVO.setProfileId(rs.getString("profile_id"));
                profileVO.setProfileName(rs.getString("profile_name"));
                profileVO.setDescription(rs.getString("description"));
                profileVO.setStatus(rs.getString("status"));
                profileVO.setNetworkCode(rs.getString("network_code"));
                profileVO.setCategory(rs.getString("category_code"));
                profileVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadTransferProfileListVO]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadTransferProfileListVO]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: profileVO =");
            	loggerValue.append(profileVO.toString());
                log.debug(methodName,loggerValue);
            }
        }
        return profileVO;
    }

    /**
     * Method :loadTransferProfileThroughProfileID
     * this method the transfer Profile details behalf of pProfileId from
     * transfer_profile table
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pProfileId
     * @param pNetworkCode
     *            java.lang.String
     * @param pCategoryCode
     *            String
     * @param pIsProductLoadRequired
     *            boolean
     * @return profileVO ProfileVO
     * @throws BTSLBaseException
     */
    public TransferProfileVO loadTransferProfileThroughProfileID(Connection pCon, String pProfileId, String pNetworkCode, String pCategoryCode, boolean pIsProductLoadRequired) throws BTSLBaseException {
        final String methodName = "loadTransferProfileThroughProfileID";
    	StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
	    	loggerValue.append("Entered:pProfileId=");
	    	loggerValue.append(pProfileId);
	    	loggerValue.append(",pNetworkCode=");
	    	loggerValue.append(pNetworkCode);
	    	loggerValue.append(",p_category=");
	    	loggerValue.append(pCategoryCode);
            log.debug(methodName,loggerValue);
        }
         
         
        TransferProfileVO profileVO = null;
        final StringBuilder strBuff = new StringBuilder("SELECT TP.profile_id,TP.profile_name,TP.short_name,TP.status,TP.description,");
        strBuff.append(" TP.daily_transfer_in_count,TP.daily_transfer_in_value,");
        strBuff.append(" TP.weekly_transfer_in_count,TP.weekly_transfer_in_value,");
        strBuff.append(" TP.monthly_transfer_in_count,TP.monthly_transfer_in_value,");
        strBuff.append(" TP.daily_transfer_out_count,TP.daily_transfer_out_value,");
        strBuff.append(" TP.weekly_transfer_out_count,TP.weekly_transfer_out_value,");
        strBuff.append(" TP.monthly_transfer_out_count,TP.monthly_transfer_out_value,");
        strBuff.append(" TP.daily_subscriber_out_count , TP.daily_subscriber_out_value,");
        strBuff.append(" TP.weekly_subscriber_out_count , TP.weekly_subscriber_out_value,");
        strBuff.append(" TP.monthly_subscriber_out_count , TP.monthly_subscriber_out_value,");
        strBuff.append(" TP.outside_daily_in_count,TP.outside_daily_in_value,");
        strBuff.append(" TP.outside_weekly_in_count,TP.outside_weekly_in_value,");
        strBuff.append(" TP.outside_monthly_in_count,TP.outside_monthly_in_value,");
        strBuff.append(" TP.outside_daily_out_count,TP.outside_daily_out_value,");
        strBuff.append(" TP.outside_weekly_out_count,TP.outside_weekly_out_value,");
        strBuff.append(" TP.outside_monthly_out_count,TP.outside_monthly_out_value,");
        strBuff.append(" TP.modified_on,TP.network_code,TP.category_code,TP.alt_daily_transfer_in_count, ");
        strBuff.append("TP.alt_daily_transfer_in_value, TP.alt_weekly_transfer_in_count, ");
        strBuff.append("TP.alt_weekly_transfer_in_value, TP.alt_monthly_transfer_in_count, ");
        strBuff.append("TP.alt_monthly_transfer_in_value, TP.alt_daily_transfer_out_count, ");
        strBuff.append("TP.alt_daily_transfer_out_value, TP.alt_weekly_transfer_out_count, ");
        strBuff.append("TP.alt_weekly_transfer_out_value, TP.alt_monthly_transfer_out_count, ");
        strBuff.append("TP.alt_monthly_transfer_out_value, TP.alt_outside_daily_in_count, ");
        strBuff.append("TP.alt_outside_daily_in_value, TP.alt_outside_weekly_in_count, ");
        strBuff.append("TP.alt_outside_weekly_in_value, TP.alt_outside_monthly_in_count, ");
        strBuff.append("TP.alt_outside_monthly_in_value, TP.alt_outside_daily_out_count, ");
        strBuff.append("TP.alt_outside_daily_out_value, TP.alt_outside_weekly_out_count, ");
        strBuff.append("TP.alt_outside_weekly_out_value, TP.alt_outside_monthly_out_count, ");
        strBuff.append("TP.alt_outside_monthly_out_value, TP.alt_daily_subs_out_count, ");
        strBuff.append("TP.alt_daily_subs_out_value, TP.alt_weekly_subs_out_count, ");
        strBuff.append("TP.alt_weekly_subs_out_value, TP.alt_monthly_subs_out_count, ");
        strBuff.append("TP.alt_monthly_subs_out_value, TP.parent_profile_id, TP.is_default ,");

        // 6.4 changes
        strBuff.append(" daily_subscriber_in_count, daily_subscriber_in_value,  weekly_subscriber_in_count, ");
        strBuff.append(" weekly_subscriber_in_value, monthly_subscriber_in_count, monthly_subscriber_in_value, ");
        strBuff.append("alt_daily_subs_in_count, alt_daily_subs_in_value, ");
        strBuff.append("alt_weekly_subs_in_count, alt_weekly_subs_in_value, ");
        strBuff.append("alt_monthly_subs_in_count, alt_monthly_subs_in_value ");

        strBuff.append(" FROM transfer_profile TP");
        strBuff.append(" WHERE TP.profile_id=?  AND TP.network_code=? and TP.category_code=? ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, pProfileId);
            pstmt.setString(2, pNetworkCode);
            pstmt.setString(3, pCategoryCode);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                profileVO = new TransferProfileVO();
                profileVO.setProfileId(rs.getString("profile_id"));
                profileVO.setProfileName(rs.getString("profile_name"));
                profileVO.setShortName(rs.getString("short_name"));
                profileVO.setStatus(rs.getString("status"));
                profileVO.setDescription(rs.getString("description"));
                profileVO.setDailyInCount(rs.getLong("daily_transfer_in_count"));
                profileVO.setDailyInValue(rs.getLong("daily_transfer_in_value"));
                profileVO.setWeeklyInCount(rs.getLong("weekly_transfer_in_count"));
                profileVO.setWeeklyInValue(rs.getLong("weekly_transfer_in_value"));
                profileVO.setMonthlyInCount(rs.getLong("monthly_transfer_in_count"));
                profileVO.setMonthlyInValue(rs.getLong("monthly_transfer_in_value"));
                profileVO.setDailyOutCount(rs.getLong("daily_transfer_out_count"));
                profileVO.setDailyOutValue(rs.getLong("daily_transfer_out_value"));
                profileVO.setWeeklyOutCount(rs.getLong("weekly_transfer_out_count"));
                profileVO.setWeeklyOutValue(rs.getLong("weekly_transfer_out_value"));
                profileVO.setMonthlyOutCount(rs.getLong("monthly_transfer_out_count"));
                profileVO.setMonthlyOutValue(rs.getLong("monthly_transfer_out_value"));
                profileVO.setDailySubscriberOutCount(rs.getLong("daily_subscriber_out_count"));
                profileVO.setDailySubscriberOutValue(rs.getLong("daily_subscriber_out_value"));
                profileVO.setWeeklySubscriberOutCount(rs.getLong("weekly_subscriber_out_count"));
                profileVO.setWeeklySubscriberOutValue(rs.getLong("weekly_subscriber_out_value"));
                profileVO.setMonthlySubscriberOutCount(rs.getLong("monthly_subscriber_out_count"));
                profileVO.setMonthlySubscriberOutValue(rs.getLong("monthly_subscriber_out_value"));
                profileVO.setUnctrlDailyInCount(rs.getLong("outside_daily_in_count"));
                profileVO.setUnctrlDailyInValue(rs.getLong("outside_daily_in_value"));
                profileVO.setUnctrlWeeklyInCount(rs.getLong("outside_Weekly_in_count"));
                profileVO.setUnctrlWeeklyInValue(rs.getLong("outside_Weekly_in_value"));
                profileVO.setUnctrlMonthlyInCount(rs.getLong("outside_Monthly_in_count"));
                profileVO.setUnctrlMonthlyInValue(rs.getLong("outside_Monthly_in_value"));
                profileVO.setUnctrlDailyOutCount(rs.getLong("outside_daily_out_count"));
                profileVO.setUnctrlDailyOutValue(rs.getLong("outside_daily_out_value"));
                profileVO.setUnctrlWeeklyOutCount(rs.getLong("outside_Weekly_out_count"));
                profileVO.setUnctrlWeeklyOutValue(rs.getLong("outside_Weekly_out_value"));
                profileVO.setUnctrlMonthlyOutCount(rs.getLong("outside_Monthly_out_count"));
                profileVO.setUnctrlMonthlyOutValue(rs.getLong("outside_Monthly_out_value"));
                profileVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                profileVO.setNetworkCode(rs.getString("network_code"));
                profileVO.setCategory(rs.getString("category_code"));

                // alerting count/value

                profileVO.setDailyInAltCount(rs.getLong("alt_daily_transfer_in_count"));
                profileVO.setDailyInAltValue(rs.getLong("alt_daily_transfer_in_value"));
                profileVO.setWeeklyInAltCount(rs.getLong("alt_weekly_transfer_in_count"));
                profileVO.setWeeklyInAltValue(rs.getLong("alt_weekly_transfer_in_value"));
                profileVO.setMonthlyInAltCount(rs.getLong("alt_monthly_transfer_in_count"));
                profileVO.setMonthlyInAltValue(rs.getLong("alt_monthly_transfer_in_value"));
                profileVO.setDailyOutAltCount(rs.getLong("alt_daily_transfer_out_count"));
                profileVO.setDailyOutAltValue(rs.getLong("alt_daily_transfer_out_value"));
                profileVO.setWeeklyOutAltCount(rs.getLong("alt_weekly_transfer_out_count"));
                profileVO.setWeeklyOutAltValue(rs.getLong("alt_weekly_transfer_out_value"));
                profileVO.setMonthlyOutAltCount(rs.getLong("alt_monthly_transfer_out_count"));
                profileVO.setMonthlyOutAltValue(rs.getLong("alt_monthly_transfer_out_value"));

                profileVO.setUnctrlDailyInAltCount(rs.getLong("alt_outside_daily_in_count"));
                profileVO.setUnctrlDailyInAltValue(rs.getLong("alt_outside_daily_in_value"));
                profileVO.setUnctrlWeeklyInAltCount(rs.getLong("alt_outside_Weekly_in_count"));
                profileVO.setUnctrlWeeklyInAltValue(rs.getLong("alt_outside_Weekly_in_value"));
                profileVO.setUnctrlMonthlyInAltCount(rs.getLong("alt_outside_Monthly_in_count"));
                profileVO.setUnctrlMonthlyInAltValue(rs.getLong("alt_outside_Monthly_in_value"));
                profileVO.setUnctrlDailyOutAltCount(rs.getLong("alt_outside_daily_out_count"));
                profileVO.setUnctrlDailyOutAltValue(rs.getLong("alt_outside_daily_out_value"));
                profileVO.setUnctrlWeeklyOutAltCount(rs.getLong("alt_outside_Weekly_out_count"));
                profileVO.setUnctrlWeeklyOutAltValue(rs.getLong("alt_outside_Weekly_out_value"));
                profileVO.setUnctrlMonthlyOutAltCount(rs.getLong("alt_outside_Monthly_out_count"));
                profileVO.setUnctrlMonthlyOutAltValue(rs.getLong("alt_outside_Monthly_out_value"));

                profileVO.setDailySubscriberOutAltCount(rs.getLong("alt_daily_subs_out_count"));
                profileVO.setDailySubscriberOutAltValue(rs.getLong("alt_daily_subs_out_value"));
                profileVO.setWeeklySubscriberOutAltCount(rs.getLong("alt_weekly_subs_out_count"));
                profileVO.setWeeklySubscriberOutAltValue(rs.getLong("alt_weekly_subs_out_value"));
                profileVO.setMonthlySubscriberOutAltCount(rs.getLong("alt_monthly_subs_out_count"));
                profileVO.setMonthlySubscriberOutAltValue(rs.getLong("alt_monthly_subs_out_value"));
                profileVO.setParentProfileID(rs.getString("parent_profile_id"));
                profileVO.setIsDefault(rs.getString("is_default"));

                // 6.4 changes
                profileVO.setDailySubscriberInCount(rs.getLong("daily_subscriber_in_count"));
                profileVO.setDailySubscriberInValue(rs.getLong("daily_subscriber_in_value"));
                profileVO.setWeeklySubscriberInCount(rs.getLong("weekly_subscriber_in_count"));
                profileVO.setWeeklySubscriberInValue(rs.getLong("weekly_subscriber_in_value"));
                profileVO.setMonthlySubscriberInCount(rs.getLong("monthly_subscriber_in_count"));
                profileVO.setMonthlySubscriberInValue(rs.getLong("monthly_subscriber_in_value"));
                profileVO.setDailySubscriberInAltCount(rs.getLong("alt_daily_subs_in_count"));
                profileVO.setDailySubscriberInAltValue(rs.getLong("alt_daily_subs_in_value"));
                profileVO.setWeeklySubscriberInAltCount(rs.getLong("alt_weekly_subs_in_count"));
                profileVO.setWeeklySubscriberInAltValue(rs.getLong("alt_weekly_subs_in_value"));
                profileVO.setMonthlySubscriberInAltCount(rs.getLong("alt_monthly_subs_in_count"));
                profileVO.setMonthlySubscriberInAltValue(rs.getLong("alt_monthly_subs_in_value"));

            }
            if (pIsProductLoadRequired) {
                final ArrayList profileProductList = this.loadTransferProfileProductsList(pCon, pProfileId);
                // load product list
                final ArrayList productList = new NetworkProductDAO().loadNetworkProductList(pCon, pNetworkCode);
                final int profileProductLength = profileProductList.size();
                int productLength = productList.size();
                // if any new product add after create the transfer profile.
                // when modify the transfer profile then
                // new products entry maintain in TRANSFER_PROFILE_PRODUCTS
                // table.
                if (profileProductLength != productLength && productLength > profileProductLength) {
                    TransferProfileProductVO transferProfileProductVO = null;
                    TransferProfileProductVO profileProductVO = null;
                    for (int i = 0; i < profileProductLength; i++) {
                        transferProfileProductVO = (TransferProfileProductVO) profileProductList.get(i);
                        for (int j = 0; j < productLength; j++) {
                            profileProductVO = (TransferProfileProductVO) productList.get(j);
                            // removed the product details from product list,
                            // which is already exist in
                            // TRANSFER_PROFILE_PRODUCTS.
                            // only new products is avilable in product list.
                            if (transferProfileProductVO.getProductCode().equals(profileProductVO.getProductCode())) {
                                productList.remove(j);
                                break;
                            }
                        }
                        productLength = productList.size();
                    }
                    // only new products list .
                    for (int j = 0; j < productLength; j++) {
                        profileProductVO = (TransferProfileProductVO) productList.get(j);
                        transferProfileProductVO = new TransferProfileProductVO();
                        transferProfileProductVO.setProductCode(profileProductVO.getProductCode());
                        transferProfileProductVO.setProductName(profileProductVO.getProductName());
                        transferProfileProductVO.setMinResidualBalanceAsLong(0);
                        transferProfileProductVO.setMaxBalanceAsLong(0);
                        profileProductList.add(transferProfileProductVO);
                    }
                }
                profileVO.setProfileProductList(profileProductList);
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileDAO[loadTransferProfileThroughProfileID]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileDAO[loadTransferProfileThroughProfileID]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
         
       
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: profileVO =");
            	loggerValue.append(profileVO.toString());
                log.debug(methodName,loggerValue);
            }
        }
        return profileVO;
    }

    /**
     * Method for loading Transfer Profile for a particular category and network
     * 
     * Used in(ChannelUserAction)
     * 
     * @param pCon
     *            java.sql.Connection
     * @param pNetworkCode
     *            String
     * @param pCategoryCode
     *            String
     * @param pParentProfileID
     *            String
     * 
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadTransferProfileByCategoryID(Connection pCon, String pNetworkCode, String pCategoryCode, String pParentProfileID) throws BTSLBaseException {
        final String methodName = "loadTransferProfileByCategoryID";
        StringBuilder loggerValue=new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered pNetworkCode=");
        	loggerValue.append(pNetworkCode);
        	loggerValue.append(" pCategoryCode=");
        	loggerValue.append(pCategoryCode);
        	loggerValue.append(" pParentProfileID=");
        	loggerValue.append(pParentProfileID);
            log.debug(methodName,loggerValue);
        }
        
        
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT profile_id, profile_name FROM transfer_profile");
        strBuff.append(" WHERE network_code = ? ");
        strBuff.append(" AND category_code = ? ");
        strBuff.append(" AND parent_profile_id = ? ");
        strBuff.append(" AND status<>? ");// change against ID=
        // TRFPROFILESTATUS(This change is
        // done to make status check on
        // transfer profile, so that only non
        // deleted transfer profile can be
        // associated with user
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        final ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, pNetworkCode);
            pstmt.setString(2, pCategoryCode);
            pstmt.setString(3, pParentProfileID);
            pstmt.setString(4, PretupsI.STATUS_DELETE);// change against ID=
            // TRFPROFILESTATUS(This
            // change is done to make
            // status check on
            // transfer profile, so
            // that only non deleted
            // transfer profile can
            // be associated with
            // user
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("profile_name"), rs.getString("profile_id")));
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
			String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadTransferProfileByCategoryID]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
			String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadTransferProfileByCategoryID]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: userProductsList size=");
            	loggerValue.append(list.size());
                log.debug(methodName,loggerValue);
            }
        }
        return list;
    }

    public HashMap<String, TransferProfileProductVO> loadTransferProfileProducts() throws BTSLBaseException {
        final String methodName = "loadTransferProfileProducts";
    	StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: ");
        }
         
         
        TransferProfileProductVO transferProfileProductVO = null;
        final StringBuilder strBuff = new StringBuilder();
        String key = null;
        final HashMap<String, TransferProfileProductVO> transferProfileProductMap = new HashMap<String, TransferProfileProductVO>();

        strBuff.append("SELECT tpp.profile_id,tpp.product_code,GREATEST(tpp.min_residual_balance,catpp.min_residual_balance) min_residual_balance, ");
        strBuff.append(" GREATEST(tpp.c2s_min_txn_amt,catpp.c2s_min_txn_amt) c2s_min_txn_amt, ");
        strBuff.append(" LEAST(tpp.max_balance,catpp.max_balance) max_balance, ");
        strBuff
            .append(" LEAST(tpp.c2s_max_txn_amt,catpp.c2s_max_txn_amt) c2s_max_txn_amt,tpp.alerting_balance, LEAST(tpp.max_pct_transfer_allowed,catpp.max_pct_transfer_allowed) max_pct_transfer_allowed ");
        strBuff.append(" FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        strBuff.append(" WHERE  tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
        strBuff.append(" AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND tp.network_code = catp.network_code "); 
        strBuff.append(" AND catp.parent_profile_id=? AND catp.status=? 	 ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        try(Connection con = OracleUtil.getSingleConnection();
        		PreparedStatement pstmt = con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, PretupsI.PARENT_PROFILE_ID_CATEGORY);
            pstmt.setString(2, PretupsI.YES);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                transferProfileProductVO = new TransferProfileProductVO();
                transferProfileProductVO.setProductCode(rs.getString("product_code"));
                transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
                transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
                transferProfileProductVO.setAltBalanceLong(rs.getLong("alerting_balance"));
                transferProfileProductVO.setAllowedMaxPercentageInt(rs.getInt("max_pct_transfer_allowed"));
                transferProfileProductVO.setC2sMinTxnAmtAsLong(rs.getLong("c2s_min_txn_amt"));
                transferProfileProductVO.setC2sMaxTxnAmtAsLong(rs.getLong("c2s_max_txn_amt"));
                transferProfileProductVO.setC2sMinTxnAmt(PretupsBL.getDisplayAmount(transferProfileProductVO.getC2sMinTxnAmtAsLong()));
                transferProfileProductVO.setC2sMaxTxnAmt(PretupsBL.getDisplayAmount(transferProfileProductVO.getC2sMaxTxnAmtAsLong()));
                key = rs.getString("profile_id") + "_" + rs.getString("product_code");
                transferProfileProductMap.put(key, transferProfileProductVO);

            }
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadTransferProfileProducts]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadTransferProfileProducts]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: ProfileVO =");
            	loggerValue.append(transferProfileProductVO);
                log.debug(methodName,loggerValue);
            }
        }
        return transferProfileProductMap;
    }

    /**
     * this method use for load the list of products those are associated with
     * transfer profile
     * Method: loadTransferProfileProductsList
     * 
     * @param pCon
     * @param pProfileId
     * @return arrayList java.util.Array
     * @throws BTSLBaseException
     */
    public ArrayList loadTransferProfileProductsList(Connection pCon, String pProfileId) throws BTSLBaseException {
        final String methodName = "loadTransferProfileProductsList";
    	StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pProfileId=");
        	loggerValue.append(pProfileId);
            log.debug(methodName,loggerValue);
        }

         
         
        final ArrayList arrayList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT TPP.product_code,PROD.product_short_code,TPP.min_residual_balance, TPP.max_balance, ");
        strBuff.append(" TPP.c2s_min_txn_amt, TPP.c2s_max_txn_amt,PROD.product_name,TPP.alerting_balance, ");
        strBuff.append(" TPP.max_pct_transfer_allowed ");
        strBuff.append(" FROM  ");
        strBuff.append(" transfer_profile_products TPP,products PROD ");
        strBuff.append(" WHERE ");
        strBuff.append("  TPP.profile_id=? AND TPP.product_code=PROD.product_code ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        try (PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);){
            
            pstmt.setString(1, pProfileId);
            TransferProfileProductVO transferProfileProductVO = null;
           try(ResultSet rs = pstmt.executeQuery();)
           {
            while (rs.next()) {
                transferProfileProductVO = new TransferProfileProductVO();
                transferProfileProductVO.setProductCode(rs.getString("product_code"));
                transferProfileProductVO.setProductShortCode(rs.getString("product_short_code"));
                transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
                transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
                transferProfileProductVO.setMaxBalance(PretupsBL.getDisplayAmount(transferProfileProductVO.getMaxBalanceAsLong()));
                transferProfileProductVO.setMinBalance(PretupsBL.getDisplayAmount(transferProfileProductVO.getMinResidualBalanceAsLong()));
                transferProfileProductVO.setProductName(rs.getString("product_name"));

                transferProfileProductVO.setAltBalanceLong(rs.getLong("alerting_balance"));
                transferProfileProductVO.setAltBalance(PretupsBL.getDisplayAmount(transferProfileProductVO.getAltBalanceLong()));
                transferProfileProductVO.setAllowedMaxPercentageInt(rs.getInt("max_pct_transfer_allowed"));
                transferProfileProductVO.setAllowedMaxPercentage(String.valueOf(transferProfileProductVO.getAllowedMaxPercentageInt()));

                transferProfileProductVO.setC2sMinTxnAmtAsLong(rs.getLong("c2s_min_txn_amt"));
                transferProfileProductVO.setC2sMaxTxnAmtAsLong(rs.getLong("c2s_max_txn_amt"));
                transferProfileProductVO.setC2sMinTxnAmt(PretupsBL.getDisplayAmount(transferProfileProductVO.getC2sMinTxnAmtAsLong()));
                transferProfileProductVO.setC2sMaxTxnAmt(PretupsBL.getDisplayAmount(transferProfileProductVO.getC2sMaxTxnAmtAsLong()));
                arrayList.add(transferProfileProductVO);
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadTransferProfileProductsList]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append( "Exception : ");
        	loggerValue.append(ex.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,loggerValue);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadTransferProfileProductsList]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: ArrayList Size =");
            	loggerValue.append(arrayList.size());
                log.debug(methodName,loggerValue);
            }
        }
        return arrayList;
    }

    /**
     * Load the user transfer profile
     * 
     * @param pCon
     * @param pTransfeProfileID
     * @param pNetworkCode
     *            TODO
     * @param pIsProductLoadRequired
     * @return TransferProfileVO
     * @throws BTSLBaseException
     */
    public TransferProfileVO loadTransferProfile(Connection pCon, String pTransfeProfileID, String pNetworkCode, boolean pIsProductLoadRequired) throws BTSLBaseException {
        final String methodName = "loadTransferProfile";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   pTransfeProfileID ");
        	loggerValue.append(pTransfeProfileID);
            log.debug(methodName,loggerValue);
        }
         
         
        final StringBuilder strBuff = new StringBuilder();
        strBuff
            .append(" SELECT tp.profile_id,tp.short_name,tp.profile_name,tp.status,tp.description,LEAST(tp.daily_transfer_in_count,catp.daily_transfer_in_count) daily_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.daily_transfer_in_value,catp.daily_transfer_in_value) daily_transfer_in_value ,LEAST(tp.weekly_transfer_in_count,catp.weekly_transfer_in_count) weekly_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.weekly_transfer_in_value,catp.weekly_transfer_in_value) weekly_transfer_in_value,LEAST(tp.monthly_transfer_in_count,catp.monthly_transfer_in_count) monthly_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.monthly_transfer_in_value,catp.monthly_transfer_in_value) monthly_transfer_in_value,LEAST(tp.daily_transfer_out_count,catp.daily_transfer_out_count) daily_transfer_out_count, ");
        strBuff
            .append(" LEAST(tp.daily_transfer_out_value,catp.daily_transfer_out_value) daily_transfer_out_value,LEAST(tp.weekly_transfer_out_count,catp.weekly_transfer_out_count) weekly_transfer_out_count, ");
        strBuff
            .append(" LEAST(tp.weekly_transfer_out_value,catp.weekly_transfer_out_value) weekly_transfer_out_value,LEAST(tp.monthly_transfer_out_count,catp.monthly_transfer_out_count) monthly_transfer_out_count, ");
        strBuff
            .append(" LEAST(tp.monthly_transfer_out_value,catp.monthly_transfer_out_value) monthly_transfer_out_value,LEAST(tp.outside_daily_in_count,catp.outside_daily_in_count) outside_daily_in_count, ");
        strBuff
            .append(" LEAST(tp.outside_daily_in_value,catp.outside_daily_in_value) outside_daily_in_value,LEAST(tp.outside_weekly_in_count,catp.outside_weekly_in_count) outside_weekly_in_count, ");
        strBuff
            .append(" LEAST(tp.outside_weekly_in_value,catp.outside_weekly_in_value) outside_weekly_in_value,LEAST(tp.outside_monthly_in_count,catp.outside_monthly_in_count) outside_monthly_in_count, ");
        strBuff
            .append(" LEAST(tp.outside_monthly_in_value,catp.outside_monthly_in_value) outside_monthly_in_value,LEAST(tp.outside_daily_out_count,catp.outside_daily_out_count) outside_daily_out_count, ");
        strBuff
            .append(" LEAST(tp.outside_daily_out_value,catp.outside_daily_out_value) outside_daily_out_value,LEAST(tp.outside_weekly_out_count,catp.outside_weekly_out_count) outside_weekly_out_count, ");
        strBuff
            .append(" LEAST(tp.outside_weekly_out_value,catp.outside_weekly_out_value) outside_weekly_out_value,LEAST(tp.outside_monthly_out_count,catp.outside_monthly_out_count) outside_monthly_out_count, ");
        strBuff.append(" LEAST(tp.outside_monthly_out_value,catp.outside_monthly_out_value) outside_monthly_out_value, ");
        strBuff.append(" tp.created_by,tp.created_on, ");
        strBuff
            .append(" LEAST(tp.daily_subscriber_out_count,catp.daily_subscriber_out_count) daily_subscriber_out_count,LEAST(tp.weekly_subscriber_out_count,catp.weekly_subscriber_out_count) weekly_subscriber_out_count, ");
        strBuff.append(" LEAST(tp.monthly_subscriber_out_count,catp.monthly_subscriber_out_count) monthly_subscriber_out_count, ");
        strBuff
            .append(" LEAST(tp.daily_subscriber_out_value,catp.daily_subscriber_out_value) daily_subscriber_out_value,LEAST(tp.weekly_subscriber_out_value,catp.weekly_subscriber_out_value) weekly_subscriber_out_value, ");
        strBuff.append(" LEAST(tp.monthly_subscriber_out_value,catp.monthly_subscriber_out_value) monthly_subscriber_out_value, ");
        strBuff
            .append(" tp.modified_by,tp.modified_on,tp.network_code,tp.category_code,LEAST(tp.alt_daily_transfer_in_count,catp.alt_daily_transfer_in_count) alt_daily_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.alt_daily_transfer_in_value,catp.alt_daily_transfer_in_value) alt_daily_transfer_in_value,LEAST(tp.alt_weekly_transfer_in_count,catp.alt_weekly_transfer_in_count) alt_weekly_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.alt_weekly_transfer_in_value,catp.alt_weekly_transfer_in_value) alt_weekly_transfer_in_value,LEAST(tp.alt_monthly_transfer_in_count,catp.alt_monthly_transfer_in_count) alt_monthly_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.alt_monthly_transfer_in_value,catp.alt_monthly_transfer_in_value) alt_monthly_transfer_in_value,LEAST(tp.alt_daily_transfer_out_count,catp.alt_daily_transfer_out_count) alt_daily_transfer_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_daily_transfer_out_value,catp.alt_daily_transfer_out_value) alt_daily_transfer_out_value,LEAST(tp.alt_weekly_transfer_out_count,catp.alt_weekly_transfer_out_count) alt_weekly_transfer_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_weekly_transfer_out_value,catp.alt_weekly_transfer_out_value) alt_weekly_transfer_out_value,LEAST(tp.alt_monthly_transfer_out_count,catp.alt_monthly_transfer_out_count) alt_monthly_transfer_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_monthly_transfer_out_value,catp.alt_monthly_transfer_out_value) alt_monthly_transfer_out_value,LEAST(tp.alt_outside_daily_in_count,catp.alt_outside_daily_in_count) alt_outside_daily_in_count, ");
        strBuff
            .append(" LEAST(tp.alt_outside_daily_in_value,catp.alt_outside_daily_in_value) alt_outside_daily_in_value,LEAST(tp.alt_outside_weekly_in_count,catp.alt_outside_weekly_in_count) alt_outside_weekly_in_count, ");
        strBuff
            .append(" LEAST(tp.alt_outside_weekly_in_value,catp.alt_outside_weekly_in_value) alt_outside_weekly_in_value,LEAST(tp.alt_outside_monthly_in_count,catp.alt_outside_monthly_in_count) alt_outside_monthly_in_count,  ");
        strBuff
            .append(" LEAST(tp.alt_outside_monthly_in_value,catp.alt_outside_monthly_in_value) alt_outside_monthly_in_value,LEAST(tp.alt_outside_daily_out_count,catp.alt_outside_daily_out_count) alt_outside_daily_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_outside_daily_out_value,catp.alt_outside_daily_out_value) alt_outside_daily_out_value,LEAST(tp.alt_outside_weekly_out_count,catp.alt_outside_weekly_out_count) alt_outside_weekly_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_outside_weekly_out_value,catp.alt_outside_weekly_out_value) alt_outside_weekly_out_value,LEAST(tp.alt_outside_monthly_out_count,catp.alt_outside_monthly_out_count) alt_outside_monthly_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_outside_monthly_out_value,catp.alt_outside_monthly_out_value) alt_outside_monthly_out_value,LEAST(tp.alt_daily_subs_out_count,catp.alt_daily_subs_out_count) alt_daily_subs_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_daily_subs_out_value,catp.alt_daily_subs_out_value) alt_daily_subs_out_value,LEAST(tp.alt_weekly_subs_out_count,catp.alt_weekly_subs_out_count) alt_weekly_subs_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_weekly_subs_out_value,catp.alt_weekly_subs_out_value) alt_weekly_subs_out_value,LEAST(tp.alt_monthly_subs_out_count,catp.alt_monthly_subs_out_count) alt_monthly_subs_out_count, ");
        strBuff.append(" LEAST(tp.alt_monthly_subs_out_value,catp.alt_monthly_subs_out_value) alt_monthly_subs_out_value,tp.parent_profile_id, ");
        strBuff.append(" LEAST(tp.daily_subscriber_in_count,catp.daily_subscriber_in_count) daily_subscriber_in_count ,");
		strBuff.append(" LEAST(tp.daily_subscriber_in_value,catp.daily_subscriber_in_value) daily_subscriber_in_value ,");
		strBuff.append(" LEAST(tp.weekly_subscriber_in_count,catp.weekly_subscriber_in_count) weekly_subscriber_in_count ,");
		strBuff.append(" LEAST(tp.weekly_subscriber_in_value,catp.weekly_subscriber_in_value) weekly_subscriber_in_value ,");		
		strBuff.append(" LEAST(tp.monthly_subscriber_in_count,catp.monthly_subscriber_in_count) monthly_subscriber_in_count ,");
		strBuff.append(" LEAST(tp.monthly_subscriber_in_value,catp.monthly_subscriber_in_value) monthly_subscriber_in_value ,");		
		strBuff.append(" LEAST(tp.alt_daily_subs_in_count,catp.alt_daily_subs_in_count) alt_daily_subs_in_count ,");
		strBuff.append(" LEAST(tp.alt_daily_subs_in_value,catp.alt_daily_subs_in_value) alt_daily_subs_in_value ,");		
		strBuff.append(" LEAST(tp.alt_weekly_subs_in_count,catp.alt_weekly_subs_in_count) alt_weekly_subs_in_count ,");
		strBuff.append(" LEAST(tp.alt_weekly_subs_in_value,catp.alt_weekly_subs_in_value) alt_weekly_subs_in_value ,");
		strBuff.append(" LEAST(tp.alt_monthly_subs_in_count,catp.alt_monthly_subs_in_count) alt_monthly_subs_in_count ,");
		strBuff.append(" LEAST(tp.alt_monthly_subs_in_value,catp.alt_monthly_subs_in_value) alt_monthly_subs_in_value ");
        strBuff.append(" FROM transfer_profile tp,transfer_profile catp ");
        strBuff.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
        strBuff.append(" AND tp.category_code=catp.category_code ");
        strBuff.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        TransferProfileVO countsVO = null;
        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, pTransfeProfileID);
            pstmt.setString(2, PretupsI.YES);
            pstmt.setString(3, pNetworkCode);
            pstmt.setString(4, PretupsI.PARENT_PROFILE_ID_CATEGORY);
            pstmt.setString(5, PretupsI.YES);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                countsVO = new UserTransferCountsVO();
                countsVO.setProfileId(rs.getString("profile_id"));
                countsVO.setShortName(rs.getString("short_name"));
                countsVO.setProfileName(rs.getString("profile_name"));
                countsVO.setStatus(rs.getString("status"));
                countsVO.setDescription(rs.getString("description"));
                countsVO.setDailyInCount(rs.getLong("daily_transfer_in_count"));
                countsVO.setDailyInValue(rs.getLong("daily_transfer_in_value"));
                countsVO.setWeeklyInCount(rs.getLong("weekly_transfer_in_count"));
                countsVO.setWeeklyInValue(rs.getLong("weekly_transfer_in_value"));
                countsVO.setMonthlyInCount(rs.getLong("monthly_transfer_in_count"));
                countsVO.setMonthlyInValue(rs.getLong("monthly_transfer_in_value"));
                countsVO.setDailyOutCount(rs.getLong("daily_transfer_out_count"));
                countsVO.setDailyOutValue(rs.getLong("daily_transfer_out_value"));
                countsVO.setWeeklyOutCount(rs.getLong("weekly_transfer_out_count"));
                countsVO.setWeeklyOutValue(rs.getLong("weekly_transfer_out_value"));
                countsVO.setMonthlyOutCount(rs.getLong("monthly_transfer_out_count"));
                countsVO.setMonthlyOutValue(rs.getLong("monthly_transfer_out_value"));
                countsVO.setUnctrlDailyInCount(rs.getLong("outside_daily_in_count"));
                countsVO.setUnctrlDailyInValue(rs.getLong("outside_daily_in_value"));
                countsVO.setUnctrlWeeklyInCount(rs.getLong("outside_weekly_in_count"));
                countsVO.setUnctrlWeeklyInValue(rs.getLong("outside_weekly_in_value"));
                countsVO.setUnctrlMonthlyInCount(rs.getLong("outside_monthly_in_count"));
                countsVO.setUnctrlMonthlyInValue(rs.getLong("outside_monthly_in_value"));
                countsVO.setUnctrlDailyOutCount(rs.getLong("outside_daily_out_count"));
                countsVO.setUnctrlDailyOutValue(rs.getLong("outside_daily_out_value"));
                countsVO.setUnctrlWeeklyOutCount(rs.getLong("outside_weekly_out_count"));
                countsVO.setUnctrlWeeklyOutValue(rs.getLong("outside_weekly_out_value"));
                countsVO.setUnctrlMonthlyOutCount(rs.getLong("outside_monthly_out_count"));
                countsVO.setUnctrlMonthlyOutValue(rs.getLong("outside_monthly_out_value"));
                countsVO.setCreatedBy(rs.getString("created_by"));

                countsVO.setDailyC2STransferOutCount(rs.getLong("daily_subscriber_out_count"));
                countsVO.setDailyC2STransferOutValue(rs.getLong("daily_subscriber_out_value"));
                countsVO.setWeeklyC2STransferOutCount(rs.getLong("weekly_subscriber_out_count"));
                countsVO.setWeeklyC2STransferOutValue(rs.getLong("weekly_subscriber_out_value"));
                countsVO.setMonthlyC2STransferOutCount(rs.getLong("monthly_subscriber_out_count"));
                countsVO.setMonthlyC2STransferOutValue(rs.getLong("monthly_subscriber_out_value"));

                countsVO.setCreatedOn(rs.getDate("created_on"));
                countsVO.setModifiedBy(rs.getString("modified_by"));
                countsVO.setModifiedOn(rs.getDate("modified_on"));
                countsVO.setNetworkCode(rs.getString("network_code"));
                countsVO.setCategory(rs.getString("category_code"));

                // alerting count/value

                countsVO.setDailyInAltCount(rs.getLong("alt_daily_transfer_in_count"));
                countsVO.setDailyInAltValue(rs.getLong("alt_daily_transfer_in_value"));
                countsVO.setWeeklyInAltCount(rs.getLong("alt_weekly_transfer_in_count"));
                countsVO.setWeeklyInAltValue(rs.getLong("alt_weekly_transfer_in_value"));
                countsVO.setMonthlyInAltCount(rs.getLong("alt_monthly_transfer_in_count"));
                countsVO.setMonthlyInAltValue(rs.getLong("alt_monthly_transfer_in_value"));
                countsVO.setDailyOutAltCount(rs.getLong("alt_daily_transfer_out_count"));
                countsVO.setDailyOutAltValue(rs.getLong("alt_daily_transfer_out_value"));
                countsVO.setWeeklyOutAltCount(rs.getLong("alt_weekly_transfer_out_count"));
                countsVO.setWeeklyOutAltValue(rs.getLong("alt_weekly_transfer_out_value"));
                countsVO.setMonthlyOutAltCount(rs.getLong("alt_monthly_transfer_out_count"));
                countsVO.setMonthlyOutAltValue(rs.getLong("alt_monthly_transfer_out_value"));

                countsVO.setUnctrlDailyInAltCount(rs.getLong("alt_outside_daily_in_count"));
                countsVO.setUnctrlDailyInAltValue(rs.getLong("alt_outside_daily_in_value"));
                countsVO.setUnctrlWeeklyInAltCount(rs.getLong("alt_outside_Weekly_in_count"));
                countsVO.setUnctrlWeeklyInAltValue(rs.getLong("alt_outside_Weekly_in_value"));
                countsVO.setUnctrlMonthlyInAltCount(rs.getLong("alt_outside_Monthly_in_count"));
                countsVO.setUnctrlMonthlyInAltValue(rs.getLong("alt_outside_Monthly_in_value"));
                countsVO.setUnctrlDailyOutAltCount(rs.getLong("alt_outside_daily_out_count"));
                countsVO.setUnctrlDailyOutAltValue(rs.getLong("alt_outside_daily_out_value"));
                countsVO.setUnctrlWeeklyOutAltCount(rs.getLong("alt_outside_Weekly_out_count"));
                countsVO.setUnctrlWeeklyOutAltValue(rs.getLong("alt_outside_Weekly_out_value"));
                countsVO.setUnctrlMonthlyOutAltCount(rs.getLong("alt_outside_Monthly_out_count"));
                countsVO.setUnctrlMonthlyOutAltValue(rs.getLong("alt_outside_Monthly_out_value"));

                countsVO.setDailySubscriberOutAltCount(rs.getLong("alt_daily_subs_out_count"));
                countsVO.setDailySubscriberOutAltValue(rs.getLong("alt_daily_subs_out_value"));
                countsVO.setWeeklySubscriberOutAltCount(rs.getLong("alt_weekly_subs_out_count"));
                countsVO.setWeeklySubscriberOutAltValue(rs.getLong("alt_weekly_subs_out_value"));
                countsVO.setMonthlySubscriberOutAltCount(rs.getLong("alt_monthly_subs_out_count"));
                countsVO.setMonthlySubscriberOutAltValue(rs.getLong("alt_monthly_subs_out_value"));
                countsVO.setParentProfileID(rs.getString("parent_profile_id"));
                
                countsVO.setDailySubscriberInCount(rs.getLong("daily_subscriber_in_count"));
                countsVO.setDailySubscriberInValue(rs.getLong("daily_subscriber_in_value"));
                countsVO.setWeeklySubscriberInCount(rs.getLong("weekly_subscriber_in_count"));
                countsVO.setWeeklySubscriberInValue(rs.getLong("weekly_subscriber_in_value"));
                countsVO.setMonthlySubscriberInCount(rs.getLong("monthly_subscriber_in_count"));
                countsVO.setMonthlySubscriberInValue(rs.getLong("monthly_subscriber_in_value"));
				countsVO.setDailySubscriberInAltCount(rs.getLong("alt_daily_subs_in_count"));
				countsVO.setDailySubscriberInAltValue(rs.getLong("alt_daily_subs_in_value"));
				countsVO.setWeeklySubscriberInAltCount(rs.getLong("alt_weekly_subs_in_count"));
				countsVO.setWeeklySubscriberInAltValue(rs.getLong("alt_weekly_subs_in_value"));
				countsVO.setMonthlySubscriberInAltCount(rs.getLong("alt_monthly_subs_in_count"));
				countsVO.setMonthlySubscriberInAltValue(rs.getLong("alt_monthly_subs_in_value"));
            }
            if (pIsProductLoadRequired && (countsVO != null)) {
                countsVO.setProfileProductList(this.loadEffTrfProfileProductList(pCon, countsVO.getProfileId()));
            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
			String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadTransferProfile]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
			String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadTransferProfile]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  UserTransferCountsVO =");
            	loggerValue.append(countsVO);
                log.debug(methodName, loggerValue);
            }
        }
        return countsVO;
    }

    public HashMap<String, TransferProfileVO> loadTransferProfile() throws BTSLBaseException {
        final String methodName = "loadTransferProfile";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        final HashMap<String, TransferProfileVO> transferProfileMap = new HashMap<String, TransferProfileVO>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff
            .append(" SELECT tp.profile_id,tp.short_name,tp.profile_name,tp.status,tp.description,LEAST(tp.daily_transfer_in_count,catp.daily_transfer_in_count) daily_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.daily_transfer_in_value,catp.daily_transfer_in_value) daily_transfer_in_value ,LEAST(tp.weekly_transfer_in_count,catp.weekly_transfer_in_count) weekly_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.weekly_transfer_in_value,catp.weekly_transfer_in_value) weekly_transfer_in_value,LEAST(tp.monthly_transfer_in_count,catp.monthly_transfer_in_count) monthly_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.monthly_transfer_in_value,catp.monthly_transfer_in_value) monthly_transfer_in_value,LEAST(tp.daily_transfer_out_count,catp.daily_transfer_out_count) daily_transfer_out_count, ");
        strBuff
            .append(" LEAST(tp.daily_transfer_out_value,catp.daily_transfer_out_value) daily_transfer_out_value,LEAST(tp.weekly_transfer_out_count,catp.weekly_transfer_out_count) weekly_transfer_out_count, ");
        strBuff
            .append(" LEAST(tp.weekly_transfer_out_value,catp.weekly_transfer_out_value) weekly_transfer_out_value,LEAST(tp.monthly_transfer_out_count,catp.monthly_transfer_out_count) monthly_transfer_out_count, ");
        strBuff
            .append(" LEAST(tp.monthly_transfer_out_value,catp.monthly_transfer_out_value) monthly_transfer_out_value,LEAST(tp.outside_daily_in_count,catp.outside_daily_in_count) outside_daily_in_count, ");
        strBuff
            .append(" LEAST(tp.outside_daily_in_value,catp.outside_daily_in_value) outside_daily_in_value,LEAST(tp.outside_weekly_in_count,catp.outside_weekly_in_count) outside_weekly_in_count, ");
        strBuff
            .append(" LEAST(tp.outside_weekly_in_value,catp.outside_weekly_in_value) outside_weekly_in_value,LEAST(tp.outside_monthly_in_count,catp.outside_monthly_in_count) outside_monthly_in_count, ");
        strBuff
            .append(" LEAST(tp.outside_monthly_in_value,catp.outside_monthly_in_value) outside_monthly_in_value,LEAST(tp.outside_daily_out_count,catp.outside_daily_out_count) outside_daily_out_count, ");
        strBuff
            .append(" LEAST(tp.outside_daily_out_value,catp.outside_daily_out_value) outside_daily_out_value,LEAST(tp.outside_weekly_out_count,catp.outside_weekly_out_count) outside_weekly_out_count, ");
        strBuff
            .append(" LEAST(tp.outside_weekly_out_value,catp.outside_weekly_out_value) outside_weekly_out_value,LEAST(tp.outside_monthly_out_count,catp.outside_monthly_out_count) outside_monthly_out_count, ");
        strBuff.append(" LEAST(tp.outside_monthly_out_value,catp.outside_monthly_out_value) outside_monthly_out_value, ");
        strBuff.append(" tp.created_by,tp.created_on, ");
        strBuff
            .append(" LEAST(tp.daily_subscriber_out_count,catp.daily_subscriber_out_count) daily_subscriber_out_count,LEAST(tp.weekly_subscriber_out_count,catp.weekly_subscriber_out_count) weekly_subscriber_out_count, ");
        strBuff.append(" LEAST(tp.monthly_subscriber_out_count,catp.monthly_subscriber_out_count) monthly_subscriber_out_count, ");
        strBuff
            .append(" LEAST(tp.daily_subscriber_out_value,catp.daily_subscriber_out_value) daily_subscriber_out_value,LEAST(tp.weekly_subscriber_out_value,catp.weekly_subscriber_out_value) weekly_subscriber_out_value, ");
        strBuff.append(" LEAST(tp.monthly_subscriber_out_value,catp.monthly_subscriber_out_value) monthly_subscriber_out_value, ");

        strBuff
            .append(" LEAST(tp.daily_subscriber_in_count,catp.daily_subscriber_in_count) daily_subscriber_in_count,LEAST(tp.weekly_subscriber_in_count,catp.weekly_subscriber_in_count) weekly_subscriber_in_count, ");
        strBuff.append(" LEAST(tp.monthly_subscriber_in_count,catp.monthly_subscriber_in_count) monthly_subscriber_in_count, ");
        strBuff
            .append(" LEAST(tp.daily_subscriber_in_value,catp.daily_subscriber_in_value) daily_subscriber_in_value,LEAST(tp.weekly_subscriber_in_value,catp.weekly_subscriber_in_value) weekly_subscriber_in_value, ");
        strBuff.append(" LEAST(tp.monthly_subscriber_in_value,catp.monthly_subscriber_in_value) monthly_subscriber_in_value, ");

        strBuff
            .append(" tp.modified_by,tp.modified_on,tp.network_code,tp.category_code,LEAST(tp.alt_daily_transfer_in_count,catp.alt_daily_transfer_in_count) alt_daily_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.alt_daily_transfer_in_value,catp.alt_daily_transfer_in_value) alt_daily_transfer_in_value,LEAST(tp.alt_weekly_transfer_in_count,catp.alt_weekly_transfer_in_count) alt_weekly_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.alt_weekly_transfer_in_value,catp.alt_weekly_transfer_in_value) alt_weekly_transfer_in_value,LEAST(tp.alt_monthly_transfer_in_count,catp.alt_monthly_transfer_in_count) alt_monthly_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.alt_monthly_transfer_in_value,catp.alt_monthly_transfer_in_value) alt_monthly_transfer_in_value,LEAST(tp.alt_daily_transfer_out_count,catp.alt_daily_transfer_out_count) alt_daily_transfer_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_daily_transfer_out_value,catp.alt_daily_transfer_out_value) alt_daily_transfer_out_value,LEAST(tp.alt_weekly_transfer_out_count,catp.alt_weekly_transfer_out_count) alt_weekly_transfer_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_weekly_transfer_out_value,catp.alt_weekly_transfer_out_value) alt_weekly_transfer_out_value,LEAST(tp.alt_monthly_transfer_out_count,catp.alt_monthly_transfer_out_count) alt_monthly_transfer_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_monthly_transfer_out_value,catp.alt_monthly_transfer_out_value) alt_monthly_transfer_out_value,LEAST(tp.alt_outside_daily_in_count,catp.alt_outside_daily_in_count) alt_outside_daily_in_count, ");
        strBuff
            .append(" LEAST(tp.alt_outside_daily_in_value,catp.alt_outside_daily_in_value) alt_outside_daily_in_value,LEAST(tp.alt_outside_weekly_in_count,catp.alt_outside_weekly_in_count) alt_outside_weekly_in_count, ");
        strBuff
            .append(" LEAST(tp.alt_outside_weekly_in_value,catp.alt_outside_weekly_in_value) alt_outside_weekly_in_value,LEAST(tp.alt_outside_monthly_in_count,catp.alt_outside_monthly_in_count) alt_outside_monthly_in_count,  ");
        strBuff
            .append(" LEAST(tp.alt_outside_monthly_in_value,catp.alt_outside_monthly_in_value) alt_outside_monthly_in_value,LEAST(tp.alt_outside_daily_out_count,catp.alt_outside_daily_out_count) alt_outside_daily_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_outside_daily_out_value,catp.alt_outside_daily_out_value) alt_outside_daily_out_value,LEAST(tp.alt_outside_weekly_out_count,catp.alt_outside_weekly_out_count) alt_outside_weekly_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_outside_weekly_out_value,catp.alt_outside_weekly_out_value) alt_outside_weekly_out_value,LEAST(tp.alt_outside_monthly_out_count,catp.alt_outside_monthly_out_count) alt_outside_monthly_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_outside_monthly_out_value,catp.alt_outside_monthly_out_value) alt_outside_monthly_out_value,LEAST(tp.alt_daily_subs_out_count,catp.alt_daily_subs_out_count) alt_daily_subs_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_daily_subs_out_value,catp.alt_daily_subs_out_value) alt_daily_subs_out_value,LEAST(tp.alt_weekly_subs_out_count,catp.alt_weekly_subs_out_count) alt_weekly_subs_out_count, ");
        strBuff
            .append(" LEAST(tp.alt_weekly_subs_out_value,catp.alt_weekly_subs_out_value) alt_weekly_subs_out_value,LEAST(tp.alt_monthly_subs_out_count,catp.alt_monthly_subs_out_count) alt_monthly_subs_out_count, ");
        strBuff.append(" LEAST(tp.alt_monthly_subs_out_value,catp.alt_monthly_subs_out_value) alt_monthly_subs_out_value,tp.parent_profile_id ");
        strBuff.append(" FROM transfer_profile tp,transfer_profile catp ");
        strBuff.append(" WHERE tp.status = ? ");
        strBuff.append(" AND tp.category_code=catp.category_code ");
        strBuff.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        TransferProfileVO countsVO = null;
        try {
            con = OracleUtil.getSingleConnection();
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.YES);
            pstmt.setString(2, PretupsI.PARENT_PROFILE_ID_CATEGORY);
            pstmt.setString(3, PretupsI.YES);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                countsVO = new UserTransferCountsVO();
                countsVO.setProfileId(rs.getString("profile_id"));
                countsVO.setShortName(rs.getString("short_name"));
                countsVO.setProfileName(rs.getString("profile_name"));
                countsVO.setStatus(rs.getString("status"));
                countsVO.setDescription(rs.getString("description"));
                countsVO.setDailyInCount(rs.getLong("daily_transfer_in_count"));
                countsVO.setDailyInValue(rs.getLong("daily_transfer_in_value"));
                countsVO.setWeeklyInCount(rs.getLong("weekly_transfer_in_count"));
                countsVO.setWeeklyInValue(rs.getLong("weekly_transfer_in_value"));
                countsVO.setMonthlyInCount(rs.getLong("monthly_transfer_in_count"));
                countsVO.setMonthlyInValue(rs.getLong("monthly_transfer_in_value"));
                countsVO.setDailyOutCount(rs.getLong("daily_transfer_out_count"));
                countsVO.setDailyOutValue(rs.getLong("daily_transfer_out_value"));
                countsVO.setWeeklyOutCount(rs.getLong("weekly_transfer_out_count"));
                countsVO.setWeeklyOutValue(rs.getLong("weekly_transfer_out_value"));
                countsVO.setMonthlyOutCount(rs.getLong("monthly_transfer_out_count"));
                countsVO.setMonthlyOutValue(rs.getLong("monthly_transfer_out_value"));
                countsVO.setUnctrlDailyInCount(rs.getLong("outside_daily_in_count"));
                countsVO.setUnctrlDailyInValue(rs.getLong("outside_daily_in_value"));
                countsVO.setUnctrlWeeklyInCount(rs.getLong("outside_weekly_in_count"));
                countsVO.setUnctrlWeeklyInValue(rs.getLong("outside_weekly_in_value"));
                countsVO.setUnctrlMonthlyInCount(rs.getLong("outside_monthly_in_count"));
                countsVO.setUnctrlMonthlyInValue(rs.getLong("outside_monthly_in_value"));
                countsVO.setUnctrlDailyOutCount(rs.getLong("outside_daily_out_count"));
                countsVO.setUnctrlDailyOutValue(rs.getLong("outside_daily_out_value"));
                countsVO.setUnctrlWeeklyOutCount(rs.getLong("outside_weekly_out_count"));
                countsVO.setUnctrlWeeklyOutValue(rs.getLong("outside_weekly_out_value"));
                countsVO.setUnctrlMonthlyOutCount(rs.getLong("outside_monthly_out_count"));
                countsVO.setUnctrlMonthlyOutValue(rs.getLong("outside_monthly_out_value"));
                countsVO.setCreatedBy(rs.getString("created_by"));

                countsVO.setDailyC2STransferOutCount(rs.getLong("daily_subscriber_out_count"));
                countsVO.setDailyC2STransferOutValue(rs.getLong("daily_subscriber_out_value"));
                countsVO.setWeeklyC2STransferOutCount(rs.getLong("weekly_subscriber_out_count"));
                countsVO.setWeeklyC2STransferOutValue(rs.getLong("weekly_subscriber_out_value"));
                countsVO.setMonthlyC2STransferOutCount(rs.getLong("monthly_subscriber_out_count"));
                countsVO.setMonthlyC2STransferOutValue(rs.getLong("monthly_subscriber_out_value"));
                // added by akanksha for ethiopia telecom
                countsVO.setDailySubscriberOutCount(rs.getLong("daily_subscriber_out_count"));
                countsVO.setDailySubscriberOutValue(rs.getLong("daily_subscriber_out_value"));
                countsVO.setWeeklySubscriberOutCount(rs.getLong("weekly_subscriber_out_count"));
                countsVO.setWeeklySubscriberOutValue(rs.getLong("weekly_subscriber_out_value"));
                countsVO.setMonthlySubscriberOutCount(rs.getLong("monthly_subscriber_out_count"));
                countsVO.setMonthlySubscriberOutValue(rs.getLong("monthly_subscriber_out_value"));

                countsVO.setCreatedOn(rs.getDate("created_on"));
                countsVO.setModifiedBy(rs.getString("modified_by"));
                countsVO.setModifiedOn(rs.getDate("modified_on"));
                countsVO.setNetworkCode(rs.getString("network_code"));
                countsVO.setCategory(rs.getString("category_code"));

                // alerting count/value

                countsVO.setDailyInAltCount(rs.getLong("alt_daily_transfer_in_count"));
                countsVO.setDailyInAltValue(rs.getLong("alt_daily_transfer_in_value"));
                countsVO.setWeeklyInAltCount(rs.getLong("alt_weekly_transfer_in_count"));
                countsVO.setWeeklyInAltValue(rs.getLong("alt_weekly_transfer_in_value"));
                countsVO.setMonthlyInAltCount(rs.getLong("alt_monthly_transfer_in_count"));
                countsVO.setMonthlyInAltValue(rs.getLong("alt_monthly_transfer_in_value"));
                countsVO.setDailyOutAltCount(rs.getLong("alt_daily_transfer_out_count"));
                countsVO.setDailyOutAltValue(rs.getLong("alt_daily_transfer_out_value"));
                countsVO.setWeeklyOutAltCount(rs.getLong("alt_weekly_transfer_out_count"));
                countsVO.setWeeklyOutAltValue(rs.getLong("alt_weekly_transfer_out_value"));
                countsVO.setMonthlyOutAltCount(rs.getLong("alt_monthly_transfer_out_count"));
                countsVO.setMonthlyOutAltValue(rs.getLong("alt_monthly_transfer_out_value"));

                countsVO.setUnctrlDailyInAltCount(rs.getLong("alt_outside_daily_in_count"));
                countsVO.setUnctrlDailyInAltValue(rs.getLong("alt_outside_daily_in_value"));
                countsVO.setUnctrlWeeklyInAltCount(rs.getLong("alt_outside_Weekly_in_count"));
                countsVO.setUnctrlWeeklyInAltValue(rs.getLong("alt_outside_Weekly_in_value"));
                countsVO.setUnctrlMonthlyInAltCount(rs.getLong("alt_outside_Monthly_in_count"));
                countsVO.setUnctrlMonthlyInAltValue(rs.getLong("alt_outside_Monthly_in_value"));
                countsVO.setUnctrlDailyOutAltCount(rs.getLong("alt_outside_daily_out_count"));
                countsVO.setUnctrlDailyOutAltValue(rs.getLong("alt_outside_daily_out_value"));
                countsVO.setUnctrlWeeklyOutAltCount(rs.getLong("alt_outside_Weekly_out_count"));
                countsVO.setUnctrlWeeklyOutAltValue(rs.getLong("alt_outside_Weekly_out_value"));
                countsVO.setUnctrlMonthlyOutAltCount(rs.getLong("alt_outside_Monthly_out_count"));
                countsVO.setUnctrlMonthlyOutAltValue(rs.getLong("alt_outside_Monthly_out_value"));

                countsVO.setDailySubscriberOutAltCount(rs.getLong("alt_daily_subs_out_count"));
                countsVO.setDailySubscriberOutAltValue(rs.getLong("alt_daily_subs_out_value"));
                countsVO.setWeeklySubscriberOutAltCount(rs.getLong("alt_weekly_subs_out_count"));
                countsVO.setWeeklySubscriberOutAltValue(rs.getLong("alt_weekly_subs_out_value"));
                countsVO.setMonthlySubscriberOutAltCount(rs.getLong("alt_monthly_subs_out_count"));
                countsVO.setMonthlySubscriberOutAltValue(rs.getLong("alt_monthly_subs_out_value"));

                countsVO.setDailySubscriberInCount(rs.getLong("daily_subscriber_In_count"));
                countsVO.setDailySubscriberInValue(rs.getLong("daily_subscriber_In_value"));
                countsVO.setWeeklySubscriberInCount(rs.getLong("weekly_subscriber_In_count"));
                countsVO.setWeeklySubscriberInValue(rs.getLong("weekly_subscriber_In_value"));
                countsVO.setMonthlySubscriberInCount(rs.getLong("monthly_subscriber_In_count"));
                countsVO.setMonthlySubscriberInValue(rs.getLong("monthly_subscriber_In_value"));

                countsVO.setParentProfileID(rs.getString("parent_profile_id"));

                transferProfileMap.put(countsVO.getProfileId() + "_" + countsVO.getNetworkCode(), countsVO);
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
			String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadTransferProfile]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
			String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadTransferProfile]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        	try{
                if (con!= null){
                	con.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  UserTransferCountsVO =");
            	loggerValue.append(countsVO);
                log.debug(methodName,loggerValue);
            }
        }
        return transferProfileMap;
    }

    /**
     * Method loadTrfProfileForCategoryCode.
     * 
     * @author sandeep.goel
     * @param pCon
     *            Connection
     * @param pCategoryCode
     *            String
     * @param pNetworkCode
     *            String
     * @param pIsProductLoadRequired
     *            boolean
     * @return TransferProfileVO
     * @throws BTSLBaseException
     * 
     * 
     */

    // reversal edited
    public TransferProfileVO loadTrfProfileForCategoryCode(Connection pCon, String pCategoryCode, String pNetworkCode, boolean pIsProductLoadRequired) throws BTSLBaseException {
        final String methodName = "loadTrfProfileForCategoryCode";
    	StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered   pCategoryCode ");
        	loggerValue.append(pCategoryCode);
            log.debug(methodName,loggerValue);
        }
        
         
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT ");
        strBuff.append(" profile_id, short_name, profile_name, status, description, daily_transfer_in_count, ");
        strBuff.append(" daily_transfer_in_value, weekly_transfer_in_count, weekly_transfer_in_value, monthly_transfer_in_count, ");
        strBuff.append(" monthly_transfer_in_value, daily_transfer_out_count, daily_transfer_out_value, weekly_transfer_out_count, ");
        strBuff.append(" weekly_transfer_out_value, monthly_transfer_out_count, monthly_transfer_out_value, outside_daily_in_count, ");
        strBuff.append(" outside_daily_in_value, outside_weekly_in_count, outside_weekly_in_value, outside_monthly_in_count, ");
        strBuff.append(" outside_monthly_in_value, outside_daily_out_count, outside_daily_out_value, outside_weekly_out_count, ");
        strBuff.append(" outside_weekly_out_value, outside_monthly_out_count, outside_monthly_out_value, created_by, created_on, ");
        strBuff.append(" daily_subscriber_out_count, weekly_subscriber_out_count, monthly_subscriber_out_count, ");
        strBuff.append(" daily_subscriber_out_value, weekly_subscriber_out_value, monthly_subscriber_out_value, ");
        strBuff.append(" modified_by, modified_on, network_code, category_code,alt_daily_transfer_in_count, ");
        strBuff.append("alt_daily_transfer_in_value, alt_weekly_transfer_in_count, ");
        strBuff.append("alt_weekly_transfer_in_value, alt_monthly_transfer_in_count, ");
        strBuff.append("alt_monthly_transfer_in_value, alt_daily_transfer_out_count, ");
        strBuff.append("alt_daily_transfer_out_value, alt_weekly_transfer_out_count, ");
        strBuff.append("alt_weekly_transfer_out_value, alt_monthly_transfer_out_count, ");
        strBuff.append("alt_monthly_transfer_out_value, alt_outside_daily_in_count, ");
        strBuff.append("alt_outside_daily_in_value, alt_outside_weekly_in_count, ");
        strBuff.append("alt_outside_weekly_in_value, alt_outside_monthly_in_count, ");
        strBuff.append("alt_outside_monthly_in_value, alt_outside_daily_out_count, ");
        strBuff.append("alt_outside_daily_out_value, alt_outside_weekly_out_count, ");
        strBuff.append("alt_outside_weekly_out_value, alt_outside_monthly_out_count, ");
        strBuff.append("alt_outside_monthly_out_value, alt_daily_subs_out_count, ");
        strBuff.append("alt_daily_subs_out_value, alt_weekly_subs_out_count, ");
        strBuff.append("alt_weekly_subs_out_value, alt_monthly_subs_out_count, ");
        strBuff.append("alt_monthly_subs_out_value, parent_profile_id ,");
        // 6.4 changes
        strBuff.append(" daily_subscriber_in_count, daily_subscriber_in_value,  weekly_subscriber_in_count, ");
        strBuff.append(" weekly_subscriber_in_value, monthly_subscriber_in_count, monthly_subscriber_in_value, ");
        strBuff.append("alt_daily_subs_in_count, alt_daily_subs_in_value, ");
        strBuff.append("alt_weekly_subs_in_count, alt_weekly_subs_in_value, ");
        strBuff.append("alt_monthly_subs_in_count, alt_monthly_subs_in_value ");

        strBuff.append(" FROM  ");
        strBuff.append(" transfer_profile ");
        strBuff.append(" WHERE ");
        strBuff.append(" category_code = ? AND parent_profile_id=? AND ");
        strBuff.append(" status = ?	AND network_code = ? ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        TransferProfileVO countsVO = null;
        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, pCategoryCode);
            pstmt.setString(2, PretupsI.PARENT_PROFILE_ID_CATEGORY);
            pstmt.setString(3, PretupsI.YES);
            pstmt.setString(4, pNetworkCode);

            try(ResultSet rs = pstmt.executeQuery();){
            if (rs.next()) {
                countsVO = new UserTransferCountsVO();
                countsVO.setProfileId(rs.getString("profile_id"));
                countsVO.setShortName(rs.getString("short_name"));
                countsVO.setProfileName(rs.getString("profile_name"));
                countsVO.setStatus(rs.getString("status"));
                countsVO.setDescription(rs.getString("description"));
                countsVO.setDailyInCount(rs.getLong("daily_transfer_in_count"));
                countsVO.setDailyInValue(rs.getLong("daily_transfer_in_value"));
                countsVO.setWeeklyInCount(rs.getLong("weekly_transfer_in_count"));
                countsVO.setWeeklyInValue(rs.getLong("weekly_transfer_in_value"));
                countsVO.setMonthlyInCount(rs.getLong("monthly_transfer_in_count"));
                countsVO.setMonthlyInValue(rs.getLong("monthly_transfer_in_value"));
                countsVO.setDailyOutCount(rs.getLong("daily_transfer_out_count"));
                countsVO.setDailyOutValue(rs.getLong("daily_transfer_out_value"));
                countsVO.setWeeklyOutCount(rs.getLong("weekly_transfer_out_count"));
                countsVO.setWeeklyOutValue(rs.getLong("weekly_transfer_out_value"));
                countsVO.setMonthlyOutCount(rs.getLong("monthly_transfer_out_count"));
                countsVO.setMonthlyOutValue(rs.getLong("monthly_transfer_out_value"));
                countsVO.setUnctrlDailyInCount(rs.getLong("outside_daily_in_count"));
                countsVO.setUnctrlDailyInValue(rs.getLong("outside_daily_in_value"));
                countsVO.setUnctrlWeeklyInCount(rs.getLong("outside_weekly_in_count"));
                countsVO.setUnctrlWeeklyInValue(rs.getLong("outside_weekly_in_value"));
                countsVO.setUnctrlMonthlyInCount(rs.getLong("outside_monthly_in_count"));
                countsVO.setUnctrlMonthlyInValue(rs.getLong("outside_monthly_in_value"));
                countsVO.setUnctrlDailyOutCount(rs.getLong("outside_daily_out_count"));
                countsVO.setUnctrlDailyOutValue(rs.getLong("outside_daily_out_value"));
                countsVO.setUnctrlWeeklyOutCount(rs.getLong("outside_weekly_out_count"));
                countsVO.setUnctrlWeeklyOutValue(rs.getLong("outside_weekly_out_value"));
                countsVO.setUnctrlMonthlyOutCount(rs.getLong("outside_monthly_out_count"));
                countsVO.setUnctrlMonthlyOutValue(rs.getLong("outside_monthly_out_value"));
                countsVO.setCreatedBy(rs.getString("created_by"));

                countsVO.setDailySubscriberOutCount(rs.getLong("daily_subscriber_out_count"));
                countsVO.setDailySubscriberOutValue(rs.getLong("daily_subscriber_out_value"));
                countsVO.setWeeklySubscriberOutCount(rs.getLong("weekly_subscriber_out_count"));
                countsVO.setWeeklySubscriberOutValue(rs.getLong("weekly_subscriber_out_value"));
                countsVO.setMonthlySubscriberOutCount(rs.getLong("monthly_subscriber_out_count"));
                countsVO.setMonthlySubscriberOutValue(rs.getLong("monthly_subscriber_out_value"));

                countsVO.setCreatedOn(rs.getDate("created_on"));
                countsVO.setModifiedBy(rs.getString("modified_by"));
                countsVO.setModifiedOn(rs.getDate("modified_on"));
                countsVO.setNetworkCode(rs.getString("network_code"));
                countsVO.setCategory(rs.getString("category_code"));

                // alerting count/value

                countsVO.setDailyInAltCount(rs.getLong("alt_daily_transfer_in_count"));
                countsVO.setDailyInAltValue(rs.getLong("alt_daily_transfer_in_value"));
                countsVO.setWeeklyInAltCount(rs.getLong("alt_weekly_transfer_in_count"));
                countsVO.setWeeklyInAltValue(rs.getLong("alt_weekly_transfer_in_value"));
                countsVO.setMonthlyInAltCount(rs.getLong("alt_monthly_transfer_in_count"));
                countsVO.setMonthlyInAltValue(rs.getLong("alt_monthly_transfer_in_value"));
                countsVO.setDailyOutAltCount(rs.getLong("alt_daily_transfer_out_count"));
                countsVO.setDailyOutAltValue(rs.getLong("alt_daily_transfer_out_value"));
                countsVO.setWeeklyOutAltCount(rs.getLong("alt_weekly_transfer_out_count"));
                countsVO.setWeeklyOutAltValue(rs.getLong("alt_weekly_transfer_out_value"));
                countsVO.setMonthlyOutAltCount(rs.getLong("alt_monthly_transfer_out_count"));
                countsVO.setMonthlyOutAltValue(rs.getLong("alt_monthly_transfer_out_value"));

                countsVO.setUnctrlDailyInAltCount(rs.getLong("alt_outside_daily_in_count"));
                countsVO.setUnctrlDailyInAltValue(rs.getLong("alt_outside_daily_in_value"));
                countsVO.setUnctrlWeeklyInAltCount(rs.getLong("alt_outside_Weekly_in_count"));
                countsVO.setUnctrlWeeklyInAltValue(rs.getLong("alt_outside_Weekly_in_value"));
                countsVO.setUnctrlMonthlyInAltCount(rs.getLong("alt_outside_Monthly_in_count"));
                countsVO.setUnctrlMonthlyInAltValue(rs.getLong("alt_outside_Monthly_in_value"));
                countsVO.setUnctrlDailyOutAltCount(rs.getLong("alt_outside_daily_out_count"));
                countsVO.setUnctrlDailyOutAltValue(rs.getLong("alt_outside_daily_out_value"));
                countsVO.setUnctrlWeeklyOutAltCount(rs.getLong("alt_outside_Weekly_out_count"));
                countsVO.setUnctrlWeeklyOutAltValue(rs.getLong("alt_outside_Weekly_out_value"));
                countsVO.setUnctrlMonthlyOutAltCount(rs.getLong("alt_outside_Monthly_out_count"));
                countsVO.setUnctrlMonthlyOutAltValue(rs.getLong("alt_outside_Monthly_out_value"));

                countsVO.setDailySubscriberOutAltCount(rs.getLong("alt_daily_subs_out_count"));
                countsVO.setDailySubscriberOutAltValue(rs.getLong("alt_daily_subs_out_value"));
                countsVO.setWeeklySubscriberOutAltCount(rs.getLong("alt_weekly_subs_out_count"));
                countsVO.setWeeklySubscriberOutAltValue(rs.getLong("alt_weekly_subs_out_value"));
                countsVO.setMonthlySubscriberOutAltCount(rs.getLong("alt_monthly_subs_out_count"));
                countsVO.setMonthlySubscriberOutAltValue(rs.getLong("alt_monthly_subs_out_value"));

                // 6.4 changes
                countsVO.setDailySubscriberInCount(rs.getLong("daily_subscriber_in_count"));
                countsVO.setDailySubscriberInValue(rs.getLong("daily_subscriber_in_value"));
                countsVO.setWeeklySubscriberInCount(rs.getLong("weekly_subscriber_in_count"));
                countsVO.setWeeklySubscriberInValue(rs.getLong("weekly_subscriber_in_value"));
                countsVO.setMonthlySubscriberInCount(rs.getLong("monthly_subscriber_in_count"));
                countsVO.setMonthlySubscriberInValue(rs.getLong("monthly_subscriber_in_value"));
                countsVO.setDailySubscriberInAltCount(rs.getLong("alt_daily_subs_in_count"));
                countsVO.setDailySubscriberInAltValue(rs.getLong("alt_daily_subs_in_value"));
                countsVO.setWeeklySubscriberInAltCount(rs.getLong("alt_weekly_subs_in_count"));
                countsVO.setWeeklySubscriberInAltValue(rs.getLong("alt_weekly_subs_in_value"));
                countsVO.setMonthlySubscriberInAltCount(rs.getLong("alt_monthly_subs_in_count"));
                countsVO.setMonthlySubscriberInAltValue(rs.getLong("alt_monthly_subs_in_value"));

                countsVO.setParentProfileID(rs.getString("parent_profile_id"));
                if (pIsProductLoadRequired) {
                    countsVO.setProfileProductList(this.loadTransferProfileProductsList(pCon, countsVO.getProfileId()));
                }
            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadTrfProfileForCategoryCode]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadTrfProfileForCategoryCode]",
                "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
       
        	
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:  UserTransferCountsVO =");
            	loggerValue.append(countsVO);
                log.debug(methodName,loggerValue);
            }
        }
        return countsVO;
    }

    /**
     * Method loadEffTrfProfileProduct
     * This method Load the effected tarnsfer Profile Products information i.e.
     * after performing the LEAST or
     * GREATEST functions
     * 
     * @param pCon
     * @param pProfileId
     * @return ArrayList
     * @throws BTSLBaseException
     */
    private ArrayList loadEffTrfProfileProductList(Connection pCon, String pProfileId) throws BTSLBaseException {
        final String methodName = "loadEffTrfProfileProduct";
        StringBuilder loggerValue= new StringBuilder();
        if (log.isDebugEnabled()) {
        	loggerValue.append("Entered: pProfileId=");
        	loggerValue.append(pProfileId);
            log.debug(methodName,loggerValue);
        }

       
        TransferProfileProductVO transferProfileProductVO = null;
        final ArrayList arrayList = new ArrayList();
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT tpp.product_code,prod.product_short_code,prod.product_name,");
        strBuff.append("GREATEST( tpp.min_residual_balance, ctpp.min_residual_balance) min_residual_balance,");
        strBuff.append("GREATEST(  tpp.c2s_min_txn_amt,ctpp.c2s_min_txn_amt)c2s_min_txn_amt,");
        strBuff.append("LEAST(tpp.max_balance,ctpp.max_balance)max_balance,");
        strBuff.append("LEAST(tpp.c2s_max_txn_amt,ctpp.c2s_max_txn_amt)c2s_max_txn_amt,");
        strBuff.append("LEAST(tpp.alerting_balance,ctpp.alerting_balance)alerting_balance,");
        strBuff.append("LEAST(tpp.max_pct_transfer_allowed,ctpp.max_pct_transfer_allowed)max_pct_transfer_allowed ");
        strBuff.append("FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products ctpp,products prod ");
        strBuff.append("WHERE tpp.profile_id=? AND tpp.profile_id=tp.profile_id AND catp.profile_id=ctpp.profile_id ");
        strBuff.append("AND tpp.product_code=ctpp.product_code AND tp.category_code=catp.category_code ");
        strBuff.append("AND catp.parent_profile_id=? AND catp.status='Y' AND tp.network_code = catp.network_code ");
        strBuff.append("AND tpp.product_code=prod.product_code AND ctpp.product_code=prod.product_code");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        try( PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, pProfileId);
            pstmt.setString(2, PretupsI.PARENT_PROFILE_ID_CATEGORY);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                transferProfileProductVO = new TransferProfileProductVO();
                transferProfileProductVO.setProductCode(rs.getString("product_code"));
                transferProfileProductVO.setProductShortCode(rs.getString("product_short_code"));
                transferProfileProductVO.setProductName(rs.getString("product_name"));
                transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
                transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
                transferProfileProductVO.setMaxBalance(PretupsBL.getDisplayAmount(transferProfileProductVO.getMaxBalanceAsLong()));
                transferProfileProductVO.setMinBalance(PretupsBL.getDisplayAmount(transferProfileProductVO.getMinResidualBalanceAsLong()));

                transferProfileProductVO.setAltBalanceLong(rs.getLong("alerting_balance"));
                transferProfileProductVO.setAltBalance(PretupsBL.getDisplayAmount(transferProfileProductVO.getAltBalanceLong()));
                transferProfileProductVO.setAllowedMaxPercentageInt(rs.getInt("max_pct_transfer_allowed"));
                transferProfileProductVO.setAllowedMaxPercentage(String.valueOf(transferProfileProductVO.getAllowedMaxPercentageInt()));

                transferProfileProductVO.setC2sMinTxnAmtAsLong(rs.getLong("c2s_min_txn_amt"));
                transferProfileProductVO.setC2sMaxTxnAmtAsLong(rs.getLong("c2s_max_txn_amt"));
                transferProfileProductVO.setC2sMinTxnAmt(PretupsBL.getDisplayAmount(transferProfileProductVO.getC2sMinTxnAmtAsLong()));
                transferProfileProductVO.setC2sMaxTxnAmt(PretupsBL.getDisplayAmount(transferProfileProductVO.getC2sMaxTxnAmtAsLong()));
                arrayList.add(transferProfileProductVO);
            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
			String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadEffTrfProfileProduct]", "", "",
                "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
			String logVal1=loggerValue.toString();
            log.error(methodName, logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TransferProfileDAO[loadEffTrfProfileProduct]", "", "",
                "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append( "Exiting: arrayList.size() =");
            	loggerValue.append(arrayList.size());
                log.debug(methodName,loggerValue);
            }
        }
        return arrayList;
    }

    /**
     * 
     * @param pCon
     * @param pCategoryCode
     * @param pNetworkCode
     *            String
     * @param pParentProfileID
     *            String
     * @return boolean
     * @throws BTSLBaseException
     * @author nilesh.kumar
     */
    public boolean isTransferProfileExistForCategoryCodeForAutoC2C(Connection pCon, String pCategoryCode, String pNetworkCode, String pParentProfileID, String pTransferProfileID) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder();
    	if (log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append("Entered:  pCategoryCode=");
        	loggerValue.append(pCategoryCode);
			loggerValue.append(",pNetworkCode=");
        	loggerValue.append(pNetworkCode);
        	loggerValue.append(",pParentProfileID=");
        	loggerValue.append(pParentProfileID);
            log.debug("isTransferProfileExistForCategoryCode",loggerValue);
        }
         
         
        boolean isExist = false;
        final StringBuilder strBuff = new StringBuilder("SELECT 1  FROM  transfer_profile WHERE ");
        strBuff.append(" network_code = ? AND status = 'Y' AND parent_profile_id=? AND profile_id=? ");
        final String sqlSelect = strBuff.toString();
        final String methodName = "isTransferProfileExistForCategoryCodeForAutoC2C";
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, pNetworkCode);
            pstmt.setString(2, pParentProfileID);
            pstmt.setString(3, pTransferProfileID);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                isExist = true;
            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append( "SQLException : ");
        	loggerValue.append(sqe.getMessage());
			String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileDAO[isTransferProfileExistForCategoryCodeForAutoC2C]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
			String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileDAO[isTransferProfileExistForCategoryCodeForAutoC2C]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	if (log.isDebugEnabled()) {
        		loggerValue.setLength(0);
            	loggerValue.append("Exiting: isExist=");
            	loggerValue.append(isExist);
                log.debug(methodName,loggerValue);
            }
        }
        return isExist;
    }
    
    
    
    
    
    
    
    public TransferProfileVO loadTransferProfThruProfileIDWithDisplayAmt(Connection pCon, String pProfileId, String pNetworkCode, String pCategoryCode, boolean pIsProductLoadRequired) throws BTSLBaseException {
        final String methodName = "loadTransferProfileThroughProfileID";
    	StringBuilder loggerValue= new StringBuilder(); 
		if (log.isDebugEnabled()) {
			loggerValue.setLength(0);
	    	loggerValue.append("Entered:pProfileId=");
	    	loggerValue.append(pProfileId);
	    	loggerValue.append(",pNetworkCode=");
	    	loggerValue.append(pNetworkCode);
	    	loggerValue.append(",p_category=");
	    	loggerValue.append(pCategoryCode);
            log.debug(methodName,loggerValue);
        }
         
         
        TransferProfileVO profileVO = null;
        final StringBuilder strBuff = new StringBuilder("SELECT TP.profile_id,TP.profile_name,TP.short_name,TP.status,TP.description,");
        strBuff.append(" TP.daily_transfer_in_count,TP.daily_transfer_in_value,");
        strBuff.append(" TP.weekly_transfer_in_count,TP.weekly_transfer_in_value,");
        strBuff.append(" TP.monthly_transfer_in_count,TP.monthly_transfer_in_value,");
        strBuff.append(" TP.daily_transfer_out_count,TP.daily_transfer_out_value,");
        strBuff.append(" TP.weekly_transfer_out_count,TP.weekly_transfer_out_value,");
        strBuff.append(" TP.monthly_transfer_out_count,TP.monthly_transfer_out_value,");
        strBuff.append(" TP.daily_subscriber_out_count , TP.daily_subscriber_out_value,");
        strBuff.append(" TP.weekly_subscriber_out_count , TP.weekly_subscriber_out_value,");
        strBuff.append(" TP.monthly_subscriber_out_count , TP.monthly_subscriber_out_value,");
        strBuff.append(" TP.outside_daily_in_count,TP.outside_daily_in_value,");
        strBuff.append(" TP.outside_weekly_in_count,TP.outside_weekly_in_value,");
        strBuff.append(" TP.outside_monthly_in_count,TP.outside_monthly_in_value,");
        strBuff.append(" TP.outside_daily_out_count,TP.outside_daily_out_value,");
        strBuff.append(" TP.outside_weekly_out_count,TP.outside_weekly_out_value,");
        strBuff.append(" TP.outside_monthly_out_count,TP.outside_monthly_out_value,");
        strBuff.append(" TP.modified_on,TP.network_code,TP.category_code,TP.alt_daily_transfer_in_count, ");
        strBuff.append("TP.alt_daily_transfer_in_value, TP.alt_weekly_transfer_in_count, ");
        strBuff.append("TP.alt_weekly_transfer_in_value, TP.alt_monthly_transfer_in_count, ");
        strBuff.append("TP.alt_monthly_transfer_in_value, TP.alt_daily_transfer_out_count, ");
        strBuff.append("TP.alt_daily_transfer_out_value, TP.alt_weekly_transfer_out_count, ");
        strBuff.append("TP.alt_weekly_transfer_out_value, TP.alt_monthly_transfer_out_count, ");
        strBuff.append("TP.alt_monthly_transfer_out_value, TP.alt_outside_daily_in_count, ");
        strBuff.append("TP.alt_outside_daily_in_value, TP.alt_outside_weekly_in_count, ");
        strBuff.append("TP.alt_outside_weekly_in_value, TP.alt_outside_monthly_in_count, ");
        strBuff.append("TP.alt_outside_monthly_in_value, TP.alt_outside_daily_out_count, ");
        strBuff.append("TP.alt_outside_daily_out_value, TP.alt_outside_weekly_out_count, ");
        strBuff.append("TP.alt_outside_weekly_out_value, TP.alt_outside_monthly_out_count, ");
        strBuff.append("TP.alt_outside_monthly_out_value, TP.alt_daily_subs_out_count, ");
        strBuff.append("TP.alt_daily_subs_out_value, TP.alt_weekly_subs_out_count, ");
        strBuff.append("TP.alt_weekly_subs_out_value, TP.alt_monthly_subs_out_count, ");
        strBuff.append("TP.alt_monthly_subs_out_value, TP.parent_profile_id, TP.is_default ,");

        // 6.4 changes
        strBuff.append(" daily_subscriber_in_count, daily_subscriber_in_value,  weekly_subscriber_in_count, ");
        strBuff.append(" weekly_subscriber_in_value, monthly_subscriber_in_count, monthly_subscriber_in_value, ");
        strBuff.append("alt_daily_subs_in_count, alt_daily_subs_in_value, ");
        strBuff.append("alt_weekly_subs_in_count, alt_weekly_subs_in_value, ");
        strBuff.append("alt_monthly_subs_in_count, alt_monthly_subs_in_value ");

        strBuff.append(" FROM transfer_profile TP");
        strBuff.append(" WHERE TP.profile_id=?  AND TP.network_code=? and TP.category_code=? ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            log.debug(methodName,loggerValue);
        }
        try(PreparedStatement pstmt = pCon.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, pProfileId);
            pstmt.setString(2, pNetworkCode);
            pstmt.setString(3, pCategoryCode);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                profileVO = new TransferProfileVO();
                profileVO.setProfileId(rs.getString("profile_id"));
                profileVO.setProfileName(rs.getString("profile_name"));
                profileVO.setShortName(rs.getString("short_name"));
                profileVO.setStatus(rs.getString("status"));
                profileVO.setDescription(rs.getString("description"));
                profileVO.setDailyInCount(rs.getLong("daily_transfer_in_count"));
                profileVO.setDailyInValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("daily_transfer_in_value"))));
                profileVO.setWeeklyInCount(rs.getLong("weekly_transfer_in_count"));
                profileVO.setWeeklyInValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("weekly_transfer_in_value"))));
                profileVO.setMonthlyInCount(rs.getLong("monthly_transfer_in_count"));
                profileVO.setMonthlyInValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("monthly_transfer_in_value"))));
                profileVO.setDailyOutCount(rs.getLong("daily_transfer_out_count"));
                profileVO.setDailyOutValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("daily_transfer_out_value"))));
                profileVO.setWeeklyOutCount(rs.getLong("weekly_transfer_out_count"));
                profileVO.setWeeklyOutValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("weekly_transfer_out_value"))));
                profileVO.setMonthlyOutCount(rs.getLong("monthly_transfer_out_count"));
                profileVO.setMonthlyOutValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("monthly_transfer_out_value"))));
                profileVO.setDailySubscriberOutCount(rs.getLong("daily_subscriber_out_count"));
                profileVO.setDailySubscriberOutValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("daily_subscriber_out_value"))));
                profileVO.setWeeklySubscriberOutCount(rs.getLong("weekly_subscriber_out_count"));
                profileVO.setWeeklySubscriberOutValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("weekly_subscriber_out_value"))));
                profileVO.setMonthlySubscriberOutCount(rs.getLong("monthly_subscriber_out_count"));
                profileVO.setMonthlySubscriberOutValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("monthly_subscriber_out_value"))));
                profileVO.setUnctrlDailyInCount(rs.getLong("outside_daily_in_count"));
                profileVO.setUnctrlDailyInValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("outside_daily_in_value"))));
                profileVO.setUnctrlWeeklyInCount(rs.getLong("outside_Weekly_in_count"));
                profileVO.setUnctrlWeeklyInValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("outside_Weekly_in_value"))));
                profileVO.setUnctrlMonthlyInCount(rs.getLong("outside_Monthly_in_count"));
                profileVO.setUnctrlMonthlyInValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("outside_Monthly_in_value"))));
                profileVO.setUnctrlDailyOutCount(rs.getLong("outside_daily_out_count"));
                profileVO.setUnctrlDailyOutValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("outside_daily_out_value"))));
                profileVO.setUnctrlWeeklyOutCount(rs.getLong("outside_Weekly_out_count"));
                profileVO.setUnctrlWeeklyOutValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("outside_Weekly_out_value"))));
                profileVO.setUnctrlMonthlyOutCount(rs.getLong("outside_Monthly_out_count"));
                profileVO.setUnctrlMonthlyOutValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("outside_Monthly_out_value"))));
                profileVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                profileVO.setNetworkCode(rs.getString("network_code"));
                profileVO.setCategory(rs.getString("category_code"));

                // alerting count/value

                profileVO.setDailyInAltCount(rs.getLong("alt_daily_transfer_in_count"));
                profileVO.setDailyInAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_daily_transfer_in_value"))));
                profileVO.setWeeklyInAltCount(rs.getLong("alt_weekly_transfer_in_count"));
                profileVO.setWeeklyInAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_weekly_transfer_in_value"))));
                profileVO.setMonthlyInAltCount(rs.getLong("alt_monthly_transfer_in_count"));
                profileVO.setMonthlyInAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_monthly_transfer_in_value"))));
                profileVO.setDailyOutAltCount(rs.getLong("alt_daily_transfer_out_count"));
                profileVO.setDailyOutAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_daily_transfer_out_value"))));
                profileVO.setWeeklyOutAltCount(rs.getLong("alt_weekly_transfer_out_count"));
                profileVO.setWeeklyOutAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_weekly_transfer_out_value"))));
                profileVO.setMonthlyOutAltCount(rs.getLong("alt_monthly_transfer_out_count"));
                profileVO.setMonthlyOutAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_monthly_transfer_out_value"))));

                profileVO.setUnctrlDailyInAltCount(rs.getLong("alt_outside_daily_in_count"));
                profileVO.setUnctrlDailyInAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_outside_daily_in_value"))));
                profileVO.setUnctrlWeeklyInAltCount(rs.getLong("alt_outside_Weekly_in_count"));
                profileVO.setUnctrlWeeklyInAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_outside_Weekly_in_value"))));
                profileVO.setUnctrlMonthlyInAltCount(rs.getLong("alt_outside_Monthly_in_count"));
                profileVO.setUnctrlMonthlyInAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_outside_Monthly_in_value"))));
                profileVO.setUnctrlDailyOutAltCount(rs.getLong("alt_outside_daily_out_count"));
                profileVO.setUnctrlDailyOutAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_outside_daily_out_value"))));
                profileVO.setUnctrlWeeklyOutAltCount(rs.getLong("alt_outside_Weekly_out_count"));
                profileVO.setUnctrlWeeklyOutAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_outside_Weekly_out_value"))));
                profileVO.setUnctrlMonthlyOutAltCount(rs.getLong("alt_outside_Monthly_out_count"));
                profileVO.setUnctrlMonthlyOutAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_outside_Monthly_out_value"))));

                profileVO.setDailySubscriberOutAltCount(rs.getLong("alt_daily_subs_out_count"));
                profileVO.setDailySubscriberOutAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_daily_subs_out_value"))));
                profileVO.setWeeklySubscriberOutAltCount(rs.getLong("alt_weekly_subs_out_count"));
                profileVO.setWeeklySubscriberOutAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_weekly_subs_out_value"))));
                profileVO.setMonthlySubscriberOutAltCount(rs.getLong("alt_monthly_subs_out_count"));
                profileVO.setMonthlySubscriberOutAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_monthly_subs_out_value"))));
                profileVO.setParentProfileID(rs.getString("parent_profile_id"));
                profileVO.setIsDefault(rs.getString("is_default"));

                // 6.4 changes
                profileVO.setDailySubscriberInCount(rs.getLong("daily_subscriber_in_count"));
                profileVO.setDailySubscriberInValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("daily_subscriber_in_value"))));
                profileVO.setWeeklySubscriberInCount(rs.getLong("weekly_subscriber_in_count"));
                profileVO.setWeeklySubscriberInValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("weekly_subscriber_in_value"))));
                profileVO.setMonthlySubscriberInCount(rs.getLong("monthly_subscriber_in_count"));
                profileVO.setMonthlySubscriberInValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("monthly_subscriber_in_value"))));
                profileVO.setDailySubscriberInAltCount(rs.getLong("alt_daily_subs_in_count"));
                profileVO.setDailySubscriberInAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_daily_subs_in_value"))));
                profileVO.setWeeklySubscriberInAltCount(rs.getLong("alt_weekly_subs_in_count"));
                profileVO.setWeeklySubscriberInAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_weekly_subs_in_value"))));
                profileVO.setMonthlySubscriberInAltCount(rs.getLong("alt_monthly_subs_in_count"));
                profileVO.setMonthlySubscriberInAltValue(Long.valueOf(PretupsBL.getDisplayAmount(rs.getLong("alt_monthly_subs_in_value"))));

            }
            if (pIsProductLoadRequired) {
                final ArrayList profileProductList = this.loadTransferProfileProductsList(pCon, pProfileId);
                // load product list
                final ArrayList productList = new NetworkProductDAO().loadNetworkProductList(pCon, pNetworkCode);
                final int profileProductLength = profileProductList.size();
                int productLength = productList.size();
                // if any new product add after create the transfer profile.
                // when modify the transfer profile then
                // new products entry maintain in TRANSFER_PROFILE_PRODUCTS
                // table.
                if (profileProductLength != productLength && productLength > profileProductLength) {
                    TransferProfileProductVO transferProfileProductVO = null;
                    TransferProfileProductVO profileProductVO = null;
                    for (int i = 0; i < profileProductLength; i++) {
                        transferProfileProductVO = (TransferProfileProductVO) profileProductList.get(i);
                        for (int j = 0; j < productLength; j++) {
                            profileProductVO = (TransferProfileProductVO) productList.get(j);
                            // removed the product details from product list,
                            // which is already exist in
                            // TRANSFER_PROFILE_PRODUCTS.
                            // only new products is avilable in product list.
                            if (transferProfileProductVO.getProductCode().equals(profileProductVO.getProductCode())) {
                                productList.remove(j);
                                break;
                            }
                        }
                        productLength = productList.size();
                    }
                    // only new products list .
                    for (int j = 0; j < productLength; j++) {
                        profileProductVO = (TransferProfileProductVO) productList.get(j);
                        transferProfileProductVO = new TransferProfileProductVO();
                        transferProfileProductVO.setProductCode(profileProductVO.getProductCode());
                        transferProfileProductVO.setProductName(profileProductVO.getProductName());
                        transferProfileProductVO.setMinResidualBalanceAsLong(0);
                        transferProfileProductVO.setMaxBalanceAsLong(0);
                        profileProductList.add(transferProfileProductVO);
                    }
                }
                profileVO.setProfileProductList(profileProductList);
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileDAO[loadTransferProfileThroughProfileID]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
    		String logVal1=loggerValue.toString();
            log.error(methodName,logVal1);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "TransferProfileDAO[loadTransferProfileThroughProfileID]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
         
       
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: profileVO =");
            	loggerValue.append(profileVO.toString());
                log.debug(methodName,loggerValue);
            }
        }
        return profileVO;
    }

    
    

}