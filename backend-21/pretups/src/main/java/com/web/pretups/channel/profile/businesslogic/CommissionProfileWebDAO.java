package com.web.pretups.channel.profile.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileServicesVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalcommSlabDetails;
import com.btsl.pretups.channel.profile.businesslogic.BatchModifyCommissionProfileVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVersionVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionSlabDetails;
import com.btsl.pretups.channel.profile.businesslogic.OTFDetailsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;
import com.restapi.networkadmin.commissionprofile.requestVO.ChangeStatusForCommissionProfileVO;
import com.web.pretups.channel.profile.web.CommissionProfileForm;

/**
 * @author akanksha
 *
 */
public class CommissionProfileWebDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method for checking Commission Profile Name is already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_profileName
     *            String
     * @param p_setId
     *            String
     * 
     * @return flag boolean
     * @throws BTSLBaseException
     */
    public boolean isCommissionProfileSetNameExist(Connection p_con, String p_networkCode, String p_profileName, String p_setId) throws BTSLBaseException {
        final String methodName = "isCommissionProfileSetNameExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_networkCode=" + p_networkCode + " p_profileName=" + p_profileName + " p_setId=" + p_setId);
        }


       
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();
        /*
         * In add mode setId is null but in edit mode setId is not null
         * beca we have tp apply the where claue
         */
        if (BTSLUtil.isNullString(p_setId)) {
            strBuff.append("SELECT comm_profile_set_name FROM commission_profile_set ");
            strBuff.append("WHERE network_code = ? AND upper(comm_profile_set_name) = upper(?)");
        } else {
            strBuff.append("SELECT comm_profile_set_name FROM commission_profile_set ");
            strBuff.append("WHERE network_code = ? AND upper(comm_profile_set_name) = upper(?) AND comm_profile_set_id != ?");
        }
        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            // commented for DB2pstmt =

            
            if (BTSLUtil.isNullString(p_setId)) {
                pstmt.setString(1, p_networkCode);
                // commented for DB2 pstmt.setFormOfUse(2,

                pstmt.setString(2, p_profileName);
            } else {
                pstmt.setString(1, p_networkCode);
                // commented for DB2pstmt.setFormOfUse(2,

                pstmt.setString(2, p_profileName);
                pstmt.setString(3, p_setId);
            }
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[isCommissionProfileSetNameExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[isCommissionProfileSetNameExist]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method for checking Commission Profile is already exist with the same
     * applicable date of the same set id.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_applicableDate
     *            Date
     * @param p_setId
     *            String
     * @param p_version
     *            String
     * 
     * @return flag boolean
     * @throws BTSLBaseException
     */
    public boolean isCommissionProfileAlreadyExist(Connection p_con, Date p_applicableDate, String p_setId, String p_version) throws BTSLBaseException {
        final String methodName = "isCommissionProfileAlreadyExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_applicableDate=" + p_applicableDate + " p_setId=" + p_setId + " p_version=" + p_version);
        }

        
       
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT comm_profile_set_id FROM commission_profile_set_version ");
        strBuff.append("WHERE applicable_from = ? AND comm_profile_set_id = ? AND comm_profile_set_version != ?");

        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            

            pstmt.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_applicableDate));
            pstmt.setString(2, p_setId);
            pstmt.setString(3, p_version);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[isCommissionProfileAlreadyExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[isCommissionProfileAlreadyExist]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method for loading Commission Profile Set Versions.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_categoryCode
     *            String
     * @param p_currentDate
     *            java.util.Date
     * 
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCommissionProfileSetVersion(Connection p_con, String p_networkCode, String p_categoryCode, Date p_currentDate) throws BTSLBaseException {

        final String methodName = "loadCommissionProfileSetVersion";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_networkCode=" + p_networkCode + " p_categoryCode=" + p_categoryCode + " currentDate=" + p_currentDate);
        }

   CommissionProfileWebQry commissionProfileWebQry = (CommissionProfileWebQry)ObjectProducer.getObject(QueryConstants.COMM_PROFILE_WEB_QRY, QueryConstants.QUERY_PRODUCER);
   Boolean isOthComChnl = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL);
        final String sqlSelect = commissionProfileWebQry.loadCommissionProfileSetVersionQry();

            if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        final ArrayList list = new ArrayList();

        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_categoryCode);
            pstmtSelect.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
            pstmtSelect.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            CommissionProfileSetVersionVO commissionProfileSetVersionVO = null;

            while (rs.next()) {
                commissionProfileSetVersionVO = new CommissionProfileSetVersionVO();
                commissionProfileSetVersionVO.setCommProfileSetId(rs.getString("comm_profile_set_id"));
                commissionProfileSetVersionVO.setCommProfileSetVersion(rs.getString("comm_profile_set_version"));
                commissionProfileSetVersionVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                commissionProfileSetVersionVO.setOldApplicableFrom(rs.getTimestamp("applicable_from").getTime());
                commissionProfileSetVersionVO.setDualCommissionType(rs.getString("dual_comm_type"));
              
                if(isOthComChnl){
				commissionProfileSetVersionVO.setOtherCommissionProfileSetID(rs.getString("oth_comm_prf_set_id"));
				commissionProfileSetVersionVO.setCommissionType(rs.getString("oth_comm_prf_type"));
				commissionProfileSetVersionVO.setCommissionTypeValue(rs.getString("oth_comm_prf_type_value"));
				commissionProfileSetVersionVO.setOtherCommissionName(rs.getString("OTH_COMM_PRF_SET_NAME"));
                }

                list.add(commissionProfileSetVersionVO);
            }
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommissionProfileSetVersion]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCommissionProfileSetVersion()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommissionProfileSetVersion]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCommissionProfileSetVersion()", "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: commissionProfileSetVersionList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * This method loads the Commission profile Details on the basis of
     * Comm_profile_product_id
     * 
     * @param p_con
     * @param p_commProfileProductId
     *            String
     * 
     * @return commissionprofileDetailList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCommissionProfileDetailList(Connection p_con, String p_commProfileProductId,String networkCode) throws BTSLBaseException {
        final String methodName = "loadCommissionProfileDetailList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_commProfileProductId=" + p_commProfileProductId);
        }
        
        final ArrayList commissionDetailList = new ArrayList();
        CommissionProfileDeatilsVO commissionProfileDeatilsVO = null;
        try {

        	final StringBuilder selectQueryBuff = new StringBuilder(" SELECT comm_profile_detail_id,comm_profile_products_id,");
        	selectQueryBuff.append(" start_range,end_range,commission_type,commission_rate,");
        	selectQueryBuff.append(" tax1_type, tax1_rate, tax2_type, tax2_rate,tax3_type,tax3_rate ");
        	selectQueryBuff.append(" FROM commission_profile_details WHERE comm_profile_products_id = ? ");

        	final String selectQuery = selectQueryBuff.toString();
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, "select query:" + selectQuery);
        	}
        	try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
        	{
        		pstmtSelect.setString(1, p_commProfileProductId);

        		try(ResultSet rs = pstmtSelect.executeQuery();)
        		{
        			while (rs.next()) {
        				commissionProfileDeatilsVO = new CommissionProfileDeatilsVO();
        				commissionProfileDeatilsVO.setCommProfileDetailID(rs.getString("comm_profile_detail_id"));
        				commissionProfileDeatilsVO.setCommProfileProductsID(rs.getString("comm_profile_products_id"));
        				commissionProfileDeatilsVO.setStartRange(rs.getLong("start_range"));
        				commissionProfileDeatilsVO.setStartRangeAsString(PretupsBL.getDisplayAmount(rs.getLong("start_range")));
        				commissionProfileDeatilsVO.setEndRange(rs.getLong("end_range"));
        				commissionProfileDeatilsVO.setEndRangeAsString(PretupsBL.getDisplayAmount(rs.getLong("end_range")));
        				commissionProfileDeatilsVO.setCommType(rs.getString("commission_type"));
        				commissionProfileDeatilsVO.setCommRate(rs.getDouble("commission_rate"));
        				if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(commissionProfileDeatilsVO.getCommType())) {
        					commissionProfileDeatilsVO.setCommRateAsString(PretupsBL.getDisplayAmount(rs.getLong("commission_rate")));
        				} else {
        					commissionProfileDeatilsVO.setCommRateAsString(String.valueOf(commissionProfileDeatilsVO.getCommRate()));
        				}
        				commissionProfileDeatilsVO.setTax1Type(rs.getString("tax1_type"));
        				commissionProfileDeatilsVO.setTax1Rate(rs.getDouble("tax1_rate"));
        				if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(commissionProfileDeatilsVO.getTax1Type())) {
        					commissionProfileDeatilsVO.setTax1RateAsString(PretupsBL.getDisplayAmount(rs.getLong("tax1_rate")));
        				} else {
        					commissionProfileDeatilsVO.setTax1RateAsString(String.valueOf(commissionProfileDeatilsVO.getTax1Rate()));
        				}
        				commissionProfileDeatilsVO.setTax2Type(rs.getString("tax2_type"));
        				commissionProfileDeatilsVO.setTax2Rate(rs.getDouble("tax2_rate"));
        				if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(commissionProfileDeatilsVO.getTax2Type())) {
        					commissionProfileDeatilsVO.setTax2RateAsString(PretupsBL.getDisplayAmount(rs.getLong("tax2_rate")));
        				} else {
        					commissionProfileDeatilsVO.setTax2RateAsString(String.valueOf(commissionProfileDeatilsVO.getTax2Rate()));
        				}
        				commissionProfileDeatilsVO.setTax3Type(rs.getString("tax3_type"));
        				commissionProfileDeatilsVO.setTax3Rate(rs.getDouble("tax3_rate"));
        				if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(commissionProfileDeatilsVO.getTax3Type())) {
        					commissionProfileDeatilsVO.setTax3RateAsString(PretupsBL.getDisplayAmount(rs.getLong("tax3_rate")));
        				} else {
        					commissionProfileDeatilsVO.setTax3RateAsString(String.valueOf(commissionProfileDeatilsVO.getTax3Rate()));
        				}

        				commissionDetailList.add(commissionProfileDeatilsVO);
        			}

        			return commissionDetailList;
        		}
        	}
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommissionProfileDetailList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("commissionProfileWebDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommissionProfileDetailList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("commissionProfileWebDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting commissionDetailList Size:" + commissionDetailList.size());
            }
        }// end of finally
    }

    /**
     * Method for loading Commission Profile Set Versions.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_commProifleSetId
     *            String
     * @param p_commProfileSetVersion
     *            String
     * 
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadAdditionalProfileServicesList(Connection p_con, String p_commProifleSetId, String p_commProfileSetVersion) throws BTSLBaseException {

        final String methodName = "loadAdditionalProfileServicesList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_commProifleSetId=" + p_commProifleSetId + " p_commProfileSetVersion=" + p_commProfileSetVersion);
        }

        

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT cpst.comm_profile_service_type_id,cpst.comm_profile_set_id,cpst.comm_profile_set_version,");
        strBuff.append(" cpst.min_transfer_value,cpst.max_transfer_value,cpst.service_type,st.name,cpst.sub_service,");
        strBuff.append(" cpst.GATEWAY_CODE,cpst.APPLICABLE_TIME_RANGE,cpst.APPLICABLE_FROM,cpst.APPLICABLE_TO , l.lookup_name AS status");
        strBuff.append(" FROM comm_profile_service_types cpst ,service_type st ,LOOKUPS l   WHERE cpst.service_type = st.service_type ");
        strBuff.append(" AND cpst.comm_profile_set_id = ? ");
        strBuff.append(" AND cpst.comm_profile_set_version = ?  AND st.STATUS =l.lookup_code AND l.LOOKUP_type ='LKTST' ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        final ArrayList list = new ArrayList();

        try (PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);){
            
            pstmtSelect.setString(1, p_commProifleSetId);
            pstmtSelect.setString(2, p_commProfileSetVersion);

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            AdditionalProfileServicesVO additionalProfileServicesVO = null;

            while (rs.next()) {
                additionalProfileServicesVO = new AdditionalProfileServicesVO();
                additionalProfileServicesVO.setCommProfileServiceTypeID(rs.getString("comm_profile_service_type_id"));
                additionalProfileServicesVO.setCommProfileSetID(rs.getString("comm_profile_set_id"));
                additionalProfileServicesVO.setCommProfileSetVersion(rs.getString("comm_profile_set_version"));
                additionalProfileServicesVO.setMinTransferValue(rs.getLong("min_transfer_value"));
                additionalProfileServicesVO.setMaxTransferValue(rs.getLong("max_transfer_value"));
                additionalProfileServicesVO.setServiceType(rs.getString("service_type"));
                additionalProfileServicesVO.setServiceTypeDesc(rs.getString("name"));
                additionalProfileServicesVO.setSubServiceCode(rs.getString("sub_service"));
                additionalProfileServicesVO.setGatewayCode(rs.getString("GATEWAY_CODE"));
                if (!BTSLUtil.isNullString(rs.getString("APPLICABLE_TIME_RANGE"))) {
                    additionalProfileServicesVO.setAdditionalCommissionTimeSlab(rs.getString("APPLICABLE_TIME_RANGE"));
                } else {
                    additionalProfileServicesVO.setAdditionalCommissionTimeSlab("");
                }
                if ((rs.getDate("APPLICABLE_FROM") != null)) {
                    additionalProfileServicesVO.setApplicableFromAdditional(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(((rs.getDate("APPLICABLE_FROM"))))));
                } else {
                    additionalProfileServicesVO.setApplicableFromAdditional("");
                }
                if (rs.getDate("APPLICABLE_TO") != null) {
                    additionalProfileServicesVO.setApplicableToAdditional(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(rs.getDate("APPLICABLE_TO"))));
                } else {
                    additionalProfileServicesVO.setApplicableToAdditional("");
                }
                additionalProfileServicesVO.setAddtnlComStatus(rs.getString("status"));
                list.add(additionalProfileServicesVO);
            }
        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadAdditionalProfileServicesList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadAdditionalProfileServicesList()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadAdditionalProfileServicesList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadAdditionalProfileServicesList()", "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: commissionProfileServiceList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * This method loads the Additional profile Details on the basis of
     * comm_profile_set_id
     * 
     * @param p_con
     * @param p_commProfileServiceTypeID
     *            String
     * 
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadAdditionalProfileDetailList(Connection p_con, String p_commProfileServiceTypeID,String networkCode) throws BTSLBaseException {
        final String methodName = "loadAdditionalProfileDetailList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_commProfileServiceTypeID=" + p_commProfileServiceTypeID);
        }
        Boolean isOwnerCommissionAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED);
        final ArrayList addCommissionDetailList = new ArrayList();
        AdditionalProfileDeatilsVO additionalProfileDeatilsVO = null;
        try {

            final StringBuilder selectQueryBuff = new StringBuilder(" SELECT adc.addnl_comm_profile_detail_id,");
            selectQueryBuff.append(" adc.comm_profile_service_type_id,adc.start_range,end_range,");
            selectQueryBuff.append(" adc.addnl_comm_type,adc.addnl_comm_rate,adc.diffrential_factor,");
            selectQueryBuff
                .append(" adc.tax1_type, adc.tax1_rate, adc.tax2_type, adc.tax2_rate, adc.status,adc.roam_addnl_comm_type,adc.roam_addnl_com_rate, lk.lookup_name status_name");
            if (isOwnerCommissionAllowed) {
				selectQueryBuff.append(" ,adc.own_addnl_comm_type ,adc.own_addnl_comm_rate,adc.own_tax1_type,adc.own_tax1_rate,adc.own_tax2_type,adc.own_tax2_rate");
			}   
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, networkCode)){
				selectQueryBuff.append(" ,adc.otf_type, adc.otf_applicable_from, adc.otf_applicable_to,adc.OTF_TIME_SLAB ");
			} 
            selectQueryBuff.append(" FROM addnl_comm_profile_details adc,lookups lk ");
            selectQueryBuff.append(" WHERE adc.comm_profile_service_type_id = ? AND adc.status=lk.lookup_code AND lk.lookup_type='URTYP' AND adc.status IN ('Y','S')");

            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_commProfileServiceTypeID);

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                additionalProfileDeatilsVO = new AdditionalProfileDeatilsVO();
                additionalProfileDeatilsVO.setAddCommProfileDetailID(rs.getString("addnl_comm_profile_detail_id"));
                additionalProfileDeatilsVO.setCommProfileServiceTypeID(rs.getString("comm_profile_service_type_id"));
                additionalProfileDeatilsVO.setStartRange(rs.getLong("start_range"));
                additionalProfileDeatilsVO.setStartRangeAsString(PretupsBL.getDisplayAmount(rs.getLong("start_range")));
                additionalProfileDeatilsVO.setEndRange(rs.getLong("end_range"));
                additionalProfileDeatilsVO.setEndRangeAsString(PretupsBL.getDisplayAmount(rs.getLong("end_range")));
                additionalProfileDeatilsVO.setAddCommType(rs.getString("addnl_comm_type"));
                additionalProfileDeatilsVO.setAddCommRate(rs.getDouble("addnl_comm_rate"));
                if(!BTSLUtil.isNullString(rs.getString("roam_addnl_comm_type"))){
                additionalProfileDeatilsVO.setAddRoamCommType(rs.getString("roam_addnl_comm_type"));
                }else{
                	additionalProfileDeatilsVO.setAddRoamCommType("");
                }
                additionalProfileDeatilsVO.setAddRoamCommRate(rs.getDouble("roam_addnl_com_rate"));

                if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDeatilsVO.getAddCommType())) {
                    additionalProfileDeatilsVO.setAddCommRateAsString(PretupsBL.getDisplayAmount(rs.getLong("addnl_comm_rate")));
                }

                else {
                    additionalProfileDeatilsVO.setAddCommRateAsString(String.valueOf(additionalProfileDeatilsVO.getAddCommRate()));
                }
                // added for roam recharge

                if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDeatilsVO.getAddRoamCommType())) {
                    additionalProfileDeatilsVO.setAddRoamCommRateAsString(PretupsBL.getDisplayAmount(rs.getLong("roam_addnl_com_rate")));
                } else {
                    additionalProfileDeatilsVO.setAddRoamCommRateAsString(String.valueOf(additionalProfileDeatilsVO.getAddRoamCommRate()));
                }

                additionalProfileDeatilsVO.setDiffrentialFactor(rs.getDouble("diffrential_factor"));
                additionalProfileDeatilsVO.setDiffrentialFactorAsString(String.valueOf(rs.getDouble("diffrential_factor")));
                if(!BTSLUtil.isNullString(rs.getString("tax1_type"))){
                additionalProfileDeatilsVO.setTax1Type(rs.getString("tax1_type"));
                }else{
                	additionalProfileDeatilsVO.setTax1Type("");
                }
                additionalProfileDeatilsVO.setTax1Rate(rs.getDouble("tax1_rate"));
                if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDeatilsVO.getTax1Type())) {
                    additionalProfileDeatilsVO.setTax1RateAsString(PretupsBL.getDisplayAmount(rs.getLong("tax1_rate")));
                } else {
                    additionalProfileDeatilsVO.setTax1RateAsString(String.valueOf(additionalProfileDeatilsVO.getTax1Rate()));
                }
                if(!BTSLUtil.isNullString(rs.getString("tax2_type"))){
                    additionalProfileDeatilsVO.setTax2Type(rs.getString("tax2_type"));
                    }else{
                    	additionalProfileDeatilsVO.setTax2Type("");
                    }
                additionalProfileDeatilsVO.setTax2Rate(rs.getDouble("tax2_rate"));
                if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDeatilsVO.getTax2Type())) {
                    additionalProfileDeatilsVO.setTax2RateAsString(PretupsBL.getDisplayAmount(rs.getLong("tax2_rate")));
                } else {
                    additionalProfileDeatilsVO.setTax2RateAsString(String.valueOf(additionalProfileDeatilsVO.getTax2Rate()));
                }

                additionalProfileDeatilsVO.setAddtnlComStatus(rs.getString("status"));
                additionalProfileDeatilsVO.setAddtnlComStatusName(rs.getString("status_name"));
                if (isOwnerCommissionAllowed) 
			    {
                	 if(!BTSLUtil.isNullString(rs.getString("OWN_ADDNL_COMM_TYPE"))){
                         additionalProfileDeatilsVO.setAddOwnerCommType(rs.getString("OWN_ADDNL_COMM_TYPE"));
                         }else{
                         	additionalProfileDeatilsVO.setAddOwnerCommType("");
                         }
			    	if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDeatilsVO.getAddOwnerCommType())) {
                		additionalProfileDeatilsVO.setAddOwnerCommRateAsString(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("OWN_ADDNL_COMM_RATE"))));
                	} else {
                		additionalProfileDeatilsVO.setAddOwnerCommRateAsString(String.valueOf(rs.getDouble("OWN_ADDNL_COMM_RATE")));
                	}
			    	if(!BTSLUtil.isNullString(rs.getString("OWN_TAX1_TYPE"))){
                        additionalProfileDeatilsVO.setOwnerTax1Type(rs.getString("OWN_TAX1_TYPE"));
                        }else{
                        	additionalProfileDeatilsVO.setOwnerTax1Type("");
                        }
			    	if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDeatilsVO.getAddOwnerCommType())) {
			    		additionalProfileDeatilsVO.setOwnerTax1RateAsString(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("OWN_TAX1_RATE"))));
                	} else {
                		additionalProfileDeatilsVO.setOwnerTax1RateAsString(String.valueOf(rs.getDouble("OWN_TAX1_RATE")));
                	}
			    	if(!BTSLUtil.isNullString(rs.getString("OWN_TAX2_TYPE"))){
                        additionalProfileDeatilsVO.setOwnerTax2Type(rs.getString("OWN_TAX2_TYPE"));
                        }else{
                        	additionalProfileDeatilsVO.setOwnerTax2Type("");
                        }		    	
			    	if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDeatilsVO.getAddOwnerCommType())) {
			    		additionalProfileDeatilsVO.setOwnerTax2RateAsString(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("OWN_TAX2_RATE"))));
                	} else {
			    		additionalProfileDeatilsVO.setOwnerTax2RateAsString(String.valueOf(rs.getDouble("OWN_TAX2_RATE")));
                	}
			    }
                if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, networkCode)){
                	if(rs.getDate("otf_applicable_from")!=null){
                	additionalProfileDeatilsVO.setOtfApplicableFromStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("otf_applicable_from")))));
                	}
                	if(rs.getDate("otf_applicable_to")!=null){
                	additionalProfileDeatilsVO.setOtfApplicableToStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("otf_applicable_to")))));
                	additionalProfileDeatilsVO.setOrigOtfApplicableToStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("otf_applicable_to")))));
                	}
                	if(!BTSLUtil.isNullString(rs.getString("otf_type"))){
                        additionalProfileDeatilsVO.setOtfType(rs.getString("otf_type"));
                        }else{
                        	additionalProfileDeatilsVO.setOtfType("");
                        }
                	additionalProfileDeatilsVO.setOtfTimeSlab(rs.getString("OTF_TIME_SLAB"));
                	if(!BTSLUtil.isNullString(additionalProfileDeatilsVO.getOtfTimeSlab())){
                		String[] s = additionalProfileDeatilsVO.getOtfTimeSlab().split(",");
                		additionalProfileDeatilsVO.setOrigOtfApplicableToStr(additionalProfileDeatilsVO.getOrigOtfApplicableToStr()+" "+(s[s.length-1]).split("-")[1]+":00");
                	}else{
                		additionalProfileDeatilsVO.setOrigOtfApplicableToStr(additionalProfileDeatilsVO.getOrigOtfApplicableToStr()+" 00:00:00");
                	}
                }
                addCommissionDetailList.add(additionalProfileDeatilsVO);
            }

            return addCommissionDetailList;
        }
            }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadAdditionalProfileDetailList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("commissionProfileWebDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadAdditionalProfileDetailList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("commissionProfileWebDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting addCommissionDetailList Size:" + addCommissionDetailList.size());
            }
        }// end of finally
    }

    /**
     * Method for Updating Card Group Set.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_commissionProfileSetVO
     *            CommissionProfileSetVO
     * 
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int updateCommissionProfileSet(Connection p_con, CommissionProfileSetVO p_commissionProfileSetVO) throws BTSLBaseException {


        
        int updateCount = 0;

        final String methodName = "updateCommissionProfileSet";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_commissionProfileSetVO=" + p_commissionProfileSetVO);
        }

        try {
            final StringBuilder strBuff = new StringBuilder();

            strBuff.append("UPDATE commission_profile_set SET comm_profile_set_name = ? ,");
            strBuff.append("short_code = ?, comm_last_version = ? ,modified_on = ?, modified_by = ?, last_dual_comm_type = ? ");
            strBuff.append("WHERE comm_profile_set_id = ?");

            final String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }

            // commented for DB2 psmtUpdate =

            try(PreparedStatement psmtUpdate =  p_con.prepareStatement(insertQuery);)
            // commented for DB2psmtUpdate.setFormOfUse(1,
            {
            psmtUpdate.setString(1, p_commissionProfileSetVO.getCommProfileSetName());
            psmtUpdate.setString(2, p_commissionProfileSetVO.getShortCode());
            psmtUpdate.setString(3, p_commissionProfileSetVO.getCommLastVersion());
            psmtUpdate.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_commissionProfileSetVO.getModifiedOn()));
            psmtUpdate.setString(5, p_commissionProfileSetVO.getModifiedBy());
            psmtUpdate.setString(6, p_commissionProfileSetVO.getDualCommissionType());
            psmtUpdate.setString(7, p_commissionProfileSetVO.getCommProfileSetId());

            final boolean modified = this.recordModified(p_con, p_commissionProfileSetVO.getCommProfileSetId(), p_commissionProfileSetVO.getLastModifiedOn());

            // if modified = true mens record modified by another user
            if (modified) {
                throw new BTSLBaseException("error.modified");
            }
            updateCount = psmtUpdate.executeUpdate();

        }
        }// end of try
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "commissionProfileWebDAO[updateCommissionProfileSet]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "commissionProfileWebDAO[updateCommissionProfileSet]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
        	if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally

        return updateCount;
    }

    /**
     * This method is used to check whether the record in the database is
     * modified or not If there is any error then throws the SQLException
     * 
     * @param con
     *            Connection
     * @param p_commProfileSetID
     *            String
     * @param oldLastModified
     *            long
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean recordModified(Connection con, String p_commProfileSetID, long oldLastModified) throws BTSLBaseException {
        final String methodName = "recordModified";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_commProfileSetID= " + p_commProfileSetID + "oldLastModified= " + oldLastModified);
        }

       
        boolean modified = false;
        final String sqlRecordModified = "SELECT modified_on FROM commission_profile_set WHERE comm_profile_set_id = ?";
        Timestamp newLastModified = null;
        if ((oldLastModified) == 0) {
            return false;
        }
      
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY: sqlselect= " + sqlRecordModified);
            }
            // create a prepared statement and execute it
            try (PreparedStatement pstmt = con.prepareStatement(sqlRecordModified);){
            pstmt.setString(1, p_commProfileSetID);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " old=" + oldLastModified);
                if (newLastModified != null) {
                    _log.debug(methodName, " new=" + newLastModified.getTime());
                } else {
                    _log.debug(methodName, " new=null");
                }
            }
            if (newLastModified != null && newLastModified.getTime() != oldLastModified) {
                modified = true;
            }

            return modified;
        }
            }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "commissionProfileWebDAO[recordModified]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "commissionProfileWebDAO[recordModified]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch

        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exititng: modified=" + modified);
            }
        } // end of finally
    } // end recordModified

    /**
     * Method for update Commission Profile Set Table.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_voList
     *            java.util.ArrayList
     * 
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int suspendCommissionProfileList(Connection p_con, ArrayList p_voList) throws BTSLBaseException {

        
        int updateCount = 0;

        final String methodName = "suspendCommissionProfileList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_voList Size= " + p_voList.size());
        }

        try {
            // checking the modified status of all the networks one by one
            int listSize = 0;
            boolean modified = false;
            if (p_voList != null) {
                listSize = p_voList.size();
            }

            final StringBuilder strBuff = new StringBuilder();

            strBuff.append("Update commission_profile_set SET status = ?, modified_by = ?, modified_on = ?,");
            strBuff.append("language_1_message = ?, language_2_message = ?");
            strBuff.append(" WHERE comm_profile_set_id = ?");

            final String updateQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlUpdate:" + updateQuery);
            }

            // commented for DB2 psmtUpdate = (OraclePreparedStatement)

            try(PreparedStatement psmtUpdate =  p_con.prepareStatement(updateQuery);)
            {
            CommissionProfileSetVO commissionProfileSetVO = null;
            for (int i = 0; i < listSize; i++) {
                commissionProfileSetVO = (CommissionProfileSetVO) p_voList.get(i);

                psmtUpdate.setString(1, commissionProfileSetVO.getStatus());
                psmtUpdate.setString(2, commissionProfileSetVO.getModifiedBy());
                psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(commissionProfileSetVO.getModifiedOn()));
                psmtUpdate.setString(4, commissionProfileSetVO.getLanguage1Message());

                // commented for DB2 psmtUpdate.setFormOfUse(5,

                psmtUpdate.setString(5, commissionProfileSetVO.getLanguage2Message());

                psmtUpdate.setString(6, commissionProfileSetVO.getCommProfileSetId());

                modified = this.recordModified(p_con, commissionProfileSetVO.getCommProfileSetId(), commissionProfileSetVO.getLastModifiedOn());

                // if modified = true mens record modified by another user
                if (modified) {
                    throw new BTSLBaseException("error.modified");
                }

                updateCount = psmtUpdate.executeUpdate();

                psmtUpdate.clearParameters();

                // check the status of the update
                if (updateCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }

        }
        }// end of try
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException:" + be.toString());
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[suspendCommissionProfileList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[suspendCommissionProfileList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
        	if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally

        return updateCount;
    }

    /**
     * Method for loading Commission Profile Sets.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_networkCode
     *            String
     * @param p_categoryCode
     *            String
     * 
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCommissionProfileSet(Connection p_con, String p_networkCode, String p_categoryCode, String p_gradeCode, String p_geographyCode) throws BTSLBaseException {
        final String methodName = "loadCommissionProfileSet";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,
                "Entered p_networkCode=" + p_networkCode + " p_categoryCode=" + p_categoryCode + " p_gradeCode=" + p_gradeCode + " p_geographyCode=" + p_geographyCode);
        }

        Boolean isDefaultProfile = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE);

        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT cs.comm_profile_set_id,cs.comm_profile_set_name,cs.category_code,cs.network_code,cs.grade_code,cs.geography_code,");
        strBuff.append("cs.comm_last_version,cs.created_on,cs.created_by, cs.modified_on,cs.modified_by, ");
        strBuff.append(" cs.short_code,cs.status,cs.language_1_message,cs.language_2_message,cs.IS_DEFAULT,cs.last_dual_comm_type FROM commission_profile_set cs");
        strBuff.append(" WHERE cs.network_code = ?");
        strBuff.append(" AND cs.category_code = ? AND cs.status <> 'N'");
        if(!p_gradeCode.equals("ALL"))
        {
        strBuff.append(" AND cs.grade_code = ?");
        }
        if(!p_geographyCode.equals("ALL"))
        {
            strBuff.append("AND cs.geography_code = ?");
        }
        strBuff.append(" ORDER BY comm_profile_set_name");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        final ArrayList list = new ArrayList();
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            int i = 1;
            pstmtSelect.setString(i++, p_networkCode);
            pstmtSelect.setString(i++, p_categoryCode);
            if(!p_gradeCode.equals("ALL"))
            {
            pstmtSelect.setString(i++, p_gradeCode);
            }
            if(!p_geographyCode.equals("ALL"))
            {
            pstmtSelect.setString(i++, p_geographyCode);
            }
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            CommissionProfileSetVO commissionProfileSetVO = null;
            while (rs.next()) {
                commissionProfileSetVO = new CommissionProfileSetVO();
                commissionProfileSetVO.setCommProfileSetId(rs.getString("comm_profile_set_id"));
                commissionProfileSetVO.setCommProfileSetName(rs.getString("comm_profile_set_name"));
                commissionProfileSetVO.setCategoryCode(rs.getString("category_code"));
                commissionProfileSetVO.setNetworkCode(rs.getString("network_code"));
                commissionProfileSetVO.setCommLastVersion(rs.getString("comm_last_version"));
                commissionProfileSetVO.setCreatedBy(rs.getString("created_by"));
                commissionProfileSetVO.setModifiedBy(rs.getString("modified_by"));
                commissionProfileSetVO.setCreatedOn(rs.getDate("created_on"));
                commissionProfileSetVO.setModifiedOn(rs.getDate("modified_on"));
                commissionProfileSetVO.setLastModifiedOn(rs.getTimestamp("modified_on").getTime());
                commissionProfileSetVO.setShortCode(rs.getString("short_code"));
                commissionProfileSetVO.setStatus(rs.getString("status"));
                commissionProfileSetVO.setLanguage1Message(rs.getString("language_1_message"));
                commissionProfileSetVO.setLanguage2Message(rs.getString("language_2_message"));
                if (isDefaultProfile) {
                    // default profile.
                    commissionProfileSetVO.setDefaultProfile(rs.getString("IS_DEFAULT"));
                }
                commissionProfileSetVO.setGradeCode(rs.getString("GRADE_CODE"));
                commissionProfileSetVO.setGrphDomainCode(rs.getString("GEOGRAPHY_CODE"));
                commissionProfileSetVO.setDualCommissionType(rs.getString("last_dual_comm_type"));
                list.add(commissionProfileSetVO);
            }

        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "commissionProfileWebDAO[loadCommissionProfileSet]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "commissionProfileWebDAO[loadCommissionProfileSet]",
                "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: commissionProfileSetList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for Deleting Commission Profile Set(just update the status set
     * status=N).
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_commissionProfileSetVO
     *            CommissionProfileSetVO
     * @return deleteCount int
     * @throws BTSLBaseException
     */
    public int deleteCommissionProfileSet(Connection p_con, CommissionProfileSetVO p_commissionProfileSetVO) throws BTSLBaseException {
        
        int deleteCount = 0;
        final String methodName = "deleteCommissionProfileSet";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_commissionProfileSetVO=" + p_commissionProfileSetVO);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("UPDATE commission_profile_set SET status = ? , modified_by = ? , modified_on = ? ");
            strBuff.append("WHERE comm_profile_set_id = ?");
            final String deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            try(PreparedStatement psmtDelete = p_con.prepareStatement(deleteQuery);)
            {
            psmtDelete.setString(1, p_commissionProfileSetVO.getStatus());
            psmtDelete.setString(2, p_commissionProfileSetVO.getModifiedBy());
            psmtDelete.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_commissionProfileSetVO.getModifiedOn()));
            psmtDelete.setString(4, p_commissionProfileSetVO.getCommProfileSetId());

            deleteCount = psmtDelete.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "commissionProfileWebDAO[deleteCommissionProfileSet]",
                "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "commissionProfileWebDAO[deleteCommissionProfileSet]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	
        	if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        } // end of finally

        return deleteCount;
    }

    /**
     * Method for checking Commission Profile Name is already exist or not.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_commProfileSetId
     *            String
     * @param p_status
     *            String
     * 
     * @return flag boolean
     * @throws BTSLBaseException
     */
    public boolean isCommissionProfileSetAssociated(Connection p_con, String p_commProfileSetId, String p_status) throws BTSLBaseException {
        final String methodName = "isCommissionProfileSetAssociated";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_commProfileSetId=" + p_commProfileSetId + " p_status=" + p_status);
        }


        
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();

        strBuff.append("SELECT comm_profile_set_name FROM commission_profile_set cps, channel_users cs , users u");
        strBuff.append(" WHERE cps.comm_profile_set_id = ? AND cps.comm_profile_set_id = cs.comm_profile_set_id ");
        strBuff.append(" AND cs.user_id = u.user_id AND u.status not in (" + p_status + ")");

        final String sqlSelect = strBuff.toString();

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        try (PreparedStatement pstmt =  p_con.prepareStatement(sqlSelect);){
            // commented for DB2 pstmt =

           
            pstmt.setString(1, p_commProfileSetId);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                existFlag = true;
            }

            return existFlag;
        }
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[isCommissionProfileSetAssociated]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[isCommissionProfileSetAssociated]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * Method for loading Commission Profile Set Versions List.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_commissionProfileSetId
     *            String
     * @param p_moduleCode
     *            String
     * 
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadCommissionProfileSetVersionList(Connection p_con, String p_commissionProfileSetId) throws BTSLBaseException {

        final String methodName = "loadCommissionProfileSetVersionList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_commissionProfileSetId=" + p_commissionProfileSetId);
        }
        
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT cv.dual_comm_type,cv.comm_profile_set_id,cv.comm_profile_set_version,cv.applicable_from ");
        strBuff.append(" FROM commission_profile_set cs,commission_profile_set_version cv WHERE cs.comm_profile_set_id = ? ");
        strBuff.append(" AND cs.comm_profile_set_id = cv.comm_profile_set_id AND ");
        strBuff.append(" cs.status <> 'N' ORDER BY comm_profile_set_version");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        final ArrayList list = new ArrayList();

        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, p_commissionProfileSetId);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            CommissionProfileSetVersionVO commissionProfileSetVersionVO = null;
            while (rs.next()) {
                commissionProfileSetVersionVO = new CommissionProfileSetVersionVO();
                commissionProfileSetVersionVO.setCommProfileSetId(rs.getString("comm_profile_set_id"));
                commissionProfileSetVersionVO.setCommProfileSetVersion(rs.getString("comm_profile_set_version"));
                commissionProfileSetVersionVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                commissionProfileSetVersionVO.setOldApplicableFrom(rs.getTimestamp("applicable_from").getTime());
                commissionProfileSetVersionVO.setDualCommissionType(rs.getString("dual_comm_type"));
                list.add(commissionProfileSetVersionVO);
            }

        } 
        }catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommissionProfileSetVersionList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommissionProfileSetVersionList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCardGroupSetVersionList", "error.general.processing");
        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: loadCommissionProfileSetVersionList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for updating the default commission profile.
     * 
     * @param p_con
     * @param p_commissionProfileSetId
     * @param p_categoryCode
     * @return int
     * @throws BTSLBaseException
     * @author AshishT
     */
    public int updateDefaultCommission(Connection p_con, String p_commissionProfileSetId, String p_categoryCode, String p_oldCode,String networkCode) throws BTSLBaseException {

        final String methodName = "updateDefaultGrades";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_commissionProfileSetId=" + p_commissionProfileSetId + "p_categoryCode= " + p_categoryCode + "p_oldCode= " + p_oldCode);
        }
        PreparedStatement pstmtUpdateAll = null;
        PreparedStatement pstmtUpdateCom = null;
        PreparedStatement pstmtUpdateOld = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" UPDATE COMMISSION_PROFILE_SET SET IS_DEFAULT=? ");
        strBuff.append(" WHERE STATUS=? AND CATEGORY_CODE=? and network_code=?");

        final StringBuilder updateGrade = new StringBuilder();
        updateGrade.append("UPDATE COMMISSION_PROFILE_SET SET IS_DEFAULT=? ");
        updateGrade.append("WHERE CATEGORY_CODE=? and COMM_PROFILE_SET_ID=? and network_code=? ");

        final StringBuilder updateOldStatus = new StringBuilder();
        updateOldStatus.append("UPDATE COMMISSION_PROFILE_SET SET STATUS=?,IS_DEFAULT=? WHERE COMM_PROFILE_SET_ID=? and network_code=? ");

        int count = 0;
        final String sqlSelect = strBuff.toString();
        final String sqlUpdateCommission = updateGrade.toString();
        final String sqlUpdateOld = updateOldStatus.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            _log.debug(methodName, "QUERY sqlUpdateCommission=" + sqlUpdateCommission);
            _log.debug(methodName, "QUERY sqlUpdateOld=" + sqlUpdateOld);
        }

        try {
            pstmtUpdateAll = p_con.prepareStatement(sqlSelect);
            pstmtUpdateAll.setString(1, PretupsI.NO);
            pstmtUpdateAll.setString(2, PretupsI.YES);
            pstmtUpdateAll.setString(3, p_categoryCode);
            pstmtUpdateAll.setString(4, networkCode);

            pstmtUpdateCom = p_con.prepareStatement(sqlUpdateCommission);
            pstmtUpdateCom.setString(1, PretupsI.YES);
            pstmtUpdateCom.setString(2, p_categoryCode);
            pstmtUpdateCom.setString(3, p_commissionProfileSetId);
            pstmtUpdateCom.setString(4, networkCode);

            count = pstmtUpdateAll.executeUpdate();
            count = pstmtUpdateCom.executeUpdate();
            pstmtUpdateOld = p_con.prepareStatement(sqlUpdateOld);
            pstmtUpdateOld.setString(1, PretupsI.YES);
            if (p_commissionProfileSetId.equalsIgnoreCase(p_oldCode)) {
                pstmtUpdateOld.setString(2, PretupsI.YES);
            } else {
                pstmtUpdateOld.setString(2, PretupsI.NO);
            }
            pstmtUpdateOld.setString(3, p_oldCode);
            pstmtUpdateOld.setString(4, networkCode);
            count = pstmtUpdateOld.executeUpdate();

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "commissionProfileWebDAO[updateDefaultGrades]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateDefaultGrades()", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "commissionProfileWebDAO[updateDefaultGrades]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "updateDefaultGrades()", "error.general.processing");
        } finally {
        	try{
        		if (pstmtUpdateAll!= null){
        			pstmtUpdateAll.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtUpdateCom!= null){
        			pstmtUpdateCom.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtUpdateOld!= null){
        			pstmtUpdateOld.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateDefaultGrades count = " + count);
            }
        }
        return count;
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
     * @return boolean
     * @exception BTSLBaseException
     */
    public ArrayList loadCommissionProfileList(Connection p_con, String p_domainCode, String p_categoryCode, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList commProfileSlabDetails = null;
        ArrayList addCommProfSlabDetails = null;
        final String methodName = "loadCommissionProfileList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_categoryCode=" + p_categoryCode + "Entered p_domainCode=" + p_domainCode);
        }

        CommissionProfileWebQry commissionProfileWebQry = (CommissionProfileWebQry)ObjectProducer.getObject(QueryConstants.COMM_PROFILE_WEB_QRY, QueryConstants.QUERY_PRODUCER);
        
        final String selectQuery1 = commissionProfileWebQry.loadCommissionProfileListQry();

        PreparedStatement pstmtCommProfProductID = null;
        ResultSet rsCommProfProductID = null;
        final StringBuilder strBuffProductID = new StringBuilder();
        strBuffProductID.append("SELECT cpp.comm_profile_products_id,p.product_name,");
        strBuffProductID.append("cpp.min_transfer_value,cpp.max_transfer_value,cpp.transfer_multiple_off,");
        strBuffProductID.append("cpp.taxes_on_foc_applicable,cpp.payment_mode,cpp.transaction_type,cpp.taxes_on_channel_transfer ");
        strBuffProductID.append(" FROM commission_profile_products cpp,products p WHERE cpp.product_code = p.product_code ");
        strBuffProductID.append("AND cpp.comm_profile_set_id = ? AND cpp.comm_profile_set_version = ?");
        final String selectQuery1a = strBuffProductID.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "selectQuery1a:" + selectQuery1a);
        }

        PreparedStatement pstmtCommProfileDetails = null;
        ResultSet rsCommProfileDetails = null;
        final StringBuilder selectQueryBuff = new StringBuilder(" SELECT comm_profile_detail_id,comm_profile_products_id,");
        selectQueryBuff.append(" start_range,end_range,commission_type,commission_rate,");
        selectQueryBuff.append(" tax1_type, tax1_rate, tax2_type, tax2_rate,tax3_type,tax3_rate ");
        selectQueryBuff.append(" FROM commission_profile_details WHERE comm_profile_products_id = ? ");
        final String selectQuery2 = selectQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "selectQuery2:" + selectQuery2);
        }

        PreparedStatement pstmtAddCommProfExist = null;
        ResultSet rsAddCommProfExist = null;
        final StringBuilder strBuffAddCommProfExist = new StringBuilder();
        strBuffAddCommProfExist.append(" SELECT cpst.comm_profile_service_type_id,");
        strBuffAddCommProfExist.append(" cpst.min_transfer_value,cpst.max_transfer_value,st.name");
        strBuffAddCommProfExist.append(" FROM comm_profile_service_types cpst ,service_type st WHERE cpst.service_type = st.service_type ");
        strBuffAddCommProfExist.append(" AND cpst.comm_profile_set_id = ? ");
        strBuffAddCommProfExist.append(" AND cpst.comm_profile_set_version = ? ");
        final String selectQuery3 = strBuffAddCommProfExist.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadCommissionProfileList()", "selectQuery3:" + selectQuery3);
        }

        PreparedStatement pstmtAddCommProfDetails = null;
        ResultSet rsAddCommProfDetails = null;
        final StringBuilder strBuffAddCommProfDetails = new StringBuilder(" SELECT addnl_comm_profile_detail_id,");
        strBuffAddCommProfDetails.append(" comm_profile_service_type_id,start_range,end_range,");
        strBuffAddCommProfDetails.append(" addnl_comm_type,addnl_comm_rate,diffrential_factor,");
        strBuffAddCommProfDetails.append(" tax1_type, tax1_rate, tax2_type, tax2_rate ");
        strBuffAddCommProfDetails.append(" FROM addnl_comm_profile_details  ");
        strBuffAddCommProfDetails.append(" WHERE comm_profile_service_type_id = ? ");
        final String selectQuery4 = strBuffAddCommProfDetails.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "selectQuery4:" + selectQuery4);
        }

        final ArrayList list = new ArrayList();

        try {
            int i = 1;
            pstmtCommProfileDetails = p_con.prepareStatement(selectQuery2);
            pstmtAddCommProfExist = p_con.prepareStatement(selectQuery3);
            pstmtAddCommProfDetails = p_con.prepareStatement(selectQuery4);
            pstmtCommProfProductID = p_con.prepareStatement(selectQuery1a);
            pstmtSelect = p_con.prepareStatement(selectQuery1);
            pstmtSelect.setString(i++, p_domainCode);
            pstmtSelect.setString(i++, p_categoryCode);
            pstmtSelect.setString(i++, p_categoryCode);
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_fromDate));
            pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_toDate));
            rs = pstmtSelect.executeQuery();
            CommissionProfileVO commissionProfileVO = null;
            while (rs.next()) {
                commissionProfileVO = new CommissionProfileVO();
                final CommissionProfileSetVO commissionProfileSetVO = new CommissionProfileSetVO();
                commissionProfileSetVO.setCommProfileSetId(rs.getString("comm_profile_set_id"));
                commissionProfileSetVO.setNetworkName(rs.getString("network_name"));
                commissionProfileSetVO.setCategoryName(rs.getString("category_name"));
                commissionProfileSetVO.setCommProfileSetName(rs.getString("comm_profile_set_name"));
                commissionProfileSetVO.setShortCode(rs.getString("short_code"));
                commissionProfileVO.setCommissionProfileSetVO(commissionProfileSetVO);

                final CommissionProfileSetVersionVO commissionProfileSetVersionVO = new CommissionProfileSetVersionVO();
                commissionProfileSetVersionVO.setCommProfileSetVersion(rs.getString("comm_profile_set_version"));
                commissionProfileSetVersionVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                commissionProfileSetVersionVO.setOldApplicableFrom(rs.getTimestamp("applicable_from").getTime());
                commissionProfileVO.setCommissionProfileSetVersionVO(commissionProfileSetVersionVO);

                i = 1;
                pstmtCommProfProductID.clearParameters();
                pstmtCommProfProductID.setString(i++, rs.getString("comm_profile_set_id"));
                pstmtCommProfProductID.setString(i++, rs.getString("comm_profile_set_version"));
                rsCommProfProductID = pstmtCommProfProductID.executeQuery();

                while (rsCommProfProductID.next()) {
                    final CommissionProfileProductsVO commissionProfileProductsVO = new CommissionProfileProductsVO();
                    commissionProfileProductsVO.setCommProfileProductID(rsCommProfProductID.getString("comm_profile_products_id"));
                    commissionProfileProductsVO.setProductCodeDesc(rsCommProfProductID.getString("product_name"));
                    commissionProfileProductsVO.setPaymentMode(rsCommProfProductID.getString("payment_mode"));
                    if(rs.getString("payment_mode") != null)
                    	commissionProfileProductsVO.setPaymentModeDesc(((LookupsVO)LookupsCache.getObject(PretupsI.PAYMENT_INSTRUMENT_MODE, rs.getString("payment_mode"))).getLookupName());
                    commissionProfileProductsVO.setTransactionType(rsCommProfProductID.getString("transaction_type"));
                    if(rs.getString("transaction_type") != null)
                    	commissionProfileProductsVO.setTransactionTypeDesc(((LookupsVO)LookupsCache.getObject(PretupsI.TRANSACTION_TYPE, rs.getString("transaction_type"))).getLookupName());
                    commissionProfileProductsVO.setMinTransferValue(rsCommProfProductID.getLong("min_transfer_value"));
                    commissionProfileProductsVO.setMaxTransferValue(rsCommProfProductID.getLong("max_transfer_value"));
                    commissionProfileProductsVO.setTransferMultipleOff(rsCommProfProductID.getLong("transfer_multiple_off"));
                    commissionProfileProductsVO.setTaxOnFOCApplicable(rsCommProfProductID.getString("taxes_on_foc_applicable"));
                    commissionProfileProductsVO.setTaxOnChannelTransfer(rsCommProfProductID.getString("taxes_on_channel_transfer"));
                    commissionProfileVO.setCommissionProfileProductsVO(commissionProfileProductsVO);

                    i = 1;
                    pstmtCommProfileDetails.clearParameters();
                    pstmtCommProfileDetails.setString(i++, rsCommProfProductID.getString("comm_profile_products_id"));
                    rsCommProfileDetails = pstmtCommProfileDetails.executeQuery();
                    commProfileSlabDetails = new ArrayList();
                    while (rsCommProfileDetails.next()) {
                        final CommissionProfileDeatilsVO commissionProfileDeatilsVO = new CommissionProfileDeatilsVO();
                        commissionProfileDeatilsVO.setStartRange(rsCommProfileDetails.getLong("start_range"));
                        commissionProfileDeatilsVO.setStartRangeAsString(PretupsBL.getDisplayAmount(rsCommProfileDetails.getLong("start_range")));
                        commissionProfileDeatilsVO.setEndRange(rsCommProfileDetails.getLong("end_range"));
                        commissionProfileDeatilsVO.setEndRangeAsString(PretupsBL.getDisplayAmount(rsCommProfileDetails.getLong("end_range")));
                        commissionProfileDeatilsVO.setCommType(rsCommProfileDetails.getString("commission_type"));
                        commissionProfileDeatilsVO.setCommRate(rsCommProfileDetails.getDouble("commission_rate"));
                        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(commissionProfileDeatilsVO.getCommType())) {
                            commissionProfileDeatilsVO.setCommRateAsString(PretupsBL.getDisplayAmount(rsCommProfileDetails.getLong("commission_rate")));
                        } else {
                            commissionProfileDeatilsVO.setCommRateAsString(String.valueOf(commissionProfileDeatilsVO.getCommRate()));
                        }
                        commissionProfileDeatilsVO.setTax1Type(rsCommProfileDetails.getString("tax1_type"));
                        commissionProfileDeatilsVO.setTax1Rate(rsCommProfileDetails.getDouble("tax1_rate"));
                        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(commissionProfileDeatilsVO.getTax1Type())) {
                            commissionProfileDeatilsVO.setTax1RateAsString(PretupsBL.getDisplayAmount(rsCommProfileDetails.getLong("tax1_rate")));
                        } else {
                            commissionProfileDeatilsVO.setTax1RateAsString(String.valueOf(commissionProfileDeatilsVO.getTax1Rate()));
                        }
                        commissionProfileDeatilsVO.setTax2Type(rsCommProfileDetails.getString("tax2_type"));
                        commissionProfileDeatilsVO.setTax2Rate(rsCommProfileDetails.getDouble("tax2_rate"));
                        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(commissionProfileDeatilsVO.getTax2Type())) {
                            commissionProfileDeatilsVO.setTax2RateAsString(PretupsBL.getDisplayAmount(rsCommProfileDetails.getLong("tax2_rate")));
                        } else {
                            commissionProfileDeatilsVO.setTax2RateAsString(String.valueOf(commissionProfileDeatilsVO.getTax2Rate()));
                        }

                        commissionProfileDeatilsVO.setTax3Type(rsCommProfileDetails.getString("tax3_type"));
                        commissionProfileDeatilsVO.setTax3Rate(rsCommProfileDetails.getDouble("tax3_rate"));
                        if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(commissionProfileDeatilsVO.getTax3Type())) {
                            commissionProfileDeatilsVO.setTax3RateAsString(PretupsBL.getDisplayAmount(rsCommProfileDetails.getLong("tax3_rate")));
                        } else {
                            commissionProfileDeatilsVO.setTax3RateAsString(String.valueOf(commissionProfileDeatilsVO.getTax3Rate()));
                        }

                        commProfileSlabDetails.add(commissionProfileDeatilsVO);
                    }
                    commissionProfileVO.setCommProfileSlabDetail(commProfileSlabDetails);

                    boolean flagAddCommProfExist = false;
                    i = 1;
                    pstmtAddCommProfExist.clearParameters();
                    pstmtAddCommProfExist.setString(i++, rs.getString("comm_profile_set_id"));
                    pstmtAddCommProfExist.setString(i++, rs.getString("comm_profile_set_version"));
                    rsAddCommProfExist = pstmtAddCommProfExist.executeQuery();
                    if (rsAddCommProfExist.getFetchSize() != 0) {
                        flagAddCommProfExist = true;
                        while (rsAddCommProfExist.next()) {
                            final AdditionalProfileServicesVO additionalProfileServicesVO = new AdditionalProfileServicesVO();
                            additionalProfileServicesVO.setCommProfileServiceTypeID(rsAddCommProfExist.getString("comm_profile_service_type_id"));
                            additionalProfileServicesVO.setMinTransferValue(rsAddCommProfExist.getLong("min_transfer_value"));
                            additionalProfileServicesVO.setMaxTransferValue(rsAddCommProfExist.getLong("max_transfer_value"));
                            additionalProfileServicesVO.setServiceTypeDesc(rsAddCommProfExist.getString("name"));
                            commissionProfileVO.setAdditionalProfileServicesVO(additionalProfileServicesVO);
                            commissionProfileVO.setFlagAddCommProfExist(flagAddCommProfExist);
                            i = 1;
                            pstmtAddCommProfDetails.clearParameters();
                            pstmtAddCommProfDetails.setString(i++, rsAddCommProfExist.getString("comm_profile_service_type_id"));
                            rsAddCommProfDetails = pstmtAddCommProfDetails.executeQuery();

                            addCommProfSlabDetails = new ArrayList();
                            while (rsAddCommProfDetails.next()) {
                                final AdditionalProfileDeatilsVO additionalProfileDeatilsVO = new AdditionalProfileDeatilsVO();
                                additionalProfileDeatilsVO.setStartRange(rsAddCommProfDetails.getLong("start_range"));
                                additionalProfileDeatilsVO.setStartRangeAsString(PretupsBL.getDisplayAmount(rsAddCommProfDetails.getLong("start_range")));
                                additionalProfileDeatilsVO.setEndRange(rsAddCommProfDetails.getLong("end_range"));
                                additionalProfileDeatilsVO.setEndRangeAsString(PretupsBL.getDisplayAmount(rsAddCommProfDetails.getLong("end_range")));
                                additionalProfileDeatilsVO.setAddCommType(rsAddCommProfDetails.getString("addnl_comm_type"));
                                additionalProfileDeatilsVO.setAddCommRate(rsAddCommProfDetails.getDouble("addnl_comm_rate"));
                                if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDeatilsVO.getAddCommType())) {
                                    additionalProfileDeatilsVO.setAddCommRateAsString(PretupsBL.getDisplayAmount(rsAddCommProfDetails.getLong("addnl_comm_rate")));
                                } else {
                                    additionalProfileDeatilsVO.setAddCommRateAsString(String.valueOf(additionalProfileDeatilsVO.getAddCommRate()));
                                }

                                additionalProfileDeatilsVO.setDiffrentialFactor(rsAddCommProfDetails.getDouble("diffrential_factor"));
                                additionalProfileDeatilsVO.setDiffrentialFactorAsString(String.valueOf(rsAddCommProfDetails.getDouble("diffrential_factor")));
                                additionalProfileDeatilsVO.setTax1Type(rsAddCommProfDetails.getString("tax1_type"));
                                additionalProfileDeatilsVO.setTax1Rate(rsAddCommProfDetails.getDouble("tax1_rate"));
                                if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDeatilsVO.getTax1Type())) {
                                    additionalProfileDeatilsVO.setTax1RateAsString(PretupsBL.getDisplayAmount(rsAddCommProfDetails.getLong("tax1_rate")));
                                } else {
                                    additionalProfileDeatilsVO.setTax1RateAsString(String.valueOf(additionalProfileDeatilsVO.getTax1Rate()));
                                }

                                additionalProfileDeatilsVO.setTax2Type(rsAddCommProfDetails.getString("tax2_type"));
                                additionalProfileDeatilsVO.setTax2Rate(rsAddCommProfDetails.getDouble("tax2_rate"));
                                if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalProfileDeatilsVO.getTax2Type())) {
                                    additionalProfileDeatilsVO.setTax2RateAsString(PretupsBL.getDisplayAmount(rsAddCommProfDetails.getLong("tax2_rate")));
                                } else {
                                    additionalProfileDeatilsVO.setTax2RateAsString(String.valueOf(additionalProfileDeatilsVO.getTax2Rate()));
                                }

                                addCommProfSlabDetails.add(additionalProfileDeatilsVO);
                            }
                            commissionProfileVO.setAddCommProfSlabDetails(addCommProfSlabDetails);
                        }
                    } else {
                        flagAddCommProfExist = false;
                        commissionProfileVO.setFlagAddCommProfExist(flagAddCommProfExist);
                    }
                }
                list.add(commissionProfileVO);
            }
        } catch (SQLException sqe) {

            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "commissionProfileWebDAO[loadCommissionProfileList]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("loadCommissionProfileList()", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "commissionProfileWebDAO[loadCommissionProfileList]",
                "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (rsCommProfProductID!= null){
        			rsCommProfProductID.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (rsCommProfileDetails!= null){
        			rsCommProfileDetails.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (rsAddCommProfExist!= null){
        			rsAddCommProfExist.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (rsAddCommProfDetails!= null){
        			rsAddCommProfDetails.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}

        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
        	try{
        		if (pstmtAddCommProfExist!= null){
        			pstmtAddCommProfExist.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtAddCommProfDetails!= null){
        			pstmtAddCommProfDetails.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtCommProfProductID!= null){
        			pstmtCommProfProductID.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtCommProfileDetails!= null){
        			pstmtCommProfileDetails.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: loadCommissionProfileList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * added by gaurav pandey for batch add/modify commission profile (road map
     * 5.8)
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_categoryCode
     * @param p_currentDate
     * @return
     */

    public ArrayList loadSetNameSetVersion(Connection p_con, String p_networkCode, String p_categoryCode, Date p_currentDate) {
        final String methodName = "loadSetNameSetVersion";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_networkCode=" + p_networkCode);
        }
        /*String p_networkCode = SqlParameterEncoder.encodeParams(networkCode) ;
        String p_categoryCode = SqlParameterEncoder.encodeParams(categoryCode);*/

        ArrayList list = null;
        try {
            final StringBuilder selectQueryBuff = new StringBuilder(" SELECT cv.DUAL_COMM_TYPE, cs.COMM_PROFILE_SET_NAME, cs.SHORT_CODE,cs.GRADE_CODE ,cs.GEOGRAPHY_CODE,");
            selectQueryBuff.append(" cv.comm_profile_set_id,cv.comm_profile_set_version, ");
            selectQueryBuff
                .append(" cv.applicable_from,cp.COMM_PROFILE_PRODUCTS_ID,cp.MIN_TRANSFER_VALUE,cp.MAX_TRANSFER_VALUE,cp.TAXES_ON_CHANNEL_TRANSFER,cp.TAXES_ON_FOC_APPLICABLE, ");
            selectQueryBuff.append("cp.TRANSFER_MULTIPLE_OFF,cp.PRODUCT_CODE,cp.PAYMENT_MODE,cp.TRANSACTION_TYPE,cd.COMM_PROFILE_DETAIL_ID,cd.COMMISSION_TYPE,cd.COMMISSION_RATE,cd.START_RANGE,");
            selectQueryBuff.append("cd.END_RANGE,cd.TAX1_RATE,cd.TAX1_TYPE,cd.TAX2_RATE,");
            selectQueryBuff.append("cd.TAX2_TYPE,cd.TAX3_RATE,cd.TAX3_TYPE ");
            selectQueryBuff.append("FROM commission_profile_set cs,");
            selectQueryBuff.append(" commission_profile_set_version cv, ");
            selectQueryBuff.append(" COMMISSION_PROFILE_PRODUCTS cp, COMMISSION_PROFILE_DETAILS cd");
            selectQueryBuff.append(" WHERE cs.network_code =? AND cs.category_code = ? ");
            selectQueryBuff.append(" AND cs.comm_profile_set_id = cv.comm_profile_set_id AND  ");
            selectQueryBuff.append("cp.COMM_PROFILE_SET_VERSION = cv.COMM_PROFILE_SET_VERSION AND");
            selectQueryBuff.append(" (cv.applicable_from >=? OR cv.applicable_from =(SELECT MAX(cv2.applicable_from)");
            selectQueryBuff.append(" from commission_profile_set_version cv2 WHERE cs.comm_profile_set_id = cv2.comm_profile_set_id ))");
            selectQueryBuff.append("  AND cs.status <> 'N' ");
            selectQueryBuff.append("AND cs.COMM_PROFILE_SET_ID=cp.COMM_PROFILE_SET_ID AND cp.COMM_PROFILE_PRODUCTS_ID=cd.COMM_PROFILE_PRODUCTS_ID");
            selectQueryBuff.append(" ORDER BY comm_profile_set_id,cp.PRODUCT_CODE, cd.START_RANGE asc ");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("loadCommProfileSetLatestVersion", "select query:" + selectQuery);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_categoryCode);
            pstmtSelect.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            BatchModifyCommissionProfileVO batchModifyCommissionProfileVO = null;
            if (!(rs == null)) {
                list = new ArrayList();
            }
            while (rs.next()) {
                batchModifyCommissionProfileVO = new BatchModifyCommissionProfileVO();
                batchModifyCommissionProfileVO.setCommissionProfileType(SqlParameterEncoder.encodeParams(rs.getString("DUAL_COMM_TYPE")));
                batchModifyCommissionProfileVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                batchModifyCommissionProfileVO.setCommProfileSetId(SqlParameterEncoder.encodeParams(rs.getString("comm_profile_set_id")));
                batchModifyCommissionProfileVO.setCommProfileSetName(SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_SET_NAME")));
                batchModifyCommissionProfileVO.setCommProfileProductID(SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_PRODUCTS_ID")));
                batchModifyCommissionProfileVO.setShortCode(SqlParameterEncoder.encodeParams(rs.getString("SHORT_CODE")));
                batchModifyCommissionProfileVO.setEndRangeAsString(SqlParameterEncoder.encodeParams(rs.getString("END_RANGE")));
                batchModifyCommissionProfileVO.setStartRangeAsString(SqlParameterEncoder.encodeParams(rs.getString("START_RANGE")));
                batchModifyCommissionProfileVO.setCommType(SqlParameterEncoder.encodeParams(rs.getString("COMMISSION_TYPE")));
                batchModifyCommissionProfileVO.setCommRateAsString(SqlParameterEncoder.encodeParams(String.valueOf(rs.getDouble("COMMISSION_RATE"))));
                batchModifyCommissionProfileVO.setProductCode(SqlParameterEncoder.encodeParams(rs.getString("PRODUCT_CODE")));
                batchModifyCommissionProfileVO.setPaymentMode(SqlParameterEncoder.encodeParams(rs.getString("PAYMENT_MODE")));
                batchModifyCommissionProfileVO.setTransactionType(SqlParameterEncoder.encodeParams(rs.getString("TRANSACTION_TYPE")));
                batchModifyCommissionProfileVO.setCommProfileDetailID(SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_DETAIL_ID")));
                batchModifyCommissionProfileVO.setSetVersion(SqlParameterEncoder.encodeParams(rs.getString("comm_profile_set_version")));
                batchModifyCommissionProfileVO.setMinTransferValueAsString(SqlParameterEncoder.encodeParams(rs.getString("MIN_TRANSFER_VALUE")));
                batchModifyCommissionProfileVO.setMaxTransferValueAsString(SqlParameterEncoder.encodeParams(rs.getString("MAX_TRANSFER_VALUE")));
                batchModifyCommissionProfileVO.setTaxOnChannelTransfer(SqlParameterEncoder.encodeParams(rs.getString("TAXES_ON_CHANNEL_TRANSFER")));
                batchModifyCommissionProfileVO.setTaxOnFOCApplicable(SqlParameterEncoder.encodeParams(rs.getString("TAXES_ON_FOC_APPLICABLE")));
                batchModifyCommissionProfileVO.setTransferMultipleOffAsString(SqlParameterEncoder.encodeParams(rs.getString("TRANSFER_MULTIPLE_OFF")));
                batchModifyCommissionProfileVO.setTax1RateAsString(SqlParameterEncoder.encodeParams(String.valueOf(rs.getDouble("TAX1_RATE"))));
                batchModifyCommissionProfileVO.setTax1Type(SqlParameterEncoder.encodeParams(rs.getString("TAX1_TYPE")));
                batchModifyCommissionProfileVO.setTax2RateAsString(SqlParameterEncoder.encodeParams(String.valueOf(rs.getDouble("TAX2_RATE"))));
                batchModifyCommissionProfileVO.setTax2Type(SqlParameterEncoder.encodeParams(rs.getString("TAX2_TYPE")));
                batchModifyCommissionProfileVO.setTax3RateAsString(SqlParameterEncoder.encodeParams(String.valueOf(rs.getDouble("TAX3_RATE"))));
                batchModifyCommissionProfileVO.setTax3Type(SqlParameterEncoder.encodeParams(rs.getString("TAX3_TYPE")));
                batchModifyCommissionProfileVO.setGradeCode(SqlParameterEncoder.encodeParams(rs.getString("GRADE_CODE")));
                batchModifyCommissionProfileVO.setGrphDomainCode(SqlParameterEncoder.encodeParams(rs.getString("GEOGRAPHY_CODE")));
                list.add(batchModifyCommissionProfileVO);

            }

        }
            }
        }// end of try

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommProfileSetLatestVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());

        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommProfileSetLatestVersion]", "", "", "", "Exception:" + e.getMessage());

        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting list size:" + list.size());
            }

        }// end of finally
        return list;
    }

    /**
     * added by gaurav pandey for batch add/modify commisiion profile
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_categoryCode
     * @param p_currentDate
     * @return
     */

    public ArrayList loadAdditionalcommDetailForBatchModify(Connection p_con, String p_networkCode, String p_categoryCode, Date p_currentDate) {
        final String methodName = "loadAdditionalcommDetailForBatchModify";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_networkCode=" + p_networkCode);
        }
        /*String p_networkCode = SqlParameterEncoder.encodeParams(networkCode);
        String p_categoryCode = SqlParameterEncoder.encodeParams(categoryCode);*/
        Boolean isOwnerCommissionAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED);

        ArrayList list = null;
        int listsize=0;
        try {
            final StringBuilder selectQueryBuff = new StringBuilder(" SELECT cs.COMM_PROFILE_SET_NAME, cs.COMM_PROFILE_SET_ID , cv.COMM_PROFILE_SET_VERSION ,");
            selectQueryBuff.append(" cps.COMM_PROFILE_SERVICE_TYPE_ID,cps.MIN_TRANSFER_VALUE,cps.MAX_TRANSFER_VALUE,cps.SERVICE_TYPE, ");
            selectQueryBuff.append(" ac.ADDNL_COMM_RATE,ac.ADDNL_COMM_TYPE,ac.DIFFRENTIAL_FACTOR,ac.ROAM_ADDNL_COM_RATE, ");
            selectQueryBuff.append(" ac.ROAM_ADDNL_COMM_TYPE,ac.START_RANGE, ac.ADDNL_COMM_PROFILE_DETAIL_ID, ");
            selectQueryBuff.append(" ac.END_RANGE,ac.TAX1_TYPE,ac.TAX1_RATE,ac.TAX2_TYPE,ac.TAX2_RATE,ac.STATUS,cps.sub_service,");
            selectQueryBuff.append(" cps.APPLICABLE_FROM , cps.APPLICABLE_TO, cps.APPLICABLE_TIME_RANGE, cps.GATEWAY_CODE");
			if (isOwnerCommissionAllowed) {
				selectQueryBuff.append(" ,ac.own_addnl_comm_type ,ac.own_addnl_comm_rate,ac.own_tax1_type,ac.own_tax1_rate,ac.own_tax2_type,ac.own_tax2_rate");
			}
            selectQueryBuff.append(" FROM commission_profile_set cs,");
            selectQueryBuff.append(" commission_profile_set_version cv, ");
            selectQueryBuff.append("  COMM_PROFILE_SERVICE_TYPES cps,ADDNL_COMM_PROFILE_DETAILS ac ");
            selectQueryBuff.append(" WHERE cs.NETWORK_CODE=? AND cs.CATEGORY_CODE=? ");
            selectQueryBuff.append(" AND cs.comm_profile_set_id = cv.comm_profile_set_id AND  ");

            selectQueryBuff.append(" (cv.applicable_from >=? OR cv.applicable_from =(SELECT MAX(cv2.applicable_from)");
            selectQueryBuff.append(" from commission_profile_set_version cv2 WHERE cs.comm_profile_set_id = cv2.comm_profile_set_id ))");
            selectQueryBuff.append("  AND cs.status <> 'N' ");
            selectQueryBuff.append("AND cs.COMM_PROFILE_SET_ID=cps.COMM_PROFILE_SET_ID AND cv.COMM_PROFILE_SET_VERSION=cps.COMM_PROFILE_SET_VERSION");
            selectQueryBuff.append(" AND cps.COMM_PROFILE_SERVICE_TYPE_ID=ac.COMM_PROFILE_SERVICE_TYPE_ID ");
			selectQueryBuff.append(" ORDER BY cv.comm_profile_set_id,cv.comm_profile_set_version,cps.SERVICE_TYPE,cps.sub_service,ac.START_RANGE asc");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, p_networkCode);
            pstmtSelect.setString(2, p_categoryCode);
            pstmtSelect.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs != null) {
                list = new ArrayList();
            }
            AdditionalProfileDeatilsVO additionalProfileDeatilsVO = null;
            while (rs.next()) {
                additionalProfileDeatilsVO = new AdditionalProfileDeatilsVO();
                additionalProfileDeatilsVO.setProfileName(SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_SET_NAME")));
                additionalProfileDeatilsVO.setSetVersion(SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_SET_VERSION")));
                additionalProfileDeatilsVO.setSetID(SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_SET_ID")));
                additionalProfileDeatilsVO.setServiceID(SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_SERVICE_TYPE_ID")));
                additionalProfileDeatilsVO.setMinTrasferValueAsString(SqlParameterEncoder.encodeParams(rs.getString("MIN_TRANSFER_VALUE")));
                additionalProfileDeatilsVO.setMaxTransferValueAsString(SqlParameterEncoder.encodeParams(rs.getString("MAX_TRANSFER_VALUE")));
                additionalProfileDeatilsVO.setServiceType(SqlParameterEncoder.encodeParams(rs.getString("SERVICE_TYPE")));
                additionalProfileDeatilsVO.setAddCommRateAsString(SqlParameterEncoder.encodeParams(String.valueOf(rs.getDouble("ADDNL_COMM_RATE"))));
                additionalProfileDeatilsVO.setAddCommType(SqlParameterEncoder.encodeParams(rs.getString("ADDNL_COMM_TYPE")));
                additionalProfileDeatilsVO.setDiffrentialFactorAsString(SqlParameterEncoder.encodeParams(String.valueOf(rs.getDouble("DIFFRENTIAL_FACTOR"))));
                additionalProfileDeatilsVO.setAddRoamCommRateAsString(SqlParameterEncoder.encodeParams(String.valueOf(rs.getDouble("ROAM_ADDNL_COM_RATE"))));
                additionalProfileDeatilsVO.setAddRoamCommType(SqlParameterEncoder.encodeParams(rs.getString("ROAM_ADDNL_COMM_TYPE")));
                additionalProfileDeatilsVO.setAddCommProfileDetailID(SqlParameterEncoder.encodeParams(rs.getString("ADDNL_COMM_PROFILE_DETAIL_ID")));
                additionalProfileDeatilsVO.setEndRangeAsString(SqlParameterEncoder.encodeParams(rs.getString("END_RANGE")));
                additionalProfileDeatilsVO.setStartRangeAsString(SqlParameterEncoder.encodeParams(rs.getString("START_RANGE")));
                additionalProfileDeatilsVO.setTax1RateAsString(SqlParameterEncoder.encodeParams(String.valueOf(rs.getDouble("TAX1_RATE"))));
                additionalProfileDeatilsVO.setTax2RateAsString(SqlParameterEncoder.encodeParams(String.valueOf(rs.getDouble("TAX2_RATE"))));
                additionalProfileDeatilsVO.setTax1Type(SqlParameterEncoder.encodeParams(rs.getString("TAX1_TYPE")));
                additionalProfileDeatilsVO.setTax2Type(SqlParameterEncoder.encodeParams(rs.getString("TAX2_TYPE")));
                additionalProfileDeatilsVO.setAddtnlComStatus(SqlParameterEncoder.encodeParams(rs.getString("STATUS")));
                additionalProfileDeatilsVO.setSubServiceCode(SqlParameterEncoder.encodeParams(rs.getString("sub_service")));
                  final Date fromDate = rs.getDate("APPLICABLE_FROM");
                if (fromDate != null) {
                    additionalProfileDeatilsVO.setApplicableFromAdditional(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("APPLICABLE_FROM"))));
                }
                final Date toDate = rs.getDate("APPLICABLE_TO");
                if (toDate != null) {
                    additionalProfileDeatilsVO.setApplicableToAdditional(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("APPLICABLE_TO"))));
                }
                additionalProfileDeatilsVO.setAdditionalCommissionTimeSlab(SqlParameterEncoder.encodeParams(rs.getString("APPLICABLE_TIME_RANGE")));
                additionalProfileDeatilsVO.setGatewayCode(SqlParameterEncoder.encodeParams(rs.getString("GATEWAY_CODE")));
			    if (isOwnerCommissionAllowed) 
			    {
			    	additionalProfileDeatilsVO.setAddOwnerCommType(SqlParameterEncoder.encodeParams(rs.getString("OWN_ADDNL_COMM_TYPE")));
			    	additionalProfileDeatilsVO.setAddOwnerCommRateAsString(SqlParameterEncoder.encodeParams(rs.getString("OWN_ADDNL_COMM_RATE")));
			    	additionalProfileDeatilsVO.setOwnerTax1Type(SqlParameterEncoder.encodeParams(rs.getString("OWN_TAX1_TYPE")));
			    	additionalProfileDeatilsVO.setOwnerTax1RateAsString(SqlParameterEncoder.encodeParams(rs.getString("OWN_TAX1_RATE")));
			    	additionalProfileDeatilsVO.setOwnerTax2Type(SqlParameterEncoder.encodeParams(rs.getString("OWN_TAX2_TYPE")));
			    	additionalProfileDeatilsVO.setOwnerTax2RateAsString(SqlParameterEncoder.encodeParams(rs.getString("OWN_TAX2_RATE")));
			    }
                list.add(additionalProfileDeatilsVO);
            }
            
            if(list!=null){
            	listsize=list.size();
            }
        }
            }
        }// end of try

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommProfileSetLatestVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());

        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommProfileSetLatestVersion]", "", "", "", "Exception:" + e.getMessage());

        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting list size:" +listsize);
            }

        }// end of finally
        return list;
    }

    /**
     * 
     * @param p_con
     * @param setID
     * @param version
     * @return
     */

    public ArrayList loadOldApplicableDate(Connection p_con, String setID, String version) {
        final String methodName = "loadOldApplicableDate";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        

        ArrayList list = null;
        try {
            final StringBuilder selectQueryBuff = new StringBuilder("SELECT cv.APPLICABLE_FROM ,cs.COMM_LAST_VERSION FROM COMMISSION_PROFILE_SET_VERSION cv, ");
            selectQueryBuff.append(" COMMISSION_PROFILE_SET cs WHERE cv.COMM_PROFILE_SET_ID=?  ");
            selectQueryBuff.append("  AND cv.COMM_PROFILE_SET_VERSION=? AND cv.COMM_PROFILE_SET_ID=cs.COMM_PROFILE_SET_ID ");
            final String selectQuery = selectQueryBuff.toString();
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, setID);
            pstmtSelect.setString(2, version);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (!(rs == null)) {
                list = new ArrayList();
            }
            final CommissionProfileSetVO commissionProfileSetVO = new CommissionProfileSetVO();
            if (rs.next()) {
                commissionProfileSetVO.setApplicableFrom(rs.getTimestamp("APPLICABLE_FROM"));
                commissionProfileSetVO.setCommLastVersion(rs.getString("COMM_LAST_VERSION"));
                list.add(commissionProfileSetVO);
            }

        }
            }
        }catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommProfileSetLatestVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());

        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommProfileSetLatestVersion]", "", "", "", "Exception:" + e.getMessage());

        }// end of catch
        finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting list size:" + list.size());
            }

        }// end of finally
        return list;
    }

    /**
     * Method :loadCategoryList
     * This method load list of Categories on the basis of domain code
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            java.lang.String
     * @return HashMap
     * @throws BTSLBaseException
     * @author Gaurav pandey
     */
    public HashMap loadCategoryListForBatchAdd(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadCategoryListForBatchAdd";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_domainCode=" + p_domainCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder("SELECT C.sequence_no, C.category_code, C.category_name,C.sms_interface_allowed,C.web_interface_allowed,");
        strBuff.append(" C.services_allowed, C.user_id_prefix, C.low_bal_alert_allow,C.max_txn_msisdn FROM categories C WHERE C.domain_code=? ORDER BY sequence_no");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("loadCategoryList", "QUERY sqlSelect=" + sqlSelect);
        }
        final HashMap categoryMap = new HashMap();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainCode);
            rs = pstmtSelect.executeQuery();
            CategoryVO categoryVO = null;
            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                categoryVO.setMaxTxnMsisdn(rs.getString("max_txn_msisdn"));
                categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
                categoryMap.put(categoryVO.getCategoryCode(), categoryVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: categoryList size =" + categoryMap.size());
            }
        }
        return categoryMap;
    }

    /**
     * Method :loadCategoryList
     * This method load list of Categories on the basis of domain code
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            java.lang.String
     * @return HashMap
     * @throws BTSLBaseException
     * @author Gaurav pandey
     */
    public HashMap loadDomainListForBatchAdd(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadDomainListForBatchAdd";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_domainCode=" + p_domainCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT D.domain_code,D.domain_name,DT.restricted_msisdn,DT.display_allowed ");
        strBuff.append("FROM domains D,domain_types DT WHERE D.status <> 'N' AND D.domain_type_code =DT.domain_type_code ");
        strBuff.append("AND DT.domain_type_code <> ? ");
        strBuff.append("ORDER BY domain_name");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final HashMap domainMap = new HashMap();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainCode);
            rs = pstmtSelect.executeQuery();
            final CategoryVO categoryVO = null;
            while (rs.next()) {

                domainMap.put(rs.getString("domain_code"), rs.getString("domain_name"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: categoryList size =" + domainMap.size());
            }
        }
        return domainMap;
    }

    /**
     * Method :loadCategoryList
     * This method load list of Categories on the basis of domain code
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            java.lang.String
     * @return HashMap
     * @throws BTSLBaseException
     * @author Gaurav pandey
     */
    public HashMap loadProfileNameListForBatchAdd(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadProfileNameListForBatchAdd";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_domainCode=" + p_categoryCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("  SELECT COMM_PROFILE_SET_NAME,SHORT_CODE");

        strBuff.append(" FROM COMMISSION_PROFILE_SET ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final HashMap profileName = new HashMap();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);

            rs = pstmtSelect.executeQuery();
            final CategoryVO categoryVO = null;
            while (rs.next()) {

                profileName.put(rs.getString("COMM_PROFILE_SET_NAME"), rs.getString("SHORT_CODE"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: categoryList size =" + profileName.size());
            }
        }
        return profileName;
    }

    /**
     * Method :oadProductNameListForBatchAdd
     * This method load list of Categories on the basis of domain code
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            java.lang.String
     * @return HashMap
     * @throws BTSLBaseException
     * @author Gaurav pandey
     */
    public HashMap loadProductNameListForBatchAdd(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadProductNameListForBatchAdd";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, p_con);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("   SELECT PRODUCT_CODE,PRODUCT_NAME ");

        strBuff.append("  FROM PRODUCTS ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("oadProductNameListForBatchAdd", "QUERY sqlSelect=" + sqlSelect);
        }
        final HashMap productName = new HashMap();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);

            rs = pstmtSelect.executeQuery();
            final CategoryVO categoryVO = null;
            while (rs.next()) {

                productName.put(rs.getString("PRODUCT_NAME"), rs.getString("PRODUCT_CODE"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: categoryList size =" + productName.size());
            }
        }
        return productName;

    }

    /**
     * Method :loadProductNameListForBatchAdd
     * This method load list of Categories on the basis of domain code
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            java.lang.String
     * @return HashMap
     * @throws BTSLBaseException
     * @author Gaurav pandey
     */
    public HashMap loadServiceNameListForBatchAdd(Connection p_con, String network_id, String module) throws BTSLBaseException {
        final String methodName = "loadServiceNameListForBatchAdd";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, p_con);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("   SELECT st.service_type,st.name  ");

        strBuff.append("FROM product_service_type_mapping ps,service_type st,network_services ns   ");
        strBuff.append("WHERE ps.service_type= st.service_type  ");
        strBuff.append("AND st.service_type = ns.service_type   ");
        strBuff.append(" AND ns.sender_network = ? AND ns.receiver_network = ? AND st.module = ? ORDER BY name  ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("oadProductNameListForBatchAdd", "QUERY sqlSelect=" + sqlSelect);
        }
        final HashMap ServiceName = new HashMap();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, network_id);
            pstmtSelect.setString(2, network_id);
            pstmtSelect.setString(3, module);
            rs = pstmtSelect.executeQuery();
            final CategoryVO categoryVO = null;
            while (rs.next()) {

                ServiceName.put(rs.getString("service_type"), rs.getString("name"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: categoryList size =" + ServiceName.size());
            }
        }
        return ServiceName;

    }

    public String loadProductCode(Connection p_con, String Set_id) {
        final String methodName = "loadProductCode";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, p_con);
        }
        
        String Product_id = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("    SELECT COMM_PROFILE_PRODUCTS_ID ");
        strBuff.append("  FROM COMMISSION_PROFILE_PRODUCTS WHERE COMM_PROFILE_SET_ID=? ");
        final String sqlSelect = strBuff.toString();
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, Set_id);
           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            if (rs.next()) {
                Product_id = rs.getString("COMM_PROFILE_PRODUCTS_ID");
            }
        }
        }catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());

        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: categoryList size =" + Product_id);
            }
        }
        return Product_id;

    }

    public String loadServiceType(Connection p_con, String Set_id) {
        final String methodName = "loadServiceType";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, p_con);
        }
        
        String service_id = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("  SELECT COMM_PROFILE_SERVICE_TYPE_ID  ");
        strBuff.append("  FROM COMM_PROFILE_SERVICE_TYPES WHERE COMM_PROFILE_SET_ID=? ");
        final String sqlSelect = strBuff.toString();
        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
           
            pstmtSelect.setString(1, Set_id);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                service_id = rs.getString("COMM_PROFILE_SERVICE_TYPE_ID");
            }
        }
        }catch (Exception ex) {
            _log.error("loadProductCode", "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());

        } finally {
        	
            if (_log.isDebugEnabled()) {
                _log.debug("loadProductCode", "Exiting: categoryList size =" + service_id);
            }
        }
        return service_id;

    }

    /**
     * 
     * @param p_con
     * @param batchModifyCommissionProfileVO
     * @param userVO
     * @param theForm
     * @return
     */
    public int addBatchDetails(Connection p_con, BatchModifyCommissionProfileVO batchModifyCommissionProfileVO, UserVO userVO, CommissionProfileForm theForm) {
        final String methodName = "addBatchDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, p_con);
        }
        
        int insertCount = 0;

        final StringBuilder strBuff = new StringBuilder();
        strBuff
            .append(" INSERT INTO batches (batch_id, batch_type, batch_size, batch_name, network_code, status, created_by, created_on, modified_by, modified_on,file_name)  ");
        strBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?)");
        final String sqlSelect = strBuff.toString();
        try (PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);){
            
            // batchID=operatorUtil.formatBatchesID(p_userVO.getNetworkID(),PretupsI.BULK_USR_ID_PREFIX,new

            pstmtSelect.setString(1, batchModifyCommissionProfileVO.getBatch_ID());
            pstmtSelect.setString(2, PretupsI.BATCH_COMM_PROFILE_TYPE);
            pstmtSelect.setInt(3, theForm.getLength());
            pstmtSelect.setString(4, batchModifyCommissionProfileVO.getBatch_name());
            pstmtSelect.setString(5, userVO.getNetworkID());
            pstmtSelect.setString(6, PretupsI.BATCH_COMM_PROFILE_STATUS);
            pstmtSelect.setString(7, userVO.getCreatedBy());
            pstmtSelect.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(userVO.getCreatedOn()));
            pstmtSelect.setString(9, userVO.getModifiedBy());
            pstmtSelect.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(userVO.getModifiedOn()));
            pstmtSelect.setString(11, theForm.getFileName());
            insertCount = pstmtSelect.executeUpdate();
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());

        } finally {

        	
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insert count=" + insertCount);
            }
        }
        return insertCount;

    }

    public HashMap loadProfileNameListForBatchModify(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadProfileNameListForBatchAdd";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_domainCode=" + p_con);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("  SELECT  COMM_PROFILE_SET_NAME,SHORT_CODE");

        strBuff.append(" FROM COMMISSION_PROFILE_SET ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final HashMap profileName = new HashMap();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);

            rs = pstmtSelect.executeQuery();
            final CategoryVO categoryVO = null;
            while (rs.next()) {

                profileName.put(rs.getString("COMM_PROFILE_SET_NAME"), rs.getString("SHORT_CODE"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCategoryList", "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: categoryList size =" + profileName.size());
            }
        }
        return profileName;
    }
    
    public List<OTFDetailsVO> loadProfileOtfDetailList(Connection p_con, String profiledetailId,String otfType,String commTYPE) throws BTSLBaseException {
        final String methodName = "loadAdditionalProfileOtfDetailList";
        LogFactory.printLog(methodName, "Entered OTF Type=" + otfType, _log);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final List<OTFDetailsVO> otfDetailList = new ArrayList();
        OTFDetailsVO otfdetails = null;
        try {

            final StringBuilder selectQueryBuff = new StringBuilder(" SELECT prfle_otf_detail_id,Otf_value,");
            selectQueryBuff.append(" OTF_Type,OTF_rate ");
            selectQueryBuff.append(" from profile_otf_details where profile_detail_id = ? AND COMM_TYPE=? ORDER BY length(otf_value),  otf_value asc ");
            
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);

            pstmtSelect.setString(1, profiledetailId);
            pstmtSelect.setString(2, commTYPE);
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
            	otfdetails = new OTFDetailsVO();
            	otfdetails.setOtfDetailID(rs.getString("prfle_otf_detail_id"));
            	
            	if(!BTSLUtil.isNullString(rs.getString("otf_type"))){
            		otfdetails.setOtfType(rs.getString("otf_type"));
                    }else{
                    	otfdetails.setOtfType("");
                    }
            	if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(otfType)&&!BTSLUtil.isNullString(rs.getString("OTF_value"))){
            	otfdetails.setOtfValue(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("OTF_value"))));
            	}else{
            		otfdetails.setOtfValue(rs.getString("OTF_value"));
            	}
            	if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(otfdetails.getOtfType())&&!BTSLUtil.isNullString(rs.getString("OTF_rate"))){
            		otfdetails.setOtfRate(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("OTF_rate"))));
                	}else{
                		otfdetails.setOtfRate(rs.getString("OTF_rate"));
                	}
            	otfDetailList.add(otfdetails);
            }

            return otfDetailList;
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadAdditionalProfileDetailList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("commissionProfileWebDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadAdditionalProfileDetailList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("commissionProfileWebDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
        	LogFactory.printLog(methodName, "Exiting OTFDetailsList Size: "+otfDetailList, _log);
        }// end of finally
    }
    
   
    
    

  

    /**added by akanksha for batch add/modify commission profile for
     * retrieving OTF details for target based commission for additional commission
     * @param pcon
     * @param pNetworkCode
     * @param pCategoryCode
     * @param pCurrentDate
     * @return Map
     */
    public Map loadAddcommOTFDetailForBatchModify(Connection pcon, String pNetworkCode, String pCategoryCode, Date pCurrentDate) {
        final String methodName = "loadAddcommOTFDetailForBatchModify";
        LogFactory.printLog(methodName,  "Entered p_networkCode=" + pNetworkCode, _log);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        /*String pNetworkCode = SqlParameterEncoder.encodeParams(NetworkCode);
        String pCategoryCode = SqlParameterEncoder.encodeParams(CategoryCode);*/
        List<OTFDetailsVO> list = null;
        Map<String, List<OTFDetailsVO>> data=null;
        long listSize=0;
        try {

        	data = new HashMap<>();
            final StringBuilder selectQueryBuff = new StringBuilder(" SELECT  CAST(otf.prfle_otf_detail_id  as INTEGER) as otf_detail_id,CAST(otf.PROFILE_DETAIL_ID as INTEGER) AS PROFILE_DETAIL_ID, otf.OTF_VALUE,otf.OTF_TYPE as type, otf.OTF_RATE, ac.OTF_APPLICABLE_FROM,ac.OTF_APPLICABLE_TO,ac.OTF_TIME_SLAB,ac.OTF_TYPE");
            selectQueryBuff.append(" FROM PROFILE_OTF_DETAILS otf, commission_profile_set cs,commission_profile_set_version cv,  ");
            selectQueryBuff.append("  COMM_PROFILE_SERVICE_TYPES cps,ADDNL_COMM_PROFILE_DETAILS ac ");
            selectQueryBuff.append(" WHERE cs.NETWORK_CODE=? AND cs.CATEGORY_CODE=? ");
            selectQueryBuff.append(" and otf.comm_type=? and otf.profile_detail_id=ac.ADDNL_COMM_PROFILE_DETAIL_ID ");
            selectQueryBuff.append(" AND (cv.applicable_from >=? OR cv.applicable_from =(SELECT MAX(cv2.applicable_from) from commission_profile_set_version cv2 WHERE cs.comm_profile_set_id = cv2.comm_profile_set_id ))");
			selectQueryBuff.append("  and cs.status <>?");
            selectQueryBuff.append(" AND cs.COMM_PROFILE_SET_ID=cps.COMM_PROFILE_SET_ID AND cv.COMM_PROFILE_SET_VERSION=cps.COMM_PROFILE_SET_VERSION ");
            selectQueryBuff.append(" AND cps.COMM_PROFILE_SERVICE_TYPE_ID=ac.COMM_PROFILE_SERVICE_TYPE_ID AND cs.COMM_PROFILE_SET_ID=cv.COMM_PROFILE_SET_ID ORDER BY CAST(otf.PROFILE_DETAIL_ID as INTEGER), CAST(otf.prfle_otf_detail_id  as INTEGER) ");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = pcon.prepareStatement(selectQuery);
            pstmtSelect.setString(1, pNetworkCode);
            pstmtSelect.setString(2, pCategoryCode);
            pstmtSelect.setString(3,PretupsI.COMM_TYPE_ADNLCOMM);
            pstmtSelect.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(pCurrentDate));
            pstmtSelect.setString(5, PretupsI.NO);
            rs = pstmtSelect.executeQuery();
            if (rs != null) {
                OTFDetailsVO otfDeatilsVO = null;
            	while (rs.next()) {
            	list = new ArrayList();
                otfDeatilsVO = new OTFDetailsVO();
                otfDeatilsVO.setOtfDetailID(SqlParameterEncoder.encodeParams(rs.getString("otf_detail_id")));
                otfDeatilsVO.setOtfType(SqlParameterEncoder.encodeParams(rs.getString("type")));
                if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(otfDeatilsVO.getOtfType())){
                	otfDeatilsVO.setOtfRate(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("OTF_RATE"))));
                }
                else{
                	otfDeatilsVO.setOtfRate(SqlParameterEncoder.encodeParams(rs.getString("OTF_RATE")));
                }
                otfDeatilsVO.setOtfCountOrAmount(SqlParameterEncoder.encodeParams(rs.getString("OTF_TYPE")));
                if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(otfDeatilsVO.getOtfCountOrAmount())){
                	otfDeatilsVO.setOtfValue(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("OTF_VALUE"))));
                }
                else{
                	otfDeatilsVO.setOtfValue(SqlParameterEncoder.encodeParams(rs.getString("OTF_VALUE")));
                }
                otfDeatilsVO.setOtfApplicableFrom(rs.getDate("OTF_APPLICABLE_FROM"));
                otfDeatilsVO.setOtfApplicableTo(rs.getDate("OTF_APPLICABLE_TO"));
                otfDeatilsVO.setOtfTimeSlab(SqlParameterEncoder.encodeParams(rs.getString("OTF_TIME_SLAB")));
                list.add(otfDeatilsVO);
                if(data.containsKey(rs.getString("PROFILE_DETAIL_ID")+"_"+PretupsI.COMM_TYPE_ADNLCOMM)){
                    List<OTFDetailsVO> lista = data.get(SqlParameterEncoder.encodeParams(rs.getString("PROFILE_DETAIL_ID"))+"_"+PretupsI.COMM_TYPE_ADNLCOMM);
                    lista.add(otfDeatilsVO);
                    data.put(SqlParameterEncoder.encodeParams(rs.getString("PROFILE_DETAIL_ID"))+"_"+PretupsI.COMM_TYPE_ADNLCOMM,lista);
             }else{
                    data.put(SqlParameterEncoder.encodeParams(rs.getString("PROFILE_DETAIL_ID"))+"_"+PretupsI.COMM_TYPE_ADNLCOMM, list);
             }
            }
            	if(list!=null){
            		listSize=list.size();
            	}
            }
        }// end of try

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommProfileSetLatestVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());

        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommProfileSetLatestVersion]", "", "", "", "Exception:" + e.getMessage());

        }// end of catch
        finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            LogFactory.printLog(methodName,  "Exiting list size:" +listSize, _log);
        }// end of finally
        return data;
    }
    
    
    /**added by akanksha for batch add/modify commission profile for
     * retrieving OTF details for target based commission for base commission
     * @param pcon
     * @param pNetworkCode
     * @param pCategoryCode
     * @param pCurrentDate
     * @return Map
     */
    public Map loadBaseCommOTFDetailForBatchModify(Connection pcon, String pNetworkCode, String pCategoryCode, Date pCurrentDate) {
        final String methodName = "loadBaseCommOTFDetailForBatchModify";
        LogFactory.printLog(methodName,  "Entered p_networkCode=" + pNetworkCode, _log);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;

        List<OTFDetailsVO> list = null;
        Map<String, List<OTFDetailsVO>> data=null;
        long listSize=0;
        try {

        	data = new HashMap<>();
            final StringBuilder selectQueryBuff = new StringBuilder(" SELECT  CAST(otf.prfle_otf_detail_id  as INTEGER) as otf_detail_id ,CAST(otf.PROFILE_DETAIL_ID as INTEGER) as PROFILE_DETAIL_ID, otf.OTF_VALUE,otf.OTF_TYPE as type, ");
        	selectQueryBuff.append(" otf.OTF_RATE, bc.OTF_APPLICABLE_FROM, bc.OTF_APPLICABLE_TO, bc.OTF_TIME_SLAB");
        	selectQueryBuff.append(" FROM PROFILE_OTF_DETAILS otf,  commission_profile_set cs, commission_profile_set_version cv, ");
        	selectQueryBuff.append(" COMMISSION_PROFILE_PRODUCTS cp, COMMISSION_PROFILE_DETAILS bc");
        	selectQueryBuff.append("  WHERE cs.network_code =? AND cs.category_code = ? and otf.comm_type=? and otf.profile_detail_id=bc.COMM_PROFILE_DETAIL_ID");
            selectQueryBuff.append(" AND cs.comm_profile_set_id = cv.comm_profile_set_id AND cp.COMM_PROFILE_SET_VERSION = cv.COMM_PROFILE_SET_VERSION");
            selectQueryBuff.append(" AND(cv.applicable_from >=? OR cv.applicable_from =(SELECT MAX(cv2.applicable_from) ");
            selectQueryBuff.append(" from commission_profile_set_version cv2 WHERE cs.comm_profile_set_id = cv2.comm_profile_set_id ))");
			selectQueryBuff.append("  AND cs.status <> ? AND cs.COMM_PROFILE_SET_ID=cp.COMM_PROFILE_SET_ID AND cp.COMM_PROFILE_PRODUCTS_ID=bc.COMM_PROFILE_PRODUCTS_ID");
            selectQueryBuff.append(" ORDER BY CAST(otf.PROFILE_DETAIL_ID as INTEGER), CAST(otf.prfle_otf_detail_id  as INTEGER) ");
            final String selectQuery = selectQueryBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = pcon.prepareStatement(selectQuery);
            pstmtSelect.setString(1, pNetworkCode);
            pstmtSelect.setString(2, pCategoryCode);
            pstmtSelect.setString(3,PretupsI.COMM_TYPE_BASECOMM);
            pstmtSelect.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(pCurrentDate));
            pstmtSelect.setString(5, PretupsI.NO);
            rs = pstmtSelect.executeQuery();
            if (rs != null) {
                OTFDetailsVO otfDeatilsVO = null;
            	while (rs.next()) {
            	list = new ArrayList();
                otfDeatilsVO = new OTFDetailsVO();
                otfDeatilsVO.setOtfDetailID(rs.getString("otf_detail_id"));
                otfDeatilsVO.setOtfType(rs.getString("type"));
                if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(otfDeatilsVO.getOtfType())){
                	otfDeatilsVO.setOtfRate(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("OTF_RATE"))));
                }
                else{
                	otfDeatilsVO.setOtfRate(rs.getString("OTF_RATE"));
                }
                otfDeatilsVO.setOtfValue(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("OTF_VALUE"))));
                otfDeatilsVO.setOtfApplicableFrom(rs.getDate("OTF_APPLICABLE_FROM"));
                otfDeatilsVO.setOtfApplicableTo(rs.getDate("OTF_APPLICABLE_TO"));
                otfDeatilsVO.setOtfTimeSlab(rs.getString("OTF_TIME_SLAB"));
                list.add(otfDeatilsVO);
                if(data.containsKey(rs.getString("PROFILE_DETAIL_ID")+"_"+PretupsI.COMM_TYPE_BASECOMM)){
                    List<OTFDetailsVO> lista = data.get(rs.getString("PROFILE_DETAIL_ID")+"_"+PretupsI.COMM_TYPE_BASECOMM);
                    lista.add(otfDeatilsVO);
                    data.put(rs.getString("PROFILE_DETAIL_ID")+"_"+PretupsI.COMM_TYPE_BASECOMM,lista);
             }else{
                    data.put(rs.getString("PROFILE_DETAIL_ID")+"_"+PretupsI.COMM_TYPE_BASECOMM, list);
             }
            }
            	if(list!=null){
            		listSize=list.size();
            	}
            }
        }// end of try

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommProfileSetLatestVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());

        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "commissionProfileWebDAO[loadCommProfileSetLatestVersion]", "", "", "", "Exception:" + e.getMessage());

        }// end of catch
        finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            LogFactory.printLog(methodName,  "Exiting list size:" +listSize, _log);

        }// end of finally
        return data;
    }

    
    
    /**
     * @param pCon
     * @param batchModifyCommissionProfileVO
     * @param userVO
     * @param theForm
     * @return
     */
    public UserVO loadUserIdByMsisdn(Connection pCon,CommissionProfileForm theForm) {
        final String methodName = "loadUserIdByMsisdn";
        LogFactory.printLog(methodName, " Entered :", _log);
        PreparedStatement pstmtSelect = null;
        UserVO userVO=null;
        int userCount=0;
        final StringBuilder strBuff = new StringBuilder();
        strBuff
            .append(" Select user_id,category_code from users where msisdn=? and network_code=? ");
        strBuff.append(" and user_type=? and status <> ? and status <> ? ");
        final String sqlSelect = strBuff.toString();
        ResultSet rs = null;
        try {
        	pstmtSelect = pCon.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, theForm.getSearchMsisdn());
            pstmtSelect.setString(2, theForm.getNetworkCode());
            pstmtSelect.setString(3, PretupsI.USER_TYPE_CHANNEL);
            pstmtSelect.setString(4, PretupsI.USER_STATUS_DELETED);
            pstmtSelect.setString(5, PretupsI.USER_STATUS_CANCELED);
            rs=pstmtSelect.executeQuery();
            
            if(rs.next()){
            	userVO=new UserVO();
            	userVO.setUserID(rs.getString("user_id"));
            	userVO.setCategoryCode(rs.getString("category_code"));
            	
            	userCount++;
            }
            
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileWebDAO[loadUserIdByMsisdn]", "", "", "",
                "Exception:" + ex.getMessage());

        } finally {
        	try{
            	if (rs!= null){
            		rs.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            LogFactory.printLog(methodName, "Exiting: select count=" + userCount, _log);
        }
        return userVO;

    }
    
    
    
    /**
     * @param pCon
     * @param batchModifyCommissionProfileVO
     * @param userVO
     * @param theForm
     * @return
     */
    public UserVO loadUserIdByLoginID(Connection pCon,CommissionProfileForm theForm) {
        final String methodName = "loadUserIdByLoginID";
        LogFactory.printLog(methodName, " Entered ", _log);
        PreparedStatement pstmtSelect = null;
        String userID=null;
        int userCount=0;
        final StringBuilder strBuff = new StringBuilder();
        strBuff
            .append(" Select user_id,category_code from users where login_id=? and network_code=? ");
        strBuff.append(" and user_type=? and status <> ? and status <> ? ");
        final String sqlSelect = strBuff.toString();
        ResultSet rs = null;
        UserVO userVO=null;
        try {
        	pstmtSelect = pCon.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, theForm.getSearchLoginId());
            pstmtSelect.setString(2, theForm.getNetworkCode());
            pstmtSelect.setString(3, PretupsI.USER_TYPE_CHANNEL);
            pstmtSelect.setString(4, PretupsI.USER_STATUS_DELETED);
            pstmtSelect.setString(5, PretupsI.USER_STATUS_CANCELED);
            rs=pstmtSelect.executeQuery();
            
            if(rs.next()){
            	userVO=new UserVO();
            	userVO.setUserID(rs.getString("user_id"));
            	userVO.setCategoryCode(rs.getString("category_code"));
            	userCount++;
            }
            
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileWebDAO[loadUserIdByLoginID]", "", "", "",
                "Exception:" + ex.getMessage());

        } finally {
        	try{
            	if (rs!= null){
            		rs.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            LogFactory.printLog(methodName, "Exiting: select count=" + userCount, _log);
        }
        return userVO;

    }
    
    
    
    /**Load user's setID
     * @param pcon
     * @param userID
     * @return
     */
    public CommissionProfileSetVO loadUserCommSetID(Connection pcon,String userID) {
        final String methodName = "loadUserCommSetID";
        LogFactory.printLog(methodName, " Entered ", _log);
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelectSet = null;
        ResultSet rs = null;
        ResultSet rsSet = null;
        CommissionProfileSetVO commissionProfileSetVO=null;
        String setID=null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff
            .append(" Select COMM_PROFILE_SET_ID from channel_users where user_id=? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("Select Commission ID", "QUERY sqlSelect=" + sqlSelect);
        }
        
        final StringBuilder strBuffSet = new StringBuilder();
        strBuffSet.append(" Select COMM_PROFILE_SET_NAME, SHORT_CODE from COMMISSION_PROFILE_SET ");
        strBuffSet.append(" where COMM_PROFILE_SET_ID=?");
        final String sqlSelectSet = strBuffSet.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("Commission detail: ", "QUERY sqlSelect=" + sqlSelectSet);
        }
        
        try {
        	pstmtSelect = pcon.prepareStatement(sqlSelect);
        	pstmtSelectSet=pcon.prepareStatement(sqlSelectSet);
            pstmtSelect.setString(1,userID);
            rs=pstmtSelect.executeQuery();
           
            if(rs.next()){
            	commissionProfileSetVO=new CommissionProfileSetVO();
            	commissionProfileSetVO.setCommProfileSetId(rs.getString("COMM_PROFILE_SET_ID"));
            	
            	pstmtSelectSet.setString(1, commissionProfileSetVO.getCommProfileSetId());
            	rsSet=pstmtSelectSet.executeQuery();
            	if(rsSet.next()){
            	commissionProfileSetVO.setCommProfileSetName(SqlParameterEncoder.encodeParams(rsSet.getString("COMM_PROFILE_SET_NAME")));
            	commissionProfileSetVO.setShortCode(SqlParameterEncoder.encodeParams(rsSet.getString("SHORT_CODE")));
            	}
            	}
            
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileWebDAO[loadUserCommSetID]", "", "", "",
                "Exception:" + ex.getMessage());

        } finally {
        	try{
            	if (rs!= null){
            		rs.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
        	try{
            	if (rsSet!= null){
            		rsSet.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
        	try{
        		if (pstmtSelect!= null){
        			pstmtSelect.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
        		if (pstmtSelectSet!= null){
        			pstmtSelectSet.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
            LogFactory.printLog(methodName, " Exiting ", _log);
        }
        return commissionProfileSetVO;

    }
    
    /**
     * Method loadCommProfileSetLatestVersion.
     * 
     * @param pCon
     *            Connection
     * @param pCommissionSetID
     *            String
     * @param pApplicableDate
     *            Date
     * @return String
     * @throws BTSLBaseException
     */
    public CommissionProfileSetVersionVO loadCommProfileSetLatestVersionDetails(Connection pCon, String pCommissionSetID, Date pApplicableDate) throws BTSLBaseException {
        final String methodName = "loadCommProfileSetLatestVersion";
        LogFactory.printLog(methodName, "Entered p_commissionSetID=" + pCommissionSetID + " p_applicableDate=" + pApplicableDate, _log);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String latestCommProfileVersion = null;
        try {
        	CommissionProfileSetVersionVO commissionProfileSetVersionVO=null;
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT dual_comm_type,comm_profile_set_version,APPLICABLE_FROM ");
            selectQueryBuff.append(" FROM commission_profile_set_version cgd ");
            selectQueryBuff.append(" WHERE cgd.comm_profile_set_id=? ");
            selectQueryBuff.append(" AND applicable_from =(SELECT MAX(applicable_from) ");
            selectQueryBuff.append(" FROM commission_profile_set_version ");
            selectQueryBuff.append(" WHERE applicable_from<=? AND comm_profile_set_id=cgd.comm_profile_set_id) ");
            String selectQuery = selectQueryBuff.toString();
            LogFactory.printLog(methodName, "select query:" + selectQuery, _log);
            pstmtSelect = pCon.prepareStatement(selectQuery);
            pstmtSelect.setString(1, pCommissionSetID);
            pstmtSelect.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(pApplicableDate));
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
            	commissionProfileSetVersionVO= new CommissionProfileSetVersionVO();
            	commissionProfileSetVersionVO.setCommProfileSetVersion(rs.getString("comm_profile_set_version"));
            	commissionProfileSetVersionVO.setApplicableFrom(rs.getTimestamp("APPLICABLE_FROM"));
            	commissionProfileSetVersionVO.setDualCommissionType(rs.getString("dual_comm_type"));
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED);
            }
            return commissionProfileSetVersionVO;
        }// end of try
        catch (BTSLBaseException bex) {
            _log.error(methodName, "BTSLBaseException " + bex.getMessage());
            throw bex;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileWebDAO[loadCommProfileSetLatestVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadCardGroupSetVersionLatestVersion", PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileWebDAO[loadCommProfileSetLatestVersion]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }// end of catch
        finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		_log.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            LogFactory.printLog(methodName, "Exiting latestCommProfileVersion:" + latestCommProfileVersion,_log);
        }// end of finally
    }
    
    
    /**
     * @param pcon
     * @param categoryCode
     * @return
     */
    public String serviceAllowed(Connection pcon,String categoryCode) {
        final String methodName = "serviceAllowed";
        LogFactory.printLog(methodName, " Entered categoryCode" +categoryCode, _log);
        
        String serviceAllowed=null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff
            .append(" Select SERVICES_ALLOWED from CATEGORIES where CATEGORY_CODE=? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("Select Commission ID", "QUERY sqlSelect=" + sqlSelect);
        }
        
       
        try (PreparedStatement pstmtSelect = pcon.prepareStatement(sqlSelect);){
        	
            pstmtSelect.setString(1,categoryCode);
            try(ResultSet rs=pstmtSelect.executeQuery();){
           
            if(rs.next()){
            	serviceAllowed=SqlParameterEncoder.encodeParams(rs.getString("SERVICES_ALLOWED"));
            }
            
        } 
        }catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CommissionProfileWebDAO[serviceAllowed]", "", "", "",
                "Exception:" + ex.getMessage());

        } finally {
        	
            LogFactory.printLog(methodName, " Exiting ", _log);
        }
        return serviceAllowed;

    }
   
    
 /**added by simarnoor.bains for batch add/modify commission profile for
  * retrieving OTF details for target based commission for base commission
  * @param pcon
  * @param pNetworkCode
  * @param pCategoryCode
  * @param pCurrentDate
  * @return Map
  */
 public Map loadOTFDetailForBatchModify(Connection pcon, String pNetworkCode, String pCategoryCode, Date pCurrentDate) {
     final String methodName = "loadBaseCommOTFDetailForBatchModify";
    LogFactory.printLog(methodName,  "Entered p_networkCode=" + pNetworkCode, _log);
     PreparedStatement pstmtSelect = null;
     ResultSet rs = null;
     /* String pNetworkCode = SqlParameterEncoder.encodeParams(NetworkCode);
     String pCategoryCode = SqlParameterEncoder.encodeParams(CategoryCode);*/
     List<OTFDetailsVO> list = null;
     Map<String, List<OTFDetailsVO>> data=null;
     try {

     	data = new HashMap<>();
        final StringBuilder selectQueryBuff = new StringBuilder("SELECT ctf.COMM_PROFILE_OTF_ID, ctf.PRODUCT_CODE,CAST(otf.PROFILE_DETAIL_ID as INTEGER) as PROFILE_DETAIL_ID, ");
     	selectQueryBuff.append(" ctf.COMM_PROFILE_SET_ID, ctf.COMM_PROFILE_SET_VERSION,  ctf.OTF_APPLICABLE_FROM, ");
     	selectQueryBuff.append(" ctf.OTF_APPLICABLE_TO, ctf.OTF_TIME_SLAB,otf.OTF_VALUE, ");
     	selectQueryBuff.append(" otf.OTF_TYPE,otf.OTF_RATE ");
     	selectQueryBuff.append(" FROM PROFILE_OTF_DETAILS otf,COMMISSION_PROFILE_OTF ctf");
     	selectQueryBuff.append(" WHERE otf.PROFILE_DETAIL_ID= ctf.COMM_PROFILE_OTF_ID");
         final String selectQuery = selectQueryBuff.toString();
         if (_log.isDebugEnabled()) {
             _log.debug(methodName, "select query:" + selectQuery);
         }
         pstmtSelect = pcon.prepareStatement(selectQuery);
         rs = pstmtSelect.executeQuery();
         if (rs != null) {
        	 OTFDetailsVO otfDeatilsVO = null;
        	 while (rs.next()) {
        		 otfDeatilsVO = new OTFDetailsVO();
        		 otfDeatilsVO.setOtfProfileID(SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_OTF_ID")));
        		 otfDeatilsVO.setOtfDetailID(SqlParameterEncoder.encodeParams(rs.getString("PROFILE_DETAIL_ID")));
        		 otfDeatilsVO.setCommProfileSetId(SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_SET_ID")));
        		 otfDeatilsVO.setSetVersion(SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_SET_VERSION")));
        		 otfDeatilsVO.setProductCode(SqlParameterEncoder.encodeParams(rs.getString("PRODUCT_CODE")));
        		 otfDeatilsVO.setOtfType(SqlParameterEncoder.encodeParams(rs.getString("OTF_TYPE")));
        		 if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(otfDeatilsVO.getOtfType())){
        			 otfDeatilsVO.setOtfRate(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("OTF_RATE"))));
        		 }
        		 else{
        			 otfDeatilsVO.setOtfRate(SqlParameterEncoder.encodeParams(rs.getString("OTF_RATE")));
        		 }
        		 otfDeatilsVO.setOtfValue(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("OTF_VALUE"))));
        		 otfDeatilsVO.setOtfApplicableFrom(rs.getDate("OTF_APPLICABLE_FROM"));
        		 otfDeatilsVO.setOtfApplicableTo(rs.getDate("OTF_APPLICABLE_TO"));
        		 otfDeatilsVO.setOtfTimeSlab(SqlParameterEncoder.encodeParams(rs.getString("OTF_TIME_SLAB")));
        		 if(data.containsKey(SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_SET_ID"))+"_"+SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_SET_VERSION"))+"_"+SqlParameterEncoder.encodeParams(rs.getString("PRODUCT_CODE"))+"_"+PretupsI.COMM_TYPE_BASECOMM)){
        			 List<OTFDetailsVO> lista = data.get(SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_SET_ID"))+"_"+SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_SET_VERSION"))+"_"+SqlParameterEncoder.encodeParams(rs.getString("PRODUCT_CODE"))+
        			 "_"+PretupsI.COMM_TYPE_BASECOMM);
        			 lista.add(otfDeatilsVO);
        			 //data.put(rs.getString("COMM_PROFILE_SET_ID")+"_"+rs.getString("COMM_PROFILE_SET_VERSION")+"_"+rs.getString("PRODUCT_CODE")+"_"+PretupsI.COMM_TYPE_BASECOMM,lista);
        		 }else{
        			 list = new ArrayList();
        			 list.add(otfDeatilsVO);
        			 data.put(SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_SET_ID"))
        					 +"_"+SqlParameterEncoder.encodeParams(rs.getString("COMM_PROFILE_SET_VERSION"))
        					 +"_"+SqlParameterEncoder.encodeParams(rs.getString("PRODUCT_CODE"))+"_"+PretupsI.COMM_TYPE_BASECOMM, list);
        		 }
        	 }
         }
     }// end of try

     catch (SQLException sqle) {
         _log.error(methodName, "SQLException " + sqle.getMessage());
         _log.errorTrace(methodName, sqle);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
             "commissionProfileWebDAO[loadCommProfileSetLatestVersion]", "", "", "", "SQL Exception:" + sqle.getMessage());

     }// end of catch
     catch (Exception e) {
         _log.error(methodName, "Exception " + e.getMessage());
         _log.errorTrace(methodName, e);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
             "commissionProfileWebDAO[loadCommProfileSetLatestVersion]", "", "", "", "Exception:" + e.getMessage());

     }// end of catch
     finally {
    	 try{
     		if (rs!= null){
     			rs.close();
     		}
     	}
     	catch (SQLException e){
     		_log.error("An error occurred closing result set.", e);
     	}
    	 try{
         	if (pstmtSelect!= null){
         		pstmtSelect.close();
         	}
         }
         catch (SQLException e){
         	_log.error("An error occurred closing statement.", e);
         }
         LogFactory.printLog(methodName,  "Exiting list size:", _log);

     }// end of finally
     return data;
 }
 
 
 
 
 
 /**
  * This method loads the Commission profile Details on the basis of
  * Comm_profile_product_id
  * 
  * @param p_con
  * @param p_commProfileProductId
  *            String
  * 
  * @return commissionprofileDetailList ArrayList
  * @throws BTSLBaseException
  */
 public List<CommissionSlabDetails>  getCommissionProfileDetailList(Connection p_con, String p_commProfileProductId,String networkCode) throws BTSLBaseException {
     final String methodName = "getCommissionProfileDetailList";
     if (_log.isDebugEnabled()) {
         _log.debug(methodName, "Entered p_commProfileProductId=" + p_commProfileProductId);
     }
     
     List commisiondetaillist = new ArrayList();
     CommissionSlabDetails commissionSlabDetails = null;
     try {

     	final StringBuilder selectQueryBuff = new StringBuilder(" SELECT comm_profile_detail_id,comm_profile_products_id,");
     	selectQueryBuff.append(" start_range,end_range,commission_type,commission_rate,");
     	selectQueryBuff.append(" tax1_type, tax1_rate, tax2_type, tax2_rate,tax3_type,tax3_rate ");
     	selectQueryBuff.append(" FROM commission_profile_details WHERE comm_profile_products_id = ? ");

     	final String selectQuery = selectQueryBuff.toString();
     	if (_log.isDebugEnabled()) {
     		_log.debug(methodName, "select query:" + selectQuery);
     	}
     	try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
     	{
     		pstmtSelect.setString(1, p_commProfileProductId);

     		try(ResultSet rs = pstmtSelect.executeQuery();)
     		{
     			while (rs.next()) {
     				commissionSlabDetails = new CommissionSlabDetails();
     				//commissionSlabDetails.setCommProfileDetailID(rs.getString("comm_profile_detail_id"));
     				//commissionProfileDeatilsVO.setCommProfileProductsID(rs.getString("comm_profile_products_id"));
     				commissionSlabDetails.setFromRange(PretupsBL.getDisplayAmount(rs.getLong("start_range")));
     				commissionSlabDetails.setToRange(PretupsBL.getDisplayAmount(rs.getLong("end_range")));
     				commissionSlabDetails.setCommission(selectQuery);
     				
     				
     			//	commissionProfileDeatilsVO.setCommRate(rs.getDouble("commission_rate"));
     				if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(rs.getString("commission_type"))) {
     					commissionSlabDetails.setCommission(PretupsBL.getDisplayAmount(rs.getLong("commission_rate")));
     					commissionSlabDetails.setCommissionType(rs.getString("commission_type"));
     				} else {
     					commissionSlabDetails.setCommission(String.valueOf(rs.getLong("commission_rate")));
     					commissionSlabDetails.setCommissionType(rs.getString("commission_type"));
     				}
     //				commissionProfileDeatilsVO.setTax1Type(rs.getString("tax1_type"));
     //				commissionProfileDeatilsVO.setTax1Rate(rs.getDouble("tax1_rate"));
     				if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(rs.getString("tax1_type"))) {
     					commissionSlabDetails.setTax1(PretupsBL.getDisplayAmount(rs.getLong("tax1_rate")));
     					commissionSlabDetails.setTax1Type(rs.getString("tax1_type"));
     				} else {
     					commissionSlabDetails.setTax1(String.valueOf(rs.getLong("tax1_rate")));
     					commissionSlabDetails.setTax1Type(rs.getString("tax1_type"));
     				}
     				
     				if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(rs.getString("tax2_type"))) {
     					commissionSlabDetails.setTax2(PretupsBL.getDisplayAmount(rs.getLong("tax2_rate")));
     					commissionSlabDetails.setTax2Type(rs.getString("tax2_type"));
     				} else {
     					commissionSlabDetails.setTax2(String.valueOf(rs.getLong("tax2_rate")));
     					commissionSlabDetails.setTax2Type(rs.getString("tax2_type"));
     				}
     				
     				if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(rs.getString("tax3_type"))) {
     					commissionSlabDetails.setTax3(PretupsBL.getDisplayAmount(rs.getLong("tax3_rate")));
     					commissionSlabDetails.setTax3Type(rs.getString("tax3_type"));
     				} else {
     					commissionSlabDetails.setTax3(String.valueOf(rs.getLong("tax3_rate")));
     					commissionSlabDetails.setTax3Type(rs.getString("tax3_type"));
     				}
     			
     				commisiondetaillist.add(commissionSlabDetails);
     			}

     			return commisiondetaillist;
     		}
     	}
     }// end of try
     catch (SQLException sqle) {
         _log.error(methodName, "SQLException " + sqle.getMessage());
         _log.errorTrace(methodName, sqle);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
             "commissionProfileWebDAO[getCommissionProfileDetailList]", "", "", "", "SQL Exception:" + sqle.getMessage());
         throw new BTSLBaseException("commissionProfileWebDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
     }// end of catch
     catch (Exception e) {
         _log.error(methodName, "Exception " + e.getMessage());
         _log.errorTrace(methodName, e);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
             "commissionProfileWebDAO[getCommissionProfileDetailList]", "", "", "", "Exception:" + e.getMessage());
         throw new BTSLBaseException("commissionProfileWebDAO", methodName, PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
     }// end of catch
     finally {
     	
         if (_log.isDebugEnabled()) {
             _log.debug(methodName, "Exiting commissionDetailList Size:" + commisiondetaillist.size());
         }
     }// end of finally
 }
 
 
 
 
 /**
  * This method gets the Additional profile Details on the basis of
  * comm_profile_set_id
  * 
  * @param p_con
  * @param p_commProfileServiceTypeID
  *            String
  * 
  * @return java.util.ArrayList
  * @throws BTSLBaseException
  */
 public ArrayList<AdditionalcommSlabDetails> getAdditionalProfileDetailList(Connection p_con, String p_commProfileServiceTypeID,String networkCode) throws BTSLBaseException {
     final String methodName = "getAdditionalProfileDetailList";
     if (_log.isDebugEnabled()) {
         _log.debug(methodName, "Entered p_commProfileServiceTypeID=" + p_commProfileServiceTypeID);
     }
     Boolean isOwnerCommissionAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED);
     final ArrayList addCommissionDetailList = new ArrayList();
     AdditionalcommSlabDetails additionalcommSlabDetails = null;
     try {

         final StringBuilder selectQueryBuff = new StringBuilder(" SELECT adc.addnl_comm_profile_detail_id,");
         selectQueryBuff.append(" adc.comm_profile_service_type_id,adc.start_range,end_range,");
         selectQueryBuff.append(" adc.addnl_comm_type,adc.addnl_comm_rate,adc.diffrential_factor,");
         selectQueryBuff
             .append(" adc.tax1_type, adc.tax1_rate, adc.tax2_type, adc.tax2_rate, adc.status,adc.roam_addnl_comm_type,adc.roam_addnl_com_rate, lk.lookup_name status_name");
         if (isOwnerCommissionAllowed) {
				selectQueryBuff.append(" ,adc.own_addnl_comm_type ,adc.own_addnl_comm_rate,adc.own_tax1_type,adc.own_tax1_rate,adc.own_tax2_type,adc.own_tax2_rate");
			}   
         if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, networkCode)){
				selectQueryBuff.append(" ,adc.otf_type, adc.otf_applicable_from, adc.otf_applicable_to,adc.OTF_TIME_SLAB ");
			} 
         selectQueryBuff.append(" FROM addnl_comm_profile_details adc,lookups lk ");
         selectQueryBuff.append(" WHERE adc.comm_profile_service_type_id = ? AND adc.status=lk.lookup_code AND lk.lookup_type='URTYP' AND adc.status IN ('Y','S')");

         final String selectQuery = selectQueryBuff.toString();
         if (_log.isDebugEnabled()) {
             _log.debug(methodName, "select query:" + selectQuery);
         }
         try(PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);)
         {
         pstmtSelect.setString(1, p_commProfileServiceTypeID);

         try(ResultSet rs = pstmtSelect.executeQuery();)
         {
         while (rs.next()) {
        	 additionalcommSlabDetails = new AdditionalcommSlabDetails();
        	 additionalcommSlabDetails.setAddtnlCommProfileDetailID(rs.getString("addnl_comm_profile_detail_id"));
        	 additionalcommSlabDetails.setCommProfileServiceTypeId(rs.getString("comm_profile_service_type_id"));
        	 additionalcommSlabDetails.setCommStartRange(PretupsBL.getDisplayAmount(rs.getLong("start_range")));
        	 additionalcommSlabDetails.setCommToRange(PretupsBL.getDisplayAmount(rs.getLong("end_range")));
        	 additionalcommSlabDetails.setCommissionType(rs.getString("addnl_comm_type"));
        	 additionalcommSlabDetails.setCommisionRate(String.valueOf( rs.getDouble("addnl_comm_rate")));
             if(!BTSLUtil.isNullString(rs.getString("roam_addnl_comm_type"))){
            	 additionalcommSlabDetails.setRoamAdditionalCommType(rs.getString("roam_addnl_comm_type"));
             }else{
            	 additionalcommSlabDetails.setRoamAdditionalCommType("");
             }
             
             additionalcommSlabDetails.setRoamAdditionalCommRate(String.valueOf(rs.getDouble("roam_addnl_com_rate")));

             if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalcommSlabDetails.getCommissionType())) {
            	 additionalcommSlabDetails.setRoamAdditionalCommRate(PretupsBL.getDisplayAmount(rs.getLong("addnl_comm_rate")));
             }

             else {
            	 additionalcommSlabDetails.setRoamAdditionalCommRate(String.valueOf(additionalcommSlabDetails.getRoamAdditionalCommRate()));
             }
             // added for roam recharge

             if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalcommSlabDetails.getRoamAdditionalCommType())) {
            	 additionalcommSlabDetails.setRoamAdditionalCommRate(PretupsBL.getDisplayAmount(rs.getLong("roam_addnl_com_rate")));
             } else {
                 additionalcommSlabDetails.setRoamAdditionalCommRate(additionalcommSlabDetails.getRoamAdditionalCommRate());
             }

            
             
             additionalcommSlabDetails.setCommisionDiffFactor(String.valueOf(rs.getDouble("diffrential_factor")));
             if(!BTSLUtil.isNullString(rs.getString("tax1_type"))){
            	 additionalcommSlabDetails.setTax1Type(rs.getString("tax1_type"));
             }else{
            	 additionalcommSlabDetails.setTax1Type("");
             }
             
             if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalcommSlabDetails.getTax1Type())) {
            	 additionalcommSlabDetails.setTax1Rate(PretupsBL.getDisplayAmount(rs.getLong("tax1_rate")));
             } else {
            	 additionalcommSlabDetails.setTax1Rate((String.valueOf(rs.getLong("tax1_rate")+"")));
             }
             
             if(!BTSLUtil.isNullString(rs.getString("tax2_type"))){
            	 additionalcommSlabDetails.setTax2Type(rs.getString("tax2_type"));
                 }else{
                	 additionalcommSlabDetails.setTax2Type("");
                 }
             
             
             if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalcommSlabDetails.getTax2Type())) {
            	 additionalcommSlabDetails.setTax2Rate(PretupsBL.getDisplayAmount(rs.getLong("tax2_rate")));
             } else {
            	 additionalcommSlabDetails.setTax2Rate(rs.getDouble("tax2_rate")+"");
             }

             //additionalcommSlabDetails.setAddtnlComStatus(rs.getString("status"));
             additionalcommSlabDetails.setStatus(rs.getString("status_name"));
             if (isOwnerCommissionAllowed) 
			    {
             	 if(!BTSLUtil.isNullString(rs.getString("OWN_ADDNL_COMM_TYPE"))){
             		additionalcommSlabDetails.setOwnerAddnlCommType(rs.getString("OWN_ADDNL_COMM_TYPE"));
                      }else{
                    	  additionalcommSlabDetails.setOwnerAddnlCommType("");
                      }
			    	if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalcommSlabDetails.getOwnerAddnlCommType())) {
			    		additionalcommSlabDetails.setOwnerAddnlCommRate(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("OWN_ADDNL_COMM_RATE"))));
             	} else {
             		additionalcommSlabDetails.setOwnerAddnlCommRate(String.valueOf(rs.getDouble("OWN_ADDNL_COMM_RATE")+""));
             	}
			    	if(!BTSLUtil.isNullString(rs.getString("OWN_TAX1_TYPE"))){
			    		additionalcommSlabDetails.setOwnerTax1Type(rs.getString("OWN_TAX1_TYPE"));
                     }else{
                    	 additionalcommSlabDetails.setOwnerTax1Type("");                     }
			    	if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalcommSlabDetails.getOwnerAddnlCommType())) {
			    		additionalcommSlabDetails.setOwnerTax1Rate(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("OWN_TAX1_RATE"))));
             	} else {
             		additionalcommSlabDetails.setOwnerTax1Rate(String.valueOf(rs.getDouble("OWN_TAX1_RATE") +""));
             	}
			    	if(!BTSLUtil.isNullString(rs.getString("OWN_TAX2_TYPE"))){
			    		additionalcommSlabDetails.setOwnerTax2Type(rs.getString("OWN_TAX2_TYPE"));
                     }else{
                    	 additionalcommSlabDetails.setOwnerTax2Type("");
                     }		    	
			    	if (PretupsI.AMOUNT_TYPE_AMOUNT.equals(additionalcommSlabDetails.getOwnerAddnlCommType())) {
			    		additionalcommSlabDetails.setOwnerTax2Rate(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("OWN_TAX2_RATE"))));
             	} else {
             		additionalcommSlabDetails.setOwnerTax2Rate(String.valueOf(rs.getDouble("OWN_TAX2_RATE")));
             	}
			    }
             if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, networkCode)){
             	if(rs.getDate("otf_applicable_from")!=null){
             		additionalcommSlabDetails.setCacApplicableFrom(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("otf_applicable_from")))));
             	}
             	if(rs.getDate("otf_applicable_to")!=null){
//             	additionalProfileDeatilsVO.setOtfApplicableToStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("otf_applicable_to")))));
//             	additionalProfileDeatilsVO.setOrigOtfApplicableToStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("otf_applicable_to")))));
             		additionalcommSlabDetails.setCacApplicableTo(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("otf_applicable_to")))));	
             		
             	}
             	if(!BTSLUtil.isNullString(rs.getString("otf_type"))){
             		additionalcommSlabDetails.setCacDetailType(rs.getString("otf_type"));
                     }else{
                    	 additionalcommSlabDetails.setCacDetailType("");
                     }
             	additionalcommSlabDetails.setCacTimeSlab(rs.getString("OTF_TIME_SLAB"));
             	if(!BTSLUtil.isNullString(additionalcommSlabDetails.getCacTimeSlab())){
             		String[] s = additionalcommSlabDetails.getCacTimeSlab().split(",");
             		//additionalcommSlabDetails.setCacApplicableTo(additionalcommSlabDetails.getCacApplicableTo()+" "+(s[s.length-1]).split("-")[1]+":00");
             		additionalcommSlabDetails.setCacApplicableTo(additionalcommSlabDetails.getCacApplicableTo());
             	}else{
             		additionalcommSlabDetails.setCacApplicableTo(additionalcommSlabDetails.getCacApplicableTo());
             	}
             }
             addCommissionDetailList.add(additionalcommSlabDetails);
         }

         return addCommissionDetailList;
     }
         }
     }// end of try
     catch (SQLException sqle) {
         _log.error(methodName, "SQLException " + sqle.getMessage());
         _log.errorTrace(methodName, sqle);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
             "commissionProfileWebDAO[loadAdditionalProfileDetailList]", "", "", "", "SQL Exception:" + sqle.getMessage());
         throw new BTSLBaseException("commissionProfileWebDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
     }// end of catch
     catch (Exception e) {
         _log.error(methodName, "Exception " + e.getMessage());
         _log.errorTrace(methodName, e);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
             "commissionProfileWebDAO[loadAdditionalProfileDetailList]", "", "", "", "Exception:" + e.getMessage());
         throw new BTSLBaseException("commissionProfileWebDAO", methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);
     }// end of catch
     finally {
     	
         if (_log.isDebugEnabled()) {
             _log.debug(methodName, "Exiting addCommissionDetailList Size:" + addCommissionDetailList.size());
         }
     }// end of finally
 }
 /**
  * Method for update Commission Profile Set Table.
  * 
  * @param p_con
  *            java.sql.Connection
  * @param p_voList
  *            java.util.ArrayList
  * 
  * @return updateCount int
  * @throws BTSLBaseException
 * @throws SQLException 
  */
 
