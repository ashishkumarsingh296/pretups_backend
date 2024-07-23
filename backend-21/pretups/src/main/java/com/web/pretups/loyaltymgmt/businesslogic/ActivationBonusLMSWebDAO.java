package com.web.pretups.loyaltymgmt.businesslogic;

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
import com.btsl.pretups.loyaltymgmt.businesslogic.ActivationProfileCombinedLMSVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.ProfileSetDetailsLMSVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.ProfileSetLMSVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.ProfileSetVersionLMSVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.xl.ExcelRW;

import jxl.write.WritableWorkbook;

public class ActivationBonusLMSWebDAO {

    private static final Log LOG = LogFactory.getLog(ActivationBonusLMSWebDAO.class.getClass().getName());
    private static ActivationBonusLMSWebQry activationBonusLMSWebQry = (ActivationBonusLMSWebQry) ObjectProducer
            .getObject(QueryConstants.ACTIVATION_BONUS_LMS_WEB_QRY, QueryConstants.QUERY_PRODUCER);
    private static final String SQL_EXCEPTION = "SQLException: ";
    private static final String EXCEPTION = "Exception: ";

    public int addActivationBonusSet(Connection p_con, ProfileSetLMSVO p_profileSetVO, String p_requestType)
            throws BTSLBaseException {
        // commented for DB2 OraclePreparedStatement psmtInsertUpdate = null;
        PreparedStatement psmtInsertUpdate = null;
        int insertCount = 0;
        StringBuilder strBuff = null;
        final String methodName = "addActivationBonusSet";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_profileSetVO =");
        	loggerValue.append(p_profileSetVO.toString());
        	loggerValue.append(" p_requestType=");
        	loggerValue.append(p_requestType);
            LOG.debug(methodName, loggerValue);
        }
        Boolean isOptInOutAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
        String insertUpdateQuery = null;
        try {

            // if request is for add activation profile
            if ("addactprofile".equals(p_requestType)) {
                strBuff = new StringBuilder();
                strBuff.append("INSERT INTO profile_set (profile_type,");
                strBuff.append(" set_id,set_name,last_version,status,");
                strBuff.append(" created_on,created_by,modified_on,modified_by,short_code,network_code,promotion_type, ref_based_allowed,MESSAGE_MANAGEMENT_ENABLED  ");// brajesh
                if (isOptInOutAllow) {
                    strBuff.append(",OPT_IN_OUT_ENABLED) ");
                } else {
                    strBuff.append(") ");
                }
                strBuff.append(" values ");
                if (isOptInOutAllow) {
                    strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                } else {
                    strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                }
                insertUpdateQuery = strBuff.toString();
            }
            // when request is for modify act profile
            else if ("modifyactprofile".equals(p_requestType)) {

                strBuff = new StringBuilder();
                strBuff.append("UPDATE profile_set ");
                strBuff.append(" SET last_version=?,modified_on=?,modified_by=?,MESSAGE_MANAGEMENT_ENABLED=?, OPT_IN_OUT_ENABLED=? , ref_based_allowed=?");
                strBuff.append(" WHERE set_id=? ");
                insertUpdateQuery = strBuff.toString();
            }
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(insertUpdateQuery);
    			LOG.debug(methodName, loggerValue);
    		}

