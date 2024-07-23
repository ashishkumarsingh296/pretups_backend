package com.txn.pretups.channel.profile.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileServicesVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.OtfProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

public class CommissionProfileTxnDAO {

    /**
     * Commons Logging instance.
     */
    private final Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method loadAdditionCommissionDetails.
     * 
     * @param p_con
     *            Connection
     * @param p_transferID
     *            String
     * @param p_commProfileSetID
     *            String
     * @param p_requestAmount
     *            long
     * @param p_applicableDate
     *            java.util.Date
     * @param p_serviceType
     *            String
     * @return AdditionalProfileDeatilsVO
     * @throws BTSLBaseException
     */
    public AdditionalProfileDeatilsVO loadAdditionCommissionDetails(Connection p_con, String p_transferID, String p_commProfileSetID, long p_requestAmount, java.util.Date p_applicableDate, String p_serviceType, String p_subService, String p_gatewayCode) throws BTSLBaseException {
        final String methodName = "loadAdditionCommissionDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_transferID=" + p_transferID + " p_commProfileSetID=" + p_commProfileSetID + " p_requestAmount=" + p_requestAmount + " p_applicableDate=" + p_applicableDate + " p_serviceType=" + p_serviceType + "p_subService=" + p_subService + "p_gatewayCode=" + p_gatewayCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        AdditionalProfileDeatilsVO additionalProfileDetailsVO = null;
        String latestCommProfileSetVersion = null;
        Boolean isOwnerCommissionAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED);
        try {

            // latestCommProfileSetVersion=loadCommProfileSetLatestVersion(p_con,p_commProfileSetID,p_applicableDate);
            AdditionalProfileServicesVO additionalProfileServicesVO = loadCommProfileServiceIDDetails(p_con, p_commProfileSetID, p_applicableDate, p_serviceType, p_subService, p_gatewayCode);
            if (additionalProfileServicesVO != null) {
                // Commented till now As Discussed By Sanjay Kumar
                /*
                 * if(additionalProfileServicesVO.getMaxTransferValue()>
                 * p_requestAmount ||
                 * additionalProfileServicesVO.getMinTransferValue
                 * ()<p_requestAmount)
                 * throw new BTSLBaseException("commissionProfileTxnDAO",
                 * "loadAdditionCommissionDetails"
                 * ,PretupsErrorCodesI.DIFF_ERROR_AMOUNT_NOTINRANGE);
                 */

                StringBuffer selectQueryBuff = new StringBuffer(" SELECT addnl_comm_profile_detail_id,start_range, end_range, addnl_comm_type, addnl_comm_rate, ");
                selectQueryBuff.append(" tax1_type,tax1_rate, tax2_type, tax2_rate, diffrential_factor,roam_addnl_comm_type,roam_addnl_com_rate ");
				//added for owner commision
				if (isOwnerCommissionAllowed)  {
					selectQueryBuff.append(" ,own_tax1_type,own_tax1_rate, own_tax2_type, own_tax2_rate,own_addnl_comm_type, own_addnl_comm_rate ");
				}
				selectQueryBuff.append(" ,otf_applicable_from,otf_applicable_to,otf_type,otf_time_slab ");
                selectQueryBuff.append(" FROM addnl_comm_profile_details ");
                selectQueryBuff.append(" WHERE comm_profile_service_type_id=? AND (start_range<=? AND end_range>=?) AND status=? ");
                String selectQuery = selectQueryBuff.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "select query:" + selectQuery);
                }
                pstmtSelect = p_con.prepareStatement(selectQuery);
                pstmtSelect.setString(1, additionalProfileServicesVO.getCommProfileServiceTypeID());
                pstmtSelect.setLong(2, p_requestAmount);
                pstmtSelect.setLong(3, p_requestAmount);
                pstmtSelect.setString(4, PretupsI.YES);
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                    additionalProfileDetailsVO = new AdditionalProfileDeatilsVO();
                    additionalProfileDetailsVO.setAddCommProfileDetailID(rs.getString("addnl_comm_profile_detail_id"));
                    additionalProfileDetailsVO.setStartRange(rs.getLong("start_range"));
                    additionalProfileDetailsVO.setEndRange(rs.getLong("end_range"));
                    additionalProfileDetailsVO.setAddCommType(rs.getString("addnl_comm_type"));
                    additionalProfileDetailsVO.setAddCommRate(rs.getDouble("addnl_comm_rate"));
                    additionalProfileDetailsVO.setTax1Type(rs.getString("tax1_type"));
                    additionalProfileDetailsVO.setTax1Rate(rs.getDouble("tax1_rate"));
                    additionalProfileDetailsVO.setTax2Type(rs.getString("tax2_type"));
                    additionalProfileDetailsVO.setTax2Rate(rs.getDouble("tax2_rate"));
                    additionalProfileDetailsVO.setDiffrentialFactor(rs.getDouble("diffrential_factor"));
                    additionalProfileDetailsVO.setAddRoamCommType(rs.getString("roam_addnl_comm_type"));
                    additionalProfileDetailsVO.setAddRoamCommRate(rs.getDouble("roam_addnl_com_rate"));