public int suspendCommissionProfileListFromRestAPI(Connection p_con, ArrayList p_voList) throws BTSLBaseException, SQLException {
	  int updateCount = 0;

	     final String methodName = "suspendCommissionProfileList";
	     if (_log.isDebugEnabled()) {
	         _log.debug(methodName, "Entered: p_voList Size= " + p_voList.size());
	     }

	     try {
	         // checking the modified status of all the networks one by one
	         int listSize = 0;
	         boolean modified = false;
	         if (p_voList != null) {
	             listSize = p_voList.size();
	         }

	         final StringBuilder strBuff = new StringBuilder();

	         strBuff.append("Update commission_profile_set SET status = ?, modified_by = ?, modified_on = ?,");
	         strBuff.append("language_1_message = ?, language_2_message = ?");
	         strBuff.append(" WHERE comm_profile_set_id = ?");

	         final String updateQuery = strBuff.toString();
	         if (_log.isDebugEnabled()) {
	             _log.debug(methodName, "Query sqlUpdate:" + updateQuery);
	         }

	         // commented for DB2 psmtUpdate = (OraclePreparedStatement)

	         try(PreparedStatement psmtUpdate =  p_con.prepareStatement(updateQuery);)
	         {
	        	 ChangeStatusForCommissionProfileVO commissionProfileSetVO = null;
	         for (int i = 0; i < listSize; i++) {
	             commissionProfileSetVO = (ChangeStatusForCommissionProfileVO) p_voList.get(i);

	             psmtUpdate.setString(1, commissionProfileSetVO.getStatus());
	             psmtUpdate.setString(2, commissionProfileSetVO.getModifiedBy());
	             psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(commissionProfileSetVO.getModifiedOn()));
	             psmtUpdate.setString(4, commissionProfileSetVO.getLanguage1Message());

	             // commented for DB2 psmtUpdate.setFormOfUse(5,

	             psmtUpdate.setString(5, commissionProfileSetVO.getLanguage2Message());

	             psmtUpdate.setString(6, commissionProfileSetVO.getCommProfileSetId());

	             modified = this.recordModified(p_con, commissionProfileSetVO.getCommProfileSetId(), commissionProfileSetVO.getLastModifiedOn());

	             // if modified = true mens record modified by another user
	             if (modified) {
	                 throw new BTSLBaseException("error.modified");
	             }

	             updateCount = psmtUpdate.executeUpdate();

	             psmtUpdate.clearParameters();

	             // check the status of the update
	             if (updateCount <= 0) {
	                 throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	             }
	         }

	     }
	     }// end of try
	     catch (BTSLBaseException be) {
	         _log.error(methodName, "BTSLBaseException:" + be.toString());
	         throw be;
	     } catch (SQLException sqle) {
	         _log.error(methodName, "SQLException: " + sqle.getMessage());
	         _log.errorTrace(methodName, sqle);
	         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	             "commissionProfileWebDAO[suspendCommissionProfileList]", "", "", "", "SQL Exception:" + sqle.getMessage());
	         throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	     } // end of catch
	     catch (Exception e) {
	         _log.error(methodName, "Exception: " + e.getMessage());
	         _log.errorTrace(methodName, e);
	         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	             "commissionProfileWebDAO[suspendCommissionProfileList]", "", "", "", "Exception:" + e.getMessage());
	         throw new BTSLBaseException(this, methodName, "error.general.processing");
	     } // end of catch
	     finally {
	    	
	     	if (_log.isDebugEnabled()) {
	             _log.debug(methodName, "Exiting: updateCount=" + updateCount);
	         }
	     } // end of finally

	     return updateCount;
}


