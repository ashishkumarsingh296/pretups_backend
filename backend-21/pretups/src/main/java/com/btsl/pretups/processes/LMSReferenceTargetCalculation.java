package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
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
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.processes.businesslogic.ProgressiveMessageVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;

public class LMSReferenceTargetCalculation {

    private static Log _logger = LogFactory.getLog(LMSReferenceTargetCalculation.class.getName());
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO = null;

    public static void main(String[] args) {
        // String file =null;
        final String methodName = "main";
        try {
            if (args.length != 2) {
                _logger.info(methodName, "Usage : LMSReferenceTargetCalculation [Constants file] [LogConfig file] [Upload File Path]");
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                _logger.info(methodName, " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                _logger.info(methodName, " Logconfig File Not Found .............");
                return;
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            LookupsCache.loadLookAtStartup();
        }// end try
        catch (Exception ex) {
            _logger.errorTrace(methodName, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            final LMSReferenceTargetCalculation lMSReferenceTargetCalculation = new LMSReferenceTargetCalculation();
            lMSReferenceTargetCalculation.process();
        } catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(methodName, be);
        } catch (Exception e) {
            _logger.error(methodName, "Exception : " + e.getMessage());
            _logger.errorTrace(methodName, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info(methodName, " Exiting");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    public void process() throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (_logger.isDebugEnabled()) {
            _logger.debug("LMSReferenceTargetCalculation", " Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        boolean statusOk = false;
        Date processedUpto = null;
        final Date currentDate = new Date();
        ArrayList refBasedProfileList = null;
        ArrayList aasociateUsersList = null;
        ProgressiveMessageVO processTargetVO = null;
        ArrayList usersTranAmtList = null;
        int targetUpdated = 0;
        try {
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, ProcessI.LMSREFTCAL);
            statusOk = _processStatusVO.isStatusOkBool();
            // check process status.
            if (statusOk) {
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    processedUpto = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(processedUpto));
                    final int diffDate = BTSLUtil.getDifferenceInUtilDates(processedUpto, currentDate);
                    if (diffDate <= 1) {
                        _logger.error("LMSReferenceTargetCalculation", " LMSReferenceTargetCalculation Process has been already executed.....");
                        throw new BTSLBaseException("LMSReferenceTargetCalculation", "process", PretupsErrorCodesI.LOYALTY_POINTS_UPLOAD_PROCESS_ALREADY_EXECUTED);
                    }
                }
            } else {
                throw new BTSLBaseException("LMSReferenceTargetCalculation", "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }

            // Find profile details for those reference target need to be
            // calculated
            refBasedProfileList = loadReferenceBasedProfileDetails(con);

            if (refBasedProfileList.size() > 0) {
                for (int i = 0; i < refBasedProfileList.size(); i++) {

                    processTargetVO = (ProgressiveMessageVO) refBasedProfileList.get(i);
                    aasociateUsersList = loadChannelUsersForRefBasedTarget(con, processTargetVO.getSetId(), PretupsI.USER_STATUS_ACTIVE);
                    if (aasociateUsersList.size() > 0) {
                        processTargetVO.setTargetUserList(aasociateUsersList);
                        usersTranAmtList = calculateUsersTransactionAmount(con, processTargetVO.getTargetUserList(), processTargetVO.getRefFrom(), processTargetVO.getRefTo(),
                            processTargetVO.getType());
                        if (usersTranAmtList.size() > 0) {
                            processTargetVO.setUserTransactionAmountList(usersTranAmtList);
                        }
                    }
                }

                for (int i = 0; i < refBasedProfileList.size(); i++) {
                    processTargetVO = (ProgressiveMessageVO) refBasedProfileList.get(i);
                    targetUpdated = updateChannelUsersForRefBasedTarget(con, processTargetVO.getUserTransactionAmountList(), processTargetVO.getSetId(), processTargetVO
                        .getEndRange());
                    if (targetUpdated > 0) {
                        con.commit();
                    } else {
                        con.rollback();
                    }
                }
            } else {
                _logger.error("LMSReferenceTargetCalculation", " LMSReferenceTargetCalculation No profile is selected to calculate reference target values.....");
            }
        } catch (BTSLBaseException ex) {
            _logger.errorTrace(METHOD_NAME, ex);
        } catch (SQLException ex) {
            _logger.errorTrace(METHOD_NAME, ex);
        } catch (ParseException ex) {
            _logger.errorTrace(METHOD_NAME, ex);
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
        } finally {
            try {
                if (statusOk) {
                    if (markProcessStatusAsComplete(con, ProcessI.LMSREFTCAL) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    }
                }
				if (mcomCon != null) {
					mcomCon.close("LMSReferenceTargetCalculation#process");
					mcomCon = null;
				}
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
        }
    }

    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) {
        final String METHOD_NAME = "markProcessStatusAsComplete";
        if (_logger.isDebugEnabled()) {
            _logger.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        _processStatusVO.setExecutedOn(currentDate);
        _processStatusVO.setExecutedUpto(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            }
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    public ArrayList loadReferenceBasedProfileDetails(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadReferenceBasedProfileDetails";
        if (_logger.isDebugEnabled()) {
            _logger.debug("loadReferenceBasedProfileDetails", " Entered");
        }
        ArrayList profileList = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final Date currentDate = new Date();
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append(" SELECT PS.PROFILE_TYPE,PS.SET_ID,PS.SET_NAME,PS.LAST_VERSION,PS.PROMOTION_TYPE, ");
            strBuff.append(" PS.NETWORK_CODE,PS.REF_BASED_ALLOWED,PD.TYPE,PD.DETAIL_TYPE,PD.DETAIL_SUBTYPE, ");
            strBuff.append(" PD.END_RANGE,PSV.REFERENCE_FROM,PSV.REFERENCE_TO,PM.MODIFIED_ON ");
            strBuff.append(" FROM PROFILE_SET PS, PROFILE_SET_VERSION PSV, PROFILE_DETAILS PD,PROFILE_MAPPING PM ");
            strBuff.append(" WHERE PS.SET_ID = PSV.SET_ID AND PS.SET_ID = PD.SET_ID AND PS.SET_ID = PM.SET_ID ");
            strBuff.append(" AND PSV.SET_ID =PD.SET_ID AND PSV.SET_ID=PM.SET_ID  ");
            strBuff.append(" AND PS.REF_BASED_ALLOWED='Y' AND PS.PROMOTION_TYPE=? ");
            strBuff.append(" AND PD.DETAIL_TYPE=? AND PD.DETAIL_SUBTYPE=? ");
            strBuff.append(" AND PM.MODIFIED_ON=? ");
            final String sqlSelect = strBuff.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadReferenceBasedProfileDetails", "QUERY sqlSelect=" + sqlSelect);
            }

            profileList = new ArrayList();
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.LMS_PROMOTION_TYPE_STOCK);
            pstmt.setString(2, PretupsI.PROFILE_VOL);
            pstmt.setString(3, PretupsI.USER_SUB_TYPE_AMOUNT);
            pstmt.setDate(4, BTSLUtil.getSQLDateFromUtilDate(currentDate));
            rs = pstmt.executeQuery();
            ProgressiveMessageVO progressiveMessageVO = null;
            while (rs.next()) {
                progressiveMessageVO = new ProgressiveMessageVO();
                progressiveMessageVO.setProfileType(rs.getString("PROFILE_TYPE"));
                progressiveMessageVO.setSetId(rs.getString("SET_ID"));
                progressiveMessageVO.setSetName(rs.getString("SET_NAME"));
                progressiveMessageVO.setLastVersion(rs.getString("LAST_VERSION"));
                progressiveMessageVO.setPromotionType(rs.getString("PROMOTION_TYPE"));
                progressiveMessageVO.setNetworkCode(rs.getString("NETWORK_CODE"));
                progressiveMessageVO.setRefBasedAllowed(rs.getString("REF_BASED_ALLOWED"));
                progressiveMessageVO.setType(rs.getString("TYPE"));
                progressiveMessageVO.setDetailType(rs.getString("DETAIL_TYPE"));
                progressiveMessageVO.setDetailSubType(rs.getString("DETAIL_SUBTYPE"));
                progressiveMessageVO.setEndRange(String.valueOf(rs.getInt("END_RANGE")));
                progressiveMessageVO.setRefFrom(rs.getDate("REFERENCE_FROM"));
                progressiveMessageVO.setRefTo(rs.getDate("REFERENCE_TO"));
                progressiveMessageVO.setModifiedOn(rs.getDate("MODIFIED_ON"));
                profileList.add(progressiveMessageVO);

            }
        } catch (SQLException sqe) {
            _logger.error("loadReferenceBasedProfileDetails", "SQLException : " + sqe);
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "LMSReferenceTargetCalculation[loadReferenceBasedProfileDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadReferenceBasedProfileDetails", "error.general.sql.processing");
        } catch (Exception ex) {
            _logger.error("loadCategoryProfileMappingListByDomainCode", "Exception : " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "LMSReferenceTargetCalculation[loadReferenceBasedProfileDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadReferenceBasedProfileDetails", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadReferenceBasedProfileDetails", " Exit profileList size= " + profileList.size());
            }
        }
        return profileList;
    }

    public ArrayList loadChannelUsersForRefBasedTarget(Connection p_con, String p_profileSetID, String p_refBasedStatus) throws BTSLBaseException {
        final String METHOD_NAME = "loadChannelUsersForRefBasedTarget";
        if (_logger.isDebugEnabled()) {
            _logger.debug("loadChannelUsersForRefBasedTarget", " Entered p_profileSetID= " + p_profileSetID + "p_refBasedStatus= " + p_refBasedStatus);
        }
        ArrayList userlist = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ListValueVO listVO = null;
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append(" SELECT CH.USER_ID, CH.LMS_PROFILE, CH.REF_BASED, CH.LMS_TARGET  ");
            strBuff.append(" FROM CHANNEL_USERS CH WHERE CH.LMS_PROFILE=? ");
            final String sqlSelect = strBuff.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadChannelUsersForRefBasedTarget", "QUERY sqlSelect=" + sqlSelect);
            }

            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_profileSetID);
            rs = pstmt.executeQuery();
            userlist = new ArrayList();
            while (rs.next()) {
                listVO = new ListValueVO(rs.getString("USER_ID"), rs.getString("LMS_PROFILE"));
                userlist.add(listVO);
            }
        } catch (SQLException sqe) {
            _logger.error("loadChannelUsersForRefBasedTarget", "SQLException : " + sqe);
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "LMSReferenceTargetCalculation[loadChannelUsersForRefBasedTarget]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadChannelUsersForRefBasedTarget", "error.general.sql.processing");
        } catch (Exception ex) {
            _logger.error("loadChannelUsersForRefBasedTarget", "Exception : " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "LMSReferenceTargetCalculation[loadChannelUsersForRefBasedTarget]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadCategoryProfileMappingListByDomainCode", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadChannelUsersForRefBasedTarget", " Exit UsersList size= " + userlist.size());
            }
        }
        return userlist;
    }

    public ArrayList calculateUsersTransactionAmount(Connection p_con, ArrayList p_userDetailList, Date p_refFrom, Date p_refTo, String p_module) throws BTSLBaseException {
        final String METHOD_NAME = "calculateUsersTransactionAmount";
        if (_logger.isDebugEnabled()) {
            _logger.debug("calculateUsersTransactionAmount",
                " Entered p_userDetailList= " + p_userDetailList.size() + "p_refFrom= " + p_refFrom + " p_refFrom= " + p_refFrom + " p_module= " + p_module);
        }
        ArrayList userAmountlist = null;
        PreparedStatement psto2c = null;
        ResultSet rso2c = null;
        PreparedStatement pstc2c = null;
        ResultSet rsc2c = null;
        PreparedStatement pstc2s = null;
        ResultSet rsc2s = null;
        ListValueVO listVO = null;
        String user_id = null;
        ListValueVO listVOUpdated = null;
        try {

            final StringBuffer strBuff1 = new StringBuffer();
            strBuff1.append(" select sum(O2C_TRANSFER_IN_AMOUNT) from DAILY_CHNL_TRANS_MAIN  ");
            strBuff1.append(" where user_id= ? and TRANS_DATE >= ? and TRANS_DATE <=?  ");
            final String selecto2c = strBuff1.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("calculateUsersTransactionAmount", "QUERY selecto2c=" + selecto2c);
            }

            final StringBuffer strBuff2 = new StringBuffer();
            strBuff2.append(" select sum(C2C_TRANSFER_OUT_AMOUNT) from DAILY_CHNL_TRANS_MAIN ");
            strBuff2.append(" where user_id= ? and TRANS_DATE >= ? and TRANS_DATE <=?  ");
            final String selectc2c = strBuff2.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("calculateUsersTransactionAmount", "QUERY selectc2c=" + selectc2c);
            }

            final StringBuffer strBuff3 = new StringBuffer();
            strBuff3.append(" select sum(SENDER_TRANSFER_AMOUNT) from DAILY_C2S_TRANS_DETAILS ");
            strBuff3.append(" where user_id= ? and TRANS_DATE >= ? and TRANS_DATE <=?  ");
            final String selectc2s = strBuff3.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("calculateUsersTransactionAmount", "QUERY selectc2s=" + selectc2s);
            }
            userAmountlist = new ArrayList();
            for (int i = 0; i < p_userDetailList.size(); i++) {
                listVO = (ListValueVO) p_userDetailList.get(i);
                user_id = listVO.getLabel();

                if (p_module.equalsIgnoreCase(PretupsI.O2C_MODULE)) {
                    psto2c = p_con.prepareStatement(selecto2c);
                    psto2c.setString(1, user_id);
                    psto2c.setDate(2, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getTimestampFromUtilDate(p_refFrom)));
                    psto2c.setDate(3, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getTimestampFromUtilDate(p_refTo)));
                    rso2c = psto2c.executeQuery();
                    if (rso2c.next()) {
                        listVOUpdated = new ListValueVO(user_id, String.valueOf(rso2c.getLong(1)));
                    }
                }

                if (p_module.equalsIgnoreCase(PretupsI.C2C_MODULE)) {
                    pstc2c = p_con.prepareStatement(selectc2c);
                    pstc2c.setString(1, user_id);
                    pstc2c.setDate(2, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getTimestampFromUtilDate(p_refFrom)));
                    pstc2c.setDate(3, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getTimestampFromUtilDate(p_refTo)));
                    rsc2c = pstc2c.executeQuery();
                    if (rsc2c.next()) {
                        listVOUpdated = new ListValueVO(user_id, String.valueOf(rsc2c.getLong(1)));
                    }
                }

                if (p_module.equalsIgnoreCase(PretupsI.C2S_MODULE)) {
                    pstc2s = p_con.prepareStatement(selectc2c);
                    pstc2s.setString(1, user_id);
                    pstc2s.setDate(2, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getTimestampFromUtilDate(p_refFrom)));
                    pstc2s.setDate(3, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getTimestampFromUtilDate(p_refTo)));
                    rsc2s = pstc2s.executeQuery();
                    if (rsc2s.next()) {
                        listVOUpdated = new ListValueVO(user_id, String.valueOf(rsc2s.getLong(1)));
                    }
                }
                userAmountlist.add(listVOUpdated);
            }
        } catch (SQLException sqe) {
            _logger.error("loadChannelUsersForRefBasedTarget", "SQLException : " + sqe);
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "LMSReferenceTargetCalculation[calculateUsersTransactionAmount]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "calculateUsersTransactionAmount", "error.general.sql.processing");
        } catch (Exception ex) {
            _logger.error("calculateUsersTransactionAmount", "Exception : " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "LMSReferenceTargetCalculation[calculateUsersTransactionAmount]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "calculateUsersTransactionAmount", "error.general.processing");
        } finally {
            try {
                if (rso2c != null) {
                    rso2c.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psto2c != null) {
                    psto2c.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsc2c != null) {
                    rsc2c.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstc2c != null) {
                    pstc2c.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rsc2s != null) {
                    rsc2s.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstc2s != null) {
                    pstc2s.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("calculateUsersTransactionAmount", " Exit UsersList size= " + userAmountlist.size());
            }
        }
        return userAmountlist;
    }

    public int updateChannelUsersForRefBasedTarget(Connection p_con, ArrayList p_userDetailList, String p_profileSetID, String p_currentTarget) throws BTSLBaseException {
        final String METHOD_NAME = "updateChannelUsersForRefBasedTarget";
        if (_logger.isDebugEnabled()) {
            _logger.debug("updateChannelUsersForRefBasedTarget", " Entered p_profileSetID= " + p_profileSetID + "p_currentTarget= " + p_currentTarget);
        }
        // ArrayList userlist = null;
        PreparedStatement pstmt = null;
        final ResultSet rs = null;
        ListValueVO listVO = null;
        int totalUserUpdated = 0;
        int count = 0;
        String user_id = null;
        String refBasedFinalTarget = null;

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append(" UPDATE CHANNEL_USERS SET LMS_TARGET =? WHERE USER_ID=? AND LMS_PROFILE=?  AND REF_BASED=?  ");
            final String sqlUpdate = strBuff.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("updateChannelUsersForRefBasedTarget", "QUERY sqlUpdate=" + sqlUpdate);
            }
            int userDetailListSizes=p_userDetailList.size();
            for (int i = 0; i < userDetailListSizes; i++) {
                listVO = (ListValueVO) p_userDetailList.get(i);
                user_id = listVO.getLabel();
                refBasedFinalTarget = String.valueOf(Integer.parseInt(listVO.getValue()) + Integer.parseInt(p_currentTarget));
                pstmt = p_con.prepareStatement(sqlUpdate);
                pstmt.setString(1, refBasedFinalTarget);
                pstmt.setString(2, user_id);
                pstmt.setString(3, p_profileSetID);
                pstmt.setString(4, PretupsI.STATUS_ACTIVE);
                count = pstmt.executeUpdate();
                if (count > 0) {
                    totalUserUpdated++;
                }

                if (totalUserUpdated > 2000) {
                    p_con.commit();
                }
            }
        } catch (SQLException sqe) {
            _logger.error("updateChannelUsersForRefBasedTarget", "SQLException : " + sqe);
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "LMSReferenceTargetCalculation[updateChannelUsersForRefBasedTarget]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "updateChannelUsersForRefBasedTarget", "error.general.sql.processing");
        } catch (Exception ex) {
            _logger.error("updateChannelUsersForRefBasedTarget", "Exception : " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "LMSReferenceTargetCalculation[updateChannelUsersForRefBasedTarget]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "updateChannelUsersForRefBasedTarget", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("updateChannelUsersForRefBasedTarget", " Exit total users updated= " + totalUserUpdated);
            }
        }
        return totalUserUpdated;
    }
}