					//added for owner commision
					if (isOwnerCommissionAllowed) 
					{
						additionalProfileDetailsVO.setAddOwnerCommType(rs.getString("own_addnl_comm_type"));
						additionalProfileDetailsVO.setAddOwnerCommRate(rs.getDouble("own_addnl_comm_rate"));
						additionalProfileDetailsVO.setOwnerTax1Type(rs.getString("own_tax1_type"));
						additionalProfileDetailsVO.setOwnerTax1Rate(rs.getDouble("own_tax1_rate"));
						additionalProfileDetailsVO.setOwnerTax2Type(rs.getString("own_tax2_type"));
						additionalProfileDetailsVO.setOwnerTax2Rate(rs.getDouble("own_tax2_rate"));
					}
					additionalProfileDetailsVO.setOtfApplicableFrom(rs.getDate("otf_applicable_from"));
					additionalProfileDetailsVO.setOtfApplicableTo(rs.getDate("otf_applicable_to"));
					additionalProfileDetailsVO.setOtfType(rs.getString("otf_type"));
					additionalProfileDetailsVO.setOtfTimeSlab(rs.getString("otf_time_slab"));
                    additionalProfileDetailsVO.setCommProfileServiceTypeID(additionalProfileServicesVO.getCommProfileServiceTypeID());
                } else {
                    if (_log.isDebugEnabled()) {
                        _log.debug("CommissionProfileTxnDAO", methodName, "Differential Not applicable for this range for transfer ID=" + p_transferID);
                    }
                }
            } else {
                if (_log.isDebugEnabled()) {
                    _log.debug("CommissionProfileTxnDAO", methodName, "No differential slabs defined for Comm Profile Set " + p_commProfileSetID + " and version=" + latestCommProfileSetVersion + " for transfer ID=" + p_transferID);
                }
            }
            return additionalProfileDetailsVO;
        }// end of try
        catch (BTSLBaseException bex) {
            _log.error(methodName, "BTSLBaseException " + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadAdditionCommissionDetails]", p_transferID, "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CommissionProfileTxnDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCardGroupDetails", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadAdditionCommissionDetails]", p_transferID, "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CommissionProfileTxnDAO", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
                _log.debug(methodName, "Exiting additionalProfileDetailsVO:" + additionalProfileDetailsVO);
            }
        }// end of finally
    }

    /**
     * Method loadCommProfileServiceIDDetails.
     * 
     * @param p_con
     *            Connection
     * @param p_commissionSetID
     *            String
     * @param p_version
     *            String
     * @param p_serviceType
     *            String
     * @return AdditionalProfileServicesVO
     * @throws BTSLBaseException
     */
    public AdditionalProfileServicesVO loadCommProfileServiceIDDetails(Connection p_con, String p_commissionSetID, Date p_applicableDate, String p_serviceType, String p_subService, String p_gatewayCode) throws BTSLBaseException {
        final String methodName = "loadCommProfileServiceIDDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_commissionSetID=" + p_commissionSetID + " p_applicableDate=" + p_applicableDate + " p_serviceType=" + p_serviceType + " p_subService=" + p_subService + "p_gatewayCode=" + p_gatewayCode);
        }
        String srvcProdMappingAllowed = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean flag = false;
        int i = 1;
        Date p_currentDate = new Date();
        AdditionalProfileServicesVO additionalProfileServicesVO = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer("SELECT cpsv.comm_profile_set_version ,cpst.comm_profile_service_type_id,cpst.min_transfer_value,");
            selectQueryBuff.append(" cpst.max_transfer_value ,cpst.service_type,cpst.sub_service ,gateway_code ,APPLICABLE_TIME_RANGE ");
            selectQueryBuff.append(" FROM commission_profile_set_version cpsv,comm_profile_service_types cpst ");
            selectQueryBuff.append(" WHERE cpsv.comm_profile_set_id=cpst.comm_profile_set_id AND cpsv.comm_profile_set_id=? ");
            selectQueryBuff.append(" and cpst.APPLICABLE_FROM <= ? AND cpst.APPLICABLE_TO >= ? ");
            selectQueryBuff.append(" AND cpsv.applicable_from =(SELECT MAX(applicable_from) FROM commission_profile_set_version  icpst  WHERE applicable_from<= ? AND comm_profile_set_id=cpsv.comm_profile_set_id) ");
            selectQueryBuff.append(" AND service_type=? AND cpsv.COMM_PROFILE_SET_VERSION=cpst.COMM_PROFILE_SET_VERSION  and (gateway_code=? or gateway_code=?) ");
            if (srvcProdMappingAllowed.contains(p_serviceType)) {
                selectQueryBuff.append(" AND cpst.sub_service=?");
            }
            selectQueryBuff.append(" order by gateway_code desc ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(i++, p_commissionSetID);
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_applicableDate));
            pstmtSelect.setString(i++, p_serviceType);
            if (srvcProdMappingAllowed.contains(p_serviceType)) {
                pstmtSelect.setString(i++, p_subService);
            }
            pstmtSelect.setString(i++, p_gatewayCode);
            pstmtSelect.setString(i, PretupsI.ALL);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                additionalProfileServicesVO = AdditionalProfileServicesVO.getInstance();
                additionalProfileServicesVO.setCommProfileServiceTypeID(rs.getString("comm_profile_service_type_id"));
                additionalProfileServicesVO.setMinTransferValue(rs.getLong("min_transfer_value"));
                additionalProfileServicesVO.setMaxTransferValue(rs.getLong("max_transfer_value"));
                if (srvcProdMappingAllowed.contains(p_serviceType)) {
                    additionalProfileServicesVO.setSubServiceCode(rs.getString("sub_service"));
                }
                additionalProfileServicesVO.setGatewayCode(rs.getString("gateway_code"));
                additionalProfileServicesVO.setAdditionalCommissionTimeSlab(rs.getString("APPLICABLE_TIME_RANGE"));
                if ((additionalProfileServicesVO.getGatewayCode().equals(p_gatewayCode) || PretupsI.ALL.equals(additionalProfileServicesVO.getGatewayCode())) && BTSLUtil.timeRangeValidation(additionalProfileServicesVO.getAdditionalCommissionTimeSlab(), p_currentDate)) {
                    flag = true;
                    break;
                }
                // additionalProfileServicesVOList.add(additionalProfileServicesVO);
            }
            if (!flag) {
                additionalProfileServicesVO = null;
            }

            return additionalProfileServicesVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadCommProfileServiceIDDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCommProfileSetLatestVersion", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadCommProfileServiceIDDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
                _log.debug(methodName, "Exiting additionalProfileServicesVO:" + additionalProfileServicesVO);
            }
        }// end of finally
    }
    
    public String calculateTotalIncome(Connection p_con, Date fromDate, Date toDate, String p_userId) throws BTSLBaseException
    {
    	final String methodName = "calculateTotalIncome";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_fromDate=" + fromDate + " p_toDate=" + toDate +" p_userId=" + p_userId);
        }
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        String totalIncomeStr ="";
        int totalIncome = 0;
        int c2sIncome = 0;
        int c2cIncome = 0;
        int i = 1;
        
    	try 
    	{
    		StringBuffer selectQueryBuff = new StringBuffer(" SELECT sum(margin_amount) AS total FROM ADJUSTMENTS WHERE USER_ID = ? ");
    		selectQueryBuff.append("AND CREATED_ON BETWEEN ? AND ?");
    		
    		
    		
    		pstmtSelect = p_con.prepareStatement(selectQueryBuff.toString());
    		
    		pstmtSelect.setString(i++, p_userId);
    		pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(fromDate));
    		pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(toDate));
    		
    		Date tomorrow = DateUtils.addDays(toDate, 1); 
    		
    		 rs = pstmtSelect.executeQuery();
             if (rs.next()) 
             {
            	 c2sIncome = rs.getInt("total");
             }
             
             selectQueryBuff.delete(0, selectQueryBuff.length());
             i = 1;
             
             selectQueryBuff.append(" SELECT sum(cti.commission_value) AS total FROM CHANNEL_TRANSFERS_ITEMS cti, CHANNEL_TRANSFERS ct WHERE ct.TO_USER_ID = ? ");
     		 selectQueryBuff.append("AND cti.TRANSFER_ID = ct.TRANSFER_ID AND cti.transfer_date BETWEEN ? AND ? ");
     		 
     		 pstmtSelect1 = p_con.prepareStatement(selectQueryBuff.toString());

     		
     		 pstmtSelect1.setString(i++, p_userId);
     		 pstmtSelect1.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(fromDate));
     		 pstmtSelect1.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(tomorrow));
     		
    		
     		 rs1 = pstmtSelect1.executeQuery();
     		 if (rs1.next()) 
     		 {
     			 c2cIncome = rs1.getInt("total");
     		 }
     		 totalIncome = c2cIncome + c2sIncome;
     		 
     	 totalIncomeStr =	PretupsBL.getDisplayAmount(Long.valueOf(totalIncome));
     	
     
    	}
    	catch(SQLException sqle)
    	{
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[calculateTotalIncome]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "calculateTotalIncome", PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        
    	}
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
        try {    
            if (rs1 != null) {
                rs1.close();
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelect1 != null) {
                pstmtSelect1.close();
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting with total:" + totalIncome);
            }
        }
        
    	return totalIncomeStr;
    }

    /**
     * Method loadCommProfileSetLatestVersion.
     * 
     * @param p_con
     *            Connection
     * @param p_commissionSetID
     *            String
     * @param p_applicableDate
     *            Date
     * @return String
     * @throws BTSLBaseException
     */
    public String loadCommProfileSetLatestVersion(Connection p_con, String p_commissionSetID, Date p_applicableDate) throws BTSLBaseException {
        final String methodName = "loadCommProfileSetLatestVersion";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_commissionSetID=" + p_commissionSetID + " p_applicableDate=" + p_applicableDate);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String latestCommProfileVersion = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(" SELECT comm_profile_set_version ");
            selectQueryBuff.append(" FROM commission_profile_set_version cgd ");
            selectQueryBuff.append(" WHERE cgd.comm_profile_set_id=? ");
            selectQueryBuff.append(" AND applicable_from =(SELECT MAX(applicable_from) ");
            selectQueryBuff.append(" FROM commission_profile_set_version ");
            selectQueryBuff.append(" WHERE applicable_from<=? AND comm_profile_set_id=cgd.comm_profile_set_id) ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_commissionSetID);
            pstmtSelect.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_applicableDate));
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                latestCommProfileVersion = rs.getString("comm_profile_set_version");
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED);
            }
            return latestCommProfileVersion;
        }// end of try
        catch (BTSLBaseException bex) {
            _log.error(methodName, "BTSLBaseException " + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadCommProfileSetLatestVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadCardGroupSetVersionLatestVersion", PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadCommProfileSetLatestVersion]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
                _log.debug(methodName, "Exiting latestCommProfileVersion:" + latestCommProfileVersion);
            }
        }// end of finally
    }

    /**
     * commission profile is associated or not
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @param p_status
     *            string
     * 
     * @return boolean
     * @exception BTSLBaseException
     * @author nilesh.kumar
     */
    public boolean isCommissionProfileIDAssociatedForAutoC2C(Connection p_con, String p_userId, String p_status) throws BTSLBaseException {
        final String methodName = "isCommissionProfileIDAssociatedForAutoC2C";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_userId=" + p_userId + " p_status=" + p_status);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append("SELECT cps.comm_profile_set_id,cv.comm_profile_set_version FROM commission_profile_set cps, channel_users cs , users u, commission_profile_set_version cv");
        strBuff.append(" WHERE u.user_id=? AND cps.comm_profile_set_id = cs.comm_profile_set_id AND cps.comm_profile_set_id=cv.comm_profile_set_id ");
        strBuff.append(" AND cs.user_id = u.user_id AND u.status not in (" + p_status + ")");
        strBuff.append(" AND cps.status not in ('N','C','S')");

        String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try {
            // commented for DB2 pstmt =
            // (OraclePreparedStatement)p_con.prepareStatement(sqlSelect);
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[isCommissionProfileIDAssociatedForAutoC2C]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "isCommissionProfileIDAssociated", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[isCommissionProfileIDAssociatedForAutoC2C]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * @param p_con
     * @param p_networkCode
     * @param p_categoryCode
     * @return
     * @throws BTSLBaseException
     * @author vikas.kumar
     */
    public CommissionProfileSetVO loadDefaultCommissionProfileSetForCategory(Connection p_con, String p_networkCode, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadDefaultCommissionProfileSetForCategory";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_networkCode=" + p_networkCode + " p_categoryCode=" + p_categoryCode);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        CommissionProfileSetVO commissionProfileSetVO = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT comm_profile_set_id,comm_profile_set_name,category_code,network_code,");
        strBuff.append(" comm_last_version,created_on,created_by, modified_on,modified_by, ");
        strBuff.append(" short_code,status,language_1_message,language_2_message,IS_DEFAULT FROM commission_profile_set");
        strBuff.append(" WHERE network_code = ? ");
        strBuff.append(" AND category_code = ? and status <> 'N' and IS_DEFAULT= ? ");
        strBuff.append(" ORDER BY comm_profile_set_name");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_categoryCode);
            pstmtSelect.setString(3, PretupsI.YES);
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                commissionProfileSetVO = CommissionProfileSetVO.getInstance();
                commissionProfileSetVO.setCommProfileSetId(rs.getString("comm_profile_set_id"));
                commissionProfileSetVO.setCommProfileSetName(rs.getString("comm_profile_set_name"));
                commissionProfileSetVO.setCategoryCode(rs.getString("category_code"));
                commissionProfileSetVO.setNetworkCode(rs.getString("network_code"));
                commissionProfileSetVO.setCommLastVersion(rs.getString("comm_last_version"));
                commissionProfileSetVO.setCreatedBy(rs.getString("created_by"));
                commissionProfileSetVO.setModifiedBy(rs.getString("modified_by"));
                commissionProfileSetVO.setCreatedOn(rs.getDate("created_on"));
                commissionProfileSetVO.setModifiedOn(rs.getDate("modified_on"));
                commissionProfileSetVO.setLastModifiedOn((rs.getTimestamp("modified_on").getTime()));
                commissionProfileSetVO.setShortCode(rs.getString("short_code"));
                commissionProfileSetVO.setStatus(rs.getString("status"));
                commissionProfileSetVO.setLanguage1Message(rs.getString("language_1_message"));
                commissionProfileSetVO.setLanguage2Message(rs.getString("language_2_message"));
                commissionProfileSetVO.setDefaultProfile(rs.getString("IS_DEFAULT"));

            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadDefaultCommissionProfileSetForCategory]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadDefaultCommissionProfileSetForCategory]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
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
                _log.debug(methodName, "Exiting: commissionProfileSetVO size=" + commissionProfileSetVO.toString());
            }
        }
        return commissionProfileSetVO;
    }

    /**
     * Added for retailer app to get the service_type_ID
     * Method loadCommProfileServiceIDDetailsForCalculator.
     * 
     * @param p_con
     *            Connection
     * @param p_commissionSetID
     *            String
     * @param p_version
     *            String
     * @param p_serviceType
     *            String
     * @return AdditionalProfileServicesVO
     * @throws BTSLBaseException
     */
    public AdditionalProfileServicesVO loadCommProfileServiceIDDetailsForCalculator(Connection p_con, String p_commissionSetID, Date p_applicableDate, String p_serviceType, String p_subService, String p_gatewayCode) throws BTSLBaseException {
        final String methodName = "loadCommProfileServiceIDDetailsForCalculator";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_commissionSetID=" + p_commissionSetID + " p_applicableDate=" + p_applicableDate + " p_serviceType=" + p_serviceType + " p_subService=" + p_subService + "p_gatewayCode=" + p_gatewayCode);
        }
        String srvcProdMappingAllowed = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean flag = false;
        int i = 1;
        Date p_currentDate = new Date();
        AdditionalProfileServicesVO additionalProfileServicesVO = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer("SELECT cpsv.comm_profile_set_version ,cpst.comm_profile_service_type_id,cpst.min_transfer_value,");
            selectQueryBuff.append(" cpst.max_transfer_value ,cpst.service_type,cpst.sub_service ,gateway_code ,APPLICABLE_TIME_RANGE ");
            selectQueryBuff.append(" FROM commission_profile_set_version cpsv,comm_profile_service_types cpst ");
            selectQueryBuff.append(" WHERE cpsv.comm_profile_set_id=cpst.comm_profile_set_id AND cpsv.comm_profile_set_id=? ");
            selectQueryBuff.append(" and cpst.APPLICABLE_FROM <= ? AND cpst.APPLICABLE_TO >= ? ");
            selectQueryBuff.append(" AND cpsv.applicable_from =(SELECT MAX(applicable_from) FROM commission_profile_set_version  icpst  WHERE applicable_from<= ? AND comm_profile_set_id=cpsv.comm_profile_set_id) ");
            selectQueryBuff.append(" AND service_type=? AND cpsv.COMM_PROFILE_SET_VERSION=cpst.COMM_PROFILE_SET_VERSION  and (gateway_code=? or gateway_code=?) ");
            if (srvcProdMappingAllowed.contains(p_serviceType)) {
                selectQueryBuff.append(" AND cpst.sub_service=?");
            }
            selectQueryBuff.append(" order by gateway_code desc ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(i++, p_commissionSetID);
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_applicableDate));
            pstmtSelect.setString(i++, p_serviceType);
            if (srvcProdMappingAllowed.contains(p_serviceType)) {
                pstmtSelect.setString(i++, p_subService);
            }
            pstmtSelect.setString(i++, p_gatewayCode);
            pstmtSelect.setString(i, PretupsI.ALL);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                additionalProfileServicesVO = AdditionalProfileServicesVO.getInstance();
                additionalProfileServicesVO.setCommProfileServiceTypeID(rs.getString("comm_profile_service_type_id"));
                additionalProfileServicesVO.setMinTransferValue(rs.getLong("min_transfer_value"));
                additionalProfileServicesVO.setMaxTransferValue(rs.getLong("max_transfer_value"));
                if (srvcProdMappingAllowed.contains(p_serviceType)) {
                    additionalProfileServicesVO.setSubServiceCode(rs.getString("sub_service"));
                }
                additionalProfileServicesVO.setGatewayCode(rs.getString("gateway_code"));
                additionalProfileServicesVO.setAdditionalCommissionTimeSlab(rs.getString("APPLICABLE_TIME_RANGE"));
                /*
                 * if((additionalProfileServicesVO.getGatewayCode().equals(
                 * p_gatewayCode) ||
                 * PretupsI.ALL.equals(additionalProfileServicesVO
                 * .getGatewayCode())) &&
                 * BTSLUtil.timeRangeValidation(additionalProfileServicesVO
                 * .getAdditionalCommissionTimeSlab(),p_currentDate)){
                 * flag = true;
                 * break;
                 * }
                 */

                if ((additionalProfileServicesVO.getGatewayCode().equals(p_gatewayCode)) || PretupsI.ALL.equals(additionalProfileServicesVO.getGatewayCode())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                additionalProfileServicesVO = null;
            }

            return additionalProfileServicesVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadCommProfileServiceIDDetailsForCalculator]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCommProfileSetLatestVersion", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadCommProfileServiceIDDetailsForCalculator]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
                _log.debug(methodName, "Exiting additionalProfileServicesVO:" + additionalProfileServicesVO);
            }
        }// end of finally
    }

    /**
     * Added for retailer app to load additional commission details
     * Method loadAdditionCommissionDetailsForCalculator.
     * 
     * @param p_con
     *            Connection
     * @param p_transferID
     *            String
     * @param p_commProfileSetID
     *            String
     * @param p_requestAmount
     *            long
     * @param p_applicableDate
     *            java.util.Date
     * @param p_serviceType
     *            String
     * @return AdditionalProfileDeatilsVO
     * @throws BTSLBaseException
     */
    public AdditionalProfileDeatilsVO loadAdditionCommissionDetailsForCalculator(Connection p_con, String p_transferID, String p_commProfileSetID, long p_requestAmount, java.util.Date p_applicableDate, String p_serviceType, String p_subService, String p_gatewayCode) throws BTSLBaseException {
        final String methodName = "loadAdditionCommissionDetailsForCalculator";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_transferID=" + p_transferID + " p_commProfileSetID=" + p_commProfileSetID + " p_requestAmount=" + p_requestAmount + " p_applicableDate=" + p_applicableDate + " p_serviceType=" + p_serviceType + "p_subService=" + p_subService + "p_gatewayCode=" + p_gatewayCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        AdditionalProfileDeatilsVO additionalProfileDetailsVO = null;
        String latestCommProfileSetVersion = null;
        try {

            // latestCommProfileSetVersion=loadCommProfileSetLatestVersion(p_con,p_commProfileSetID,p_applicableDate);
            AdditionalProfileServicesVO additionalProfileServicesVO = loadCommProfileServiceIDDetailsForCalculator(p_con, p_commProfileSetID, p_applicableDate, p_serviceType, p_subService, p_gatewayCode);
            if (additionalProfileServicesVO != null) {
                // Commented till now As Discussed By Sanjay Kumar
                /*
                 * if(additionalProfileServicesVO.getMaxTransferValue()>
                 * p_requestAmount ||
                 * additionalProfileServicesVO.getMinTransferValue
                 * ()<p_requestAmount)
                 * throw new BTSLBaseException("commissionProfileTxnDAO",
                 * "loadAdditionCommissionDetails"
                 * ,PretupsErrorCodesI.DIFF_ERROR_AMOUNT_NOTINRANGE);
                 */

                StringBuffer selectQueryBuff = new StringBuffer(" SELECT addnl_comm_profile_detail_id,start_range, end_range, addnl_comm_type, addnl_comm_rate, ");
                selectQueryBuff.append(" tax1_type,tax1_rate, tax2_type, tax2_rate, diffrential_factor,roam_addnl_comm_type,roam_addnl_com_rate ");
                selectQueryBuff.append(" FROM addnl_comm_profile_details ");
                selectQueryBuff.append(" WHERE comm_profile_service_type_id=? AND (start_range<=? AND end_range>=?) AND status=? ");
                String selectQuery = selectQueryBuff.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "select query:" + selectQuery);
                }
                pstmtSelect = p_con.prepareStatement(selectQuery);
                pstmtSelect.setString(1, additionalProfileServicesVO.getCommProfileServiceTypeID());
                pstmtSelect.setLong(2, PretupsBL.getSystemAmount(p_requestAmount));
                pstmtSelect.setLong(3, PretupsBL.getSystemAmount(p_requestAmount));
                // pstmtSelect.setLong(2,p_requestAmount);
                // pstmtSelect.setLong(3,p_requestAmount);
                pstmtSelect.setString(4, PretupsI.YES);
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                    additionalProfileDetailsVO = new AdditionalProfileDeatilsVO();
                    additionalProfileDetailsVO.setAddCommProfileDetailID(rs.getString("addnl_comm_profile_detail_id"));
                    additionalProfileDetailsVO.setStartRange(rs.getLong("start_range"));
                    additionalProfileDetailsVO.setEndRange(rs.getLong("end_range"));
                    additionalProfileDetailsVO.setAddCommType(rs.getString("addnl_comm_type"));
                    additionalProfileDetailsVO.setAddCommRate(rs.getDouble("addnl_comm_rate"));
                    additionalProfileDetailsVO.setTax1Type(rs.getString("tax1_type"));
                    additionalProfileDetailsVO.setTax1Rate(rs.getDouble("tax1_rate"));
                    additionalProfileDetailsVO.setTax2Type(rs.getString("tax2_type"));
                    additionalProfileDetailsVO.setTax2Rate(rs.getDouble("tax2_rate"));
                    additionalProfileDetailsVO.setDiffrentialFactor(rs.getDouble("diffrential_factor"));
                    additionalProfileDetailsVO.setAddRoamCommType(rs.getString("roam_addnl_comm_type"));
                    additionalProfileDetailsVO.setAddRoamCommRate(rs.getDouble("roam_addnl_com_rate"));

                    additionalProfileDetailsVO.setCommProfileServiceTypeID(additionalProfileServicesVO.getCommProfileServiceTypeID());
                } else {
                    if (_log.isDebugEnabled()) {
                        _log.debug("CommissionProfileTxnDAO", methodName, "Differential Not applicable for this range for transfer ID=" + p_transferID);
                    }
                }
            } else {
                if (_log.isDebugEnabled()) {
                    _log.debug("CommissionProfileTxnDAO", methodName, "No differential slabs defined for Comm Profile Set " + p_commProfileSetID + " and version=" + latestCommProfileSetVersion + " for transfer ID=" + p_transferID);
                }
            }
            return additionalProfileDetailsVO;
        }// end of try
        catch (BTSLBaseException bex) {
            _log.error(methodName, "BTSLBaseException " + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadAdditionCommissionDetailsForCalculator]", p_transferID, "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CommissionProfileTxnDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCardGroupDetails", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadAdditionCommissionDetailsForCalculator]", p_transferID, "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CommissionProfileTxnDAO", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
                _log.debug(methodName, "Exiting additionalProfileDetailsVO:" + additionalProfileDetailsVO);
            }
        }// end of finally
    }

    
    /**
     * Added for retailer app to load additional commission details
     * Method loadAdditionCommissionDetailsForCalculator.
     * 
     * @param p_con
     *            Connection
     * @param p_transferID
     *            String
     * @param p_commProfileSetID
     *            String
     * @param p_requestAmount
     *            long
     * @param p_applicableDate
     *            java.util.Date
     * @param p_serviceType
     *            String
     * @return AdditionalProfileDeatilsVO
     * @throws BTSLBaseException
     */
    public CommissionProfileDeatilsVO loadCommissionProfileDetails(Connection con, String commProfileProductID) throws BTSLBaseException {
        final String methodName = "loadCommissionProfileDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered commProfileProductID=" + commProfileProductID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        CommissionProfileDeatilsVO commissionProfileDeatilsVO = null;
        String latestCommProfileSetVersion = null;
        try {

            final StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
            strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
            strBuffSelectCProfileProdDetail.append("FROM commission_profile_details cpd ");
            strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ? ");
                String selectQuery = strBuffSelectCProfileProdDetail.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "select query:" + selectQuery);
                }
                pstmtSelect = con.prepareStatement(selectQuery);
                pstmtSelect.setString(1, commProfileProductID);
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                	commissionProfileDeatilsVO = new CommissionProfileDeatilsVO();
                	commissionProfileDeatilsVO.setCommProfileDetailID(rs.getString("comm_profile_detail_id"));
                   

                
            } 
            return commissionProfileDeatilsVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadCommissionProfileDetails]", commProfileProductID, "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CommissionProfileTxnDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCardGroupDetails", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadCommissionProfileDetails]", commProfileProductID, "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CommissionProfileTxnDAO", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
                _log.debug(methodName, "Exiting loadCommissionProfileDetails:" + commissionProfileDeatilsVO);
            }
        }// end of finally
    }
    
    public CommissionProfileDeatilsVO loadCommissionProfileDetailsForOTF(Connection con, String commProfileDetailID) throws BTSLBaseException {
        final String methodName = "loadCommissionProfileDetailsForOTF";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered commProfileDetailID=" + commProfileDetailID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        CommissionProfileDeatilsVO commissionProfileDeatilsVO = null;
        String latestCommProfileSetVersion = null;
        try {

            final StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
            strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id,cpd.otf_applicable_from,");
            strBuffSelectCProfileProdDetail.append("cpd.otf_applicable_to,cpd.otf_time_slab,cpd.comm_profile_detail_id FROM commission_profile_details cpd ");
            strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_detail_id = ? ");
                String selectQuery = strBuffSelectCProfileProdDetail.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "select query:" + selectQuery);
                }
                pstmtSelect = con.prepareStatement(selectQuery);
                pstmtSelect.setString(1, commProfileDetailID);
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                	commissionProfileDeatilsVO = new CommissionProfileDeatilsVO();
                	commissionProfileDeatilsVO.setOtfApplicableFrom(rs.getDate("otf_applicable_from"));
                	commissionProfileDeatilsVO.setOtfApplicableTo(rs.getDate("otf_applicable_to"));
                	commissionProfileDeatilsVO.setOtfTimeSlab(rs.getString("otf_time_slab"));
                	commissionProfileDeatilsVO.setBaseCommProfileDetailID(rs.getString("comm_profile_detail_id"));
                	

            } 
            return commissionProfileDeatilsVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadCommissionProfileDetails]", commProfileDetailID, "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CommissionProfileTxnDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCardGroupDetails", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadCommissionProfileDetails]", commProfileDetailID, "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CommissionProfileTxnDAO", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
                _log.debug(methodName, "Exiting loadCommissionProfileDetails:" + commissionProfileDeatilsVO);
            }
        }// end of finally
    }
    
    public OtfProfileVO loadOtfProfileDetails(Connection con, String commProfileSetID, String commProfileSetVerID, String productCode) throws BTSLBaseException {
        final String methodName = "loadOtfProfileDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered commProfileDetailID=" + commProfileSetID + ",commProfileSetVerID=" + commProfileSetVerID+ ",productCode=" + productCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        OtfProfileVO otfProfileVO = null;
        try {

            final StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder("SELECT cpo.comm_profile_otf_id, ");
            strBuffSelectCProfileProdDetail.append(" cpo.otf_applicable_from, cpo.otf_applicable_to, cpo.otf_time_slab  FROM commission_profile_otf cpo ");
            strBuffSelectCProfileProdDetail.append(" WHERE cpo.comm_profile_set_id = ? and cpo.comm_profile_set_version = ? and cpo.product_code = ?");
            String selectQuery = strBuffSelectCProfileProdDetail.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, commProfileSetID);
            pstmtSelect.setString(2, commProfileSetVerID);
            pstmtSelect.setString(3, productCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
            	otfProfileVO = new OtfProfileVO();
            	otfProfileVO.setCommProfileOtfID(rs.getString("comm_profile_otf_id"));
            	otfProfileVO.setCommProfileSetID(commProfileSetID);
            	otfProfileVO.setCommProfileSetVersion(commProfileSetVerID);
            	otfProfileVO.setProductCode(productCode);
            	if (rs.getDate("otf_applicable_from") != null) {
            		otfProfileVO.setOtfApplicableFrom(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("otf_applicable_from"))));
                } else {
                	otfProfileVO.setOtfApplicableFrom("");
                }
            	if (rs.getDate("otf_applicable_to") != null) {
            		otfProfileVO.setOtfApplicableTo(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("otf_applicable_to"))));
                } else {
                	otfProfileVO.setOtfApplicableTo("");
                }
            	otfProfileVO.setOtfTimeSlab(rs.getString("otf_time_slab"));
            } 
            return otfProfileVO;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadOtfProfileDetails]", commProfileSetID, "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("CommissionProfileTxnDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error("loadCardGroupDetails", "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadOtfProfileDetails]", commProfileSetID, "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CommissionProfileTxnDAO", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
                _log.debug(methodName, "Exiting loadOtfProfileDetails:" + otfProfileVO);
            }
        }// end of finally
    }
    
    public CommissionProfileSetVO loadCommProfileSetDetails(Connection p_con, String p_commissionSetID, Date p_applicableDate) throws BTSLBaseException {
        final String methodName = "loadCommProfileSetDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_commissionSetID=" + p_commissionSetID + " p_applicableDate=" + p_applicableDate);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        CommissionProfileSetVO  commissionProfileSetVO = new CommissionProfileSetVO();
        String latestCommProfileVersion = null;
        try {
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT comm_profile_set_version,dual_comm_type ");
            selectQueryBuff.append(" FROM commission_profile_set_version cgd ");
            selectQueryBuff.append(" WHERE cgd.comm_profile_set_id=? ");
            selectQueryBuff.append(" AND applicable_from =(SELECT MAX(applicable_from) ");
            selectQueryBuff.append(" FROM commission_profile_set_version ");
            selectQueryBuff.append(" WHERE applicable_from<=? AND comm_profile_set_id=cgd.comm_profile_set_id) ");
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_commissionSetID);
            pstmtSelect.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_applicableDate));
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
            	commissionProfileSetVO.setCommProfileVersion(rs.getString("comm_profile_set_version"));
            	commissionProfileSetVO.setDualCommissionType(rs.getString("dual_comm_type"));
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED);
            }
            return commissionProfileSetVO;
        }
        catch (BTSLBaseException bex) {
            _log.error(methodName, "BTSLBaseException " + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadCommProfileSetLatestVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadCommProfileSetLatestVersion]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
                _log.debug(methodName, "Exiting latestCommProfileVersion:" + latestCommProfileVersion);
            }
        }// end of finally
    }
    
    /**
     * this method returns status of commission profile
     * @param p_con
     * @param p_commissionSetID
     * @return
     * @throws BTSLBaseException
     */
    
    public String loadCommProfileStatusById(Connection p_con, String p_commissionSetID) throws BTSLBaseException {
        final String methodName = "loadCommProfileStatusById";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_commissionSetID=" + p_commissionSetID );
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String result="";
        CommissionProfileSetVO  commissionProfileSetVO = new CommissionProfileSetVO();
        String latestCommProfileVersion = null;
        try {
            StringBuilder selectQueryBuff = new StringBuilder("SELECT status FROM COMMISSION_PROFILE_SET WHERE COMM_PROFILE_SET_ID=?");
           
            String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_commissionSetID);
            
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
            	result=rs.getString("status");
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED);
            }
            return result;
        }
        catch (BTSLBaseException bex) {
            _log.error(methodName, "BTSLBaseException " + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadCommProfileStatusById]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileTxnDAO[loadCommProfileStatusById]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
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
                _log.debug(methodName, "Exiting loadCommProfileStatusById:" + result);
            }
        }// end of finally
    }
    
    
    
    

}