            psmtInsertUpdate = (PreparedStatement) p_con.prepareStatement(insertUpdateQuery);
            if ("addactprofile".equals(p_requestType)) {
                psmtInsertUpdate.setString(1, p_profileSetVO.getProfileType().toUpperCase());
                psmtInsertUpdate.setString(2, p_profileSetVO.getSetId());
                psmtInsertUpdate.setString(3, p_profileSetVO.getSetName());
                psmtInsertUpdate.setString(4, p_profileSetVO.getLastVersion());
                psmtInsertUpdate.setString(5, p_profileSetVO.getStatus());
                psmtInsertUpdate.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(p_profileSetVO.getCreatedOn()));
                psmtInsertUpdate.setString(7, p_profileSetVO.getCreatedBy());
                psmtInsertUpdate.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(p_profileSetVO.getModifiedOn()));
                psmtInsertUpdate.setString(9, p_profileSetVO.getModifiedBy());
                psmtInsertUpdate.setString(10, p_profileSetVO.getShortCode());
                psmtInsertUpdate.setString(11, p_profileSetVO.getNetworkCode());
                psmtInsertUpdate.setString(12, p_profileSetVO.getPromotionType());
                psmtInsertUpdate.setString(13, p_profileSetVO.getRefBasedAllow());
                psmtInsertUpdate.setString(14, p_profileSetVO.getMsgConfEnableFlag());
                if (isOptInOutAllow) {
                    psmtInsertUpdate.setString(15, p_profileSetVO.getOptInOut());
                }
                insertCount = psmtInsertUpdate.executeUpdate();
            } else if ("modifyactprofile".equals(p_requestType)) {
                psmtInsertUpdate.setString(1, p_profileSetVO.getLastVersion());
                psmtInsertUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_profileSetVO.getModifiedOn()));
                psmtInsertUpdate.setString(3, p_profileSetVO.getModifiedBy());
                psmtInsertUpdate.setString(4, p_profileSetVO.getMsgConfEnableFlag());
                psmtInsertUpdate.setString(5, p_profileSetVO.getOptInOut());
                psmtInsertUpdate.setString(6, p_profileSetVO.getRefBasedAllow());
                psmtInsertUpdate.setString(7, p_profileSetVO.getSetId());

                insertCount = psmtInsertUpdate.executeUpdate();
            }
        } // end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[addActivationBonusSet]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[addActivationBonusSet]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsertUpdate != null) {
                    psmtInsertUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount:");
            	loggerValue.append(insertCount);
            	LOG.debug(methodName, loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    public ArrayList loadProfileList(Connection p_con, String p_networkCode, ArrayList p_profileList)
            throws BTSLBaseException {
        final String methodName = "loadProfileList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode =");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_profileList size=");
        	loggerValue.append(p_profileList.size());
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ProfileSetLMSVO profileSetVO = null;
        ResultSet rs = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT ps.profile_type,ps.set_id,ps.set_name,ps.last_version,ps.short_code ");
            sbf.append("FROM profile_set ps ");
            sbf.append("WHERE ps.network_code=? AND ps.status NOT IN ('N','R') AND ps.profile_type=?  ORDER BY ps.set_name");
            final String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, PretupsI.LMS_PROFILE_TYPE);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileSetVO = new ProfileSetLMSVO();
                String set_id = rs.getString("set_id");
                Map<String, Integer> countOftotalVersionSuspendActive = new HashMap<String, Integer>(1);
                countOftotalVersionSuspendActive = countOfTotalVersionSuspendActive(p_con, set_id);
                int totalVersion = 0;
                int totalSuspendedVersion = 0;
                int totalActiveVersion = 0;
                if (countOftotalVersionSuspendActive != null && countOftotalVersionSuspendActive.size() > 0) {
                    totalVersion = countOftotalVersionSuspendActive.get("totalVersion");
                    totalSuspendedVersion = countOftotalVersionSuspendActive.get("totalSuspendedVersion");
                    totalActiveVersion = countOftotalVersionSuspendActive.get("totalActiveVersion");
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(methodName, "totalVersion=" + totalVersion + "totalSuspendedVersion="
                                + totalSuspendedVersion + "totalActiveVersion=" + totalActiveVersion);
                    }
                }
                if (totalSuspendedVersion == totalVersion) {
                    continue;
                } else {
                    profileSetVO.setProfileType(rs.getString("profile_type"));
                    profileSetVO.setSetId(rs.getString("set_id"));
                    profileSetVO.setSetName(rs.getString("set_name"));
                    profileSetVO.setLastVersion(rs.getString("last_version"));
                    profileSetVO.setShortCode(rs.getString("short_code"));
                    p_profileList.add(profileSetVO);
                }
                countOftotalVersionSuspendActive = null;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadProfileList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadProfileList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle) {
                    LOG.errorTrace(methodName, sqle);
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: profileListSize:");
            	loggerValue.append(p_profileList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return p_profileList;
    }

    public ArrayList loadVersions(Connection p_con, String p_profileSetId) throws BTSLBaseException {
        final String methodName = "loadVersions";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_profileSetId =");
        	loggerValue.append(p_profileSetId);
            LOG.debug(methodName, loggerValue);
        }
        ArrayList list = null;
        StringBuilder sbf = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ProfileSetVersionLMSVO versionVO = null;
        try {
            list = new ArrayList();
            sbf = new StringBuilder();
            sbf.append("SELECT psv.set_id,psv.VERSION,psv.applicable_from,psv.bonus_duration ");
            sbf.append("FROM PROFILE_SET_VERSION psv ");
            // sbf.append("WHERE (applicable_from=(SELECT MAX(applicable_from) FROM PROFILE_SET_VERSION WHERE applicable_from<=SYSDATE AND psv.set_id=set_id) ");

            // sbf.append("OR applicable_from>SYSDATE) ");
            // else
            // sbf.append("AND psv.set_id=?)");
            sbf.append(" where psv.status NOT IN (?,?) ");
            if (p_profileSetId != null) {
                sbf.append("AND psv.set_id=? ");
            }

            sbf.append(" ORDER BY psv.VERSION DESC");

            final String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmt = p_con.prepareStatement(selectQuery);
            int i = 0;

            pstmt.setString(++i, PretupsI.USER_STATUS_DELETED);
            pstmt.setString(++i, PretupsI.USER_STATUS_SUSPEND);
            if (p_profileSetId != null) {
                pstmt.setString(++i, p_profileSetId);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                versionVO = new ProfileSetVersionLMSVO();
                versionVO.setSetId(rs.getString("set_id"));
                versionVO.setVersion(rs.getString("version"));
                versionVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                versionVO.setBonusDuration(rs.getLong("bonus_duration"));
                list.add(versionVO);
            }

        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadVersions]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadVersions]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle) {
                    LOG.errorTrace(methodName, sqle);
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }

        return list;
    }

    public ArrayList loadVersionsList(Connection p_con, String p_profileSetId, Date validUpToDate) throws BTSLBaseException {
        final String methodName = "loadVersionsList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_profileSetId =");
            loggerValue.append(p_profileSetId);
            LOG.debug(methodName, loggerValue);
        }
        ArrayList list = null;
        StringBuilder sbf = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ProfileSetVersionLMSVO versionVO = null;
        try {
            list = new ArrayList();
            sbf = new StringBuilder();
            sbf.append("SELECT psv.set_id,psv.VERSION ");
            sbf.append("FROM PROFILE_SET_VERSION psv ");
            sbf.append(" where psv.status NOT IN (?) ");
            if (p_profileSetId != null) {
                sbf.append("AND psv.set_id=? ");
            }
            if( validUpToDate != null)
                sbf.append("AND psv.applicable_to <= ? ");
            sbf.append(" ORDER BY psv.VERSION DESC");

            final String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
                loggerValue.setLength(0);
                loggerValue.append("Query =");
                loggerValue.append(selectQuery);
                LOG.debug(methodName, loggerValue);
            }
            pstmt = p_con.prepareStatement(selectQuery);
            int i = 0;

            pstmt.setString(++i, PretupsI.USER_STATUS_DELETED);

            if (p_profileSetId != null) {
                pstmt.setString(++i, p_profileSetId);
            }
            if(validUpToDate != null){
                pstmt.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(validUpToDate));
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                versionVO = new ProfileSetVersionLMSVO();
                versionVO.setSetId(rs.getString("set_id"));
                versionVO.setVersion(rs.getString("version"));
                list.add(versionVO);
            }

        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append(SQL_EXCEPTION);
            loggerValue.append(sqle.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO["+ methodName +"]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(EXCEPTION);
            loggerValue.append(e.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO["+methodName+"]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle) {
                    LOG.errorTrace(methodName, sqle);
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }

        return list;
    }

    public boolean deleteProfileSetVersion(Connection p_con, String p_setID, String p_version, UserVO p_userVO,
            boolean p_setDel, boolean p_versionDel) throws BTSLBaseException {
        final String methodName = "deleteProfileSetVersion";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_setID =");
        	loggerValue.append(p_setID);
        	loggerValue.append(" p_version=");
        	loggerValue.append(p_version);
        	loggerValue.append(" p_setDel=");
        	loggerValue.append(p_setDel);
        	loggerValue.append(" p_versionDel=");
        	loggerValue.append(p_versionDel);
        	loggerValue.append(" p_userVO=");
        	loggerValue.append(p_userVO);
            LOG.debug(methodName, loggerValue);
        }
        int deleteCountForSet = 0;
        int deleteCountForVersion = 0;
        boolean flag = false;
        PreparedStatement pstmtSetUpdate = null;
        PreparedStatement pstmtVersionUpdate = null;
        StringBuilder sbf = null;
        try {
            if (p_setDel) {
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set SET ");
                sbf.append("status='N',modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=?");
                final String updateSetTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateSetTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version  SET ");
                sbf.append("status='N',modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=?");
                final String updateVersionTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateVersionTable);
        			LOG.debug(methodName, loggerValue);
        		}
                final Date currentDate = new Date();
                pstmtSetUpdate = p_con.prepareStatement(updateSetTable);
                pstmtVersionUpdate = p_con.prepareStatement(updateVersionTable);
                pstmtSetUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtSetUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtSetUpdate.setString(3, p_setID);
                pstmtVersionUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtVersionUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtVersionUpdate.setString(3, p_setID);
            } else if (p_versionDel) {
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set  SET ");
                sbf.append("modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=?");
                final String updateSetTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateSetTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version  SET ");
                sbf.append("status='N',modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=? AND version=?");
                final String updateVersionTable = sbf.toString();
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "SQL Query :" + updateVersionTable);
                }
                final Date currentDate = new Date();
                pstmtSetUpdate = p_con.prepareStatement(updateSetTable);
                pstmtVersionUpdate = p_con.prepareStatement(updateVersionTable);
                pstmtSetUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtSetUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtSetUpdate.setString(3, p_setID);
                pstmtVersionUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtVersionUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtVersionUpdate.setString(3, p_setID);
                pstmtVersionUpdate.setString(4, p_version);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[deleteProfileSetVersion]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[deleteProfileSetVersion]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtSetUpdate != null) {
                    pstmtSetUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtVersionUpdate != null) {
                	pstmtVersionUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: flag:");
            	loggerValue.append(flag);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return flag;
    }

    public int updateProfileVersionDetail(Connection p_con, String version, String p_user_id, String p_setid,
            Date p_currentDate, ProfileSetVersionLMSVO p_profileSetVersionVO, boolean isreferenceBased)
            throws BTSLBaseException {
        // commented for DB2OraclePreparedStatement psmtInsertUpdate = null;
        PreparedStatement psmtInsertUpdate = null;
        PreparedStatement psmtDelete = null;
        int insertCount = 0;
        StringBuilder strBuff = null;
        final String methodName = "updateProfileVersionDetail";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_setID =");
        	loggerValue.append(p_setid);
        	loggerValue.append(" version=");
        	loggerValue.append(version);
        	loggerValue.append(" p_user_id=");
        	loggerValue.append(p_user_id);
        	loggerValue.append(" p_profileSetVersionVO=");
        	loggerValue.append(p_profileSetVersionVO.toString());
            LOG.debug(methodName, loggerValue);
        }
        String insertUpdateQuery = null;
        try {

            strBuff = new StringBuilder();
            strBuff.append("UPDATE profile_set_version ");
            strBuff.append(" SET modified_on=?,modified_by=? ");
            strBuff.append(",one_time_bonus=? ");
            strBuff.append(",bonus_duration=? ");

            strBuff.append(",opt_contribution=? ");
            strBuff.append(",prt_contribution=?,applicable_from=?,applicable_to=?  ");

            strBuff.append(",reference_from=?,reference_to=? ");

            strBuff.append(" WHERE set_id=? AND version=?  ");
            insertUpdateQuery = strBuff.toString();
            strBuff = new StringBuilder();
            strBuff.append("Delete from profile_details ");
            strBuff.append("where set_id=? AND version=? ");
            final String deleteQuery = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(insertUpdateQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            // commented for DB2psmtInsertUpdate =
            // (OraclePreparedStatement)p_con.prepareStatement(insertUpdateQuery);
            psmtInsertUpdate = (PreparedStatement) p_con.prepareStatement(insertUpdateQuery);
            int i = 0;
            psmtInsertUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
            psmtInsertUpdate.setString(++i, p_user_id);
            if (p_profileSetVersionVO.getOneTimeBonus() > 0) {
                psmtInsertUpdate.setLong(++i, PretupsBL.getSystemAmount(p_profileSetVersionVO.getOneTimeBonus()));
            } else {
                psmtInsertUpdate.setLong(++i, 0);
            }
            if (p_profileSetVersionVO.getBonusDuration() > 0) {
                psmtInsertUpdate.setString(++i, Long.toString(p_profileSetVersionVO.getBonusDuration()));
            } else {
                psmtInsertUpdate.setLong(++i, 0);
            }

            psmtInsertUpdate.setString(++i, p_profileSetVersionVO.getOptContribution());
            psmtInsertUpdate.setString(++i, p_profileSetVersionVO.getPrtContribution());
            psmtInsertUpdate.setTimestamp(++i,
                    BTSLUtil.getTimestampFromUtilDate(p_profileSetVersionVO.getApplicableFrom()));
            psmtInsertUpdate.setTimestamp(++i,
                    BTSLUtil.getTimestampFromUtilDate(p_profileSetVersionVO.getApplicableTo()));

            psmtInsertUpdate
                    .setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_profileSetVersionVO.getRefApplicableFrom()));
            psmtInsertUpdate.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_profileSetVersionVO.getRefApplicableTo()));

            psmtInsertUpdate.setString(++i, p_setid);
            psmtInsertUpdate.setString(++i, version);
            insertCount = psmtInsertUpdate.executeUpdate();
            psmtDelete = p_con.prepareStatement(deleteQuery);
            psmtDelete.setString(1, p_setid);
            psmtDelete.setString(2, version);
            insertCount = psmtDelete.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[updateProfileVersionDetail]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[updateProfileVersionDetail]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsertUpdate != null) {
                    psmtInsertUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (psmtDelete != null) {
                    psmtDelete.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount:");
            	loggerValue.append(insertCount);
            	LOG.debug(methodName, loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    public int checkUserAssociationForProfile(Connection p_con, String p_str[]) throws BTSLBaseException {
        final String methodName = "checkUserAssociationForProfile";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_str[].size() =");
        	loggerValue.append(p_str.length);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        int insertCount = 0;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT uop.USER_ID, uop.PROFILE_TYPE, uop.SET_ID, u.CATEGORY_CODE  FROM USER_OTH_PROFILES uop, USERS u, CATEGORIES c");
        strBuff.append(" WHERE uop.SET_ID= ? AND u.USER_ID=uop.USER_ID AND uop.PROFILE_TYPE= ? AND c.CATEGORY_NAME= ?");
        strBuff.append(" AND u.CATEGORY_CODE=c.CATEGORY_CODE AND u.STATUS=?");
        final String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
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
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDao[checkUserAssociationForProfile]", "", "", "",
                    loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDao[checkUserAssociationForProfile]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount:");
            	loggerValue.append(insertCount);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return insertCount;
    }

    public ArrayList loadLookupServicesList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadLookupServicesList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered ");
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT l.lookup_code,l.lookup_name,l.lookup_type,l.status ");
        strBuff.append(" FROM LOOKUPS l, LOOKUP_TYPES lt ");
        strBuff.append(" WHERE l.status = 'Y' AND l.lookup_type = lt.lookup_type  AND ");
        strBuff.append(" lt.lookup_type IN ('" + PretupsI.LMS_O2C_SERVICE_LIST + "', '" + PretupsI.LMS_C2C_SERVICE_LIST
                + "','" + PretupsI.LMS_C2S_SERVICE_LIST + "')");
        strBuff.append(" ORDER BY l.lookup_type");
        final String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("lookup_type").equalsIgnoreCase(PretupsI.LMS_O2C_SERVICE_LIST)) {
                    final ListValueVO vo = new ListValueVO(rs.getString("lookup_name"), rs.getString("lookup_code")
                            + ":" + "O2C");
                    vo.setType(rs.getString("lookup_type"));
                    list.add(vo);
                } else if (rs.getString("lookup_type").equalsIgnoreCase(PretupsI.LMS_C2C_SERVICE_LIST)) {
                    final ListValueVO vo = new ListValueVO(rs.getString("lookup_name"), rs.getString("lookup_code")
                            + ":" + "C2C");
                    vo.setType(rs.getString("lookup_type"));
                    list.add(vo);
                } else if (rs.getString("lookup_type").equalsIgnoreCase(PretupsI.LMS_C2S_SERVICE_LIST)) {
                    final ListValueVO vo = new ListValueVO(rs.getString("lookup_name"), rs.getString("lookup_code")
                            + ":" + "C2S");
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadServicesList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadLookupServicesList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: serviceList size:");
            	loggerValue.append(list.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return list;
    }

    public ArrayList loadApprovalProfileList(Connection p_con, String p_networkCode, ArrayList p_profileList)
            throws BTSLBaseException {
        final String methodName = "loadApprovalProfileList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode =");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_profileList size=");
        	loggerValue.append(p_profileList.size());
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ProfileSetLMSVO profileSetVO = null;
        ResultSet rs = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT ps.profile_type,ps.set_id,ps.set_name,psv.version,ps.short_code,ps.status ");
            sbf.append("FROM profile_set ps, PROFILE_SET_VERSION psv ");
            sbf.append("WHERE ps.network_code=? AND ps.status<>'N' AND  psv.status='W' AND ps.set_id=psv.set_id AND ps.profile_type=? ORDER BY ps.set_name");
            final String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, PretupsI.LMS_PROFILE_TYPE);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileSetVO = new ProfileSetLMSVO();
                profileSetVO.setProfileType(rs.getString("profile_type"));
                profileSetVO.setSetId(rs.getString("set_id"));
                profileSetVO.setSetName(rs.getString("set_name"));
                profileSetVO.setLastVersion(rs.getString("version"));
                profileSetVO.setShortCode(rs.getString("short_code"));
                profileSetVO.setStatus(rs.getString("status"));
                p_profileList.add(profileSetVO);
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadApprovalProfileList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadProfileList", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadApprovalProfileList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle) {
                    LOG.errorTrace(methodName, sqle);
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: p_profileList.size:");
            	loggerValue.append(p_profileList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return p_profileList;
    }

    public ArrayList loadResumeProfileList(Connection p_con, String p_networkCode, ArrayList p_profileList)
            throws BTSLBaseException {
        final String methodName = "loadResumeProfileList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode =");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_profileList size=");
        	loggerValue.append(p_profileList.size());
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ProfileSetLMSVO profileSetVO = null;
        ResultSet rs = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT distinct ps.profile_type,ps.set_id,ps.set_name,ps.LAST_VERSION,ps.short_code,ps.status ");
            sbf.append("FROM profile_set ps,profile_set_version psv ");
            sbf.append("WHERE ps.network_code=? AND ps.status <>'N' AND ps.set_id=psv.set_id and psv.status='S' ");
            sbf.append("AND ps.profile_type=? ORDER BY ps.set_name");
            final String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, PretupsI.LMS_PROFILE_TYPE);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileSetVO = new ProfileSetLMSVO();
                profileSetVO.setProfileType(rs.getString("profile_type"));
                profileSetVO.setSetId(rs.getString("set_id"));
                profileSetVO.setSetName(rs.getString("set_name"));
                profileSetVO.setShortCode(rs.getString("short_code"));
                profileSetVO.setStatus(rs.getString("status"));
                p_profileList.add(profileSetVO);
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadApprovalProfileList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadProfileList", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadApprovalProfileList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadApprovalProfileList", "error.general.processing");
        } // end of catch
        finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle) {
                    LOG.errorTrace(methodName, sqle);
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: p_profileList.size:");
            	loggerValue.append(p_profileList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return p_profileList;
    }

    public int addActivationBonusVersion(Connection p_con, ProfileSetVersionLMSVO p_profileSetVersionVO)
            throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        final String methodName = "addActivationBonusVersion";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_profileSetVersionVO =");
        	loggerValue.append(p_profileSetVersionVO.toString());
            LOG.debug(methodName, loggerValue);
        }
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO profile_set_version (set_id,");
            strBuff.append("version,applicable_from, ");
            if (p_profileSetVersionVO.getOneTimeBonus() > 0) {
                strBuff.append(" one_time_bonus, ");
            }
            if (p_profileSetVersionVO.getBonusDuration() > 0) {
                strBuff.append(" bonus_duration, ");
            }
            strBuff.append(" status,created_on,created_by,modified_on,modified_by, applicable_to, reference_from, reference_to,opt_contribution,prt_contribution)");
            strBuff.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?");
            if (p_profileSetVersionVO.getOneTimeBonus() > 0) {
                strBuff.append(" ,?");
            }
            if (p_profileSetVersionVO.getBonusDuration() > 0) {
                strBuff.append(" ,?");
            }
            strBuff.append(" )");
            final String insertQuery = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(insertQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            int i = 0;
            psmtInsert = p_con.prepareStatement(insertQuery);
            psmtInsert.setString(++i, p_profileSetVersionVO.getSetId());
            psmtInsert.setString(++i, p_profileSetVersionVO.getVersion());
            psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_profileSetVersionVO.getApplicableFrom()));
            if (p_profileSetVersionVO.getOneTimeBonus() > 0) {
                psmtInsert.setLong(++i, PretupsBL.getSystemAmount(p_profileSetVersionVO.getOneTimeBonus()));
            }
            if (p_profileSetVersionVO.getBonusDuration() > 0) {
                psmtInsert.setLong(++i, p_profileSetVersionVO.getBonusDuration());
            }

            psmtInsert.setString(++i, p_profileSetVersionVO.getStatus());
            psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_profileSetVersionVO.getCreatedOn()));
            psmtInsert.setString(++i, p_profileSetVersionVO.getCreatedBy());
            psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_profileSetVersionVO.getModifiedOn()));
            psmtInsert.setString(++i, p_profileSetVersionVO.getModifiedBy());
            psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_profileSetVersionVO.getApplicableTo()));
            psmtInsert.setTimestamp(++i,
                    BTSLUtil.getTimestampFromUtilDate(p_profileSetVersionVO.getRefApplicableFrom()));
            psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_profileSetVersionVO.getRefApplicableTo()));
            psmtInsert.setString(++i, p_profileSetVersionVO.getOptContribution());
            psmtInsert.setString(++i, p_profileSetVersionVO.getPrtContribution());
            insertCount = psmtInsert.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[addActivationBonusVersion]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[addActivationBonusVersion]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount:");
            	loggerValue.append(insertCount);
            	LOG.debug(methodName, loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    public int addActivationBonusSetDetail(Connection p_con, ArrayList p_detailVOList, ProfileSetLMSVO profileSetVO,
            boolean p_updateVersion) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        final String methodName = "addActivationBonusSetDetail";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_detailVOList Size =");
        	loggerValue.append(p_detailVOList.size());
        	loggerValue.append(" p_updateVersion=");
        	loggerValue.append(p_updateVersion);
            LOG.debug(methodName, loggerValue);
        }

        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO profile_details (set_id,");
            strBuff.append("version,detail_id,type,user_type,detail_type,detail_subtype,period_id,service_code,");
            strBuff.append("start_range,end_range,points_type,points,min_limit,max_limit,subscriber_type,reference_date,PRODUCT_CODE ) ");
            strBuff.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String insertQuery = strBuff.toString();

            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(insertQuery);
    			LOG.debug(methodName, loggerValue);
    		}

            psmtInsert = p_con.prepareStatement(insertQuery);
            ProfileSetDetailsLMSVO detailVO = null;
            for (int i = 0, j = p_detailVOList.size(); i < j; i++) {

                detailVO = (ProfileSetDetailsLMSVO) p_detailVOList.get(i);
                psmtInsert.setString(1, profileSetVO.getSetId());
                // when new date and old date in case of modify details are same
                // decrement version to original
                if (p_updateVersion) {
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
                    psmtInsert.setDate(17, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(detailVO
                            .getReferenceDate())));
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

        } // end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler
                    .handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "ActivationBonusDAO[addActivationBonusSetDetail]", "", "", "",
                            loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[addActivationBonusSetDetail]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount:");
            	loggerValue.append(insertCount);
            	LOG.debug(methodName, loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    public ActivationProfileCombinedLMSVO loadProfileVersionDetails(Connection p_con, String p_profileSetID,
            String p_version, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadProfileVersionDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_profileSetID =");
        	loggerValue.append(p_profileSetID);
        	loggerValue.append(" p_version=");
        	loggerValue.append(p_version);
        	loggerValue.append(" p_networkCode=");
        	loggerValue.append(p_networkCode);
            LOG.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ActivationProfileCombinedLMSVO profileDetailsCombinedVO = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT  ps.set_id,ps.set_name,ps.short_code,psv.applicable_from,psv.bonus_duration,psv.one_time_bonus, ");
            sbf.append(" ps.last_version,psv.applicable_to,psv.reference_from,psv.reference_to, ps.ref_based_allowed, ps.promotion_type,ps.MESSAGE_MANAGEMENT_ENABLED,");
            sbf.append(" psv.opt_contribution, psv.prt_contribution,ps.OPT_IN_OUT_ENABLED ");
            sbf.append(" FROM PROFILE_SET ps,PROFILE_SET_VERSION psv ");
            sbf.append(" WHERE psv.set_id=? AND psv.VERSION=? AND psv.set_id=ps.set_id AND ps.network_code=?");
            final String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_profileSetID);
            pstmt.setString(2, p_version);
            pstmt.setString(3, p_networkCode);
            rs = pstmt.executeQuery();
            Date date = new Date();
            if (rs.next()) {
                profileDetailsCombinedVO = new ActivationProfileCombinedLMSVO();
                profileDetailsCombinedVO.setSetName(rs.getString("set_name"));
                profileDetailsCombinedVO.setShortCode(rs.getString("short_code"));
                profileDetailsCombinedVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                if (rs.getLong("bonus_duration") > 0) {
                    profileDetailsCombinedVO.setBonusDuration(Long.parseLong(rs.getString("bonus_duration")));
                }
                if (rs.getLong("one_time_bonus") > 0) {
                    profileDetailsCombinedVO.setOneTimeBonus(Long.parseLong(PretupsBL.getDisplayAmount(rs
                            .getLong("one_time_bonus"))));
                }

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
                profileDetailsCombinedVO.setPromotionTypeName(((LookupsVO) LookupsCache.getObject(
                        PretupsI.LMS_PROMOTION_TYPE, rs.getString("promotion_type"))).getLookupName());
                profileDetailsCombinedVO.setOptInOut(rs.getString("OPT_IN_OUT_ENABLED"));
                // Handling of Expired profile
                if (BTSLUtil.getDifferenceInUtilDates(date, rs.getTimestamp("applicable_to")) < 0) {
                    profileDetailsCombinedVO.setLmsProfileExpiredFlag("Y");
                } else {
                    profileDetailsCombinedVO.setLmsProfileExpiredFlag("N");
                }

            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadProfileVersionDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadProfileVersionDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }
        return profileDetailsCombinedVO;
    }

    public ArrayList loadActivationProfileServicesList(Connection p_con, String p_actProifleSetId,
            String p_actProfileSetVersion) throws BTSLBaseException {

        final String methodName = "loadActivationProfileServicesList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_actProifleSetId =");
        	loggerValue.append(p_actProifleSetId);
        	loggerValue.append(" p_actProfileSetVersion=");
        	loggerValue.append(p_actProfileSetVersion);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtmodule = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        final List<String> lookuptype = new ArrayList<String>();
        final StringBuilder strBuff = new StringBuilder();
        ListValueVO listValueVO = null;
        strBuff.append(" SELECT DISTINCT  l.lookup_code,l.lookup_name FROM PROFILE_DETAILS pd,lookups l ");
        strBuff.append(" WHERE l.lookup_code=pd.SERVICE_CODE AND pd.set_id=? AND pd.VERSION=? AND l.lookup_type=?");
        final String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuilder strcode = new StringBuilder();
        strcode.append(" SELECT DISTINCT type FROM PROFILE_DETAILS WHERE set_id=? AND VERSION=? ");
        final String moduleselect = strcode.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY moduleselect=" + moduleselect);
        }

        final ArrayList list = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);// 2
            pstmtmodule = p_con.prepareStatement(moduleselect);
            pstmtmodule.setString(1, p_actProifleSetId);
            pstmtmodule.setString(2, p_actProfileSetVersion);
            rs1 = pstmtmodule.executeQuery();
            while (rs1.next()) {
                lookuptype.add(rs1.getString("type") + "SL");
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "QUERY lookuptype=" + lookuptype.size());
            }
            int lookuptypeSize = lookuptype.size();
            for (int j = 0; j < lookuptypeSize; j++) {
                pstmtSelect.setString(1, p_actProifleSetId);
                pstmtSelect.setString(2, p_actProfileSetVersion);
                pstmtSelect.setString(3, lookuptype.get(j));
                if (!BTSLUtil.isNullString(lookuptype.get(j))) {
                    rs = pstmtSelect.executeQuery();
                    while (rs.next()) {
                        listValueVO = new ListValueVO(rs.getString("lookup_name"), rs.getString("lookup_code"));
                        list.add(listValueVO);
                    }
                }
                pstmtSelect.clearParameters();
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDao[loadActivationProfileServicesList]", "", "", "",
                    loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler
                    .handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "ActivationBonusDao[loadActivationProfileServicesList]", "", "", "",
                            loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtmodule != null) {
                    pstmtmodule.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: ActivationProfileServiceList size:");
            	loggerValue.append(list.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return list;
    }

    public ArrayList loadActivationProfileDetailList(Connection p_con, String p_actProfileServiceTypeID,
            String p_actProifleSetId, String p_actProfileSetVersion) throws BTSLBaseException {
        final String methodName = "loadActivationProfileDetailList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_actProfileServiceTypeID =");
        	loggerValue.append(p_actProfileServiceTypeID);
        	loggerValue.append(" p_actProifleSetId=");
        	loggerValue.append(p_actProifleSetId);
        	loggerValue.append(" p_actProfileSetVersion=");
        	loggerValue.append(p_actProfileSetVersion);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList addActivationDetailList = new ArrayList();
        ProfileSetDetailsLMSVO activationProfileDeatilsVO = null;
        try {
            final StringBuilder selectQueryBuff = new StringBuilder(
                    "SELECT pd.start_range,pd.end_range,pd.points_type,pd.period_id,pd.points,pd.detail_type,pd.detail_subtype,pd.subscriber_type, ");
            selectQueryBuff
                    .append(" pd.type,pd.user_type,pd.service_code,pd.min_limit,pd.max_limit,pd.subscriber_type,pd.version,TO_CHAR(pd.reference_date,'DD/MM/YY') referenceDate ");
            selectQueryBuff.append(" ,pd.product_code");
            selectQueryBuff.append(" FROM PROFILE_DETAILS pd ");
            selectQueryBuff.append(" WHERE pd.set_id=? AND pd.VERSION=? AND service_code=?");
            final String selectQuery = selectQueryBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_actProifleSetId);
            pstmtSelect.setString(2, p_actProfileSetVersion);
            pstmtSelect.setString(3, p_actProfileServiceTypeID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                activationProfileDeatilsVO = new ProfileSetDetailsLMSVO();
                if (rs.getString("detail_subtype").equals(PretupsI.USER_SUB_TYPE_COUNT)) {
                    activationProfileDeatilsVO.setStartRangeAsString(Long.toString(rs.getLong("start_range")));
                    activationProfileDeatilsVO.setEndRangeAsString(Long.toString(rs.getLong("end_range")));
                    activationProfileDeatilsVO.setStartRange(Long.parseLong(rs.getString("start_range")));
                    activationProfileDeatilsVO.setEndRange(Long.parseLong(rs.getString("end_range")));
                } else {
                    activationProfileDeatilsVO.setStartRangeAsString(PretupsBL.getDisplayAmount(rs
                            .getLong("start_range")));
                    activationProfileDeatilsVO.setEndRangeAsString(PretupsBL.getDisplayAmount(rs.getLong("end_range")));
                    activationProfileDeatilsVO.setStartRange(Long.parseLong(PretupsBL.getDisplayAmount(rs
                            .getLong("start_range"))));
                    activationProfileDeatilsVO.setEndRange(Long.parseLong(PretupsBL.getDisplayAmount(rs
                            .getLong("end_range"))));
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
                activationProfileDeatilsVO.setProductCode(rs.getString("product_code"));
                addActivationDetailList.add(activationProfileDeatilsVO);
            }
            return addActivationDetailList;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadActivationProfileDetailList]", "", "", "",
                    loggerValue.toString());
            throw new BTSLBaseException("ActivationBonusDAO", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadActivationProfileDetailList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException("ActivationBonusDAO", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: addActivationDetailList size:");
            	loggerValue.append(addActivationDetailList.size());
            	LOG.debug(methodName, loggerValue);
            }
        }// end of finally
    }

    public boolean suspendProfileSetVersion(Connection p_con, String p_setID, String p_version, UserVO p_userVO,
            boolean p_setDel, boolean p_versionDel) throws BTSLBaseException {
        final String methodName = "suspendProfileSetVersion";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_setID =");
        	loggerValue.append(p_setID);
        	loggerValue.append(" p_version=");
        	loggerValue.append(p_version);
        	loggerValue.append(" p_setDel=");
        	loggerValue.append(p_setDel);
        	loggerValue.append(" p_versionDel=");
        	loggerValue.append(p_versionDel);
        	loggerValue.append(" p_userVO=");
        	loggerValue.append(p_userVO);
            LOG.debug(methodName, loggerValue);
        }
        int deleteCountForSet = 0;
        int deleteCountForVersion = 0;
        boolean flag = false;
        PreparedStatement pstmtSetUpdate = null;
        PreparedStatement pstmtVersionUpdate = null;
        StringBuilder sbf = null;
        try {
            if (p_setDel) {
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set SET ");
                sbf.append("status='S',modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=?");
                final String updateSetTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateSetTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version SET ");
                sbf.append("status='S',modified_by=?,modified_on=? ");
                // OCM FIX2
                sbf.append("WHERE set_id=? and status='Y' ");
                final String updateVersionTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateVersionTable);
        			LOG.debug(methodName, loggerValue);
        		}
                final Date currentDate = new Date();
                pstmtSetUpdate = p_con.prepareStatement(updateSetTable);
                pstmtVersionUpdate = p_con.prepareStatement(updateVersionTable);
                pstmtSetUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtSetUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtSetUpdate.setString(3, p_setID);
                pstmtVersionUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtVersionUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtVersionUpdate.setString(3, p_setID);
            } else if (p_versionDel) {
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set SET ");
                sbf.append("modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=?");
                final String updateSetTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateSetTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version  SET ");
                sbf.append("status='S',modified_by=?,modified_on=? ");
                // OCM FIX2
                sbf.append("WHERE set_id=? AND version=?  and status='Y' ");
                final String updateVersionTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateVersionTable);
        			LOG.debug(methodName, loggerValue);
        		}
                final Date currentDate = new Date();
                pstmtSetUpdate = p_con.prepareStatement(updateSetTable);
                pstmtVersionUpdate = p_con.prepareStatement(updateVersionTable);
                pstmtSetUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtSetUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtSetUpdate.setString(3, p_setID);
                pstmtVersionUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtVersionUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtVersionUpdate.setString(3, p_setID);
                pstmtVersionUpdate.setString(4, p_version);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[suspendProfileSetVersion]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "deleteProfileSetVersion", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[suspendProfileSetVersion]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "deleteProfileSetVersion", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtSetUpdate != null) {
                    pstmtSetUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtVersionUpdate != null) {
                	pstmtVersionUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: flag:");
            	loggerValue.append(flag);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return flag;
    }

    public boolean approveRejectProfileSet(Connection p_con, String p_setID, String p_version, UserVO p_userVO,
            boolean p_approve, boolean p_reject) throws BTSLBaseException {
        final String methodName = "approveRejectProfileSet";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_setID =");
        	loggerValue.append(p_setID);
        	loggerValue.append(" p_version=");
        	loggerValue.append(p_version);
        	loggerValue.append(" p_approve=");
        	loggerValue.append(p_approve);
        	loggerValue.append(" p_reject=");
        	loggerValue.append(p_reject);
        	loggerValue.append(" p_user ID=");
        	loggerValue.append(p_userVO.getActiveUserID());
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
            if (p_approve) {
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set  SET ");
                sbf.append("status='Y',modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=?");
                final String updateSetTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateSetTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version SET ");
                sbf.append("status='Y',modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=? and version=? ");
                final String updateVersionTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateVersionTable);
        			LOG.debug(methodName, loggerValue);
        		}
                final Date currentDate = new Date();
                pstmtSetUpdate = p_con.prepareStatement(updateSetTable);
                pstmtVersionUpdate = p_con.prepareStatement(updateVersionTable);
                pstmtSetUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtSetUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtSetUpdate.setString(3, p_setID);
                pstmtVersionUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtVersionUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtVersionUpdate.setString(3, p_setID);
                pstmtVersionUpdate.setString(4, p_version);
            } else if (p_reject) {
                sbf = new StringBuilder();
                sbf.append(" select version from PROFILE_SET_VERSION where set_id=? ");
                final String selectVersionTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(selectVersionTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set  SET ");
                sbf.append("status='N',modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=?");
                final String updateSetTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateSetTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version SET ");
                sbf.append("status='N',modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=? and version=? ");
                final String updateVersionTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateVersionTable);
        			LOG.debug(methodName, loggerValue);
        		}
                final Date currentDate = new Date();
                pstmtVersionSelect = p_con.prepareStatement(selectVersionTable);
                pstmtSetUpdate = p_con.prepareStatement(updateSetTable);
                pstmtVersionUpdate = p_con.prepareStatement(updateVersionTable);

                pstmtVersionSelect.setString(1, p_setID);

                pstmtSetUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtSetUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtSetUpdate.setString(3, p_setID);
                pstmtVersionUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtVersionUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtVersionUpdate.setString(3, p_setID);
                pstmtVersionUpdate.setString(4, p_version);

                rs = pstmtVersionSelect.executeQuery();
                while (rs.next()) {
                    countProfileVersion++;

                }
            }
            // if(!p_reject)
            if (!(countProfileVersion > 1)) {
                countForSet = pstmtSetUpdate.executeUpdate();
            }
            if (pstmtVersionUpdate != null) {
                countForVersion = pstmtVersionUpdate.executeUpdate();
            }
            if (countForVersion > 0) {
                flag = true;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[approveRejectProfileSet]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[approveRejectProfileSet]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtVersionSelect != null) {
                    pstmtVersionSelect.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtSetUpdate != null) {
                    pstmtSetUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtVersionUpdate != null) {
                    pstmtVersionUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: flag:");
            	loggerValue.append(flag);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return flag;
    }

    // By Zeeshan Aleem
    public ProfileSetLMSVO loadMessageList(Connection p_con, String profileId) throws BTSLBaseException {
        final String methodName = "loadMessageList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: profileId =");
        	loggerValue.append(profileId);
            LOG.debug(methodName, loggerValue);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered: profileId = " + profileId);
        }
        Boolean isOptInOutAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ProfileSetLMSVO profileSetLMSVO = null;
        ListValueVO listValueVO = null;
        listValueVO = new ListValueVO();
        String Message_code = null;
        String Message_code2 = null;
        String Message_code3 = null;
        String Message_code4 = null;
        StringBuilder strBuff = null;
        StringBuilder strBuff2 = null;
        String sqlSelect = null;
        String sqlSelect2 = null;

        HashMap<String, String> profileDetails = fetchLMSProfleDetails(p_con, profileId);
        String promotionType = null;
        String optInOutEnabled = null;
        String messageManagementEnabled = null;
        if (profileDetails != null) {
            promotionType = profileDetails.get("PROMOTION_TYPE");
            optInOutEnabled = profileDetails.get("OPT_IN_OUT_ENABLED");
            messageManagementEnabled = profileDetails.get("MESSAGE_MANAGEMENT_ENABLED");
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "promotionType = " + promotionType + ", optInOutEnabled=" + optInOutEnabled
                        + " ,messageManagementEnabled=" + messageManagementEnabled);
            }

        }

        try {
            int index = 1;
            strBuff = new StringBuilder();
            strBuff2 = new StringBuilder();
            profileSetLMSVO = new ProfileSetLMSVO();
            strBuff.append("SELECT message_code,message1,message2 from MESSAGES_MASTER  where message_code=? ");
            strBuff2.append("SELECT message_code,message1,message2 from MESSAGES_MASTER  where message_code=? ");
            sqlSelect = strBuff.toString();
            sqlSelect2 = strBuff2.toString();
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt2 = p_con.prepareStatement(sqlSelect2);
            if (isOptInOutAllow && PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(promotionType)
                    && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(optInOutEnabled)
                    && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
                Message_code = PretupsI.OPTINOUT_WEL_MESSAGE + "_" + profileId;
            } else if (isOptInOutAllow
                    && PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(promotionType)
                    && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(optInOutEnabled)
                    && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
                Message_code = PretupsI.OPTINOUT_TRA_WEL_MSG + "_" + profileId;
            } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(promotionType)
                    && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
                Message_code = PretupsI.TRA_WEL_MESSAGE + "_" + profileId;
            } else {
                Message_code = PretupsI.WEL_MESSAGE + "_" + profileId;
            }

            profileSetLMSVO.setPromotionType(promotionType);
            profileSetLMSVO.setOptInOut(optInOutEnabled);
            profileSetLMSVO.setMsgConfEnableFlag(messageManagementEnabled);

            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sqlSelect);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmt.setString(index++, Message_code);
            // pstmt.setString(index++,Message_code2);
            // pstmt.setString(index++,Message_code3);
            // pstmt.setString(index++,Message_code4);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileSetLMSVO.setLang1welcomemsg(rs.getString("message1"));
                profileSetLMSVO.setLang2welcomemsg(rs.getString("message2"));
                profileSetLMSVO.setMessageCode(rs.getString("Message_code"));
            }
            if (!PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equals(promotionType)) {
                // StringBuilder strBuff1 = new StringBuilder();
                Message_code = PretupsI.SUCCESS_MESSAGE + "_" + profileId;
                // strBuff1.append("SELECT message_code,message1,message2 from
                // MESSAGES_MASTER where message_code=? ");
                // sqlSelect = strBuff1.toString();
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
                }
                pstmt2.clearParameters();
                pstmt2.setString(1, Message_code);
                rs1 = pstmt2.executeQuery();
                while (rs1.next()) {
                    profileSetLMSVO.setLang1seccessmsg(rs1.getString("message1"));
                    profileSetLMSVO.setLang2seccessmsg(rs1.getString("message2"));
                    // System.out.println("*****"+rs.getString("message1")+"******"+rs.getString("message2")+"*********");
                    profileSetLMSVO.setMessageCode(rs1.getString("Message_code"));
                    // System.out.println(rs.getString("message1")+rs.getString("message2"));
                }
                // StringBuilder strBuff2 = new StringBuilder();
                Message_code = PretupsI.FAILURE_MESSAGE + "_" + profileId;
                // strBuff2.append("SELECT message_code,message1,message2 from
                // MESSAGES_MASTER where message_code=? ");
                // sqlSelect = strBuff2.toString();
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
                }
                pstmt2.clearParameters();
                pstmt2.setString(1, Message_code);
                rs2 = pstmt2.executeQuery();
                while (rs2.next()) {
                    profileSetLMSVO.setLang1failuremsg(rs2.getString("message1"));
                    profileSetLMSVO.setLang2failuremsg(rs2.getString("message2"));
                    // System.out.println("*****"+rs.getString("message1")+"******"+rs.getString("message2")+"*********");
                    profileSetLMSVO.setMessageCode(rs2.getString("Message_code"));
                    // System.out.println(rs.getString("message1")+rs.getString("message2"));
                }
            }
            profileSetLMSVO.setSetId(profileId);
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "LoyalityDAO[loadMessageList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");

        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "LoyalityDAO[loadMessageList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt2 != null) {
                    pstmt2.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("loadMessageList", "Exiting: ");
            }
        }

        return profileSetLMSVO;
    }

    public int deleteMessages(Connection p_con, String setId) throws BTSLBaseException {
        final String methodName = "deleteMessages";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered ");
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        int deleteCount = 0;
        int flag = 0;

        try {
            String messageCode = null;
            final StringBuilder sbf = new StringBuilder();
            sbf.append("DELETE from MESSAGES_MASTER WHERE MESSAGE_CODE=?");
            final String query = sbf.toString();
            pstmt = p_con.prepareStatement(query);
            messageCode = PretupsI.WEL_MESSAGE + "_" + setId;
            pstmt.setString(1, messageCode);
            deleteCount = pstmt.executeUpdate();
            if (deleteCount == 1) {
                flag = 1;
            }
            messageCode = PretupsI.SUCCESS_MESSAGE + "_" + setId;
            // StringBuilder sbf1=new
            // StringBuilder();//sbf1.append("DELETE from MESSAGES_MASTER WHERE MESSAGE_CODE=?");//query=sbf1.toString();
            pstmt.clearParameters();
            pstmt.setString(1, messageCode);
            deleteCount = pstmt.executeUpdate();
            if (deleteCount == 1) {
                flag = 1;
            }
            messageCode = PretupsI.FAILURE_MESSAGE + "_" + setId;
            // StringBuilder sbf2=new
            // StringBuilder();//sbf2.append("DELETE from MESSAGES_MASTER WHERE MESSAGE_CODE=?");//query=sbf2.toString();
            pstmt.clearParameters();
            pstmt.setString(1, messageCode);
            deleteCount = pstmt.executeUpdate();
            if (deleteCount == 1) {
                flag = 1;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[deleteMessages]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBounsDAO[deleteMessages]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting ");
            }
        }
        return flag;
    }

    public boolean updateMessageList(Connection p_con, ProfileSetLMSVO profileSetLMSVO, String profileId)
            throws BTSLBaseException {
        final String methodName = "updateMessageList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
            LOG.debug(methodName, loggerValue);
        }
        boolean flag = false;
        PreparedStatement pstmt = null;
        Boolean isOptInOutAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW);
        final ResultSet rs = null;
        ListValueVO listValueVO = null;
        listValueVO = new ListValueVO();
        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("update MESSAGES_MASTER  set MESSAGE1=?,MESSAGE2=? where message_code=? ");
            final String sqlSelect = strBuff.toString();
            pstmt = p_con.prepareStatement(sqlSelect);
            HashMap<String, String> profileDetails = fetchLMSProfleDetails(p_con, profileId);
            String promotionType = null;
            String optInOutEnabled = null;
            String messageManagementEnabled = null;
            if (profileDetails != null) {
                promotionType = profileDetails.get("PROMOTION_TYPE");
                optInOutEnabled = profileDetails.get("OPT_IN_OUT_ENABLED");
                messageManagementEnabled = profileDetails.get("MESSAGE_MANAGEMENT_ENABLED");
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("Entered: promotionType=");
                	loggerValue.append(promotionType);
                	loggerValue.append(",optInOutEnabled=");
                	loggerValue.append(optInOutEnabled);
                	loggerValue.append(",messageManagementEnabled=");
                	loggerValue.append(messageManagementEnabled);
                    LOG.debug(methodName, loggerValue);
                }

            }
            String Message_code = "";
            if (isOptInOutAllow && PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(promotionType)
                    && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(optInOutEnabled)
                    && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
                Message_code = PretupsI.OPTINOUT_WEL_MESSAGE + "_" + profileId;
            } else if (isOptInOutAllow
                    && PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(promotionType)
                    && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(optInOutEnabled)
                    && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
                Message_code = PretupsI.OPTINOUT_TRA_WEL_MSG + "_" + profileId;
            } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(promotionType)
                    && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
                Message_code = PretupsI.TRA_WEL_MESSAGE + "_" + profileId;
            } else {
                Message_code = PretupsI.WEL_MESSAGE + "_" + profileId;
            }
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sqlSelect);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmt.setString(1, profileSetLMSVO.getLang1welcomemsg());
            pstmt.setString(2, profileSetLMSVO.getLang2welcomemsg());
            pstmt.setString(3, Message_code);

            final int countForVersion = pstmt.executeUpdate();
            if (countForVersion > 0) {
                flag = true;
            }
            // StringBuilder strBuff1 = new StringBuilder();
            Message_code = PretupsI.SUCCESS_MESSAGE + "_" + profileId;
            // strBuff1.append("update MESSAGES_MASTER  set MESSAGE1=?,MESSAGE2=? where message_code=? ");
            // sqlSelect = strBuff1.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sqlSelect);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmt.clearParameters();
            pstmt.setString(1, profileSetLMSVO.getLang1seccessmsg());
            pstmt.setString(2, profileSetLMSVO.getLang2seccessmsg());
            pstmt.setString(3, Message_code);
            final int countForVersion1 = pstmt.executeUpdate();
            if (countForVersion1 > 0) {
                flag = true;
            }
            // StringBuilder strBuff2 = new StringBuilder();
            Message_code = PretupsI.FAILURE_MESSAGE + "_" + profileId;
            // strBuff2.append("update MESSAGES_MASTER  set MESSAGE1=?,MESSAGE2=? where message_code=? ");
            // sqlSelect = strBuff2.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sqlSelect);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmt.clearParameters();
            pstmt.setString(1, profileSetLMSVO.getLang1failuremsg());
            pstmt.setString(2, profileSetLMSVO.getLang2failuremsg());
            pstmt.setString(3, Message_code);
            final int countForVersion2 = pstmt.executeUpdate();
            if (countForVersion2 > 0) {
                flag = true;
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[updateMessageList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[updateMessageList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: flag:");
            	loggerValue.append(flag);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return flag;
    }

    public ArrayList loadpromotionList(Connection p_con, String network_id) throws BTSLBaseException {
        final String methodName = "loadpromotionList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ProfileSetLMSVO profileSetLMSVO = null;
        final ArrayList list = new ArrayList();
        /*
         * ArrayList list1=null; ListValueVO listValueVO=null; listValueVO =new
         * ListValueVO()
         */;
        int index = 1;
        try {

            final String sqlSelect = activationBonusLMSWebQry.loadpromotionListQry();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sqlSelect);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(index++, PretupsI.YES);

            pstmt.setString(index++, PretupsI.LMS);
            pstmt.setString(index++, network_id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                profileSetLMSVO = new ProfileSetLMSVO();
                profileSetLMSVO.setSetId(rs.getString("set_id"));
                profileSetLMSVO.setSetName(rs.getString("set_name"));
                list.add(profileSetLMSVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[loadpromotionList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[loadpromotionList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: ");
            }
        }
        return list;
    }

    public ArrayList loadUserForLMSAssociation(Connection p_con, String p_domainCode, String p_geographyCode,
            String p_category_code, String p_user_id, String p_gradeCode, Map<String, String> p_msisdnUserIDMap,
            boolean workBookReq, WritableWorkbook workbook, MessageResources p_messages, Locale p_locale)
            throws BTSLBaseException {
        final String methodName = "loadUserForLMSAssociation";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_domainCode =");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(" p_geographyCode=");
        	loggerValue.append(p_geographyCode);
        	loggerValue.append(" p_category_code=");
        	loggerValue.append(p_category_code);
        	loggerValue.append(" p_gradeCode=");
        	loggerValue.append(p_gradeCode);
        	loggerValue.append(" p_user_id=");
        	loggerValue.append(p_user_id);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // ProfileSetLMSVO profileSetLMSVO=null;
        /*
         * ArrayList list=null; ArrayList list1=null; ListValueVO
         * listValueVO=null; listValueVO =new ListValueVO();
         */
        ArrayList batchList = new ArrayList();

        ChannelUserVO channelUserVO = null;
        final UserPhoneVO userPhoneVO = null;
        int sheetNO = 0;
        // ExcelRW excelRW=new ExcelRW();
        boolean written = false;
        final LMSExcelBL associateLMSAction = new LMSExcelBL();
        try {
            pstmt = activationBonusLMSWebQry.loadUserForLMSAssociationQry(p_con, p_domainCode, p_geographyCode,
                    p_category_code, p_user_id, p_gradeCode);
            rs = pstmt.executeQuery();
            final String password = null;
            while (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setUserProfileID(rs.getString("set_name"));
                channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));
                batchList.add(channelUserVO);
                if (workBookReq) {
                    p_msisdnUserIDMap.put(rs.getString("msisdn"), rs.getString("user_id"));
                    if (batchList.size() == Integer.parseInt(Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE"))) {
                        associateLMSAction.createWorkbook(batchList, sheetNO, workbook, p_messages, p_locale);
                        batchList = new ArrayList();
                        sheetNO++;
                        written = true;
                    }
                }
            }
            if (workBookReq && batchList.size() >= 0 && !written) {
                associateLMSAction.createWorkbook(batchList, sheetNO, workbook, p_messages, p_locale);
                batchList = new ArrayList();
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[loadUserForLMSAssociation]", "", "", "",
                    loggerValue.toString());
            throw new BTSLBaseException(this, "loadpromotionList", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[loadUserForLMSAssociation]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadpromotionList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: ");
            }
        }

        return batchList;
    }

    public int isprofileSingleVersionExist(Connection p_con, String p_setID) throws BTSLBaseException {
        final String methodName = "isprofileSingleVersionExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_setID =");
        	loggerValue.append(p_setID);
            LOG.debug(methodName, loggerValue);
        }
        int verCountExist = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("select count(*) as max from PROFILE_SET_VERSION where set_id=? and status in ('Y','W')");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sbf.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            final String selectQuery = sbf.toString();
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_setID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                verCountExist = rs.getInt("max");
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler
                    .handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "",
                            loggerValue.toString());
            throw new BTSLBaseException(this, "isprofileCategoryMapp", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: verCountExist:");
            	loggerValue.append(verCountExist);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return verCountExist;
    }

    public boolean resumeProfileSet(Connection p_con, String p_setID, String p_version, UserVO p_userVO,
            boolean p_resume) throws BTSLBaseException {
        final String methodName = "resumeProfileSet";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_setID =");
        	loggerValue.append(p_setID);
        	loggerValue.append(" p_version=");
        	loggerValue.append(p_version);
        	loggerValue.append(" resume=");
        	loggerValue.append(p_resume);
        	loggerValue.append(" p_user ID=");
        	loggerValue.append(p_userVO.getActiveUserID());
            LOG.debug(methodName, loggerValue);
        }
        int countForSet = 0;
        int countForVersion = 0;
        boolean flag = false;
        PreparedStatement pstmtSetUpdate = null;
        PreparedStatement pstmtVersionUpdate = null;
        StringBuilder sbf = null;
        try {
            if (p_resume) {
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set SET ");
                sbf.append("status='Y',modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=?");
                final String updateSetTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateSetTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version  SET ");
                sbf.append("status='Y',modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=? and version=? ");
                final String updateVersionTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateVersionTable);
        			LOG.debug(methodName, loggerValue);
        		}
                final Date currentDate = new Date();
                pstmtSetUpdate = p_con.prepareStatement(updateSetTable);
                pstmtVersionUpdate = p_con.prepareStatement(updateVersionTable);
                pstmtSetUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtSetUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtSetUpdate.setString(3, p_setID);
                pstmtVersionUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtVersionUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtVersionUpdate.setString(3, p_setID);
                pstmtVersionUpdate.setString(4, p_version);
                countForSet = pstmtSetUpdate.executeUpdate();
                countForVersion = pstmtVersionUpdate.executeUpdate();
                if (countForSet > 0 && countForVersion > 0) {
                    flag = true;
                }
            } else {

                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set  SET ");
                sbf.append("modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=?");
                final String updateSetTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateSetTable);
        			LOG.debug(methodName, loggerValue);
        		}
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version  SET ");
                sbf.append("status='Y',modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=? AND version=?");
                final String updateVersionTable = sbf.toString();
                if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Query =");
        			loggerValue.append(updateVersionTable);
        			LOG.debug(methodName, loggerValue);
        		}
                final Date currentDate = new Date();
                pstmtSetUpdate = p_con.prepareStatement(updateSetTable);
                pstmtVersionUpdate = p_con.prepareStatement(updateVersionTable);
                pstmtSetUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtSetUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtSetUpdate.setString(3, p_setID);
                pstmtVersionUpdate.setString(1, p_userVO.getActiveUserID());
                pstmtVersionUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
                pstmtVersionUpdate.setString(3, p_setID);
                pstmtVersionUpdate.setString(4, p_version);

                countForSet = pstmtSetUpdate.executeUpdate();
                countForVersion = pstmtVersionUpdate.executeUpdate();
                if (countForSet > 0 && countForVersion > 0) {
                    flag = true;
                }
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[resumeProfileSet]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "approveRejectProfileSet", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[resumeProfileSet]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtSetUpdate != null) {
                    pstmtSetUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtVersionUpdate != null) {
                    pstmtVersionUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: flag:");
            	loggerValue.append(flag);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return flag;
    }

    public int addMessage(Connection p_con, String p_code, String p_lang1, String p_lang2) throws BTSLBaseException {
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
            final StringBuilder updateQueryBuff = new StringBuilder(" UPDATE PROFILE_SET_VERSION SET ");
            updateQueryBuff.append(" MESSAGE1=?,MESSAGE2=? WHERE set_id=?  ");
            final String insertQuery = updateQueryBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(insertQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmtUpdate = p_con.prepareStatement(insertQuery);
            pstmtUpdate.setString(1, p_lang1);
            pstmtUpdate.setString(2, p_lang2);
            pstmtUpdate.setString(3, p_code);
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBounsDAO[updateOtherProfileForAssociation]", "", "", "",
                    loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[updateOtherProfileForAssociation]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return updateCount;
    }

    public boolean isprofileNmaeExist(Connection p_con, String profileName) throws BTSLBaseException {
        final String methodName = "isprofileNmaeExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: profileName =");
        	loggerValue.append(profileName);
            LOG.debug(methodName, loggerValue);
        }
        boolean isexists = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append(" select set_name from profile_set where (set_name=? or set_name=LOWER(?) )and status in ( 'Y','S','W')");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sbf.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            final String selectQuery = sbf.toString();
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, profileName);
            pstmt.setString(2, profileName);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                isexists = true;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler
                    .handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "",
                            loggerValue.toString());
            throw new BTSLBaseException(this, "isprofileCategoryMapp", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "isprofileSingleVersionExist", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: isexists:");
            	loggerValue.append(isexists);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return isexists;
    }

    public boolean isprofileAssociated(Connection p_con, String setID) throws BTSLBaseException {
        final String methodName = "isprofileAssociated";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_setID =");
        	loggerValue.append(setID);
            LOG.debug(methodName, loggerValue);
        }
        boolean isexists = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append(" select ch.user_id from channel_users ch,users u, profile_set ps ");
            sbf.append(" where u.user_id=ch.user_id and ch.lms_profile=ps.set_id and ps.set_id=? ");
            sbf.append(" and u.status not in ('N','C') ");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sbf.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            final String selectQuery = sbf.toString();
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, setID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                isexists = true;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[isprofileAssociated]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "isprofileSingleVersionExist", "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: isexists:");
            	loggerValue.append(isexists);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return isexists;
    }

    public boolean isprofileExpired(Connection p_con, String p_setID, String version) throws BTSLBaseException {
        final String methodName = "isprofileExpired";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_setID =");
        	loggerValue.append(p_setID);
            LOG.debug(methodName, loggerValue);
        }
        final int verCountExist = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isexpired = true;
        try {
            String sbf = activationBonusLMSWebQry.isprofileExpiredQry();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sbf.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            final String selectQuery = sbf.toString();
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_setID);
            pstmt.setString(2, version);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                isexpired = false;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler
                    .handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "",
                            loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: isexpired:");
            	loggerValue.append(isexpired);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return isexpired;
    }

    public boolean isprofileActive(Connection p_con, String p_setID, String version) throws BTSLBaseException {
        final String methodName = "isprofileActive";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_setID =");
        	loggerValue.append(p_setID);
            LOG.debug(methodName, loggerValue);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_setID= " + p_setID);
        }
        final int verCountExist = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isexpired = true;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("select set_id from PROFILE_SET_VERSION where set_id=? and version=? and status='Y' ");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sbf.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            final String selectQuery = sbf.toString();
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_setID);
            pstmt.setString(2, version);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                isexpired = false;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler
                    .handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "",
                            loggerValue.toString());
            throw new BTSLBaseException(this, "isprofileExpired", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[isprofileSingleVersionExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: isexpired:");
            	loggerValue.append(isexpired);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return isexpired;
    }

    public int addActivationBonusMessages(Connection p_con, ProfileSetLMSVO p_profileSetVO, String p_requestType)
            throws BTSLBaseException {
        // commented for DB2 OraclePreparedStatement psmtInsertUpdate = null;
        PreparedStatement psmtInsertUpdate = null;
        int insertCount = 0;
        StringBuilder strBuff = null;
        final String methodName = "addActivationBonusMessages";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_profileSetVO =");
        	loggerValue.append(p_profileSetVO.toString());
        	loggerValue.append(" p_requestType=");
        	loggerValue.append(p_requestType);
            LOG.debug(methodName, loggerValue);
        }
        String insertUpdateQuery = null;// if request is for add activation
        // profile
        try {

            strBuff = new StringBuilder();
            strBuff.append("Insert into MESSAGES_MASTER(MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE,");
            strBuff.append(" NETWORK_CODE, MESSAGE1,  MESSAGE2)");
            strBuff.append(" values ");
            strBuff.append(" (?,?,?,?,?,?)");
            insertUpdateQuery = strBuff.toString();

            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(insertUpdateQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            psmtInsertUpdate = (PreparedStatement) p_con.prepareStatement(insertUpdateQuery);
            psmtInsertUpdate.setString(1, "ALL");
            psmtInsertUpdate.setString(2, p_profileSetVO.getMessageCode());
            psmtInsertUpdate.setString(3, p_profileSetVO.getdefaultMessage());
            psmtInsertUpdate.setString(4, p_profileSetVO.getNetworkCode());
            psmtInsertUpdate.setString(5, p_profileSetVO.getMessage1());
            psmtInsertUpdate.setString(6, p_profileSetVO.getMessage2());
            insertCount = psmtInsertUpdate.executeUpdate();

        } // end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[addActivationBonusMessages]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[addActivationBonusMessages]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsertUpdate != null) {
                    psmtInsertUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount:");
            	loggerValue.append(insertCount);
            	LOG.debug(methodName, loggerValue);
            }
        } // end of finally
        return insertCount;
    }

    public ArrayList loadpromotionListForMessage(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadpromotionListForMessage";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ProfileSetLMSVO profileSetLMSVO = null;
        ArrayList list = null;
        /*
         * ArrayList list1=null; ListValueVO listValueVO=null; listValueVO =new
         * ListValueVO();
         */
        try {
            final StringBuilder strBuff = new StringBuilder();
            list = new ArrayList();
            // list1=new ArrayList();
            strBuff.append("SELECT set_id,set_name ");
            strBuff.append(" from profile_set where status in (?) and profile_type=? and MESSAGE_MANAGEMENT_ENABLED='Y'");// Brajesh
            final String sqlSelect = strBuff.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sqlSelect);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.YES);
            pstmt.setString(2, PretupsI.LMS);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileSetLMSVO = new ProfileSetLMSVO();
                profileSetLMSVO.setSetId(rs.getString("set_id"));
                profileSetLMSVO.setSetName(rs.getString("set_name"));
                list.add(profileSetLMSVO);
            }

        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[loadpromotionListForMessage]", "", "", "",
                    loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler
                    .handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "ActivationBonusLMSWebDAO[loadpromotionListForMessage]", "", "", "",
                            loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: ");
            }
        }

        return list;
    }

    public boolean isMessageExists(Connection p_con, String messageCode) throws BTSLBaseException {
        final String methodName = "isMessageExists";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: messageCode =");
        	loggerValue.append(messageCode);
            LOG.debug(methodName, loggerValue);
        }
        final int verCountExist = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isExists = false;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("select MESSAGE_CODE from MESSAGES_MASTER where MESSAGE_CODE=? ");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sbf.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            final String selectQuery = sbf.toString();
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, messageCode);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                isExists = true;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[isMessageExists]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "isprofileExpired", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[isMessageExists]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: isExists:");
            	loggerValue.append(isExists);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return isExists;
    }

    public Map<String, String> loadMapForLMSAssociation(Connection p_con, String p_geographyCode, String p_category_code,
            String p_user_id, String p_gradeCode, Map<String, String> p_msisdnUserIDMap) throws BTSLBaseException {
        final String methodName = "loadMapForLMSAssociation";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // ProfileSetLMSVO profileSetLMSVO=null;
        /*
         * ArrayList list=null; ArrayList list1=null; ListValueVO
         * listValueVO=null; listValueVO =new ListValueVO(); ArrayList batchList
         * =new ArrayList();
         */

        // ExcelRW excelRW=new ExcelRW();
        // LMSExcelBL associateLMSAction=new LMSExcelBL();
        try {
            pstmt = activationBonusLMSWebQry.loadMapForLMSAssociationQry(p_con, p_geographyCode, p_category_code,
                    p_user_id, p_gradeCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {

                p_msisdnUserIDMap.put(rs.getString("msisdn"), rs.getString("user_id"));

            }
            /*
             * if(batchList.size()>0) {
             * associateLMSAction.createWorkbook(batchList,sheetNO); batchList
             * =new ArrayList(); }
             */
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[loadMapForLMSAssociation]", "", "", "",
                    loggerValue.toString());
            throw new BTSLBaseException(this, "loadMapForLMSAssociation", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[loadMapForLMSAssociation]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadMapForLMSAssociation", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: ");
            }
        }
        return p_msisdnUserIDMap;
    }

    public int removeLmsProfileMapping(ArrayList userList, String p_setId, String categoryCode, String geographyCode,
            String gradeCode, String userID, String networkCode) throws BTSLBaseException {
        final String METHOD_NAME = "removeLmsProfileMapping";
        int updateCount = 0, i = 0, usercount = 0;
        ChannelUserVO channelUserVO = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: userList =");
        	loggerValue.append(userList);
            LOG.debug(METHOD_NAME, loggerValue);
        }
        PreparedStatement pstmtUpdate = null;
        final StringBuilder updateQueryBuff = new StringBuilder(
                "update Channel_users set LMS_PROFILE='', lms_profile_updated_on=? ");
        updateQueryBuff.append(" where user_id=?");

        final String updateQuery = updateQueryBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateQuery);
			LOG.debug(METHOD_NAME, loggerValue);
		}

        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            pstmtUpdate = (PreparedStatement) con.prepareStatement(updateQuery);
            int userListSize = userList.size();
            Date date = new Date();
            for (i = 0; i < userListSize; i++) {
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO = (ChannelUserVO) userList.get(i);
                pstmtUpdate.clearParameters();
                pstmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(date));
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

        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[removeLmsProfileMapping]", "", "", "",
                    loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[removeLmsProfileMapping]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }

        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                if (LOG.isDebugEnabled()) {
                    LOG.error(METHOD_NAME, " Exception Closing prepared statement: " + e.getMessage());
                }
            }
            if (mcomCon != null) {
                mcomCon.close("ActivationBonusLMSWebDAO#" + METHOD_NAME);
                mcomCon = null;
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: usercount:");
            	loggerValue.append(usercount);
            	LOG.debug(METHOD_NAME, loggerValue);
            }
        }
        return usercount;
    }

    // brajesh

    public HashMap<String, ArrayList<ProfileSetDetailsLMSVO>> loadLmsProfileCache() throws BTSLBaseException {

        final String methodName = "loadLmsProfileCache";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
            LOG.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final HashMap<String, ArrayList<ProfileSetDetailsLMSVO>> map = new HashMap<String, ArrayList<ProfileSetDetailsLMSVO>>();
        ArrayList<ProfileSetDetailsLMSVO> lmsProfileList = new ArrayList<ProfileSetDetailsLMSVO>();
        final String sqlSelect = activationBonusLMSWebQry.loadLmsProfileCacheQry();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}

        Connection con = null;
        try {
            final Date date = new Date();
            con = OracleUtil.getSingleConnection();
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(date));
            pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
            rs = pstmt.executeQuery();
            ProfileSetDetailsLMSVO lmsprofileVO = null;
            String prevSetId = null;
            while (rs.next()) {
                lmsprofileVO = new ProfileSetDetailsLMSVO();
                final String currSetId = rs.getString("set_id");
                if (!(currSetId.equals(prevSetId)) && (prevSetId != null)) {
                    map.put(prevSetId, lmsProfileList);
                    lmsProfileList = new ArrayList<ProfileSetDetailsLMSVO>();
                }
                lmsprofileVO.setSetId(rs.getString("set_id"));
                lmsprofileVO.setVersion(rs.getString("version"));
                lmsprofileVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                lmsProfileList.add(lmsprofileVO);
                prevSetId = currSetId;
            }
            map.put(prevSetId, lmsProfileList);
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[loadLmsProfileCache]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[loadLmsProfileCache]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: categoryList size:");
            	loggerValue.append(map.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return map;
    }

    // brajesh
    public Map loadLmsProfileDetailsCache(String p_setId, String p_profileVersion, long p_transferVal, Map map)
            throws BTSLBaseException {

        final String methodName = "loadLmsProfileDetailsCache";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
            LOG.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        final PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        final ResultSet rs1 = null;

        final StringBuilder strBuff = new StringBuilder(
                " SELECT pd.set_id, pd.VERSION, pd.TYPE, pd.user_type, pd.detail_subtype,pd.detail_id,pd.detail_type, ");
        strBuff.append("pd.period_id, pd.service_code, pd.start_range, pd.end_range, ");
        strBuff.append("pd.points_type, pd.points, pd.product_code, psv.applicable_from, psv.applicable_to, ps.set_name ");
        strBuff.append("FROM profile_details pd, profile_set_version psv, profile_set ps ");
        strBuff.append("WHERE psv.status = 'Y'  AND ps.profile_type = 'LMS' ");
        strBuff.append(" AND pd.set_id = ? AND pd.VERSION = ?  and pd.set_id=psv.SET_ID and psv.SET_ID=ps.SET_ID  and pd.start_range<=? and pd.end_range>=?");
        final String sqlSelect = strBuff.toString();

        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_setId);
            pstmt.setString(2, p_profileVersion);
            pstmt.setLong(3, p_transferVal);
            pstmt.setLong(4, p_transferVal);
            rs = pstmt.executeQuery();

            final ArrayList lmsProfileDetailList = new ArrayList();
            ProfileSetDetailsLMSVO profileSetDetailsLMSVO = null;
            while (rs.next()) {
                profileSetDetailsLMSVO = new ProfileSetDetailsLMSVO();
                profileSetDetailsLMSVO.setSetId(rs.getString("set_id"));
                profileSetDetailsLMSVO.setVersion(rs.getString("VERSION"));
                profileSetDetailsLMSVO.setType(rs.getString("TYPE"));
                profileSetDetailsLMSVO.setUserType(rs.getString("user_type"));
                profileSetDetailsLMSVO.setDetailSubType(rs.getString("detail_subtype"));
                profileSetDetailsLMSVO.setPeriodId(rs.getString("period_id"));
                profileSetDetailsLMSVO.setServiceCode(rs.getString("service_code"));
                profileSetDetailsLMSVO.setStartRangeAsString(rs.getString("start_range"));
                profileSetDetailsLMSVO.setEndRangeAsString(rs.getString("end_range"));
                profileSetDetailsLMSVO.setPointsTypeCode(rs.getString("points_type"));
                profileSetDetailsLMSVO.setPointsAsString(rs.getString("points"));
                profileSetDetailsLMSVO.setProductCode(rs.getString("product_code"));
                profileSetDetailsLMSVO.setSetName(rs.getString("set_name"));
                profileSetDetailsLMSVO.setDetailId(rs.getString("detail_id"));
                profileSetDetailsLMSVO.setDetailType(rs.getString("detail_type"));
                profileSetDetailsLMSVO.setApplicableFrom(rs.getDate("applicable_from"));
                profileSetDetailsLMSVO.setApplicableTo(rs.getDate("applicable_to"));
                lmsProfileDetailList.add(profileSetDetailsLMSVO);
            }

            map.put(p_setId + "_" + p_profileVersion + "_" + p_transferVal, lmsProfileDetailList);
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[loadLmsProfileDetailsCache]", "", "", "",
                    loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusLMSWebDAO[loadLmsProfileDetailsCache]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (mcomCon != null) {
                mcomCon.close("ActivationBonusLMSWebDAO[loadLmsProfileDetailsCache]");
                mcomCon = null;
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: categoryList size:");
            	loggerValue.append(map.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return map;
    }

    public int[] addLmsProfileMapping(ArrayList userList, String p_setId, String categoryCode, String geographyCode,
            String gradeCode, String userID, String networkCode, boolean isAssociationRequiredFlag)
            throws BTSLBaseException {
        final String methodName = "addLmsProfileMapping";
        int addCount = -1, i = 0, userAssociateCount = 0, userDeassociateCount = 0, mappingInsert, updateCount = -1;
        final int[] retAssociateDeassociateCount = new int[2];
        ChannelUserVO channelUserVO = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        final Date date = new Date();
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: VO =");
            LOG.debug(methodName, loggerValue);
        }
        // commented for DB2 OraclePreparedStatement pstmtInsert = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtAssociateLMS = null;
        PreparedStatement pstmtDeassociateLMS = null;
        final StringBuilder updateQueryBuff = new StringBuilder(
                "update Channel_users set LMS_PROFILE=?, lms_profile_updated_on=?, OPT_IN_OUT_STATUS=?, CONTROL_GROUP=? ");
        updateQueryBuff.append(" where user_id=?");
        final String updateQuery = updateQueryBuff.toString();
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("Insert into PROFILE_MAPPING");
        strBuff.append(" (SRV_CLASS_OR_CATEGORY_CODE, PROFILE_TYPE, SET_ID, IS_DEFAULT, CREATED_ON, ");
        strBuff.append("    CREATED_BY, MODIFIED_ON, MODIFIED_BY, GRADE_CODE, ");
        strBuff.append(" GRPH_DOMAIN_CODE,NETWORK_CODE)");
        strBuff.append(" Values  (?,?,?,?,?,?,?,?,?,?,?)");
        final String insertUpdateQuery = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(updateQuery);
			LOG.debug(methodName, loggerValue);
		}
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append("Query =");
			loggerValue.append(insertUpdateQuery);
			LOG.debug(methodName, loggerValue);
		}

        final StringBuilder updateQueryBuff4Remove = new StringBuilder(
                "update Channel_users set LMS_PROFILE='', lms_profile_updated_on=?, OPT_IN_OUT_STATUS=?, CONTROL_GROUP=? ");
        updateQueryBuff4Remove.append(" where user_id=?");

        final String updateQuery4Remove = updateQueryBuff4Remove.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "update Query for de-associate LMS Profile= " + updateQuery4Remove);
        }

        try {
            // commented for DB2 pstmtInsert =
            // (OraclePreparedStatement)p_con.prepareStatement(insertQuery);
            int k = 0;
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            if (isAssociationRequiredFlag) {
                pstmtInsert = (PreparedStatement) con.prepareStatement(insertUpdateQuery);
                pstmtInsert.setString(++k, categoryCode);
                pstmtInsert.setString(++k, PretupsI.LMS);
                pstmtInsert.setString(++k, p_setId);
                pstmtInsert.setString(++k, PretupsI.NO);
                pstmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(date));
                pstmtInsert.setString(++k, userID);
                pstmtInsert.setDate(++k, BTSLUtil.getSQLDateFromUtilDate(date));
                pstmtInsert.setString(++k, userID);
                pstmtInsert.setString(++k, gradeCode);
                pstmtInsert.setString(++k, geographyCode);
                pstmtInsert.setString(++k, networkCode);
                mappingInsert = pstmtInsert.executeUpdate();
            }
            // pstmtInsert.clearParameters();
            pstmtAssociateLMS = (PreparedStatement) con.prepareStatement(updateQuery);
            pstmtDeassociateLMS = (PreparedStatement) con.prepareStatement(updateQuery4Remove);
            int index = 1;
            int userListSize = userList.size();
            for (i = 0; i < userListSize; i++) {
                index = 1;
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO = (ChannelUserVO) userList.get(i);
                if (PretupsI.YES.equalsIgnoreCase(channelUserVO.getAssType())) {
                    pstmtAssociateLMS.setString(index++, p_setId);
                    pstmtAssociateLMS.setTimestamp(index++, BTSLUtil.getTimestampFromUtilDate(date));
                    pstmtAssociateLMS.setString(index++, PretupsI.NORMAL);
                    pstmtAssociateLMS.setString(index++, channelUserVO.getControlGroup());
                    pstmtAssociateLMS.setString(index++, channelUserVO.getUserID());
                    addCount = pstmtAssociateLMS.executeUpdate();
                    if (addCount == 1) {
                        mcomCon.finalCommit();
                        userAssociateCount++;
                        addCount = -1;
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(methodName, "The user " + channelUserVO.getUserID()
                                    + " has been associate LMS Profile " + p_setId);
                        }
                    } else {
                        mcomCon.finalRollback();
                    }
                } else if (PretupsI.NO.equalsIgnoreCase(channelUserVO.getAssType())) {
                    pstmtDeassociateLMS.clearParameters();
                    pstmtDeassociateLMS.setTimestamp(index++, BTSLUtil.getTimestampFromUtilDate(date));
                    pstmtDeassociateLMS.setString(index++, PretupsI.NORMAL);
                    pstmtDeassociateLMS.setString(index++, channelUserVO.getControlGroup());
                    pstmtDeassociateLMS.setString(index++, channelUserVO.getUserID());
                    updateCount = pstmtDeassociateLMS.executeUpdate();
                    if (updateCount >= 0) {
                        mcomCon.finalCommit();
                        userDeassociateCount++;
                        updateCount = -1;
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(methodName, "The user " + channelUserVO.getUserID()
                                    + " has been de-associate LMS Profile " + p_setId);
                        }
                    } else {
                        mcomCon.finalRollback();
                    }
                }
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "LoyalityDAO[addInterfaceDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "addInterfaceDetails", "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "LoyalityDAO[addInterfaceDetails]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "addInterfaceDetails", "error.general.processing");
        } finally {
            try {
                if (pstmtAssociateLMS != null) {
                    pstmtAssociateLMS.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtDeassociateLMS != null) {
                    pstmtDeassociateLMS.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (mcomCon != null) {
                mcomCon.close("ActivationBonusLMSWebDAO#addRewardDetails");
                mcomCon = null;
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: updateCount:");
            	loggerValue.append(updateCount);
            	loggerValue.append(" addCount:");
            	loggerValue.append(addCount);
            	LOG.debug(methodName, loggerValue);
            }
            retAssociateDeassociateCount[0] = userAssociateCount;
            retAssociateDeassociateCount[1] = userDeassociateCount;
        }
        return retAssociateDeassociateCount;
    }

    public HashMap<String, String> fetchLMSProfleDetails(Connection p_con, String p_setID) throws BTSLBaseException {
        final String methodName = "fetchLMSProfleDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_setID =");
        	loggerValue.append(p_setID);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, String> map = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("select PROMOTION_TYPE,OPT_IN_OUT_ENABLED,MESSAGE_MANAGEMENT_ENABLED from PROFILE_SET where set_id=? ");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sbf.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            final String selectQuery = sbf.toString();
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_setID);

            rs = pstmt.executeQuery();
            map = new HashMap<String, String>();
            if (rs.next()) {
                map.put("PROMOTION_TYPE", rs.getString("PROMOTION_TYPE"));
                map.put("OPT_IN_OUT_ENABLED", rs.getString("OPT_IN_OUT_ENABLED"));
                map.put("MESSAGE_MANAGEMENT_ENABLED", rs.getString("MESSAGE_MANAGEMENT_ENABLED"));
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[" + methodName + "]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[" + methodName + "]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: map.size:");
            	loggerValue.append(map.size());
            	LOG.debug(methodName, loggerValue);
            }
        }
        return map;
    }

    public ArrayList loadUserForLMSAssociation(Connection p_con, String p_domainCode, String p_geographyCode,
            String p_category_code, String p_user_id, String p_gradeCode, Map<String, String> p_msisdnUserIDMap,
            boolean workBookReq, WritableWorkbook workbook, Locale p_locale, MessageResources p_messages)
            throws BTSLBaseException {
        final String methodName = "loadUserForLMSAssociation";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_domainCode =");
        	loggerValue.append(p_domainCode);
        	loggerValue.append(" p_geographyCode=");
        	loggerValue.append(p_geographyCode);
        	loggerValue.append(" p_category_code=");
        	loggerValue.append(p_category_code);
        	loggerValue.append(" p_gradeCode=");
        	loggerValue.append(p_gradeCode);
        	loggerValue.append(" p_user_id=");
        	loggerValue.append(p_user_id);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // ProfileSetLMSVO profileSetLMSVO=null;
        /*
         * ArrayList list=null; ArrayList list1=null; ListValueVO
         * listValueVO=null; listValueVO =new ListValueVO();
         */
        ArrayList batchList = new ArrayList();
        ChannelUserVO channelUserVO = null;
        final UserPhoneVO userPhoneVO = null;
        int sheetNO = 0;
        // ExcelRW excelRW=new ExcelRW();
        // AssociateLMSAction associateLMSAction=new AssociateLMSAction();
        try {
            pstmt = activationBonusLMSWebQry.loadUserForLMSAssociationQuery(p_con, p_domainCode, p_geographyCode,
                    p_category_code, p_user_id, p_gradeCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                channelUserVO = ChannelUserVO.getInstance();
                channelUserVO.setUserID(rs.getString("user_id"));
                channelUserVO.setMsisdn(rs.getString("msisdn"));
                channelUserVO.setUserProfileID(rs.getString("set_name"));
                channelUserVO.setControlGroup(rs.getString("CONTROL_GROUP"));
                channelUserVO.setAssType(PretupsI.YES);
                batchList.add(channelUserVO);
                if (workBookReq) {
                    p_msisdnUserIDMap.put(rs.getString("msisdn"), rs.getString("user_id"));
                    if (batchList.size() == Integer.parseInt(Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE"))) {
                        createWorkbook(batchList, sheetNO, workbook, p_locale, p_messages);
                        batchList = new ArrayList();
                        sheetNO++;
                    }
                }
            }
            if (workBookReq && batchList.size() > 0) {
                createWorkbook(batchList, sheetNO, workbook, p_locale, p_messages);
                batchList = new ArrayList();
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "LoyalityDAO[loadUserForLMSAssociation]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadpromotionList", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "LoyalityDAO[loadUserForLMSAssociation]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadpromotionList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting: ");
            }
        }

        return batchList;
    }

    public void createWorkbook(ArrayList dataList, int SheetNo, WritableWorkbook workbook, Locale p_locale,
            MessageResources p_messages) {
        final String METHOD_NAME = "createWorkbook";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
            LOG.debug(METHOD_NAME, loggerValue);
        }
        String fileArr[][] = null;
        String controlGroup = "Y";
        try {
            controlGroup = Constants.getProperty("LMS_CONTROL_GROUP_REQUIRED");
        } catch (RuntimeException e1) {
            controlGroup = "Y";
        }
        int cols = 3;
        if (PretupsI.YES.equals(controlGroup)) {
            cols = 4;
        }
        final int rows = dataList.size() + 1;
        fileArr = new String[rows][cols]; // ROW-COL
        fileArr[0][0] = "lmsprofile.xlsheading.label.msisdn";
        fileArr[0][1] = "lmsprofile.xlsheading.label.associate.currently";
        fileArr[0][2] = "lmsprofile.xlsheading.label.associate.required";
        if (PretupsI.YES.equals(controlGroup)) {
            fileArr[0][3] = "lmsprofile.xlsheading.label.controlgroup.required";
        }
        try {
            fileArr = this.convertTo2dArray(fileArr, dataList, rows, controlGroup);
            final ExcelRW excelRW = new ExcelRW();
            // excelRW.writeExcel(ExcelFileIDI.BATCH_FOC_INITIATE,fileArr,((MessageResources)
            // request.getAttribute(Globals.MESSAGES_KEY)),BTSLUtil.getBTSLLocale(request),filePath+""+fileName);
            excelRW.writeMultipleExcelNew(workbook, ExcelFileIDI.BATCH_FOC_INITIATE, fileArr, p_messages, p_locale,
                    SheetNo);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("createWorkbook", "Exited:");
            }
        }

    }

    private String[][] convertTo2dArray(String[][] p_fileArr, ArrayList dataList, int p_rows, String p_controlGroup)
            throws BTSLBaseException {
        final String METHOD_NAME = "convertTo2dArray";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fileArr =");
        	loggerValue.append(p_fileArr);
        	loggerValue.append(" dataList=");
        	loggerValue.append(dataList);
            LOG.debug(METHOD_NAME, loggerValue);
        }
        try {
            // first row is already generated,and the number of cols are fixed
            // to eight
            // Iterator iterator=p_hashMap.keySet().iterator();
            String key = null;
            ChannelUserVO channelUserVO = null;
            int rows = 0;
            int cols;
            int   dataListSize = dataList.size();
            for (int i = 0; i < dataListSize; i++) {
                key = String.valueOf(i + 1);
                channelUserVO = (ChannelUserVO) dataList.get(i);
                // Only those records are written into the xls file for which
                // status='Y' and insuspend='N'

                {
                    rows++;
                    if (rows >= p_rows) {
                        break;
                    }
                    cols = 0;
                    // p_fileArr[rows][cols++]=key;
                    p_fileArr[rows][cols++] = channelUserVO.getMsisdn();

                    p_fileArr[rows][cols++] = channelUserVO.getUserProfileID();// Y
                    // or
                    // N
                    // (for
                    // profile
                    // to
                    // be
                    // associated
                    // or
                    // not)
                    p_fileArr[rows][cols++] = "";// Y or N (for profile to be
                    // associated or not)
                    if (PretupsI.YES.equals(p_controlGroup)) {
                        if (PretupsI.YES.equalsIgnoreCase(channelUserVO.getControlGroup())) {
                            p_fileArr[rows][cols++] = channelUserVO.getControlGroup();// Y
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
                            p_fileArr[rows][cols++] = "";
                        }
                    }
                }

            }
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ActivationBonusLMSWebDAO", METHOD_NAME, "");
        } finally {
        	if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: p_fileArr:");
            	loggerValue.append(p_fileArr);
            	LOG.debug(METHOD_NAME, loggerValue);
            }
        }
        return p_fileArr;
    }

    // Handling of controlled profile
    public boolean isControlledProfileAlreadyAssociated(String msisdn) throws BTSLBaseException {
        final String METHOD_NAME = "isControlledProfileAlreadyAssociated";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: msisdn =");
        	loggerValue.append(msisdn);
            LOG.debug(METHOD_NAME, loggerValue);
        }
        boolean returnVal = false;
        Connection con = null;
        MComConnectionI mcomCon = null;
        PreparedStatement pstmtSelect = null;
        ResultSet resultSet = null;

        final String selectQuery = activationBonusLMSWebQry.isControlledProfileAlreadyAssociatedQry();
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "selectQuery= " + selectQuery);
        }

        try {
            int k = 0;
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            pstmtSelect = (PreparedStatement) con.prepareStatement(selectQuery);
            pstmtSelect.clearParameters();
            pstmtSelect.setString(++k, PretupsI.YES);
            pstmtSelect.setString(++k, PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT);
            pstmtSelect.setString(++k, PretupsI.LMS_PROMOTION_TYPE_STOCK);
            pstmtSelect.setString(++k, msisdn);
            resultSet = pstmtSelect.executeQuery();
            while (resultSet.next()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Entered :" + msisdn);
                }
                returnVal = true;
                break;
            }
        }

        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "AssociateLMSAction[" + METHOD_NAME + "]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "AssociateLMSAction[" + METHOD_NAME + "]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }

        finally {
        	try {
                if (resultSet != null) {
                	resultSet.close();
                }
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.error(METHOD_NAME,
                            " Exception Closing prepared statement in " + METHOD_NAME + ": " + e.getMessage());
                }
                LOG.errorTrace(METHOD_NAME, e);
            }
        	try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.error(METHOD_NAME,
                            " Exception Closing prepared statement in " + METHOD_NAME + ": " + e.getMessage());
                }
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (mcomCon != null) {
                mcomCon.close("ActivationBonusLMSWebDAO#" + METHOD_NAME);
                mcomCon = null;
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: returnVal:");
            	loggerValue.append(returnVal);
            	LOG.debug(METHOD_NAME, loggerValue);
            }
        }
        return returnVal;

    }

    // Handling of controlled profile
    public boolean isProfileActive(String msisdn, String setId) throws BTSLBaseException {
        final String METHOD_NAME = "isProfileActive";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: msisdn =");
        	loggerValue.append(msisdn);
        	loggerValue.append(" setId=");
        	loggerValue.append(setId);
            LOG.debug(METHOD_NAME, loggerValue);
        }
        boolean returnVal = false;
        Connection con = null;
        MComConnectionI mcomCon = null;
        PreparedStatement pstmtSelect = null;
        ResultSet resultSet = null;

        final String selectQuery = activationBonusLMSWebQry.isProfileActiveQry();
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "selectQuery= " + selectQuery);
        }

        try {
            int k = 0;
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            pstmtSelect = (PreparedStatement) con.prepareStatement(selectQuery);
            pstmtSelect.clearParameters();
            pstmtSelect.setString(++k, setId);
            pstmtSelect.setString(++k, PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT);
            pstmtSelect.setString(++k, PretupsI.LMS_PROMOTION_TYPE_STOCK);
            resultSet = pstmtSelect.executeQuery();
            while (resultSet.next()) {
            	if(LOG.isDebugEnabled()){
        			loggerValue.setLength(0);
        			loggerValue.append("Entered =");
        			loggerValue.append(msisdn);
        			LOG.debug(METHOD_NAME, loggerValue);
        		}
                if (resultSet.getInt("total") > 0) {
                    returnVal = true;
                }
            }
        }

        catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "AssociateLMSAction[" + METHOD_NAME + "]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        }

        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(METHOD_NAME, loggerValue);
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "AssociateLMSAction[" + METHOD_NAME + "]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }

        finally {
        	try {
                if (resultSet != null) {
                	resultSet.close();
                }
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.error(METHOD_NAME,
                            " Exception Closing prepared statement in " + METHOD_NAME + ": " + e.getMessage());
                }
                LOG.errorTrace(METHOD_NAME, e);
            }
        	try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.error(METHOD_NAME,
                            " Exception Closing prepared statement in " + METHOD_NAME + ": " + e.getMessage());
                }
                LOG.errorTrace(METHOD_NAME, e);
            }
            if (mcomCon != null) {
                mcomCon.close("ActivationBonusLMSWebDAO#"+METHOD_NAME);
                mcomCon = null;
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: returnVal:");
            	loggerValue.append(returnVal);
            	LOG.debug(METHOD_NAME, loggerValue);
            }
        }
        return returnVal;
    }

    public ArrayList loadSuspendedVersions(Connection p_con, String p_profileSetId) throws BTSLBaseException {
        final String methodName = "loadSuspendedVersions";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_profileSetId =");
        	loggerValue.append(p_profileSetId);
            LOG.debug(methodName, loggerValue);
        }
        ArrayList list = null;
        StringBuilder sbf = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ProfileSetVersionLMSVO versionVO = null;
        try {
            list = new ArrayList();
            sbf = new StringBuilder();
            sbf.append("SELECT psv.set_id,psv.VERSION,psv.applicable_from,psv.bonus_duration ");
            sbf.append("FROM PROFILE_SET_VERSION psv ");
            sbf.append(" where psv.status IN (?) ");
            if (p_profileSetId != null) {
                sbf.append("AND psv.set_id=? ");
            }
            sbf.append(" order by psv.VERSION desc ");
            final String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(selectQuery);
    			LOG.debug(methodName, loggerValue);
    		}
            pstmt = p_con.prepareStatement(selectQuery);
            int i = 0;
            pstmt.setString(++i, PretupsI.USER_STATUS_SUSPEND);
            if (p_profileSetId != null) {
                pstmt.setString(++i, p_profileSetId);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                versionVO = new ProfileSetVersionLMSVO();
                versionVO.setSetId(rs.getString("set_id"));
                versionVO.setVersion(rs.getString("version"));
                versionVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                versionVO.setBonusDuration(rs.getLong("bonus_duration"));
                list.add(versionVO);
            }

        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[" + methodName + "]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[" + methodName + "]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle) {
                    LOG.errorTrace(methodName, sqle);
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }

        return list;
    }

    public int isSingleSusVersionExist(Connection p_con, String p_setID) throws BTSLBaseException {
        final String methodName = "isSingleSusVersionExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_setID =");
        	loggerValue.append(p_setID);
            LOG.debug(methodName, loggerValue);
        }
        int verCountExist = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("select count(*) as max from PROFILE_SET_VERSION where set_id=? and status in ('S')");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append("Query =");
    			loggerValue.append(sbf.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            final String selectQuery = sbf.toString();
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_setID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                verCountExist = rs.getInt("max");
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[" + methodName + "]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[" + methodName + "]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: verCountExist:");
            	loggerValue.append(verCountExist);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return verCountExist;
    }

    public Map<String, Integer> countOfTotalVersionSuspendActive(Connection p_con, String p_setID)
            throws BTSLBaseException {
        final String methodName = "countOfTotalVersionSuspendActive";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_setID =");
        	loggerValue.append(p_setID);
            LOG.debug(methodName, loggerValue);
        }
        Map<String, Integer> countOftotalVersionSuspendActive = new HashMap<String, Integer>(1);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            StringBuilder selectQueryBuff = new StringBuilder(
                    " select totalVersion, totalSuspendedVersion, totalActiveVersion ");
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
    			loggerValue.append("Query =");
    			loggerValue.append(selectQueryBuff.toString());
    			LOG.debug(methodName, loggerValue);
    		}
            String selectQuery = selectQueryBuff.toString();
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_setID);
            pstmt.setString(2, p_setID);
            pstmt.setString(3, PretupsI.SUSPEND);
            pstmt.setString(4, p_setID);
            pstmt.setString(5, PretupsI.YES);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                countOftotalVersionSuspendActive.put("totalVersion", rs.getInt("totalVersion"));
                countOftotalVersionSuspendActive.put("totalSuspendedVersion", rs.getInt("totalSuspendedVersion"));
                countOftotalVersionSuspendActive.put("totalActiveVersion", rs.getInt("totalActiveVersion"));
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[" + methodName + "]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[" + methodName + "]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: countOftotalVersionSuspendActive:");
            	loggerValue.append(countOftotalVersionSuspendActive);
            	LOG.debug(methodName, loggerValue);
            }
        }
        return countOftotalVersionSuspendActive;
    }

    public ArrayList loadProfileDetailsVersionList(Connection p_con, String p_networkCode, String promotionType, String status, ArrayList p_profileList)
            throws BTSLBaseException {
        final String methodName = "loadProfileDetailsVersionList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_networkCode =");
            loggerValue.append(p_networkCode);
            loggerValue.append(" p_profileList size=");
            loggerValue.append(p_profileList.size());
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ProfileSetLMSVO profileSetVO = null;
        ResultSet rs = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT  ps.set_id,ps.set_name,ps.short_code,psv.applicable_from,psv.bonus_duration,psv.one_time_bonus,psv.version,ps.status,ps.last_version,psv.applicable_to,ps.promotion_type, ps.message_management_enabled ");
            sbf.append("FROM PROFILE_SET ps, PROFILE_SET_VERSION psv ");
            sbf.append("WHERE ps.network_code =? and ps.status NOT IN ('N') AND ps.last_version =psv.version AND ps.profile_type =? ");
            if(promotionType != null) {
                sbf.append("AND ps.promotion_type =? ");
            }
            if(status != null){
                sbf.append("AND ps.status =? ");
            }
            sbf.append("AND psv.set_id=ps.set_id ORDER BY ps.set_name");

            final String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
                loggerValue.setLength(0);
                loggerValue.append("Query =");
                loggerValue.append(selectQuery);
                LOG.debug(methodName, loggerValue);
            }
            int i=1;
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(i++, p_networkCode);
            pstmt.setString(i++, PretupsI.LMS_PROFILE_TYPE);
            if(promotionType != null){
                pstmt.setString(i++, promotionType);
            }
            if(status != null){
                pstmt.setString(i++, status);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileSetVO = new ProfileSetLMSVO();
                profileSetVO.setSetId(rs.getString("set_id"));
                profileSetVO.setSetName(rs.getString("set_name"));
                profileSetVO.setLastVersion(rs.getString("last_version"));
                profileSetVO.setShortCode(rs.getString("short_code"));
                profileSetVO.setApplicableFromDate(String.valueOf(rs.getTimestamp("applicable_from")));
                profileSetVO.setApplicableToDate(String.valueOf(rs.getTimestamp("applicable_to")));
                profileSetVO.setPromotionType(rs.getString("promotion_type"));
                profileSetVO.setStatus(rs.getString("status"));
                profileSetVO.setMsgConfEnableFlag(rs.getString("message_management_enabled"));
                p_profileList.add(profileSetVO);
            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append(SQL_EXCEPTION);
            loggerValue.append(sqle.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO["+ methodName +" ]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(EXCEPTION);
            loggerValue.append(e.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO["+ methodName +" ]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle) {
                    LOG.errorTrace(methodName, sqle);
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
                loggerValue.setLength(0);
                loggerValue.append("Exiting: profileListSize:");
                loggerValue.append(p_profileList.size());
                LOG.debug(methodName, loggerValue);
            }
        }
        return p_profileList;
    }
    public ProfileSetLMSVO loadProfileDetails(Connection con, String networkCode, String setId, String version)
            throws BTSLBaseException {
        final String methodName = "loadProfileDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_networkCode =");
            loggerValue.append(networkCode);
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ProfileSetLMSVO profileSetVO = null;
        ResultSet rs = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT  ps.set_id,ps.set_name,ps.short_code,psv.applicable_from,psv.bonus_duration,psv.one_time_bonus,psv.version,ps.status,ps.last_version,psv.applicable_to,ps.promotion_type, ps.message_management_enabled ");
            sbf.append("FROM PROFILE_SET ps, PROFILE_SET_VERSION psv ");
            sbf.append("WHERE ps.network_code =? and ps.status NOT IN ('N') AND ps.profile_type =? ");
            if(setId != null) {
                sbf.append("AND ps.set_id =? ");
            }
            if(version != null){
                sbf.append("AND psv.version =? ");
            }
            sbf.append("AND psv.set_id=ps.set_id ORDER BY ps.set_name");

            final String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
                loggerValue.setLength(0);
                loggerValue.append("Query =");
                loggerValue.append(selectQuery);
                LOG.debug(methodName, loggerValue);
            }
            int i=1;
            pstmt = con.prepareStatement(selectQuery);
            pstmt.setString(i++, networkCode);
            pstmt.setString(i++, PretupsI.LMS_PROFILE_TYPE);
            if(setId != null){
                pstmt.setString(i++, setId);
            }
            if(version !=null){
                pstmt.setString(i++, version);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileSetVO = new ProfileSetLMSVO();
                profileSetVO.setSetId(rs.getString("set_id"));
                profileSetVO.setSetName(rs.getString("set_name"));
                profileSetVO.setLastVersion(rs.getString("last_version"));
                profileSetVO.setShortCode(rs.getString("short_code"));
                profileSetVO.setApplicableFromDate(String.valueOf(rs.getTimestamp("applicable_from")));
                profileSetVO.setApplicableToDate(String.valueOf(rs.getTimestamp("applicable_to")));
                profileSetVO.setPromotionType(rs.getString("promotion_type"));
                profileSetVO.setStatus(rs.getString("status"));
                profileSetVO.setMsgConfEnableFlag(rs.getString("message_management_enabled"));

            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append(SQL_EXCEPTION);
            loggerValue.append(sqle.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO["+ methodName +" ]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(EXCEPTION);
            loggerValue.append(e.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO["+ methodName +" ]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle) {
                    LOG.errorTrace(methodName, sqle);
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
                loggerValue.setLength(0);
                LOG.debug(methodName, loggerValue);
            }
        }
        return profileSetVO;
    }


    public ArrayList loadApprovalProfileListWithApplicableDate(Connection con, String networkCode,String setId, String version, ArrayList profileList)
            throws BTSLBaseException {
        final String methodName = "loadApprovalProfileListWithApplicableDate";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_networkCode =");
            loggerValue.append(networkCode);
            loggerValue.append(" p_profileList size=");
            loggerValue.append(profileList.size());
            LOG.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ProfileSetLMSVO profileSetVO = null;
        ResultSet rs = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT ps.profile_type,ps.set_id,ps.set_name,psv.version,ps.short_code,ps.status,ps.promotion_type, psv.applicable_from, psv.applicable_to, ps.message_management_enabled ");
            sbf.append("FROM profile_set ps, PROFILE_SET_VERSION psv ");
            sbf.append("WHERE ps.network_code=? AND ps.status<>'N' AND  psv.status='W' AND ps.set_id=psv.set_id AND ps.profile_type=? ");
            if(setId != null){
              sbf.append("AND ps.set_id=? ");
            }
            if(version != null){
                sbf.append("AND psv.version=? ");
            }
            sbf.append("ORDER BY ps.set_name");
            final String selectQuery = sbf.toString();
            if(LOG.isDebugEnabled()){
                loggerValue.setLength(0);
                loggerValue.append("Query =");
                loggerValue.append(selectQuery);
                LOG.debug(methodName, loggerValue);
            }
            int i =1;
            pstmt = con.prepareStatement(selectQuery);
            pstmt.setString(i++, networkCode);
            pstmt.setString(i++, PretupsI.LMS_PROFILE_TYPE);
            if(setId != null)
                pstmt.setString(i++, setId);
            if(version != null)
                pstmt.setString(i++, version);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileSetVO = new ProfileSetLMSVO();
                profileSetVO.setProfileType(rs.getString("profile_type"));
                profileSetVO.setSetId(rs.getString("set_id"));
                profileSetVO.setSetName(rs.getString("set_name"));
                profileSetVO.setLastVersion(rs.getString("version"));
                profileSetVO.setShortCode(rs.getString("short_code"));
                profileSetVO.setStatus(rs.getString("status"));
                profileSetVO.setPromotionType(rs.getString("promotion_type"));
                profileSetVO.setApplicableFromDate(String.valueOf(rs.getTimestamp("applicable_from")));
                profileSetVO.setApplicableToDate(String.valueOf(rs.getTimestamp("applicable_to")));
                profileSetVO.setMsgConfEnableFlag(rs.getString("message_management_enabled"));
                profileList.add(profileSetVO);
            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append(SQL_EXCEPTION);
            loggerValue.append(sqle.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadApprovalProfileListWithApplicableDate]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadApprovalProfileListWithApplicableDate", "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(EXCEPTION);
            loggerValue.append(e.getMessage());
            LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationBonusDAO[loadApprovalProfileListWithApplicableDate]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException sqle) {
                    LOG.errorTrace(methodName, sqle);
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if(LOG.isDebugEnabled()){
                loggerValue.setLength(0);
                loggerValue.append("Exiting: profileList.size:");
                loggerValue.append(profileList.size());
                LOG.debug(methodName, loggerValue);
            }
        }
        return profileList;
    }


}
