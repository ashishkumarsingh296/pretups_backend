package com.txn.pretups.loyaltymgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.processes.businesslogic.ActivationBonusVO;
import com.btsl.util.BTSLUtil;

public class LoyaltyTxnDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method loads the list of profileSetVO from the profile_set table
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_setId
     * @return ProfileSetVO
     * @throws BTSLBaseException
     * @author vibhu.trehan
     */
    public PromotionDetailsVO loadProfile(Connection p_con, String p_setId) throws BTSLBaseException {
        final String methodName = "loadProfile";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_setId=" + p_setId);
        }
        PreparedStatement pstmt = null;
        PromotionDetailsVO promotionDetailsVO = null;
        ResultSet rs = null;
        try {
            StringBuffer sbf = new StringBuffer();
			sbf.append("SELECT ps.PROMOTION_TYPE, ps.set_name, network_code,LAST_VERSION,OPT_IN_OUT_ENABLED  ");
            sbf.append("FROM profile_set ps ");
            sbf.append("WHERE ps.set_id=? AND ps.profile_type=? AND ps.status='Y' ");
            String selectQuery = sbf.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "SQL Query :" + selectQuery);
            }
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_setId);
            pstmt.setString(2, PretupsI.LMS_PROFILE_TYPE);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                promotionDetailsVO = new PromotionDetailsVO();
                promotionDetailsVO.setPromotionType(rs.getString("PROMOTION_TYPE"));
                promotionDetailsVO.setSetName(rs.getString("set_name"));
                promotionDetailsVO.setNetworkCode(rs.getString("network_code"));
				promotionDetailsVO.setVersion(rs.getString("LAST_VERSION"));
				promotionDetailsVO.setOptInOutEnabled(rs.getString("OPT_IN_OUT_ENABLED"));
            }

        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyDAO[loadProfile]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyDAO[loadProfile]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle) {
                    _log.errorTrace(methodName, sqle);
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
            }
        }
        return promotionDetailsVO;
    }

    /**
     * vibhu.trehan
     * to load profile details on basis of servicecode,profle set and version
     * 
     * @param p_con
     * @param p_actProfileServiceTypeID
     * @param p_actProifleSetId
     * @param p_actProfileSetVersion
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public PromotionDetailsVO loadLMSProfileAndVersion(Connection p_con, String p_lmsProfileServiceType, String p_lmsPromoSetId, long p_txnAmount) throws BTSLBaseException {
        final String methodName = "loadLMSProfileAndVersion";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_actProfileServiceTypeID=" + p_lmsProfileServiceType + "p_lmsPromoSetId" + p_lmsPromoSetId);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        PromotionDetailsVO promotionDetailsVO = null;
        // ArrayList addActivationDetailList = new ArrayList();
        try {

        	LoyaltyTxnQry selectQueryBuff=(LoyaltyTxnQry)ObjectProducer.getObject(QueryConstants.LOYALTY_TXN_QRY, QueryConstants.QUERY_PRODUCER);
            String selectQuery = selectQueryBuff.loadLMSProfileAndVersionQry(p_lmsProfileServiceType);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_lmsPromoSetId);
            // pstmtSelect.setString(2,p_actProfileSetVersion);
            pstmtSelect.setString(2, p_lmsProfileServiceType);
            pstmtSelect.setString(3, PretupsI.PROFILE_TRANS);
            pstmtSelect.setLong(4, p_txnAmount);
            pstmtSelect.setLong(5, p_txnAmount);
            pstmtSelect.setString(6, p_lmsPromoSetId);
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
                // addActivationDetailList.add(activationProfileDeatilsVO);
            }

            return promotionDetailsVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyDAO[loadLMSProfileAndVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("LoyaltyDAO", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("LoyaltyDAO", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyDAO[loadLMSProfileAndVersion]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LoyaltyDAO", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
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
                _log.debug("LoyaltyDAO", "Exiting  loadLMSProfileAndVersion promotionDetailsVO:" + promotionDetailsVO);
            }
        }// end of finally
    }

    public int creditLoyaltyPointToPayeeC2S(Connection con, LoyaltyVO loyaltyVO) throws BTSLBaseException {
        final String methodName = "DistributeLoyaltyPoints Method";

        if (_log.isDebugEnabled()) {

            _log.debug(methodName, "Entered with userId: " + loyaltyVO.getUserid());
        }
        int count = 0;
        try {

            long totalCreditPoints = 0;
            // int count=0;
            count = creditLoyaltyPoint(con, loyaltyVO);
            totalCreditPoints = loyaltyVO.getCrpointtoPayee();
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
                _log.errorTrace(methodName, e);
            }
            ;
            _log.error("", "Exception : " + bex);
            _log.errorTrace(methodName, bex);
        } catch (Exception ex) {
            _log.error("", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
        }

        return count;
    }

    public int creditLoyaltyPointToPayeerO2C(Connection con, LoyaltyVO loyaltyVO) throws BTSLBaseException {
        final String methodName = "DistributeLoyaltyPoints Method";
        if (_log.isDebugEnabled()) {

            _log.debug(methodName, "Entered with userId: " + loyaltyVO.getUserid());
        }
        int count = 0;
        try {

            long totalCreditPoints = 0;
            // int count=0;
            count = creditLoyaltyPoint(con, loyaltyVO);
            totalCreditPoints = loyaltyVO.getCrpointtoPayeer();
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
                _log.errorTrace(methodName, e);
            }
            ;
            _log.error("", "Exception : " + bex);
            _log.errorTrace(methodName, bex);
        } catch (Exception ex) {
            _log.error("", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
        }

        return count;

    }

    public int creditLoyaltyPoint(Connection con, LoyaltyVO p_loyaltyVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("creditLoyaltyPoint ", " UserId :" + p_loyaltyVO.getUserid());
        }
        final String methodName = "ActivationBonusCalculation";
        double p_loyaltypoint = 0;
        double c_loyaltypoint = 0;
        ActivationBonusVO bonusOldVO = null;
        Date realCurrentDate = new Date();
        int insertCount = 0;
        try {
            // check entry already present in BONUS table corresponding to
            // user_id,product_type, point date and product code
            bonusOldVO = checkUserAlreadyExist(p_loyaltyVO.getUserid(), realCurrentDate, p_loyaltyVO.getProductCode(), con,null);
            if (bonusOldVO != null) {
                // if it is present then update the entries
                p_loyaltypoint = p_loyaltyVO.getTotalCrLoyaltyPoint();
                bonusOldVO.setLastAllocationType(PretupsI.PROFILE_TRANS);
                bonusOldVO.setPointsDate(realCurrentDate);

                bonusOldVO.setUserId(p_loyaltyVO.getUserid());
                bonusOldVO.setLastAllocationdate(realCurrentDate);
                bonusOldVO.setProductCode(p_loyaltyVO.getProductCode());
                // Write Profile Bonus Log
                // ProfileBonusLog.log("Success",bonusOldVO,p_redemptionVO.getUserID(),c2sTransferVO.getReceiverMsisdn(),bonusVO.getPoints(),PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue()),bonusOldVO.getPoints());
                bonusOldVO.setPoints(p_loyaltypoint);
                bonusOldVO.setTransferId(p_loyaltyVO.getTxnId());
                bonusOldVO.setVersion(p_loyaltyVO.getVersion());// Added By
                                                                // brajesh
                
                bonusOldVO.setSetID(p_loyaltyVO.getSetId());
                insertCount = updateBonusOfUser(bonusOldVO, con);

                /*
                 * //
                 * bonusOldVO.setSetID(p_loyaltyVO.getSetId());
                 * 
                 * //
                 */// //
            } else {
                long accpoints = 0L;
                // bonusOldVO = new ActivationBonusVO();
                bonusOldVO = checkUserExistLastDateDetail(p_loyaltyVO.getUserid(), p_loyaltyVO.getProductCode(), con);
                // and if it is not present then insert new entry
                /*
                 * if(bonusOldVO==null)
                 * bonusOldVO = new ActivationBonusVO();
                 * accpoints= (long)p_loyaltyVO.getTotalCrLoyaltyPoint() +
                 * bonusOldVO.getAccumulatedPoints();
                 */
                if (bonusOldVO == null) {
                    bonusOldVO = new ActivationBonusVO();
                    bonusOldVO.setAccumulatedPoints(p_loyaltyVO.getTotalCrLoyaltyPoint());
                } else {
                    bonusOldVO.setAccumulatedPoints(p_loyaltyVO.getTotalCrLoyaltyPoint() + bonusOldVO.getAccumulatedPoints());
                }
                // bonusOldVO.setAccumulatedPoints(accpoints);
                bonusOldVO.setProfileType("LMS");

                // Brajesh
                // Done to set different bucket codes for different types of
                // allocation types
                LookupsVO lookupsVO = new LookupsVO();
                lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.BUCKET_CODE, PretupsI.BUCKET_CODE_TRANS);

                // Brajesh
                bonusOldVO.setBucketCode(lookupsVO.getLookupName());
                //
                bonusOldVO.setProductCode(p_loyaltyVO.getProductCode());
                bonusOldVO.setLastAllocationType(PretupsI.PROFILE_TRANS);
                bonusOldVO.setCreatedOn(realCurrentDate);
                bonusOldVO.setCreatedBy(PretupsI.SYSTEM);
                bonusOldVO.setModifiedOn(realCurrentDate);
                bonusOldVO.setModifiedBy(PretupsI.SYSTEM);
                // bonusOldVO.setLastAllocationType("VOLUME");
                bonusOldVO.setPointsDate(realCurrentDate);
                bonusOldVO.setUserId(p_loyaltyVO.getUserid());
                bonusOldVO.setLastAllocationdate(realCurrentDate);
                bonusOldVO.setPoints(p_loyaltyVO.getTotalCrLoyaltyPoint());
                bonusOldVO.setTransferId(p_loyaltyVO.getTxnId());
                // Brajesh
                bonusOldVO.setSetID(p_loyaltyVO.getSetId());
                bonusOldVO.setVersion(p_loyaltyVO.getVersion());

                // Write Profile Bonus Log
                // ProfileBonusLog.log("Success",bonusVO,actBonusSubsMappingVO.getUserID(),c2sTransferVO.getReceiverMsisdn(),bonusVO.getPoints(),PretupsBL.getDisplayAmount(c2sTransferVO.getTransferValue()),0);
                insertCount = saveBonus(bonusOldVO, con);
                if (insertCount <= 0) {
                    // _logger.debug("process","Entry not inserted in BONUS table");

                    throw new BTSLBaseException(methodName, "process", PretupsErrorCodesI.INSERTION_ERROR_BONUS_TABLE);
                }
            }
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, "creditLoyaltyPoint", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);

        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("creditLoyaltyPointToPayeeC2S", "Exiting with Count :" + insertCount);
            }
        }
        return insertCount;
    }

    public int updateBonusOfUser(ActivationBonusVO p_bonusVO, Connection p_con) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("updateBonusOfUser", "Entered p_bonusVO: " + p_bonusVO.toString());
        }
        int count = 0;
        final String methodName = "checkUserAlreadyExist";
        try {
            // Brajesh
            LookupsVO lookupsVO = new LookupsVO();
            lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.BUCKET_CODE, PretupsI.BUCKET_CODE_TRANS);
            //
            

            StringBuffer qryBuffer = new StringBuffer();
            qryBuffer.append(" UPDATE BONUS SET ACCUMULATED_POINTS=ACCUMULATED_POINTS+?, points=?, last_allocation_type=?,last_allocated_on=?, ");
            qryBuffer.append(" transfer_id=? ,version=? ,profile_id=? WHERE user_id_or_msisdn=? AND profile_type='LMS' AND  ");
            qryBuffer.append(" product_code=?  AND points_date=? AND bucket_code=? ");
            String query = qryBuffer.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("makeQuery", "Query: " + query);
            }
            try(PreparedStatement _updateBonusStmt = p_con.prepareStatement(query);)
            {
            int i = 1;
            _updateBonusStmt.setDouble(i++, p_bonusVO.getPoints());
            _updateBonusStmt.setDouble(i++, p_bonusVO.getPoints());
            _updateBonusStmt.setString(i++, p_bonusVO.getLastAllocationType());
            _updateBonusStmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getLastAllocationdate()));
            _updateBonusStmt.setString(i++, p_bonusVO.getTransferId());
            //
            _updateBonusStmt.setString(i++, p_bonusVO.getVersion());// brajesh
            _updateBonusStmt.setString(i++,p_bonusVO.getSetId());
            _updateBonusStmt.setString(i++, p_bonusVO.getUserId());
            _updateBonusStmt.setString(i++, p_bonusVO.getProductCode());
            _updateBonusStmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getPointsDate()));
            //
            _updateBonusStmt.setString(i++, p_bonusVO.getBucketCode());

            //
            count = _updateBonusStmt.executeUpdate();
        } 
        }catch (SQLException se) {
            _log.error(methodName, "SQLException: " + se.getMessage());
            _log.errorTrace(methodName, se);
            throw new BTSLBaseException("RunLMSForTargetCredit", methodName, PretupsErrorCodesI.BONUS_TABLE_NOT_UPDATED);
        } catch (Exception e) {
            _log.error(methodName, "SQLException: " + e.getMessage());
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException("RunLMSForTargetCredit", methodName, PretupsErrorCodesI.BONUS_TABLE_NOT_UPDATED);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("updateBonusOfUser", "Exiting..... count: " + count);
            }
        }
        return count;
    }

    public ActivationBonusVO checkUserExistLastDateDetail(String p_userId, String p_productCode, Connection p_con) throws BTSLBaseException {
        final String methodName = "checkUserExistLastDateDetail";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_userId: ");
        	loggerValue.append(p_userId);
        	loggerValue.append(" p_productCode: ");
        	loggerValue.append(p_productCode);
            _log.debug(methodName,loggerValue);
        }
        ActivationBonusVO bonusVO = null;
         
        try {

            // Brajesh

            LookupsVO lookupsVO = new LookupsVO();
            lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.BUCKET_CODE, PretupsI.BUCKET_CODE_TRANS);
            //
            
            // if user details does not exist in bonus table, we check if it has
            // any previous record so that we can add points in accumulated
            StringBuffer qryBuffer = new StringBuffer();
            qryBuffer.append(" select accumulated_points from bonus ");
            qryBuffer.append(" where USER_ID_OR_MSISDN=? AND profile_type='LMS' ");
            qryBuffer.append(" AND product_code=?  and points_date = ");
            qryBuffer.append(" (select max (points_date) from bonus where USER_ID_OR_MSISDN=? AND profile_type=? ");
            qryBuffer.append(" AND product_code=? ) ");
            String query = qryBuffer.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query: " + query);
            }
            try(PreparedStatement _checkUserExistLastDateStmt = p_con.prepareStatement(query);)
            {
            _checkUserExistLastDateStmt.clearParameters();
            _checkUserExistLastDateStmt.setString(1, p_userId);
            _checkUserExistLastDateStmt.setString(2, p_productCode);
            // Brajesh
            // _checkUserExistLastDateStmt.setString(3,lookupsVO.getLookupName());

            //
            _checkUserExistLastDateStmt.setString(3, p_userId);
            _checkUserExistLastDateStmt.setString(4, PretupsI.LMS_PROFILE_TYPE);
            _checkUserExistLastDateStmt.setString(5, p_productCode);
            //
            // _checkUserExistLastDateStmt.setString(7,lookupsVO.getLookupName());

            //
           try(ResultSet rst = _checkUserExistLastDateStmt.executeQuery();)
           {
            if (rst.next()) {
                bonusVO = new ActivationBonusVO();
                bonusVO.setAccumulatedPoints(rst.getLong("accumulated_points"));
            }

        } 
            }
        }catch (SQLException se) {
            _log.error(methodName, "SQLException: " + se.getMessage());
            _log.errorTrace(methodName, se);
            throw new BTSLBaseException("LoyaltyDAO", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
        } catch (Exception e) {
            _log.error(methodName, "SQLException: " + e.getMessage());
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException("LoyaltyDAO", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
        } finally {
          
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting.....bonusVO: " + bonusVO);
            }
        }
        return bonusVO;
    }

    private ActivationBonusVO checkUserAlreadyExist(String p_userId, Date p_currentDate, String p_productCode, Connection p_con, String p_actionType) throws BTSLBaseException {
        final String methodName = "checkUserAlreadyExist";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_useId: ");
        	loggerValue.append(p_userId);
        	loggerValue.append(" p_processedUpto: ");
        	loggerValue.append(p_currentDate);
        	loggerValue.append(" p_productCode: ");
        	loggerValue.append(p_productCode);
            _log.debug(methodName, loggerValue);
        }
        ActivationBonusVO bonusVO = null;
       
        try {
             
            // check user details exist in BONUS table, before adding new
            // entries, checkUserAlreadyExist()
            StringBuffer qryBuffer = new StringBuffer();
            //
            // Brajesh
            LookupsVO lookupsVO = new LookupsVO();
            lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.BUCKET_CODE, PretupsI.BUCKET_CODE_TRANS);

            LoyaltyTxnQry loyaltyTxnQry=(LoyaltyTxnQry)ObjectProducer.getObject(QueryConstants.LOYALTY_TXN_QRY, QueryConstants.QUERY_PRODUCER);
            
            String query=loyaltyTxnQry.checkUserAlreadyExistQry();
            
            if (_log.isDebugEnabled()) {
                _log.debug("makeQuery", "Query: " + query);
            }
            try(PreparedStatement _checkUserExistStmt = p_con.prepareStatement(query);)
            {
            _checkUserExistStmt.clearParameters();
            _checkUserExistStmt.setString(1, p_userId);
            _checkUserExistStmt.setString(2, p_productCode);
            _checkUserExistStmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(p_currentDate));
            // Brajesh
			if(BTSLUtil.isNullString(p_actionType)){
            _checkUserExistStmt.setString(4, lookupsVO.getLookupName());
			}
			//
            try(ResultSet rst = _checkUserExistStmt.executeQuery();)
            {
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
        } 
            }
        }catch (SQLException se) {
            _log.error(methodName, "SQLException: " + se.getMessage());
            _log.errorTrace(methodName, se);
            throw new BTSLBaseException("ActivationBonusCalculation", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
        } catch (Exception e) {
            _log.error(methodName, "SQLException: " + e.getMessage());
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException("RunLMSForTargetCredit", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
        } finally {
          
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting.....bonusVO: " + bonusVO);
            }
        }
        return bonusVO;
    }

    private int saveBonus(ActivationBonusVO p_bonusVO, Connection p_con) throws BTSLBaseException {
        final String methodName = "saveBonus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_processingDate: p_bonusVO: " + p_bonusVO.toString());
        }
        int count = 0;
        try {
            
            // insert entries in bonus table if user does not exist, saveBonus()
            StringBuffer qryBuffer = new StringBuffer();
            qryBuffer.append(" INSERT INTO BONUS (profile_type,user_id_or_msisdn,points, ");
            qryBuffer.append(" bucket_code,product_code,points_date,last_redemption_id,last_redemption_on, ");
            qryBuffer.append(" last_allocation_type,last_allocated_on,created_on,created_by,modified_on, ");
            qryBuffer.append(" modified_by,transfer_id, accumulated_points,profile_id,version)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
            String query = qryBuffer.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("makeQuery", "Query:" + query);
            }
            try(PreparedStatement _saveBonusStmt = p_con.prepareStatement(query);)
            {

            _saveBonusStmt.clearParameters();
            _saveBonusStmt.setString(1, p_bonusVO.getProfileType());
            _saveBonusStmt.setString(2, p_bonusVO.getUserId());
            _saveBonusStmt.setDouble(3, p_bonusVO.getPoints());
            _saveBonusStmt.setString(4, p_bonusVO.getBucketCode());
            _saveBonusStmt.setString(5, p_bonusVO.getProductCode());
            _saveBonusStmt.setDate(6, BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getPointsDate()));
            _saveBonusStmt.setString(7, p_bonusVO.getLastRedemptionId());
            _saveBonusStmt.setDate(8, BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getLastRedemptionDate()));
            _saveBonusStmt.setString(9, p_bonusVO.getLastAllocationType());
            _saveBonusStmt.setDate(10, BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getLastAllocationdate()));
            _saveBonusStmt.setDate(11, BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getCreatedOn()));
            _saveBonusStmt.setString(12, p_bonusVO.getCreatedBy());
            _saveBonusStmt.setDate(13, BTSLUtil.getSQLDateFromUtilDate(p_bonusVO.getModifiedOn()));
            _saveBonusStmt.setString(14, p_bonusVO.getModifiedBy());
            _saveBonusStmt.setString(15, p_bonusVO.getTransferId());
            _saveBonusStmt.setLong(16, p_bonusVO.getAccumulatedPoints());
            // Brajesh
            _saveBonusStmt.setString(17, p_bonusVO.getSetId());
            _saveBonusStmt.setString(18, p_bonusVO.getVersion());

            //
            count = _saveBonusStmt.executeUpdate();
        } 
        }catch (SQLException se) {
            _log.error(methodName, "SQLException: " + se.getMessage());
            _log.errorTrace(methodName, se);
            throw new BTSLBaseException("ActivationBonusCalculation", methodName, PretupsErrorCodesI.INSERTION_ERROR_BONUS_TABLE);
        } catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            throw new BTSLBaseException("ActivationBonusCalculation", methodName, PretupsErrorCodesI.INSERTION_ERROR_BONUS_TABLE);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting..... count: " + count);
            }
        }
        return count;
    }

    public int debitNetworkLoyaltyStock(Connection con, LoyaltyVO loyaltyVO, long totaldebitPoint) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("debitNetworkLoyaltyStock Method.....", "");
        }
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement psmtInsert = null;

        int count = 0;
        long p_loyaltypoint = 0;
        long c_loyaltypoint = 0;
        long updatedloyaltypoint = 0;
        ResultSet rs = null;
        ResultSet rs1 = null;
        int insertCount = 0;
        final String methodName = "debitNetworkLoyaltyStock";
        try {
            StringBuffer SelectstrBuff = new StringBuffer("SELECT LOYALTY_STOCK,PREVIOUS_LOYALTY_STOCK FROM LOYALTY_STOCK WHERE NETWORK_CODE=? AND NETWORK_CODE_FOR= ? AND PRODUCT_CODE= ? FOR UPDATE ");
            StringBuffer UpdatestrBuff = new StringBuffer("UPDATE LOYALTY_STOCK SET LOYALTY_STOCK= ?,PREVIOUS_LOYALTY_STOCK= ? WHERE NETWORK_CODE=? AND NETWORK_CODE_FOR= ? AND PRODUCT_CODE= ?");
            StringBuffer InserstrBuff = new StringBuffer("INSERT INTO LOYALTY_STOCK_TRANSACTION (TXN_NO,NETWORK_CODE,LOYALTY_STOCK,PREVIOUS_LOYALTY_STOCK,LAST_TXN_TYPE,LOYALTY_POINT_SPEND,TXN_STATUS,REQUESTED_POINTS,CREATED_ON,CREATED_BY) VALUES (?,?,?,?,?,?,?,?,?,?)");
            String sqlSelect = SelectstrBuff.toString();
            String sqlUpdate = UpdatestrBuff.toString();
            String sqlInsert = InserstrBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("debitNetworkLoyaltyStock Method.....sqlSelect ", "" + sqlSelect);
            }
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, loyaltyVO.getNetworkCode());
            pstmt.setString(2, loyaltyVO.getNetworkCode());
            pstmt.setString(3, "ETOPUP");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                p_loyaltypoint = rs.getLong("PREVIOUS_LOYALTY_STOCK");
                c_loyaltypoint = rs.getLong("LOYALTY_STOCK");
            }
            if (c_loyaltypoint >= totaldebitPoint) {
                updatedloyaltypoint = c_loyaltypoint - totaldebitPoint;
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOYALTY_NETWORK_STOCK_NOT_OK);
            }
            p_loyaltypoint = c_loyaltypoint;
            if (_log.isDebugEnabled()) {
                _log.debug("debitNetworkLoyaltyStock Method.....sqlUpdate ", "" + sqlUpdate + "previous loyaltypoint " + p_loyaltypoint + "CurrentLoyaltypoint " + updatedloyaltypoint);
            }

            pstmt1 = con.prepareStatement(sqlUpdate);
            pstmt1.setLong(1, updatedloyaltypoint);
            pstmt1.setLong(2, p_loyaltypoint);
            pstmt1.setString(3, loyaltyVO.getNetworkCode());
            pstmt1.setString(4, loyaltyVO.getNetworkCode());
            pstmt1.setString(5, "ETOPUP");
            count = pstmt1.executeUpdate();
            if (count < 1) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOYALTY_NETWORK_STOCK_NOT_OK);
            } else {
                count = 0;
                psmtInsert = con.prepareStatement(sqlInsert);
                psmtInsert.setString(1, loyaltyVO.getLmstxnid());
                psmtInsert.setString(2, loyaltyVO.getNetworkCode());
                psmtInsert.setLong(3, p_loyaltypoint - totaldebitPoint);
                psmtInsert.setLong(4, p_loyaltypoint);
                psmtInsert.setString(5, loyaltyVO.getServiceType());
                psmtInsert.setLong(6, totaldebitPoint);
                psmtInsert.setString(7, "SUCCESS");
                psmtInsert.setLong(8, totaldebitPoint);
                psmtInsert.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(new Date()));
                psmtInsert.setString(10, PretupsI.SYSTEM);
                insertCount = psmtInsert.executeUpdate();
                System.out.println("");
                if (insertCount == 0) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOYALTY_PROCESSING_FAILED);
                } else {
                    count = 1;
                }
            }
        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOYALTY_NETWORK_STOCK_NOT_OK);
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
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtInsert != null) {
                	psmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with Count :" + count);
            }
        }
        return count;
    }

}
