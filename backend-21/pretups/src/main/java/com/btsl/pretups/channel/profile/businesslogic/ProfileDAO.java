package com.btsl.pretups.channel.profile.businesslogic;

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
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

public class ProfileDAO {

    private static Log LOG = LogFactory.getLog(ProfileDAO.class.getName());

    /**
     * rahul.dutt
     * method to insert profile type into profile set table
     * 
     * @param p_con
     * @param p_profileSetVO
     * @return int
     * @throws BTSLBaseException
     */

    public int addActivationBonusSet(Connection p_con, ProfileSetVO p_profileSetVO, String p_requestType) throws BTSLBaseException {
        PreparedStatement psmtInsertUpdate = null;
        int insertCount = 0;
        StringBuilder strBuff = null;
        final String methodName = "addActivationBonusSet";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_profileSetVO= ");
        	loggerValue.append(p_profileSetVO.toString());
			loggerValue.append(" p_requestType=");
        	loggerValue.append(p_requestType);
            LOG.debug(methodName,loggerValue);
        }
        String insertUpdateQuery = null;
        try {

            // if request is for add activation profile
            if ("addactprofile".equals(p_requestType)) {
                strBuff = new StringBuilder();
                strBuff.append("INSERT INTO profile_set (profile_type,");
                strBuff.append(" set_id,set_name,last_version,status,");
                strBuff.append(" created_on,created_by,modified_on,modified_by,short_code,network_code ) ");
                strBuff.append(" values ");
                strBuff.append(" (?,?,?,?,?,?,?,?,?,?,?)");
                insertUpdateQuery = strBuff.toString();
            }
            // when request is for modify act profile
            else if ("modifyactprofile".equals(p_requestType)) {

                strBuff = new StringBuilder();
                strBuff.append("UPDATE profile_set ");
                strBuff.append(" SET last_version=?,modified_on=?,modified_by=?");
                strBuff.append(" WHERE set_id=? ");
                insertUpdateQuery = strBuff.toString();
            }
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(insertUpdateQuery);
                LOG.debug(methodName,loggerValue);
            }

            // commented for DB2psmtInsertUpdate =
           
