/**
 * @(#)LoyaltyDAO.java
 *                     Copyright(c) 2005, Bharti Telesoft Ltd.
 *                     All Rights Reserved
 * 
 *                     <description>
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     rakesh.sinha Dec,2013 Initital Creation
 *                     Vibhu Trehan Jan,2014 Modification & customization
 * 
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 */

package com.btsl.pretups.loyaltymgmt.businesslogic;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.processes.businesslogic.ActivationBonusVO;
import com.btsl.util.BTSLUtil;

public class LoyaltyDAO {

    private static final Log LOG = LogFactory.getLog(LoyaltyDAO.class.getClass().getName());
    private LoyaltyDAOQry loyaltyDAOQry = (LoyaltyDAOQry) ObjectProducer.getObject(QueryConstants.LOYALTY_DAO,QueryConstants.QUERY_PRODUCER);
    /**
     * rakesh
     * this method is used for correcting subscriber retailer mapping
     * 
     * @param con
     * @param userID
     * @param listSize
     * @return int
     * @throws BTSLBaseException
     */
    public int insertMappingSummary(Connection con, String userID, int listSize) throws BTSLBaseException {
        
        StringBuilder strBuild = null;
        int insertCount = 0;
        final String methodName = "insertMappingSummary";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered: userID=" + userID + " listSize=" + listSize);
        }
        try {
            java.util.Date date = new Date();

            strBuild = new StringBuilder();
            strBuild.append("INSERT INTO subs_activation_summary (user_id,  ");
            strBuild.append(" activated_users,activation_date ) values ");
            strBuild.append("(?,?,?)");
            String insertQuery = strBuild.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Query insertQuery:" + insertQuery);
            }
           try(PreparedStatement psmtInsert = con.prepareStatement(insertQuery);)
           {
            psmtInsert.setString(1, userID);
            psmtInsert.setInt(2, listSize);
            psmtInsert.setDate(3, BTSLUtil.getSQLDateFromUtilDate(date));
            insertCount = psmtInsert.executeUpdate();
        } 
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException: " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[insertMappingSummary]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception: " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[insertMappingSummary]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
           
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * This method loads the list of profileSetVO from the profile_set table
     * 
     * @param Connection
     *            con
     * @param String
     *            userID
     * @return setId
     * @throws BTSLBaseException
     * @author vibhu.trehan
     */
    public PromotionDetailsVO loadSetIdByUserId(Connection con, ArrayList arr) throws BTSLBaseException {
        final String methodName = "loadSetIdByUserId";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_msisdn=" + arr.get(0));
        }
        