public ArrayList loadCommissionProfile(Connection p_con, String p_networkCode, String p_categoryCode, String geoCode,
		String gradeCode, String status, Date p_currentDate) throws BTSLBaseException {

	final String methodName = "loadCommissionProfile";

	CommissionProfileWebQry commissionProfileWebQry = (CommissionProfileWebQry) ObjectProducer
			.getObject(QueryConstants.COMM_PROFILE_WEB_QRY, QueryConstants.QUERY_PRODUCER);
	final String sqlSelect = commissionProfileWebQry.loadCommissionProfileSetVersionQry1(geoCode, gradeCode,status);

	if (_log.isDebugEnabled()) {
		_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	}

	final ArrayList list = new ArrayList();

	try (PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {

		int i = 1;
		pstmtSelect.setString(i++, p_networkCode);
		pstmtSelect.setString(i++, p_categoryCode);
		if (!geoCode.equals(PretupsI.ALL)) {
			pstmtSelect.setString(i++, geoCode);
		}
		if (!gradeCode.equals(PretupsI.ALL)) {
			pstmtSelect.setString(i++, gradeCode);
		}
		
		if (!status.equals(PretupsI.ALL)) {
			pstmtSelect.setString(i++, status);
			}
		
		pstmtSelect.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
		
		try (ResultSet rs = pstmtSelect.executeQuery();) {
			CommissionProfileSetVersionVO commissionProfileSetVersionVO = null;
			int index = 0;
			while (rs.next()) {
				commissionProfileSetVersionVO = new CommissionProfileSetVersionVO();
				commissionProfileSetVersionVO.setCommProfileSetId(rs.getString("comm_profile_set_id"));
				commissionProfileSetVersionVO.setCommProfileSetVersion(rs.getString("comm_profile_set_version"));
				commissionProfileSetVersionVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
				commissionProfileSetVersionVO.setOldApplicableFrom(rs.getTimestamp("applicable_from").getTime());
				commissionProfileSetVersionVO.setDualCommissionType(rs.getString("dual_comm_type"));
				commissionProfileSetVersionVO.setCreated_on(rs.getString("created_on"));
				commissionProfileSetVersionVO.setIsDefault(rs.getString("is_default"));
				commissionProfileSetVersionVO.setGeoCode(rs.getString("geography_code"));
				commissionProfileSetVersionVO.setComm_set_name(rs.getString("comm_profile_set_name"));
				commissionProfileSetVersionVO.setStatus(rs.getString("status"));
				commissionProfileSetVersionVO.setServicesAllowed(rs.getString("services_allowed"));
                commissionProfileSetVersionVO.setCategoryName(rs.getString("category_name"));
				++index;
				commissionProfileSetVersionVO.setRowId("" + index);
				list.add(commissionProfileSetVersionVO);
			}
		}
	} catch (SQLException sqe) {
		_log.error(methodName, "SQLException : " + sqe);
		_log.errorTrace(methodName, sqe);
		throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	} catch (Exception ex) {
		_log.error(methodName, "Exception : " + ex);
		_log.errorTrace(methodName, ex);
		throw new BTSLBaseException(this, methodName, "error.general.processing");
	} finally {

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting: commissionProfileSetVersionList size=" + list.size());
		}
	}
	return list;
}