            psmtInsertUpdate = (PreparedStatement) p_con.prepareStatement(insertUpdateQuery);
            if ("addactprofile".equals(p_requestType)) {
                psmtInsertUpdate.setString(1, p_profileSetVO.getProfileType().toUpperCase());
                psmtInsertUpdate.setString(2, p_profileSetVO.getSetId());
                // commented for DB2psmtInsertUpdate.setFormOfUse(3,
                
                psmtInsertUpdate.setString(3, p_profileSetVO.getSetName());
                psmtInsertUpdate.setString(4, p_profileSetVO.getLastVersion());
                psmtInsertUpdate.setString(5, p_profileSetVO.getStatus());
                psmtInsertUpdate.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(p_profileSetVO.getCreatedOn()));
                psmtInsertUpdate.setString(7, p_profileSetVO.getCreatedBy());
                psmtInsertUpdate.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(p_profileSetVO.getModifiedOn()));
                psmtInsertUpdate.setString(9, p_profileSetVO.getModifiedBy());
                psmtInsertUpdate.setString(10, p_profileSetVO.getShortCode().toUpperCase());
                psmtInsertUpdate.setString(11, p_profileSetVO.getNetworkCode());
                insertCount = psmtInsertUpdate.executeUpdate();
            } else if ("modifyactprofile".equals(p_requestType)) {
                psmtInsertUpdate.setString(1, p_profileSetVO.getLastVersion());
                psmtInsertUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_profileSetVO.getModifiedOn()));
                psmtInsertUpdate.setString(3, p_profileSetVO.getModifiedBy());
                psmtInsertUpdate.setString(4, p_profileSetVO.getSetId());
                insertCount = psmtInsertUpdate.executeUpdate();
            }
        } // end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addActivationBonusSet]", "", "",
                "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addActivationBonusSet]", "", "",
                "", logVal1);
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
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                LOG.debug(methodName,loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * rahul.dutt
     * method for inserting profile set version
     * 
     * @param p_con
     * @param p_profileSetVersionVO
     * @return int
     * @throws BTSLBaseException
     */
    public int addActivationBonusVersion(Connection p_con, ProfileSetVersionVO p_profileSetVersionVO) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        final String methodName = "addActivationBonusVersion";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_profileSetVersionVO= ");
        	loggerValue.append(p_profileSetVersionVO.toString());
            LOG.debug(methodName,loggerValue);
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
            strBuff.append(" status,created_on,created_by,modified_on,modified_by,applicable_to)");
            strBuff.append(" values (?,?,?,?,?,?,?,?,?");
            if (p_profileSetVersionVO.getOneTimeBonus() > 0) {
                strBuff.append(" ,?");
            }
            if (p_profileSetVersionVO.getBonusDuration() > 0) {
                strBuff.append(" ,?");
            }
            strBuff.append(" )");
            final String insertQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Query sqlInsert:" + insertQuery);
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
            psmtInsert.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(BTSLUtil.addDaysInUtilDate(p_profileSetVersionVO.getApplicableFrom(),365)));
            insertCount = psmtInsert.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException: " );
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addActivationBonusVersion]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addActivationBonusVersion]", "",
                "", "", logVal1);
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
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                LOG.debug(methodName,loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * rahul.dutt
     * this method checks whether activation profile short code already exists
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_shortCode
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isActivationProfileShortCodeExist(Connection p_con, String p_networkCode, String p_shortCode) throws BTSLBaseException {
        final String methodName = "isActivationProfileShortCodeExist";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_shortCode=");
        	loggerValue.append(p_shortCode);
            LOG.debug(methodName,loggerValue);
        }
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existFlag = false;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT short_code FROM profile_set ");
        strBuff.append("WHERE network_code = ? AND upper(short_code) = upper(?)");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(sqlSelect);
            LOG.debug("isCommissionProfileShortCodeExist",loggerValue);
        }
        try {
            // commented for DB2pstmt =
            
            pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_networkCode);
            // commented for DB2pstmt.setFormOfUse(2,
            
            pstmt.setString(2, p_shortCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception : ");
        	loggerValue.append(sqe.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusDAO[isActivationProfileShortCodeExist]", "", "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusDAO[isActivationProfileShortCodeExist]", "", "", "", logVal1);
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: existFlag=" );
            	loggerValue.append(existFlag);
                LOG.debug(methodName,loggerValue);
            }
        }
    }

    /**
     * rahul.dutt
     * addActivationBonusSetDetail
     * adds the profile details into profile_details table
     * 
     * @param p_con
     * @param p_detailVOList
     * @param profileSetVO
     * @param p_updateVersion
     * @return int
     * @throws BTSLBaseException
     */
    public int addActivationBonusSetDetail(Connection p_con, ArrayList p_detailVOList, ProfileSetVO profileSetVO, boolean p_updateVersion) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        final String methodName = "addActivationBonusSetDetail";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Inserted p_detailVOList Size= ");
        	loggerValue.append(p_detailVOList.size());
        	loggerValue.append(" p_updateVersion=");
        	loggerValue.append(p_updateVersion);
            LOG.debug(methodName,loggerValue);
        }

        try {
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("INSERT INTO profile_details (set_id,");
            strBuff.append("version,detail_id,type,user_type,detail_type,detail_subtype,period_id,service_code,");
            strBuff.append("start_range,end_range,points_type,points,min_limit,max_limit,subscriber_type,product_code) ");
            strBuff.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String insertQuery = strBuff.toString();

            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(insertQuery);
                LOG.debug(methodName,loggerValue);
            }

            psmtInsert = p_con.prepareStatement(insertQuery);
            ProfileSetDetailsVO detailVO = null;
            for (int i = 0, j = p_detailVOList.size(); i < j; i++) {

                detailVO = (ProfileSetDetailsVO) p_detailVOList.get(i);
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
                if(BTSLUtil.isNullString(detailVO.getProductCode())) {
                	psmtInsert.setString(17, PretupsI.PRODUCT_ETOPUP);
                } else {
                	psmtInsert.setString(17, detailVO.getProductCode());
                }
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
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addActivationBonusSetDetail]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addActivationBonusSetDetail]", "",
                "", "", logVal1);
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
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                LOG.debug(methodName,loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * This method loads the list of profileSetVO from the profile_set table
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_networkCode
     * @param ArrayList
     *            p_profileList
     * @return ArrayList
     * @throws BTSLBaseException
     * @author amit.singh
     */
    public ArrayList loadProfileList(Connection p_con, String p_networkCode, ArrayList p_profileList) throws BTSLBaseException {
        final String methodName = "loadProfileList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_profileList size=");
        	loggerValue.append(p_profileList.size());
            LOG.debug(methodName,loggerValue);
        }
        PreparedStatement pstmt = null;
        ProfileSetVO profileSetVO = null;
        ResultSet rs = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT ps.profile_type,ps.set_id,ps.set_name,ps.last_version,ps.short_code ");
            sbf.append("FROM profile_set ps ");
            sbf.append("WHERE ps.network_code=? AND ps.status<>'N' AND ps.profile_type=? ORDER BY ps.set_name");
            final String selectQuery = sbf.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("SQL Query :" );
            	loggerValue.append(selectQuery);
                LOG.debug("loadProfileName",loggerValue);
            }
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, PretupsI.ACT_PROF_TYPE);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                profileSetVO = makeProfileSetObject();
                profileSetVO.setProfileType(rs.getString("profile_type"));
                profileSetVO.setSetId(rs.getString("set_id"));
                profileSetVO.setSetName(rs.getString("set_name"));
                profileSetVO.setLastVersion(rs.getString("last_version"));
                profileSetVO.setShortCode(rs.getString("short_code"));
                p_profileList.add(profileSetVO);
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadProfileList]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName, logVal1);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadProfileList]", "", "", "",
            		logVal1);
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
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting p_profileList size=");
            	loggerValue.append(p_profileList.size());
                LOG.debug(methodName,loggerValue);
            }
        }
        return p_profileList;
    }

	private ProfileSetVO makeProfileSetObject() {
		ProfileSetVO profileSetVO;
		profileSetVO = new ProfileSetVO();
		return profileSetVO;
	}

    /**
     * rajdeep.deb
     * modified rahul.dutt
     * This method is used to return list of versions for all profile.
     * and returns latest version if we give profile set id as input
     * 
     * @param p_con
     * @param p_profileSetId
     * @return ArrayList
     * @throws BTSLBaseException
     * @throws SQLException
     * @throws Exception
     */
    public ArrayList loadVersions(Connection p_con, String profileSetId) throws BTSLBaseException {
        final String methodName = "loadVersions";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered +");
        	loggerValue.append(profileSetId);
            LOG.debug(methodName, loggerValue);
        }
        ArrayList list = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ProfileSetVersionVO versionVO = null;
        try {
            list = new ArrayList();
            ProfileQry profileQry = (ProfileQry)ObjectProducer.getObject(QueryConstants.PROFILE_QRY, QueryConstants.QUERY_PRODUCER);
            String sbf = profileQry.loadVersionsQry(profileSetId);
            final String selectQuery = sbf;
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("SQL query");
            	loggerValue.append(selectQuery);
                LOG.debug(methodName,loggerValue);
            }
            pstmt = p_con.prepareStatement(selectQuery);
            int i = 0;
            if (profileSetId != null) {
                pstmt.setString(++i, profileSetId);
            }
            pstmt.setString(++i, PretupsI.USER_STATUS_DELETED);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                versionVO = new ProfileSetVersionVO();
                versionVO.setSetId(rs.getString("set_id"));
                versionVO.setVersion(rs.getString("version"));
                versionVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                versionVO.setBonusDuration(rs.getLong("bonus_duration"));
                list.add(versionVO);
            }

        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadVersions]", "", "", "",
            		logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error("loadProfileList",logVal1);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadVersions]", "", "", "",
            		logVal1);
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

    /**
     * rajdeep.deb
     * this method deletes the version or profile dependin on delete flag
     * modified by rahul
     * 
     * @param p_con
     * @param p_setID
     * @param p_version
     * @param p_userVO
     * @param p_setDel
     * @param p_versionDel
     * @return
     * @throws BTSLBaseException
     */
    public boolean deleteProfileSetVersion(Connection p_con, String p_setID, String p_version, UserVO p_userVO, boolean p_setDel, boolean p_versionDel) throws BTSLBaseException {
        final String methodName = "deleteProfileSetVersion";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered with p_setID=");
        	loggerValue.append(p_setID);
        	loggerValue.append(" p_version=");
        	loggerValue.append(p_version);
        	loggerValue.append(" p_setDel=");
        	loggerValue.append(p_setDel);
        	loggerValue.append(" p_versionDel=");
        	loggerValue.append(p_versionDel);
        	loggerValue.append(" p_userVO=");
        	loggerValue.append(p_userVO);
            LOG.debug(methodName,loggerValue);
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
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("SQL Query :");
                	loggerValue.append(updateSetTable);
                    LOG.debug("deleteProfile",loggerValue);
                }
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version SET ");
                sbf.append("status='N',modified_by=?,modified_on=? ");
                sbf.append("WHERE set_id=?");
                final String updateVersionTable = sbf.toString();
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("SQL Query :");
                	loggerValue.append(updateVersionTable);
                    LOG.debug("deleteProfile",loggerValue);
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
                if (LOG.isDebugEnabled()) {
                	loggerValue.setLength(0);
                	loggerValue.append("SQL Query :");
                	loggerValue.append(updateSetTable);
                    LOG.debug("deleteProfile",loggerValue);
                }
                sbf = new StringBuilder();
                sbf.append("UPDATE profile_set_version SET ");
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
        	loggerValue.append("SQLException: ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName, logVal1);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[deleteProfileSetVersion]", "", "",
                "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[deleteProfileSetVersion]", "", "",
                "", logVal1);
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
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting flag=");
            	loggerValue.append(flag);
                LOG.debug(methodName,loggerValue);
            }
        }
        return flag;
    }

    /**
     * @author rajdeep.deb
     *         this method checks whther a profile is mapped with a category
     * @param p_con
     * @param String
     *            p_setID
     * @return boolean
     */
    public boolean isprofileCategoryMapp(Connection p_con, String p_setID) throws BTSLBaseException {
        final String methodName = "isprofileCategoryMapp";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        boolean i = true;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder loggerValue= new StringBuilder();
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT pm.srv_class_or_category_code,pm.set_id ");
            sbf.append("FROM profile_mapping pm ");
            sbf.append("WHERE pm.set_id=?");
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Sql query = ");
            	loggerValue.append(sbf.toString());
                LOG.debug(methodName,loggerValue);
            }
            final String selectQuery = sbf.toString();
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_setID);
            rs = pstmt.executeQuery();
            if (!rs.next()) {
                i = false;
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName, logVal1);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileCategoryMapp]", "", "",
                "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileCategoryMapp]", "", "",
                "", logVal1);
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
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting i=");
            	loggerValue.append(i);
                LOG.debug(methodName,loggerValue);
            }
        }
        return i;
    }

    /**
     * This method first delete the records for the available category from
     * profile_mapping table and
     * then inserts the new records for the available category in
     * profile_mapping table.
     * 
     * @param Connection
     *            p_con
     * @param Arraylist
     *            p_profileMappingVOList
     * @param String
     *            [] p_str
     * @param String
     *            p_networkCode
     * @return int
     * @throws BTSLBaseException
     * @author amit.singh
     */

    public int addCategoryProfileMapping(Connection p_con, ArrayList p_profileMappingVOList, String[] p_str, String p_networkCode) throws BTSLBaseException {
    	PreparedStatement psmtInsert = null;
    	StringBuilder loggerValue= new StringBuilder();
        
        int insertCount = 0;
        final String methodName = "addCategoryProfileMapping";
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: Inserted p_profileMappingVOList Size= ");
        	loggerValue.append(p_profileMappingVOList.size());
        	loggerValue.append("p_str length=");
        	loggerValue.append(p_str.length);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
			String logVal1=loggerValue.toString();
            LOG.debug(methodName,loggerValue);
        }

        final StringBuilder delBuff = new StringBuilder();

        delBuff.append("DELETE FROM PROFILE_MAPPING");
        delBuff.append(" WHERE SRV_CLASS_OR_CATEGORY_CODE=? AND PROFILE_TYPE=? AND NETWORK_CODE=?");

        final String deleteQuery = delBuff.toString();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Query sqlDelete:");
        	loggerValue.append(deleteQuery);
            LOG.debug(methodName,loggerValue);
        }

        try(PreparedStatement psmtDelete = p_con.prepareStatement(deleteQuery);) {
            

            for (int k = 0; k < p_str.length; k++) {
                psmtDelete.setString(1, p_str[k]);
                psmtDelete.setString(2, PretupsI.PROFILE_TYPE_ACTIVATION);
                psmtDelete.setString(3, p_networkCode);
                psmtDelete.executeUpdate();
                psmtDelete.clearParameters();
            }

        } // end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addCategoryProfileMapping]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        finally {
            LOG.debug(methodName, "Exiting..");
        } // end of finally

        final StringBuilder strBuff = new StringBuilder();

        strBuff.append("INSERT INTO PROFILE_MAPPING ( SRV_CLASS_OR_CATEGORY_CODE, PROFILE_TYPE, SET_ID, IS_DEFAULT,");
        strBuff.append("CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NETWORK_CODE )");
        strBuff.append("VALUES (?,?,?,?,?,?,?,?,?)");

        final String insertQuery = strBuff.toString();

        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Query sqlInsert:");
        	loggerValue.append(insertQuery);
            LOG.debug(methodName,loggerValue);
        }
        try {
            psmtInsert = p_con.prepareStatement(insertQuery);
            ProfileMappingVO detailVO = null;

            for (int i = 0, j = p_profileMappingVOList.size(); i < j; i++) {
                detailVO = (ProfileMappingVO) p_profileMappingVOList.get(i);
                if (!"0".equalsIgnoreCase(detailVO.getSetID())) {
                    psmtInsert.setString(1, detailVO.getCategoryCode());
                    psmtInsert.setString(2, detailVO.getProfileType());
                    psmtInsert.setString(3, detailVO.getSetID());
                    psmtInsert.setString(4, detailVO.getDefaultProfile());
                    psmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(detailVO.getCreatedOn()));
                    psmtInsert.setString(6, detailVO.getCreatedBy());
                    psmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(detailVO.getModifiedOn()));
                    psmtInsert.setString(8, detailVO.getModifiedBy());
                    psmtInsert.setString(9, detailVO.getNetworkCode());

                    insertCount = psmtInsert.executeUpdate();

                    psmtInsert.clearParameters();
                }
            }
        } // end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addCategoryProfileMapping]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[addCategoryProfileMapping]", "",
                "", "", logVal1);
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
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                LOG.debug(methodName,loggerValue);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * added by rahul
     * to load profile details of particular version for modify on the basis of
     * profile set and version
     * 
     * @param p_con
     * @param p_profileSetID
     * @param p_version
     * @return ActivationProfileCombinedVO
     * @throws BTSLBaseException
     */
    public ActivationProfileCombinedVO loadProfileVersionDetails(Connection p_con, String p_profileSetID, String p_version, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadProfileVersionDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_profileSetID");
        	loggerValue.append(p_profileSetID);
        	loggerValue.append("p_version");
        	loggerValue.append(p_version);
        	loggerValue.append("p_networkCode");
        	loggerValue.append(p_networkCode);
            LOG.debug(methodName,loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ActivationProfileCombinedVO profileDetailsCombinedVO = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append("SELECT  ps.set_id,ps.set_name,ps.short_code,psv.applicable_from,psv.bonus_duration,psv.one_time_bonus, ");
            sbf.append(" ps.last_version");
            sbf.append(" FROM PROFILE_SET ps,PROFILE_SET_VERSION psv ");
            sbf.append(" WHERE psv.set_id=? AND psv.VERSION=? AND psv.set_id=ps.set_id AND ps.network_code=?");
            final String selectQuery = sbf.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("selectQuery =");
            	loggerValue.append(selectQuery);
                LOG.debug(methodName,loggerValue);
            }
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, p_profileSetID);
            pstmt.setString(2, p_version);
            pstmt.setString(3, p_networkCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                profileDetailsCombinedVO = new ActivationProfileCombinedVO();
                profileDetailsCombinedVO.setSetName(rs.getString("set_name"));
                profileDetailsCombinedVO.setShortCode(rs.getString("short_code"));
                profileDetailsCombinedVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                if (rs.getLong("bonus_duration") > 0) {
                    profileDetailsCombinedVO.setBonusDuration(Long.parseLong(rs.getString("bonus_duration")));
                }
                if (rs.getLong("one_time_bonus") > 0) {
                    profileDetailsCombinedVO.setOneTimeBonus(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("one_time_bonus"))));
                }
                profileDetailsCombinedVO.setLastVersion(Integer.parseInt(rs.getString("last_version")));
                profileDetailsCombinedVO.setSetId(rs.getString("set_id"));
            }
        } catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQL Exception: ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadProfileVersionDetails]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadProfileVersionDetails]", "",
                "", "", logVal1);
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

    /**
     * rahul.dutt
     * to load profile details on basis of servicecode,profle set and version
     * 
     * @param p_con
     * @param p_actProfileServiceTypeID
     * @param p_actProifleSetId
     * @param p_actProfileSetVersion
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadActivationProfileDetailList(Connection p_con, String p_actProfileServiceTypeID, String p_actProifleSetId, String p_actProfileSetVersion) throws BTSLBaseException {
        final String methodName = "loadActivationProfileDetailList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_actProfileServiceTypeID=");
        	loggerValue.append(p_actProfileServiceTypeID);
        	loggerValue.append("p_actProifleSetId");
        	loggerValue.append(p_actProifleSetId);
        	loggerValue.append("p_actProfileSetVersion");
        	loggerValue.append(p_actProfileSetVersion);
            LOG.debug(methodName,loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList addActivationDetailList = new ArrayList();
        ProfileSetDetailsVO activationProfileDeatilsVO = null;
        try {

            final StringBuilder selectQueryBuff = new StringBuilder(
                "SELECT pd.start_range,pd.end_range,pd.points_type,pd.period_id,pd.points,pd.detail_type,pd.detail_subtype,pd.subscriber_type, ");
            selectQueryBuff.append(" pd.type,pd.user_type,pd.service_code,pd.min_limit,pd.max_limit,pd.subscriber_type,pd.version,pd.product_code ");
            selectQueryBuff.append(" FROM PROFILE_DETAILS pd ");
            selectQueryBuff.append(" WHERE pd.set_id=? AND pd.VERSION=? AND service_code=?");
            final String selectQuery = selectQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("select query:");
            	loggerValue.append(selectQuery);
                LOG.debug(methodName,loggerValue);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_actProifleSetId);
            pstmtSelect.setString(2, p_actProfileSetVersion);
            pstmtSelect.setString(3, p_actProfileServiceTypeID);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                activationProfileDeatilsVO = new ProfileSetDetailsVO();
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
                activationProfileDeatilsVO.setProductCode(rs.getString("product_code"));
                addActivationDetailList.add(activationProfileDeatilsVO);
            }

            return addActivationDetailList;
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException ");
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadActivationProfileDetailList]",
                "", "", "", logVal1);
            throw new BTSLBaseException("ActivationBonusDAO", methodName, PretupsErrorCodesI.ACT_BONUS_EXCEPTION);
        }// end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append( e.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[loadActivationProfileDetailList]",
                "", "", "", logVal1);
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
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting addActivationDetailList Size:");
            	loggerValue.append(addActivationDetailList.size());
                LOG.debug(methodName,loggerValue);
            }
        }// end of finally
    }

    /**
     * rahul.dutt
     * method is used for updating modified on,by status of a profile version
     * and delete profile details
     * 
     * @param p_con
     * @param version
     * @param p_user_id
     * @param p_setid
     * @param p_currentDate
     * @return int
     * @throws BTSLBaseException
     */
    public int updateProfileVersionDetail(Connection p_con, String version, String p_user_id, String p_setid, Date p_currentDate, ProfileSetVersionVO p_profileSetVersionVO) throws BTSLBaseException {
        
        PreparedStatement psmtInsertUpdate = null;
        PreparedStatement psmtDelete = null;
        int insertCount = 0;
        StringBuilder strBuff = null;
        final String methodName = "updateProfileVersionDetail";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: version= ");
        	loggerValue.append(version);
        	loggerValue.append("p_user_id");
        	loggerValue.append(p_user_id);
        	loggerValue.append("p_setid");
        	loggerValue.append(p_setid);
        	loggerValue.append("p_profileSetVersionVO");
        	loggerValue.append(p_profileSetVersionVO.toString());
            LOG.debug(methodName,loggerValue);
        }
        String insertUpdateQuery = null;
        try {

            strBuff = new StringBuilder();
            strBuff.append("UPDATE profile_set_version ");
            strBuff.append(" SET modified_on=?,modified_by=? ");
            strBuff.append(",one_time_bonus=? ");
            strBuff.append(",bonus_duration=? ");
            //strBuff.append(",product_code=? ");
            strBuff.append(" WHERE set_id=? AND version=? ");
            insertUpdateQuery = strBuff.toString();
            strBuff = new StringBuilder();
            strBuff.append("Delete from profile_details ");
            strBuff.append("where set_id=? AND version=? ");
            final String deleteQuery = strBuff.toString();
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query sqlInsert:");
            	loggerValue.append(insertUpdateQuery);
                LOG.debug(methodName,loggerValue);
            }

            // commented for DB2psmtInsertUpdate =
            
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
            //psmtInsertUpdate.setString(++i, p_profileSetVersionVO.getProductCode());
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
        	loggerValue.append("SQL Exception: " );
        	loggerValue.append(sqle.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[updateProfileVersionDetail]", "",
                "", "", logVal1);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception: ");
        	loggerValue.append(e.getMessage());
			String logVal1=loggerValue.toString();
            LOG.error(methodName,logVal1);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[updateProfileVersionDetail]", "",
                "", "", logVal1);
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
            if (LOG.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting: insertCount=");
            	loggerValue.append(insertCount);
                LOG.debug(methodName,loggerValue);
            }
        } // end of finally

        return insertCount;
    }
}