        PromotionDetailsVO promotionDetailsVO = null;
        ResultSet rs = null;
        try {
            promotionDetailsVO = new PromotionDetailsVO();
            StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT cusers.lms_profile ");
            sbf.append("FROM channel_users cusers ");
            sbf.append("WHERE cusers.user_id=? ");
            String selectQuery = sbf.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug("loadProfile", "SQL Query :" + selectQuery);
            }
            try(PreparedStatement pstmt = con.prepareStatement(selectQuery);)
            {
            int arra=arr.size();
            for (int i = 0; i < arra; i++) {
                pstmt.setString(1, (String) arr.get(i));
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    if (i == 0) {
                        promotionDetailsVO.set_setId(rs.getString("lms_profile"));
                    } else {
                        promotionDetailsVO.set_toSetId(rs.getString("lms_profile"));
                    }
                }
            }

        } 
        }catch (SQLException sqle) {
            LOG.error(methodName, "SQLException: " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyDAO[loadSetIdByUserId]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadProfile", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception: " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyDAO[loadProfile]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
                    
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting ");
            }
        }
        return promotionDetailsVO;
    }

    /**
     * This method loads the list of profileSetVO from the profile_set table
     * 
     * @param Connection
     *            con
     * @param String
     *            setId
     * @return ProfileSetVO
     * @throws BTSLBaseException
     * @author vibhu.trehan
     */
    public PromotionDetailsVO loadProfile(Connection con, String setId) throws BTSLBaseException {
        final String methodName = "loadProfile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered setId=" + setId);
        }
        
        PromotionDetailsVO promotionDetailsVO = null;
        
        try {
            StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT ps.PROMOTION_TYPE, ps.set_name, network_code,LAST_VERSION,OPT_IN_OUT_ENABLED  ");
            sbf.append("FROM profile_set ps ");
            sbf.append("WHERE ps.set_id=? AND ps.profile_type=? AND ps.status='Y' ");
            String selectQuery = sbf.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "SQL Query :" + selectQuery);
            }
            try(PreparedStatement pstmt = con.prepareStatement(selectQuery);)
            {
            pstmt.setString(1, setId);
            pstmt.setString(2, PretupsI.LMS_PROFILE_TYPE);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                promotionDetailsVO = new PromotionDetailsVO();
                promotionDetailsVO.setPromotionType(rs.getString("PROMOTION_TYPE"));
                promotionDetailsVO.setSetName(rs.getString("set_name"));
                promotionDetailsVO.setNetworkCode(rs.getString("network_code"));
                promotionDetailsVO.setVersion(rs.getString("LAST_VERSION"));
                promotionDetailsVO.setOptInOutEnabled(rs.getString("OPT_IN_OUT_ENABLED"));
            }

        }
            }
        }catch (SQLException sqle) {
            LOG.error(methodName, "SQLException: " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyDAO[loadProfile]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            LOG.error(methodName, "Exception: " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyDAO[loadProfile]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting ");
            }
        }
        return promotionDetailsVO;
    }

    /**
     * vibhu.trehan
     * to load profile details on basis of servicecode,profle set and version
     * 
     * @param con
     * @param p_actProfileServiceTypeID
     * @param p_actProifleSetId
     * @param p_actProfileSetVersion
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public PromotionDetailsVO loadLMSProfileAndVersion(Connection con, String lmsProfileServiceType, String lmsPromoSetId, long txnAmount) throws BTSLBaseException {
        final String methodName = "loadLMSProfileAndVersion";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_actProfileServiceTypeID=" + lmsProfileServiceType + "lmsPromoSetId" + lmsPromoSetId);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        PromotionDetailsVO promotionDetailsVO = null;
        try {

            

            String selectQuery = loyaltyDAOQry.loadLMSProfileAndVersion(lmsProfileServiceType);
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, lmsPromoSetId);
            pstmtSelect.setString(2, lmsProfileServiceType);
            pstmtSelect.setString(3, PretupsI.PROFILE_TRANS);
            pstmtSelect.setLong(4, txnAmount);
            pstmtSelect.setLong(5, txnAmount);
            pstmtSelect.setString(6, lmsPromoSetId);
            pstmtSelect.setString(7, PretupsI.STATUS_ACTIVE);
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                promotionDetailsVO = new PromotionDetailsVO();
                promotionDetailsVO.setStartRangeAsString(Long.toString(rs.getLong("start_range")));
                promotionDetailsVO.setEndRangeAsString(Long.toString(rs.getLong("end_range")));
                promotionDetailsVO.setStartRange(Long.parseLong(rs.getString("start_range")));
                promotionDetailsVO.setEndRange(Long.parseLong(rs.getString("end_range")));
                promotionDetailsVO.setPointsTypeCode(rs.getString("points_type"));
                promotionDetailsVO.setPointsAsString(rs.getString("points"));
                promotionDetailsVO.setPoints(rs.getLong("points"));
                promotionDetailsVO.setDetailType(rs.getString("detail_type"));
                promotionDetailsVO.setDetailSubType(rs.getString("detail_subtype"));
                promotionDetailsVO.setSubscriberType(rs.getString("subscriber_type"));
                promotionDetailsVO.setPeriodId(rs.getString("period_id"));
                promotionDetailsVO.setType(rs.getString("type"));
                promotionDetailsVO.setUserType(rs.getString("user_type"));
                promotionDetailsVO.setServiceCode(rs.getString("service_code"));
                promotionDetailsVO.setMinLimit(rs.getLong("min_limit"));
                promotionDetailsVO.setMaxLimit(rs.getLong("max_limit"));
                promotionDetailsVO.setSubscriberType(rs.getString("subscriber_type"));
                promotionDetailsVO.setVersion(rs.getString("version"));
                promotionDetailsVO.setPointsType(rs.getString("points_type"));
				promotionDetailsVO.setProductCode(rs.getString("product_code"));
            }

            return promotionDetailsVO;
        }// end of try
        catch (SQLException sqle) {
            LOG.error(methodName, "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyDAO[loadLMSProfileAndVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("LoyaltyDAO", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            LOG.error("LoyaltyDAO", "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyDAO[loadLMSProfileAndVersion]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LoyaltyDAO", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
        }// end of catch
        finally {
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing result set.", e);
              }
        	try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing result set.", e);
              }
            if (LOG.isDebugEnabled()) {
                LOG.debug("LoyaltyDAO", "Exiting  loadLMSProfileAndVersion promotionDetailsVO:" + promotionDetailsVO);
            }
        }// end of finally
    }

    public int creditLoyaltyPointToPayeeC2S(Connection con, LoyaltyVO loyaltyVO) throws BTSLBaseException {
        final String methodName = "DistributeLoyaltyPoints Method";

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered with userId: " + loyaltyVO.getUserid());
        }
        int count = 0;
        try {

            count = creditLoyaltyPoint(con, loyaltyVO);
            if (count > 0) {
            } else {
                con.rollback();
                throw new BTSLBaseException(this, "DistributeLoyaltyPoints", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
            }
        } catch (BTSLBaseException bex) {
            try {
                con.rollback();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error("", "Exception : " + bex);
            LOG.errorTrace(methodName, bex);
        } catch (Exception ex) {
            LOG.error("", "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
        }

        return count;
    }

    public int creditLoyaltyPointToPayeerO2C(Connection con, LoyaltyVO loyaltyVO) throws BTSLBaseException {
        final String methodName = "DistributeLoyaltyPoints Method";
        if (LOG.isDebugEnabled()) {

            LOG.debug(methodName, "Entered with userId: " + loyaltyVO.getUserid());
        }
        int count = 0;
        try {
            count = creditLoyaltyPoint(con, loyaltyVO);
            if (count > 0) {
                con.commit();
            } else {
                con.rollback();
                throw new BTSLBaseException(this, "DistributeLoyaltyPoints", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
            }
        } catch (BTSLBaseException bex) {
            try {
                con.rollback();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
           
            LOG.error("", "Exception : " + bex);
            LOG.errorTrace(methodName, bex);
        } catch (Exception ex) {
            LOG.error("", "Exception : " + ex);
            LOG.errorTrace(methodName, ex);
        }

        return count;

    }

    public int debitNetworkLoyaltyStock(Connection con, LoyaltyVO loyaltyVO, long totaldebitPoint) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("debitNetworkLoyaltyStock Method.....", "");
        }
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement psmtInsert = null;

        int count = 0;
        long loyaltypoint = 0;
        long cLoyaltypoint = 0;
        long updatedloyaltypoint = 0;
        ResultSet rs = null;
        int insertCount = 0;
        final String methodName = "debitNetworkLoyaltyStock";
        try {
            StringBuilder selectstrBuild = new StringBuilder("SELECT LOYALTY_STOCK,PREVIOUS_LOYALTY_STOCK FROM LOYALTY_STOCK WHERE NETWORK_CODE=? AND NETWORK_CODE_FOR= ? AND PRODUCT_CODE= ? FOR UPDATE ");
            StringBuilder updatestrBuild = new StringBuilder("UPDATE LOYALTY_STOCK SET LOYALTY_STOCK= ?,PREVIOUS_LOYALTY_STOCK= ? WHERE NETWORK_CODE=? AND NETWORK_CODE_FOR= ? AND PRODUCT_CODE= ?");
            StringBuilder inserstrBuild = new StringBuilder("INSERT INTO LOYALTY_STOCK_TRANSACTION (TXN_NO,NETWORK_CODE,LOYALTY_STOCK,PREVIOUS_LOYALTY_STOCK,LAST_TXN_TYPE,LOYALTY_POINT_SPEND,TXN_STATUS,REQUESTED_POINTS,CREATED_ON,CREATED_BY) VALUES (?,?,?,?,?,?,?,?,?,?)");
            String sqlSelect = selectstrBuild.toString();
            String sqlUpdate = updatestrBuild.toString();
            String sqlInsert = inserstrBuild.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug("debitNetworkLoyaltyStock Method.....sqlSelect ", "" + sqlSelect);
            }
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, loyaltyVO.getNetworkCode());
            pstmt.setString(2, loyaltyVO.getNetworkCode());
			pstmt.setString(3,loyaltyVO.getProductCode() );	
            rs = pstmt.executeQuery();
            while (rs.next()) {
                loyaltypoint = rs.getLong("PREVIOUS_LOYALTY_STOCK");
                cLoyaltypoint = rs.getLong("LOYALTY_STOCK");
            }
            if (cLoyaltypoint >= totaldebitPoint) {
                updatedloyaltypoint = cLoyaltypoint - totaldebitPoint;
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOYALTY_NETWORK_STOCK_NOT_OK);
            }
            loyaltypoint = cLoyaltypoint;
            pstmt.clearParameters();
            if (LOG.isDebugEnabled()) {
                LOG.debug("debitNetworkLoyaltyStock Method.....sqlUpdate ", "" + sqlUpdate + "previous loyaltypoint " + loyaltypoint + "CurrentLoyaltypoint " + updatedloyaltypoint);
            }

            pstmt1 = con.prepareStatement(sqlUpdate);
            pstmt1.setLong(1, updatedloyaltypoint);
            pstmt1.setLong(2, loyaltypoint);
            pstmt1.setString(3, loyaltyVO.getNetworkCode());
            pstmt1.setString(4, loyaltyVO.getNetworkCode());
			pstmt1.setString(5, loyaltyVO.getProductCode());
            count = pstmt1.executeUpdate();
            if (count < 1) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOYALTY_NETWORK_STOCK_NOT_OK);
            } else {
                count = 0;
                psmtInsert = con.prepareStatement(sqlInsert);
                psmtInsert.setString(1, loyaltyVO.getLmstxnid());
                psmtInsert.setString(2, loyaltyVO.getNetworkCode());
                psmtInsert.setLong(3, loyaltypoint - totaldebitPoint);
                psmtInsert.setLong(4, loyaltypoint);
                psmtInsert.setString(5, loyaltyVO.getServiceType());
                psmtInsert.setLong(6, totaldebitPoint);
                psmtInsert.setString(7, "SUCCESS");
                psmtInsert.setLong(8, totaldebitPoint);
                psmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(new Date()));
                psmtInsert.setString(10, PretupsI.SYSTEM);
                insertCount = psmtInsert.executeUpdate();
                if (insertCount == 0) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOYALTY_PROCESSING_FAILED);
                } else {
                    count = 1;
                }
            }
        } catch (Exception ex) {
            LOG.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOYALTY_NETWORK_STOCK_NOT_OK);
        } finally {
        	
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmt1!= null){
                	pstmt1.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
        	try{
                if (psmtInsert!= null){
                	psmtInsert.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting with Count :" + count);
            }
        }
        return count;
    }


    public int creditLoyaltyPoint(Connection con, LoyaltyVO loyaltyVO) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("creditLoyaltyPoint ", " UserId :" + loyaltyVO.getUserid());
        }
        final String methodName = "ActivationBonusCalculation";
        double loyaltyPoint = 0;
        ActivationBonusVO bonusOldVO = null;
        Date realCurrentDate = new Date();
        int insertCount = 0;
        try {
            // check entry already present in BONUS table corresponding to
            // user_id,product_type, point date and product code
            bonusOldVO = checkUserAlreadyExist(loyaltyVO.getUserid(), realCurrentDate, loyaltyVO.getProductCode(), con, null);
            if (bonusOldVO != null) {
                // if it is present then update the entries
            	loyaltyPoint = loyaltyVO.getTotalCrLoyaltyPoint();
                bonusOldVO.setLastAllocationType(PretupsI.PROFILE_TRANS);
                bonusOldVO.setPointsDate(realCurrentDate);

                bonusOldVO.setUserId(loyaltyVO.getUserid());
                bonusOldVO.setLastAllocationdate(realCurrentDate);
                bonusOldVO.setProductCode(loyaltyVO.getProductCode());
                // Write Profile Bonus Log
                bonusOldVO.setPoints(loyaltyPoint);
                bonusOldVO.setTransferId(loyaltyVO.getTxnId());
				bonusOldVO.setVersion(loyaltyVO.getVersion());
                insertCount = updateBonusOfUser(bonusOldVO, con, null);

            } else {
				long accpoints=0L;
				bonusOldVO=checkUserExistLastDateDetail(loyaltyVO.getUserid(),loyaltyVO.getProductCode(), con);
                if (bonusOldVO == null) {
                    bonusOldVO = new ActivationBonusVO();
                    bonusOldVO.setAccumulatedPoints(loyaltyVO.getTotalCrLoyaltyPoint());
                } else {
                    bonusOldVO.setAccumulatedPoints(loyaltyVO.getTotalCrLoyaltyPoint() + bonusOldVO.getAccumulatedPoints());
                }
                bonusOldVO.setProfileType("LMS");

                // Brajesh
                // Done to set different bucket codes for different types of
                // allocation types
                LookupsVO lookupsVO = new LookupsVO();
                lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.BUCKET_CODE, PretupsI.BUCKET_CODE_TRANS);
                bonusOldVO.setBucketCode(lookupsVO.getLookupName());
                bonusOldVO.setProductCode(loyaltyVO.getProductCode());
                bonusOldVO.setLastAllocationType(PretupsI.PROFILE_TRANS);
                bonusOldVO.setCreatedOn(realCurrentDate);
                bonusOldVO.setCreatedBy(PretupsI.SYSTEM);
                bonusOldVO.setModifiedOn(realCurrentDate);
                bonusOldVO.setModifiedBy(PretupsI.SYSTEM);
                bonusOldVO.setPointsDate(realCurrentDate);
                bonusOldVO.setUserId(loyaltyVO.getUserid());
                bonusOldVO.setLastAllocationdate(realCurrentDate);
                bonusOldVO.setPoints(loyaltyVO.getTotalCrLoyaltyPoint());
                bonusOldVO.setTransferId(loyaltyVO.getTxnId());
                bonusOldVO.setSetID(loyaltyVO.getSetId());
                bonusOldVO.setVersion(loyaltyVO.getVersion());
                insertCount = saveBonus(bonusOldVO, con);
                if (insertCount <= 0) {

                    throw new BTSLBaseException(methodName, "process", PretupsErrorCodesI.INSERTION_ERROR_BONUS_TABLE);
                }
            }
        } catch (Exception ex) {
            LOG.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, "creditLoyaltyPoint", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);

        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("creditLoyaltyPointToPayeeC2S", "Exiting with Count :" + insertCount);
            }
        }
        return insertCount;
    }

    private int saveBonus(ActivationBonusVO bonusVO, Connection con) throws BTSLBaseException {
        final String methodName = "saveBonus";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_processingDate: bonusVO: " + bonusVO.toString());
        }
        int count = 0;
        try {
            
            // insert entries in bonus table if user does not exist, saveBonus()
            StringBuilder qryBuilder = new StringBuilder();
            qryBuilder.append(" INSERT INTO BONUS (profile_type,user_id_or_msisdn,points, ");
            qryBuilder.append(" bucket_code,product_code,points_date,last_redemption_id,last_redemption_on, ");
            qryBuilder.append(" last_allocation_type,last_allocated_on,created_on,created_by,modified_on, ");
            qryBuilder.append(" modified_by,transfer_id, accumulated_points,profile_id,version)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            String query = qryBuilder.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug("makeQuery", "Query:" + query);
            }
           try(PreparedStatement saveBonusStmt = con.prepareStatement(query);)
           {
            saveBonusStmt.clearParameters();
            saveBonusStmt.setString(1, bonusVO.getProfileType());
            saveBonusStmt.setString(2, bonusVO.getUserId());
            saveBonusStmt.setDouble(3, bonusVO.getPoints());
            saveBonusStmt.setString(4, bonusVO.getBucketCode());
            saveBonusStmt.setString(5, bonusVO.getProductCode());
            saveBonusStmt.setTimestamp(6, BTSLUtil.getSQLDateTimeFromUtilDate(bonusVO.getPointsDate()));
            saveBonusStmt.setString(7, bonusVO.getLastRedemptionId());
            saveBonusStmt.setTimestamp(8, BTSLUtil.getSQLDateTimeFromUtilDate(bonusVO.getLastRedemptionDate()));
            saveBonusStmt.setString(9, bonusVO.getLastAllocationType());
            saveBonusStmt.setTimestamp(10, BTSLUtil.getSQLDateTimeFromUtilDate(bonusVO.getLastAllocationdate()));
            saveBonusStmt.setTimestamp(11, BTSLUtil.getSQLDateTimeFromUtilDate(bonusVO.getCreatedOn()));
            saveBonusStmt.setString(12, bonusVO.getCreatedBy());
            saveBonusStmt.setTimestamp(13, BTSLUtil.getSQLDateTimeFromUtilDate(bonusVO.getModifiedOn()));
            saveBonusStmt.setString(14, bonusVO.getModifiedBy());
            saveBonusStmt.setString(15, bonusVO.getTransferId());
            saveBonusStmt.setLong(16, bonusVO.getAccumulatedPoints());
            saveBonusStmt.setString(17, bonusVO.getSetId());
            saveBonusStmt.setString(18, bonusVO.getVersion());
            count = saveBonusStmt.executeUpdate();
        } 
        }catch (SQLException se) {
            LOG.error(methodName, "SQLException: " + se.getMessage());
            LOG.errorTrace(methodName, se);
            throw new BTSLBaseException("ActivationBonusCalculation", methodName, PretupsErrorCodesI.INSERTION_ERROR_BONUS_TABLE);
        } catch (Exception e) {
            LOG.error(methodName, "Exception: " + e.getMessage());
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("ActivationBonusCalculation", methodName, PretupsErrorCodesI.INSERTION_ERROR_BONUS_TABLE);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting..... count: " + count);
            }
        }
        return count;
    }

    private ActivationBonusVO checkUserAlreadyExist(String userID, Date currentDate, String productCode, Connection con, String actionType) throws BTSLBaseException {
        final String methodName = "checkUserAlreadyExist";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_useId: " + userID + " p_processedUpto: " + currentDate + " productCode: " + productCode + " , actionType=" + actionType);
        }
        ActivationBonusVO bonusVO = null;
        ResultSet rst = null;
        PreparedStatement checkUserExistStmt = null;
        try {
            
            // check user details exist in BONUS table, before adding new
            // entries, checkUserAlreadyExist()
            
            //
            // Brajesh
            LookupsVO lookupsVO = new LookupsVO();
            lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.BUCKET_CODE, PretupsI.BUCKET_CODE_TRANS);

            //
            
            String query = loyaltyDAOQry.checkUserAlreadyExist(actionType);
            if (LOG.isDebugEnabled()) {
                LOG.debug("makeQuery", "Query: " + query);
            }
            checkUserExistStmt = con.prepareStatement(query);

            checkUserExistStmt.clearParameters();
            checkUserExistStmt.setString(1, userID);
            checkUserExistStmt.setString(2, productCode);
            checkUserExistStmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(currentDate));
            // Brajesh
            if (BTSLUtil.isNullString(actionType)) {
                checkUserExistStmt.setString(4, lookupsVO.getLookupName());
            }
            //
            rst = checkUserExistStmt.executeQuery();
            if (rst.next()) {
                bonusVO = new ActivationBonusVO();
                bonusVO.setProfileType(rst.getString("profile_type"));
                bonusVO.setUserId(rst.getString("user_id_or_msisdn"));
                bonusVO.setPoints(rst.getLong("points"));
                bonusVO.setBucketCode(rst.getString("bucket_code"));
                bonusVO.setProductCode(rst.getString("product_code"));
                bonusVO.setPointsDate(rst.getDate("points_date"));
                bonusVO.setLastRedemptionId(rst.getString("last_redemption_id"));
                bonusVO.setLastRedemptionDate(rst.getDate("last_redemption_on"));
                bonusVO.setLastAllocationType(rst.getString("last_allocation_type"));
                bonusVO.setLastAllocationdate(rst.getDate("last_allocated_on"));
                bonusVO.setCreatedOn(rst.getDate("created_on"));
                bonusVO.setCreatedBy(rst.getString("created_by"));
                bonusVO.setModifiedOn(rst.getDate("modified_on"));
                bonusVO.setModifiedBy(rst.getString("modified_by"));
                bonusVO.setTransferId(rst.getString("transfer_id"));
            }
        } catch (SQLException se) {
            LOG.error(methodName, "SQLException: " + se.getMessage());
            LOG.errorTrace(methodName, se);
            throw new BTSLBaseException("ActivationBonusCalculation", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
        } catch (Exception e) {
            LOG.error(methodName, "SQLException: " + e.getMessage());
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("RunLMSForTargetCredit", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
        } finally {
        	try{
            	if (rst!= null){
            		rst.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }
        	try{
            	if (checkUserExistStmt!= null){
            		checkUserExistStmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting.....bonusVO: " + bonusVO);
            }
        }
        return bonusVO;
    }

    public ActivationBonusVO checkUserExistLastDateDetail(String userId, String productCode, Connection con) throws BTSLBaseException {
        final String methodName = "checkUserExistLastDateDetail";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered userId: " + userId + " productCode: " + productCode);
        }
        ActivationBonusVO bonusVO = null;
        ResultSet rst = null;
        PreparedStatement checkUserExistLastDateStmt = null;
        try {

            // Brajesh

            LookupsVO lookupsVO = new LookupsVO();
            lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.BUCKET_CODE, PretupsI.BUCKET_CODE_TRANS);
            //
            
            // if user details does not exist in bonus table, we check if it has
            // any previous record so that we can add points in accumulated
            StringBuilder qryBuilder = new StringBuilder();
            qryBuilder.append(" select accumulated_points from bonus ");
            qryBuilder.append(" where USER_ID_OR_MSISDN=? AND profile_type='LMS' ");
            qryBuilder.append(" AND product_code=?  and points_date = ");
            qryBuilder.append(" (select max (points_date) from bonus where USER_ID_OR_MSISDN=? AND profile_type=? ");
            qryBuilder.append(" AND product_code=? ) ");
            String query = qryBuilder.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Query: " + query);
            }
            checkUserExistLastDateStmt = con.prepareStatement(query);

            checkUserExistLastDateStmt.clearParameters();
            checkUserExistLastDateStmt.setString(1, userId);
            checkUserExistLastDateStmt.setString(2, productCode);
            checkUserExistLastDateStmt.setString(3, userId);
            checkUserExistLastDateStmt.setString(4, PretupsI.LMS_PROFILE_TYPE);
            checkUserExistLastDateStmt.setString(5, productCode);
            rst = checkUserExistLastDateStmt.executeQuery();
            if (rst.next()) {
                bonusVO = new ActivationBonusVO();
                bonusVO.setAccumulatedPoints(rst.getLong("accumulated_points"));
            }

        } catch (SQLException se) {
            LOG.error(methodName, "SQLException: " + se.getMessage());
            LOG.errorTrace(methodName, se);
            throw new BTSLBaseException("LoyaltyDAO", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
        } catch (Exception e) {
            LOG.error(methodName, "SQLException: " + e.getMessage());
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException("LoyaltyDAO", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
        } finally {
        	try{
            	if (rst!= null){
            		rst.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }
        	try{
            	if (checkUserExistLastDateStmt!= null){
            		checkUserExistLastDateStmt.close();
            	}
            }
            catch (SQLException e){
            	LOG.error("An error occurred closing result set.", e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting.....bonusVO: " + bonusVO);
            }
        }
        return bonusVO;
    }

    public int updateBonusOfUser(ActivationBonusVO bonusVO, Connection con, String actionType) throws BTSLBaseException {
        final String methodName = "updateBonusOfUser";

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered bonusVO: " + bonusVO.toString() + " actionType = " + actionType);
        }
        int count = 0;
        long currentTotalLoyaltyPointsAfterCreditDebit = 0L;
        long loyaltyPointCrDr = 0L;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreparedStatement updateBonusStmt = null;
        try {
            // Brajesh
            LookupsVO lookupsVO = new LookupsVO();
            lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.BUCKET_CODE, PretupsI.BUCKET_CODE_TRANS);
            //
           

            // Locking the bonus table while updating the bonus through
            // credit/debit
           
            String sqlQueryForLock = loyaltyDAOQry.updateBonusOfUser();
            short index = 1;
            
            pstmt = con.prepareStatement(sqlQueryForLock);
            pstmt.setString(index++, bonusVO.getUserId());
            pstmt.setString(index++, bonusVO.getUserId());
			pstmt.setString(index++, bonusVO.getProductCode());
			pstmt.setString(index++, bonusVO.getProductCode());
            rs = pstmt.executeQuery();
            long accuPoint = 0L;
            if (rs.next()) {
                accuPoint = rs.getLong("ACCUMULATED_POINTS");
            }

            pstmt.clearParameters();
            loyaltyPointCrDr = BTSLUtil.parseDoubleToLong(bonusVO.getPoints()) ;
            if (!BTSLUtil.isNullString(actionType) && PretupsI.LPT_BATCH_ACTION_CREDIT.equals(actionType)) {
                currentTotalLoyaltyPointsAfterCreditDebit = accuPoint + loyaltyPointCrDr;
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, " Point credit for user : " + loyaltyPointCrDr + " , currently user have points = " + accuPoint);
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, " current Points which user has after performing credit: " + currentTotalLoyaltyPointsAfterCreditDebit);
                }

            } else if (!BTSLUtil.isNullString(actionType) && PretupsI.LPT_BATCH_ACTION_DEBIT.equals(actionType)) {
                if (accuPoint > 0 && accuPoint >= loyaltyPointCrDr) {
                    currentTotalLoyaltyPointsAfterCreditDebit = accuPoint - loyaltyPointCrDr;
                } else {
                    LOG.error(methodName, " Accumulated bonus Point for user " + bonusVO.getUserId() + " is " + accuPoint + " and debit points " + loyaltyPointCrDr + " is not allowded.");
                    throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.LMS_POINT_DEBIT_LESS_ACCUMULATED);
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, " Point debit for user : " + loyaltyPointCrDr + " , currently user have points = " + accuPoint);
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, " current Points which user has after performing debit: " + currentTotalLoyaltyPointsAfterCreditDebit);
                }

            }
            StringBuilder qryBuilder = new StringBuilder();
            if (BTSLUtil.isNullString(actionType)) {
            	qryBuilder.append(" UPDATE BONUS SET ACCUMULATED_POINTS=ACCUMULATED_POINTS+?, points=?, last_allocation_type=?,last_allocated_on=?, ");
            	qryBuilder.append(" transfer_id=? ,LAST_REDEMPTION_ID= ?, LAST_REDEMPTION_ON=?  ,version=? WHERE user_id_or_msisdn=? AND profile_type='LMS' AND  ");
            	qryBuilder.append(" product_code=?  AND points_date=? AND bucket_code=? ");
            } else if (!BTSLUtil.isNullString(actionType) && (PretupsI.LPT_BATCH_ACTION_CREDIT.equals(actionType) || PretupsI.LPT_BATCH_ACTION_DEBIT.equals(actionType))) {
            	qryBuilder.append(" UPDATE BONUS SET ACCUMULATED_POINTS=?, points=?, last_allocation_type=?,last_allocated_on=?, ");
            	qryBuilder.append(" transfer_id=? ,LAST_REDEMPTION_ID= ?, LAST_REDEMPTION_ON=? WHERE user_id_or_msisdn=? AND profile_type='LMS' AND  ");
            	qryBuilder.append(" product_code=?  AND points_date=?  ");
            }
            String query = qryBuilder.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Query: " + query);
            }
            updateBonusStmt = con.prepareStatement(query);

            int i = 1;
            if (BTSLUtil.isNullString(actionType)) {
                updateBonusStmt.setDouble(i++, bonusVO.getPoints());
            } else if (!BTSLUtil.isNullString(actionType) && (PretupsI.LPT_BATCH_ACTION_CREDIT.equals(actionType) || PretupsI.LPT_BATCH_ACTION_DEBIT.equals(actionType))) {
                if (!BTSLUtil.isNullString(actionType) && PretupsI.LPT_BATCH_ACTION_CREDIT.equals(actionType)) {
                    updateBonusStmt.setDouble(i++, Double.parseDouble(String.valueOf(currentTotalLoyaltyPointsAfterCreditDebit)));
                } else if (!BTSLUtil.isNullString(actionType) && PretupsI.LPT_BATCH_ACTION_DEBIT.equals(actionType)) {
                    updateBonusStmt.setDouble(i++, Double.parseDouble(String.valueOf(currentTotalLoyaltyPointsAfterCreditDebit)));
                }
            }
            if (BTSLUtil.isNullString(actionType)) {
                updateBonusStmt.setDouble(i++, bonusVO.getPoints());
            } else if (!BTSLUtil.isNullString(actionType) && (PretupsI.LPT_BATCH_ACTION_CREDIT.equals(actionType) || PretupsI.LPT_BATCH_ACTION_DEBIT.equals(actionType))) {
                if (!BTSLUtil.isNullString(actionType) && PretupsI.LPT_BATCH_ACTION_CREDIT.equals(actionType)) {
                    updateBonusStmt.setDouble(i++, bonusVO.getPoints());
                } else if (!BTSLUtil.isNullString(actionType) && PretupsI.LPT_BATCH_ACTION_DEBIT.equals(actionType)) {
                    updateBonusStmt.setDouble(i++, -(bonusVO.getPoints()));
                }
            }
            updateBonusStmt.setString(i++, bonusVO.getLastAllocationType());
            updateBonusStmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(bonusVO.getLastAllocationdate()));
            updateBonusStmt.setString(i++, bonusVO.getTransferId());
			if(BTSLUtil.isNullString(actionType)){
				updateBonusStmt.setString(i++,"");
				updateBonusStmt.setTimestamp(i++, null);
			} else {
				if(!BTSLUtil.isNullString(actionType) &&PretupsI.LPT_BATCH_ACTION_CREDIT.equals(actionType)){
					updateBonusStmt.setString(i++,"");
					updateBonusStmt.setTimestamp(i++, null);
				} else if(!BTSLUtil.isNullString(actionType) &&PretupsI.LPT_BATCH_ACTION_DEBIT.equals(actionType)){
					updateBonusStmt.setString(i++,bonusVO.getTransferId());
					updateBonusStmt.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(new Date()));
				}
			}
			if(BTSLUtil.isNullString(actionType)){
				updateBonusStmt.setString(i++,bonusVO.getVersion());
			}
            updateBonusStmt.setString(i++, bonusVO.getUserId());
            updateBonusStmt.setString(i++, bonusVO.getProductCode());
            updateBonusStmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(bonusVO.getPointsDate()));
            //
            if (BTSLUtil.isNullString(actionType)) {
                updateBonusStmt.setString(i++, bonusVO.getBucketCode());
            }
            count = updateBonusStmt.executeUpdate();
            if (!BTSLUtil.isNullString(actionType) && (PretupsI.LPT_BATCH_ACTION_CREDIT.equals(actionType) || PretupsI.LPT_BATCH_ACTION_DEBIT.equals(actionType)) && (count>0)) {
                    con.commit();
            }

        } catch (SQLException se) {
            try {
                con.rollback();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error(methodName, "SQLException: " + se.getMessage());
            LOG.errorTrace(methodName, se);
            throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.BONUS_TABLE_NOT_UPDATED);
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception ee) {
                LOG.errorTrace(methodName, ee);
            }
            LOG.error(methodName, "SQLException: " + e.getMessage());
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.BONUS_TABLE_NOT_UPDATED);
        } finally {
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
        	try{
                if (updateBonusStmt != null){
                	updateBonusStmt .close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting..... count: " + count);
            }
        }
        return count;
    }

    /**
     * Method loadLmsProfileList.
     * This method loads all the LMS Profile
     * 
     * @param con
     *            Connection
     * @param networkCode
     *            String
     * @param domainCode
     *            String
     * @param p_ruleType
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadLMSProfileList(Connection con, String networkCode, String domainCode) throws BTSLBaseException {
        String methodName = "loadLMSProfileList";

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:networkCode=" + networkCode + ",domainCode=" + domainCode);
        }
        ArrayList lmsProfileList = new ArrayList();
        
        try {
            StringBuilder selectQuery = new StringBuilder("SELECT PRSET.set_id ,PRSET.set_name ");
            selectQuery.append(" FROM profile_set PRSET ");
            selectQuery.append(" WHERE PRSET.status NOT IN ('N','R','S') AND PRSET.profile_type=? ");
            selectQuery.append(" ORDER BY PRSET.set_name asc ");
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Query=" + selectQuery);
            }
            try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery.toString());)
            {
            pstmtSelect.setString(1, PretupsI.LMS_PROFILE_TYPE);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            LoyaltyVO loyaltyVO = null;
            while (rs.next()) {
                loyaltyVO = new LoyaltyVO();
                loyaltyVO.setLmsProfileName(rs.getString("set_name"));
                loyaltyVO.setSetId(rs.getString("set_id"));
                lmsProfileList.add(loyaltyVO);
                loyaltyVO = null;
            }
        }
            }
        }catch (SQLException sqe) {
            LOG.error(methodName, "SQLException:" + sqe.getMessage());
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleDAO[loyaltyVOList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            LOG.error(methodName, "Exception:" + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleDAO[loyaltyVOList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loyaltyVOList", "error.general.processing");
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting:list size=" + lmsProfileList.size());
            }
        }
        return lmsProfileList;
    }

    public int creditDebitLoyaltyPoint(Connection con, LoyaltyVO loyaltyVO, String actionType, String promotionType, String userID) throws BTSLBaseException {
        final String methodName = "creditDebitLoyaltyPoint";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " UserId= " + loyaltyVO.getUserid() + " , actionType= " + actionType + " promotionType= " + promotionType + " userID=" + userID);
        }
        double loyaltypoint = 0;
        ActivationBonusVO bonusOldVO = null;
        Date realCurrentDate = new Date();
        int insertCount = 0;
        try {
            // check entry already present in BONUS table corresponding to
            // user_id,product_type, point date and product code
            bonusOldVO = checkUserAlreadyExist(loyaltyVO.getUserid(), realCurrentDate, loyaltyVO.getProductCode(), con, actionType);
            if (bonusOldVO != null) {
                // if it is present then update the entries
                loyaltypoint = loyaltyVO.getTotalCrLoyaltyPoint();
                bonusOldVO.setLastAllocationType(promotionType);
                bonusOldVO.setPointsDate(realCurrentDate);

                bonusOldVO.setUserId(loyaltyVO.getUserid());
                bonusOldVO.setLastAllocationdate(realCurrentDate);
                bonusOldVO.setProductCode(loyaltyVO.getProductCode());
                bonusOldVO.setPoints(loyaltypoint);
                bonusOldVO.setTransferId(loyaltyVO.getTxnId());
                insertCount = updateBonusOfUser(bonusOldVO, con, actionType);
                if (insertCount <= 0) {
                    throw new BTSLBaseException(methodName, "process", PretupsErrorCodesI.UPDATED_ERROR_BONUS_TABLE);
                }
            } else {
                bonusOldVO = checkUserExistLastDateDetail(loyaltyVO.getUserid(), loyaltyVO.getProductCode(), con);
                // and if it is not present then insert new entry
                if (bonusOldVO == null) {
                    bonusOldVO = new ActivationBonusVO();
                    bonusOldVO.setAccumulatedPoints(loyaltyVO.getTotalCrLoyaltyPoint());
					if(!BTSLUtil.isNullString(actionType) && PretupsI.LPT_BATCH_ACTION_DEBIT.equals(actionType)){
						LOG.error(methodName, " Accumulated bonus Point for user "+loyaltyVO.getUserid()+" is "+" 0 " +" and debit points " + loyaltyVO.getTotalCrLoyaltyPoint()+" is not allowded.");
						throw new BTSLBaseException(methodName,methodName,PretupsErrorCodesI.LMS_POINT_DEBIT_LESS_ACCUMULATED);
					}
                } else {
					if(!BTSLUtil.isNullString(actionType) && PretupsI.LPT_BATCH_ACTION_CREDIT.equals(actionType)){
                    bonusOldVO.setAccumulatedPoints(loyaltyVO.getTotalCrLoyaltyPoint() + bonusOldVO.getAccumulatedPoints());
					} else if(!BTSLUtil.isNullString(actionType) && PretupsI.LPT_BATCH_ACTION_DEBIT.equals(actionType)){
						if(bonusOldVO.getAccumulatedPoints()>0 && bonusOldVO.getAccumulatedPoints() >=loyaltyVO.getTotalCrLoyaltyPoint())
							bonusOldVO.setAccumulatedPoints(bonusOldVO.getAccumulatedPoints() - loyaltyVO.getTotalCrLoyaltyPoint());
						else{
							LOG.error(methodName, " Accumulated bonus Point for user "+loyaltyVO.getUserid()+" is "+bonusOldVO.getAccumulatedPoints() +" and debit points " + loyaltyVO.getTotalCrLoyaltyPoint()+" is not allowded.");
							throw new BTSLBaseException(methodName,methodName,PretupsErrorCodesI.LMS_POINT_DEBIT_LESS_ACCUMULATED);
						}
						
					}else {
						bonusOldVO.setAccumulatedPoints(loyaltyVO.getTotalCrLoyaltyPoint()+bonusOldVO.getAccumulatedPoints());
					}
				}
                bonusOldVO.setProfileType(PretupsI.LMS);
                // Brajesh
                // Done to set different bucket codes for different types of
                // allocation types
                LookupsVO lookupsVO = new LookupsVO();
                lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.BUCKET_CODE, PretupsI.BUCKET_CODE_TRANS);
                // Brajesh
                bonusOldVO.setBucketCode(lookupsVO.getLookupName());
                bonusOldVO.setProductCode(loyaltyVO.getProductCode());
                bonusOldVO.setLastAllocationType(promotionType);
                bonusOldVO.setCreatedOn(realCurrentDate);
                bonusOldVO.setCreatedBy(userID);
                bonusOldVO.setModifiedOn(realCurrentDate);
                bonusOldVO.setModifiedBy(userID);
                bonusOldVO.setPointsDate(realCurrentDate);
                bonusOldVO.setUserId(loyaltyVO.getUserid());
                bonusOldVO.setLastAllocationdate(realCurrentDate);
                if (!BTSLUtil.isNullString(actionType) && PretupsI.LPT_BATCH_ACTION_CREDIT.equalsIgnoreCase(actionType)) {
                	 bonusOldVO.setPoints(loyaltyVO.getTotalCrLoyaltyPoint());
               } else if (!BTSLUtil.isNullString(actionType) && PretupsI.LPT_BATCH_ACTION_DEBIT.equalsIgnoreCase(actionType)) {
            	   bonusOldVO.setPoints(-loyaltyVO.getTotalCrLoyaltyPoint());
               }
                bonusOldVO.setTransferId(loyaltyVO.getTxnId());
                bonusOldVO.setLastRedemptionId(loyaltyVO.getLmstxnid());
                if (!BTSLUtil.isNullString(actionType) && PretupsI.LPT_BATCH_ACTION_DEBIT.equalsIgnoreCase(actionType)) {
                bonusOldVO.setLastRedemptionDate(realCurrentDate);
                }
                bonusOldVO.setSetID(loyaltyVO.getSetId());
                bonusOldVO.setVersion(loyaltyVO.getVersion());
                insertCount = saveBonus(bonusOldVO, con);
                if (insertCount <= 0) {
                    throw new BTSLBaseException(methodName, "process", PretupsErrorCodesI.INSERTION_ERROR_BONUS_TABLE);
                }
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (Exception ex) {
            LOG.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);

        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting with Count :" + insertCount);
            }
        }
        return insertCount;
    }

}