/**
 * Method for Deleting Commission Profile Set(just update the status set
 * status=N).
 * 
 * @param p_con                    java.sql.Connection
 * @param p_commissionProfileSetVO CommissionProfileSetVO
 * @return deleteCount int
 * @throws BTSLBaseException
 */
public CommissionProfileSetVO getCommissionProfileSetOldIdForDefault(Connection p_con, String categoryCode,
		String networkCode) throws BTSLBaseException {

	final String methodName = "getCommissionProfileSetOldIdForDefault";
	_log.debug(methodName, "Entered: p_commissionProfileSetVO=" + categoryCode, "Entered: networkCode=" + networkCode);
	PreparedStatement pstmtSelect = null;
	CommissionProfileSetVO commissionProfileSetVO = null;
	int userCount = 0;
	final StringBuilder strBuff = new StringBuilder();

	strBuff.append(" Select comm_profile_set_id from commission_profile_set where CATEGORY_CODE=? and NETWORK_CODE=?");
	strBuff.append(" and IS_DEFAULT='Y'");
	final String sqlSelect = strBuff.toString();
	ResultSet rs = null;
	try {
		pstmtSelect = p_con.prepareStatement(sqlSelect);
		pstmtSelect.setString(1, categoryCode);
		pstmtSelect.setString(2, networkCode);

		rs = pstmtSelect.executeQuery();

		if (rs.next()) {
			commissionProfileSetVO = new CommissionProfileSetVO();
			commissionProfileSetVO.setCommProfileSetId(rs.getString("comm_profile_set_id"));

			userCount++;
		}

	} catch (Exception ex) {
		_log.error(methodName, "Exception : " + ex);
		_log.errorTrace(methodName, ex);
		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
				"CommissionProfileWebDAO[loadUserIdByMsisdn]", "", "", "", "Exception:" + ex.getMessage());

	} finally {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			_log.error("An error occurred closing statement.", e);
		}
		try {
			if (pstmtSelect != null) {
				pstmtSelect.close();
			}
		} catch (SQLException e) {
			_log.error("An error occurred closing statement.", e);
		}
		LogFactory.printLog(methodName, "Exiting: select count=" + userCount, _log);
	}
	return commissionProfileSetVO;

}


