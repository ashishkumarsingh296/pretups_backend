package com.web.pretups.restrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AssociateMsisdnFileProcessLog;
import com.btsl.pretups.logging.BlackListLog;
import com.btsl.pretups.logging.RestrictedMsisdnLog;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.restapi.channelAdmin.restrictedlistmgmt.requestVO.RestrictedSubVO;
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.LoadSubscriberListForUnBlackResponseVO;

public class RestrictedSubscriberWebDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private RestrictedSubscriberWebQry restrictedSubscriberWebQry;

    public RestrictedSubscriberWebDAO() {
        restrictedSubscriberWebQry= (RestrictedSubscriberWebQry)ObjectProducer.getObject(QueryConstants.RESTRICTED_SUBSCRIBER_WEB_QRY, QueryConstants.QUERY_PRODUCER);
    }

    /**
     * Method for checking subscriber's existence under the owner user selected
     * Used in black listing the ALL subscribers. It is checked before black
     * listing all subs.
     * There is no status maintained for deletion so not check for deletion.
     *
     * @author Amit Ruwali
     * @param p_con
     *            java.sql.Connection
     * @param p_ownerID
     *            String
     * @return isSubscriberExist boolean
     * @throws BTSLBaseException
     */
    public boolean isSubscriberExist(Connection p_con, String p_ownerID) throws BTSLBaseException {
        final String methodName = "isSubscriberExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with parameters :: p_ownerID : " + p_ownerID);
        }

        PreparedStatement pstmtIsExist = null;
        ResultSet rs = null;
        boolean existFlag = false;
        try {
            final String sqlSelect = "SELECT 1 FROM restricted_msisdns WHERE owner_id=? AND restricted_type=? ";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            pstmtIsExist = p_con.prepareStatement(sqlSelect);
            pstmtIsExist.setString(1, p_ownerID);
            pstmtIsExist.setString(2, PretupsI.DEFAULT_RESTRICTED_TYPE);
            rs = pstmtIsExist.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[isSubscriberExist]", "",
                    "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[isSubscriberExist]", "",
                    "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsExist != null) {
                    pstmtIsExist.close();
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
     * Method changeBlackListStatusForAll
     * This is the common method for black & unblack listing all subscribers
     *
     * @author Amit Ruwali
     * @param p_con
     *            Connection
     * @param p_resVO
     *            RestrictedSubscriberVO
     * @return restrictedSubscriberVO RestrictedSubscriberVO
     * @throws BTSLBaseException
     */

    public int changeBlackListStatusForAll(Connection p_con, RestrictedSubscriberVO p_resVO) throws BTSLBaseException {
        final String methodName = "changeBlackListStatusForAll";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_resVO" + p_resVO);
        }
        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        int updateCount = -1;
        // addded for C2S Payee and Cp2P Payee Date 04/02/08
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" UPDATE restricted_msisdns set ");
        if (p_resVO.getBlackListStatus() != null) {
            strBuff.append("black_list_status=?, ");
        }
        if (p_resVO.getCp2pPayeeStatus() != null) {
            strBuff.append("cp2p_payee_status=?, ");
        }
        if (p_resVO.getC2sPayeeStatus() != null) {
            strBuff.append("c2s_payee_status=?, ");
        }
        strBuff.append("modified_by=?,modified_on=? WHERE owner_id=? ");
        final String sqlSelect = strBuff.toString();
        // End of C2S Payee and Cp2P Payee

        // String sqlSelect =
        // "UPDATE restricted_msisdns set black_list_status=?,modified_by=?,modified_on=? WHERE owner_id=?";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtUpdate = p_con.prepareStatement(sqlSelect);
            int i = 1;
            // addded for C2S Payee and Cp2P Payee Date 04/02/08
            if (p_resVO.getBlackListStatus() != null) {
                pstmtUpdate.setString(i++, p_resVO.getBlackListStatus());
            }
            if (p_resVO.getCp2pPayeeStatus() != null) {
                pstmtUpdate.setString(i++, p_resVO.getCp2pPayeeStatus());
            }
            if (p_resVO.getC2sPayeeStatus() != null) {
                pstmtUpdate.setString(i++, p_resVO.getC2sPayeeStatus());
            }
            // End of C2S Payee and Cp2P Payee
            pstmtUpdate.setString(i++, p_resVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_resVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_resVO.getOwnerID());
            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[changeBlackListStatusForAll]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[changeBlackListStatusForAll]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method :loadSubcriberList
     * This method is used to load the the subscribers on the basis of their
     * MSISDN and channel user ID for suspending or resuming the subscribers
     * and if not found then show preoper error message accordingly
     *
     * @author Amit Singh
     * @param p_con
     *            java.sql.Connection
     * @param p_msisdnList
     *            ArrayList
     * @param p_errorList
     *            ArrayList
     * @param p_channelUserID
     *            String
     * @param p_requestType
     *            String
     * @param p_fwdPath
     *            String
     * @return list ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadSubcriberList(Connection p_con, ArrayList p_msisdnList, ArrayList p_errorList, String p_channelUserID, String p_requestType, String p_fwdPath) throws BTSLBaseException {
        final String methodName = "loadSubcriberList";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered : with parameter p_errorList.size() :: ");
            msg.append(p_errorList.size());
            msg.append(", p_msisdnList.size() : ");
            msg.append(p_msisdnList.size());
            msg.append(", p_channelUserID : ");
            msg.append(p_channelUserID);
            msg.append(", p_requestType : ");
            msg.append(p_requestType);
            msg.append(", p_fwdPath : ");
            msg.append(p_fwdPath);

            String message=msg.toString();
            _log.debug(methodName,message);
        }

        RestrictedSubscriberVO restrictedSubscriberVO = null;
        PreparedStatement pstmtSubList = null;
        ArrayList subList = null;
        ResultSet rs = null;
        KeyArgumentVO keyArgumentVO = null;
        String status = null;
        String msisdn = null;

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("SELECT RM.msisdn, RM.employee_name, RM.employee_code,RM.owner_id,RM.min_txn_amount, RM.max_txn_amount, ");
            strBuff.append("RM.monthly_limit, RM.total_txn_count, RM.created_on, RM.status status_code, LK.lookup_name status, ");
            strBuff.append("RM.subscriber_type,RM.modified_on,RM.language,RM.country FROM restricted_msisdns RM, lookups LK ");
            strBuff.append("WHERE RM.status = LK.lookup_code AND LK.lookup_type = ? AND ");
            strBuff.append("RM.msisdn = ? AND RM.channel_user_id = ? AND RM.restricted_type = ? ");
            final String sqlSelect = strBuff.toString();
            pstmtSubList = p_con.prepareStatement(sqlSelect);

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            subList = new ArrayList();
            for (int i = 0, j = p_msisdnList.size(); i < j; i++) {
                msisdn = (String) p_msisdnList.get(i);
                keyArgumentVO = new KeyArgumentVO();
                final String[] msisdnArr = new String[1];
                msisdnArr[0] = msisdn;

                pstmtSubList.setString(1, PretupsI.LOOKUP_TYPE_RES_MSISDN_STATUS);
                pstmtSubList.setString(2, msisdn);
                pstmtSubList.setString(3, p_channelUserID);
                pstmtSubList.setString(4, PretupsI.DEFAULT_RESTRICTED_TYPE);
                rs = pstmtSubList.executeQuery();
                pstmtSubList.clearParameters();
                if (rs.next()) {
                    status = rs.getString("status_code");

                    // These conditions are used for error handling, i.e.- if
                    // the subscriber's status are not
                    // upto the mark for Suspending or Resuming the subscribers
                    if (status.equals(PretupsI.RES_MSISDN_STATUS_NEW)) {
                        keyArgumentVO.setArguments(msisdnArr);

                        if ("suspend".equals(p_requestType)) {
                            keyArgumentVO.setKey("restrictedsubs.inputmsisdnsforsuspend.err.msg.statusnew");
                        } else {
                            keyArgumentVO.setKey("restrictedsubs.inputmsisdnsforresume.err.msg.statusnew");
                        }
                        p_errorList.add(keyArgumentVO);
                        continue;
                    }// end of if(status.equals(PretupsI.RES_MSISDN_STATUS_NEW))
                    if (status.equals(PretupsI.RES_MSISDN_STATUS_APPROVED)) {
                        keyArgumentVO.setArguments(msisdnArr);
                        if ("suspend".equals(p_requestType)) {
                            keyArgumentVO.setKey("restrictedsubs.inputmsisdnsforsuspend.err.msg.statusapprove");
                        } else {
                            keyArgumentVO.setKey("restrictedsubs.inputmsisdnsforresume.err.msg.statusapprove");
                        }
                        p_errorList.add(keyArgumentVO);
                        continue;
                    }// end of
                    // if(status.equals(PretupsI.RES_MSISDN_STATUS_APPROVED))
                    if ("suspend".equals(p_requestType)) {
                        if (status.equals(PretupsI.RES_MSISDN_STATUS_ASSOCIATED)) {
                            restrictedSubscriberVO = new RestrictedSubscriberVO();

                            restrictedSubscriberVO.setMsisdn(rs.getString("msisdn"));
                            restrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                            restrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                            restrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                            restrictedSubscriberVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("min_txn_amount")));
                            restrictedSubscriberVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("max_txn_amount")));
                            restrictedSubscriberVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(rs.getLong("monthly_limit")));
                            restrictedSubscriberVO.setTotalTxnCountForDisp(String.valueOf(rs.getLong("total_txn_count")));
                            restrictedSubscriberVO.setStatusDes(rs.getString("status"));
                            restrictedSubscriberVO.setCreatedOnAsString(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on"))));
                            restrictedSubscriberVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                            restrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                            restrictedSubscriberVO.setLanguage(rs.getString("language"));
                            restrictedSubscriberVO.setCountry(rs.getString("country"));
                        } else {
                            // Subscriber already Suspended
                            if (status.equals(PretupsI.RES_MSISDN_STATUS_SUSPENDED)) {
                                keyArgumentVO.setArguments(msisdnArr);
                                keyArgumentVO.setKey("restrictedsubs.inputmsisdnsforsuspend.err.msg.statussuspend");
                                p_errorList.add(keyArgumentVO);
                                continue;
                            }
                        }
                    }// end of if(p_requestType.equals("suspend"))
                    else // For Resume request type
                    {
                        if (status.equals(PretupsI.RES_MSISDN_STATUS_SUSPENDED)) {
                            restrictedSubscriberVO = new RestrictedSubscriberVO();

                            restrictedSubscriberVO.setMsisdn(rs.getString("msisdn"));
                            restrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                            restrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                            restrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                            restrictedSubscriberVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("min_txn_amount")));
                            restrictedSubscriberVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("max_txn_amount")));
                            restrictedSubscriberVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(rs.getLong("monthly_limit")));
                            restrictedSubscriberVO.setTotalTxnCountForDisp(String.valueOf(rs.getLong("total_txn_count")));
                            restrictedSubscriberVO.setStatusDes(rs.getString("status"));
                            restrictedSubscriberVO.setCreatedOnAsString(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on"))));
                            restrictedSubscriberVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                            restrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                            restrictedSubscriberVO.setLanguage(rs.getString("language"));
                            restrictedSubscriberVO.setCountry(rs.getString("country"));
                        } else {
                            // Subscriber already Active
                            if (status.equals(PretupsI.RES_MSISDN_STATUS_ASSOCIATED)) {
                                keyArgumentVO.setArguments(msisdnArr);
                                keyArgumentVO.setKey("restrictedsubs.inputmsisdnsforresume.err.msg.statusactive");
                                p_errorList.add(keyArgumentVO);
                                continue;
                            }
                        }
                    }// end of else
                    // Add the subscriber information for Suspend or Resume
                    subList.add(restrictedSubscriberVO);
                }// end of if(rs.next())
                else {
                    keyArgumentVO.setArguments(msisdnArr);
                    keyArgumentVO.setKey("restrictedsubs.inputmsisdnsforsuspend.err.msg.nosubfound");
                    p_errorList.add(keyArgumentVO);
                    continue;
                }
            } // end of for loop
            // If there is any error in the MSISDN's then throw exception
            if (!p_errorList.isEmpty()) {
                throw new BTSLBaseException(this, methodName, p_errorList, p_fwdPath);
            }
        } // end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[loadSubcriberList]", "",
                    "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[loadSubcriberList]", "",
                    "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: list.size() : =" + subList.size());
            }
        }
        return subList;
    }// end of loadSubcriberList

    /**
     * Method loadSubcriberListForUnblack
     * Method for Extracting Corporate Subscriber Details.
     * This Method will Load the Restricted Subscriber Details according to the
     * msisdn and owner_id.
     *
     * @author Amit Ruwali
     * @param p_con
     *            Connection
     * @param p_status
     *            String
     * @param p_msisdn
     *            String
     * @param p_userID
     *            String
     * @return restrictedSubscriberVO RestrictedSubscriberVO
     * @throws BTSLBaseException
     */

    public ArrayList loadSubcriberListForUnblack(Connection p_con, ArrayList p_msisdnList, ArrayList p_errorList, String p_userID, String p_fwdPath, String p_loginUserID, String p_cp2pPayer, String p_cp2pPayee, String p_c2sPayee) throws BTSLBaseException {
        final String methodName = "loadSubcriberListForUnblack";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered: p_userID= ");
            msg.append(p_userID);
            msg.append(", p_loginUserID= ");
            msg.append(p_loginUserID);
            msg.append(", p_cp2pPayer= ");
            msg.append(p_cp2pPayer);
            msg.append(", p_cp2pPayee= ");
            msg.append(p_cp2pPayee);
            msg.append(", p_c2sPayee= ");
            msg.append(p_c2sPayee);

            String message=msg.toString();
            _log.debug(methodName,message);
        }
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        PreparedStatement pstmtSubList = null;
        ResultSet rs = null;
        KeyArgumentVO keyArgumentVO = null;
        String status;
        String msisdn;
        final ArrayList subsList = new ArrayList();
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT r.msisdn,r.subscriber_id,r.channel_user_id,r.channel_user_category,r.employee_code,r.owner_id,");
        strBuff.append("r.employee_name,r.network_code,r.min_txn_amount,r.max_txn_amount,r.monthly_limit,r.association_date,");
        strBuff.append("r.total_txn_count,r.total_txn_amount,r.black_list_status,r.cp2p_payee_status, r.c2s_payee_status,r.remark,r.approved_by,lk.lookup_name,");
        strBuff.append("r.approved_on,r.associated_by,r.status,r.created_on,r.created_by,r.modified_on,r.modified_by,");
        strBuff.append("r.subscriber_type,r.language,r.country ");
        strBuff.append(" FROM restricted_msisdns r,lookups lk ");
        strBuff.append(" WHERE lk.lookup_code=r.status AND lk.lookup_type=? AND");
        strBuff.append(" r.msisdn=? AND r.owner_id=? AND r.restricted_type = ? ORDER BY r.employee_name");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSubList = p_con.prepareStatement(sqlSelect);
            for (int i = 0, j = p_msisdnList.size(); i < j; i++) {
                msisdn = (String) p_msisdnList.get(i);
                keyArgumentVO = new KeyArgumentVO();
                final String[] msisdnArr = new String[1];
                msisdnArr[0] = msisdn;

                pstmtSubList.setString(1, PretupsI.LOOKUP_TYPE_RES_MSISDN_STATUS);
                pstmtSubList.setString(2, msisdn);
                pstmtSubList.setString(3, p_userID);
                pstmtSubList.setString(4, PretupsI.DEFAULT_RESTRICTED_TYPE);
                rs = pstmtSubList.executeQuery();
                pstmtSubList.clearParameters();
                if (rs.next()) {
                    // addded for C2S Payee and CP2P Payee Date 04/02/08
                    if (p_cp2pPayer != null) {
                        status = rs.getString("black_list_status");
                        if (status.equals(PretupsI.RES_MSISDN_UNBLACKLIST_STATUS)) {
                            BlackListLog.log("UNBLACKLIST", "Comma Seperated List", msisdn, "Subscriber already Unblack Listed as a CP2P Payer", "Fail",
                                    "Logged In UserID : " + p_loginUserID);
                            keyArgumentVO.setArguments(msisdnArr);
                            keyArgumentVO.setKey("restrictedsubs.loadsubscriberlistforunblack.err.msg.alreadyunblackcp2ppayer");
                            p_errorList.add(keyArgumentVO);
                            continue;
                        }
                    }
                    if (p_cp2pPayee != null) {
                        status = rs.getString("cp2p_payee_status");
                        if (status.equals(PretupsI.RES_MSISDN_UNBLACKLIST_STATUS)) {
                            BlackListLog.log("UNBLACKLIST", "Comma Seperated List", msisdn, "Subscriber already Unblack Listed as a CP2P Payee", "Fail",
                                    "Logged In UserID : " + p_loginUserID);
                            keyArgumentVO.setArguments(msisdnArr);
                            keyArgumentVO.setKey("restrictedsubs.loadsubscriberlistforunblack.err.msg.alreadyunblackcp2ppayee");
                            p_errorList.add(keyArgumentVO);
                            continue;
                        }
                    }
                    if (p_c2sPayee != null) {
                        status = rs.getString("c2s_payee_status");
                        if (status.equals(PretupsI.RES_MSISDN_UNBLACKLIST_STATUS)) {
                            BlackListLog.log("UNBLACKLIST", "Comma Seperated List", msisdn, "Subscriber already Unblack Listed as a C2S Payee", "Fail",
                                    "Logged In UserID : " + p_loginUserID);
                            keyArgumentVO.setArguments(msisdnArr);
                            keyArgumentVO.setKey("restrictedsubs.loadsubscriberlistforunblack.err.msg.alreadyunblackc2spayee");
                            p_errorList.add(keyArgumentVO);
                            continue;
                        }
                    }
                    // end of C2S Payee and Cp2P Payee
                    restrictedSubscriberVO = new RestrictedSubscriberVO();
                    restrictedSubscriberVO.setSubscriberID(rs.getString("subscriber_id"));
                    restrictedSubscriberVO.setChannelUserID(rs.getString("channel_user_id"));
                    restrictedSubscriberVO.setChannelUserCategory(rs.getString("channel_user_category"));
                    restrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                    restrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                    restrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                    restrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                    restrictedSubscriberVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                    restrictedSubscriberVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                    restrictedSubscriberVO.setTotalTxnCount(rs.getLong("total_txn_count"));
                    restrictedSubscriberVO.setTotalTransferAmount(rs.getLong("total_txn_amount"));
                    restrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                    restrictedSubscriberVO.setApprovedOn(rs.getDate("approved_on"));
                    restrictedSubscriberVO.setCreatedOnAsString(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("created_on"))));
                    restrictedSubscriberVO.setStatus(rs.getString("status"));
                    restrictedSubscriberVO.setStatusDes(rs.getString("lookup_name"));
                    restrictedSubscriberVO.setBlackListStatus(rs.getString("black_list_status"));
                    restrictedSubscriberVO.setRemarks(rs.getString("remark"));
                    restrictedSubscriberVO.setApprovedBy(rs.getString("approved_by"));
                    restrictedSubscriberVO.setAssociatedBy(rs.getString("associated_by"));
                    restrictedSubscriberVO.setAssociationDate(rs.getDate("association_date"));
                    restrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                    restrictedSubscriberVO.setLanguage(rs.getString("language"));
                    restrictedSubscriberVO.setCountry(rs.getString("country"));
                    restrictedSubscriberVO.setMsisdn(rs.getString("msisdn"));
                    subsList.add(restrictedSubscriberVO);
                } else {
                    keyArgumentVO.setArguments(msisdnArr);
                    keyArgumentVO.setKey("restrictedsubs.loadsubscriberlistforunblack.err.msg.nosubfound");
                    BlackListLog.log("UNBLACKLIST", "Comma Seperated List", msisdn, "No Subscriber Found", "Fail", "Logged In UserID : " + p_loginUserID);
                    p_errorList.add(keyArgumentVO);
                    continue;
                }
            }
            // If there is any error in the MSISDN's then throw exception
            if (!p_errorList.isEmpty()) {
                throw new BTSLBaseException(this, methodName, p_errorList, p_fwdPath);
            }
        } // end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[loadSubcriberListForUnblack]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[loadSubcriberListForUnblack]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting subsList.size=" + subsList.size());
            }
        }
        return subsList;
    }

    /**
     * Method :changeSubscriberListStatus
     * Method for Updating the status of the subscribers for Suspending(change
     * Status Active('Y') to Suspend('S'))
     * or for Rresuming(change Status Suspend('S') to Active('Y'))
     *
     * @author Amit Singh
     * @param p_con
     *            java.sql.Connection
     * @param p_updatedList
     *            ArrayList
     * @param p_modifiedBy
     *            String
     * @param p_modifiedOn
     *            Date
     * @return nonUpdatedMsisdn String
     * @throws BTSLBaseException
     */
    public String changeSubscriberListStatus(Connection p_con, ArrayList p_updatedList, String p_modifiedBy, Date p_modifiedOn, String p_ownerID) throws BTSLBaseException {
        final String methodName = "changeSubscriberListStatus";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered: = p_updatedList.size() : ");
            msg.append(p_updatedList.size());
            msg.append(", p_modifiedBy :  ");
            msg.append(p_modifiedBy);
            msg.append(", p_modifiedOn :  ");
            msg.append(p_modifiedOn);
            msg.append(", p_ownerID :  ");
            msg.append(p_ownerID);

            String message=msg.toString();
            _log.debug(methodName,message);
        }
        PreparedStatement psmtUpdate = null;
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        int updateCount = 0;
        final StringBuffer nonUpdatedMsisdn = new StringBuffer();
        boolean modified = false;

        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("UPDATE restricted_msisdns SET status = ?, modified_by = ?, ");
            strBuff.append("modified_on = ? WHERE msisdn = ? AND owner_id = ?");
            final String strUpdate = strBuff.toString();

            psmtUpdate = p_con.prepareStatement(strUpdate);

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query strUpdate:" + strUpdate);
            }

            for (int i = 0, j = p_updatedList.size(); i < j; i++) {
                restrictedSubscriberVO = (RestrictedSubscriberVO) p_updatedList.get(i);

                psmtUpdate.setString(1, restrictedSubscriberVO.getStatus());
                psmtUpdate.setString(2, p_modifiedBy);
                psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_modifiedOn));
                psmtUpdate.setString(4, restrictedSubscriberVO.getMsisdn());
                psmtUpdate.setString(5, p_ownerID);

                modified = this.isRestrictedMsisdnModified(p_con, restrictedSubscriberVO.getLastModifiedTime(), restrictedSubscriberVO.getOwnerID(), restrictedSubscriberVO
                        .getMsisdn());
                if (modified) {
                    throw new BTSLBaseException(this, methodName, "error.modify.true");
                }

                updateCount = psmtUpdate.executeUpdate();
                psmtUpdate.clearParameters();

                // If subsriber is not updated in database due to some problem
                // then Rollback,
                // otherwise Commit the transaction.
                if (updateCount <= 0) {
                    p_con.rollback();
                    nonUpdatedMsisdn.append(restrictedSubscriberVO.getMsisdn());
                    nonUpdatedMsisdn.append(",");
                    continue;
                }
                p_con.commit();
            }// end of for(int i = 0; i < p_updatedList.size(); i++)
        } // end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[changeSubscriberListStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[changeSubscriberListStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting :: nonUpdatedMsisdn.toString() :" + nonUpdatedMsisdn.toString());
            }
        } // end of finally
        return nonUpdatedMsisdn.toString();
    }// end of changeSubscriberListStatus

    /**
     * This method is used to check that is the record modified during the
     * processing.
     * isRestrictedMsisdnModified
     *
     * @param p_con
     *            Connection
     * @param p_oldlastModified
     *            Long
     * @param p_key
     *            String
     * @return boolean
     * @throws BTSLBaseException
     * @author ved.sharma
     */
    private boolean isRestrictedMsisdnModified(Connection p_con, long p_oldlastModified, String p_key1, String p_key2) throws BTSLBaseException {
        final String methodName = "isRestrictedMsisdnModified";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered:p_oldlastModified= ");
            msg.append(p_oldlastModified);
            msg.append(", p_key1=  ");
            msg.append(p_key1);
            msg.append(", p_key2=  ");
            msg.append(p_key2);

            String message=msg.toString();
            _log.debug(methodName, message);
        }
        boolean modified = false;
        if (p_oldlastModified == 0) {
            modified = false;
            return modified;
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlRecordModified = new StringBuffer("SELECT modified_on FROM restricted_msisdns WHERE owner_id=? AND msisdn=?");
        java.sql.Timestamp newlastModified = null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY=" + sqlRecordModified);
            }
            pstmtSelect = p_con.prepareStatement(sqlRecordModified.toString());
            pstmtSelect.setString(1, p_key1);
            pstmtSelect.setString(2, p_key2);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            } else {
                modified = true;
                return true;
            }
            if (newlastModified.getTime() != p_oldlastModified) {
                modified = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[isRestrictedMsisdnModified]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[isRestrictedMsisdnModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exititng:modified=" + modified);
            }
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * Method for Updating the status of the subscribers for
     * DeAssociation(change Status Active('Y') )
     * on the basis of owner_id and msisdn
     * Method :changeStatusForDeAssociation
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_updatedList
     *            ArrayList
     * @param p_modifiedBy
     *            String
     * @param p_modifiedOn
     *            Date
     * @throws BTSLBaseException
     */
    public String changeStatusForDeAssociation(Connection p_con, ArrayList p_updatedList) throws BTSLBaseException {
        final String methodName = "changeStatusForDeAssociation";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered: = p_updatedList.size() : ");
            msg.append(p_updatedList.size());
            msg.append(", Entered: = p_updatedList : ");
            msg.append(p_updatedList);

            String message=msg.toString();
            _log.debug(methodName, message);
        }
        // commented for DB2OraclePreparedStatement psmtUpdate = null;
        PreparedStatement psmtUpdate = null;
        ScheduleBatchDetailVO scheduleDetailVO = null;
        int updateCount = 0;
        final StringBuffer nonUpdatedMsisdn = new StringBuffer();
        try {
            final StringBuffer strBuff = new StringBuffer("UPDATE restricted_msisdns SET status ='A', modified_by = ?, ");
            strBuff.append("employee_code='',employee_name='',min_txn_amount='0',max_txn_amount='0',monthly_limit='0',");
            strBuff.append("channel_user_category = '',");
            strBuff.append("modified_on = ? WHERE msisdn = ? AND owner_id = ? ");
            final String strUpdate = strBuff.toString();
            // commented for DB2 psmtUpdate =
            // (OraclePreparedStatement)p_con.prepareStatement(strUpdate);
            psmtUpdate = p_con.prepareStatement(strUpdate);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query strUpdate:" + strUpdate);
            }
            boolean modified = false;
            for (int i = 0, j = p_updatedList.size(); i < j; i++) {
                scheduleDetailVO = (ScheduleBatchDetailVO) p_updatedList.get(i);
                psmtUpdate.setString(1, scheduleDetailVO.getModifiedBy());
                psmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(scheduleDetailVO.getModifiedOn()));
                psmtUpdate.setString(3, scheduleDetailVO.getMsisdn());
                psmtUpdate.setString(4, scheduleDetailVO.getOwnerID());
                modified = this.isRestrictedMsisdnModified(p_con, scheduleDetailVO.getLastModifiedTime(), scheduleDetailVO.getOwnerID(), scheduleDetailVO.getMsisdn());
                if (modified) {
                    throw new BTSLBaseException(this, methodName, "error.modify.true");
                }
                updateCount = psmtUpdate.executeUpdate();
                psmtUpdate.clearParameters();
                // If subsriber is not updated in database due to some problem
                // then Rollback, otherwise Commit the transaction.
                if (updateCount <= 0) {
                    p_con.rollback();
                    nonUpdatedMsisdn.append(scheduleDetailVO.getMsisdn());
                    nonUpdatedMsisdn.append(",");
                    continue;
                } // end of if(updateCount <= 0)
                else {
                    p_con.commit();
                }
            }// end of for loop
        } // end of try
        catch (SQLException sqle) {
            try {
                p_con.rollback();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[changeSubscriberListStatus]", "", "",
                    "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            try {
                p_con.rollback();
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[changeSubscriberListStatus]", "", "",
                    "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting");
            }
        } // end of finally
        return nonUpdatedMsisdn.toString();
    }

    /**
     * Method subsBulkRegistration
     * This Method is used for uploading the mobile no.s
     *
     * @author Amit.singh
     * @param p_con
     *            Connection
     * @param p_msisdnList
     *            ArrayList
     * @param p_userId
     *            String
     * @param p_loggedInUserID
     *            String
     * @param p_fileName
     *            String
     * @param p_canNotRegMsg
     *            String
     * @param p_alredyExistMsg
     *            String
     * @return void
     * @throws BTSLBaseException
     */
    public void subsBulkRegistration(Connection p_con, ArrayList p_msisdnList, String p_ownerID, String p_loggedInUserID, String p_fileName, String p_canNotRegMsg, String p_alredyExistMsg) throws BTSLBaseException {
        final String methodName = "subsBulkRegistration";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered p_msisdnList size : ");
            msg.append(p_msisdnList.size());
            msg.append(", p_ownerID : ");
            msg.append(p_ownerID);
            msg.append(", p_loggedInUserID : ");
            msg.append(p_loggedInUserID);
            msg.append(", p_fileName : ");
            msg.append(p_fileName);
            msg.append(", p_canNotRegMsg : ");
            msg.append(p_canNotRegMsg);
            msg.append(", p_alredyExistMsg : ");
            msg.append(p_alredyExistMsg);

            String message=msg.toString();
            _log.debug(methodName,message);
        }

        PreparedStatement isMsisdnExist = null;
        ResultSet rsIsMsisdnExist = null;
        PreparedStatement pstmtInsert = null;
        int updateCount = 0;
        String msisdn = null;
        long failCount = 0;
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        try {
            // check whether subscriber msisdn exists under the owner user
            final String isMsisdnExistQuery = "SELECT 1 FROM restricted_msisdns WHERE msisdn=? AND owner_id=?";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "isMsisdnExistQuery = " + isMsisdnExistQuery);
            }
            isMsisdnExist = p_con.prepareStatement(isMsisdnExistQuery.toString());

            // Insert the subscriber under the owner user
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT INTO restricted_msisdns (msisdn, subscriber_id, owner_id,channel_user_id, network_code, ");
            strBuff.append("status, created_on, created_by, subscriber_type, ");// the
            // code
            // is
            // changed
            // to
            // store
            // only
            // created_by
            // and
            // created_on
            // field
            // when
            // file
            // is
            // uploaded
            // for
            // bulk
            // registration
            // 1 BugNo 283 Code fix start
            strBuff.append("language, country, black_list_status, modified_on, modified_by ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");// the
            // code
            // is
            // changed
            // to
            // store
            // modified_by
            // and
            // modified_on
            // field
            // when
            // file
            // is
            // uploaded
            // for
            // bulk
            // registration
            // 1 BugNo 283 Code fix end

            final String insertQuery = strBuff.toString();
            pstmtInsert = p_con.prepareStatement(insertQuery);

            for (int i = 0, j = p_msisdnList.size(); i < j; i++) {
                restrictedSubscriberVO = (RestrictedSubscriberVO) p_msisdnList.get(i);
                msisdn = restrictedSubscriberVO.getMsisdn();

                if (restrictedSubscriberVO.getErrorCode() == null) {
                    isMsisdnExist.setString(1, msisdn);
                    isMsisdnExist.setString(2, p_ownerID);
                    rsIsMsisdnExist = isMsisdnExist.executeQuery();
                    isMsisdnExist.clearParameters();
                    if (rsIsMsisdnExist.next()) // if condition true then mark
                    // error and continue to next
                    // msisdn
                    {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, " Mobile number already exists under the owner user=" + msisdn);
                        }
                        RestrictedMsisdnLog.log(p_fileName, msisdn, "Mobile number already exists under the owner user", "Fail", "Logged In UserID : " + p_loggedInUserID);

                        restrictedSubscriberVO.setLineNumber(String.valueOf(i + 1));
                        restrictedSubscriberVO.setMsisdn(msisdn);
                        restrictedSubscriberVO.setErrorCode(p_alredyExistMsg);
                        failCount = failCount + 1;
                        restrictedSubscriberVO.setFailCount(failCount);
                        if (rsIsMsisdnExist != null) {
                            rsIsMsisdnExist.close();
                        }
                        continue;
                    }// end of if(rsIsMsisdnExist.next())
                    if (rsIsMsisdnExist != null) {
                        rsIsMsisdnExist.close();
                    }

                    // Insert the subscriber
                    pstmtInsert.setString(1, msisdn);
                    pstmtInsert.setString(2, restrictedSubscriberVO.getSubscriberID());
                    pstmtInsert.setString(3, restrictedSubscriberVO.getOwnerID());
                    pstmtInsert.setString(4, restrictedSubscriberVO.getOwnerID());
                    pstmtInsert.setString(5, restrictedSubscriberVO.getNetworkCode());
                    pstmtInsert.setString(6, restrictedSubscriberVO.getStatus());
                    pstmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(restrictedSubscriberVO.getCreatedOn()));
                    pstmtInsert.setString(8, restrictedSubscriberVO.getCreatedBy());
                    pstmtInsert.setString(9, restrictedSubscriberVO.getSubscriberType());
                    pstmtInsert.setString(10, restrictedSubscriberVO.getLanguage());
                    pstmtInsert.setString(11, restrictedSubscriberVO.getCountry());
                    pstmtInsert.setString(12, restrictedSubscriberVO.getBlackListStatus());

                    // 1 BugNo 283 Code fix start
                    pstmtInsert.setTimestamp(13, BTSLUtil.getTimestampFromUtilDate(restrictedSubscriberVO.getModifiedOn()));
                    pstmtInsert.setString(14, restrictedSubscriberVO.getModifiedBy());
                    // 1 BugNo 283 Code fix end

                    updateCount = pstmtInsert.executeUpdate();
                    pstmtInsert.clearParameters();

                    if (updateCount <= 0) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, " Cannot Insert the subscriber for msisdn=" + msisdn);
                        }
                        RestrictedMsisdnLog.log(p_fileName, msisdn, "Cannot upload the MSISDN", "Fail", "Logged In UserID : " + p_loggedInUserID);

                        restrictedSubscriberVO.setLineNumber(String.valueOf(i + 1));
                        restrictedSubscriberVO.setMsisdn(msisdn);
                        restrictedSubscriberVO.setErrorCode(p_canNotRegMsg);
                        failCount = failCount + 1;
                        restrictedSubscriberVO.setFailCount(failCount);
                        p_con.rollback();
                        continue;
                    }// end of if(updateCount<=0)

                    p_con.commit();
                    RestrictedMsisdnLog.log(p_fileName, msisdn, "MSISDN uploaded successfully", "Success", "Logged In UserID : " + p_loggedInUserID);
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, " MSISDN uploaded successfully : " + msisdn);
                    }
                }// end of if(restrictedSubscriberVO.getErrorCode()==null)
            }// end of for(int i = 0, j = p_msisdnList.size(); i<j; i++)

            // the last value of failCount is to be set in the last
            // restrictedSubscriberVO
            // and get this failCount in the Action class for getting the total
            // fail counts(form DAO+Action)
            restrictedSubscriberVO.setFailCount(failCount);
        }// end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[subsBulkRegistration]", "",
                    "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[subsBulkRegistration]", "",
                    "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsIsMsisdnExist != null) {
                    rsIsMsisdnExist.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (isMsisdnExist != null) {
                    isMsisdnExist.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting : p_msisdnList.size()=" + p_msisdnList.size());
            }
        }// end of finally
    }// end of subsBulkRegistration

    /**
     * Method loadSubsListForDelete
     *
     * @author Zahid Salim
     *         This method is used to load the the subscribers on the basis of
     *         their
     *         MSISDN and ownerID for deleting the subscribers
     *         Method :loadSubsListForDelete
     * @param p_con
     *            java.sql.Connection
     * @param p_msisdnList
     *            ArrayList
     * @param p_ownerID
     *            String
     * @param p_invalidMsisdn
     *            StringBuffer
     * @return subList ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadSubsListForDelete(Connection p_con, ArrayList p_msisdnList, String p_ownerID, StringBuffer p_invalidMsisdn) throws BTSLBaseException {
        final String methodName = "loadSubsListForDelete";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered p_msisdnList size : ");
            msg.append(p_msisdnList.size());
            msg.append(", p_ownerID : ");
            msg.append(p_ownerID);
            msg.append(", p_invalidMsisdn : ");
            msg.append(p_invalidMsisdn);

            String message=msg.toString();
            _log.debug(methodName, message);
        }
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        PreparedStatement pstmtSubList = null;
        final ArrayList subList = new ArrayList();
        ResultSet rs = null;
        try {
            final StringBuffer strBuff = new StringBuffer("SELECT RM.msisdn,RM.employee_name, RM.employee_code, RM.min_txn_amount,RM.status, ");
            strBuff.append("RM.owner_id, RM.max_txn_amount,RM.max_txn_amount,RM.monthly_limit, RM.total_txn_count, ");
            strBuff.append("RM.created_on , L.lookup_name status_desc ");
            strBuff.append("FROM restricted_msisdns RM, lookups L  ");
            strBuff.append("WHERE  RM.msisdn=? AND RM.owner_id=? AND RM.restricted_type = ? ");
            strBuff.append("AND RM.msisdn NOT IN( ");
            strBuff.append("SELECT  SBD.msisdn ");
            strBuff.append("FROM scheduled_batch_detail SBD, scheduled_batch_master SBM, restricted_msisdns RM  ");
            strBuff.append("WHERE SBD.batch_id=SBM.batch_id  AND SBM.owner_id=?  ");
            strBuff.append("AND SBD.status IN ('" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "', '" + PretupsI.SCHEDULE_STATUS_UNDERPROCESSED + "')) ");
            strBuff.append("AND L.lookup_code = RM.status AND L.lookup_type =? ");
            strBuff.append("ORDER BY RM.employee_name ");

            final String sqlSelect = strBuff.toString();
            pstmtSubList = p_con.prepareStatement(sqlSelect);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }

            final int j = p_msisdnList.size();
            for (int i = 0; i < j; i++) {
                pstmtSubList.setString(1, (String) p_msisdnList.get(i));
                pstmtSubList.setString(2, p_ownerID);
                pstmtSubList.setString(3, PretupsI.DEFAULT_RESTRICTED_TYPE);
                pstmtSubList.setString(4, p_ownerID);
                pstmtSubList.setString(5, PretupsI.LOOKUP_TYPE_RES_MSISDN_STATUS);
                rs = pstmtSubList.executeQuery();
                pstmtSubList.clearParameters();
                if (rs.next()) {
                    restrictedSubscriberVO = new RestrictedSubscriberVO();
                    restrictedSubscriberVO.setMsisdn(rs.getString("msisdn"));
                    restrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                    restrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                    restrictedSubscriberVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("min_txn_amount")));
                    restrictedSubscriberVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("max_txn_amount")));
                    restrictedSubscriberVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(rs.getLong("monthly_limit")));
                    restrictedSubscriberVO.setTotalTxnCountForDisp(PretupsBL.getDisplayAmount(rs.getLong("total_txn_count")));
                    restrictedSubscriberVO.setStatus(rs.getString("status"));
                    restrictedSubscriberVO.setStatusDes(rs.getString("status_desc"));
                    restrictedSubscriberVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on")));
                    restrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                    restrictedSubscriberVO.setCheckBoxVal("DR");
                    subList.add(restrictedSubscriberVO);
                } else {
                    p_invalidMsisdn.append(p_msisdnList.get(i));
                    p_invalidMsisdn.append(",");
                }
            }// end of for loop
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[loadSubsListForDelete]",
                    "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[loadSubsListForDelete]",
                    "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: subList.size() : =" + subList.size());
            }
        }// End of finally
        return subList;
    }

    // End of loadSubsListForDelete

    /**
     * Method :deleteResSubscriberBulk
     * This method is used to delete the details of susbcriber(s)
     *
     * @author Zahid Salim
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            Arraylist
     * @param p_ownerID
     * @return String
     * @throws BTSLBaseException
     *
     */
    public String deleteResSubscriberBulk(Connection p_con, ArrayList p_msisdnList, String p_ownerID) throws BTSLBaseException {
        final String methodName = "deleteResSubscriberBulk";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered p_msisdnList : ");
            msg.append(p_msisdnList);
            msg.append(", p_ownerID : ");
            msg.append(p_ownerID);

            String message=msg.toString();
            _log.debug(methodName, message);
        }

        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        final StringBuffer nonDeletedMsisdn = new StringBuffer();
        int deleteCount = 0;
        final String sqlDelete = "DELETE FROM restricted_msisdns WHERE owner_id=? AND msisdn=? ";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlUpdate=" + sqlDelete);
        }
        try {
            pstmtUpdate = p_con.prepareStatement(sqlDelete);
            RestrictedSubscriberVO restrictedSubscriberVO = null;
            for (int index = 0, j = p_msisdnList.size(); index < j; index++) {
                restrictedSubscriberVO = (RestrictedSubscriberVO) p_msisdnList.get(index);
                pstmtUpdate.setString(1, restrictedSubscriberVO.getOwnerID());
                pstmtUpdate.setString(2, restrictedSubscriberVO.getMsisdn());
                deleteCount = pstmtUpdate.executeUpdate();
                pstmtUpdate.clearParameters();
                if (deleteCount <= 0) {
                    p_con.rollback();
                    nonDeletedMsisdn.append(restrictedSubscriberVO.getMsisdn());
                    nonDeletedMsisdn.append(",");
                    continue;
                }
                p_con.commit();
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[deleteResSubscriberBulk]",
                    "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[deleteResSubscriberBulk]",
                    "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: deleteCount = ");
                msg.append(deleteCount);
                msg.append(", nonDeletedMsisdn = ");
                msg.append(nonDeletedMsisdn);

                String message=msg.toString();
                _log.debug(methodName, message);
            }
        }// End of finally
        return nonDeletedMsisdn.toString();
    }

    // End of deleteResSubscriberBulk

    /**
     * Method :deleteRestrictedBulk
     * This method is used to delete the details of all the susbcribers
     *
     * @author Zahid Salim
     * @param p_con
     *            Connection
     * @param p_ownerID
     *            String
     * @return updateCount int
     * @throws BTSLBaseException
     */
    public int deleteRestrictedBulk(Connection p_con, String p_ownerID) throws BTSLBaseException {
        final String methodName = "deleteRestrictedBulk";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_ownerID = " + p_ownerID);
        }

        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        int deleteCount = -1;
        final StringBuffer strBuff = new StringBuffer("DELETE ");
        strBuff.append("FROM restricted_msisdns RM ");
        strBuff.append("WHERE owner_id= ? AND restricted_type= ? ");
        strBuff.append("AND RM.msisdn NOT IN (");
        strBuff.append("SELECT  SBD.msisdn FROM scheduled_batch_detail SBD, scheduled_batch_master SBM ");
        strBuff.append("WHERE SBD.batch_id=SBM.batch_id ");
        strBuff.append("AND SBM.owner_id=? ");
        strBuff.append("AND SBD.status IN ('" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "', '" + PretupsI.SCHEDULE_STATUS_UNDERPROCESSED + "')) ");

        final String sqlUpdate = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlUpdate=" + sqlUpdate);
        }
        try {
            pstmtUpdate = p_con.prepareStatement(sqlUpdate);
            pstmtUpdate.setString(1, p_ownerID);
            pstmtUpdate.setString(2, PretupsI.DEFAULT_RESTRICTED_TYPE);
            pstmtUpdate.setString(3, p_ownerID);
            deleteCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[deleteRestrictedBulk]", "",
                    "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[deleteRestrictedBulk]", "",
                    "", "", "Exception:" + ex.getMessage());
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
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: deleteCount=" + deleteCount);
            }
        }// End of finally
        return deleteCount;
    }

    // End of deleteRestrictedBulk

    /**
     * Method blackListSingleSubs
     * Method for Black listing single subscriber
     * This Method will Black list the single subscriber
     *
     * @author Amit Ruwali
     * @param p_con
     *            Connection
     * @param p_resVO
     *            RestrictedSubscriberVO
     * @return restrictedSubscriberVO RestrictedSubscriberVO
     * @throws BTSLBaseException
     */

    public int blackListSingleSubs(Connection p_con, RestrictedSubscriberVO p_resSubsVO) throws BTSLBaseException {
        final String methodName = "blackListSingleSubs";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_resVO" + p_resSubsVO);
        }

        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        int updateCount = -1;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" UPDATE restricted_msisdns set ");
        // addded for C2S Payee and Cp2P Payee Date 04/02/08
        if (p_resSubsVO.getBlackListStatus() != null) {
            strBuff.append(" black_list_status=?,");
        }
        if (p_resSubsVO.getCp2pPayeeStatus() != null) {
            strBuff.append(" cp2p_payee_status=?,");
        }
        if (p_resSubsVO.getC2sPayeeStatus() != null) {
            strBuff.append(" c2s_payee_status=?,");
        }
        // End of C2S Payee and Cp2P Payee
        strBuff.append(" modified_by=?, modified_on=? WHERE msisdn=? AND owner_id=?");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtUpdate = p_con.prepareStatement(sqlSelect);
            int i = 1;
            // addded for C2S Payee and Cp2P Payee Date 04/02/08
            if (p_resSubsVO.getBlackListStatus() != null) {
                pstmtUpdate.setString(i++, p_resSubsVO.getBlackListStatus());
            }
            if (p_resSubsVO.getCp2pPayeeStatus() != null) {
                pstmtUpdate.setString(i++, p_resSubsVO.getCp2pPayeeStatus());
            }
            if (p_resSubsVO.getC2sPayeeStatus() != null) {
                pstmtUpdate.setString(i++, p_resSubsVO.getC2sPayeeStatus());
            }
            // End of C2S Payee and Cp2P Payee
            pstmtUpdate.setString(i++, p_resSubsVO.getModifiedBy());
            pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(p_resSubsVO.getModifiedOn()));
            pstmtUpdate.setString(i++, p_resSubsVO.getMsisdn());
            pstmtUpdate.setString(i++, p_resSubsVO.getOwnerID());
            final boolean modified = this.isBlackListSinglSubsModified(p_con, p_resSubsVO.getLastModifiedTime(), p_resSubsVO.getMsisdn(), p_resSubsVO.getOwnerID());
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            } else {
                updateCount = pstmtUpdate.executeUpdate();
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[blackListSingleSubs]", "",
                    "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[blackListSingleSubs]", "",
                    "", "", "Exception:" + ex.getMessage());
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
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method isBlackListSinglSubsModified.
     * This method is used to check that is the record modified during the
     * processing.
     *
     * @param p_con
     *            Connection
     * @param p_oldlastModified
     *            Long
     * @param p_msisdn
     *            String
     * @param p_ownerID
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    private boolean isBlackListSinglSubsModified(Connection p_con, long p_oldlastModified, String p_msisdn, String p_ownerID) throws BTSLBaseException {
        final String methodName = "isBlackListSinglSubsModified";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered:p_oldlastModified = ");
            msg.append(p_oldlastModified);
            msg.append(", p_msisdn = ");
            msg.append(p_msisdn);
            msg.append(", p_ownerID = ");
            msg.append(p_ownerID);

            String message=msg.toString();
            _log.debug(methodName, message);
        }
        boolean modified = false;
        if (p_oldlastModified == 0) {
            modified = false;
            return modified;
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer sqlRecordModified = new StringBuffer();
        sqlRecordModified.append("SELECT modified_on FROM restricted_msisdns ");
        sqlRecordModified.append("WHERE msisdn=? AND owner_id=?");
        java.sql.Timestamp newlastModified = null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY=" + sqlRecordModified);
            }
            final String query = sqlRecordModified.toString();
            pstmtSelect = p_con.prepareStatement(query);
            pstmtSelect.setString(1, p_msisdn);
            pstmtSelect.setString(2, p_ownerID);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newlastModified = rs.getTimestamp("modified_on");
            }
            // The record is not present because the record is modified by other
            // person and the
            // modification is done on the value of the key.
            else {
                modified = true;
                return true;
            }
            if (newlastModified.getTime() != p_oldlastModified) {
                modified = true;
            }
        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[isBlackListSinglSubsModified]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[isBlackListSinglSubsModified]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exititng:modified=" + modified);
            }
        }// end of finally
        return modified;
    }// end recordModified

    /**
     * Method for Updating the black list status of the subscribers
     * Method :unBlackSelSubs
     *
     * @param p_con
     *            Connection
     * @param p_updatedList
     *            ArrayList
     * @param p_modifiedBy
     *            String
     * @param p_modifiedOn
     *            Date
     * @return nonUpdatedMsisdn String
     * @throws BTSLBaseException
     */
    public String unBlackSelSubs(Connection p_con, ArrayList p_updatedList, String p_modifiedBy, Date p_modifiedOn, String p_ownerID, String p_cp2pPayer, String p_cp2pPayee, String p_c2sPayee) throws BTSLBaseException {
        final String methodName = "unBlackSelSubs";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered: = p_updatedList.size() = ");
            msg.append(p_updatedList.size());
            msg.append(", p_modifiedBy = ");
            msg.append(p_modifiedBy);
            msg.append(", p_modifiedOn = ");
            msg.append(p_modifiedOn);
            msg.append(", p_cp2pPayer= ");
            msg.append(p_cp2pPayer);
            msg.append(", p_cp2pPayee= ");
            msg.append(p_cp2pPayee);
            msg.append(", p_c2sPayee= ");
            msg.append(p_c2sPayee);

            String message=msg.toString();
            _log.debug(methodName,message);
        }
        PreparedStatement psmtUpdate = null;
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        int updateCount = 0;
        final StringBuffer nonUpdatedMsisdn = new StringBuffer();
        try {
            final StringBuffer strBuff = new StringBuffer("UPDATE restricted_msisdns SET ");
            if (p_cp2pPayer != null) {
                strBuff.append(" black_list_status = ?,");
            }
            if (p_cp2pPayee != null) {
                strBuff.append(" cp2p_payee_status=?,");
            }
            if (p_c2sPayee != null) {
                strBuff.append(" c2s_payee_status=?,");
            }
            strBuff.append(" modified_by = ?,modified_on = ? WHERE msisdn = ? AND owner_id=? ");
            final String strUpdate = strBuff.toString();
            psmtUpdate = p_con.prepareStatement(strUpdate);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query strUpdate:" + strUpdate);
            }
            for (int i = 0, k = 1, j = p_updatedList.size(); i < j; i++) {
                restrictedSubscriberVO = (RestrictedSubscriberVO) p_updatedList.get(i);
                k = 1;
                // addded for C2S Payee and Cp2P Payee Date 04/02/08
                if (p_cp2pPayer != null) {
                    psmtUpdate.setString(k++, p_cp2pPayer);
                }
                if (p_cp2pPayee != null) {
                    psmtUpdate.setString(k++, p_cp2pPayee);
                }
                if (p_c2sPayee != null) {
                    psmtUpdate.setString(k++, p_c2sPayee);
                }
                // end of C2S Payee and Cp2P Payee
                psmtUpdate.setString(k++, p_modifiedBy);
                psmtUpdate.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(p_modifiedOn));
                psmtUpdate.setString(k++, restrictedSubscriberVO.getMsisdn());
                psmtUpdate.setString(k++, p_ownerID);
                updateCount = psmtUpdate.executeUpdate();
                psmtUpdate.clearParameters();
                // If subsriber is not updated in database due to some problem
                // then Rollback,
                // otherwise Commit the transaction.
                if (updateCount <= 0) {
                    p_con.rollback();
                    // single line logger entry
                    BlackListLog.log("UNBLACKLIST", "Comma Seperated List", restrictedSubscriberVO.getMsisdn(), "Error In Updation", "Fail",
                            "Logged In UserID : " + p_modifiedBy);
                    nonUpdatedMsisdn.append(restrictedSubscriberVO.getMsisdn());
                    nonUpdatedMsisdn.append(",");
                    continue;
                } else {
                    p_con.commit();
                    BlackListLog.log("UNBLACKLIST", "Comma Seperated List", restrictedSubscriberVO.getMsisdn(), "Subscriber UnBlack Listed Successfully", "Success",
                            "Logged In UserID : " + p_modifiedBy);
                }
            }
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[unBlackSelSubs]", "", "",
                    "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[unBlackSelSubs]", "", "",
                    "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting :: nonUpdatedMsisdn.toString() :" + nonUpdatedMsisdn.toString());
            }
        } // end of finally
        return nonUpdatedMsisdn.toString();
    }

    /**
     * This method is used to load the subscribers details on the basis of
     * MSISDN and channel_user_id,
     * Method :loadSubcriberToDeassociateList
     *
     * @author ved prakash
     * @param p_con
     *            java.sql.Connection
     * @param p_scheduledList
     *            ArrayList
     * @param p_channelUserID
     *            String
     * @param p_invalidMsisdn
     *            StringBuffer
     * @return list ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadSubcriberToDeassociateList(Connection p_con, ArrayList p_scheduledList, String p_channelUserID) throws BTSLBaseException {
        final String methodName = "loadSubcriberToDeassociateList";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered : p_scheduledList.size() : ");
            msg.append(p_scheduledList.size());
            msg.append(", p_channelUserID : ");
            msg.append(p_channelUserID);

            String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSubList = null;
        ResultSet rs = null;
        ArrayList activeOrSuspenedList = null;
        ScheduleBatchDetailVO scheduleDetailVO = null;

        try {
            final StringBuffer strBuff = new StringBuffer("SELECT RM.msisdn, RM.employee_name, RM.employee_code, RM.min_txn_amount, RM.owner_id, RM.modified_on, ");
            strBuff.append("RM.max_txn_amount, RM.monthly_limit, RM.total_txn_count, RM.status, L.lookup_name status_desc, RM.created_on ");
            strBuff.append("FROM restricted_msisdns RM, lookups L ");
            strBuff.append("WHERE RM.msisdn = ? ");
            strBuff.append("AND RM.channel_user_id=? ");
            strBuff.append("AND L.lookup_code = RM.status ");
            strBuff.append("AND L.lookup_type = ? ");
            final String sqlSelect = strBuff.toString();
            pstmtSubList = p_con.prepareStatement(sqlSelect);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            activeOrSuspenedList = new ArrayList();
            for (int i = 0, j = p_scheduledList.size(); i < j; i++) {
                scheduleDetailVO = (ScheduleBatchDetailVO) p_scheduledList.get(i);
                pstmtSubList.setString(1, scheduleDetailVO.getMsisdn());
                pstmtSubList.setString(2, p_channelUserID);
                pstmtSubList.setString(3, PretupsI.LOOKUP_TYPE_RES_MSISDN_STATUS);
                rs = pstmtSubList.executeQuery();
                if (rs.next()) {
                    scheduleDetailVO.setMsisdn(rs.getString("msisdn"));
                    scheduleDetailVO.setEmployeeName(rs.getString("employee_name"));
                    scheduleDetailVO.setEmployeeCode(rs.getString("employee_code"));
                    scheduleDetailVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("min_txn_amount")));
                    scheduleDetailVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("max_txn_amount")));
                    scheduleDetailVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(rs.getLong("monthly_limit")));
                    scheduleDetailVO.setTotalTxnCountForDisp(PretupsBL.getDisplayAmount(rs.getLong("total_txn_count")));
                    scheduleDetailVO.setStatus(rs.getString("status"));
                    scheduleDetailVO.setStatusDes(rs.getString("status_desc"));
                    scheduleDetailVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("created_on")));
                    scheduleDetailVO.setOwnerID(rs.getString("owner_id"));
                    scheduleDetailVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                    activeOrSuspenedList.add(scheduleDetailVO);
                }
                pstmtSubList.clearParameters();
                rs.close();
            } // end of for loop
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubcriberToDeassociateList]", "",
                    "", "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberDAO[loadSubcriberToDeassociateList]", "",
                    "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "List Size" + activeOrSuspenedList.size());
            }
        }
        return activeOrSuspenedList;
    }

    /**
     * This method is used to load the details of schedules according t the
     * mobile numbers entered
     * against the seleted channel user
     *
     * @param p_con
     * @param p_batchDtlsStatus
     * @param p_msisdn
     * @param p_batchID
     * @param p_isRestricted
     *            TODO
     * @param p_batchStatus
     * @param p_restrictedMsisdnStatus
     * @return ArrayList
     * @throws BTSLBaseException
     *             ArrayList
     *             modify method signature add boolean p_isRestricted and modify
     *             query according to p_isRestricted
     */
    public ArrayList loadDetailsForCancelSingle(Connection p_con, String p_batchDtlsStatus, String p_msisdn, String p_batchID, boolean p_isRestricted, String p_userID) throws BTSLBaseException {
        final String methodName = "loadDetailsForCancelSingle";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered: p_batchDtlsStatus = ");
            msg.append(p_batchDtlsStatus);
            msg.append(", p_msisdn = ");
            msg.append(p_msisdn);
            msg.append(", p_batchID = ");
            msg.append(p_batchID);
            msg.append(", p_isRestricted = ");
            msg.append(p_isRestricted);
            msg.append(", p_userID = ");
            msg.append(p_userID);

            String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSubList = null;
        ResultSet rs = null;
        final ArrayList cancelList = new ArrayList();
        final ArrayList scheduleMasterVOList = new ArrayList();
        ScheduleBatchDetailVO scheduleDetailVO = null;
        ScheduleBatchMasterVO scheduleMasterVO = null;
        try {
            StringBuffer strBuff = null;
            String [] args = p_msisdn.split(",");
            if (p_isRestricted) {
                strBuff = new StringBuffer(" SELECT sbm.batch_id,rm.msisdn,rm.employee_code,rm.employee_name,rm.monthly_limit,");
                strBuff.append(" rm.min_txn_amount,rm.max_txn_amount,rm.total_txn_amount,sbm.service_type stype,");
                strBuff
                        .append(" sbd.amount,sbm.scheduled_date,sbd.transfer_status,u.user_name, lk.lookup_name status,sbd.modified_on, sbd.sub_service, U2.user_name active_user_name ");
                strBuff.append(" FROM restricted_msisdns rm,scheduled_batch_master sbm,scheduled_batch_detail sbd, users u, lookups lk, Users U2 ");
                strBuff.append(" WHERE sbd.batch_id=sbm.batch_id AND sbd.status=?  AND sbm.owner_id=rm.owner_id ");
                strBuff.append(" AND rm.msisdn=sbd.msisdn AND rm.msisdn in");
                BTSLUtil.pstmtForInQuery(args, strBuff);
                strBuff
                        .append(" AND sbm.batch_id=? AND u.user_id=sbm.initiated_by AND sbd.status = lk.lookup_code AND lk.lookup_type = ? AND rm.status = ?  AND rm.restricted_type = ? AND ( rm.associated_by =? OR sbm.parent_id =?) AND U2.user_id=sbm.active_user_id ");
            } else {
                strBuff = new StringBuffer("SELECT sbm.batch_id,sbd.msisdn,sbm.service_type stype, sbd.amount,");
                strBuff.append("sbm.scheduled_date,sbd.transfer_status, u.user_name, lk.lookup_name status,sbd.modified_on, sbd.sub_service, U2.user_name active_user_name ");
                strBuff.append("FROM scheduled_batch_master sbm,scheduled_batch_detail sbd, users u, lookups lk, Users U2 ");
                strBuff.append("WHERE sbd.batch_id=sbm.batch_id   AND sbd.status=? AND sbd.msisdn in");
                BTSLUtil.pstmtForInQuery(args, strBuff);
                strBuff.append("AND sbm.batch_id=? AND u.user_id=sbm.initiated_by AND sbd.status = lk.lookup_code AND lk.lookup_type = ?  AND U2.user_id=sbm.active_user_id ");
            }

            final String sqlSelect = strBuff.toString();
            pstmtSubList = p_con.prepareStatement(sqlSelect);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            int i = 1;
            pstmtSubList.setString(i++, p_batchDtlsStatus);
            for(int j=0;j<args.length;j++)
            {
                String param = args[j];
                param = param.replace("'", "");
                pstmtSubList.setString(i++, param);
            }
            pstmtSubList.setString(i++, p_batchID);
            pstmtSubList.setString(i++, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
            if (p_isRestricted) {
                pstmtSubList.setString(i++, PretupsI.YES);
                pstmtSubList.setString(i++, PretupsI.DEFAULT_RESTRICTED_TYPE);
                pstmtSubList.setString(i++, p_userID);
                pstmtSubList.setString(i++, p_userID);

            }
            rs = pstmtSubList.executeQuery();
            while (rs.next()) {
                scheduleDetailVO = new ScheduleBatchDetailVO();
                scheduleMasterVO = new ScheduleBatchMasterVO();
                if (p_isRestricted) {
                    scheduleDetailVO.setEmployeeCode(rs.getString("employee_code"));
                    scheduleDetailVO.setEmployeeName(rs.getString("employee_name"));
                    scheduleDetailVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(rs.getLong("monthly_limit")));
                    scheduleDetailVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("min_txn_amount")));
                    scheduleDetailVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("max_txn_amount")));
                    scheduleDetailVO.setTotalTransferAmountForDisp(PretupsBL.getDisplayAmount(rs.getLong("total_txn_amount")));
                }
                scheduleDetailVO.setBatchID(rs.getString("batch_id"));
                scheduleDetailVO.setMsisdn(rs.getString("msisdn"));
                scheduleDetailVO.setAmountForDisp(PretupsBL.getDisplayAmount(rs.getLong("amount")));
                scheduleDetailVO.setSubService(rs.getString("sub_service"));
                scheduleDetailVO.setSubServiceDesc(PretupsBL.getSelectorDescriptionFromCode(rs.getString("stype") + "_" + scheduleDetailVO.getSubService()));
                scheduleDetailVO.setStatus(rs.getString("status"));
                scheduleDetailVO.setTransactionStatus(rs.getString("transfer_status"));
                scheduleDetailVO.setCreatedBy(rs.getString("user_name"));
                scheduleDetailVO.setModifiedOn(rs.getTimestamp("modified_on"));
                scheduleDetailVO.setActiveUserName(rs.getString("active_user_name"));
                scheduleMasterVO.setScheduledDate(rs.getDate("scheduled_date"));

                cancelList.add(scheduleDetailVO);
                scheduleMasterVO.setActiveUserName(rs.getString("active_user_name"));
                scheduleMasterVO.setList(cancelList);
                scheduleMasterVOList.add(scheduleMasterVO);
            }// end of while loop
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[loadDetailsForCancelSingle]", "", "", "", " SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[loadDetailsForCancelSingle]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "List Size" + cancelList.size());
            }
        }
        return scheduleMasterVOList;
    }

    /**
     * This method is used to update the Status as 'A-Approved' whose status is
     * 'W-NEW'.
     * This is also take care about the modification of records by some one else
     * while updating the records.
     * All records selected for approval are updated with status 'A-Approved'
     * and Recjected Records are deleted.
     * The records that are selected for discard has no action.
     *
     * @param Connection
     *            p_con
     * @param ArrayList
     *            p_confirmSubsAppList
     * @return int
     * @throws BTSLBaseException
     */

    public int updateSubsListForApproval(Connection p_con, ArrayList p_confirmSubsAppList) throws BTSLBaseException {
        final String methodName = "updateSubsListForApproval";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_confirmSubsAppList.size()=" + p_confirmSubsAppList.size());
        }
        PreparedStatement pstmtUpdate = null;

        PreparedStatement pstmtDelete = null;

        RestrictedSubscriberVO restrictedSubsVO = null;
        final ArrayList approveList = new ArrayList();
        final ArrayList rejectedList = new ArrayList();
        int updateCount = 0;
        int deleteCount = 0;
        final ResultSet rs = null;

        final java.sql.Timestamp newlastModified = null;
        try {
            // Iterating the confirmSubsAppList that contains all the records
            // with status 'A','R' and 'D'
            // Records with status 'A' has to be updated in restricted_msisdns
            // Records with status 'R' has to be deleted from the
            // restricted_msisdns,and Triger is responsible to move these
            // records in History table.
            // Records with status 'D' has no action
            // Stored all the records whose status is 'A' into approveList and
            // 'R' to rejectList.
            int confirmListSize = p_confirmSubsAppList.size();
            for (int index = 0, size = confirmListSize; index < size; index++) {
                restrictedSubsVO = (RestrictedSubscriberVO) p_confirmSubsAppList.get(index);
                if (restrictedSubsVO.getStatus().equalsIgnoreCase(PretupsI.RES_MSISDN_STATUS_APPROVED)) {
                    approveList.add(restrictedSubsVO);
                } else if (restrictedSubsVO.getStatus().equalsIgnoreCase(PretupsI.RES_MSISDN_STATUS_REJECT)) {
                    rejectedList.add(restrictedSubsVO);
                }
            }
            final StringBuffer updateQuery = new StringBuffer("UPDATE restricted_msisdns SET status=?, approved_by=?,approved_on=?,modified_on=?,modified_by=?");
            updateQuery.append(" WHERE owner_id=? AND msisdn=? AND status=?");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY updateQuery:" + updateQuery);
            }
            final String sqlDelete = "DELETE FROM restricted_msisdns WHERE owner_id= ? AND msisdn=? ";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlDelete=" + sqlDelete);
            }
            pstmtDelete = p_con.prepareStatement(sqlDelete);
            int dcount = 0;
            pstmtUpdate = p_con.prepareStatement(updateQuery.toString());
            int tempCount = 0;
            // While updating,check the STATUS AS 'W-NEW' and then update.if the
            // status is changed after loading the list and before
            // updation,throw exception.
            int approveListSize = approveList.size();
            for (int index = 0, j = approveListSize; index < j; index++) {
                restrictedSubsVO = (RestrictedSubscriberVO) approveList.get(index);
                restrictedSubsVO.setApprovedOn(new Date());
                restrictedSubsVO.setModifiedOn(new Date());
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "restrictedSubsVO:" + restrictedSubsVO);
                }
                int i = 1;
                pstmtUpdate.setString(i++, restrictedSubsVO.getStatus());
                pstmtUpdate.setString(i++, restrictedSubsVO.getApprovedBy());
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(restrictedSubsVO.getApprovedOn()));
                pstmtUpdate.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(restrictedSubsVO.getModifiedOn()));
                pstmtUpdate.setString(i++, restrictedSubsVO.getModifiedBy());
                pstmtUpdate.setString(i++, restrictedSubsVO.getOwnerID());
                pstmtUpdate.setString(i++, restrictedSubsVO.getMsisdn());
                pstmtUpdate.setString(i++, PretupsI.RES_MSISDN_STATUS_NEW);
                tempCount = pstmtUpdate.executeUpdate();
                if (tempCount == 0) {
                    throw new BTSLBaseException(this, methodName, "error.modify.true");
                }
                updateCount += tempCount;
                pstmtUpdate.clearParameters();
            }// end of for loop of approve list
            // Delete all the records that are selected as rejected.Trigger is
            // responsible to move all deleted records into restricted
            // RESTRICTED_MSISDNS_HISTORY table.
            int rejectedListSize = rejectedList.size();
            for (int delete = 0; delete < rejectedListSize; delete++) {
                restrictedSubsVO = (RestrictedSubscriberVO) rejectedList.get(delete);
                pstmtDelete.setString(1, restrictedSubsVO.getOwnerID());
                pstmtDelete.setString(2, restrictedSubsVO.getMsisdn());
                dcount = pstmtDelete.executeUpdate();
                if (dcount == 0) {
                    throw new BTSLBaseException(this, methodName, "error.modify.true");
                }
                pstmtDelete.clearParameters();
                deleteCount += dcount;
            }// end of for loop of reject list
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        }// end of catch-BTSLBaseException
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[updateSubsListForApproval]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch-SQLException
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[updateSubsListForApproval]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch-Exception
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtDelete != null) {
                    pstmtDelete.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting :updateCount+deleteCount =" + updateCount + deleteCount + "updateCount =" + updateCount + " deletecount =" + deleteCount);
            }
        }// end of finally
        return updateCount + deleteCount;
    }// end of updateSubsListForApproval

    /**
     * This method is used to Load the details from the restricted_msisdns for
     * approval
     *
     * @param Connection
     *            p_con
     * @param String
     *            p_ownerID
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadSubsDetailForApproval(Connection p_con, String p_ownerID) throws BTSLBaseException {
        final String methodName = "loadSubsDetailForApproval";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_ownerID =" + p_ownerID);
        }
        final ArrayList p_subsDetailList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT rm.msisdn,rm.subscriber_id,rm.channel_user_id,rm.channel_user_category,rm.employee_code,rm.owner_id,");
        strBuff.append("rm.employee_name,rm.network_code,rm.min_txn_amount,rm.max_txn_amount,rm.monthly_limit,rm.association_date,");
        strBuff.append("rm.total_txn_count,rm.total_txn_amount,rm.black_list_status,rm.remark,rm.approved_by,rm.language,");
        strBuff.append("rm.approved_on,rm.associated_by,rm.status, l.lookup_name status_desc, rm.created_on,rm.created_by,rm.modified_on,rm.modified_by,");
        strBuff.append("rm.subscriber_type,rm.country ");
        strBuff.append(" FROM restricted_msisdns rm, lookups l");
        strBuff.append(" WHERE rm.owner_id=? AND rm.status= ? AND rm.restricted_type = ? ");
        strBuff.append(" AND l.lookup_code = rm.status ");
        strBuff.append(" AND l.lookup_type =? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, p_ownerID);
            pstmtSelect.setString(i++, PretupsI.RES_MSISDN_STATUS_NEW);
            pstmtSelect.setString(i++, PretupsI.DEFAULT_RESTRICTED_TYPE);
            pstmtSelect.setString(i++, PretupsI.LOOKUP_TYPE_RES_MSISDN_STATUS);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                restrictedSubscriberVO = new RestrictedSubscriberVO();
                restrictedSubscriberVO.setMsisdn(rs.getString("msisdn"));
                restrictedSubscriberVO.setSubscriberID(rs.getString("subscriber_id"));
                restrictedSubscriberVO.setChannelUserID(rs.getString("channel_user_id"));
                restrictedSubscriberVO.setChannelUserCategory(rs.getString("channel_user_category"));
                restrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                restrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                restrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                restrictedSubscriberVO.setNetworkCode(rs.getString("network_code"));
                restrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                restrictedSubscriberVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                restrictedSubscriberVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                restrictedSubscriberVO.setStatusDes(rs.getString("status_desc"));
                restrictedSubscriberVO.setStatus(rs.getString("status"));
                restrictedSubscriberVO.setTempStatus(rs.getString("status"));
                restrictedSubscriberVO.setCreatedOn(rs.getTimestamp("created_on"));
                restrictedSubscriberVO.setCreatedOnAsString(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(restrictedSubscriberVO.getCreatedOn())));
                restrictedSubscriberVO.setCreatedBy(rs.getString("created_by"));
                restrictedSubscriberVO.setModifiedOn(rs.getTimestamp("modified_on"));
                restrictedSubscriberVO.setModifiedBy(rs.getString("modified_by"));
                restrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                restrictedSubscriberVO.setLanguage(rs.getString("language"));
                restrictedSubscriberVO.setCountry(rs.getString("country"));
                p_subsDetailList.add(restrictedSubscriberVO);
            }// end of while

        }// end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[loadSubsDetailForApproval]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch-SQLException
        catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[loadSubsDetailForApproval]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch-Exception
        finally {

            _log.debug(methodName, "p_subsDetailList.size()=:" + p_subsDetailList.size());
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
        }// end of finally

        return p_subsDetailList;
    }// end of loadSubsDetailForApproval

    /**
     * This method is used to associate restricted msisdn
     * Method :associateRestrictedMsisdn
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_restrictedList
     *            ArrayList
     * @return String
     * @throws BTSLBaseException
     */
    public String associateRestrictedMsisdn(Connection p_con, ArrayList p_restrictedList) throws BTSLBaseException {
        final String methodName = "associateRestrictedMsisdn";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: ArrayList" + p_restrictedList);
        }
        PreparedStatement pstmtUpdate = null;
        final ResultSet rs = null;
        int updateCount = 0;
        String unprocessedMsisdn = null;
        final StringBuffer invalidDataStrBuff = new StringBuffer();
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" UPDATE restricted_msisdns ");
        strBuff.append(" SET channel_user_id=? , channel_user_category=? , ");
        strBuff.append(" employee_code=? , employee_name=? , network_code=? , monthly_limit=? ,  ");
        strBuff.append(" min_txn_amount=? , max_txn_amount=?  , associated_by=? , status=? , association_date=? ,  ");
        strBuff.append(" modified_on=? , modified_by=? , language=? , country=?  ");
        strBuff.append(" WHERE msisdn=? AND owner_id=? AND status=? ");

        final String sqlUpdate = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlUpdate=" + sqlUpdate);
        }
        try {
            pstmtUpdate = p_con.prepareStatement(sqlUpdate);
            final int indexsize = p_restrictedList.size();
            int i = 0;
            for (int index = 0; index < indexsize; index++) {
                restrictedSubscriberVO = (RestrictedSubscriberVO) p_restrictedList.get(index);
                pstmtUpdate.setString(++i, restrictedSubscriberVO.getChannelUserID());
                pstmtUpdate.setString(++i, restrictedSubscriberVO.getChannelUserCategory());
                pstmtUpdate.setString(++i, restrictedSubscriberVO.getEmployeeCode());
                pstmtUpdate.setString(++i, restrictedSubscriberVO.getEmployeeName());
                pstmtUpdate.setString(++i, restrictedSubscriberVO.getNetworkCode());
                pstmtUpdate.setLong(++i, restrictedSubscriberVO.getMonthlyLimit());
                pstmtUpdate.setLong(++i, restrictedSubscriberVO.getMinTxnAmount());
                pstmtUpdate.setLong(++i, restrictedSubscriberVO.getMaxTxnAmount());
                pstmtUpdate.setString(++i, restrictedSubscriberVO.getAssociatedBy());
                pstmtUpdate.setString(++i, restrictedSubscriberVO.getStatus());
                pstmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(restrictedSubscriberVO.getAssociationDate()));
                pstmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(restrictedSubscriberVO.getModifiedOn()));
                pstmtUpdate.setString(++i, restrictedSubscriberVO.getModifiedBy());
                pstmtUpdate.setString(++i, restrictedSubscriberVO.getLanguage());
                pstmtUpdate.setString(++i, restrictedSubscriberVO.getCountry());
                pstmtUpdate.setString(++i, restrictedSubscriberVO.getMsisdn());
                pstmtUpdate.setString(++i, restrictedSubscriberVO.getOwnerID());
                pstmtUpdate.setString(++i, PretupsI.RES_MSISDN_STATUS_APPROVED);
                i = 0;
                updateCount = pstmtUpdate.executeUpdate();
                if (updateCount <= 0) {
                    invalidDataStrBuff.append(restrictedSubscriberVO.getMsisdn() + ",");
                } else {
                    AssociateMsisdnFileProcessLog.log("Associate Restricted Msisdn", restrictedSubscriberVO.getMsisdn(), "MSISDN associated successfully", "PASS", "");
                }
                pstmtUpdate.clearParameters();
            }
            if (invalidDataStrBuff.length() > 0) {
                unprocessedMsisdn = invalidDataStrBuff.substring(0, invalidDataStrBuff.length() - 1);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[associateRestrictedMsisdn]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[associateRestrictedMsisdn]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: unprocessedMsisdn=" + unprocessedMsisdn);
            }
        }
        return unprocessedMsisdn;
    }

    /**
     * Method isSubscriberExistByChannelID.
     * This method is to check that the subscriber is exist of the passed status
     * under the passed channel_user_id, owner_id, and msisdn
     *
     * @param p_con
     *            Connection
     * @param p_channel_id
     *            String
     * @param p_owner_id
     *            String
     * @param p_subscriberList
     *            ArrayList
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * @param p_subscriberType
     *            String
     * @return String
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public String isSubscriberExistByChannelID(Connection p_con, String p_channel_id, String p_owner_id, ArrayList p_subscriberList, String p_statusUsed, String p_status, String p_subscriberType) throws BTSLBaseException {
        final String methodName = "isSubscriberExistByChannelID";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered: p_channel_id = ");
            msg.append(p_channel_id);
            msg.append(", p_owner_id = ");
            msg.append(p_owner_id);
            msg.append(", p_subscriberList size = ");
            msg.append(p_subscriberList.size());
            msg.append(", p_status = ");
            msg.append(p_status);
            msg.append(", p_statusUsed = ");
            msg.append(p_statusUsed);
            msg.append(", p_subscriberType = ");
            msg.append(p_subscriberType);

            String message=msg.toString();
            _log.debug(methodName, message);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer returnDataStrBuff = new StringBuffer();
        String returnStr = null;
        final StringBuffer strBuff = new StringBuffer("SELECT 1 FROM restricted_msisdns WHERE channel_user_id =? AND owner_id = ? AND msisdn = ? ");

        if (p_subscriberType != null) {
            strBuff.append("AND subscriber_type=? ");
        }
        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND status IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append("AND status <> ? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND status NOT IN (" + p_status + ")");
        }

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + strBuff);
        }
        try {
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            RestrictedSubscriberVO restrictedSubscriberVO = null;
            for (int index = 0, j = p_subscriberList.size(); index < j; index++) {
                restrictedSubscriberVO = (RestrictedSubscriberVO) p_subscriberList.get(index);
                int i = 1;
                pstmtSelect.setString(i++, p_channel_id);
                pstmtSelect.setString(i++, p_owner_id);
                pstmtSelect.setString(i++, restrictedSubscriberVO.getMsisdn());
                if (p_subscriberType != null) {
                    pstmtSelect.setString(i++, p_subscriberType);
                }
                if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                    pstmtSelect.setString(i++, p_status);
                }
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                    returnDataStrBuff.append(restrictedSubscriberVO.getMsisdn() + ",");
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                pstmtSelect.clearParameters();
            }
            if (returnDataStrBuff.length() > 0) {
                returnStr = returnDataStrBuff.substring(0, returnDataStrBuff.length() - 1);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[isSubscriberExistByChannelID]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[isSubscriberExistByChannelID]", "", "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting returnStr=" + returnStr);
            }
        }
        return returnStr;
    }

    /**
     * This method is used to load msisdn list that has status 'A'
     *
     * Method :loadApprovedMsisdnList
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_subscriberType
     *            String
     * @return list String
     * @throws BTSLBaseException
     */
    public String loadApprovedMsisdnList(Connection p_con, String p_subscriberType, String p_ownerID) throws BTSLBaseException {
        final String methodName = "loadApprovedMsisdnList";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered: p_subscriberType = ");
            msg.append(p_subscriberType);
            msg.append(", p_ownerid = ");
            msg.append(p_ownerID);

            String message=msg.toString();
            _log.debug(methodName, message);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String msisdnStr = null;
        final StringBuffer approvedDataStrBuff = new StringBuffer();
        try {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append(" SELECT msisdn ");
            strBuff.append(" FROM restricted_msisdns ");
            strBuff.append(" WHERE status = ? AND subscriber_type=? AND owner_id=? AND restricted_type = ?");
            final String sqlSelect = strBuff.toString();
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.RES_MSISDN_STATUS_APPROVED);
            pstmtSelect.setString(2, p_subscriberType);
            pstmtSelect.setString(3, p_ownerID);
            pstmtSelect.setString(4, PretupsI.DEFAULT_RESTRICTED_TYPE);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                approvedDataStrBuff.append(rs.getString("msisdn") + ",");
            }
            if (approvedDataStrBuff.length() > 0) {
                msisdnStr = approvedDataStrBuff.substring(0, approvedDataStrBuff.length() - 1);
            }

        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[loadApprovedMsisdnList]",
                    "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[loadApprovedMsisdnList]",
                    "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
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
                _log.debug(methodName, "Exiting: msisdnStr =" + msisdnStr);
            }
        }
        return msisdnStr;
    }

    /**
     * Method blackListSubscriberBulk
     * This Method is used for Black Listing the subsribers in bulk
     *
     * @author Amit Ruwali
     * @param p_con
     *            Connection
     * @param p_msisdnList
     *            ArrayList
     * @param p_userId
     *            String
     * @return invalidMsisdn String
     * @throws BTSLBaseException
     */

    public ArrayList blackListSubscriberBulk(Connection p_con, ArrayList p_msisdnList, String p_userId, Date p_modifiedOn, String p_modifiedBy, String p_fileName, String p_userName, String p_msisdnNotExists, String p_alreadyBlackList, String p_cp2pPayer, String p_cp2pPayee, String p_c2sPayee) throws BTSLBaseException {
        final String methodName = "blackListSubscriberBulk";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered: p_msisdnList size() = ");
            msg.append(p_msisdnList.size());
            msg.append(", p_userId = ");
            msg.append(p_userId);
            msg.append(", p_fileName = ");
            msg.append(p_fileName);
            msg.append(", p_userName = ");
            msg.append(p_userName);
            msg.append(", p_msisdnNotExists = ");
            msg.append(p_msisdnNotExists);
            msg.append(", p_alreadyBlackList = ");
            msg.append(p_alreadyBlackList);
            msg.append(", p_cp2pPayer= ");
            msg.append(p_cp2pPayer);
            msg.append(", p_cp2pPayee= ");
            msg.append(p_cp2pPayee);
            msg.append(", p_c2sPayee= ");
            msg.append(p_c2sPayee);

            String message=msg.toString();
            _log.debug(methodName,message);
        }

        PreparedStatement pstmtIsMsisdnExist = null;
        PreparedStatement psmtUpdate = null;
        ResultSet rsIsMsisdnExist = null;
        int updateCount = 0;
        String msisdn;
        long failCount = 0;
        RestrictedSubscriberVO errVO = null;
        try {
            // check whether subscriber msisdn exists under the owner user
            final String isMsisdnExistQuery = "SELECT 1 FROM restricted_msisdns WHERE msisdn=? AND owner_id=?";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "isMsisdnExistQuery = " + isMsisdnExistQuery);
            }
            pstmtIsMsisdnExist = p_con.prepareStatement(isMsisdnExistQuery.toString());

            // Update the black_list_status to "Y" if the subscriber details are
            // found and not blacklisted
            final StringBuffer strBuff = new StringBuffer("UPDATE restricted_msisdns set ");
            // addded for C2S Payee and Cp2P Payee Date 04/02/08
            // cp2p_payer,cp2p_payee,c2s_payee
            if (p_cp2pPayer != null) {
                strBuff.append(" black_list_status=?,");
            }
            if (p_cp2pPayee != null) {
                strBuff.append(" cp2p_payee_status=?,");
            }
            if (p_c2sPayee != null) {
                strBuff.append(" c2s_payee_status=?,");
            }
            strBuff.append(" modified_on=?,modified_by=? WHERE msisdn=? AND owner_id=?");
            /*
             * if(p_cp2pPayer!=null)
             * strBuff.append(" AND black_list_status NOT IN(?)");
             * if(p_cp2pPayee!=null)
             * strBuff.append(" AND cp2p_payee_status NOT IN(?)");
             * if(p_c2sPayee!=null)
             * strBuff.append(" AND c2s_payee_status NOT IN(?)");
             */
            // End of C2S Payee and Cp2P Payee
            final String isUpdateQuery = strBuff.toString();
            // "UPDATE restricted_msisdns set black_list_status=?,modified_on=?,modified_by=? WHERE msisdn=? AND owner_id=? AND black_list_status NOT IN(?)";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "isUpdateQuery = " + isUpdateQuery);
            }
            psmtUpdate = p_con.prepareStatement(isUpdateQuery.toString());

            for (int i = 0, k = 1, j = p_msisdnList.size(); i < j; i++) {
                errVO = (RestrictedSubscriberVO) p_msisdnList.get(i);
                msisdn = errVO.getMsisdn();
                if (errVO.getErrorCode() == null) {
                    pstmtIsMsisdnExist.setString(1, msisdn);
                    pstmtIsMsisdnExist.setString(2, p_userId);
                    rsIsMsisdnExist = pstmtIsMsisdnExist.executeQuery();
                    pstmtIsMsisdnExist.clearParameters();
                    if (!rsIsMsisdnExist.next()) // if false then mark error and
                    // continue to next msisdn
                    {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, " Mobile number not exists under the owner user=" + msisdn);
                        }
                        BlackListLog.log("BLACKLIST", p_fileName, msisdn, "Uploaded MSISDN not found in the Restricted MSISDN list of owner user " + p_userName, "Fail",
                                "Logged In UserID : " + p_modifiedBy);
                        errVO.setLineNumber((i + 1) + "");
                        errVO.setMsisdn(msisdn);
                        errVO.setErrorCode(p_msisdnNotExists + " " + p_userName);
                        failCount = failCount + 1;
                        errVO.setFailCount(failCount);
                        if (rsIsMsisdnExist != null) {
                            rsIsMsisdnExist.close();
                        }
                        continue;
                    } else {
                        if (rsIsMsisdnExist != null) {
                            rsIsMsisdnExist.close();
                        }
                    }
                    // Update the Black List Status if the Subscriber is not
                    // blackListed
                    // addded for C2S Payee and Cp2P Payee Date 04/02/08
                    // cp2p_payer,cp2p_payee,c2s_payee
                    k = 1;
                    if (p_cp2pPayer != null) {
                        psmtUpdate.setString(k++, p_cp2pPayer);
                    }
                    if (p_cp2pPayee != null) {
                        psmtUpdate.setString(k++, p_cp2pPayee);
                    }
                    if (p_c2sPayee != null) {
                        psmtUpdate.setString(k++, p_c2sPayee);
                    }
                    // End of C2S Payee and Cp2P Payee
                    psmtUpdate.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(p_modifiedOn));
                    psmtUpdate.setString(k++, p_modifiedBy);
                    psmtUpdate.setString(k++, msisdn);
                    psmtUpdate.setString(k++, p_userId);
                    // addded for C2S Payee and Cp2P Payee Date 04/02/08
                    // cp2p_payer,cp2p_payee,c2s_payee
                    /*
                     * if(p_cp2pPayer!=null)
                     * psmtUpdate.setString(k++,p_cp2pPayer);
                     * if(p_cp2pPayee!=null)
                     * psmtUpdate.setString(k++,p_cp2pPayee);
                     * if(p_c2sPayee!=null)
                     * psmtUpdate.setString(k++,p_c2sPayee);
                     */
                    // End of C2S Payee and Cp2P Payee
                    updateCount = psmtUpdate.executeUpdate();
                    psmtUpdate.clearParameters();
                    if (updateCount <= 0) // Means the subscriber is already
                    // black listed
                    {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, " Mobile number alreday Black Listed=" + msisdn);
                        }
                        BlackListLog.log("BLACKLIST", p_fileName, msisdn, "Msisdn already black listed under the owner user " + p_userName, "Fail",
                                "Logged In UserID : " + p_modifiedBy);
                        errVO.setLineNumber((i + 1) + "");
                        errVO.setMsisdn(msisdn);
                        errVO.setErrorCode(p_alreadyBlackList + " " + p_userName);
                        failCount = failCount + 1;
                        errVO.setFailCount(failCount);
                        p_con.rollback();
                        continue;
                    }
                    p_con.commit();
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, " Msisdn black listed successfully=" + msisdn);
                    }
                    BlackListLog.log("BLACKLIST", p_fileName, msisdn, "Msisdn black listed successfully ", "Success", "Logged In UserID : " + p_modifiedBy);
                }
            }
            errVO.setFailCount(failCount);
        }// end of try

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[blackListSubscriberBulk]",
                    "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[blackListSubscriberBulk]",
                    "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch

        finally {
            try {
                if (rsIsMsisdnExist != null) {
                    rsIsMsisdnExist.close();
                }
                if (pstmtIsMsisdnExist != null) {
                    pstmtIsMsisdnExist.close();
                }
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:failCount=" + failCount);
            }
        }// end of finally
        return p_msisdnList;
    }

    /**
     * Method loadResSubsDetails
     * Method for Extracting Corporate Subscriber Details.
     * This Method will Load the Restricted Subscriber Details according to
     * the msisdn and owner_id. or msisdn and channel_user_id or date rang. *
     *
     * @author Ved prakash
     * @param p_con
     *            Connection
     * @param p_status
     *            String
     * @param p_msisdn
     *            String
     * @param p_userID
     *            String
     * @param p_checkType
     *            boolean (if true then check condition owner_id else
     *            channel_user_id)
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadResSubsDetails(Connection p_con, String p_msisdn, String p_userID, boolean p_isOwnerID, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String methodName = "loadResSubsDetails";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered: p_msisdn = ");
            msg.append(p_msisdn);
            msg.append(", p_userId = ");
            msg.append(p_userID);
            msg.append(", p_isOwnerID = ");
            msg.append(p_isOwnerID);
            msg.append(", p_fromDate = ");
            msg.append(p_fromDate);
            msg.append(", p_toDate = ");
            msg.append(p_toDate);

            String message=msg.toString();
            _log.debug(methodName, message);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        String sqlSelect= restrictedSubscriberWebQry.loadResSubsDetails(p_isOwnerID,p_msisdn,p_fromDate,p_toDate);
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList restSubsList = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.LOOKUP_TYPE_RES_MSISDN_STATUS);
            pstmtSelect.setString(i++, PretupsI.DEFAULT_RESTRICTED_TYPE);
            pstmtSelect.setString(i++, PretupsI.LOOKUP_TYPE_BLACK_LIST_STATUS);
            if (!BTSLUtil.isNullString(p_msisdn)) {
                pstmtSelect.setString(i++, p_msisdn);
            }
            pstmtSelect.setString(i++, p_userID);
            if (p_fromDate != null && p_toDate != null)// date range check
            {
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
                pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            }
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                restrictedSubscriberVO = new RestrictedSubscriberVO();
                restrictedSubscriberVO.setMsisdn(rs.getString("msisdn"));
                restrictedSubscriberVO.setSubscriberID(rs.getString("subscriber_id"));
                restrictedSubscriberVO.setChannelUserID(rs.getString("channel_user_id"));
                restrictedSubscriberVO.setChannelUserCategory(rs.getString("channel_user_category"));
                restrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                restrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                restrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                restrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                restrictedSubscriberVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(restrictedSubscriberVO.getMonthlyLimit()));
                restrictedSubscriberVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                restrictedSubscriberVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(restrictedSubscriberVO.getMinTxnAmount()));
                restrictedSubscriberVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                restrictedSubscriberVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(restrictedSubscriberVO.getMaxTxnAmount()));
                restrictedSubscriberVO.setTotalTxnCount(rs.getLong("total_txn_count"));
                restrictedSubscriberVO.setTotalTransferAmount(rs.getLong("total_txn_amount"));
                restrictedSubscriberVO.setTotalTransferAmountForDisp(PretupsBL.getDisplayAmount(restrictedSubscriberVO.getTotalTransferAmount()));
                restrictedSubscriberVO.setApprovedOn(rs.getDate("approved_on"));
                restrictedSubscriberVO.setCreatedOnAsString(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromSQLDate(rs.getDate("created_on"))));
                restrictedSubscriberVO.setStatus(rs.getString("status"));
                restrictedSubscriberVO.setStatusDes(rs.getString("status_desc"));
                restrictedSubscriberVO.setBlackListStatus(rs.getString("black_list_status"));
                restrictedSubscriberVO.setBlackListStatusDesc(rs.getString("black_list_status_desc"));
                restrictedSubscriberVO.setRemarks(rs.getString("remark"));
                restrictedSubscriberVO.setApprovedBy(rs.getString("approved_by"));
                restrictedSubscriberVO.setAssociatedBy(rs.getString("associated_by"));
                restrictedSubscriberVO.setAssociationDate(rs.getDate("association_date"));
                restrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                restrictedSubscriberVO.setLanguage(rs.getString("language"));
                restrictedSubscriberVO.setCountry(rs.getString("country"));
                restSubsList.add(restrictedSubscriberVO);
            } // end of while(rs.next())
        } // end of try
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[loadResSubsDetails]", "",
                    "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch (SQLException sqe)
        catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[loadResSubsDetails]", "",
                    "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch (Exception ex)
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
                _log.debug(methodName, "Exiting restSubsList.size()= " + restSubsList.size());
            }
        }// end of finally
        return restSubsList;
    }// end of loadResSubsDetails

    /**
     * Method loadResSubsDetails
     * Method for Extracting Corporate Subscriber Details.
     * This Method will Load the Restricted Subscriber Details according to the
     * msisdn and owner_id.
     * (The final VO will contain only one record that conatins the
     * black_list_status either
     * it will be already black listed or not black listed->it will be checked
     * in Action Class
     *
     * @author Amit Ruwali
     * @param p_con
     *            Connection
     * @param p_status
     *            String
     * @param p_msisdn
     *            String
     * @param p_ownerID
     *            String
     * @return restrictedSubscriberVO RestrictedSubscriberVO
     * @throws BTSLBaseException
     */

    public RestrictedSubscriberVO loadResSubsDetails(Connection p_con, String p_msisdn, String p_ownerID) throws BTSLBaseException {
        final String methodName = "loadResSubsDetails";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered: p_msisdn = ");
            msg.append(p_msisdn);
            msg.append(", p_OwnerID = ");
            msg.append(p_ownerID);

            String message=msg.toString();
            _log.debug(methodName, message);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT R.msisdn,R.subscriber_id,R.channel_user_id,R.channel_user_category,R.employee_code,R.owner_id,");
        strBuff.append("R.employee_name,R.network_code,R.min_txn_amount,R.max_txn_amount,R.monthly_limit,R.association_date,");
        strBuff.append("R.total_txn_count,R.total_txn_amount,R.black_list_status,R.remark,R.approved_by,LK.lookup_name,");
        strBuff.append("R.approved_on,R.associated_by,R.status,R.created_on,R.created_by,R.modified_on,R.modified_by,");
        strBuff.append("R.subscriber_type,R.language,R.country, R.cp2p_payee_status, R.c2s_payee_status ");
        strBuff.append(" FROM restricted_msisdns R,lookups LK ");
        strBuff.append(" WHERE LK.lookup_code=R.status AND LK.lookup_type=? AND");
        strBuff.append(" R.msisdn=? AND R.owner_id=? AND R.restricted_type = ? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.LOOKUP_TYPE_RES_MSISDN_STATUS);
            pstmtSelect.setString(i++, p_msisdn);
            pstmtSelect.setString(i++, p_ownerID);
            pstmtSelect.setString(i++, PretupsI.DEFAULT_RESTRICTED_TYPE);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                restrictedSubscriberVO = new RestrictedSubscriberVO();
                restrictedSubscriberVO.setSubscriberID(rs.getString("subscriber_id"));
                restrictedSubscriberVO.setChannelUserID(rs.getString("channel_user_id"));
                restrictedSubscriberVO.setChannelUserCategory(rs.getString("channel_user_category"));
                restrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                restrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                restrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                restrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                restrictedSubscriberVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                restrictedSubscriberVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                restrictedSubscriberVO.setTotalTxnCount(rs.getLong("total_txn_count"));
                restrictedSubscriberVO.setTotalTransferAmount(rs.getLong("total_txn_amount"));
                restrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                restrictedSubscriberVO.setApprovedOn(rs.getDate("approved_on"));
                restrictedSubscriberVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on"))));
                restrictedSubscriberVO.setStatus(rs.getString("status"));
                restrictedSubscriberVO.setStatusDes(rs.getString("lookup_name"));
                restrictedSubscriberVO.setBlackListStatus(rs.getString("black_list_status"));
                // addded for C2S Payee and Cp2P Payee Date 04/02/08
                restrictedSubscriberVO.setC2sPayeeStatus(rs.getString("c2s_payee_status"));
                restrictedSubscriberVO.setCp2pPayeeStatus(rs.getString("cp2p_payee_status"));
                // end of C2S Payee and Cp2P Payee
                restrictedSubscriberVO.setRemarks(rs.getString("remark"));
                restrictedSubscriberVO.setApprovedBy(rs.getString("approved_by"));
                restrictedSubscriberVO.setAssociatedBy(rs.getString("associated_by"));
                restrictedSubscriberVO.setAssociationDate(rs.getDate("association_date"));
                restrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                restrictedSubscriberVO.setLanguage(rs.getString("language"));
                restrictedSubscriberVO.setCountry(rs.getString("country"));
                restrictedSubscriberVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[loadResSubsDetails]", "",
                    "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[loadResSubsDetails]", "",
                    "", "", "Exception:" + ex.getMessage());
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
                _log.debug(methodName, "Exiting RestrictedSubscriberVO=" + restrictedSubscriberVO);
            }
        }
        return restrictedSubscriberVO;
    }



    /**
     * Method for Updating the black list status of the subscribers
     * Method :unBlackSelSubs
     *
     * @param p_con
     *            Connection
     * @param p_updatedList
     *            ArrayList
     * @param p_modifiedBy
     *            String
     * @param p_modifiedOn
     *            Date
     * @return nonUpdatedMsisdn String
     * @throws BTSLBaseException
     */
    public String unBlackSelSubscriber(Connection p_con, ArrayList p_updatedList, String p_modifiedBy, Date p_modifiedOn, String p_ownerID, String p_cp2pPayer, String p_cp2pPayee, String p_c2sPayee) throws BTSLBaseException {
        final String methodName = "unBlackSelSubs";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered: = p_updatedList.size() = ");
            msg.append(p_updatedList.size());
            msg.append(", p_modifiedBy = ");
            msg.append(p_modifiedBy);
            msg.append(", p_modifiedOn = ");
            msg.append(p_modifiedOn);
            msg.append(", p_cp2pPayer= ");
            msg.append(p_cp2pPayer);
            msg.append(", p_cp2pPayee= ");
            msg.append(p_cp2pPayee);
            msg.append(", p_c2sPayee= ");
            msg.append(p_c2sPayee);

            String message=msg.toString();
            _log.debug(methodName,message);
        }
        PreparedStatement psmtUpdate = null;
        //RestrictedSubscriberVO restrictedSubscriberVO = null;
        RestrictedSubVO restrictedSubVO = null;
        int updateCount = 0;
        final StringBuffer nonUpdatedMsisdn = new StringBuffer();
        try {
            final StringBuffer strBuff = new StringBuffer("UPDATE restricted_msisdns SET ");
            if (p_cp2pPayer != null) {
                strBuff.append(" black_list_status = ?,");
            }
            if (p_cp2pPayee != null) {
                strBuff.append(" cp2p_payee_status=?,");
            }
            if (p_c2sPayee != null) {
                strBuff.append(" c2s_payee_status=?,");
            }
            strBuff.append(" modified_by = ?,modified_on = ? WHERE msisdn = ? AND owner_id=? ");
            final String strUpdate = strBuff.toString();
            psmtUpdate = p_con.prepareStatement(strUpdate);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query strUpdate:" + strUpdate);
            }
            for (int i = 0, k = 1, j = p_updatedList.size(); i < j; i++) {
                restrictedSubVO = (RestrictedSubVO) p_updatedList.get(i);
                k = 1;
                // addded for C2S Payee and Cp2P Payee Date 04/02/08
                if (p_cp2pPayer != null) {
                    psmtUpdate.setString(k++, p_cp2pPayer);
                }
                if (p_cp2pPayee != null) {
                    psmtUpdate.setString(k++, p_cp2pPayee);
                }
                if (p_c2sPayee != null) {
                    psmtUpdate.setString(k++, p_c2sPayee);
                }
                // end of C2S Payee and Cp2P Payee
                psmtUpdate.setString(k++, p_modifiedBy);
                psmtUpdate.setTimestamp(k++, BTSLUtil.getTimestampFromUtilDate(p_modifiedOn));
                psmtUpdate.setString(k++, restrictedSubVO.getMsisdn());
                psmtUpdate.setString(k++, p_ownerID);
                updateCount = psmtUpdate.executeUpdate();
                psmtUpdate.clearParameters();
                // If subsriber is not updated in database due to some problem
                // then Rollback,
                // otherwise Commit the transaction.
                if (updateCount <= 0) {
                    p_con.rollback();
                    // single line logger entry
                    BlackListLog.log("UNBLACKLIST", "Comma Seperated List", restrictedSubVO.getMsisdn(), "Error In Updation", "Fail",
                            "Logged In UserID : " + p_modifiedBy);
                    nonUpdatedMsisdn.append(restrictedSubVO.getMsisdn());
                    nonUpdatedMsisdn.append(",");
                    continue;
                } else {
                    p_con.commit();
                    BlackListLog.log("UNBLACKLIST", "Comma Seperated List", restrictedSubVO.getMsisdn(), "Subscriber UnBlack Listed Successfully", "Success",
                            "Logged In UserID : " + p_modifiedBy);
                }
            }
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[unBlackSelSubs]", "", "",
                    "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberWebDAO[unBlackSelSubs]", "", "",
                    "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting :: nonUpdatedMsisdn.toString() :" + nonUpdatedMsisdn.toString());
            }
        } // end of finally
        return nonUpdatedMsisdn.toString();
    }


    /**
     * Method loadSubcriberListForUnblack
     * Method for Extracting Corporate Subscriber Details.
     * This Method will Load the Restricted Subscriber Details according to the
     * msisdn and owner_id.
     *
     * @author Amit Ruwali
     * @param p_con
     *            Connection
     * @param p_status
     *            String
     * @param p_msisdn
     *            String
     * @param p_userID
     *            String
     * @return restrictedSubscriberVO RestrictedSubscriberVO
     * @throws BTSLBaseException
     */

    public ArrayList loadSubscribersListForUnblack(Connection p_con, ArrayList p_msisdnList, ArrayList p_errorList, String p_userID, String p_fwdPath, String p_loginUserID, String p_cp2pPayer, String p_cp2pPayee, String p_c2sPayee,LoadSubscriberListForUnBlackResponseVO response,Locale locale) throws BTSLBaseException {
        final String methodName = "loadSubcriberListForUnblack";
        if (_log.isDebugEnabled()) {
            StringBuffer msg=new StringBuffer("");
            msg.append("Entered: p_userID= ");
            msg.append(p_userID);
            msg.append(", p_loginUserID= ");
            msg.append(p_loginUserID);
            msg.append(", p_cp2pPayer= ");
            msg.append(p_cp2pPayer);
            msg.append(", p_cp2pPayee= ");
            msg.append(p_cp2pPayee);
            msg.append(", p_c2sPayee= ");
            msg.append(p_c2sPayee);

            String message=msg.toString();
            _log.debug(methodName,message);
        }
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        PreparedStatement pstmtSubList = null;
        ResultSet rs = null;
        KeyArgumentVO keyArgumentVO = null;
        String status;
        String msisdn;
        final ArrayList subsList = new ArrayList();
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT r.msisdn,r.subscriber_id,r.channel_user_id,r.channel_user_category,r.employee_code,r.owner_id,");
        strBuff.append("r.employee_name,r.network_code,r.min_txn_amount,r.max_txn_amount,r.monthly_limit,r.association_date,");
        strBuff.append("r.total_txn_count,r.total_txn_amount,r.black_list_status,r.cp2p_payee_status, r.c2s_payee_status,r.remark,r.approved_by,lk.lookup_name,");
        strBuff.append("r.approved_on,r.associated_by,r.status,r.created_on,r.created_by,r.modified_on,r.modified_by,");
        strBuff.append("r.subscriber_type,r.language,r.country ");
        strBuff.append(" FROM restricted_msisdns r,lookups lk ");
        strBuff.append(" WHERE lk.lookup_code=r.status AND lk.lookup_type=? AND");
        strBuff.append(" r.msisdn=? AND r.owner_id=? AND r.restricted_type = ? ORDER BY r.employee_name");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSubList = p_con.prepareStatement(sqlSelect);
            for (int i = 0, j = p_msisdnList.size(); i < j; i++) {
                msisdn = (String) p_msisdnList.get(i);
                keyArgumentVO = new KeyArgumentVO();
                final String[] msisdnArr = new String[1];
                msisdnArr[0] = msisdn;

                pstmtSubList.setString(1, PretupsI.LOOKUP_TYPE_RES_MSISDN_STATUS);
                pstmtSubList.setString(2, msisdn);
                pstmtSubList.setString(3, p_userID);
                pstmtSubList.setString(4, PretupsI.DEFAULT_RESTRICTED_TYPE);
                rs = pstmtSubList.executeQuery();
                pstmtSubList.clearParameters();
                if (rs.next()) {
                    // addded for C2S Payee and CP2P Payee Date 04/02/08
                    if (p_cp2pPayer != null) {
                        status = rs.getString("black_list_status");
                        if (status.equals(PretupsI.RES_MSISDN_UNBLACKLIST_STATUS)) {
                            BlackListLog.log("UNBLACKLIST", "Comma Seperated List", msisdn, "Subscriber already Unblack Listed as a CP2P Payer", "Fail",
                                    "Logged In UserID : " + p_loginUserID);
                            keyArgumentVO.setArguments(msisdnArr);
                            //keyArgumentVO.setKey("restrictedsubs.loadsubscriberlistforunblack.err.msg.alreadyunblackcp2ppayer");
                            keyArgumentVO.setKey(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ALREADY_UNBLACK_CP2P_PAYER, keyArgumentVO.getArguments()));
                            p_errorList.add(keyArgumentVO);
                            continue;
                        }
                    }
                    if (p_cp2pPayee != null) {
                        status = rs.getString("cp2p_payee_status");
                        if (status.equals(PretupsI.RES_MSISDN_UNBLACKLIST_STATUS)) {
                            BlackListLog.log("UNBLACKLIST", "Comma Seperated List", msisdn, "Subscriber already Unblack Listed as a CP2P Payee", "Fail",
                                    "Logged In UserID : " + p_loginUserID);
                            keyArgumentVO.setArguments(msisdnArr);
                            //keyArgumentVO.setKey("restrictedsubs.loadsubscriberlistforunblack.err.msg.alreadyunblackcp2ppayee");
                            keyArgumentVO.setKey(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ALREADY_UNBLACK_CP2P_PAYEE, keyArgumentVO.getArguments()));
                            p_errorList.add(keyArgumentVO);
                            continue;
                        }
                    }
                    if (p_c2sPayee != null) {
                        status = rs.getString("c2s_payee_status");
                        if (status.equals(PretupsI.RES_MSISDN_UNBLACKLIST_STATUS)) {
                            BlackListLog.log("UNBLACKLIST", "Comma Seperated List", msisdn, "Subscriber already Unblack Listed as a C2S Payee", "Fail",
                                    "Logged In UserID : " + p_loginUserID);
                            keyArgumentVO.setArguments(msisdnArr);
                            //keyArgumentVO.setKey("restrictedsubs.loadsubscriberlistforunblack.err.msg.alreadyunblackc2spayee");
                            keyArgumentVO.setKey(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ALREADY_UNBLACK_C2S_PAYEE, keyArgumentVO.getArguments()));
                            p_errorList.add(keyArgumentVO);
                            continue;
                        }
                    }
                    // end of C2S Payee and Cp2P Payee
                    restrictedSubscriberVO = new RestrictedSubscriberVO();
                    restrictedSubscriberVO.setSubscriberID(rs.getString("subscriber_id"));
                    restrictedSubscriberVO.setChannelUserID(rs.getString("channel_user_id"));
                    restrictedSubscriberVO.setChannelUserCategory(rs.getString("channel_user_category"));
                    restrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                    restrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                    restrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                    restrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                    restrictedSubscriberVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                    restrictedSubscriberVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                    restrictedSubscriberVO.setTotalTxnCount(rs.getLong("total_txn_count"));
                    restrictedSubscriberVO.setTotalTransferAmount(rs.getLong("total_txn_amount"));
                    restrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                    restrictedSubscriberVO.setApprovedOn(rs.getDate("approved_on"));
                    restrictedSubscriberVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on"))));
                    restrictedSubscriberVO.setStatus(rs.getString("status"));
                    restrictedSubscriberVO.setStatusDes(rs.getString("lookup_name"));
                    restrictedSubscriberVO.setBlackListStatus(rs.getString("black_list_status"));
                    restrictedSubscriberVO.setRemarks(rs.getString("remark"));
                    restrictedSubscriberVO.setApprovedBy(rs.getString("approved_by"));
                    restrictedSubscriberVO.setAssociatedBy(rs.getString("associated_by"));
                    restrictedSubscriberVO.setAssociationDate(rs.getDate("association_date"));
                    restrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                    restrictedSubscriberVO.setLanguage(rs.getString("language"));
                    restrictedSubscriberVO.setCountry(rs.getString("country"));
                    restrictedSubscriberVO.setMsisdn(rs.getString("msisdn"));
                    subsList.add(restrictedSubscriberVO);
                } else {
                    keyArgumentVO.setArguments(msisdnArr);
                    keyArgumentVO.setKey("restrictedsubs.loadsubscriberlistforunblack.err.msg.nosubfound");
                    BlackListLog.log("UNBLACKLIST", "Comma Seperated List", msisdn, "No Subscriber Found", "Fail", "Logged In UserID : " + p_loginUserID);
                    p_errorList.add(keyArgumentVO);
                    continue;
                }
            }
            // If there is any error in the MSISDN's then throw exception
            if (!p_errorList.isEmpty()) {
                response.setErrorList(p_errorList);
                throw new BTSLBaseException(this, methodName, p_errorList, p_fwdPath);
            }
        } // end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[loadSubcriberListForUnblack]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RestrictedSubscriberWebDAO[loadSubcriberListForUnblack]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtSubList != null) {
                    pstmtSubList.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting subsList.size=" + subsList.size());
            }
        }
        return subsList;
    }
}
