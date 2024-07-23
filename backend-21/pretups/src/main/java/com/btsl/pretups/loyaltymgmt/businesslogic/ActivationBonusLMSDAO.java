/**
 * Created on Created by History
 * ----------------------------------------------------------------------------
 * ----
 * 2-Jan-2014 Vikas Jauhari Initial creation
 * 
 * ----------------------------------------------------------------------------
 * ----
 * Copyright(c) 2009 Bharti Telesoft Ltd.
 **/
package com.btsl.pretups.loyaltymgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.RedemptionVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.btsl.xl.ExcelRW;

import jxl.write.WritableWorkbook;

public class ActivationBonusLMSDAO {

    private static final Log LOG = LogFactory.getLog(ActivationBonusLMSDAO.class.getClass().getName());
    private ActivationBonusLMSQry activationBonusLMSQry;
    private static MessageResources messages = null;
    public static Locale locale = null;
    private static final String EXCEPTION = "Exception:";
    private static final String SQL_EXCEPTION = "SQL Exception:";
    private static final String QUERY_KEY = "Query:";
    
    public ActivationBonusLMSDAO(){
    	activationBonusLMSQry = (ActivationBonusLMSQry)ObjectProducer.getObject(QueryConstants.ACTIVATION_BONUSLMS_QRY, QueryConstants.QUERY_PRODUCER);

    }
    