/**  done by anand
  * Method for suspending and resuming Commission Profile Set(just update the status set
  * status=S for suspend and status=Y for resume).
  * 
  * @param p_con
  *            java.sql.Connection
  * @param p_commissionProfileSetVO
  *            CommissionProfileSetVO
  * @return deleteCount int
  * @throws BTSLBaseException
  */
 public int suspendResumeCommissionProfileSet(Connection p_con, CommissionProfileSetVO p_commissionProfileSetVO) throws BTSLBaseException {
     
     int updateCount = 0;
     final String methodName = "suspendResumeCommissionProfileSet";
     if (_log.isDebugEnabled()) {
         _log.debug(methodName, "Entered: p_commissionProfileSetVO=" + p_commissionProfileSetVO);
     }
     try {
         final StringBuilder strBuff = new StringBuilder();
         strBuff.append("UPDATE commission_profile_set SET status = ? , modified_by = ? , modified_on = ? , ");
         strBuff.append("language_1_message = ?, language_2_message = ? ");
         strBuff.append("WHERE comm_profile_set_id = ?");
         final String deleteQuery = strBuff.toString();
         if (_log.isDebugEnabled()) {
             _log.debug(methodName, "Query sqlDelete:" + deleteQuery);
         }
         try(PreparedStatement psmtUpdate = p_con.prepareStatement(deleteQuery);)
         {
         psmtUpdate.setString(1, p_commissionProfileSetVO.getStatus());
         psmtUpdate.setString(2, p_commissionProfileSetVO.getModifiedBy());
         psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_commissionProfileSetVO.getModifiedOn()));
         psmtUpdate.setString(4, p_commissionProfileSetVO.getLanguage1Message());
         psmtUpdate.setString(5, p_commissionProfileSetVO.getLanguage2Message());
         psmtUpdate.setString(6, p_commissionProfileSetVO.getCommProfileSetId());

         updateCount = psmtUpdate.executeUpdate();
     }
     }// end of try
     catch (SQLException sqle) {
         _log.error(methodName, "SQLException: " + sqle.getMessage());
         _log.errorTrace(methodName, sqle);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "commissionProfileWebDAO[deleteCommissionProfileSet]",
             "", "", "", "SQL Exception:" + sqle.getMessage());
         throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
     } // end of catch
     catch (Exception e) {
         _log.error(methodName, "Exception: " + e.getMessage());
         _log.errorTrace(methodName, e);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "commissionProfileWebDAO[deleteCommissionProfileSet]",
             "", "", "", "Exception:" + e.getMessage());
         throw new BTSLBaseException(this, methodName, "error.general.processing");
     } // end of catch
     finally {
     	
     	if (_log.isDebugEnabled()) {
             _log.debug(methodName, "Exiting: deleteCount=" + updateCount);
         }
     } // end of finally

     return updateCount;
 }
 
 
 
 /**
  * Method for loading Commission Profile Set Versions which takes extra filter of p_commProfileSetId(the difference from this method->loadCommissionProfileSetVersion ).
  * 
  * @param p_con
  *            java.sql.Connection
  * @param p_networkCode
  *            String
  * @param p_categoryCode
  *            String
  * @param p_currentDate
  *            java.util.Date
  * 
  * @return java.util.ArrayList
  * @throws BTSLBaseException
  */
 public ArrayList loadCommissionProfileSetVersionTwo(Connection p_con, String p_networkCode, String p_categoryCode, Date p_currentDate, String p_commProfileSetId) throws BTSLBaseException {

     final String methodName = "loadCommissionProfileSetVersion";
     if (_log.isDebugEnabled()) {
         _log.debug(methodName, "Entered p_networkCode=" + p_networkCode + " p_categoryCode=" + p_categoryCode + " currentDate=" + p_currentDate);
     }

CommissionProfileWebQry commissionProfileWebQry = (CommissionProfileWebQry)ObjectProducer.getObject(QueryConstants.COMM_PROFILE_WEB_QRY, QueryConstants.QUERY_PRODUCER);
Boolean isOthComChnl = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL);
     final String sqlSelect = commissionProfileWebQry.loadCommissionProfileSetVersionQryTwo();

         if (_log.isDebugEnabled()) {
         _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
     }

     final ArrayList list = new ArrayList();

     try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
         
         pstmtSelect.setString(1, p_networkCode);
         pstmtSelect.setString(2, p_categoryCode);
         pstmtSelect.setString(3, p_commProfileSetId);
         pstmtSelect.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
         pstmtSelect.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
         try(ResultSet rs = pstmtSelect.executeQuery();)
         {
         CommissionProfileSetVersionVO commissionProfileSetVersionVO = null;

         while (rs.next()) {
             commissionProfileSetVersionVO = new CommissionProfileSetVersionVO();
             commissionProfileSetVersionVO.setCommProfileSetId(rs.getString("comm_profile_set_id"));
             commissionProfileSetVersionVO.setCommProfileSetVersion(rs.getString("comm_profile_set_version"));
             commissionProfileSetVersionVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
             commissionProfileSetVersionVO.setOldApplicableFrom(rs.getTimestamp("applicable_from").getTime());
             commissionProfileSetVersionVO.setDualCommissionType(rs.getString("dual_comm_type"));
           
             if(isOthComChnl){
				commissionProfileSetVersionVO.setOtherCommissionProfileSetID(rs.getString("oth_comm_prf_set_id"));
				commissionProfileSetVersionVO.setCommissionType(rs.getString("oth_comm_prf_type"));
				commissionProfileSetVersionVO.setCommissionTypeValue(rs.getString("oth_comm_prf_type_value"));
				commissionProfileSetVersionVO.setOtherCommissionName(rs.getString("OTH_COMM_PRF_SET_NAME"));
             }

             list.add(commissionProfileSetVersionVO);
         }
     }
     }catch (SQLException sqe) {
         _log.error(methodName, "SQLException : " + sqe);
         _log.errorTrace(methodName, sqe);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
             "commissionProfileWebDAO[loadCommissionProfileSetVersion]", "", "", "", "SQL Exception:" + sqe.getMessage());
         throw new BTSLBaseException(this, "loadCommissionProfileSetVersion()", "error.general.sql.processing");
     } catch (Exception ex) {
         _log.error(methodName, "Exception : " + ex);
         _log.errorTrace(methodName, ex);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
             "commissionProfileWebDAO[loadCommissionProfileSetVersion]", "", "", "", "Exception:" + ex.getMessage());
         throw new BTSLBaseException(this, "loadCommissionProfileSetVersion()", "error.general.processing");
     } finally {
     	
         if (_log.isDebugEnabled()) {
             _log.debug(methodName, "Exiting: commissionProfileSetVersionList size=" + list.size());
         }
     }
     return list;
 }
 
	/**
	 * Method for loading Commission Profile Set Versions.
	 * 
	 * @param p_con          java.sql.Connection
	 * @param p_networkCode  String
	 * @param p_categoryCode String
	 * @param p_comProfileSetId  String
	 * @param p_comProfileSetVersionId  String
	 * 
	 * @return java.util.ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadCommissionProfileSetVersionForViewDetail(Connection p_con, String p_networkCode,
			String p_categoryCode, String p_comProfileSetId, String p_comProfileSetVersionId)
			throws BTSLBaseException {

		final String methodName = "loadCommissionProfileSetVersion";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,
					"Entered p_networkCode=" + p_networkCode + " p_categoryCode=" + p_categoryCode +  "p_comProfileSetId" + p_comProfileSetId + "p_comProfileSetVersionId"
							+ p_comProfileSetVersionId);
		}

		CommissionProfileWebQry commissionProfileWebQry = (CommissionProfileWebQry) ObjectProducer
				.getObject(QueryConstants.COMM_PROFILE_WEB_QRY, QueryConstants.QUERY_PRODUCER);
		Boolean isOthComChnl = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL);
		final String sqlSelect = commissionProfileWebQry.loadCommissionProfileSetVersionQryForViewDetail();

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}

		final ArrayList list = new ArrayList();

		try (PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {

			pstmtSelect.setString(1, p_networkCode);
			pstmtSelect.setString(2, p_categoryCode);
			pstmtSelect.setString(3, p_comProfileSetId);
			pstmtSelect.setString(4, (p_comProfileSetVersionId));
			try (ResultSet rs = pstmtSelect.executeQuery();) {
				CommissionProfileSetVersionVO commissionProfileSetVersionVO = null;

				while (rs.next()) {
					commissionProfileSetVersionVO = new CommissionProfileSetVersionVO();
					commissionProfileSetVersionVO.setCommProfileSetId(rs.getString("comm_profile_set_id"));
					commissionProfileSetVersionVO.setCommProfileSetVersion(rs.getString("comm_profile_set_version"));
					commissionProfileSetVersionVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
					commissionProfileSetVersionVO.setOldApplicableFrom(rs.getTimestamp("applicable_from").getTime());
					commissionProfileSetVersionVO.setDualCommissionType(rs.getString("dual_comm_type"));

					if (isOthComChnl) {
						commissionProfileSetVersionVO
								.setOtherCommissionProfileSetID(rs.getString("oth_comm_prf_set_id"));
						commissionProfileSetVersionVO.setCommissionType(rs.getString("oth_comm_prf_type"));
						commissionProfileSetVersionVO.setCommissionTypeValue(rs.getString("oth_comm_prf_type_value"));
						commissionProfileSetVersionVO.setOtherCommissionName(rs.getString("OTH_COMM_PRF_SET_NAME"));
					}

					list.add(commissionProfileSetVersionVO);
				}
			}
		} catch (SQLException sqe) {
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"commissionProfileWebDAO[loadCommissionProfileSetVersion]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "loadCommissionProfileSetVersion()", "error.general.sql.processing");
		} catch (Exception ex) {
			_log.error(methodName, "Exception : " + ex);
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"commissionProfileWebDAO[loadCommissionProfileSetVersion]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "loadCommissionProfileSetVersion()", "error.general.processing");
		} finally {

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting: commissionProfileSetVersion size=" + list.size());
			}
		}
		return list;
	}

}