    public int addActivationBonusSet(Connection con, ProfileSetLMSVO pProfileSetVO, String pRequestType) throws BTSLBaseException {
         
        int insertCount = 0;
		
		int deleteCount = 0;
        StringBuilder strBuff = null;
        final String methodName = "addActivationBonusSet";
        String insertUpdateQuery = null;
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pProfileSetVO=");
        	loggerValue.append(pProfileSetVO.toString());
        	loggerValue.append(",pRequestType=");
        	loggerValue.append(pRequestType);
        	LOG.debug(methodName, loggerValue);
        }
        try {
			String deleteUpdateQuery=null ;
            // if request is for add activation profile
            if (pRequestType.equals("addactprofile")) {
                strBuff = new StringBuilder();
                strBuff.append("INSERT INTO profile_set (profile_type,");
                strBuff.append(" set_id,set_name,last_version,status,");
                strBuff.append(" created_on,created_by,modified_on,modified_by,short_code,network_code,promotion_type, ref_based_allowed,MESSAGE_MANAGEMENT_ENABLED");// brajesh
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
                    strBuff.append(",OPT_IN_OUT_ENABLED) ");
                } else {
                    strBuff.append(") ");
                }
                strBuff.append(" values ");
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
                    strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                } else {
                    strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                }
                insertUpdateQuery = strBuff.toString();
            }
            // when request is for modify act profile
            else if (pRequestType.equals("modifyactprofile")) {

                strBuff = new StringBuilder();
                strBuff.append("UPDATE profile_set ");
                strBuff.append(" SET last_version=?,modified_on=?,modified_by=?,MESSAGE_MANAGEMENT_ENABLED=?");
                strBuff.append(" WHERE set_id=? ");
                insertUpdateQuery = strBuff.toString();
				strBuff = new StringBuilder();
				strBuff.append("DELETE FROM MESSAGES_MASTER WHERE MESSAGE_CODE IN('"+PretupsI.WEL_MESSAGE+"_"+pProfileSetVO.getSetId()+"','"+PretupsI.TRA_WEL_MESSAGE+"_"+pProfileSetVO.getSetId());
				strBuff.append("','"+PretupsI.SUCCESS_MESSAGE+"_"+pProfileSetVO.getSetId()+"','"+PretupsI.FAILURE_MESSAGE+"_"+pProfileSetVO.getSetId());
				if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue())	{
					strBuff.append("','"+PretupsI.OPTINOUT_WEL_MESSAGE+"_"+pProfileSetVO.getSetId()+"','"+PretupsI.OPTINOUT_TRA_WEL_MSG+"_"+pProfileSetVO.getSetId());					
				}
				strBuff.append("') ");
				deleteUpdateQuery=strBuff.toString();
            }
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(insertUpdateQuery);
    			LOG.debug(methodName, loggerValue);
    		}

            try(PreparedStatement psmtInsertUpdate = con.prepareStatement(insertUpdateQuery);)
            {
            if (pRequestType.equals("addactprofile")) {
                psmtInsertUpdate.setString(1, pProfileSetVO.getProfileType().toUpperCase());
                psmtInsertUpdate.setString(2, pProfileSetVO.getSetId());
                psmtInsertUpdate.setString(3, pProfileSetVO.getSetName());
                psmtInsertUpdate.setString(4, pProfileSetVO.getLastVersion());
                psmtInsertUpdate.setString(5, pProfileSetVO.getStatus());
                psmtInsertUpdate.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(pProfileSetVO.getCreatedOn()));
                psmtInsertUpdate.setString(7, pProfileSetVO.getCreatedBy());
                psmtInsertUpdate.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(pProfileSetVO.getModifiedOn()));
                psmtInsertUpdate.setString(9, pProfileSetVO.getModifiedBy());
                psmtInsertUpdate.setString(10, pProfileSetVO.getShortCode());
                psmtInsertUpdate.setString(11, pProfileSetVO.getNetworkCode());
                psmtInsertUpdate.setString(12, pProfileSetVO.getPromotionType());
                psmtInsertUpdate.setString(13, pProfileSetVO.getRefBasedAllow());
                psmtInsertUpdate.setString(14, pProfileSetVO.getMsgConfEnableFlag());
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue()) {
                    psmtInsertUpdate.setString(15, pProfileSetVO.getOptInOut());
                }

                insertCount = psmtInsertUpdate.executeUpdate();
            } else if (pRequestType.equals("modifyactprofile")) {
                psmtInsertUpdate.setString(1, pProfileSetVO.getLastVersion());
                psmtInsertUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(pProfileSetVO.getModifiedOn()));
                psmtInsertUpdate.setString(3, pProfileSetVO.getModifiedBy());
                psmtInsertUpdate.setString(4, pProfileSetVO.getMsgConfEnableFlag());
                psmtInsertUpdate.setString(5, pProfileSetVO.getSetId());

                insertCount = psmtInsertUpdate.executeUpdate();
				if(PretupsI.NO.equalsIgnoreCase(pProfileSetVO.getMsgConfEnableFlag())) {
					if(LOG.isDebugEnabled()){
		    			loggerValue.setLength(0);
		    			loggerValue.append(QUERY_KEY);
		    			loggerValue.append(deleteUpdateQuery);
		    			LOG.debug(methodName, loggerValue);
		    		}
					try(PreparedStatement psmtDeleteUpdate = con.prepareStatement(deleteUpdateQuery);)
					{
					deleteCount = psmtDeleteUpdate.executeUpdate();
				}
				}
            }
        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addActivationBonusSet]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);

            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addActivationBonusSet]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	 if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: insertCount:");
             	loggerValue.append(insertCount);
             	loggerValue.append("deleteCount");
             	loggerValue.append(deleteCount);
             	LOG.debug(methodName, loggerValue);
             }
        } // end of finally

        return insertCount;
    }

    public int addActivationBonusVersion(Connection con, ProfileSetVersionLMSVO pProfileSetVersionVO) throws BTSLBaseException {
         
        int insertCount = 0;
        final String methodName = "addActivationBonusVersion";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pProfileSetVersionVO=");
        	loggerValue.append(pProfileSetVersionVO.toString());
        	LOG.debug(methodName, loggerValue);
        }
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO profile_set_version (set_id,");
            strBuff.append("version,applicable_from, ");
            if (pProfileSetVersionVO.getOneTimeBonus() > 0) {
                strBuff.append(" one_time_bonus, ");
            }
            if (pProfileSetVersionVO.getBonusDuration() > 0) {
                strBuff.append(" bonus_duration, ");
            }
            strBuff.append(" product_code,status,created_on,created_by,modified_on,modified_by, applicable_to, reference_from, reference_to,opt_contribution,prt_contribution)");
            strBuff.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?");
            if (pProfileSetVersionVO.getOneTimeBonus() > 0) {
                strBuff.append(" ,?");
            }
            if (pProfileSetVersionVO.getBonusDuration() > 0) {
                strBuff.append(" ,?");
            }
            strBuff.append(" )");
            String insertQuery = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(insertQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            int i = 0;
            try(PreparedStatement psmtInsert = con.prepareStatement(insertQuery);)
            {
            psmtInsert.setString(++i, pProfileSetVersionVO.getSetId());
            psmtInsert.setString(++i, pProfileSetVersionVO.getVersion());
            psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(pProfileSetVersionVO.getApplicableFrom()));
            if (pProfileSetVersionVO.getOneTimeBonus() > 0) {
                psmtInsert.setLong(++i, PretupsBL.getSystemAmount(pProfileSetVersionVO.getOneTimeBonus()));
            }
            if (pProfileSetVersionVO.getBonusDuration() > 0) {
                psmtInsert.setLong(++i, pProfileSetVersionVO.getBonusDuration());
            }
            psmtInsert.setString(++i, pProfileSetVersionVO.getProductCode());
            psmtInsert.setString(++i, pProfileSetVersionVO.getStatus());
            psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(pProfileSetVersionVO.getCreatedOn()));
            psmtInsert.setString(++i, pProfileSetVersionVO.getCreatedBy());
            psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(pProfileSetVersionVO.getModifiedOn()));
            psmtInsert.setString(++i, pProfileSetVersionVO.getModifiedBy());
            psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(pProfileSetVersionVO.getApplicableTo()));
            psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(pProfileSetVersionVO.getRefApplicableFrom()));
            psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(pProfileSetVersionVO.getRefApplicableTo()));
            psmtInsert.setString(++i, pProfileSetVersionVO.getOptContribution());
            psmtInsert.setString(++i, pProfileSetVersionVO.getPrtContribution());
            insertCount = psmtInsert.executeUpdate();
        }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addActivationBonusVersion]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addActivationBonusVersion]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: insertCount:");
             	loggerValue.append(insertCount);
             	LOG.debug(methodName, loggerValue);
             }
        } // end of finally

        return insertCount;
    }

    public int addActivationBonusSetDetail(Connection con, ArrayList pDetailVOList, ProfileSetLMSVO profileSetVO, boolean pUpdateVersion) throws BTSLBaseException {
         
        int insertCount = 0;
        final String methodName = "addActivationBonusSetDetail";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pDetailVOList=");
        	loggerValue.append(pDetailVOList.size());
        	loggerValue.append(",pUpdateVersion=");
        	loggerValue.append(pUpdateVersion);
        	LOG.debug(methodName, loggerValue);
        }

        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO profile_details (set_id,");
            strBuff.append("version,detail_id,type,user_type,detail_type,detail_subtype,period_id,service_code,");
			strBuff.append("start_range,end_range,points_type,points,min_limit,max_limit,subscriber_type,reference_date,PRODUCT_CODE ) ");
			strBuff.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            String insertQuery = strBuff.toString();

            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(insertQuery);
    			LOG.debug(methodName, loggerValue);
    		}

            try(PreparedStatement psmtInsert = con.prepareStatement(insertQuery);)
            {
            ProfileSetDetailsLMSVO detailVO = null;
            for (int i = 0, j = pDetailVOList.size(); i < j; i++) {

                detailVO = (ProfileSetDetailsLMSVO) pDetailVOList.get(i);
                psmtInsert.setString(1, profileSetVO.getSetId());
                // when new date and old date in case of modify details are same
                // decrement version to original
                if (pUpdateVersion) {
                    // version remains the same
                    psmtInsert.setString(2, detailVO.getVersion());
                }
                // version is 1 added to last version
                else {
                    psmtInsert.setString(2, profileSetVO.getLastVersion());
                }
                psmtInsert.setString(3, Integer.toString(i + 1));
                psmtInsert.setString(4, detailVO.getType().trim());
                psmtInsert.setString(5, detailVO.getUserType().trim());
                psmtInsert.setString(6, detailVO.getDetailType().trim());
                psmtInsert.setString(7, detailVO.getDetailSubType().trim());
                psmtInsert.setString(8, detailVO.getPeriodId().trim());
                psmtInsert.setString(9, detailVO.getServiceCode().trim());
                if (detailVO.getDetailSubType().equals(PretupsI.USER_SUB_TYPE_COUNT)) {
                    psmtInsert.setLong(10, detailVO.getStartRange());
                    psmtInsert.setLong(11, detailVO.getEndRange());
                } else {
                    psmtInsert.setLong(10, PretupsBL.getSystemAmount(detailVO.getStartRange()));
                    psmtInsert.setLong(11, PretupsBL.getSystemAmount(detailVO.getEndRange()));
                }
                psmtInsert.setString(12, detailVO.getPointsTypeCode());
                psmtInsert.setLong(13, detailVO.getPoints());
                psmtInsert.setLong(14, detailVO.getMinLimit());
                psmtInsert.setLong(15, detailVO.getMaxLimit());
                psmtInsert.setString(16, detailVO.getSubscriberType().trim());
                if (!BTSLUtil.isNullString(detailVO.getReferenceDate())) {
                    psmtInsert.setDate(17, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(detailVO.getReferenceDate())));
                } else {
                    psmtInsert.setDate(17, null);
                }
				psmtInsert.setString(18, detailVO.getProductCode());
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
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addActivationBonusSetDetail]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addActivationBonusSetDetail]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: insertCount:");
             	loggerValue.append(insertCount);
             	LOG.debug(methodName, loggerValue);
             }
        } // end of finally

        return insertCount;
    }

    public ArrayList loadProfileList(Connection con, String networkCode, ArrayList profileList) throws BTSLBaseException {
        final String methodName = "loadProfileList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: profileList=");
        	loggerValue.append(profileList.size());
        	loggerValue.append(",networkCode=");
        	loggerValue.append(networkCode);
        	LOG.debug(methodName, loggerValue);
        }
         
        ProfileSetLMSVO profileSetVO = null;
        
        try {
            StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT ps.profile_type,ps.set_id,ps.set_name,ps.last_version,ps.short_code,ps.PROMOTION_TYPE ");
            sbf.append("FROM profile_set ps ");
            sbf.append("WHERE ps.network_code=? AND ps.status NOT IN ('N','R') AND ps.profile_type=?  ORDER BY ps.set_name");
            String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement pstmt = con.prepareStatement(selectQuery);)
            {
            pstmt.setString(1, networkCode);
            pstmt.setString(2, PretupsI.LMS_PROFILE_TYPE);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                profileSetVO = new ProfileSetLMSVO();
				String set_id = rs.getString("set_id");
				Map<String,Integer> countOftotalVersionSuspendActive = new HashMap<String,Integer>(1);
				countOftotalVersionSuspendActive = countOfTotalVersionSuspendActive(con, set_id);
				int totalVersion = 0;
				int totalSuspendedVersion = 0;
				int totalActiveVersion = 0; 
				if(countOftotalVersionSuspendActive!=null && countOftotalVersionSuspendActive.size()>0){
					totalVersion = countOftotalVersionSuspendActive.get("totalVersion");
					totalSuspendedVersion = countOftotalVersionSuspendActive.get("totalSuspendedVersion");
					totalActiveVersion = countOftotalVersionSuspendActive.get("totalActiveVersion");
					if(LOG.isDebugEnabled()){
						LOG.debug(methodName ,"totalVersion="+totalVersion+"totalSuspendedVersion="+totalSuspendedVersion+"totalActiveVersion="+totalActiveVersion);
					}
				}
				if(totalSuspendedVersion == totalVersion){
					continue;
				} else {
	                profileSetVO.setProfileType(rs.getString("profile_type"));
	                profileSetVO.setSetId(rs.getString("set_id"));
	                profileSetVO.setSetName(rs.getString("set_name"));
	                profileSetVO.setLastVersion(rs.getString("last_version"));
	                profileSetVO.setShortCode(rs.getString("short_code"));
	                profileSetVO.setPromotionType(rs.getString("PROMOTION_TYPE"));
	                profileList.add(profileSetVO);
            	}
				countOftotalVersionSuspendActive = null;				
			}
        }
            }
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadProfileList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadProfileList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: profileList:");
             	loggerValue.append(profileList.toString());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return profileList;
    }

    public ArrayList loadVersions(Connection con, String pProfileSetID) throws BTSLBaseException {
        final String methodName = "loadVersions";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pProfileSetID=");
        	loggerValue.append(pProfileSetID);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList list = null;
        StringBuilder sbf = null;
         
        ProfileSetVersionLMSVO versionVO = null;
        try {
            list = new ArrayList();
            sbf = new StringBuilder();
            sbf.append("SELECT psv.set_id,psv.VERSION,psv.applicable_from,psv.bonus_duration ");
            sbf.append("FROM PROFILE_SET_VERSION psv ");
            sbf.append(" where psv.status NOT IN (?,?) ");
            if (pProfileSetID != null) {
                sbf.append("AND psv.set_id=? ");
            }
            sbf.append(" order by psv.VERSION desc ");
            String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement pstmt = con.prepareStatement(selectQuery);)
            {
            int i = 0;

            pstmt.setString(++i, PretupsI.USER_STATUS_DELETED);
            pstmt.setString(++i, PretupsI.USER_STATUS_SUSPEND);
            if (pProfileSetID != null) {
                pstmt.setString(++i, pProfileSetID);
            }
           try(ResultSet rs = pstmt.executeQuery();)
           {
            while (rs.next()) {
                versionVO = new ProfileSetVersionLMSVO();
                versionVO.setSetId(rs.getString("set_id"));
                versionVO.setVersion(rs.getString("version"));
                versionVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                versionVO.setBonusDuration(rs.getLong("bonus_duration"));
                list.add(versionVO);
            }

        }
            }
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadVersions]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadVersions]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }

        return list;
    }

    public boolean deleteProfileSetVersion(Connection con, String setId, String pVersion, UserVO pUserVO, boolean pSetDel, boolean pVersionDel) throws BTSLBaseException {
        final String methodName = "deleteProfileSetVersion";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: setId=");
        	loggerValue.append(setId);
        	loggerValue.append(",pVersion=");
        	loggerValue.append(pVersion);
        	loggerValue.append(",pSetDel=");
        	loggerValue.append(pSetDel);
        	loggerValue.append(",pVersionDel=");
        	loggerValue.append(pVersionDel);
        	loggerValue.append(",pUserVO=");
        	loggerValue.append(pUserVO);
        	LOG.debug(methodName, loggerValue);
        }
        int deleteCountForSet = 0;
        int deleteCountForVersion = 0;
        boolean flag = false;
        PreparedStatement pstmtSetUpdate = null;
        PreparedStatement pstmtVersionUpdate = null;
        StringBuilder sbf = null;
        try {
            if (pSetDel) {
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set ps SET ");
                sbf.append("ps.status='N',ps.modified_by=?,ps.modified_on=? ");
                sbf.append("WHERE ps.set_id=?");
                String updateSetTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateSetTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version psv SET ");
                sbf.append(" psv.status='N',psv.modified_by=?,psv.modified_on=? ");
                sbf.append(" WHERE psv.set_id=?");
                String updateVersionTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateVersionTable);
        			LOG.debug(methodName, loggerValue);
        		}
                Date currentDate = new Date();
                pstmtSetUpdate = con.prepareStatement(updateSetTable);
                pstmtVersionUpdate = con.prepareStatement(updateVersionTable);
                pstmtSetUpdate.setString(1, pUserVO.getActiveUserID());
                pstmtSetUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtSetUpdate.setString(3, setId);
                pstmtVersionUpdate.setString(1, pUserVO.getActiveUserID());
                pstmtVersionUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtVersionUpdate.setString(3, setId);
            } else if (pVersionDel) {
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set ps SET ");
                sbf.append("ps.modified_by=?,ps.modified_on=? ");
                sbf.append("WHERE ps.set_id=?");
                String updateSetTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateSetTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version psv SET ");
                sbf.append("psv.status='N',psv.modified_by=?,psv.modified_on=? ");
                sbf.append("WHERE psv.set_id=? AND psv.version=?");
                String updateVersionTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateVersionTable);
        			LOG.debug(methodName, loggerValue);
        		}
                Date currentDate = new Date();
                pstmtSetUpdate = con.prepareStatement(updateSetTable);
                pstmtVersionUpdate = con.prepareStatement(updateVersionTable);
                pstmtSetUpdate.setString(1, pUserVO.getActiveUserID());
                pstmtSetUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtSetUpdate.setString(3, setId);
                pstmtVersionUpdate.setString(1, pUserVO.getActiveUserID());
                pstmtVersionUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtVersionUpdate.setString(3, setId);
                pstmtVersionUpdate.setString(4, pVersion);
            }
            if (pstmtSetUpdate != null && pstmtVersionUpdate != null) {
                deleteCountForSet = pstmtSetUpdate.executeUpdate();
                deleteCountForVersion = pstmtVersionUpdate.executeUpdate();
            }
            if (deleteCountForSet > 0 && deleteCountForVersion > 0) {
                flag = true;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[deleteProfileSetVersion]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[deleteProfileSetVersion]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	try{
                if (pstmtSetUpdate!= null){
                	pstmtSetUpdate.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmtVersionUpdate!= null){
                	pstmtVersionUpdate.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: flag:");
             	loggerValue.append(flag);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return flag;
    }

    public ArrayList viewRedemptionenquiryDetails(Connection con, String pUserId, String pFromdate, String pTodate, String pCategoryCode, String pDomainCode, String pZoneCode) throws BTSLBaseException {
        final String methodName = "viewRedemptionenquiryDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pUserId=");
        	loggerValue.append(pUserId);
        	loggerValue.append(",pFromdate=");
        	loggerValue.append(pFromdate);
        	loggerValue.append(",pTodate=");
        	loggerValue.append(pTodate);
        	loggerValue.append(",pDomainCode=");
        	loggerValue.append(pDomainCode);
        	loggerValue.append(",pCategoryCode=");
        	loggerValue.append(pCategoryCode);
        	loggerValue.append(",pZoneCode=");
        	loggerValue.append(pZoneCode);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList redemptiondetails = null;
        PreparedStatement pstmtSelect = null;
         
        RedemptionVO redemptionVO = null;
        try {
            pstmtSelect=activationBonusLMSQry.viewRedemptionenquiryDetailsQry(con,pDomainCode,pCategoryCode,pUserId,pZoneCode,pFromdate,pTodate);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            redemptiondetails = new ArrayList();
            while (rs.next()) {
                redemptionVO = new RedemptionVO();
                redemptionVO.setUserName(rs.getString("user_name"));
                redemptionVO.setRedemptionDateStr(BTSLUtil.getDateStringFromDate(rs.getDate("redemption_date")));
                redemptionVO.setAmountTransfered(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getLong("amount_transfered"))));
                redemptionVO.setMsisdn(rs.getString("msisdn"));
                redemptionVO.setPointsRedeemedStr(PretupsBL.getDisplayAmount(rs.getLong("points_redeemed")));
                redemptiondetails.add(redemptionVO);
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[viewRedemptionenquiryDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[viewRedemptionenquiryDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: redemptiondetails:");
             	loggerValue.append(redemptiondetails.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return redemptiondetails;
    }

    public ArrayList loadCategoryDomainList(Connection con) throws BTSLBaseException {
        final String methodName = "loadCategoryDomainList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:");
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList categoryDomainList = new ArrayList();
         
        
        try {
            StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT D.domain_code, D.domain_name,D.domain_type_code,restricted_msisdn,DT.display_allowed FROM domains D,domain_types DT ");
            selectQuery.append("WHERE D.status=? AND D.owner_category != ? AND DT.domain_type_code=D.domain_type_code ");
            selectQuery.append("ORDER BY domain_name ");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
           try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery.toString());){
            pstmtSelect.setString(1, PretupsI.DOMAIN_STATUS_ACTIVE);
            pstmtSelect.setString(2, PretupsI.CATEGORY_TYPE_OPT);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            RedemptionVO redemptionVO = null;
            while (rs.next()) {
                redemptionVO = new RedemptionVO(rs.getString("domain_name"), rs.getString("domain_code"));
                redemptionVO.setType(rs.getString("restricted_msisdn"));
                redemptionVO.setOtherInfo(rs.getString("domain_type_code"));
                redemptionVO.setStatusType(rs.getString("display_allowed"));
                categoryDomainList.add(redemptionVO);
            }
        }
           }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadCategoryDomainList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadCategoryDomainList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: categoryDomainList:");
             	loggerValue.append(categoryDomainList.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return categoryDomainList;
    }

    public ActivationProfileCombinedLMSVO loadProfileVersionDetails(Connection con, String pProfileSetID, String pVersion, String networkCode) throws BTSLBaseException {
        final String methodName = "loadProfileVersionDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pProfileSetID=");
        	loggerValue.append(pProfileSetID);
        	loggerValue.append(",pVersion=");
        	loggerValue.append(pVersion);
        	loggerValue.append(",networkCode=");
        	loggerValue.append(networkCode);
        	LOG.debug(methodName, loggerValue);
        }

        
        ActivationProfileCombinedLMSVO profileDetailsCombinedVO = null;
        try {
            StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT  ps.set_id,ps.set_name,ps.short_code,psv.applicable_from,psv.bonus_duration,psv.one_time_bonus,psv.product_code, ");
            sbf.append(" ps.last_version,psv.applicable_to,psv.reference_from,psv.reference_to, ps.ref_based_allowed, ps.promotion_type,ps.MESSAGE_MANAGEMENT_ENABLED,");
            sbf.append(" psv.opt_contribution, psv.prt_contribution,ps.OPT_IN_OUT_ENABLED ");
            sbf.append(" FROM PROFILE_SET ps,PROFILE_SET_VERSION psv ");
            sbf.append(" WHERE psv.set_id=? AND psv.VERSION=? AND psv.set_id=ps.set_id AND ps.network_code=?");
            String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement pstmt = con.prepareStatement(selectQuery);)
            {
            pstmt.setString(1, pProfileSetID);
            pstmt.setString(2, pVersion);
            pstmt.setString(3, networkCode);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                profileDetailsCombinedVO = new ActivationProfileCombinedLMSVO();
                profileDetailsCombinedVO.setSetName(rs.getString("set_name"));
                profileDetailsCombinedVO.setShortCode(rs.getString("short_code"));
                profileDetailsCombinedVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                if (rs.getLong("bonus_duration") > 0) {
                    profileDetailsCombinedVO.setBonusDuration(Long.parseLong(rs.getString("bonus_duration")));
                }
                if (rs.getLong("one_time_bonus") > 0) {
                    profileDetailsCombinedVO.setOneTimeBonus(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("one_time_bonus"))));
                }
                profileDetailsCombinedVO.setProductCode(rs.getString("product_code"));
                profileDetailsCombinedVO.setLastVersion(Integer.parseInt(rs.getString("last_version")));
                profileDetailsCombinedVO.setSetId(rs.getString("set_id"));
                profileDetailsCombinedVO.setApplicableTo(rs.getTimestamp("applicable_to"));
                profileDetailsCombinedVO.setRefApplicableFrom(rs.getTimestamp("reference_from"));
                profileDetailsCombinedVO.setRefApplicableTo(rs.getTimestamp("reference_to"));
                profileDetailsCombinedVO.setReferenceBasedFlag(rs.getString("ref_based_allowed"));
                profileDetailsCombinedVO.setPromotionType(rs.getString("promotion_type"));
                profileDetailsCombinedVO.setOptContribution(rs.getString("opt_contribution"));
                profileDetailsCombinedVO.setPrtContribution(rs.getString("prt_contribution"));
                profileDetailsCombinedVO.setMsgConfEnableFlag(rs.getString("MESSAGE_MANAGEMENT_ENABLED"));
                profileDetailsCombinedVO.setPromotionTypeName(((LookupsVO) LookupsCache.getObject(PretupsI.LMS_PROMOTION_TYPE, rs.getString("promotion_type"))).getLookupName());
                profileDetailsCombinedVO.setOptInOut(rs.getString("OPT_IN_OUT_ENABLED"));
                // Handling of Expired profile
                if (BTSLUtil.getDifferenceInUtilDates(new Date(), rs.getTimestamp("applicable_to")) <=  -1  || ((rs.getTimestamp("applicable_to").getTime() - new Date().getTime())/(1000*60*60)) < 0) {
                    profileDetailsCombinedVO.setLmsProfileExpiredFlag("Y");
                } else {
                    profileDetailsCombinedVO.setLmsProfileExpiredFlag("N");
                }
            }
        } 
            }
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadProfileVersionDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadProfileVersionDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }
        return profileDetailsCombinedVO;
    }

    public ArrayList loadActivationProfileServicesList(Connection con, String pActProifleSetId, String pActProfileSetVersion) throws BTSLBaseException {

        final String methodName = "loadActivationProfileServicesList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pActProifleSetId=");
        	loggerValue.append(pActProifleSetId);
        	loggerValue.append(",pActProfileSetVersion=");
        	loggerValue.append(pActProfileSetVersion);
        	LOG.debug(methodName, loggerValue);
        }
         
        List<String> lookuptype = new ArrayList<String>();
        StringBuilder strBuff = new StringBuilder();
        ListValueVO listValueVO = null;
        strBuff.append(" SELECT DISTINCT  l.lookup_Code,l.lookup_name FROM PROFILE_DETAILS pd,lookups l ");
        strBuff.append(" WHERE l.lookup_Code=pd.SERVICE_CODE AND pd.set_id=? AND pd.VERSION=? AND l.lookup_type=?");
        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

        StringBuilder strcode = new StringBuilder();
        strcode.append(" SELECT DISTINCT type FROM PROFILE_DETAILS WHERE set_id=? AND VERSION=? ");
        String moduleselect = strcode.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(moduleselect);
			LOG.debug(methodName, loggerValue);
		}
        ArrayList list = new ArrayList();
        try( PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);// 2
        		 PreparedStatement pstmtmodule = con.prepareStatement(moduleselect);) {
            
            pstmtmodule.setString(1, pActProifleSetId);
            pstmtmodule.setString(2, pActProfileSetVersion);
            try(ResultSet rs1 = pstmtmodule.executeQuery();)
            {
            while (rs1.next()) {
                lookuptype.add(rs1.getString("type") + "SL");
            }
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("QUERY Lookup");
    			loggerValue.append(lookuptype);
    			LOG.debug(methodName, loggerValue);
    		}
            int lookuptypes=lookuptype.size();
            for (int j = 0; j <lookuptypes ; j++) {
                pstmtSelect.setString(1, pActProifleSetId);
                pstmtSelect.setString(2, pActProfileSetVersion);
                pstmtSelect.setString(3, lookuptype.get(j));
                if (!BTSLUtil.isNullString(lookuptype.get(j))) {
                    try(ResultSet rs = pstmtSelect.executeQuery();)
                    {
                    while (rs.next()) {
                        listValueVO = new ListValueVO(rs.getString("lookup_name"), rs.getString("lookup_code"));
                        list.add(listValueVO);
                    }
                }
                pstmtSelect.clearParameters();
            }
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDao[loadActivationProfileServicesList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDao[loadActivationProfileServicesList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: ActivationProfileServiceList:");
             	loggerValue.append(list.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return list;
    }

    public ArrayList loadActivationProfileDetailList(Connection con, String pActProfileServiceTypeID, String pActProifleSetId, String pActProfileSetVersion) throws BTSLBaseException {
        final String methodName = "loadActivationProfileDetailList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pActProifleSetId=");
        	loggerValue.append(pActProifleSetId);
        	loggerValue.append(",pActProifleSetId=");
        	loggerValue.append(pActProifleSetId);
        	loggerValue.append(",pActProfileSetVersion=");
        	loggerValue.append(pActProfileSetVersion);
        	LOG.debug(methodName, loggerValue);
        }
         
        ArrayList addActivationDetailList = new ArrayList();
        ProfileSetDetailsLMSVO activationProfileDeatilsVO = null;
        try {
            StringBuilder selectQueryBuff = new StringBuilder("SELECT pd.start_range,pd.end_range,pd.points_type,pd.period_id,pd.points,pd.detail_type,pd.detail_subtype,pd.subscriber_type, ");
            selectQueryBuff.append(" pd.type,pd.user_type,pd.service_code,pd.min_limit,pd.max_limit,pd.subscriber_type,pd.version,TO_CHAR(pd.reference_date,'DD/MM/YY') referenceDate ");
            selectQueryBuff.append(" FROM PROFILE_DETAILS pd ");
            selectQueryBuff.append(" WHERE pd.set_id=? AND pd.VERSION=? AND service_code=?");
            String selectQuery = selectQueryBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, pActProifleSetId);
            pstmtSelect.setString(2, pActProfileSetVersion);
            pstmtSelect.setString(3, pActProfileServiceTypeID);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                activationProfileDeatilsVO = new ProfileSetDetailsLMSVO();
                if (rs.getString("detail_subtype").equals(PretupsI.USER_SUB_TYPE_COUNT)) {
                    activationProfileDeatilsVO.setStartRangeAsString(Long.toString(rs.getLong("start_range")));
                    activationProfileDeatilsVO.setEndRangeAsString(Long.toString(rs.getLong("end_range")));
                    activationProfileDeatilsVO.setStartRange(Long.parseLong(rs.getString("start_range")));
                    activationProfileDeatilsVO.setEndRange(Long.parseLong(rs.getString("end_range")));
                } else {
                    activationProfileDeatilsVO.setStartRangeAsString(PretupsBL.getDisplayAmount(rs.getLong("start_range")));
                    activationProfileDeatilsVO.setEndRangeAsString(PretupsBL.getDisplayAmount(rs.getLong("end_range")));
                    activationProfileDeatilsVO.setStartRange(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("start_range"))));
                    activationProfileDeatilsVO.setEndRange(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("end_range"))));
                }
                activationProfileDeatilsVO.setPointsTypeCode(rs.getString("points_type"));
                activationProfileDeatilsVO.setPointsAsString(rs.getString("points"));
                activationProfileDeatilsVO.setPoints(rs.getLong("points"));
                activationProfileDeatilsVO.setDetailType(rs.getString("detail_type"));
                activationProfileDeatilsVO.setDetailSubType(rs.getString("detail_subtype"));
                activationProfileDeatilsVO.setSubscriberType(rs.getString("subscriber_type"));
                activationProfileDeatilsVO.setPeriodId(rs.getString("period_id"));
                activationProfileDeatilsVO.setType(rs.getString("type"));
                activationProfileDeatilsVO.setUserType(rs.getString("user_type"));
                activationProfileDeatilsVO.setServiceCode(rs.getString("service_code"));
                activationProfileDeatilsVO.setMinLimit(rs.getLong("min_limit"));
                activationProfileDeatilsVO.setMaxLimit(rs.getLong("max_limit"));
                activationProfileDeatilsVO.setSubscriberType(rs.getString("subscriber_type"));
                activationProfileDeatilsVO.setVersion(rs.getString("version"));
                activationProfileDeatilsVO.setReferenceDate(rs.getString("referenceDate"));
                activationProfileDeatilsVO.setReferenceType(rs.getString("period_id"));
                addActivationDetailList.add(activationProfileDeatilsVO);
            }
            return addActivationDetailList;
        }
            }
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadActivationProfileDetailList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException("ActivationBonusDAO", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,sqle);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadActivationProfileDetailList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException("ActivationBonusDAO", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION,e);
        }// end of catch
        finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: addActivationDetailList:");
             	loggerValue.append(addActivationDetailList.size());
             	LOG.debug(methodName, loggerValue);
             }
        }// end of finally
    }

    public int updateProfileVersionDetail(Connection con, String version, String userId, String setId, Date pCurrentDate, ProfileSetVersionLMSVO pProfileSetVersionVO, boolean isreferenceBased) throws BTSLBaseException {
       
        
        int insertCount = 0;
        StringBuilder strBuff = null;
        final String methodName = "updateProfileVersionDetail";
        String insertUpdateQuery = null;
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: version=");
        	loggerValue.append(version);
        	loggerValue.append(",userId=");
        	loggerValue.append(userId);
        	loggerValue.append(",setId=");
        	loggerValue.append(setId);
        	loggerValue.append(",pProfileSetVersionVO=");
        	loggerValue.append(pProfileSetVersionVO.toString());
        	LOG.debug(methodName, loggerValue);
        }
        try {
            strBuff = new StringBuilder();
            strBuff.append("UPDATE profile_set_version ");
            strBuff.append(" SET modified_on=?,modified_by=? ");
            strBuff.append(",one_time_bonus=? ");
            strBuff.append(",bonus_duration=? ");
            strBuff.append(",product_code=? ");
            strBuff.append(",opt_contribution=? ");
            strBuff.append(",prt_contribution=?,applicable_from=?,applicable_to=?  ");
            if (isreferenceBased) {
                strBuff.append(",reference_from=?,reference_to=? ");
            }
            strBuff.append(" WHERE set_id=? AND version=?  ");
            insertUpdateQuery = strBuff.toString();
            strBuff = new StringBuilder();
            strBuff.append("Delete from profile_details ");
            strBuff.append("where set_id=? AND version=? ");
            String deleteQuery = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(insertUpdateQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement psmtInsertUpdate = con.prepareStatement(insertUpdateQuery);)
            {
            int i = 0;
            psmtInsertUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(pCurrentDate));
            psmtInsertUpdate.setString(++i, userId);
            if (pProfileSetVersionVO.getOneTimeBonus() > 0) {
                psmtInsertUpdate.setLong(++i, PretupsBL.getSystemAmount(pProfileSetVersionVO.getOneTimeBonus()));
            } else {
                psmtInsertUpdate.setLong(++i, 0);
            }
            if (pProfileSetVersionVO.getBonusDuration() > 0) {
                psmtInsertUpdate.setString(++i, Long.toString(pProfileSetVersionVO.getBonusDuration()));
            } else {
                psmtInsertUpdate.setLong(++i, 0);
            }
            psmtInsertUpdate.setString(++i, pProfileSetVersionVO.getProductCode());
            psmtInsertUpdate.setString(++i, pProfileSetVersionVO.getOptContribution());
            psmtInsertUpdate.setString(++i, pProfileSetVersionVO.getPrtContribution());
            psmtInsertUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(pProfileSetVersionVO.getApplicableFrom()));
            psmtInsertUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(pProfileSetVersionVO.getApplicableTo()));
            if (isreferenceBased) {
                psmtInsertUpdate.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(pProfileSetVersionVO.getRefApplicableFrom()));
                psmtInsertUpdate.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(pProfileSetVersionVO.getRefApplicableTo()));
            }
            psmtInsertUpdate.setString(++i, setId);
            psmtInsertUpdate.setString(++i, version);
            insertCount = psmtInsertUpdate.executeUpdate();
            try(PreparedStatement psmtDelete = con.prepareStatement(deleteQuery);)
            {
            psmtDelete.setString(1, setId);
            psmtDelete.setString(2, version);
            insertCount = psmtDelete.executeUpdate();
            }
        } 
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[updateProfileVersionDetail]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[updateProfileVersionDetail]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: insertCount:");
             	loggerValue.append(insertCount);
             	LOG.debug(methodName, loggerValue);
             }
        } // end of finally

        return insertCount;
    }

    public int checkUserAssociationForProfile(Connection con, String pStr[]) throws BTSLBaseException {
        final String methodName = "checkUserAssociationForProfile";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pStr=");
        	loggerValue.append(pStr.length);
        	LOG.debug(methodName, loggerValue);
        }
         
        int insertCount = 0;
        
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT uop.USER_ID, uop.PROFILE_TYPE, uop.SET_ID, u.CATEGORY_CODE  FROM USER_OTH_PROFILES uop, USERS u, CATEGORIES c");
        strBuff.append(" WHERE uop.SET_ID= ? AND u.USER_ID=uop.USER_ID AND uop.PROFILE_TYPE= ? AND c.CATEGORY_NAME= ?");
        strBuff.append(" AND u.CATEGORY_CODE=c.CATEGORY_CODE AND u.STATUS=?");
        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);) {
           
            if (pStr.length > 1) {
                pstmtSelect.setString(1, pStr[1]);
            } else {
                pstmtSelect.setString(1, "0");
            }
            pstmtSelect.setString(2, PretupsI.PROFILE_TYPE_ACTIVATION);
            pstmtSelect.setString(3, pStr[0]);
            pstmtSelect.setString(4, PretupsI.YES);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                insertCount++;
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDao[checkUserAssociationForProfile]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDao[checkUserAssociationForProfile]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: insertCount:");
             	loggerValue.append(insertCount);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return insertCount;
    }

    public ArrayList loadServicesList(Connection con, String networkCode, String pModule, String pCatCode) throws BTSLBaseException {
        final String methodName = "loadServicesList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode=");
        	loggerValue.append(networkCode);
        	loggerValue.append(",pModule=");
        	loggerValue.append(pModule);
        	loggerValue.append(",pCatCode=");
        	loggerValue.append(pCatCode);
        	LOG.debug(methodName, loggerValue);
        }
        
         
        String sqlSelect =activationBonusLMSQry.loadServicesListQry(pCatCode);
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        ArrayList list = new ArrayList();
        ListValueVO vo1 = new ListValueVO("O2C Transfer", "O2C" + ":" + "O2C");
        ListValueVO vo2 = new ListValueVO("C2C Transfer", "C2C" + ":" + "C2C");
        list.add(vo1);
        list.add(vo2);
        try (PreparedStatement pstmt = con.prepareStatement(sqlSelect);){
            
            pstmt.setString(1, networkCode);
            pstmt.setString(2, networkCode);
            pstmt.setString(3, pModule);
            pstmt.setString(4, PretupsI.SERVICE_TYPE_IAT);
            if (!BTSLUtil.isNullString(pCatCode)) {
                pstmt.setString(5, pCatCode);
            }
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                ListValueVO vo = new ListValueVO(rs.getString("name"), rs.getString("service_type") + ":" + rs.getString("module"));
                vo.setType(rs.getString("type"));
                list.add(vo);
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadServicesList] ", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadServicesList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: serviceList:");
             	loggerValue.append(list.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return list;
    }

    public ArrayList loadLookupServicesList(Connection con) throws BTSLBaseException {
        final String methodName = "loadLookupServicesList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	LOG.debug(methodName, loggerValue);
        }
       
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT l.lookup_Code,l.lookup_name,l.lookup_type,l.status ");
        strBuff.append(" FROM LOOKUPS l, LOOKUP_TYPES lt ");
        strBuff.append(" WHERE l.status = 'Y' AND l.lookup_type = lt.lookup_type  AND ");
        strBuff.append(" lt.lookup_type IN ('" + PretupsI.LMS_O2C_SERVICE_LIST + "', '" + PretupsI.LMS_C2C_SERVICE_LIST + "','" + PretupsI.LMS_C2S_SERVICE_LIST + "')");
        strBuff.append(" ORDER BY l.lookup_type");
        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        ArrayList list = new ArrayList();
        try( PreparedStatement  pstmt = con.prepareStatement(sqlSelect);ResultSet rs = pstmt.executeQuery();) {
            
           
            while (rs.next()) {
                if (rs.getString("lookup_type").equalsIgnoreCase(PretupsI.LMS_O2C_SERVICE_LIST)) {
                    ListValueVO vo = new ListValueVO(rs.getString("lookup_name"), rs.getString("lookup_Code") + ":" + "O2C");
                    vo.setType(rs.getString("lookup_type"));
                    list.add(vo);
                } else if (rs.getString("lookup_type").equalsIgnoreCase(PretupsI.LMS_C2C_SERVICE_LIST)) {
                    ListValueVO vo = new ListValueVO(rs.getString("lookup_name"), rs.getString("lookup_Code") + ":" + "C2C");
                    vo.setType(rs.getString("lookup_type"));
                    list.add(vo);
                } else if (rs.getString("lookup_type").equalsIgnoreCase(PretupsI.LMS_C2S_SERVICE_LIST)) {
                    ListValueVO vo = new ListValueVO(rs.getString("lookup_name"), rs.getString("lookup_Code") + ":" + "C2S");
                    vo.setType(rs.getString("lookup_type"));
                    list.add(vo);
                }
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadServicesList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadLookupServicesList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: serviceList:");
             	loggerValue.append(list.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return list;
    }

    public boolean isChannelUserExist(Connection con, String pNetworkID, String pUserMsisdn) throws BTSLBaseException {
        final String methodName = "isChannelUserExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pNetworkID=");
        	loggerValue.append(pNetworkID);
        	loggerValue.append(",pUserMsisdn=");
        	loggerValue.append(pUserMsisdn);
        	LOG.debug(methodName, loggerValue);
        }

         
         
        boolean found = false;
        StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM users WHERE  NETWORK_CODE = ? and MSISDN = ?");
        String selectQuery = sqlBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(selectQuery);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);) {
            
            pstmtSelect.setString(1, pNetworkID);
            pstmtSelect.setString(2, pUserMsisdn);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                found = true;
            }
        } 
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isChannelUserExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[isChannelUserExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: found:");
             	loggerValue.append(found);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return found;
    }

    public boolean isMappingExist(Connection con, String pUserMsisdn, String pSubsMSISDN) throws BTSLBaseException {
        final String methodName = "isMappingExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pSubsMSISDN=");
        	loggerValue.append(pSubsMSISDN);
        	loggerValue.append(",pUserMsisdn=");
        	loggerValue.append(pUserMsisdn);
        	LOG.debug(methodName, loggerValue);
        }

        
         
        boolean found = false;
        StringBuilder sqlBuff = new StringBuilder("SELECT 1 FROM USERS U,ACT_BONUS_SUBS_MAPPING absm ");
        sqlBuff.append("WHERE u.USER_ID=absm.USER_ID AND u.MSISDN =? AND absm.SUBSCRIBER_MSISDN=?");
        String selectQuery = sqlBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(selectQuery);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);) {
            
            pstmtSelect.setString(1, pUserMsisdn);
            pstmtSelect.setString(2, pSubsMSISDN);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                found = true;
            } else {
                found = false;
            }
        } 
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isMappingExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[isMappingExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: found:");
             	loggerValue.append(found);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return found;
    }

    public ListValueVO userProfileMap(Connection con, String pMsisdn) throws BTSLBaseException {
        final String methodName = "userProfileMap";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pMsisdn=");
        	loggerValue.append(pMsisdn);
        	LOG.debug(methodName, loggerValue);
        }
        
         
        ListValueVO listVO = null;
        StringBuilder sbf = new StringBuilder();
        sbf.append("SELECT u.user_id,u.msisdn,uop.set_id FROM users u,user_oth_profiles uop ");
        sbf.append("WHERE u.user_id=uop.user_id and u.msisdn=?");
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sbf.toString());
			LOG.debug(methodName, loggerValue);
		}
        String query = sbf.toString();
        try(PreparedStatement pstmt = con.prepareStatement(query);) {
            
            pstmt.setString(1, pMsisdn);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                listVO = new ListValueVO(rs.getString("msisdn"), rs.getString("set_id"));
            } else {
                listVO = new ListValueVO();
            }
        } 
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[userProfileMap]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "isAssociateExist", "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[userProfileMap]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: listVO:");
             	loggerValue.append(listVO.toString());
             	LOG.debug(methodName, loggerValue);
             }

        }
        return listVO;
    }

    public int deleteUserProfileMapping(Connection con, String pUserId, String setId) throws BTSLBaseException {
        final String methodName = "deleteUserProfileMapping";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pUserId=");
        	loggerValue.append(pUserId);
        	loggerValue.append(",setId=");
        	loggerValue.append(setId);
        	LOG.debug(methodName, loggerValue);
        }
         
        int deleteCount = 0;
        StringBuilder sbf = new StringBuilder();
        sbf.append("DELETE from user_oth_profiles WHERE user_id=? and set_id=?");
        String query = sbf.toString();
        try(PreparedStatement pstmt = con.prepareStatement(query);) {
            
            pstmt.setString(1, pUserId);
            pstmt.setString(2, setId);
            deleteCount = pstmt.executeUpdate();
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[deleteUserProfileMapping]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "isAssociateExist", "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[deleteUserProfileMapping]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting ");
            }
        }
        return deleteCount;
    }

    public boolean isSubscriberExist(Connection con, String pSubsMSISDN) throws BTSLBaseException {
        final String methodName = "isSubscriberExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pSubsMSISDN=");
        	loggerValue.append(pSubsMSISDN);
        	LOG.debug(methodName, loggerValue);
        }

         
       
        boolean found = false;
        StringBuilder sqlBuff = new StringBuilder();
        sqlBuff.append("SELECT 1 FROM ACT_BONUS_SUBS_MAPPING ");
        sqlBuff.append("WHERE status<>'N'AND SUBSCRIBER_MSISDN = ? ");
        String selectQuery = sqlBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(selectQuery);
			LOG.debug(methodName, loggerValue);
		}

        try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);) {
           
            pstmtSelect.setString(1, pSubsMSISDN);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                found = true;
            }
        }
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isSubscriberExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[isSubscriberExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: found:");
             	loggerValue.append(found);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return found;
    }

    public boolean isProfileAssociateWithUserExist(Connection con, String setId, String pUserId) throws BTSLBaseException {
        final String methodName = "isProfileAssociateWithUserExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: setId=");
        	loggerValue.append(setId);
        	loggerValue.append(",pUserId=");
        	loggerValue.append(pUserId);
        	LOG.debug(methodName, loggerValue);
        }

         
        
        boolean found = false;
        StringBuilder sqlBuff = new StringBuilder();
        sqlBuff.append("SELECT 1 FROM USER_OTH_PROFILES WHERE USER_ID = ? AND ");
        sqlBuff.append(" PROFILE_TYPE = ? AND SET_ID = ? ");

        String selectQuery = sqlBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(selectQuery);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);) {
            
            pstmtSelect.setString(1, pUserId);
            pstmtSelect.setString(2, PretupsI.ACT_PROF_TYPE);
            pstmtSelect.setString(3, setId);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                found = true;
            } else {
                return false;
            }
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isProfileAssociateWithUserExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[isProfileAssociateWithUserExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: found:");
             	loggerValue.append(found);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return found;
    }

    // public ArrayList validateUserdetails(Connection con,String
    // pZoneCode,String pDomainCode,String pCategoryCode,String
    // pChannelUser)
    public ArrayList validateUserdetails(Connection con, String pZoneCode, String pDomainCode, String pCategoryCode, String pChannelUser, String pUserIdLoggedUser) throws BTSLBaseException {
        final String methodName = "validateUserdetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pZoneCode=");
        	loggerValue.append(pZoneCode);
        	LOG.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
         
        ArrayList userList = new ArrayList();
       
        try {
           pstmtSelect=activationBonusLMSQry.validateUserdetailsQry(con, pDomainCode, pCategoryCode, pUserIdLoggedUser, pZoneCode, pChannelUser);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                userList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
            }
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[validateUserdetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[validateUserdetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: userList.size:");
             	loggerValue.append(userList.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return userList;
    }

    public ArrayList loadBonusPointDetails(Connection con, String pUserId, String pFromdate, String pTodate, String pCategoryCode, String pDomainCode, String pZoneCode) throws BTSLBaseException {
        final String methodName = "loadBonusPointDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pUserId=");
        	loggerValue.append(pUserId);
        	loggerValue.append(",pFromdate=");
        	loggerValue.append(pFromdate);
        	loggerValue.append(",pTodate=");
        	loggerValue.append(pTodate);
        	loggerValue.append(",pDomainCode=");
        	loggerValue.append(pDomainCode);
        	loggerValue.append(",pCategoryCode=");
        	loggerValue.append(pCategoryCode);
        	loggerValue.append(",pZoneCode=");
        	loggerValue.append(pZoneCode);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList detailsList = null;
        PreparedStatement pstmtSelect = null;
        
        RedemptionVO redemptionVO = null;
        try {
            pstmtSelect = activationBonusLMSQry.loadBonusPointDetailsQry( con, pDomainCode, pCategoryCode, pUserId, pZoneCode, pFromdate, pTodate);
           try(ResultSet rs = pstmtSelect.executeQuery();)
           {
            detailsList = new ArrayList();
            while (rs.next()) {
                redemptionVO = new RedemptionVO();
                redemptionVO.setUserName(rs.getString("user_name"));
                redemptionVO.setRedemptionDateStr(BTSLUtil.getDateStringFromDate(rs.getDate("points_date")));
                redemptionVO.setMsisdn(rs.getString("msisdn"));
                redemptionVO.setPointsRedeemedStr(PretupsBL.getDisplayAmount(rs.getLong("points")));
                detailsList.add(redemptionVO);
            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadBonusPointDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadBonusPointDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: detailsList.size:");
             	loggerValue.append(detailsList.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return detailsList;
    }

    public boolean suspendProfileSetVersion(Connection con, String setId, String pVersion, UserVO pUserVO, boolean pSetDel, boolean pVersionDel) throws BTSLBaseException {
        final String methodName = "suspendProfileSetVersion";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: setId=");
        	loggerValue.append(setId);
        	loggerValue.append(",pVersion=");
        	loggerValue.append(pVersion);
        	loggerValue.append(",pSetDel=");
        	loggerValue.append(pSetDel);
        	loggerValue.append(",pVersionDel=");
        	loggerValue.append(pVersionDel);
        	loggerValue.append(",pUserVO=");
        	loggerValue.append(pUserVO);
        	LOG.debug(methodName, loggerValue);
        }
        int deleteCountForSet = 0;
        int deleteCountForVersion = 0;
        boolean flag = false;
        PreparedStatement pstmtSetUpdate = null;
        PreparedStatement pstmtVersionUpdate = null;
        StringBuilder sbf = null;
        try {
            if (pSetDel) {
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set ps SET ");
                sbf.append("ps.status='S',ps.modified_by=?,ps.modified_on=? ");
                sbf.append("WHERE ps.set_id=?");
                String updateSetTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateSetTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version psv SET ");
                sbf.append("psv.status='S',psv.modified_by=?,psv.modified_on=? ");
                // OCM FIX2
                sbf.append("WHERE psv.set_id=? and psv.status='Y' ");
                String updateVersionTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateVersionTable);
        			LOG.debug(methodName, loggerValue);
        		}
                Date currentDate = new Date();
                pstmtSetUpdate = con.prepareStatement(updateSetTable);
                pstmtVersionUpdate = con.prepareStatement(updateVersionTable);
                pstmtSetUpdate.setString(1, pUserVO.getActiveUserID());
                pstmtSetUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtSetUpdate.setString(3, setId);
                pstmtVersionUpdate.setString(1, pUserVO.getActiveUserID());
                pstmtVersionUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtVersionUpdate.setString(3, setId);
            } else if (pVersionDel) {
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set ps SET ");
                sbf.append("ps.modified_by=?,ps.modified_on=? ");
                sbf.append("WHERE ps.set_id=?");
                String updateSetTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateSetTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version psv SET ");
                sbf.append("psv.status='S',psv.modified_by=?,psv.modified_on=? ");
                // OCM FIX2
                sbf.append("WHERE psv.set_id=? AND psv.version=?  and psv.status='Y' ");
                String updateVersionTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateVersionTable);
        			LOG.debug(methodName, loggerValue);
        		}
                Date currentDate = new Date();
                pstmtSetUpdate = con.prepareStatement(updateSetTable);
                pstmtVersionUpdate = con.prepareStatement(updateVersionTable);
                pstmtSetUpdate.setString(1, pUserVO.getActiveUserID());
                pstmtSetUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtSetUpdate.setString(3, setId);
                pstmtVersionUpdate.setString(1, pUserVO.getActiveUserID());
                pstmtVersionUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtVersionUpdate.setString(3, setId);
                pstmtVersionUpdate.setString(4, pVersion);
            }
            if (pstmtSetUpdate != null && pstmtVersionUpdate != null) {
                deleteCountForSet = pstmtSetUpdate.executeUpdate();
                deleteCountForVersion = pstmtVersionUpdate.executeUpdate();
            }
            if (deleteCountForSet > 0 && deleteCountForVersion > 0) {
                flag = true;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[suspendProfileSetVersion]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[suspendProfileSetVersion]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	try{
                if (pstmtSetUpdate!= null){
                	pstmtSetUpdate.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmtVersionUpdate!= null){
                	pstmtVersionUpdate.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing statement.", e);
              }
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: flag:");
             	loggerValue.append(flag);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return flag;
    }

    public ArrayList loadApprovalProfileList(Connection con, String networkCode, ArrayList profileList) throws BTSLBaseException {
        final String methodName = "loadApprovalProfileList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode=");
        	loggerValue.append(networkCode);
        	loggerValue.append(",profileList.size()=");
        	loggerValue.append(profileList.size());
        	LOG.debug(methodName, loggerValue);
        }
         
        ProfileSetLMSVO profileSetVO = null;
         
        try {
            StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT ps.profile_type,ps.set_id,ps.set_name,psv.version,ps.short_code,ps.status ");
            sbf.append("FROM profile_set ps, PROFILE_SET_VERSION psv ");
            sbf.append("WHERE ps.network_code=? AND ps.status<>'N' AND  psv.status='W' AND ps.set_id=psv.set_id AND ps.profile_type=? ORDER BY ps.set_name");
            String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
           try(PreparedStatement pstmt = con.prepareStatement(selectQuery);)
           {
            pstmt.setString(1, networkCode);
            pstmt.setString(2, PretupsI.LMS_PROFILE_TYPE);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                profileSetVO = new ProfileSetLMSVO();
                profileSetVO.setProfileType(rs.getString("profile_type"));
                profileSetVO.setSetId(rs.getString("set_id"));
                profileSetVO.setSetName(rs.getString("set_name"));
                profileSetVO.setLastVersion(rs.getString("version"));
                profileSetVO.setShortCode(rs.getString("short_code"));
                profileSetVO.setStatus(rs.getString("status"));
                profileList.add(profileSetVO);
            }
        } 
           }
           }catch (SQLException sqle) {
        	   loggerValue.setLength(0);
   			loggerValue.append(SQL_EXCEPTION);
   			loggerValue.append(sqle.getMessage());
   			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadApprovalProfileList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadApprovalProfileList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: profileList.size:");
             	loggerValue.append(profileList.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return profileList;
    }

    public boolean approveRejectProfileSet(Connection con, String setId, String pVersion, UserVO pUserVO, boolean pApprove, boolean pReject) throws BTSLBaseException {
        final String methodName = "approveRejectProfileSet";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: setId=");
        	loggerValue.append(setId);
        	loggerValue.append(",pVersion=");
        	loggerValue.append(pVersion);
        	loggerValue.append(",pApprove=");
        	loggerValue.append(pApprove);
        	loggerValue.append(",pReject=");
        	loggerValue.append(pReject);
        	loggerValue.append(",p_user ID=");
        	loggerValue.append(pUserVO.getActiveUserID());
        	LOG.debug(methodName, loggerValue);
        }
        int countForSet = 0;
        int countForVersion = 0;
        boolean flag = false;
        PreparedStatement pstmtSetUpdate = null;
        PreparedStatement pstmtVersionUpdate = null;
        PreparedStatement pstmtVersionSelect = null;
        StringBuilder sbf = null;
        ResultSet rs = null;
        int countProfileVersion = 0;
        try {
            if (pApprove) {
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set ps SET ");
                sbf.append("ps.status='Y',ps.modified_by=?,ps.modified_on=? ");
                sbf.append("WHERE ps.set_id=?");
                String updateSetTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateSetTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version psv SET ");
                sbf.append("psv.status='Y',psv.modified_by=?,psv.modified_on=? ");
                sbf.append("WHERE psv.set_id=? and psv.version=? ");
                String updateVersionTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateVersionTable);
        			LOG.debug(methodName, loggerValue);
        		}
                Date currentDate = new Date();
                pstmtSetUpdate = con.prepareStatement(updateSetTable);
                pstmtVersionUpdate = con.prepareStatement(updateVersionTable);
                pstmtSetUpdate.setString(1, pUserVO.getActiveUserID());
                pstmtSetUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtSetUpdate.setString(3, setId);
                pstmtVersionUpdate.setString(1, pUserVO.getActiveUserID());
                pstmtVersionUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtVersionUpdate.setString(3, setId);
                pstmtVersionUpdate.setString(4, pVersion);
            } else if (pReject) {
                sbf = new StringBuilder();
                sbf.append(" select version from PROFILE_SET_VERSION where set_id=? ");
                String selectVersionTable = sbf.toString();
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "SQL select Query :" + selectVersionTable);
                }
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set ps SET ");
                sbf.append("ps.status='N',ps.modified_by=?,ps.modified_on=? ");
                sbf.append("WHERE ps.set_id=?");
                String updateSetTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateSetTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version psv SET ");
                sbf.append("psv.status='N',psv.modified_by=?,psv.modified_on=? ");
                sbf.append("WHERE psv.set_id=? and psv.version=? ");
                String updateVersionTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(updateVersionTable);
        			LOG.debug(methodName, loggerValue);
        		}
                Date currentDate = new Date();
                pstmtVersionSelect = con.prepareStatement(selectVersionTable);
                pstmtSetUpdate = con.prepareStatement(updateSetTable);
                pstmtVersionUpdate = con.prepareStatement(updateVersionTable);

                pstmtVersionSelect.setString(1, setId);

                pstmtSetUpdate.setString(1, pUserVO.getActiveUserID());
                pstmtSetUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtSetUpdate.setString(3, setId);
                pstmtVersionUpdate.setString(1, pUserVO.getActiveUserID());
                pstmtVersionUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtVersionUpdate.setString(3, setId);
                pstmtVersionUpdate.setString(4, pVersion);

                rs = pstmtVersionSelect.executeQuery();
                while (rs.next()) {
                    countProfileVersion++;

                }
            }
            if (!(countProfileVersion > 1)) {
                countForSet = pstmtSetUpdate.executeUpdate();
            }

            countForVersion = pstmtVersionUpdate.executeUpdate();
            if (countForVersion > 0) {
                flag = true;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[approveRejectProfileSet]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[approveRejectProfileSet]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
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
        	try{
                if (pstmtVersionSelect!= null){
                	pstmtVersionSelect.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing result set.", e);
              }
        	try{
                if (pstmtSetUpdate!= null){
                	pstmtSetUpdate.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing result set.", e);
              }
        	try{
                if (pstmtVersionUpdate!= null){
                	pstmtVersionUpdate.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing result set.", e);
              }
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: flag:");
             	loggerValue.append(flag);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return flag;
    }

    // By Zeeshan Aleem
    public ProfileSetLMSVO loadMessageList(Connection con, String profileId) throws BTSLBaseException {
        final String methodName = "loadMessageList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: profileId=");
        	loggerValue.append(profileId);
        	LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ProfileSetLMSVO profileSetLMSVO = null;
        String messageCode = null;
        StringBuilder strBuff = null;
        StringBuilder strBuff2 = null;
        String sqlSelect = null;
        String sqlSelect2 = null;

        HashMap<String, String> profileDetails = fetchLMSProfleDetails(con, profileId);
        String promotionType = null;
        String optInOutEnabled = null;
        String messageManagementEnabled = null;
        if (profileDetails != null) {
            promotionType = profileDetails.get("PROMOTION_TYPE");
            optInOutEnabled = profileDetails.get("OPT_IN_OUT_ENABLED");
            messageManagementEnabled = profileDetails.get("MESSAGE_MANAGEMENT_ENABLED");
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "promotionType = " + promotionType + ", optInOutEnabled=" + optInOutEnabled + " ,messageManagementEnabled=" + messageManagementEnabled);
            }
        }

        try {
            int index = 1;
            strBuff = new StringBuilder();
            strBuff2 = new StringBuilder();
            profileSetLMSVO = new ProfileSetLMSVO();
            strBuff.append("SELECT messageCode,message1,message2 from MESSAGES_MASTER  where messageCode=? ");
            strBuff2.append("SELECT messageCode,message1,message2 from MESSAGES_MASTER  where messageCode=? ");
            sqlSelect = strBuff.toString();
            sqlSelect2 = strBuff2.toString();
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(promotionType) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(optInOutEnabled) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
                messageCode = PretupsI.OPTINOUT_WEL_MESSAGE + "_" + profileId;
            } else if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(promotionType) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(optInOutEnabled) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
                messageCode = PretupsI.OPTINOUT_TRA_WEL_MSG + "_" + profileId;
            } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(promotionType) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
                messageCode = PretupsI.TRA_WEL_MESSAGE + "_" + profileId;
            } else {
                messageCode = PretupsI.WEL_MESSAGE + "_" + profileId;
            }

            profileSetLMSVO.setPromotionType(promotionType);
            profileSetLMSVO.setOptInOut(optInOutEnabled);
            profileSetLMSVO.setMsgConfEnableFlag(messageManagementEnabled);

            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlSelect);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(index++, messageCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileSetLMSVO.setLang1welcomemsg(rs.getString("message1"));
                profileSetLMSVO.setLang2welcomemsg(rs.getString("message2"));
                profileSetLMSVO.setMessageCode(rs.getString("messageCode"));
            }
            if (!PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equals(promotionType)) {
                messageCode = PretupsI.SUCCESS_MESSAGE + "_" + profileId;
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(sqlSelect);
        			LOG.debug(methodName, loggerValue);
        		}
                pstmt2 = con.prepareStatement(sqlSelect2);
                pstmt2.clearParameters();
                pstmt2.setString(1, messageCode);
                rs1 = pstmt2.executeQuery();
                while (rs1.next()) {
                    profileSetLMSVO.setLang1seccessmsg(rs1.getString("message1"));
                    profileSetLMSVO.setLang2seccessmsg(rs1.getString("message2"));
                    profileSetLMSVO.setMessageCode(rs1.getString("messageCode"));
                }
                messageCode = PretupsI.FAILURE_MESSAGE + "_" + profileId;
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(sqlSelect);
        			LOG.debug(methodName, loggerValue);
        		}
                pstmt2.clearParameters();
                pstmt2.setString(1, messageCode);
                rs2 = pstmt2.executeQuery();
                while (rs2.next()) {
                    profileSetLMSVO.setLang1failuremsg(rs2.getString("message1"));
                    profileSetLMSVO.setLang2failuremsg(rs2.getString("message2"));
                    profileSetLMSVO.setMessageCode(rs2.getString("messageCode"));
                }
            }
            profileSetLMSVO.setSetId(profileId);
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityDAO[loadMessageList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);

        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityDAO[loadMessageList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing result set.", e);
              }
        	try{
                if (rs1!= null){
                	rs1.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing result set.", e);
              }
        	try{
                if (rs2!= null){
                	rs2.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing result set.", e);
              }
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing result set.", e);
              }
        	try{
                if (pstmt2!= null){
                	pstmt2.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing result set.", e);
              }
            if (LOG.isDebugEnabled()) {
                LOG.debug("loadMessageList", "Exiting: ");
            }
        }

        return profileSetLMSVO;
    }

    public int deleteMessages(Connection con, String setId) throws BTSLBaseException {
        final String methodName = "deleteMessages";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:");
        	LOG.debug(methodName, loggerValue);
        }
         
        int deleteCount = 0;
        int flag = 0;

        try {
            String messageCode = null;
            StringBuilder sbf = new StringBuilder();
            sbf.append("DELETE from MESSAGES_MASTER WHERE messageCode=?");
            String query = sbf.toString();
            try(PreparedStatement pstmt = con.prepareStatement(query);)
            {
            messageCode = PretupsI.WEL_MESSAGE + "_" + setId;
            pstmt.setString(1, messageCode);
            deleteCount = pstmt.executeUpdate();
            if (deleteCount == 1) {
                flag = 1;
            }
            messageCode = PretupsI.SUCCESS_MESSAGE + "_" + setId;
            pstmt.clearParameters();
            pstmt.setString(1, messageCode);
            deleteCount = pstmt.executeUpdate();
            if (deleteCount == 1) {
                flag = 1;
            }
            messageCode = PretupsI.FAILURE_MESSAGE + "_" + setId;
            pstmt.clearParameters();
            pstmt.setString(1, messageCode);
            deleteCount = pstmt.executeUpdate();
            if (deleteCount == 1) {
                flag = 1;
            }
        } 
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[deleteMessages]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[deleteMessages]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting ");
            }
        }
        return flag;
    }

    public boolean updateMessageList(Connection con, ProfileSetLMSVO profileSetLMSVO, String profileId) throws BTSLBaseException {
        final String methodName = "updateMessageList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	LOG.debug(methodName, loggerValue);
        }
        boolean flag = false;
        
        try {
            StringBuilder strBuff = new StringBuilder();
            strBuff.append("update MESSAGES_MASTER  set MESSAGE1=?,MESSAGE2=? where messageCode=? ");
            String sqlSelect = strBuff.toString();
            try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);)
            {
            String messageCode = PretupsI.WEL_MESSAGE + "_" + profileId;
            String messageCode2 = PretupsI.OPTINOUT_WEL_MESSAGE + "_" + profileId;
            String messageCode3 = PretupsI.OPTINOUT_TRA_WEL_MSG + "_" + profileId;
            String messageCode4 = PretupsI.TRA_WEL_MESSAGE + "_" + profileId;
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            pstmt.setString(1, profileSetLMSVO.getLang1welcomemsg());
            pstmt.setString(2, profileSetLMSVO.getLang2welcomemsg());
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(profileSetLMSVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetLMSVO.getOptInOut()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetLMSVO.getMsgConfEnableFlag())) {
                pstmt.setString(3, messageCode2);
            } else if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetLMSVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetLMSVO.getOptInOut()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetLMSVO.getMsgConfEnableFlag())) {
                pstmt.setString(3, messageCode3);
            } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetLMSVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetLMSVO.getMsgConfEnableFlag())) {
                pstmt.setString(3, messageCode4);
            } else {
                pstmt.setString(3, messageCode);
            }
            int countForVersion = pstmt.executeUpdate();
            if (countForVersion > 0) {
                flag = true;
            }
            if (PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(profileSetLMSVO.getPromotionType())) {
                messageCode = PretupsI.SUCCESS_MESSAGE + "_" + profileId;
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(sqlSelect);
        			LOG.debug(methodName, loggerValue);
        		}
                pstmt.clearParameters();
                pstmt.setString(1, profileSetLMSVO.getLang1seccessmsg());
                pstmt.setString(2, profileSetLMSVO.getLang2seccessmsg());
                pstmt.setString(3, messageCode);
                int countForVersion1 = pstmt.executeUpdate();
                if (countForVersion1 > 0) {
                    flag = true;
                }
                messageCode = PretupsI.FAILURE_MESSAGE + "_" + profileId;
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append(QUERY_KEY);
        			loggerValue.append(sqlSelect);
        			LOG.debug(methodName, loggerValue);
        		}
                pstmt.clearParameters();
                pstmt.setString(1, profileSetLMSVO.getLang1failuremsg());
                pstmt.setString(2, profileSetLMSVO.getLang2failuremsg());
                pstmt.setString(3, messageCode);
                int countForVersion2 = pstmt.executeUpdate();
                if (countForVersion2 > 0) {
                    flag = true;
                }
            }
        }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityDAO[updateMessageList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityDAO[updateMessageList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: flag:");
             	loggerValue.append(flag);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return flag;
    }

    public ArrayList loadpromotionList(Connection con) throws BTSLBaseException {
        final String methodName = "loadpromotionList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered:");
        	LOG.debug(methodName, loggerValue);
        }
         
        ProfileSetLMSVO profileSetLMSVO = null;
        ArrayList list = null;
        int index = 1;
        try {
            list = new ArrayList();
            // Added by Diwakar for not loading the expired profile.
            String sqlSelect=activationBonusLMSQry.loadpromotionListQry();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlSelect);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);)
            {
            pstmt.setString(index++, PretupsI.YES);
            pstmt.setString(index++, PretupsI.SUSPEND);
            pstmt.setString(index++, PretupsI.LMS);
            try(ResultSet rs = pstmt.executeQuery();)
            {

            while (rs.next()) {
                profileSetLMSVO = new ProfileSetLMSVO();
                profileSetLMSVO.setSetId(rs.getString("set_id"));
                profileSetLMSVO.setSetName(rs.getString("set_name"));
                list.add(profileSetLMSVO);
            }
        }
            }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityDAO[loadpromotionList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityDAO[loadpromotionList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: ");
            }
        }
        return list;
    }

    public int isprofileSingleVersionExist(Connection con, String setId) throws BTSLBaseException {
        final String methodName = "isprofileSingleVersionExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: setId=");
        	loggerValue.append(setId);
        	LOG.debug(methodName, loggerValue);
        }
        int verCountExist = 0;
         
        try {
            StringBuilder sbf = new StringBuilder();
            sbf.append("select count(*) as max from PROFILE_SET_VERSION where set_id=? and status in ('Y','W')");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sbf.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            String selectQuery = sbf.toString();
            try(PreparedStatement pstmt = con.prepareStatement(selectQuery);)
            {
            pstmt.setString(1, setId);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                verCountExist = rs.getInt("max");
            }
        }
            }
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileSingleVersionExist] ", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "isprofileCategoryMapp", "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: verCountExist:");
             	loggerValue.append(verCountExist);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return verCountExist;
    }

    public int addMessage(Connection con, String pCode, String pLang1, String pLang2) throws BTSLBaseException {
        final String methodName = "addMessage";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	LOG.debug(methodName, loggerValue);
        }
        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;
        try {
            StringBuilder updateQueryBuff = new StringBuilder(" UPDATE PROFILE_SET_VERSION SET ");
            updateQueryBuff.append(" MESSAGE1=?,MESSAGE2=? WHERE set_id=?  ");
            String insertQuery = updateQueryBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(insertQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmtUpdate = con.prepareStatement(insertQuery);
            pstmtUpdate.setString(1, pLang1);
            pstmtUpdate.setString(2, pLang2);
            pstmtUpdate.setString(3, pCode);
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBounsDAO[updateOtherProfileForAssociation]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[updateOtherProfileForAssociation]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: updateCount:");
             	loggerValue.append(updateCount);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return updateCount;
    }

    public boolean isprofileNmaeExist(Connection con, String profileName) throws BTSLBaseException {
        final String methodName = "isprofileNmaeExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: profileName=");
        	loggerValue.append(profileName);
        	LOG.debug(methodName, loggerValue);
        }
        boolean isexists = false;
         
        try {
            StringBuilder sbf = new StringBuilder();
            sbf.append(" select set_name from profile_set where set_name=? and status in ( 'Y','S','W')");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sbf.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            String selectQuery = sbf.toString();
           try( PreparedStatement pstmt = con.prepareStatement(selectQuery);)
           {
            pstmt.setString(1, profileName);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                isexists = true;
            }
        }
           }
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "isprofileCategoryMapp", "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileSingleVersionExist] ", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
        	 if (LOG.isDebugEnabled()) {
              	loggerValue.setLength(0);
              	loggerValue.append("Exiting: isexists:");
              	loggerValue.append(isexists);
              	LOG.debug(methodName, loggerValue);
              }
        }
        return isexists;
    }

    public boolean isprofileAssociated(Connection con, String setID) throws BTSLBaseException {
        final String methodName = "isprofileAssociated";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: setID=");
        	loggerValue.append(setID);
        	LOG.debug(methodName, loggerValue);
        }
        boolean isexists = false;
         
        
        try {
            StringBuilder sbf = new StringBuilder();
            sbf.append(" select ch.user_id from channel_users ch,users u, profile_set ps ");
            sbf.append(" where u.user_id=ch.user_id and ch.lms_profile=ps.set_id and ps.set_id=? ");
            sbf.append(" and u.status='Y' ");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sbf.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            String selectQuery = sbf.toString();
            try(PreparedStatement pstmt = con.prepareStatement(selectQuery);)
            {
            pstmt.setString(1, setID);
           try(ResultSet rs = pstmt.executeQuery();)
           {
            if (rs.next()) {
                isexists = true;
            }
           }
        } 
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileAssociated]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
        	if (LOG.isDebugEnabled()) {
              	loggerValue.setLength(0);
              	loggerValue.append("Exiting: isexists:");
              	loggerValue.append(isexists);
              	LOG.debug(methodName, loggerValue);
              }
        }
        return isexists;
    }

    public boolean isprofileExpired(Connection con, String setId, String version) throws BTSLBaseException {
        final String methodName = "isprofileExpired";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: setId=");
        	loggerValue.append(setId);
        	LOG.debug(methodName, loggerValue);
        }
        int verCountExist = 0;
         
        boolean isexpired = true;
        try {
			String selectQuery=activationBonusLMSQry.isprofileExpiredQry();
			if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement pstmt = con.prepareStatement(selectQuery);)
            {
            pstmt.setString(1, setId);
            pstmt.setString(2, version);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                isexpired = false;
            }
        }
            }
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
        	if (LOG.isDebugEnabled()) {
              	loggerValue.setLength(0);
              	loggerValue.append("Exiting: isexpired:");
              	loggerValue.append(isexpired);
              	LOG.debug(methodName, loggerValue);
              }
        }
        return isexpired;
    }

    public boolean isprofileActive(Connection con, String setId, String version) throws BTSLBaseException {
        final String methodName = "isprofileActive";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: setId=");
        	loggerValue.append(setId);
        	LOG.debug(methodName, loggerValue);
        }
        int verCountExist = 0;
         
        boolean isexpired = true;
        try {
            StringBuilder sbf = new StringBuilder();
            sbf.append("select set_id from PROFILE_SET_VERSION where set_id=? and version=? and status='Y' ");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sbf.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            String selectQuery = sbf.toString();
           try(PreparedStatement pstmt = con.prepareStatement(selectQuery);)
           {
            pstmt.setString(1, setId);
            pstmt.setString(2, version);
            try( ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                isexpired = false;
            }
        } 
           }
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
        	if (LOG.isDebugEnabled()) {
              	loggerValue.setLength(0);
              	loggerValue.append("Exiting: isexpired:");
              	loggerValue.append(isexpired);
              	LOG.debug(methodName, loggerValue);
              }
        }
        return isexpired;
    }

    public int addActivationBonusMessages(Connection con, ProfileSetLMSVO pProfileSetVO, String pRequestType) throws BTSLBaseException {
         
        int insertCount = 0;
        StringBuilder strBuff = null;
        final String methodName = "addActivationBonusMessages";
        String insertUpdateQuery = null;
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: pProfileSetVO=");
        	loggerValue.append(pProfileSetVO.toString());
        	loggerValue.append(",pRequestType=");
        	loggerValue.append(pRequestType);
        	LOG.debug(methodName, loggerValue);
        }
        try {

            // if request is for add activation profile

            strBuff = new StringBuilder();
            strBuff.append("Insert into MESSAGES_MASTER(MESSAGE_TYPE, messageCode, DEFAULT_MESSAGE,");
            strBuff.append(" NETWORK_CODE, MESSAGE1,  MESSAGE2)");
            strBuff.append(" values ");
            strBuff.append(" (?,?,?,?,?,?)");
            insertUpdateQuery = strBuff.toString();

            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(insertUpdateQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement psmtInsertUpdate = con.prepareStatement(insertUpdateQuery);)
            {
            psmtInsertUpdate.setString(1, "ALL");
            psmtInsertUpdate.setString(2, pProfileSetVO.getMessageCode());
            psmtInsertUpdate.setString(3, pProfileSetVO.getdefaultMessage());
            psmtInsertUpdate.setString(4, pProfileSetVO.getNetworkCode());
            psmtInsertUpdate.setString(5, pProfileSetVO.getMessage1());
            psmtInsertUpdate.setString(6, pProfileSetVO.getMessage2());
            insertCount = psmtInsertUpdate.executeUpdate();

        } 
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addActivationBonusMessages]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addActivationBonusMessages]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: insertCount:");
             	loggerValue.append(insertCount);
             	LOG.debug(methodName, loggerValue);
             }
        } // end of finally
        return insertCount;
    }

    public ArrayList loadpromotionListForMessage(Connection con) throws BTSLBaseException {
        final String methodName = "loadpromotionListForMessage";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	LOG.debug(methodName, loggerValue);
        }
        ProfileSetLMSVO profileSetLMSVO = null;
        ArrayList list = null;
        ArrayList list1 = null;
        try {
            StringBuilder strBuff = new StringBuilder();
            list = new ArrayList();
            list1 = new ArrayList();
            strBuff.append("SELECT set_id,set_name ");
            strBuff.append(" from profile_set where status in (?) and profile_type=? and MESSAGE_MANAGEMENT_ENABLED='Y'");// Brajesh
            String sqlSelect = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sqlSelect);
    			LOG.debug(methodName, loggerValue);
    		}
           try( PreparedStatement pstmt = con.prepareStatement(sqlSelect);)
           {
            pstmt.setString(1, PretupsI.YES);
            pstmt.setString(2, PretupsI.LMS);
           try(  ResultSet rs = pstmt.executeQuery();)
           {
            while (rs.next()) {
                list1 = new ArrayList();
                profileSetLMSVO = new ProfileSetLMSVO();
                profileSetLMSVO.setSetId(rs.getString("set_id"));
                profileSetLMSVO.setSetName(rs.getString("set_name"));
                list.add(profileSetLMSVO);
            }

        } 
           }
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityDAO[loadpromotionListForMessage]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityDAO[loadpromotionListForMessage]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: ");
            }
        }

        return list;
    }

    public boolean isMessageExists(Connection con, String messageCode) throws BTSLBaseException {
        final String methodName = "isMessageExists";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: messageCode=");
        	loggerValue.append(messageCode);
        	LOG.debug(methodName, loggerValue);
        }
         
        
        boolean isExists = false;
        try {
            StringBuilder sbf = new StringBuilder();
            sbf.append("select messageCode from MESSAGES_MASTER where messageCode=? ");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sbf.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            String selectQuery = sbf.toString();
            try(PreparedStatement pstmt = con.prepareStatement(selectQuery);)
            {
            pstmt.setString(1, messageCode);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                isExists = true;
            }
            }
        }
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isMessageExists]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isMessageExists]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
        	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: isExists:");
             	loggerValue.append(isExists);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return isExists;
    }

    public void loadMapForLMSAssociation(Connection con, String pGeographyCode, String categoryCode, String userId, String gradeCode, Map<String, String> msisdnUserIDMap, WritableWorkbook workbook) throws BTSLBaseException {
        final String methodName = "loadMapForLMSAssociation";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList batchList = new ArrayList();
        int sheetNo = 0;
        try {
			pstmt = activationBonusLMSQry.loadMapForLMSAssociationQry(con, gradeCode, categoryCode, userId, pGeographyCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {

                msisdnUserIDMap.put(rs.getString("msisdn"), rs.getString("user_id"));

            }
            if (!(batchList.isEmpty())) {
                createWorkbook(batchList, sheetNo, workbook, locale, messages);
                batchList = new ArrayList();
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityDAO[loadMapForLMSAssociation]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityDAO[loadMapForLMSAssociation]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing result set.", e);
              }
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  LOG.error("An error occurred closing result set.", e);
              }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: ");
            }
        }
    }

    public int removeLmsProfileMapping(ArrayList userList, String setId, String categoryCode, String geographyCode, String gradeCode, String userID, String networkCode) throws BTSLBaseException {
        String methodName = "removeLmsProfileMapping";
        int updateCount = 0;
        int i = 0;
        int usercount = 0;
        ChannelUserVO channelUserVO = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userList=");
        	loggerValue.append(userList);
        	LOG.debug(methodName, loggerValue);
        }
         
        StringBuilder updateQueryBuff = new StringBuilder("update Channel_users set LMS_PROFILE='', lms_profile_updated_on=? ");
        updateQueryBuff.append(" where user_id=?");

        String updateQuery = updateQueryBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}

        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            try(PreparedStatement pstmtUpdate =con.prepareStatement(updateQuery);)
            {
            int   userLists=userList.size();	
            for (i = 0; i <userLists; i++) {
                channelUserVO = new ChannelUserVO();
                channelUserVO = (ChannelUserVO) userList.get(i);
                pstmtUpdate.clearParameters();
                pstmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(new Date()));
                pstmtUpdate.setString(2, channelUserVO.getUserID());
                updateCount = pstmtUpdate.executeUpdate();
                if (updateCount == 1) {
                	mcomCon.finalCommit();
                    usercount++;
                } else {
                	mcomCon.finalRollback();
                }
            }
        }
        }

        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusLMSDAO[removeLmsProfileMapping]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        }

        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusLMSDAO[removeLmsProfileMapping]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        }

        finally {
        	
			if (mcomCon != null) {
				mcomCon.close("ActivationBonusLMSDAO#removeLmsProfileMapping");
				mcomCon = null;
			}
			if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: updateCount:");
             	loggerValue.append(updateCount);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return usercount;
    }

    public void createWorkbook(ArrayList dataList, int sheetNo, WritableWorkbook workbook, Locale locale, MessageResources messages) {
        final String methodName = "createWorkbook";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	LOG.debug(methodName, loggerValue);
        }
        String fileArr[][] = null;
        int cols = 4;
        int rows = dataList.size() + 1;
        fileArr = new String[rows][cols]; // ROW-COL
        fileArr[0][0] = "lmsprofile.xlsheading.label.msisdn";
        fileArr[0][1] = "lmsprofile.xlsheading.label.associate.currently";
        fileArr[0][2] = "lmsprofile.xlsheading.label.associate.required";
        fileArr[0][3] = "lmsprofile.xlsheading.label.controlgroup.required";
        try {
            fileArr = this.convertTo2dArray(fileArr, dataList, rows);
            ExcelRW excelRW = new ExcelRW();
            excelRW.writeMultipleExcelNew(workbook, ExcelFileIDI.BATCH_FOC_INITIATE, fileArr, messages, locale, sheetNo);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited:");
            }
        }

    }

    private String[][] convertTo2dArray(String[][] fileArr, ArrayList dataList, int pRows) throws BTSLBaseException {
        final String methodName = "convertTo2dArray";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: fileArr=");
        	loggerValue.append(fileArr);
        	loggerValue.append(",dataList=");
        	loggerValue.append(dataList);
        	LOG.debug(methodName, loggerValue);
        }
        try {

            ChannelUserVO channelUserVO = null;
            int rows = 0;
            int cols;
            int dataLists=dataList.size();
            for (int i = 0; i < dataLists; i++) {
                String key = String.valueOf(i + 1);
                channelUserVO = (ChannelUserVO) dataList.get(i);
                // Only those records are written into the xls file for which
                // status='Y' and insuspend='N'

                {
                    rows++;
                    if (rows >= pRows) {
                        break;
                    }
                    cols = 0;
                    fileArr[rows][cols++] = channelUserVO.getMsisdn();

                    fileArr[rows][cols++] = channelUserVO.getUserProfileID();// Y
                                                                               // or
                                                                               // N
                                                                               // (for
                                                                               // profile
                                                                               // to
                                                                               // be
                                                                               // associated
                                                                               // or
                                                                               // not)
                    fileArr[rows][cols++] = "";// Y or N (for profile to be
                                                 // associated or not)
                    if (PretupsI.YES.equalsIgnoreCase(channelUserVO.getControlGroup())) {
                        fileArr[rows][cols++] = channelUserVO.getControlGroup();// Y
                                                                                  // or
                                                                                  // N
                                                                                  // (for
                                                                                  // control
                                                                                  // group
                                                                                  // to
                                                                                  // be
                                                                                  // associated
                                                                                  // or
                                                                                  // not)
                    } else {
                        fileArr[rows][cols++] = "";
                    }
                }

            }
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "Exception in converting to 2-D Array.",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: fileArr:");
             	loggerValue.append(fileArr);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return fileArr;
    }

    private String generateCommaString(ArrayList list) throws BTSLBaseException {
        final String methodName = "generateCommaString";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: list=");
        	loggerValue.append(list);
        	LOG.debug(methodName, loggerValue);
        }
        String commaStr = "";
        String catArr[] = new String[1];
        String listStr = null;
        try {
            int size = list.size();
            ListValueVO listVO = null;
            for (int i = 0; i < size; i++) {
                listVO = (ListValueVO) list.get(i);
                listStr = listVO.getValue();
                if (listStr.indexOf(':') != -1) {
                    catArr = listStr.split(":");
                    listStr = catArr[1]; // for category code
                }
                commaStr = commaStr + "'" + listStr + "',";
            }
            commaStr = commaStr.substring(0, commaStr.length() - 1);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "Exception in generating Comma String.",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: commaStr:");
             	loggerValue.append(commaStr);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return commaStr;
    }

    public HashMap<String, String> fetchLMSProfleDetails(Connection con, String setId) throws BTSLBaseException {
        final String methodName = "fetchLMSProfleDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: setId=");
        	loggerValue.append(setId);
        	LOG.debug(methodName, loggerValue);
        }
       
        
        HashMap<String, String> map = null;
        try {
            StringBuilder sbf = new StringBuilder();
            sbf.append("select PROMOTION_TYPE,OPT_IN_OUT_ENABLED,MESSAGE_MANAGEMENT_ENABLED from PROFILE_SET where set_id=? ");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(sbf.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            String selectQuery = sbf.toString();
            try(PreparedStatement pstmt = con.prepareStatement(selectQuery);)
            {
            pstmt.setString(1, setId);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            map = new HashMap<String, String>();
            if (rs.next()) {
                map.put("PROMOTION_TYPE", rs.getString("PROMOTION_TYPE"));
                map.put("OPT_IN_OUT_ENABLED", rs.getString("OPT_IN_OUT_ENABLED"));
                map.put("MESSAGE_MANAGEMENT_ENABLED", rs.getString("MESSAGE_MANAGEMENT_ENABLED"));
            }
            OracleUtil.closeQuietly(rs);
            OracleUtil.closeQuietly(pstmt);
        }
            }
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " ActivationBonusDAO[ " + methodName + "]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " ActivationBonusDAO[" + methodName + "]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
          	
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: map:");
             	loggerValue.append(map.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return map;
    }

    public ArrayList loadLmsProfileListWithStatus(Connection con, String networkCode, ArrayList profileList) throws BTSLBaseException {
        final String methodName = "loadLmsProfileListWithStatus";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: networkCode=");
        	loggerValue.append(networkCode);
        	LOG.debug(methodName, loggerValue);
        }

        
        
        ActivationProfileCombinedLMSVO profileDetailsCombinedVO = null;

        try {
            StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT distinct ps.set_id,ps.set_name,ps.short_code,psv.applicable_from, ");
            sbf.append(" ps.last_version,psv.applicable_to ");
            sbf.append(" FROM PROFILE_SET ps,PROFILE_SET_VERSION psv ");
            sbf.append(" WHERE psv.set_id = ps.set_id ");
            sbf.append(" AND psv.VERSION = ps.LAST_VERSION AND ps.status NOT IN ('N','R','S') ");
            sbf.append(" AND ps.profile_type=?  AND ps.network_code=?  ORDER BY ps.set_name");
            String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement pstmt = con.prepareStatement(selectQuery);)
            {
            short index = 1;
            pstmt.setString(index++, PretupsI.LMS_PROFILE_TYPE);
            pstmt.setString(index++, networkCode);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                profileDetailsCombinedVO = new ActivationProfileCombinedLMSVO();
                profileDetailsCombinedVO.setSetName(rs.getString("set_name"));
                profileDetailsCombinedVO.setShortCode(rs.getString("short_code"));
                profileDetailsCombinedVO.setLastVersion(Integer.parseInt(rs.getString("last_version")));
                profileDetailsCombinedVO.setSetId(rs.getString("set_id"));
				if(LOG.isDebugEnabled()) {
					LOG.debug(methodName,rs.getString("set_name")+","+new Date()+","+BTSLUtil.getDifferenceInUtilDates(new Date(),rs.getTimestamp("applicable_to"))+","+rs.getTimestamp("applicable_to")+", "+ ((rs.getTimestamp("applicable_to").getTime() - new Date().getTime())/(1000*60*60)));
				}
                // Handling of Expired profile
                if (BTSLUtil.getDifferenceInUtilDates(new Date(), rs.getTimestamp("applicable_to")) <= -1  || ((rs.getTimestamp("applicable_to").getTime() - new Date().getTime())/(1000*60*60)) < 0) {
                    profileDetailsCombinedVO.setLmsProfileExpiredFlag("Y");
                } else {
                    profileDetailsCombinedVO.setLmsProfileExpiredFlag("N");
                }
                profileDetailsCombinedVO.setSetId(rs.getString("set_id") + "_" + profileDetailsCombinedVO.getLmsProfileExpiredFlag());
                profileList.add(profileDetailsCombinedVO);
            }
        } 
            }
        }catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadLmsProfileListWithStatus]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadLmsProfileListWithStatus]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }
        return profileList;
    }
    
    public Map<String,Integer>  countOfTotalVersionSuspendActive(Connection p_con,String p_setID) throws BTSLBaseException
	{
		final String methodName = "countOfTotalVersionSuspendActive";
		 StringBuilder loggerValue= new StringBuilder();
	        if (LOG.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered: p_setID=");
	        	loggerValue.append(p_setID);
	        	LOG.debug(methodName, loggerValue);
	        }
		Map<String,Integer> countOftotalVersionSuspendActive = new HashMap<String,Integer>(1);
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try
		{
			StringBuilder selectQueryBuff =	new StringBuilder(" select totalVersion, totalSuspendedVersion, totalActiveVersion ");
			selectQueryBuff.append(" from "); 
			selectQueryBuff.append("( ");
			selectQueryBuff.append("select count(set_id) as totalVersion from PROFILE_SET_VERSION "); 
			selectQueryBuff.append("where SET_ID= ? ");
			selectQueryBuff.append(") X ");
			selectQueryBuff.append(", ");
			selectQueryBuff.append("( ");
			selectQueryBuff.append("select count(set_id) as totalSuspendedVersion from PROFILE_SET_VERSION "); 
			selectQueryBuff.append("where SET_ID= ? ");
			selectQueryBuff.append("and status= ? ");
			selectQueryBuff.append(") Y ");
			selectQueryBuff.append(", ");
			selectQueryBuff.append("( ");
			selectQueryBuff.append("select count(set_id) as totalActiveVersion from PROFILE_SET_VERSION "); 
			selectQueryBuff.append("where SET_ID= ? ");
			selectQueryBuff.append("and status= ? ");
			selectQueryBuff.append(") Z ");
			if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(selectQueryBuff.toString());
    			LOG.debug(methodName, loggerValue);
    		}
			String selectQuery=selectQueryBuff.toString();
			pstmt=p_con.prepareStatement(selectQuery);
			pstmt.setString(1,p_setID);
			pstmt.setString(2, p_setID);
			pstmt.setString(3, PretupsI.SUSPEND);
			pstmt.setString(4, p_setID);
			pstmt.setString(5, PretupsI.YES);
			rs=pstmt.executeQuery();
			if(rs.next())
			{
				countOftotalVersionSuspendActive.put("totalVersion", rs.getInt("totalVersion"));
				countOftotalVersionSuspendActive.put("totalSuspendedVersion", rs.getInt("totalSuspendedVersion"));
				countOftotalVersionSuspendActive.put("totalActiveVersion", rs.getInt("totalActiveVersion"));
			}
		}
		catch (SQLException sqle)
		{
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName,sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ActivationBonusDAO["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
		} // end of catch
		catch (Exception e)
		{
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
			LOG.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ActivationBonusDAO["+methodName+"]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing",e);
		} // end of catch
		finally
		{
			try 
			{
				if (rs != null)
				{
					rs.close();
					}
			} 
			catch (Exception e)
			{
				LOG.errorTrace(methodName,e);
			}
			try
			{
				if (pstmt != null)
				{
					pstmt.close();
				}
			} 
			catch (Exception e)
			{
				LOG.errorTrace(methodName,e);
			}
			if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: countOftotalVersionSuspendActive:");
             	loggerValue.append(countOftotalVersionSuspendActive);
             	LOG.debug(methodName, loggerValue);
             }
		}
		return countOftotalVersionSuspendActive;
	}

}
